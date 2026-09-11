package com.nuapro.pages;

import com.nuapro.config.Config;
import com.nuapro.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import java.time.Duration;
import java.util.Locale;
import org.openqa.selenium.support.ui.WebDriverWait;

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
    private final By addCapabilityButton = By.xpath("//div[contains(@class, 'nua-re-sidebar-header')]//button[contains(normalize-space(.), 'Add Capability')]");
    private final By addCapabilityModal = By.xpath("//div[contains(@class, 'nua-re-modal-overlay')][.//h3[contains(normalize-space(.), 'Add Custom Capability')]]");
    private final By addCapabilityInput = By.xpath("//div[contains(@class, 'nua-re-modal-overlay')][.//h3[contains(normalize-space(.), 'Add Custom Capability')]]//input[contains(@class, 'auto-code-field')]");
    private final By addCapabilitySubmit = By.xpath("//div[contains(@class, 'nua-re-modal-overlay')][.//h3[contains(normalize-space(.), 'Add Custom Capability')]]//button[contains(@class, 'save-changes')]");

    public RoleEditorPage(WebDriver driver) {
        this.driver = driver;
        this.waitUtils = new WaitUtils(driver);
    }

    public void clickAddRole() {
        waitUtils.clickElement(addRoleBtn);
    }

    public RoleEditorPage openCapabilitiesTab() {
        driver.get(Config.getBaseUrl()
                + "/wp-admin/admin.php?page=new-user-approve-admin#/action=role-editor/tab=capabilities");
        waitUtils.waitForUrlContains("action=role-editor");
        waitUtils.waitForVisibility(By.cssSelector(".role-editor-main-tabpanel"));
        waitUtils.waitForVisibility(By.cssSelector(".nua-re-layout"));
        return this;
    }

    public RoleEditorPage openRolesTab() {
        driver.get(Config.getBaseUrl()
                + "/wp-admin/admin.php?page=new-user-approve-admin#/action=role-editor/tab=roles");
        waitUtils.waitForUrlContains("action=role-editor");
        waitUtils.waitForVisibility(By.cssSelector(".role-editor-main-tabpanel"));
        waitUtils.waitForVisibility(By.cssSelector(".nua-roles-list-page"));
        return this;
    }

    public void createCustomCapability(String capabilityName) {
        waitUtils.clickElement(addCapabilityButton);
        waitUtils.sendKeys(addCapabilityInput, capabilityName);
        waitUtils.clickElement(addCapabilitySubmit);
        waitUtils.waitForInvisibility(addCapabilityModal);
        waitUtils.waitForVisibility(customCapabilityLocator(capabilityName));
    }

    public boolean isCustomCapabilityPresent(String capabilityName) {
        try {
            return waitUtils.waitForVisibility(customCapabilityLocator(capabilityName)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public void editCustomCapability(String oldName, String newName) {
        WebElement editButton = hoverCustomCapabilityAction(oldName, "Edit");
        try {
            editButton.click();
        } catch (org.openqa.selenium.ElementClickInterceptedException intercepted) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", editButton);
        }
        By editModal = By.xpath("//div[contains(@class, 'nua-re-modal-overlay')][.//h3[contains(normalize-space(.), 'Edit Capability')]]");
        By editInput = By.xpath("//div[contains(@class, 'nua-re-modal-overlay')][.//h3[contains(normalize-space(.), 'Edit Capability')]]//input[contains(@class, 'auto-code-field')]");
        By editSubmit = By.xpath("//div[contains(@class, 'nua-re-modal-overlay')][.//h3[contains(normalize-space(.), 'Edit Capability')]]//button[contains(@class, 'save-changes')]");
        waitUtils.sendKeys(editInput, newName);
        waitUtils.clickElement(editSubmit);
        waitUtils.waitForInvisibility(editModal);
        waitUtils.waitForVisibility(customCapabilityLocator(newName));
    }

    public void deleteCustomCapability(String capabilityName) {
        WebElement deleteButton = hoverCustomCapabilityAction(capabilityName, "Delete");
        try {
            deleteButton.click();
        } catch (org.openqa.selenium.ElementClickInterceptedException intercepted) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", deleteButton);
        }
        try {
            driver.switchTo().alert().accept();
        } catch (org.openqa.selenium.NoAlertPresentException ignored) {
            // The UI may replace native confirmation with an application modal.
        }
        new WebDriverWait(driver, Duration.ofSeconds(10)).until(currentDriver ->
                currentDriver.findElements(customCapabilityLocator(capabilityName)).stream()
                        .noneMatch(element -> {
                            try {
                                return element.isDisplayed();
                            } catch (org.openqa.selenium.StaleElementReferenceException e) {
                                return false;
                            }
                        }));
    }

    private WebElement hoverCustomCapabilityAction(String capabilityName, String actionTitle) {
        By capabilityLocator = customCapabilityLocator(capabilityName);
        WebElement capability = waitUtils.waitForVisibility(capabilityLocator);
        new Actions(driver).moveToElement(capability).perform();

        By actionLocator = By.cssSelector("button[title='" + actionTitle + "']");
        return new WebDriverWait(driver, Duration.ofSeconds(5)).until(currentDriver -> {
            WebElement currentCapability = currentDriver.findElement(capabilityLocator);
            WebElement action = currentCapability.findElement(actionLocator);
            return action.isDisplayed() && action.isEnabled() ? action : null;
        });
    }

    public void selectRoleForCapabilities(String roleIdentifier) {
        WebElement searchInput = waitUtils.waitForVisibility(By.cssSelector(".nua-re-role-search-input"));
        String searchTerm = roleIdentifier.replace('_', ' ');
        ((JavascriptExecutor) driver).executeScript(
                "const input = arguments[0];"
                        + "const setter = Object.getOwnPropertyDescriptor(HTMLInputElement.prototype, 'value').set;"
                        + "setter.call(input, arguments[1]);"
                        + "input.dispatchEvent(new Event('input', {bubbles: true}));",
                searchInput, searchTerm);

        By roleButton = By.xpath("//button[contains(@class, 'nua-re-role-item')][.//*[contains("
                + "translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), "
                + xpathLiteral(searchTerm.toLowerCase(Locale.ROOT)) + ")]]");
        waitUtils.clickElement(roleButton);
        waitUtils.waitForVisibility(By.xpath(
                "//button[contains(@class, 'nua-re-role-item') and contains(@class, 'active')][.//*[contains("
                        + "translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), "
                        + xpathLiteral(searchTerm.toLowerCase(Locale.ROOT)) + ")]]"));
    }

    public void searchCapability(String capability) {
        WebElement searchInput = waitUtils.waitForVisibility(By.cssSelector(".nua-re-search-input"));
        ((JavascriptExecutor) driver).executeScript(
                "const input = arguments[0];"
                        + "const setter = Object.getOwnPropertyDescriptor(HTMLInputElement.prototype, 'value').set;"
                        + "setter.call(input, arguments[1]);"
                        + "input.dispatchEvent(new Event('input', {bubbles: true}));",
                searchInput, capability);
        waitUtils.waitForVisibility(capabilityCheckbox(capability));
    }

    public boolean isCapabilityGranted(String capability) {
        try {
            return waitUtils.waitForVisibility(capabilityCheckbox(capability)).isSelected();
        } catch (Exception e) {
            return false;
        }
    }

    public void setCapabilityGranted(String capability, boolean granted) {
        By checkbox = capabilityCheckbox(capability);
        WebElement element = waitUtils.waitForVisibility(checkbox);
        if (element.isSelected() != granted) {
            waitUtils.clickEnabledControl(element);
        }
        new WebDriverWait(driver, Duration.ofSeconds(5)).until(currentDriver ->
                currentDriver.findElement(checkbox).isSelected() == granted);
    }

    public void saveCapabilities() {
        waitUtils.clickElement(By.cssSelector("button.nua-re-toolbar-save"));
        waitUtils.waitForLoaderToDisappear();
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

    public void searchRoleForDeletion(String roleIdentifier) {
        searchRole(roleIdentifier);
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

    private By capabilityCheckbox(String capability) {
        return By.xpath(capabilityCheckboxXpath(capability));
    }

    private String capabilityCheckboxXpath(String capability) {
        return "//*[contains(concat(' ', normalize-space(@class), ' '), ' nua-re-cap-item ')][.//span[contains(@class, 'nua-re-cap-label') and "
                + "translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz')="
                + xpathLiteral(capability.toLowerCase(Locale.ROOT)) + "]]//input[@type='checkbox']";
    }

    private By customCapabilityLocator(String capabilityName) {
        return By.xpath("//*[contains(concat(' ', normalize-space(@class), ' '), ' nua-re-cap-custom ')][.//span[contains(@class, 'nua-re-cap-label') and "
                + "translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz')="
                + xpathLiteral(capabilityName.toLowerCase(Locale.ROOT)) + "]]");
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
