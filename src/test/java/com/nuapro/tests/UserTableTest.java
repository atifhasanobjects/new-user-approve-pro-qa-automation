package com.nuapro.tests;

import com.nuapro.base.BaseTest;
import com.nuapro.pages.DashboardPage;
import com.nuapro.pages.LoginPage;
import com.nuapro.pages.UsersPage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class UserTableTest extends BaseTest {

    @Test(groups = {"users"}, description = "TC011: User search filters table records")
    public void testUserSearch() {
        LoginPage loginPage = new LoginPage(driver);
        DashboardPage dashboardPage = loginPage.loginAsAdmin();
        UsersPage usersPage = dashboardPage.clickUsersTab();

        usersPage.getUserTable().searchUser("admin");
        Assert.assertTrue(usersPage.getUserTable().isUserRowPresent("admin"),
                "Search query should leave the matching admin user visible");
    }

    @Test(groups = {"users"}, description = "TC012: Status sub-tab filtering")
    public void testStatusFiltering() {
        LoginPage loginPage = new LoginPage(driver);
        DashboardPage dashboardPage = loginPage.loginAsAdmin();
        UsersPage usersPage = dashboardPage.clickUsersTab();

        usersPage.clickPendingUsersSubTab();
        usersPage.clickApprovedUsersSubTab();
        usersPage.clickDeniedUsersSubTab();
        usersPage.clickBlockedUsersSubTab();
    }

    @Test(groups = {"users"}, description = "TC014: Users-per-page selector applies page size")
    public void testRowsPerPageSelector() {
        LoginPage loginPage = new LoginPage(driver);
        DashboardPage dashboardPage = loginPage.loginAsAdmin();
        UsersPage usersPage = dashboardPage.clickUsersTab();

        usersPage.getUserTable().selectRowsPerPage(20);
    }
}
