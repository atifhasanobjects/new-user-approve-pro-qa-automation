package com.nuapro.pages;

import com.nuapro.config.Config;
import com.nuapro.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.TimeoutException;

public class DashboardPage {

    private final WebDriver driver;
    private final WaitUtils waitUtils;

    private final By mainDashboardRoot = By.cssSelector(".nua_dash_parent_tabs, .nua-dash-header");
    // MUI Base renders tabs as links/buttons with role="tab". Values used by
    // React state are not guaranteed to be forwarded to the DOM, so each
    // locator is scoped to the plugin's tab list and has a visible-label fallback.
    private final By dashboardTab = mainTab("dashboard", "dashboard", "Dashboard");
    private final By usersTab = mainTab("users", "action=users", "Users");
    private final By roleEditorTab = mainTab(null, "action=role-editor", "Role Editor");
    private final By invitationCodeTab = mainTab("inv code", "action=inv-codes", "Invitation Code");
    private final By autoApproveTab = mainTab("auto approve", "action=auto-approve", "Auto Approve");
    private final By integrationsTab = mainTab(null, "action=integrations", "Integration");
    private final By mobileAppTab = mainTab(null, "action=mobile-app", "Mobile App");
    private final By settingsTab = mainTab(null, "action=settings", "Settings");

    public DashboardPage(WebDriver driver) {
        this.driver = driver;
        this.waitUtils = new WaitUtils(driver);
    }

    public DashboardPage open() {
        driver.get(Config.getBaseUrl() + "/wp-admin/admin.php?page=new-user-approve-admin");
        return this;
    }

    public boolean isLoaded() {
        try {
            return waitUtils.waitForVisibility(mainDashboardRoot).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public DashboardPage waitUntilLoaded() {
        waitUtils.waitForVisibility(mainDashboardRoot);
        return this;
    }

    public void assertMainNavigationAvailable() {
        waitUtils.waitForVisibility(mainDashboardRoot);
        By[] tabs = {usersTab, roleEditorTab, invitationCodeTab, autoApproveTab, integrationsTab, mobileAppTab, settingsTab};
        String[] names = {"Users", "Role Editor", "Invitation Codes", "Auto Approve", "Integrations", "Mobile App", "Settings"};
        for (int i = 0; i < tabs.length; i++) {
            try {
                waitUtils.waitForClickable(tabs[i]);
            } catch (TimeoutException e) {
                throw new AssertionError("Missing or non-clickable " + names[i] + " tab. "
                        + "matches=" + driver.findElements(tabs[i]).size()
                        + ", " + waitUtils.diagnostics(), e);
            }
        }
    }

    private void clickMainTab(By locator, String route, String tabName) {
        try {
            waitUtils.clickElement(locator);
            waitUtils.waitForUrlContains(route);
            waitUtils.waitForVisibility(mainDashboardRoot);
        } catch (TimeoutException e) {
            throw new AssertionError("Unable to open " + tabName + " tab. "
                    + "matches=" + driver.findElements(locator).size()
                    + ", " + waitUtils.diagnostics(), e);
        }
    }

    public UsersPage clickUsersTab() {
        clickMainTab(usersTab, "action=users", "Users");
        return new UsersPage(driver);
    }

    public RoleEditorPage clickRoleEditorTab() {
        clickMainTab(roleEditorTab, "action=role-editor", "Role Editor");
        return new RoleEditorPage(driver);
    }

    public InvitationCodesPage clickInvitationCodesTab() {
        clickMainTab(invitationCodeTab, "action=inv-codes", "Invitation Codes");
        return new InvitationCodesPage(driver);
    }

    public AutoApprovePage clickAutoApproveTab() {
        clickMainTab(autoApproveTab, "action=auto-approve", "Auto Approve");
        return new AutoApprovePage(driver);
    }

    public SettingsPage clickSettingsTab() {
        clickMainTab(settingsTab, "action=settings", "Settings");
        return new SettingsPage(driver);
    }

    public IntegrationsPage clickIntegrationsTab() {
        clickMainTab(integrationsTab, "action=integrations", "Integrations");
        return new IntegrationsPage(driver);
    }

    public MobileAppPage clickMobileAppTab() {
        clickMainTab(mobileAppTab, "action=mobile-app", "Mobile App");
        return new MobileAppPage(driver);
    }

    public void navigateToHash(String hashRoute) {
        String baseUrl = Config.getBaseUrl() + "/wp-admin/admin.php?page=new-user-approve-admin#/";
        if (hashRoute.startsWith("#/")) {
            driver.get(Config.getBaseUrl() + "/wp-admin/admin.php?page=new-user-approve-admin" + hashRoute);
        } else {
            driver.get(baseUrl + hashRoute);
        }
        waitUtils.waitForUrlContains(hashRoute.startsWith("#/") ? hashRoute.substring(2) : hashRoute);
        waitUtils.waitForVisibility(mainDashboardRoot);
    }

    public void waitForRoute(String route) {
        waitUtils.waitForUrlContains(route);
    }

    public boolean isPanelVisible(String cssSelector) {
        try {
            return waitUtils.waitForVisibility(By.cssSelector(cssSelector)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isTextVisible(String text) {
        try {
            String textLiteral = xpathLiteral(text);
            By textLocator = By.xpath("//*[self::button or self::h1 or self::h2 or self::h3 or self::label]"
                    + "[contains(normalize-space(.), " + textLiteral + ")]");
            return waitUtils.waitForVisibility(textLocator).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    private String xpathLiteral(String value) {
        if (!value.contains("'")) {
            return "'" + value + "'";
        }
        if (!value.contains("\"")) {
            return "\"" + value + "\"";
        }
        return "concat('" + value.replace("'", "',\"'\",'") + "')";
    }

    private static By mainTab(String dataTarget, String routeValue, String label) {
        String dataTargetCondition = dataTarget == null ? "false()" : "@datatarget=" + xpathLiteralStatic(dataTarget);
        String routeCondition = "@value=" + xpathLiteralStatic(routeValue)
                + " or contains(@href, " + xpathLiteralStatic(routeValue) + ")";
        String labelCondition = "contains(normalize-space(.), " + xpathLiteralStatic(label) + ")";
        return By.xpath("//*[contains(concat(' ', normalize-space(@class), ' '), ' nua_dash_parent_tabs ')]"
                + "//*[self::button or self::a or @role='tab']["
                + "@data-testid=" + xpathLiteralStatic("nua-tab-" + routeValue.replace("action=", "").replace("inv-codes", "inv-codes"))
                + " or " + dataTargetCondition + " or " + routeCondition + " or " + labelCondition + "][1]");
    }

    private static String xpathLiteralStatic(String value) {
        if (!value.contains("'")) {
            return "'" + value + "'";
        }
        if (!value.contains("\"")) {
            return "\"" + value + "\"";
        }
        return "concat('" + value.replace("'", "',\"'\",'") + "')";
    }
}
