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

    @Test(groups = {"roles", "capabilities", "destructive"},
            description = "Capabilities: Add, edit, assign, persist, and delete a custom capability")
    public void testCustomRoleCapabilityPersistence() {
        LoginPage loginPage = new LoginPage(driver);
        DashboardPage dashboardPage = loginPage.loginAsAdmin();
        RoleEditorPage rolePage = dashboardPage.clickRoleEditorTab();

        String roleName = TestData.generateRoleName();
        String displayName = "QA Capability Role " + roleName.replace('_', ' ');
        String capabilityName = "qa_capability_" + TestData.generateUniqueId();
        String editedCapabilityName = capabilityName + "_edited";

        rolePage.createRole(roleName, displayName);
        try {
            rolePage.openCapabilitiesTab();
            // The Capabilities tab defaults to Administrator. Custom
            // capability Edit/Delete actions are only rendered after a
            // non-administrator role is selected.
            rolePage.selectRoleForCapabilities(roleName);
            rolePage.createCustomCapability(capabilityName);
            Assert.assertTrue(rolePage.isCustomCapabilityPresent(capabilityName),
                    "New custom capability should appear under Custom Capabilities");

            rolePage.editCustomCapability(capabilityName, editedCapabilityName);
            Assert.assertFalse(rolePage.isCustomCapabilityPresent(capabilityName),
                    "Original custom capability name should no longer be present after editing");
            Assert.assertTrue(rolePage.isCustomCapabilityPresent(editedCapabilityName),
                    "Edited custom capability should appear under Custom Capabilities");

            rolePage.selectRoleForCapabilities(roleName);
            rolePage.searchCapability(editedCapabilityName);
            rolePage.setCapabilityGranted(editedCapabilityName, true);
            rolePage.saveCapabilities();

            rolePage.openCapabilitiesTab();
            rolePage.selectRoleForCapabilities(roleName);
            rolePage.searchCapability(editedCapabilityName);
            Assert.assertTrue(rolePage.isCapabilityGranted(editedCapabilityName),
                    "Assigned custom capability should persist for the custom role after refresh");

            rolePage.deleteCustomCapability(editedCapabilityName);
            Assert.assertFalse(rolePage.isCustomCapabilityPresent(editedCapabilityName),
                    "Deleted custom capability should be removed from the Capabilities tab");
        } finally {
            // Keep the suite isolated without attempting to delete a default role.
            try {
                rolePage.openCapabilitiesTab();
                if (rolePage.isCustomCapabilityPresent(editedCapabilityName)) {
                    rolePage.deleteCustomCapability(editedCapabilityName);
                } else if (rolePage.isCustomCapabilityPresent(capabilityName)) {
                    rolePage.deleteCustomCapability(capabilityName);
                }
            } catch (Exception cleanupFailure) {
                // Preserve the primary assertion/error; cleanup is best effort.
            }
            try {
                rolePage.openRolesTab();
                rolePage.searchRoleForDeletion(roleName);
                rolePage.deleteRole(roleName);
            } catch (Exception cleanupFailure) {
                // Preserve the primary assertion/error; cleanup is best effort.
            }
        }
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
