# Email notification testing

The notification tests use a local SMTP catcher. They do not require access to
personal admin or user mailboxes.

## Start Mailpit

If Docker is available, run:

```text
docker run --name nua-mailpit -p 1025:1025 -p 8025:8025 axllent/mailpit
```

Mailpit's web UI is available at `http://127.0.0.1:8025` and its API is
available at `http://127.0.0.1:8025/api/v1/messages`.

MailHog is also supported by setting
`-DNUA_EMAIL_CATCHER_TYPE=mailhog`. Its default API port is 8025 and SMTP port
is 1025.

## Configure WordPress mail

Configure the local WordPress mail/SMTP integration to send through:

```text
SMTP host: 127.0.0.1
SMTP port: 1025
Authentication: disabled
```

Do this in the local WordPress mail plugin or mail configuration. Do not change
the NUA Pro plugin source for test mail routing.

Set the NUA Pro Administrator Email Address to the test admin address used by
the automation, for example:

```text
qa-admin@example.test
```

## Run from IntelliJ IDEA

Add these VM options to an email test run configuration:

```text
-DNUA_EMAIL_TEST_ENABLED=true
-DNUA_EMAIL_ADMIN_ADDRESS=qa-admin@example.test
-DNUA_EMAIL_SITE_ADMIN_ADDRESS=atif.objects@gmail.com
-DNUA_EMAIL_CATCHER_TYPE=mailpit
-DNUA_EMAIL_CATCHER_API_PORT=8025
-DNUA_EMAIL_CATCHER_SMTP_PORT=1025
```

For the all-admin scenario, list every controlled administrator address:

```text
-DNUA_EMAIL_ADMIN_RECIPIENTS=qa-admin@example.test,qa-admin-two@example.test
```

The specific-recipient scenario requires an existing WordPress user selected
in the Admin Notification settings:

```text
-DNUA_EMAIL_SPECIFIC_RECIPIENT=qa-recipient@example.test
```

Email tests are skipped when `NUA_EMAIL_TEST_ENABLED` is not `true`, so a
normal UI-only test run does not report false email failures when no catcher is
running.

## What the tests verify

The browser performs registration, approval, or denial. WordPress and
PHPMailer send the message to Mailpit/MailHog. The Java test then queries the
catcher's API and matches the recipient, unique subject marker, username/email,
and message marker. A missing message fails the email scenario after the
configured polling timeout.

