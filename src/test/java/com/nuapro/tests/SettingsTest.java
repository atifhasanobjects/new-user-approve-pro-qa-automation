package com.nuapro.tests;

import com.nuapro.base.BaseTest;
import com.nuapro.pages.DashboardPage;
import com.nuapro.pages.LoginPage;
import com.nuapro.pages.RegistrationPage;
import com.nuapro.pages.SettingsPage;
import com.nuapro.pages.UsersPage;
import com.nuapro.utils.TestData;
import org.testng.Assert;
import org.testng.annotations.Test;

public class SettingsTest extends BaseTest {

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
}
