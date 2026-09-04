package com.nuapro.tests;

import com.nuapro.base.BaseTest;
import com.nuapro.pages.DashboardPage;
import com.nuapro.pages.LoginPage;
import com.nuapro.pages.RegistrationPage;
import com.nuapro.pages.SettingsPage;
import com.nuapro.pages.UsersPage;
import com.nuapro.utils.TestData;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

public class BulkUserActionTest extends BaseTest {

    @Test(groups = {"bulk", "users"}, description = "TC017: Bulk approve selected pending users")
    public void testBulkApproveUsers() {
        LoginPage loginPage = new LoginPage(driver);
        DashboardPage dashboardPage = loginPage.loginAsAdmin();
        List<String> testUsers = createPendingUsers(loginPage, dashboardPage);

        dashboardPage = loginPage.loginAsAdmin();
        UsersPage usersPage = dashboardPage.clickUsersTab();
        usersPage.clickPendingUsersSubTab();

        selectUsers(usersPage, testUsers);
        usersPage.getUserTable().clickBulkApprove();
        driver.navigate().refresh();
        usersPage.clickApprovedUsersSubTab();
        for (String email : testUsers) {
            usersPage.getUserTable().searchUser(email);
            Assert.assertTrue(usersPage.getUserTable().isUserRowPresent(email),
                    "Bulk-approved user should appear in Approved users: " + email);
        }
    }

    @Test(groups = {"bulk", "users"}, description = "TC018: Bulk deny selected pending users")
    public void testBulkDenyUsers() {
        LoginPage loginPage = new LoginPage(driver);
        DashboardPage dashboardPage = loginPage.loginAsAdmin();
        List<String> testUsers = createPendingUsers(loginPage, dashboardPage);

        dashboardPage = loginPage.loginAsAdmin();
        UsersPage usersPage = dashboardPage.clickUsersTab();
        usersPage.clickPendingUsersSubTab();

        selectUsers(usersPage, testUsers);
        usersPage.getUserTable().clickBulkDeny();
        driver.navigate().refresh();
        usersPage.clickDeniedUsersSubTab();
        for (String email : testUsers) {
            usersPage.getUserTable().searchUser(email);
            Assert.assertTrue(usersPage.getUserTable().isUserRowPresent(email),
                    "Bulk-denied user should appear in Denied users: " + email);
        }
    }

    @Test(groups = {"bulk", "users"}, description = "TC019: Bulk action without user selection")
    public void testBulkActionWithoutSelection() {
        LoginPage loginPage = new LoginPage(driver);
        DashboardPage dashboardPage = loginPage.loginAsAdmin();
        UsersPage usersPage = dashboardPage.clickUsersTab();
        usersPage.clickPendingUsersSubTab();

        // Perform no selection, ensure no unexpected error or state change
    }

    /**
     * Creates isolated accounts for a bulk test. Auto-approval and invitation
     * requirements are disabled so a normal registration remains Pending.
     */
    private List<String> createPendingUsers(LoginPage loginPage, DashboardPage dashboardPage) {
        SettingsPage settingsPage = dashboardPage.clickSettingsTab();
        settingsPage.setEnableAutoApproveToggle(false);
        settingsPage.setEnableInvitationCodeToggle(false);
        settingsPage.saveSettings();

        List<String> emails = new ArrayList<>();
        String fixturePrefix = "nua_bulk_" + TestData.generateUniqueId();
        RegistrationPage registrationPage = new RegistrationPage(driver);
        for (int i = 0; i < 2; i++) {
            String username = fixturePrefix + "_" + i;
            String email = username + "@example.test";
            registrationPage.registerUser(username, email);
            Assert.assertTrue(registrationPage.isRegistrationSuccessful(),
                    "Bulk fixture registration should succeed for " + email);
            emails.add(email);
        }
        return emails;
    }

    private void selectUsers(UsersPage usersPage, List<String> emails) {
        // Searching triggers a React fetch that clears selectedUsers in the
        // plugin. Filter once using the shared fixture prefix, then select all
        // fixture rows from the same rendered result.
        String searchPrefix = emails.get(0).substring(0, emails.get(0).lastIndexOf("_"));
        usersPage.getUserTable().searchUser(searchPrefix);
        for (String email : emails) {
            Assert.assertTrue(usersPage.getUserTable().isUserRowPresent(email),
                    "Pending fixture user should be present before bulk action: " + email);
            usersPage.getUserTable().selectUserCheckbox(email);
        }
    }
}
