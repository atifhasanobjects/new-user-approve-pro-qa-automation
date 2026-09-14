package com.nuapro.tests;

import com.nuapro.base.BaseTest;
import com.nuapro.config.Config;
import com.nuapro.pages.DashboardPage;
import com.nuapro.pages.LoginPage;
import com.nuapro.pages.RegistrationPage;
import com.nuapro.pages.SettingsPage;
import com.nuapro.pages.UsersPage;
import com.nuapro.utils.EmailCatcherClient;
import com.nuapro.utils.TestData;
import org.testng.Assert;
import org.testng.SkipException;

/**
 * Shared setup for notification tests. Email tests are opt-in because a normal
 * UI-only Maven run may not have a local SMTP catcher running.
 */
public abstract class NotificationTestBase extends BaseTest {

    protected final EmailCatcherClient emailClient = new EmailCatcherClient();

    protected void requireEmailTestEnvironment() {
        if (!Config.isEmailVerificationEnabled()) {
            throw new SkipException(
                    "Email verification is disabled. Run with "
                            + "-DNUA_EMAIL_TEST_ENABLED=true after configuring Mailpit/MailHog "
                            + "and WordPress SMTP.");
        }
        emailClient.requireAvailable();
    }

    protected DashboardPage loginAsAdmin() {
        return new LoginPage(driver).loginAsAdmin();
    }

    protected void configurePendingRegistration(DashboardPage dashboardPage) {
        SettingsPage settingsPage = dashboardPage.clickSettingsTab();
        settingsPage.setEnableAutoApproveToggle(false);
        settingsPage.setAutoDenialToggle(false);
        // Render the dependent control before explicitly disabling it.
        settingsPage.setEnableInvitationCodeToggle(true);
        settingsPage.setInvitationCodeRequiredToggle(false);
        settingsPage.setEnableInvitationCodeToggle(false);
        settingsPage.setRegistrationDeadlineToggle(false);
        settingsPage.saveSettings();
    }

    protected void configureAdminNotification(DashboardPage dashboardPage,
                                              String subject, String message) {
        SettingsPage settingsPage = dashboardPage.clickSettingsTab();
        settingsPage.setSendNotificationEmailsToAllAdmins(false);
        settingsPage.setNotifyAdminsOnStatusUpdate(true);
        settingsPage.setSuppressSiteAdminNotification(false);
        settingsPage.setSpecificAdminRecipientsEnabled(false);
        settingsPage.setAdminNotificationSubject(subject);
        settingsPage.setAdminNotificationMessage(
                message + "\n{username}\n{user_email}");
        settingsPage.setAdminNotificationHtml(false);
        settingsPage.saveSettings();
    }

    protected void configureUserWelcomeNotification(DashboardPage dashboardPage,
                                                    String subject, String message) {
        SettingsPage settingsPage = dashboardPage.clickSettingsTab();
        settingsPage.setUserWelcomeNotification(true, subject, message, false);
        settingsPage.saveSettings();
    }

    protected void configureUserApprovalNotification(DashboardPage dashboardPage,
                                                     String subject, String message) {
        SettingsPage settingsPage = dashboardPage.clickSettingsTab();
        settingsPage.setUserApprovalNotification(
                subject, message + "\n{username}\n{user_email}", false);
        settingsPage.saveSettings();
    }

    protected void configureUserDenialNotification(DashboardPage dashboardPage,
                                                   String subject, String message,
                                                   boolean suppressed) {
        SettingsPage settingsPage = dashboardPage.clickSettingsTab();
        settingsPage.setUserDenialNotification(
                subject, message + "\n{username}\n{user_email}", false);
        settingsPage.setSuppressDenialNotification(suppressed);
        settingsPage.saveSettings();
    }

    protected PendingUser registerPendingUser() {
        String username = TestData.generateUsername();
        String email = TestData.generateEmail();
        new RegistrationPage(driver).registerUser(username, email);

        Assert.assertTrue(new RegistrationPage(driver).isRegistrationSuccessful(),
                "Registration should succeed for notification test user");

        DashboardPage dashboardPage = loginAsAdmin();
        UsersPage usersPage = dashboardPage.clickUsersTab();
        usersPage.clickPendingUsersSubTab();
        usersPage.getUserTable().searchUser(email);
        Assert.assertTrue(usersPage.getUserTable().isUserRowPresent(email),
                "Notification test user should appear in Pending users: " + email);
        return new PendingUser(username, email);
    }

    protected void approveUser(PendingUser user) {
        UsersPage usersPage = loginAsAdmin().clickUsersTab();
        usersPage.clickPendingUsersSubTab();
        usersPage.getUserTable().searchUser(user.email());
        usersPage.getUserTable().approveUser(user.email());
    }

    protected void denyUser(PendingUser user) {
        UsersPage usersPage = loginAsAdmin().clickUsersTab();
        usersPage.clickPendingUsersSubTab();
        usersPage.getUserTable().searchUser(user.email());
        usersPage.getUserTable().denyUser(user.email());
    }

    protected String marker(String type) {
        return "QA_" + type + "_" + TestData.generateUniqueId();
    }

    protected record PendingUser(String username, String email) {
    }
}
