package com.nuapro.pages;

import com.nuapro.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.Locale;

public class RoleEditorPage {

    private final WebDriver driver;
    private final WaitUtils waitUtils;

    private final By addRoleBtn = By.cssSelector(".nua-roles-list-page button.nua-btn.save-changes, [data-testid='nua-add-role-btn']");
    private final By roleSearchInput = By.cssSelector(".nua-roles-list-page input.nua-search-field");
    private final By addRoleDialog = By.xpath("//div[contains(concat(' ', normalize-space(@class), ' '), ' MuiDialog-root ')][.//h2[contains(normalize-space(.), 'Add New Role')]]");
    private final By roleNameInput = By.xpath("//div[contains(concat(' ', normalize-space(@class), ' '), ' MuiDialog-root ')][.//h2[contains(normalize-space(.), 'Add New Role')]]//div[contains(@class, 'nua-re-form-group')][.//label[contains(normalize-space(.), 'Display Name')]]//input");
    private final By roleDisplayInput = By.xpath("//div[contains(concat(' ', normalize-space(@class), ' '), ' MuiDialog-root ')][.//h2[contains(normalize-space(.), 'Add New Role')]]//div[contains(@class, 'nua-re-form-group')][.//label[contains(normalize-space(.), 'Role Slug')]]//input");
    private final By saveRoleBtn = By.xpath("//div[contains(concat(' ', normalize-space(@class), ' '), ' MuiDialog-root ')][.//h2[contains(normalize-space(.), 'Add New Role')]]//button[contains(normalize-space(.), 'Create Role')]");

    private final By roleColumnHeader = By.cssSelector(".nua-roles-table-container .nua-sort-header");
    private final By usersColumnHeader = By.xpath("(//*[contains(@class, 'nua-roles-table-container')]//*[contains(@class, 'nua-sort-header')])[2]");
    private final By capsColumnHeader = By.xpath("(//*[contains(@class, 'nua-roles-table-container')]//*[contains(@class, 'nua-sort-header')])[3]");

    public RoleEditorPage(WebDriver driver) {
        this.driver = driver;
        this.waitUtils = new WaitUtils(driver);
    }

    public void clickAddRole() {
        waitUtils.clickElement(addRoleBtn);
    }

    public void createRole(String roleName, String displayName) {
        clickAddRole();
        // The plugin creates the role with a display name and a separate
        // sanitized slug. Keep the page-object arguments aligned with that
        // API contract so callers can delete the role by its slug.
        waitUtils.sendKeys(roleNameInput, displayName);
        waitUtils.sendKeys(roleDisplayInput, roleName);
        waitUtils.clickElement(saveRoleBtn);
        // Role creation is an async REST request and does not render the
        // generic loader. The dialog closes only after the request succeeds.
        waitUtils.waitForInvisibility(addRoleDialog);
        // The plugin refreshes the roles list asynchronously after the create
        // request. Reload once so the following lookup cannot be overwritten
        // by the pre-create list response.
        driver.navigate().refresh();
        searchRole(roleName);
        waitUtils.waitForVisibility(roleRowLocator(roleName));
    }

    public boolean isRolePresent(String roleName) {
        try {
            searchRole(roleName);
            return waitUtils.waitForVisibility(roleRowLocator(roleName)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public void deleteRole(String roleName) {
        WebElement row = waitUtils.waitForVisibility(roleRowLocator(roleName));
        row.findElement(By.cssSelector("button[title='Delete'], .delete-role-icon, .NuaDeleteIcon")).click();
        waitUtils.waitForVisibility(By.cssSelector(".MuiDialog-root.openNuaModal .delete-confirmation"));
        waitUtils.clickElement(By.cssSelector(".MuiDialog-root.openNuaModal .importBtn"));
        waitUtils.waitForInvisibility(By.cssSelector(".MuiDialog-root.openNuaModal"));
    }

    public void sortRoleColumn() {
        waitUtils.clickElement(roleColumnHeader);
    }

    public void sortUsersColumn() {
        waitUtils.clickElement(usersColumnHeader);
    }

    public void sortCapabilitiesColumn() {
        waitUtils.clickElement(capsColumnHeader);
    }

    private By roleRowLocator(String roleIdentifier) {
        String normalizedIdentifier = roleIdentifier.replace('_', ' ').toLowerCase(Locale.ROOT);
        String lowerCaseText = "translate(normalize-space(.), "
                + xpathLiteral("ABCDEFGHIJKLMNOPQRSTUVWXYZ") + ", "
                + xpathLiteral("abcdefghijklmnopqrstuvwxyz") + ")";
        return By.xpath("//tr[contains(@class, 'nua-roles-table-row')][descendant::*[contains("
                + lowerCaseText + ", " + xpathLiteral(normalizedIdentifier) + ")]]");
    }

    private void searchRole(String roleIdentifier) {
        WebElement searchInput = waitUtils.waitForVisibility(roleSearchInput);
        String searchTerm = roleIdentifier.replace('_', ' ');
        // The plugin fetches roles on every keypress. Rapid Selenium typing can
        // let an older, shorter-query response overwrite the final result.
        // Set the controlled React input once and dispatch one input event.
        ((JavascriptExecutor) driver).executeScript(
                "const input = arguments[0];"
                        + "const setter = Object.getOwnPropertyDescriptor(HTMLInputElement.prototype, 'value').set;"
                        + "setter.call(input, arguments[1]);"
                        + "input.dispatchEvent(new Event('input', { bubbles: true }));",
                searchInput, searchTerm);
        waitUtils.waitForLoaderToDisappear();
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
}
