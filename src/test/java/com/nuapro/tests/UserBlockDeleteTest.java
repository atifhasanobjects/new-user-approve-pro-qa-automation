package com.nuapro.tests;

import com.nuapro.base.BaseTest;
import com.nuapro.components.ConfirmationModal;
import com.nuapro.pages.DashboardPage;
import com.nuapro.pages.LoginPage;
import com.nuapro.pages.UsersPage;
import com.nuapro.utils.TestData;
import org.testng.Assert;
import org.testng.annotations.Test;

public class UserBlockDeleteTest extends BaseTest {

    @Test(groups = {"users"}, description = "TC007: Block user and verify Blocked status")
    public void testBlockUser() {
        LoginPage loginPage = new LoginPage(driver);
        DashboardPage dashboardPage = loginPage.loginAsAdmin();
        UsersPage usersPage = dashboardPage.clickUsersTab();
        usersPage.clickAllUsersSubTab();

        String testEmail = TestData.generateEmail();
        usersPage.getUserTable().searchUser(testEmail);

        if (usersPage.getUserTable().isUserRowPresent(testEmail)) {
            usersPage.getUserTable().openDeleteModal(testEmail);
            ConfirmationModal modal = new ConfirmationModal(driver);
            if (modal.isDisplayed()) {
                modal.toggleBlockUser(true);
                modal.confirm();
            }
            usersPage.clickBlockedUsersSubTab();
            Assert.assertTrue(usersPage.getUserTable().isUserRowPresent(testEmail), "User should appear in Blocked tab");
        }
    }

    @Test(groups = {"users", "destructive"}, description = "TC009: Delete user from plugin table")
    public void testDeleteUser() {
        LoginPage loginPage = new LoginPage(driver);
        DashboardPage dashboardPage = loginPage.loginAsAdmin();
        UsersPage usersPage = dashboardPage.clickUsersTab();
        usersPage.clickAllUsersSubTab();

        String testEmail = TestData.generateEmail();
        usersPage.getUserTable().searchUser(testEmail);

        if (usersPage.getUserTable().isUserRowPresent(testEmail)) {
            usersPage.getUserTable().openDeleteModal(testEmail);
            ConfirmationModal modal = new ConfirmationModal(driver);
            if (modal.isDisplayed()) {
                modal.confirm();
            }
            driver.navigate().refresh();
            usersPage.getUserTable().searchUser(testEmail);
            Assert.assertFalse(usersPage.getUserTable().isUserRowPresent(testEmail), "Deleted user should no longer exist in table");
        }
    }

    @Test(groups = {"users"}, description = "TC010: Cancel user deletion")
    public void testCancelUserDeletion() {
        LoginPage loginPage = new LoginPage(driver);
        DashboardPage dashboardPage = loginPage.loginAsAdmin();
        UsersPage usersPage = dashboardPage.clickUsersTab();
        usersPage.clickAllUsersSubTab();

        String testEmail = TestData.generateEmail();
        usersPage.getUserTable().searchUser(testEmail);

        if (usersPage.getUserTable().isUserRowPresent(testEmail)) {
            usersPage.getUserTable().openDeleteModal(testEmail);
            ConfirmationModal modal = new ConfirmationModal(driver);
            if (modal.isDisplayed()) {
                modal.cancel();
            }
            Assert.assertTrue(usersPage.getUserTable().isUserRowPresent(testEmail), "Cancelled deletion user should remain in table");
        }
    }
}
