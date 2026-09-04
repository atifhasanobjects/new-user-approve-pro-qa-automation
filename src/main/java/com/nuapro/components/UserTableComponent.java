package com.nuapro.components;

import com.nuapro.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class UserTableComponent {

    private final WebDriver driver;
    private final WaitUtils waitUtils;

    private final By searchInput = By.cssSelector("input.nua-search-field, [data-testid='nua-user-search-input']");
    private final By rowsPerPageSelect = By.cssSelector(".nua-header-filters .MuiSelect-select");
    private final By bulkSelectAllCheckbox = By.cssSelector("th input.nua_checkbox, [data-testid='nua-bulk-select-all']");
    private final By bulkApproveButton = By.cssSelector("button.bulkApprove, [data-testid='nua-bulk-approve-btn']");
    private final By bulkDenyButton = By.cssSelector("button.bulkDeny, [data-testid='nua-bulk-deny-btn']");
    private final By bulkDeleteButton = By.cssSelector("button.bulkButton[color='error'], [data-testid='nua-bulk-delete-btn']");
    private final By paginationComponent = By.cssSelector(".nua-nav-pagination");
    private final By totalEntriesText = By.cssSelector(".nua-table-total-data");

    public UserTableComponent(WebDriver driver) {
        this.driver = driver;
        this.waitUtils = new WaitUtils(driver);
    }

    public void searchUser(String query) {
        waitUtils.sendKeys(searchInput, query);
        waitUtils.waitForLoaderToDisappear();
    }

    public By getRowLocatorByEmailOrUsername(String identifier) {
        String value = xpathLiteral(identifier);
        // All Users rows do not have an id attribute; status-specific tables do.
        // Scope the match to table rows so the identifier is not matched in
        // unrelated controls or the page header.
        return By.xpath("//tr[descendant::*[contains(normalize-space(.), " + value + ")] ]");
    }

    public boolean isUserRowPresent(String identifier) {
        try {
            return waitUtils.waitForVisibility(getRowLocatorByEmailOrUsername(identifier)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public String getUserStatus(String identifier) {
        By rowLocator = getRowLocatorByEmailOrUsername(identifier);
        WebElement row = waitUtils.waitForVisibility(rowLocator);
        WebElement statusSpan = row.findElement(By.cssSelector("span[class*='user-']"));
        return statusSpan.getText().trim();
    }

    public void approveUser(String identifier) {
        By rowLocator = getRowLocatorByEmailOrUsername(identifier);
        WebElement row = waitUtils.waitForVisibility(rowLocator);
        WebElement approveBtn = row.findElement(By.cssSelector("button[data-value='approve'], .approve"));
        approveBtn.click();
        waitUtils.waitForLoaderToDisappear();
    }

    public void denyUser(String identifier) {
        By rowLocator = getRowLocatorByEmailOrUsername(identifier);
        WebElement row = waitUtils.waitForVisibility(rowLocator);
        WebElement denyBtn = row.findElement(By.cssSelector("button[data-value='deny'], .deny"));
        denyBtn.click();
        waitUtils.waitForLoaderToDisappear();
    }

    public void openDeleteModal(String identifier) {
        By rowLocator = getRowLocatorByEmailOrUsername(identifier);
        WebElement row = waitUtils.waitForVisibility(rowLocator);
        WebElement deleteIcon = row.findElement(By.cssSelector(".NuaDeleteIcon, [data-testid='nua-delete-user']"));
        deleteIcon.click();
    }

    public void selectUserCheckbox(String identifier) {
        By rowLocator = getRowLocatorByEmailOrUsername(identifier);
        WebElement row = waitUtils.waitForVisibility(rowLocator);
        WebElement checkbox = row.findElement(By.cssSelector("input.nua_checkbox"));
        if (!checkbox.isSelected()) {
            waitUtils.clickEnabledControl(checkbox);
        }
    }

    public void selectAllCheckboxes() {
        waitUtils.clickEnabledControl(bulkSelectAllCheckbox);
    }

    public void clickBulkApprove() {
        waitUtils.clickElement(bulkApproveButton);
        waitUtils.waitForLoaderToDisappear();
    }

    public void clickBulkDeny() {
        waitUtils.clickElement(bulkDenyButton);
        waitUtils.waitForLoaderToDisappear();
    }

    public void clickBulkDelete() {
        waitUtils.clickElement(bulkDeleteButton);
    }

    public void selectRowsPerPage(int count) {
        waitUtils.clickElement(rowsPerPageSelect);
        By optionLocator = By.xpath("//li[@role='option' and (normalize-space(.)='" + count + "' or @data-value='" + count + "')]");
        waitUtils.clickElement(optionLocator);
        waitUtils.waitForLoaderToDisappear();
    }

    public boolean isPaginationVisible() {
        try {
            return waitUtils.waitForVisibility(paginationComponent).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public String getTotalEntriesText() {
        try {
            return waitUtils.waitForVisibility(totalEntriesText).getText();
        } catch (Exception e) {
            return "";
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
}
