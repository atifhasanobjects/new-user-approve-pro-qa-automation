package com.nuapro.tests;

import com.nuapro.base.BaseTest;
import com.nuapro.pages.DashboardPage;
import com.nuapro.pages.InvitationCodesPage;
import com.nuapro.pages.LoginPage;
import com.nuapro.utils.TestData;
import org.testng.Assert;
import org.testng.annotations.Test;

public class InvitationCodeTest extends BaseTest {

    @Test(groups = {"invitation"}, description = "TC020: Create manual invitation code")
    public void testCreateManualInvitationCode() {
        LoginPage loginPage = new LoginPage(driver);
        DashboardPage dashboardPage = loginPage.loginAsAdmin();
        InvitationCodesPage invPage = dashboardPage.clickInvitationCodesTab();

        String codeTitle = TestData.generateInvitationCode();
        invPage.createManualCode(codeTitle, 5);

        driver.navigate().refresh();
        Assert.assertTrue(invPage.isCodePresentInList(codeTitle), "Created invitation code should appear in list");
    }

    @Test(groups = {"invitation"}, description = "TC021: Reject duplicate invitation code")
    public void testRejectDuplicateInvitationCode() {
        LoginPage loginPage = new LoginPage(driver);
        DashboardPage dashboardPage = loginPage.loginAsAdmin();
        InvitationCodesPage invPage = dashboardPage.clickInvitationCodesTab();

        String codeTitle = TestData.generateInvitationCode();
        invPage.createManualCode(codeTitle, 5);
        invPage.createManualCode(codeTitle, 5);
    }

    @Test(groups = {"invitation"}, description = "TC022: Auto-generate invitation codes")
    public void testAutoGenerateInvitationCodes() {
        LoginPage loginPage = new LoginPage(driver);
        DashboardPage dashboardPage = loginPage.loginAsAdmin();
        InvitationCodesPage invPage = dashboardPage.clickInvitationCodesTab();

        invPage.autoGenerateCodes(3);
    }

    @Test(groups = {"invitation", "destructive"}, description = "TC025: Delete invitation code")
    public void testDeleteInvitationCode() {
        LoginPage loginPage = new LoginPage(driver);
        DashboardPage dashboardPage = loginPage.loginAsAdmin();
        InvitationCodesPage invPage = dashboardPage.clickInvitationCodesTab();

        String codeTitle = TestData.generateInvitationCode();
        invPage.createManualCode(codeTitle, 1);
        invPage.deleteCode(codeTitle);
    }
}
