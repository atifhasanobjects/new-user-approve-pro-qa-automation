package com.nuapro.tests;

import com.nuapro.base.BaseTest;
import com.nuapro.pages.DashboardPage;
import com.nuapro.pages.LoginPage;
import com.nuapro.pages.RoleEditorPage;
import com.nuapro.utils.TestData;
import org.testng.Assert;
import org.testng.annotations.Test;

public class RoleEditorTest extends BaseTest {

    @Test(groups = {"roles"}, description = "TC046: Create custom role and verify in list")
    public void testCreateCustomRole() {
        LoginPage loginPage = new LoginPage(driver);
        DashboardPage dashboardPage = loginPage.loginAsAdmin();
        RoleEditorPage rolePage = dashboardPage.clickRoleEditorTab();

        String roleName = TestData.generateRoleName();
        String displayName = "QA Role " + System.currentTimeMillis();

        rolePage.createRole(roleName, displayName);
        driver.navigate().refresh();
        Assert.assertTrue(rolePage.isRolePresent(roleName) || rolePage.isRolePresent(displayName), "Created role should appear in roles table");
    }

    @Test(groups = {"roles"}, description = "TC054: Sort Role column")
    public void testSortRoleColumn() {
        LoginPage loginPage = new LoginPage(driver);
        DashboardPage dashboardPage = loginPage.loginAsAdmin();
        RoleEditorPage rolePage = dashboardPage.clickRoleEditorTab();

        rolePage.sortRoleColumn();
    }

    @Test(groups = {"roles"}, description = "TC055: Sort Users column")
    public void testSortUsersColumn() {
        LoginPage loginPage = new LoginPage(driver);
        DashboardPage dashboardPage = loginPage.loginAsAdmin();
        RoleEditorPage rolePage = dashboardPage.clickRoleEditorTab();

        rolePage.sortUsersColumn();
    }

    @Test(groups = {"roles"}, description = "TC056: Sort Capabilities column")
    public void testSortCapabilitiesColumn() {
        LoginPage loginPage = new LoginPage(driver);
        DashboardPage dashboardPage = loginPage.loginAsAdmin();
        RoleEditorPage rolePage = dashboardPage.clickRoleEditorTab();

        rolePage.sortCapabilitiesColumn();
    }

    @Test(groups = {"roles", "destructive"}, description = "TC049: Delete custom role")
    public void testDeleteRole() {
        LoginPage loginPage = new LoginPage(driver);
        DashboardPage dashboardPage = loginPage.loginAsAdmin();
        RoleEditorPage rolePage = dashboardPage.clickRoleEditorTab();

        String roleName = TestData.generateRoleName();
        String displayName = "Delete Target Role " + roleName.replace('_', ' ');
        rolePage.createRole(roleName, displayName);
        rolePage.deleteRole(roleName);
    }
}
