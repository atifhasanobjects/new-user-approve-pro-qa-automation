package com.nuapro.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nuapro.config.Config;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Small API client for a local Mailpit or MailHog instance.
 *
 * <p>The SMTP catcher is intentionally queried outside Selenium. The browser
 * proves the UI workflow; this client proves that WordPress/PHPMailer handed
 * the expected message to the configured mail server.</p>
 */
public class EmailCatcherClient {

    private final HttpClient httpClient;
    private final String catcherType;
    private final String apiUrl;
    private final Duration requestTimeout;

    public EmailCatcherClient() {
        this(HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build());
    }

    EmailCatcherClient(HttpClient httpClient) {
        this.httpClient = httpClient;
        this.catcherType = Config.getEmailCatcherType();
        this.apiUrl = Config.getEmailCatcherApiUrl();
        this.requestTimeout = Duration.ofSeconds(10);
    }

    public boolean isAvailable() {
        try {
            request("GET", messagesPath());
            return true;
        } catch (RuntimeException e) {
            return false;
        }
    }

    public void requireAvailable() {
        if (!isAvailable()) {
            throw new IllegalStateException(
                    "Email catcher is unavailable at " + apiUrl
                            + ". Start Mailpit/MailHog and verify WordPress SMTP is configured "
                            + "for port " + Config.getEmailCatcherSmtpPort() + ".");
        }
    }

    public void clearMessages() {
        request("DELETE", "/api/v1/messages");
    }

    public CapturedEmail awaitMessage(String recipient, String subjectMarker,
                                      String... bodyMarkers) {
        Instant deadline = Instant.now()
                .plusSeconds(Config.getEmailPollTimeoutSeconds());
        List<CapturedEmail> lastMessages = Collections.emptyList();

        while (Instant.now().isBefore(deadline)) {
            lastMessages = getMessages();
            Optional<CapturedEmail> matchingMessage = lastMessages.stream()
                    .filter(message -> message.matches(recipient, subjectMarker, bodyMarkers))
                    .findFirst();
            if (matchingMessage.isPresent()) {
                return matchingMessage.get();
            }
            sleepBetweenPolls();
        }

        throw new AssertionError("Expected email was not captured. "
                + "recipient=" + recipient
                + ", subjectMarker=" + subjectMarker
                + ", messages=" + summarize(lastMessages));
    }

    public boolean hasMessage(String recipient, String subjectMarker,
                               String... bodyMarkers) {
        return getMessages().stream()
                .anyMatch(message -> message.matches(recipient, subjectMarker, bodyMarkers));
    }

    public void assertNoMessage(String recipient, String subjectMarker,
                                 String... bodyMarkers) {
        Instant deadline = Instant.now().plusSeconds(
                Math.min(Config.getEmailPollTimeoutSeconds(), 10));
        while (Instant.now().isBefore(deadline)) {
            if (hasMessage(recipient, subjectMarker, bodyMarkers)) {
                throw new AssertionError("Unexpected email was captured. "
                        + "recipient=" + recipient
                        + ", subjectMarker=" + subjectMarker);
            }
            sleepBetweenPolls();
        }
    }

    public List<CapturedEmail> getMessages() {
        JsonObject response = parseObject(request("GET", messagesPath()));
        JsonArray messages = firstArray(response, "messages", "Messages", "items", "Items");
        if (messages == null) {
            return Collections.emptyList();
        }

        List<CapturedEmail> capturedEmails = new ArrayList<>();
        for (JsonElement messageElement : messages) {
            if (!messageElement.isJsonObject()) {
                continue;
            }
            JsonObject summary = messageElement.getAsJsonObject();
            CapturedEmail message = parseMessage(summary);
            String id = firstString(summary, "ID", "id", "MessageID", "messageId");

            // Mailpit's list endpoint normally omits the message body. Fetch
            // the detail endpoint so body assertions are meaningful.
            if (id != null && ("mailpit".equals(catcherType)
                    || message.body().isEmpty())) {
                try {
                    JsonObject detail = parseObject(
                            request("GET", "/api/v1/message/" + encodePath(id)));
                    message = mergeMessage(message, parseMessage(detail));
                } catch (RuntimeException ignored) {
                    // Keep the summary so a useful recipient/subject failure
                    // is still reported if a message disappears while polling.
                }
            }
            capturedEmails.add(message);
        }
        return capturedEmails;
    }

    private String messagesPath() {
        return "mailhog".equals(catcherType)
                ? "/api/v2/messages"
                : "/api/v1/messages";
    }

    private CapturedEmail parseMessage(JsonObject root) {
        String subject = firstString(root, "Subject", "subject");
        String text = firstString(root, "Text", "text");
        String html = firstString(root, "HTML", "html");
        String created = firstString(root, "Created", "created");
        List<String> recipients = stringArrayValues(root, "To", "to");

        JsonObject content = objectValue(root, "Content", "content");
        if (content != null) {
            JsonObject headers = objectValue(content, "Headers", "headers");
            if (headers != null) {
                subject = firstHeaderValue(headers, "Subject", subject);
                recipients = firstHeaderValues(headers, "To", recipients);
            }
            String body = firstString(content, "Body", "body");
            if ((text == null || text.isEmpty()) && body != null) {
                text = body;
            }
        }

        String body = joinNonEmpty(text, html);
        return new CapturedEmail(recipients, subject, body, html, created);
    }

    private CapturedEmail mergeMessage(CapturedEmail summary, CapturedEmail detail) {
        List<String> recipients = detail.recipients().isEmpty()
                ? summary.recipients() : detail.recipients();
        String subject = isBlank(detail.subject()) ? summary.subject() : detail.subject();
        String body = isBlank(detail.body()) ? summary.body() : detail.body();
        String html = isBlank(detail.html()) ? summary.html() : detail.html();
        String created = isBlank(detail.created()) ? summary.created() : detail.created();
        return new CapturedEmail(recipients, subject, body, html, created);
    }

    private String request(String method, String path) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl + path))
                    .timeout(requestTimeout)
                    .method(method, HttpRequest.BodyPublishers.noBody())
                    .header("Accept", "application/json")
                    .build();
            HttpResponse<String> response = httpClient.send(
                    request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new IllegalStateException("Email catcher returned HTTP "
                        + response.statusCode() + " for " + method + " " + path
                        + ": " + response.body());
            }
            return response.body();
        } catch (IOException | InterruptedException e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            throw new IllegalStateException(
                    "Unable to call email catcher at " + apiUrl + path, e);
        }
    }

    private void sleepBetweenPolls() {
        try {
            Thread.sleep(Config.getEmailPollIntervalMillis());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Email polling was interrupted", e);
        }
    }

    private String summarize(List<CapturedEmail> messages) {
        if (messages.isEmpty()) {
            return "[]";
        }
        StringBuilder summary = new StringBuilder("[");
        for (CapturedEmail message : messages) {
            if (summary.length() > 1) {
                summary.append(", ");
            }
            summary.append("{to=").append(message.recipients())
                    .append(", subject=").append(message.subject()).append("}");
        }
        return summary.append("]").toString();
    }

    private JsonObject parseObject(String json) {
        JsonElement element = JsonParser.parseString(json);
        if (!element.isJsonObject()) {
            throw new IllegalStateException("Email catcher returned non-object JSON: " + json);
        }
        return element.getAsJsonObject();
    }

    private JsonArray firstArray(JsonObject object, String... names) {
        for (String name : names) {
            JsonElement value = object.get(name);
            if (value != null && value.isJsonArray()) {
                return value.getAsJsonArray();
            }
        }
        return null;
    }

    private JsonObject objectValue(JsonObject object, String... names) {
        for (String name : names) {
            JsonElement value = object.get(name);
            if (value != null && value.isJsonObject()) {
                return value.getAsJsonObject();
            }
        }
        return null;
    }

    private String firstString(JsonObject object, String... names) {
        for (String name : names) {
            JsonElement value = object.get(name);
            if (value != null && !value.isJsonNull() && value.isJsonPrimitive()) {
                return value.getAsString();
            }
        }
        return "";
    }

    private List<String> stringArrayValues(JsonObject object, String... names) {
        for (String name : names) {
            JsonElement value = object.get(name);
            if (value == null || value.isJsonNull()) {
                continue;
            }
            if (value.isJsonPrimitive()) {
                return List.of(value.getAsString());
            }
            if (value.isJsonArray()) {
                List<String> values = new ArrayList<>();
                for (JsonElement item : value.getAsJsonArray()) {
                    if (item.isJsonPrimitive()) {
                        values.add(item.getAsString());
                    } else if (item.isJsonObject()) {
                        String address = firstString(item.getAsJsonObject(), "Address", "address", "Email", "email");
                        if (!address.isEmpty()) {
                            values.add(address);
                        }
                    }
                }
                return values;
            }
        }
        return Collections.emptyList();
    }

    private List<String> firstHeaderValues(JsonObject headers, String name,
                                           List<String> fallback) {
        for (String key : headers.keySet()) {
            if (key.equalsIgnoreCase(name)) {
                JsonElement value = headers.get(key);
                if (value.isJsonArray()) {
                    List<String> values = new ArrayList<>();
                    for (JsonElement item : value.getAsJsonArray()) {
                        if (item.isJsonPrimitive()) {
                            values.add(item.getAsString());
                        }
                    }
                    return values;
                }
                if (value.isJsonPrimitive()) {
                    return List.of(value.getAsString());
                }
            }
        }
        return fallback;
    }

    private String firstHeaderValue(JsonObject headers, String name, String fallback) {
        List<String> values = firstHeaderValues(headers, name,
                fallback == null ? Collections.emptyList() : List.of(fallback));
        return values.isEmpty() ? fallback : values.get(0);
    }

    private String joinNonEmpty(String first, String second) {
        String left = first == null ? "" : first;
        String right = second == null ? "" : second;
        if (left.isEmpty()) {
            return right;
        }
        if (right.isEmpty()) {
            return left;
        }
        return left + "\n" + right;
    }

    private String encodePath(String value) {
        return value.replace("/", "%2F");
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    public record CapturedEmail(List<String> recipients, String subject,
                                String body, String html, String created) {
        public boolean matches(String expectedRecipient, String subjectMarker,
                               String... bodyMarkers) {
            String normalizedRecipient = expectedRecipient == null
                    ? "" : expectedRecipient.trim().toLowerCase(Locale.ROOT);
            boolean recipientMatches = recipients.stream()
                    .map(value -> value.toLowerCase(Locale.ROOT))
                    .anyMatch(value -> value.contains(normalizedRecipient));
            boolean subjectMatches = subject != null
                    && subject.toLowerCase(Locale.ROOT)
                    .contains(subjectMarker.toLowerCase(Locale.ROOT));
            if (!recipientMatches || !subjectMatches) {
                return false;
            }
            String searchableBody = (body == null ? "" : body).toLowerCase(Locale.ROOT);
            for (String marker : bodyMarkers) {
                if (marker != null && !searchableBody.contains(marker.toLowerCase(Locale.ROOT))) {
                    return false;
                }
            }
            return true;
        }
    }
}
