package com.nuapro.tests;

import com.nuapro.config.Config;
import com.nuapro.pages.DashboardPage;
import com.nuapro.pages.SettingsPage;
import com.nuapro.utils.EmailCatcherClient;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.Test;

public class AdminNotificationTest extends NotificationTestBase {

    @Test(groups = {"email", "notification", "admin-notification"},
            description = "Admin receives notification after a pending registration")
    public void testAdminReceivesPendingRegistrationNotification() {

        requireEmailTestEnvironment();

        DashboardPage dashboardPage = loginAsAdmin();
        configurePendingRegistration(dashboardPage);

        String subjectMarker = marker("ADMIN_REGISTRATION");
        String bodyMarker = marker("ADMIN_REGISTRATION_BODY");
        configureAdminNotification(dashboardPage, subjectMarker, bodyMarker);
        emailClient.clearMessages();

        PendingUser user = registerPendingUser();

        EmailCatcherClient.CapturedEmail email = emailClient.awaitMessage(
                Config.getEmailSiteAdminAddress(),
                subjectMarker,
                bodyMarker,
                user.username(),
                user.email());

        Assert.assertTrue(
                email.subject().contains(subjectMarker),
                "Admin registration email should contain the configured subject marker"
        );
    }

    @Test(groups = {"email", "notification", "admin-notification"},
            description = "Admin receives notification when a pending user is approved")
    public void testAdminReceivesStatusUpdateOnApproval() {
        requireEmailTestEnvironment();

        DashboardPage dashboardPage = loginAsAdmin();
        configurePendingRegistration(dashboardPage);

        configureAdminNotification(
                dashboardPage,
                marker("ADMIN_APPROVAL_REGISTRATION"),
                marker("ADMIN_APPROVAL_REGISTRATION_BODY"));
        PendingUser user = registerPendingUser();

        // Ignore the registration notification and correlate only the approval
        // event with the message marker configured for this test.
        emailClient.clearMessages();
        approveUser(user);

        EmailCatcherClient.CapturedEmail email = emailClient.awaitMessage(
                Config.getEmailSiteAdminAddress(),
                "User Status Updated",
                user.username(),
                "approve");
        Assert.assertTrue(email.body().contains(user.username()),
                "Admin approval email should identify the approved user");
    }

    @Test(groups = {"email", "notification", "admin-notification"},
            description = "Admin receives notification when a pending user is denied")
    public void testAdminReceivesStatusUpdateOnDenial() {
        requireEmailTestEnvironment();

        DashboardPage dashboardPage = loginAsAdmin();
        configurePendingRegistration(dashboardPage);

        configureAdminNotification(
                dashboardPage,
                marker("ADMIN_DENIAL_REGISTRATION"),
                marker("ADMIN_DENIAL_REGISTRATION_BODY"));
        PendingUser user = registerPendingUser();

        emailClient.clearMessages();
        denyUser(user);

        EmailCatcherClient.CapturedEmail email = emailClient.awaitMessage(
                Config.getEmailSiteAdminAddress(),
                "User Status Updated",
                user.username(),
                "deny");
        Assert.assertTrue(email.body().contains(user.username()),
                "Admin denial email should identify the denied user");
    }

    @Test(groups = {"email", "notification", "admin-notification"},
            description = "All configured administrators receive a registration notification")
    public void testAllConfiguredAdminsReceiveRegistrationNotification() {
        requireEmailTestEnvironment();

        DashboardPage dashboardPage = loginAsAdmin();
        configurePendingRegistration(dashboardPage);
        String subjectMarker = marker("ALL_ADMINS");
        String bodyMarker = marker("ALL_ADMINS_BODY");
        configureAdminNotification(dashboardPage, subjectMarker, bodyMarker);

        SettingsPage settingsPage = dashboardPage.clickSettingsTab();
        settingsPage.setSendNotificationEmailsToAllAdmins(true);
        settingsPage.saveSettings();
        emailClient.clearMessages();

        PendingUser user = registerPendingUser();
        for (String adminRecipient : Config.getEmailAdminRecipients()) {
            emailClient.awaitMessage(
                    adminRecipient,
                    subjectMarker,
                    bodyMarker,
                    user.username(),
                    user.email());
        }
    }

    @Test(groups = {"email", "notification", "admin-notification"},
            description = "Site-admin suppression prevents the site-admin registration notification")
    public void testSiteAdminSuppressionPreventsNotification() {
        requireEmailTestEnvironment();

        DashboardPage dashboardPage = loginAsAdmin();
        configurePendingRegistration(dashboardPage);
        String subjectMarker = marker("SUPPRESS_SITE_ADMIN");
        String bodyMarker = marker("SUPPRESS_SITE_ADMIN_BODY");
        configureAdminNotification(dashboardPage, subjectMarker, bodyMarker);

        SettingsPage settingsPage = dashboardPage.clickSettingsTab();
        settingsPage.setSuppressSiteAdminNotification(true);
        settingsPage.saveSettings();
        emailClient.clearMessages();

        PendingUser user = registerPendingUser();
        emailClient.assertNoMessage(
                Config.getEmailSiteAdminAddress(),
                subjectMarker,
                bodyMarker,
                user.username(),
                user.email());
    }

    @Test(groups = {"email", "notification", "admin-notification"},
            description = "Specific admin recipient receives the registration notification")
    public void testSpecificAdminRecipientReceivesNotification() {
        requireEmailTestEnvironment();
        String specificRecipient = Config.getEmailSpecificRecipient();
        if (specificRecipient.isBlank()) {
            throw new SkipException(
                    "Set NUA_EMAIL_SPECIFIC_RECIPIENT to an existing WordPress user email "
                            + "before running the specific-recipient scenario.");
        }

        DashboardPage dashboardPage = loginAsAdmin();
        configurePendingRegistration(dashboardPage);
        String subjectMarker = marker("SPECIFIC_ADMIN");
        String bodyMarker = marker("SPECIFIC_ADMIN_BODY");
        configureAdminNotification(dashboardPage, subjectMarker, bodyMarker);

        SettingsPage settingsPage = dashboardPage.clickSettingsTab();
        settingsPage.setSuppressSiteAdminNotification(true);
        settingsPage.selectSpecificAdminRecipient(specificRecipient);
        settingsPage.saveSettings();
        emailClient.clearMessages();

        PendingUser user = registerPendingUser();
        emailClient.awaitMessage(
                specificRecipient,
                subjectMarker,
                bodyMarker,
                user.username(),
                user.email());
    }

    @Test(groups = {"email", "notification", "admin-notification"},
            description = "Admin notification HTML mode preserves HTML content")
    public void testAdminNotificationHtmlMode() {
        requireEmailTestEnvironment();

        DashboardPage dashboardPage = loginAsAdmin();
        configurePendingRegistration(dashboardPage);
        String subjectMarker = marker("ADMIN_HTML");
        String bodyMarker = marker("ADMIN_HTML_BODY");
        configureAdminNotification(dashboardPage, subjectMarker, "<strong>"
                + bodyMarker + "</strong>");

        SettingsPage settingsPage = dashboardPage.clickSettingsTab();
        settingsPage.setAdminNotificationHtml(true);
        settingsPage.saveSettings();
        emailClient.clearMessages();

        PendingUser user = registerPendingUser();
        EmailCatcherClient.CapturedEmail email = emailClient.awaitMessage(
                Config.getEmailSiteAdminAddress(),
                subjectMarker,
                bodyMarker,
                user.username(),
                user.email());
        Assert.assertTrue(email.html().contains("<strong>"),
                "HTML admin notification should preserve HTML markup");
    }
}
