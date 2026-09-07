package com.nuapro.tests;

import com.nuapro.base.BaseTest;
import com.nuapro.pages.DashboardPage;
import com.nuapro.pages.InvitationCodesPage;
import com.nuapro.pages.LoginPage;
import com.nuapro.pages.ThemeMyLoginLoginPage;
import com.nuapro.pages.ThemeMyLoginRegistrationPage;
import com.nuapro.pages.RegistrationPage;
import com.nuapro.pages.SettingsPage;
import com.nuapro.pages.UsersPage;
import com.nuapro.utils.TestData;
import org.openqa.selenium.JavascriptExecutor;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class SettingsTest extends BaseTest {

    private static final ZoneId WORDPRESS_ZONE = ZoneId.of("Asia/Karachi");

    private ZoneId getBrowserTimezone() {
        String tz = (String) ((JavascriptExecutor) driver).executeScript(
                "return Intl.DateTimeFormat().resolvedOptions().timeZone;");
        return ZoneId.of(tz);
    }

    @Test(groups = {"settings"}, description = "TC037: General settings save and refresh persistence")
    public void testGeneralSettingsPersistence() {
        LoginPage loginPage = new LoginPage(driver);
        DashboardPage dashboardPage = loginPage.loginAsAdmin();
        SettingsPage settingsPage = dashboardPage.clickSettingsTab();

        settingsPage.clickGeneralTab();
        Assert.assertTrue(settingsPage.isPanelVisible(".nua_setting_tab"), "General settings panel should be visible");
        settingsPage.saveSettings();
        driver.navigate().refresh();
        Assert.assertTrue(settingsPage.isPanelVisible(".nua_setting_tab"), "General settings panel should remain visible after refresh");
    }

    @Test(groups = {"settings"}, description = "TC038: Registration settings save and refresh persistence")
    public void testRegistrationSettingsPersistence() {
        LoginPage loginPage = new LoginPage(driver);
        DashboardPage dashboardPage = loginPage.loginAsAdmin();
        SettingsPage settingsPage = dashboardPage.clickSettingsTab();

        settingsPage.clickRegistrationTab();
        Assert.assertTrue(settingsPage.isPanelVisible(".registration_settings"), "Registration settings panel should be visible");
        settingsPage.saveSettings();
        driver.navigate().refresh();
        Assert.assertTrue(settingsPage.isPanelVisible(".registration_settings"), "Registration settings panel should remain visible after refresh");
    }

    @Test(groups = {"settings"}, description = "TC040: Admin notification settings persistence")
    public void testAdminNotificationSettingsPersistence() {
        LoginPage loginPage = new LoginPage(driver);
        DashboardPage dashboardPage = loginPage.loginAsAdmin();
        SettingsPage settingsPage = dashboardPage.clickSettingsTab();

        settingsPage.clickAdminNotifTab();
        Assert.assertTrue(settingsPage.isPanelVisible(".admin_notification_email"), "Admin notification panel should be visible");
        settingsPage.saveSettings();
        driver.navigate().refresh();
        Assert.assertTrue(settingsPage.isPanelVisible(".admin_notification_email"), "Admin notification panel should remain visible after refresh");
    }

    @Test(groups = {"settings"}, description = "TC041: User notification settings persistence")
    public void testUserNotificationSettingsPersistence() {
        LoginPage loginPage = new LoginPage(driver);
        DashboardPage dashboardPage = loginPage.loginAsAdmin();
        SettingsPage settingsPage = dashboardPage.clickSettingsTab();

        settingsPage.clickUserNotifTab();
        Assert.assertTrue(settingsPage.isPanelVisible(".user_settings"), "User notification panel should be visible");
        settingsPage.saveSettings();
        driver.navigate().refresh();
        Assert.assertTrue(settingsPage.isPanelVisible(".user_settings"), "User notification panel should remain visible after refresh");
    }

    @Test(groups = {"settings", "approval"}, description = "Approval settings: registration role request toggle persists")
    public void testRegistrationRoleRequestTogglePersistence() {
        LoginPage loginPage = new LoginPage(driver);
        DashboardPage dashboardPage = loginPage.loginAsAdmin();
        SettingsPage settingsPage = dashboardPage.clickSettingsTab();

        settingsPage.setEnableAutoApproveToggle(true);
        settingsPage.setUserRoleRequestToggle(true);
        settingsPage.clearAutoApprovalRoles();
        settingsPage.selectAutoApprovalRole("Subscriber");
        settingsPage.saveSettings();
        driver.navigate().refresh();

        Assert.assertTrue(settingsPage.isAutoApproveToggleEnabled(),
                "Enable Auto-Approve should remain enabled after refresh");
        Assert.assertTrue(settingsPage.isUserRoleRequestToggleEnabled(),
                "Select User Role Request (Registration) should remain enabled after refresh");
        Assert.assertTrue(settingsPage.isAutoApprovalRoleSelected("Subscriber"),
                "Subscriber should remain selected for role-based auto-approval");

        RegistrationPage registrationPage = new RegistrationPage(driver);
        registrationPage.open();
        Assert.assertTrue(registrationPage.isUserRoleSelectAvailable(),
                "Registration page should display the user-role selector");
        String username = TestData.generateUsername();
        registrationPage.registerUserWithRole(username, username + "@example.test", "subscriber");
        Assert.assertTrue(registrationPage.isRegistrationSuccessful(),
                "Registration with a selected auto-approved role should succeed");
    }

    @Test(groups = {"settings", "approval"}, description = "Approval settings: selected auto-approval role persists")
    public void testSpecificUserRoleAutoApprovalPersistence() {
        LoginPage loginPage = new LoginPage(driver);
        DashboardPage dashboardPage = loginPage.loginAsAdmin();
        SettingsPage settingsPage = dashboardPage.clickSettingsTab();

        settingsPage.setEnableAutoApproveToggle(true);
        settingsPage.setUserRoleRequestToggle(true);
        settingsPage.clearAutoApprovalRoles();
        settingsPage.selectAutoApprovalRole("Subscriber");
        settingsPage.saveSettings();
        driver.navigate().refresh();

        Assert.assertTrue(settingsPage.isAutoApproveToggleEnabled(),
                "Enable Auto-Approve should remain enabled after refresh");
        Assert.assertTrue(settingsPage.isUserRoleRequestToggleEnabled(),
                "Select User Role Request (Registration) should remain enabled");
        Assert.assertTrue(settingsPage.isAutoApprovalRoleSelected("Subscriber"),
                "Subscriber should remain selected for role-based auto-approval after refresh");

        RegistrationPage registrationPage = new RegistrationPage(driver);
        String approvedUsername = TestData.generateUsername();
        String approvedEmail = approvedUsername + "@example.test";
        registrationPage.registerUserWithRole(approvedUsername, approvedEmail, "subscriber");
        Assert.assertTrue(registrationPage.isRegistrationSuccessful(),
                "Registration with a configured role should succeed");

        dashboardPage = loginPage.loginAsAdmin();
        UsersPage usersPage = dashboardPage.clickUsersTab();
        usersPage.clickApprovedUsersSubTab();
        usersPage.getUserTable().searchUser(approvedEmail);
        Assert.assertTrue(usersPage.getUserTable().isUserRowPresent(approvedEmail),
                "User selecting Subscriber should be automatically approved");

        String pendingUsername = TestData.generateUsername();
        String pendingEmail = pendingUsername + "@example.test";
        registrationPage.registerUserWithRole(pendingUsername, pendingEmail, "editor");
        Assert.assertTrue(registrationPage.isRegistrationSuccessful(),
                "Registration with a non-configured role should still submit");

        dashboardPage = loginPage.loginAsAdmin();
        usersPage = dashboardPage.clickUsersTab();
        usersPage.clickPendingUsersSubTab();
        usersPage.getUserTable().searchUser(pendingEmail);
        Assert.assertTrue(usersPage.getUserTable().isUserRowPresent(pendingEmail),
                "User selecting a non-configured role should remain pending");
    }

    @Test(groups = {"settings", "invitation"}, description = "Invitation settings: required invitation code toggle persists")
    public void testInvitationCodeRequiredPersistence() {
        LoginPage loginPage = new LoginPage(driver);
        DashboardPage dashboardPage = loginPage.loginAsAdmin();
        SettingsPage settingsPage = dashboardPage.clickSettingsTab();

        settingsPage.setEnableInvitationCodeToggle(true);
        settingsPage.setInvitationCodeRequiredToggle(true);
        settingsPage.saveSettings();
        driver.navigate().refresh();

        Assert.assertTrue(settingsPage.isInvitationCodeToggleEnabled(),
                "Invitation Code should remain enabled after refresh");
        Assert.assertTrue(settingsPage.isInvitationCodeRequiredToggleEnabled(),
                "Make Invitation Code Required should remain enabled after refresh");

        RegistrationPage registrationPage = new RegistrationPage(driver);
        registrationPage.registerUser(TestData.generateUsername(), TestData.generateEmail());

        Assert.assertTrue(registrationPage.isRegistrationFailed(),
                "Registration without an invitation code should be rejected");
        Assert.assertTrue(registrationPage.getRegistrationErrorText()
                        .contains("ERROR: Please add an Invitation code."),
                "Expected invitation-code error should be displayed. Actual: "
                        + registrationPage.getRegistrationErrorText());
    }

    @Test(groups = {"settings", "denial"}, description = "Denial settings: Deny Automatically persists")
    public void testAutomaticDenialPersistence() {
        LoginPage loginPage = new LoginPage(driver);
        DashboardPage dashboardPage = loginPage.loginAsAdmin();
        SettingsPage settingsPage = dashboardPage.clickSettingsTab();

        // Keep the fixture pending so automatic denial, rather than another
        // approval rule, controls the resulting status.
        settingsPage.setEnableAutoApproveToggle(false);
        settingsPage.setEnableInvitationCodeToggle(false);
        settingsPage.setInvitationCodeRequiredToggle(false);
        settingsPage.setAutoDenialSchedule(1, "minute");
        settingsPage.saveSettings();
        driver.navigate().refresh();

        Assert.assertTrue(settingsPage.isAutoDenialToggleEnabled(),
                "Deny Automatically should remain enabled after refresh");
        Assert.assertTrue(settingsPage.isAutoDenialSettingsVisible(),
                "Deny After and Time Period settings should be visible when automatic denial is enabled");
        Assert.assertEquals(settingsPage.getAutoDenialAfterValue(), "1",
                "Automatic denial should be configured for one minute");
        Assert.assertEquals(settingsPage.getAutoDenialAfterUnit(), "minute",
                "Automatic denial time period should be minutes");

        RegistrationPage registrationPage = new RegistrationPage(driver);
        registrationPage.open();
        Assert.assertTrue(registrationPage.isRegistrationFormAvailable(),
                "Registration form should be available for creating a pending user for automatic denial");

        String username = TestData.generateUsername();
        String email = username + "@example.test";
        registrationPage.registerUser(username, email);
        Assert.assertTrue(registrationPage.isRegistrationSuccessful(),
                "Registration should submit successfully and create a pending user");

        dashboardPage = loginPage.loginAsAdmin();
        UsersPage usersPage = dashboardPage.clickUsersTab();
        usersPage.clickPendingUsersSubTab();
        usersPage.getUserTable().searchUser(email);
        Assert.assertTrue(usersPage.getUserTable().isUserRowPresent(email),
                "Newly registered user should initially appear in Pending users");

        Assert.assertTrue(waitForDeniedUser(usersPage, email),
                "Pending user should be automatically denied after one minute");
    }

    @Test(groups = {"settings", "password"}, description = "Password & Security: Bypass password reset keeps original password after approval")
    public void testBypassPasswordResetKeepsOriginalPassword() {
        LoginPage loginPage = new LoginPage(driver);
        DashboardPage dashboardPage = loginPage.loginAsAdmin();
        SettingsPage settingsPage = dashboardPage.clickSettingsTab();

        String knownPassword = "QAMyPass123!"; // must meet Theme My Login password strength
        // Isolate the fixture so it must enter Pending before approval.
        settingsPage.setEnableAutoApproveToggle(false);
        settingsPage.setAutoDenialToggle(false);
        settingsPage.setEnableInvitationCodeToggle(false);
        settingsPage.setInvitationCodeRequiredToggle(false);
        settingsPage.setRegistrationDeadlineToggle(false);
        settingsPage.setBypassPasswordResetToggle(true);
        settingsPage.saveSettings();
        driver.navigate().refresh();
        Assert.assertFalse(settingsPage.isAutoApproveToggleEnabled(),
                "Auto-Approve must be disabled so the fixture enters Pending");
        Assert.assertTrue(settingsPage.isBypassPasswordResetToggleEnabled(),
                "Bypass Password Reset should remain enabled after refresh");

        ThemeMyLoginRegistrationPage registrationPage = new ThemeMyLoginRegistrationPage(driver);
        String username = TestData.generateUsername();
        String email = username + "@example.test";
        registrationPage.registerUser(username, email, knownPassword);
        Assert.assertFalse(registrationPage.isRegistrationErrorDisplayed(),
                "Registration should succeed with a known password. Error: " + registrationPage.getRegistrationErrorText());

        dashboardPage = loginPage.loginAsAdmin();
        UsersPage usersPage = dashboardPage.clickUsersTab();
        usersPage.clickPendingUsersSubTab();
        Assert.assertTrue(usersPage.getUserTable().isUserRowPresent(username),
                "New user should appear in Pending users before approval");

        usersPage.getUserTable().approveUser(username);

        // If bypass works, the original password must still be valid.
        ThemeMyLoginLoginPage userLogin = new ThemeMyLoginLoginPage(driver);
        userLogin.login(username, knownPassword);
        Assert.assertFalse(userLogin.isErrorMessageDisplayed(),
                "Login with the original password should succeed when bypass is enabled");
    }

    private boolean waitForDeniedUser(UsersPage usersPage, String email) {
        long deadline = System.nanoTime() + Duration.ofSeconds(75).toNanos();
        while (System.nanoTime() < deadline) {
            driver.navigate().refresh();
            usersPage.clickDeniedUsersSubTab();
            usersPage.getUserTable().searchUser(email);
            if (usersPage.getUserTable().isUserRowPresent(email)) {
                return true;
            }
            try {
                Thread.sleep(5000);
            } catch (InterruptedException interrupted) {
                Thread.currentThread().interrupt();
                return false;
            }
        }
        return false;
    }

    @Test(groups = {"settings", "registration"}, description = "Registration deadline blocks new registrations after the configured date")
    public void testRegistrationDeadlineBlocksRegistration() {
        LoginPage loginPage = new LoginPage(driver);
        DashboardPage dashboardPage = loginPage.loginAsAdmin();
        SettingsPage settingsPage = dashboardPage.clickSettingsTab();

        settingsPage.setEnableAutoApproveToggle(false);
        settingsPage.setAutoDenialToggle(false);
        // Temporarily enable the invitation-code section so the override
        // control is rendered and can be explicitly reset.
        settingsPage.setEnableInvitationCodeToggle(true);
        settingsPage.setInvitationCodeRequiredToggle(false);
        settingsPage.setApproveInvitationCodeAfterDeadline(false);
        settingsPage.setEnableInvitationCodeToggle(false);
        LocalDateTime deadline = fiveMinutePickerDeadline();
        settingsPage.setRegistrationDeadlineDateTime(deadline);
        settingsPage.saveSettings();
        driver.navigate().refresh();
        Assert.assertTrue(settingsPage.isRegistrationDeadlineEnabled(),
                "Registration Deadline should persist as enabled");
        Assert.assertTrue(settingsPage.isRegistrationDeadlineDateTypeEnabled(),
                "Date and Time deadline type should persist as enabled");
        Assert.assertFalse(settingsPage.isRegistrationDeadlineNumberTypeEnabled(),
                "Number of Registration deadline type should persist as disabled");
        Instant configuredDeadline = parseConfiguredDeadlineInstant(settingsPage.getRegistrationDeadlineDateTime());

        RegistrationPage registrationPage = new RegistrationPage(driver);
        String beforeDeadlineUsername = TestData.generateUsername();
        String beforeDeadlineEmail = beforeDeadlineUsername + "@example.test";
        registrationPage.registerUser(beforeDeadlineUsername, beforeDeadlineEmail);
        Assert.assertTrue(registrationPage.isRegistrationSuccessful(),
                "Registration before the deadline should be accepted");

        dashboardPage = loginPage.loginAsAdmin();
        UsersPage usersPage = dashboardPage.clickUsersTab();
        usersPage.clickPendingUsersSubTab();
        usersPage.getUserTable().searchUser(beforeDeadlineEmail);
        Assert.assertTrue(usersPage.getUserTable().isUserRowPresent(beforeDeadlineEmail),
                "User registered before the deadline should initially be pending");

        waitUntil(configuredDeadline.plusSeconds(15));

        dashboardPage = loginPage.loginAsAdmin();
        usersPage = dashboardPage.clickUsersTab();
        usersPage.clickPendingUsersSubTab();
        usersPage.getUserTable().searchUser(beforeDeadlineEmail);
        Assert.assertTrue(usersPage.getUserTable().isUserRowPresent(beforeDeadlineEmail),
                "Registration deadline should not change the status of a user already registered before it");

        String afterDeadlineUsername = TestData.generateUsername();
        String afterDeadlineEmail = afterDeadlineUsername + "@example.test";
        registrationPage.registerUser(afterDeadlineUsername, afterDeadlineEmail);
        Assert.assertTrue(registrationPage.isRegistrationFailed(),
                "Registration after the deadline should be rejected");
    }

    @Test(groups = {"settings", "registration", "invitation"}, description = "Invitation code can approve registration after deadline")
    public void testInvitationCodeApprovalAfterRegistrationDeadline() {
        LoginPage loginPage = new LoginPage(driver);
        DashboardPage dashboardPage = loginPage.loginAsAdmin();
        SettingsPage settingsPage = dashboardPage.clickSettingsTab();

        settingsPage.setEnableAutoApproveToggle(false);
        settingsPage.setAutoDenialToggle(false);
        settingsPage.setEnableInvitationCodeToggle(true);
        settingsPage.setInvitationCodeRequiredToggle(true);
        settingsPage.saveSettings();

        String invitationCode = TestData.generateInvitationCode();
        InvitationCodesPage invitationCodesPage = dashboardPage.clickInvitationCodesTab();
        invitationCodesPage.createManualCode(invitationCode, 1);
        Assert.assertTrue(invitationCodesPage.isCodePresentInList(invitationCode),
                "The invitation code fixture should be active before the deadline test starts");

        dashboardPage = loginPage.loginAsAdmin();
        settingsPage = dashboardPage.clickSettingsTab();
        settingsPage.setAutoDenialToggle(false);
        LocalDateTime deadline = fiveMinutePickerDeadline();
        settingsPage.setRegistrationDeadlineDateTime(deadline);
        settingsPage.setApproveInvitationCodeAfterDeadline(true);
        settingsPage.saveSettings();
        driver.navigate().refresh();
        Assert.assertTrue(settingsPage.isRegistrationDeadlineEnabled(),
                "Registration Deadline should persist as enabled");
        Assert.assertTrue(settingsPage.isRegistrationDeadlineDateTypeEnabled(),
                "Date and Time deadline type should persist as enabled");
        Assert.assertFalse(settingsPage.isRegistrationDeadlineNumberTypeEnabled(),
                "Number of Registration deadline type should persist as disabled");
        Assert.assertTrue(settingsPage.isApproveInvitationCodeAfterDeadlineEnabled(),
                "Approve On Invitation Code After Deadline should persist as enabled");

        Instant configuredDeadline = parseConfiguredDeadlineInstant(settingsPage.getRegistrationDeadlineDateTime());
        waitUntil(configuredDeadline.plusSeconds(15));

        RegistrationPage registrationPage = new RegistrationPage(driver);
        String username = TestData.generateUsername();
        String email = username + "@example.test";
        registrationPage.registerUserWithInvitationCode(username, email, invitationCode);
        Assert.assertTrue(registrationPage.isRegistrationSuccessful(),
                "Valid invitation-code registration should be accepted after the deadline");

        dashboardPage = loginPage.loginAsAdmin();
        UsersPage usersPage = dashboardPage.clickUsersTab();
        usersPage.clickApprovedUsersSubTab();
        usersPage.getUserTable().searchUser(email);
        Assert.assertTrue(usersPage.getUserTable().isUserRowPresent(email),
                "Invitation-code registration after the deadline should be approved");
    }

    private void waitUntil(LocalDateTime deadline) {
        waitUntil(deadline.atZone(getBrowserTimezone()).toInstant());
    }

    private Instant parseConfiguredDeadlineInstant(String pickerValue) {
        // pickerValue example: "2026-09-07 01:00 PM"
        // The MUI DateTimePicker displays browser-local time, so parse it
        // using the browser's timezone rather than the WordPress server zone.
        java.time.format.DateTimeFormatter fmt = java.time.format.DateTimeFormatter
                .ofPattern("yyyy-MM-dd hh:mm a", java.util.Locale.ENGLISH);
        LocalDateTime ldt = LocalDateTime.parse(pickerValue, fmt);
        return ldt.atZone(getBrowserTimezone()).toInstant();
    }

    private LocalDateTime fiveMinutePickerDeadline() {
        // The MUI clock exposes minutes in five-minute increments. Round
        // upward so the selected value is always a future, enabled option.
        // DateTimePicker calculates minDateTime from the browser's local
        // JavaScript clock, so use that same clock instead of the JVM clock.
        String browserNow = (String) ((JavascriptExecutor) driver).executeScript(
                "const d = new Date();"
                        + "const p = n => String(n).padStart(2, '0');"
                        + "return d.getFullYear() + '-' + p(d.getMonth() + 1) + '-' + p(d.getDate())"
                        + " + ' ' + p(d.getHours()) + ':' + p(d.getMinutes()) + ':' + p(d.getSeconds());");
        LocalDateTime target = LocalDateTime.parse(
                browserNow,
                java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                .plusMinutes(5);
        int roundedMinute = ((target.getMinute() + 4) / 5) * 5;
        if (roundedMinute == 60) {
            return target.withMinute(0).withSecond(0).withNano(0).plusHours(1);
        }
        return target.withMinute(roundedMinute).withSecond(0).withNano(0);
    }

    private void waitUntil(Instant deadline) {
        while (Instant.now().isBefore(deadline)) {
            long remainingMillis = Duration.between(Instant.now(), deadline).toMillis();
            try {
                Thread.sleep(Math.min(5000, Math.max(250, remainingMillis)));
            } catch (InterruptedException interrupted) {
                Thread.currentThread().interrupt();
                throw new AssertionError("Interrupted while waiting for registration deadline", interrupted);
            }
        }
    }
}
