package com.nuapro.tests;

import com.nuapro.base.BaseTest;
import com.nuapro.pages.DashboardPage;
import com.nuapro.pages.LoginPage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class DashboardNavigationTest extends BaseTest {

    @Test(groups = {"navigation"}, description = "TC058: Navigate through all main plugin sections")
    public void testNavigateAllSections() {
        LoginPage loginPage = new LoginPage(driver);
        DashboardPage dashboardPage = loginPage.loginAsAdmin();

        dashboardPage.clickUsersTab();
        Assert.assertTrue(driver.getCurrentUrl().contains("action=users"), "URL should contain action=users");

        dashboardPage.clickRoleEditorTab();
        Assert.assertTrue(driver.getCurrentUrl().contains("action=role-editor"), "URL should contain action=role-editor");

        dashboardPage.clickInvitationCodesTab();
        Assert.assertTrue(driver.getCurrentUrl().contains("action=inv-codes"), "URL should contain action=inv-codes");

        dashboardPage.clickAutoApproveTab();
        Assert.assertTrue(driver.getCurrentUrl().contains("action=auto-approve"), "URL should contain action=auto-approve");

        dashboardPage.clickIntegrationsTab();
        Assert.assertTrue(driver.getCurrentUrl().contains("action=integrations"), "URL should contain action=integrations");

        dashboardPage.clickMobileAppTab();
        Assert.assertTrue(driver.getCurrentUrl().contains("action=mobile-app"), "URL should contain action=mobile-app");

        dashboardPage.clickSettingsTab();
        Assert.assertTrue(driver.getCurrentUrl().contains("action=settings"), "URL should contain action=settings");
    }

    @Test(groups = {"navigation"}, description = "TC059: Direct hash route navigation")
    public void testDirectHashRouteNavigation() {
        LoginPage loginPage = new LoginPage(driver);
        DashboardPage dashboardPage = loginPage.loginAsAdmin();

        dashboardPage.navigateToHash("#/action=settings/tab=general");
        Assert.assertTrue(driver.getCurrentUrl().contains("action=settings"), "Direct hash route should load settings view");
    }
}
