package com.nuapro.tests;

import com.nuapro.base.BaseTest;
import com.nuapro.pages.AutoApprovePage;
import com.nuapro.pages.DashboardPage;
import com.nuapro.pages.LoginPage;
import com.nuapro.pages.SettingsPage;
import com.nuapro.utils.TestData;
import org.testng.Assert;
import org.testng.annotations.Test;

public class AutoApprovalTest extends BaseTest {

    @Test(groups = {"autoapproval"}, description = "TC031: Enable Auto Approve Whitelist and add domain")
    public void testWhitelistDomainAutoApproval() {
        LoginPage loginPage = new LoginPage(driver);
        DashboardPage dashboardPage = loginPage.loginAsAdmin();
        enableAndVerifyAutoApprove(dashboardPage);
        AutoApprovePage autoPage = dashboardPage.clickAutoApproveTab();

        String domain = TestData.generateDomain();
        autoPage.updateWhitelist(domain, true);

        driver.navigate().refresh();
        Assert.assertTrue(autoPage.getWhitelistContent().contains(domain), "Whitelisted domain should persist after save and refresh");
        Assert.assertTrue(autoPage.isWhitelistToggleEnabled(), "Whitelist toggle state should persist as enabled");
    }

    @Test(groups = {"autoapproval"}, description = "TC033: Enable Auto Approve Blacklist and add domain")
    public void testBlacklistDomainBehavior() {
        LoginPage loginPage = new LoginPage(driver);
        DashboardPage dashboardPage = loginPage.loginAsAdmin();
        enableAndVerifyAutoApprove(dashboardPage);
        AutoApprovePage autoPage = dashboardPage.clickAutoApproveTab();

        String domain = TestData.generateDomain();
        // Do not replace the plugin's configured blacklist message.
        autoPage.updateBlacklist(domain, null, true);

        driver.navigate().refresh();
        Assert.assertTrue(autoPage.getBlacklistContent().contains(domain), "Blacklisted domain should persist after save and refresh");
        Assert.assertTrue(autoPage.isBlacklistToggleEnabled(), "Blacklist toggle state should persist as enabled");
    }

    private void enableAndVerifyAutoApprove(DashboardPage dashboardPage) {
        SettingsPage settingsPage = dashboardPage.clickSettingsTab();
        settingsPage.setEnableAutoApproveToggle(true);
        settingsPage.saveSettings();
        driver.navigate().refresh();
        Assert.assertTrue(settingsPage.isAutoApproveToggleEnabled(),
                "Enable Auto-Approve must be enabled and persisted before configuring whitelist or blacklist rules");
    }
}
