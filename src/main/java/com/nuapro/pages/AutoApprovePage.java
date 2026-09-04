package com.nuapro.pages;

import com.nuapro.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class AutoApprovePage {

    private final WebDriver driver;
    private final WaitUtils waitUtils;

    private final By whitelistTab = By.xpath("//*[contains(concat(' ', normalize-space(@class), ' '), ' auto-approve-tab-list ')]//*[self::button or self::a or @role='tab'][contains(normalize-space(.), 'WhiteList') or contains(normalize-space(.), 'Whitelist')]");
    private final By blacklistTab = By.xpath("//*[contains(concat(' ', normalize-space(@class), ' '), ' auto-approve-tab-list ')]//*[self::button or self::a or @role='tab'][contains(normalize-space(.), 'BlackList') or contains(normalize-space(.), 'Blacklist')]");

    private final By whitelistToggleInput = By.id("isWhitelistEnabled");
    private final By whitelistToggleLabel = By.cssSelector("label.nua_switch[for='isWhitelistEnabled']");

    private final By blacklistToggleInput = By.id("isBlacklistEnabled");
    private final By blacklistToggleLabel = By.cssSelector("label.nua_switch[for='isBlacklistEnabled']");

    private final By whitelistTextarea = By.cssSelector("textarea#whitelist, textarea[name='whitelist']");
    private final By blacklistTextarea = By.cssSelector("textarea#blacklist, textarea[name='blacklist']");
    private final By customMessageInput = By.cssSelector("input[name='customMessage']");

    private final By saveButton = By.cssSelector("button.auto-approve-save-btn, button.save-changes");

    public AutoApprovePage(WebDriver driver) {
        this.driver = driver;
        this.waitUtils = new WaitUtils(driver);
    }

    public AutoApprovePage clickWhitelistTab() {
        waitUtils.clickElement(whitelistTab);
        waitUtils.waitForEnabledControl(whitelistToggleInput);
        return this;
    }

    public AutoApprovePage clickBlacklistTab() {
        waitUtils.clickElement(blacklistTab);
        waitUtils.waitForEnabledControl(blacklistToggleInput);
        return this;
    }

    public void setWhitelistToggle(boolean enable) {
        clickWhitelistTab();
        waitUtils.setCheckboxState(whitelistToggleInput, whitelistToggleLabel, enable);
    }

    public void setBlacklistToggle(boolean enable) {
        clickBlacklistTab();
        waitUtils.setCheckboxState(blacklistToggleInput, blacklistToggleLabel, enable);
    }

    public boolean isWhitelistToggleEnabled() {
        clickWhitelistTab();
        return waitUtils.isCheckboxSelected(whitelistToggleInput);
    }

    public boolean isBlacklistToggleEnabled() {
        clickBlacklistTab();
        return waitUtils.isCheckboxSelected(blacklistToggleInput);
    }

    public void updateWhitelist(String domains, boolean enableToggle) {
        clickWhitelistTab();
        setWhitelistToggle(enableToggle);
        waitUtils.sendKeys(whitelistTextarea, domains);
        waitUtils.clickElement(saveButton);
        waitUtils.waitForLoaderToDisappear();
    }

    public void updateWhitelist(String domains) {
        updateWhitelist(domains, true);
    }

    public void updateBlacklist(String domains, String customMsg, boolean enableToggle) {
        clickBlacklistTab();
        setBlacklistToggle(enableToggle);
        waitUtils.sendKeys(blacklistTextarea, domains);
        // null preserves the existing site message. An empty string explicitly
        // clears it so the plugin's default blacklist message is exercised.
        if (customMsg != null) {
            waitUtils.sendKeys(customMessageInput, customMsg);
        }
        waitUtils.clickElement(saveButton);
        waitUtils.waitForLoaderToDisappear();
    }

    public void updateBlacklist(String domains) {
        updateBlacklist(domains, null, true);
    }

    public String getWhitelistContent() {
        clickWhitelistTab();
        return waitUtils.waitForVisibility(whitelistTextarea).getAttribute("value");
    }

    public String getBlacklistContent() {
        clickBlacklistTab();
        return waitUtils.waitForVisibility(blacklistTextarea).getAttribute("value");
    }
}
