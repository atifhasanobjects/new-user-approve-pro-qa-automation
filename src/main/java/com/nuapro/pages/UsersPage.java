package com.nuapro.pages;

import com.nuapro.components.UserTableComponent;
import com.nuapro.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class UsersPage {

    private final WebDriver driver;
    private final WaitUtils waitUtils;
    private final UserTableComponent userTable;

    private final By allUsersSubTab = subTab("All Users", "tab=all-users", "all-users");
    private final By approvedUsersSubTab = subTab("Approved", "tab=approved-users", "approved");
    private final By pendingUsersSubTab = subTab("Pending", "tab=pending-users", "pending");
    private final By deniedUsersSubTab = subTab("Denied", "tab=denied-users", "denied");
    private final By blockedUsersSubTab = subTab("Blocked Users", "tab=blocked-users", "blocked");
    private final By roleChangeSubTab = subTab("Role Change Request", "tab=user_roles", "role-change");

    public UsersPage(WebDriver driver) {
        this.driver = driver;
        this.waitUtils = new WaitUtils(driver);
        this.userTable = new UserTableComponent(driver);
    }

    public UserTableComponent getUserTable() {
        return userTable;
    }

    public UsersPage clickAllUsersSubTab() {
        waitUtils.clickElement(allUsersSubTab);
        waitUtils.waitForLoaderToDisappear();
        return this;
    }

    public UsersPage clickApprovedUsersSubTab() {
        waitUtils.clickElement(approvedUsersSubTab);
        waitUtils.waitForLoaderToDisappear();
        return this;
    }

    public UsersPage clickPendingUsersSubTab() {
        waitUtils.clickElement(pendingUsersSubTab);
        waitUtils.waitForLoaderToDisappear();
        return this;
    }

    public UsersPage clickDeniedUsersSubTab() {
        waitUtils.clickElement(deniedUsersSubTab);
        waitUtils.waitForLoaderToDisappear();
        return this;
    }

    public UsersPage clickBlockedUsersSubTab() {
        waitUtils.clickElement(blockedUsersSubTab);
        waitUtils.waitForLoaderToDisappear();
        return this;
    }

    public UsersPage clickRoleChangeSubTab() {
        waitUtils.clickElement(roleChangeSubTab);
        waitUtils.waitForLoaderToDisappear();
        return this;
    }

    private static By subTab(String label, String value, String testIdSuffix) {
        return By.xpath("//*[contains(concat(' ', normalize-space(@class), ' '), ' users_subtabs_list ')]"
                + "//*[self::button or self::a or @role='tab'][@value='" + value + "' or @data-testid='nua-subtab-" + testIdSuffix
                + "' or contains(normalize-space(.), '" + label + "')]");
    }
}
