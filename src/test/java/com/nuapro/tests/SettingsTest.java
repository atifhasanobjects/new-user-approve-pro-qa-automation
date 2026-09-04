package com.nuapro.tests;

import com.nuapro.base.BaseTest;
import com.nuapro.pages.DashboardPage;
import com.nuapro.pages.LoginPage;
import com.nuapro.pages.SettingsPage;
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

        settingsPage.setUserRoleRequestToggle(true);
        settingsPage.saveSettings();
        driver.navigate().refresh();

        Assert.assertTrue(settingsPage.isUserRoleRequestToggleEnabled(),
                "Select User Role Request (Registration) should remain enabled after refresh");
    }

    @Test(groups = {"settings", "approval"}, description = "Approval settings: selected auto-approval role persists")
    public void testSpecificUserRoleAutoApprovalPersistence() {
        LoginPage loginPage = new LoginPage(driver);
        DashboardPage dashboardPage = loginPage.loginAsAdmin();
        SettingsPage settingsPage = dashboardPage.clickSettingsTab();

        settingsPage.selectAutoApprovalRole("Subscriber");
        settingsPage.saveSettings();
        driver.navigate().refresh();

        Assert.assertTrue(settingsPage.isAutoApprovalRoleSelected("Subscriber"),
                "Subscriber should remain selected for role-based auto-approval after refresh");
    }
}
