package com.nuapro.tests;

import com.nuapro.base.BaseTest;
import com.nuapro.pages.DashboardPage;
import com.nuapro.pages.IntegrationsPage;
import com.nuapro.pages.LoginPage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class IntegrationTest extends BaseTest {

    @Test(groups = {"integration", "woocommerce"}, description = "WooCommerce registration integration test placeholder")
    public void testWooCommerceIntegration() {
        LoginPage loginPage = new LoginPage(driver);
        DashboardPage dashboardPage = loginPage.loginAsAdmin();
        IntegrationsPage integrationsPage = dashboardPage.clickIntegrationsTab();
        Assert.assertTrue(integrationsPage.isLoaded(), "Integrations tab should render successfully");
    }

    @Test(groups = {"integration", "gravityforms"}, description = "Gravity Forms registration integration test placeholder")
    public void testGravityFormsIntegration() {
        LoginPage loginPage = new LoginPage(driver);
        DashboardPage dashboardPage = loginPage.loginAsAdmin();
        IntegrationsPage integrationsPage = dashboardPage.clickIntegrationsTab();
        Assert.assertTrue(integrationsPage.isLoaded(), "Integrations tab should render successfully");
    }
}
