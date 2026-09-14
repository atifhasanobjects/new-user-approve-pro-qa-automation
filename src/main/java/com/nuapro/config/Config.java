package com.nuapro.config;

import java.util.Arrays;
import java.util.List;

public class Config {

    public static String getBaseUrl() {
        String url = System.getenv("SELENIUM_BASE_URL");
        if (url == null || url.trim().isEmpty()) {
            url = System.getProperty("SELENIUM_BASE_URL", "http://nuaproautomation.local");
        }
        return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    }

    public static String getAdminUser() {
        String user = System.getenv("SELENIUM_ADMIN_USER");
        if (user == null || user.trim().isEmpty()) {
            user = System.getProperty("SELENIUM_ADMIN_USER", "atifhasan");
        }
        return user;
    }

    public static String getAdminPass() {
        String pass = System.getenv("SELENIUM_ADMIN_PASS");
        if (pass == null || pass.trim().isEmpty()) {
            pass = System.getProperty("SELENIUM_ADMIN_PASS", "google12345");
        }
        return pass;
    }

    public static String getBrowser() {
        String browser = System.getenv("SELENIUM_BROWSER");
        if (browser == null || browser.trim().isEmpty()) {
            browser = System.getProperty("SELENIUM_BROWSER", "chrome");
        }
        return browser.toLowerCase();
    }

    public static boolean isHeadless() {
        String headless = System.getenv("SELENIUM_HEADLESS");
        if (headless == null || headless.trim().isEmpty()) {
            headless = System.getProperty("SELENIUM_HEADLESS", "false");
        }
        return Boolean.parseBoolean(headless);
    }

    public static int getTimeoutSeconds() {
        String timeout = System.getenv("SELENIUM_TIMEOUT");
        if (timeout == null || timeout.trim().isEmpty()) {
            timeout = System.getProperty("SELENIUM_TIMEOUT", "15");
        }
        try {
            return Integer.parseInt(timeout);
        } catch (NumberFormatException e) {
            return 15;
        }
    }

    public static boolean isEmailVerificationEnabled() {
        return Boolean.parseBoolean(getConfigValue(
                "NUA_EMAIL_TEST_ENABLED", "false"));
    }

    public static String getEmailCatcherType() {
        return getConfigValue("NUA_EMAIL_CATCHER_TYPE", "mailpit").toLowerCase();
    }

    public static String getEmailCatcherApiUrl() {
        String defaultUrl = "http://" + getEmailCatcherHost()
                + ":" + getEmailCatcherApiPort();
        return getConfigValue("NUA_EMAIL_CATCHER_API_URL", defaultUrl)
                .replaceAll("/+$", "");
    }

    public static String getEmailCatcherHost() {
        return getConfigValue("NUA_EMAIL_CATCHER_HOST", "127.0.0.1");
    }

    public static int getEmailCatcherApiPort() {
        return getIntegerConfigValue("NUA_EMAIL_CATCHER_API_PORT", 8025);
    }

    public static int getEmailCatcherSmtpPort() {
        return getIntegerConfigValue("NUA_EMAIL_CATCHER_SMTP_PORT", 1025);
    }

    public static int getEmailPollTimeoutSeconds() {
        return getIntegerConfigValue("NUA_EMAIL_POLL_TIMEOUT", 30);
    }

    public static long getEmailPollIntervalMillis() {
        return getLongConfigValue("NUA_EMAIL_POLL_INTERVAL_MILLIS", 500L);
    }

    public static String getEmailAdminAddress() {
        return getConfigValue("NUA_EMAIL_ADMIN_ADDRESS", "qa-admin@example.test");
    }

    public static String getEmailSiteAdminAddress() {
        return getConfigValue("NUA_EMAIL_SITE_ADMIN_ADDRESS", "atif.objects@gmail.com");
    }

    public static List<String> getEmailAdminRecipients() {
        return Arrays.stream(getConfigValue(
                        "NUA_EMAIL_ADMIN_RECIPIENTS", getEmailAdminAddress()).split(","))
                .map(String::trim)
                .filter(value -> !value.isEmpty())
                .toList();
    }

    public static String getEmailSpecificRecipient() {
        return getConfigValue("NUA_EMAIL_SPECIFIC_RECIPIENT", "");
    }

    private static String getConfigValue(String key, String defaultValue) {
        String value = System.getenv(key);
        if (value == null || value.trim().isEmpty()) {
            value = System.getProperty(key, defaultValue);
        }
        return value.trim();
    }

    private static int getIntegerConfigValue(String key, int defaultValue) {
        try {
            return Integer.parseInt(getConfigValue(key, String.valueOf(defaultValue)));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private static long getLongConfigValue(String key, long defaultValue) {
        try {
            return Long.parseLong(getConfigValue(key, String.valueOf(defaultValue)));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}
