package com.nuapro.tests;

import com.nuapro.base.BaseTest;
import com.nuapro.pages.DashboardPage;
import com.nuapro.pages.InvitationCodesPage;
import com.nuapro.pages.LoginPage;
import com.nuapro.pages.RegistrationPage;
import com.nuapro.pages.SettingsPage;
import com.nuapro.pages.UsersPage;
import com.nuapro.utils.TestData;
import org.testng.Assert;
import org.testng.annotations.Test;

public class InvitationCodeFunctionalTest extends BaseTest {

    @Test(groups = {"invitation", "functional"}, description = "Verify Invitation Code Enable/Disable UI toggle and functional registration flow")
    public void testInvitationCodeFunctionalFlow() {
        // 1. Enable Invitation Code in Settings
        LoginPage loginPage = new LoginPage(driver);
        DashboardPage dashboardPage = loginPage.loginAsAdmin();

        SettingsPage settingsPage = dashboardPage.clickSettingsTab();
        settingsPage.setEnableInvitationCodeToggle(true);
        settingsPage.saveSettings();

        // Verify persisted server state before using a dependent workflow.
        driver.navigate().refresh();
        Assert.assertTrue(settingsPage.isInvitationCodeToggleEnabled(),
                "Invitation Code toggle must be enabled and persisted before creating a code");

        // 2. Create manual invitation code
        InvitationCodesPage invPage = dashboardPage.clickInvitationCodesTab();
        String codeTitle = TestData.generateInvitationCode();
        invPage.createManualCode(codeTitle, 5);

        Assert.assertTrue(invPage.isCodePresentInList(codeTitle), "UI Verification: Invitation code created and visible in list");

        // 3. Functional Verification: Register user with invitation code
        String testUser = TestData.generateUsername();
        String testEmail = TestData.generateEmail();

        RegistrationPage regPage = new RegistrationPage(driver);
        regPage.registerUserWithInvitationCode(testUser, testEmail, codeTitle);

        // 4. Check user approval in NUA dashboard
        dashboardPage = loginPage.loginAsAdmin();
        UsersPage usersPage = dashboardPage.clickUsersTab();
        usersPage.getUserTable().searchUser(testEmail);

        Assert.assertTrue(usersPage.getUserTable().isUserRowPresent(testEmail),
                "Functional Verification: User registered with valid invitation code should be recorded in plugin users table");
    }
}
