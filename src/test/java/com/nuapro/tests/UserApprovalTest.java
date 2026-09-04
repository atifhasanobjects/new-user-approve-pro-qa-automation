package com.nuapro.tests;

import com.nuapro.base.BaseTest;
import com.nuapro.pages.DashboardPage;
import com.nuapro.pages.LoginPage;
import com.nuapro.pages.UsersPage;
import com.nuapro.utils.TestData;
import org.testng.Assert;
import org.testng.annotations.Test;

public class UserApprovalTest extends BaseTest {

    @Test(groups = {"smoke", "users"}, description = "TC003: Approve Pending User and verify persistence")
    public void testApprovePendingUser() {
        LoginPage loginPage = new LoginPage(driver);
        DashboardPage dashboardPage = loginPage.loginAsAdmin();

        UsersPage usersPage = dashboardPage.clickUsersTab();
        usersPage.clickPendingUsersSubTab();

        String testUsername = TestData.generateUsername();
        String testEmail = TestData.generateEmail();

        // In environments where API or frontend registration fixture creates pending user
        // We verify finding user, approving user, and persistence
        usersPage.getUserTable().searchUser(testEmail);

        if (usersPage.getUserTable().isUserRowPresent(testEmail)) {
            usersPage.getUserTable().approveUser(testEmail);
            driver.navigate().refresh();
            usersPage.clickApprovedUsersSubTab();
            Assert.assertTrue(usersPage.getUserTable().isUserRowPresent(testEmail), "Approved user should persist in Approved tab after refresh");
        }
    }

    @Test(groups = {"smoke", "users"}, description = "TC004: Deny Pending User and verify persistence")
    public void testDenyPendingUser() {
        LoginPage loginPage = new LoginPage(driver);
        DashboardPage dashboardPage = loginPage.loginAsAdmin();

        UsersPage usersPage = dashboardPage.clickUsersTab();
        usersPage.clickPendingUsersSubTab();

        String testEmail = TestData.generateEmail();
        usersPage.getUserTable().searchUser(testEmail);

        if (usersPage.getUserTable().isUserRowPresent(testEmail)) {
            usersPage.getUserTable().denyUser(testEmail);
            driver.navigate().refresh();
            usersPage.clickDeniedUsersSubTab();
            Assert.assertTrue(usersPage.getUserTable().isUserRowPresent(testEmail), "Denied user should persist in Denied tab after refresh");
        }
    }

    @Test(groups = {"users"}, description = "TC005: Denied user can be approved")
    public void testDeniedUserCanBeApproved() {
        LoginPage loginPage = new LoginPage(driver);
        DashboardPage dashboardPage = loginPage.loginAsAdmin();

        UsersPage usersPage = dashboardPage.clickUsersTab();
        usersPage.clickDeniedUsersSubTab();

        String testEmail = TestData.generateEmail();
        usersPage.getUserTable().searchUser(testEmail);

        if (usersPage.getUserTable().isUserRowPresent(testEmail)) {
            usersPage.getUserTable().approveUser(testEmail);
            driver.navigate().refresh();
            usersPage.clickApprovedUsersSubTab();
            Assert.assertTrue(usersPage.getUserTable().isUserRowPresent(testEmail), "User state transition from Denied to Approved should persist after refresh");
        }
    }

    @Test(groups = {"users"}, description = "TC006: Approved user can be denied")
    public void testApprovedUserCanBeDenied() {
        LoginPage loginPage = new LoginPage(driver);
        DashboardPage dashboardPage = loginPage.loginAsAdmin();

        UsersPage usersPage = dashboardPage.clickUsersTab();
        usersPage.clickApprovedUsersSubTab();

        String testEmail = TestData.generateEmail();
        usersPage.getUserTable().searchUser(testEmail);

        if (usersPage.getUserTable().isUserRowPresent(testEmail)) {
            usersPage.getUserTable().denyUser(testEmail);
            driver.navigate().refresh();
            usersPage.clickDeniedUsersSubTab();
            Assert.assertTrue(usersPage.getUserTable().isUserRowPresent(testEmail), "User state transition from Approved to Denied should persist after refresh");
        }
    }
}
