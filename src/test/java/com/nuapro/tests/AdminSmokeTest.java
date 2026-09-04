package com.nuapro.tests;

import com.nuapro.base.BaseTest;
import com.nuapro.pages.DashboardPage;
import com.nuapro.pages.LoginPage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class AdminSmokeTest extends BaseTest {

    @Test(groups = {"smoke", "auth"}, description = "TC001: Admin can log into WordPress dashboard")
    public void testAdminLogin() {
        LoginPage loginPage = new LoginPage(driver);
        DashboardPage dashboardPage = loginPage.loginAsAdmin();
        Assert.assertTrue(driver.getCurrentUrl().contains("wp-admin"), "WordPress Admin Dashboard URL should be displayed after login");
    }

    @Test(groups = {"smoke", "navigation"}, description = "TC057: React dashboard loads without permanent loader")
    public void testReactDashboardLoads() {
        LoginPage loginPage = new LoginPage(driver);
        DashboardPage dashboardPage = loginPage.loginAsAdmin();
        dashboardPage.open();
        Assert.assertTrue(dashboardPage.isLoaded(), "NUA React Dashboard should load cleanly");
        dashboardPage.assertMainNavigationAvailable();
    }
}
