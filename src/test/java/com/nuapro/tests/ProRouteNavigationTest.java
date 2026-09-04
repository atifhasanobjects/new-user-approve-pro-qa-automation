package com.nuapro.tests;

import com.nuapro.base.BaseTest;
import com.nuapro.pages.DashboardPage;
import com.nuapro.pages.LoginPage;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class ProRouteNavigationTest extends BaseTest {

    @DataProvider(name = "proRoutes")
    public Object[][] proRoutes() {
        return new Object[][]{
                {"#/action=settings/tab=general", "action=settings", ".setting-main-tabpanel", "General"},
                {"#/action=settings/tab=registration", "action=settings", ".setting-main-tabpanel", "Registration Settings"},
                {"#/action=settings/tab=admin_notification", "action=settings", ".setting-main-tabpanel", "Admin Notification"},
                {"#/action=settings/tab=user_notification", "action=settings", ".setting-main-tabpanel", "User Notification"},
                {"#/action=settings/tab=help", "action=settings", ".setting-main-tabpanel", "Help"},
                {"#/action=auto-approve/tab=whitelist", "action=auto-approve", ".auto-approve-main-tabpanel", "WhiteList"},
                {"#/action=auto-approve/tab=blacklist", "action=auto-approve", ".auto-approve-main-tabpanel", "BlackList"},
                {"#/action=users/tab=all-users", "action=users", ".users-main-tabpanel", "All Users"},
                {"#/action=users/tab=approved-users", "action=users", ".users-main-tabpanel", "Approved"},
                {"#/action=users/tab=pending-users", "action=users", ".users-main-tabpanel", "Pending"},
                {"#/action=users/tab=denied-users", "action=users", ".users-main-tabpanel", "Denied"},
                {"#/action=users/tab=blocked-users", "action=users", ".users-main-tabpanel", "Blocked Users"},
                {"#/action=users/tab=user_roles", "action=users", ".users-main-tabpanel", "Role Change Request"},
                {"#/action=inv-codes/tab=all-codes", "action=inv-codes", ".invitation-main-tabpanel", "Invitation Code"},
                {"#/", "page=new-user-approve-admin", ".nua_dash_parent_tabs", "Dashboard"},
                {"#/action=role-editor/tab=roles", "action=role-editor", ".role-editor-main-tabpanel", "Roles"},
                {"#/action=role-editor/tab=capabilities", "action=role-editor", ".role-editor-main-tabpanel", "Capabilities"},
                {"#/action=integrations", "action=integrations", ".integration-main-tabpanel", "Integration"},
                {"#/action=mobile-app", "action=mobile-app", ".mobileapp-main-tabpanel", "Mobile App"}
        };
    }

    @Test(dataProvider = "proRoutes", groups = {"navigation", "routes"},
            description = "All supported NUA Pro hash routes render their expected panel")
    public void routeRendersExpectedPanel(String hashRoute, String expectedRoute, String expectedPanel, String expectedText) {
        DashboardPage dashboardPage = new LoginPage(driver).loginAsAdmin();
        dashboardPage.navigateToHash(hashRoute);
        dashboardPage.waitForRoute(expectedRoute);

        Assert.assertTrue(dashboardPage.isPanelVisible(expectedPanel),
                "Expected panel " + expectedPanel + " was not visible for route " + hashRoute);
        Assert.assertTrue(dashboardPage.isTextVisible(expectedText),
                "Expected route text " + expectedText + " was not visible for route " + hashRoute);
    }
}
