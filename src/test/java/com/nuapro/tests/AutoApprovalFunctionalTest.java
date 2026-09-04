package com.nuapro.tests;

import com.nuapro.base.BaseTest;
import com.nuapro.pages.AutoApprovePage;
import com.nuapro.pages.DashboardPage;
import com.nuapro.pages.LoginPage;
import com.nuapro.pages.RegistrationPage;
import com.nuapro.pages.SettingsPage;
import com.nuapro.pages.UsersPage;
import com.nuapro.utils.TestData;
import org.testng.Assert;
import org.testng.annotations.Test;

public class AutoApprovalFunctionalTest extends BaseTest {

    @Test(groups = {"autoapproval", "functional"}, description = "Verify Auto Approve Whitelist UI state toggle persistence & real registration auto-approval")
    public void testAutoApproveWhitelistFunctionalFlow() {
        // 1. Admin logs in and enables Whitelist domain
        LoginPage loginPage = new LoginPage(driver);
        DashboardPage dashboardPage = loginPage.loginAsAdmin();
        enableAndVerifyAutoApprove(dashboardPage);
        AutoApprovePage autoPage = dashboardPage.clickAutoApproveTab();

        String domain = TestData.generateDomain();
        autoPage.updateWhitelist(domain, true);

        // 2. Refresh & verify UI state
        driver.navigate().refresh();
        Assert.assertTrue(autoPage.isWhitelistToggleEnabled(), "UI Verification: Whitelist toggle should be ON");
        Assert.assertTrue(autoPage.getWhitelistContent().contains(domain), "UI Verification: Whitelisted domain persists");

        // 3. Functional Verification: Register user with whitelisted domain on frontend
        String testUser = TestData.generateUsername();
        String testEmail = TestData.generateDomainEmail(domain);

        RegistrationPage regPage = new RegistrationPage(driver);
        regPage.registerUser(testUser, testEmail);
        Assert.assertTrue(regPage.isRegistrationSuccessful(),
                "A whitelisted-domain registration should complete successfully");

        // 4. Admin logs back in & checks user status in Approved Users tab
        dashboardPage = loginPage.loginAsAdmin();
        UsersPage usersPage = dashboardPage.clickUsersTab();
        usersPage.clickApprovedUsersSubTab();
        usersPage.getUserTable().searchUser(testEmail);

        Assert.assertTrue(usersPage.getUserTable().isUserRowPresent(testEmail),
                "Functional Verification: User registered with whitelisted domain should be automatically Approved");
    }

    @Test(groups = {"autoapproval", "functional"}, description = "Verify Blacklist domain prevents user registration or sets Blocked status")
    public void testBlacklistDomainFunctionalFlow() {
        LoginPage loginPage = new LoginPage(driver);
        DashboardPage dashboardPage = loginPage.loginAsAdmin();
        enableAndVerifyAutoApprove(dashboardPage);
        AutoApprovePage autoPage = dashboardPage.clickAutoApproveTab();

        String domain = TestData.generateDomain();
        // Preserve the site's configured blacklist message. This test manages
        // only the generated domain and toggle state.
        autoPage.updateBlacklist(domain, null, true);

        // UI Verification
        driver.navigate().refresh();
        Assert.assertTrue(autoPage.isBlacklistToggleEnabled(), "UI Verification: Blacklist toggle should be ON");
        Assert.assertTrue(autoPage.getBlacklistContent().contains(domain), "UI Verification: Blacklisted domain persists");

        // Functional Verification
        String testUser = TestData.generateUsername();
        String testEmail = TestData.generateDomainEmail(domain);

        RegistrationPage regPage = new RegistrationPage(driver);
        regPage.registerUser(testUser, testEmail);

        Assert.assertTrue(regPage.isRegistrationFailed(),
                "A registration with a blacklisted domain must be rejected");
    }

    @Test(groups = {"autoapproval", "functional", "blacklist", "whitelist"},
            description = "TC061: Blacklist takes priority when the same domain appears in both lists")
    public void testBlacklistTakesPriorityOverWhitelistForSameDomain() {
        LoginPage loginPage = new LoginPage(driver);
        DashboardPage dashboardPage = loginPage.loginAsAdmin();
        enableAndVerifyAutoApprove(dashboardPage);

        AutoApprovePage autoPage = dashboardPage.clickAutoApproveTab();
        String domain = TestData.generateDomain();
        autoPage.updateWhitelist(domain, true);

        // An empty message deliberately restores the plugin's default message.
        autoPage.updateBlacklist(domain, "", true);

        driver.navigate().refresh();
        Assert.assertTrue(autoPage.isWhitelistToggleEnabled(), "Whitelist toggle should persist as enabled");
        Assert.assertTrue(autoPage.isBlacklistToggleEnabled(), "Blacklist toggle should persist as enabled");
        Assert.assertTrue(autoPage.getWhitelistContent().contains(domain), "Domain should persist in Whitelist");
        Assert.assertTrue(autoPage.getBlacklistContent().contains(domain), "Domain should persist in Blacklist");

        RegistrationPage registrationPage = new RegistrationPage(driver);
        registrationPage.registerUser(TestData.generateUsername(), TestData.generateDomainEmail(domain));

        Assert.assertTrue(registrationPage.isRegistrationFailed(),
                "Blacklist must reject a domain even when the same domain is whitelisted");
        String errorText = registrationPage.getRegistrationErrorText();
        Assert.assertTrue(errorText.contains("ERROR")
                        && errorText.contains("This email domain is not allowed for registration. Please contact administrator."),
                "Blacklist priority must display the default WordPress registration error. Actual: " + errorText);
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
