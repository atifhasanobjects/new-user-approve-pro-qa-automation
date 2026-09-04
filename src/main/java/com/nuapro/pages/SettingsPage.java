package com.nuapro.pages;

import com.nuapro.config.Config;
import com.nuapro.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class SettingsPage {

    private final WebDriver driver;
    private final WaitUtils waitUtils;

    private final By generalTab = By.xpath("//button[contains(normalize-space(.),'General Settings') or contains(normalize-space(.),'General') or @value='tab=general'] | //a[contains(@href,'tab=general')]");
    private final By registrationTab = By.xpath("//button[contains(normalize-space(.),'Registration Settings') or contains(normalize-space(.),'Registration') or @value='tab=registration'] | //a[contains(@href,'tab=registration')]");
    private final By notificationTab = By.xpath("//button[normalize-space(.)='Notification' or @value='action=notification-settings'] | //a[contains(@href,'notification')]");
    private final By adminNotifTab = By.xpath("//button[contains(normalize-space(.),'Admin Notification') or @value='tab=admin_notification' or @value='tab=admin']");
    private final By userNotifTab = By.xpath("//button[contains(normalize-space(.),'User Notification') or @value='tab=user_notification' or @value='tab=user']");
    private final By helpTab = By.xpath("//button[contains(normalize-space(.),'Help') or @value='tab=help'] | //a[contains(@href,'tab=help')]");

    // General Toggles
    private final By autoApproveToggleInput = By.id("nua_enable_auto_approve");
    private final By autoApproveToggleLabel = By.cssSelector("label.nua_switch[for='nua_enable_auto_approve']");

    private final By userRoleRequestToggleInput = By.id("nua_enable_user_role_request");
    private final By userRoleRequestToggleLabel = By.cssSelector("label.nua_switch[for='nua_enable_user_role_request']");
    private final By autoApprovalRolesCombobox = By.xpath("//*[@id='roles_chooser']//*[@role='combobox'] | //*[@id='roles_chooser']//input");

    private final By inviteCodeToggleInput = By.id("nua_enable_invitation_code");
    private final By inviteCodeToggleLabel = By.cssSelector("label.nua_switch[for='nua_enable_invitation_code']");

    private final By inviteCodeRequiredInput = By.id("nua_make_invitation_code_required");
    private final By inviteCodeRequiredLabel = By.cssSelector("label.nua_switch[for='nua_make_invitation_code_required']");

    private final By adminEmailInput = By.id("nua_admin_email_address");

    private final By saveSettingsBtn = By.cssSelector("button.save-changes, .setting-save-btn button");

    public SettingsPage(WebDriver driver) {
        this.driver = driver;
        this.waitUtils = new WaitUtils(driver);
    }

    public SettingsPage clickGeneralTab() {
        // The plugin's nested React tabs do not consistently preserve the
        // parent hash route when clicked. The General controls are reliably
        // mounted on this canonical route, so navigate there deterministically.
        String generalSettingsUrl = Config.getBaseUrl()
                + "/wp-admin/admin.php?page=new-user-approve-admin#/action=settings/tab=general";
        if (!driver.getCurrentUrl().contains("action=settings/tab=general")) {
            driver.get(generalSettingsUrl);
        }
        return this;
    }

    public SettingsPage clickRegistrationTab() {
        waitUtils.clickElement(registrationTab);
        waitUtils.waitForLoaderToDisappear();
        return this;
    }

    public SettingsPage clickAdminNotifTab() {
        if (!driver.getPageSource().contains("Admin Notification")) {
            waitUtils.clickElement(notificationTab);
        }
        waitUtils.clickElement(adminNotifTab);
        waitUtils.waitForLoaderToDisappear();
        return this;
    }

    public SettingsPage clickUserNotifTab() {
        if (!driver.getPageSource().contains("User Notification")) {
            waitUtils.clickElement(notificationTab);
        }
        waitUtils.clickElement(userNotifTab);
        waitUtils.waitForLoaderToDisappear();
        return this;
    }

    public SettingsPage clickHelpTab() {
        waitUtils.clickElement(helpTab);
        waitUtils.waitForLoaderToDisappear();
        return this;
    }

    public SettingsPage waitForPanel(String cssSelector) {
        waitUtils.waitForVisibility(By.cssSelector(cssSelector));
        return this;
    }

    public boolean isPanelVisible(String cssSelector) {
        try {
            return waitUtils.waitForVisibility(By.cssSelector(cssSelector)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public void setEnableAutoApproveToggle(boolean enable) {
        clickGeneralTab();
        waitUtils.setCheckboxState(autoApproveToggleInput, autoApproveToggleLabel, enable);
    }

    public void setEnableInvitationCodeToggle(boolean enable) {
        clickGeneralTab();
        waitUtils.setCheckboxState(inviteCodeToggleInput, inviteCodeToggleLabel, enable);
    }

    public void setInvitationCodeRequiredToggle(boolean enable) {
        clickGeneralTab();
        waitUtils.setCheckboxState(inviteCodeRequiredInput, inviteCodeRequiredLabel, enable);
    }

    public boolean isInvitationCodeToggleEnabled() {
        clickGeneralTab();
        return waitUtils.isCheckboxSelected(inviteCodeToggleInput);
    }

    public boolean isInvitationCodeRequiredToggleEnabled() {
        clickGeneralTab();
        return waitUtils.isCheckboxSelected(inviteCodeRequiredInput);
    }

    public boolean isAutoApproveToggleEnabled() {
        clickGeneralTab();
        return waitUtils.isCheckboxSelected(autoApproveToggleInput);
    }

    public void setUserRoleRequestToggle(boolean enable) {
        clickGeneralTab();
        waitUtils.setCheckboxState(userRoleRequestToggleInput, userRoleRequestToggleLabel, enable);
    }

    public boolean isUserRoleRequestToggleEnabled() {
        clickGeneralTab();
        return waitUtils.isCheckboxSelected(userRoleRequestToggleInput);
    }

    public SettingsPage selectAutoApprovalRole(String roleLabel) {
        clickGeneralTab();
        waitUtils.clickElement(autoApprovalRolesCombobox);
        By option = By.xpath("//*[@role='option' and normalize-space(.)=" + xpathLiteral(roleLabel) + "]");
        waitUtils.clickElement(option);
        return this;
    }

    public SettingsPage clearAutoApprovalRoles() {
        clickGeneralTab();
        By removeRole = By.cssSelector("#roles_chooser [class*='multi-value__remove']");
        while (!driver.findElements(removeRole).isEmpty()) {
            waitUtils.clickElement(removeRole);
        }
        return this;
    }

    public boolean isAutoApprovalRoleSelected(String roleLabel) {
        clickGeneralTab();
        By selectedRole = By.xpath("//*[@id='roles_chooser']//*[normalize-space(.)="
                + xpathLiteral(roleLabel) + "]");
        try {
            return waitUtils.waitForVisibility(selectedRole).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public void setAdminEmailAddress(String email) {
        clickGeneralTab();
        waitUtils.sendKeys(adminEmailInput, email);
    }

    public void saveSettings() {
        waitUtils.clickElement(saveSettingsBtn);
        // Toast markup/timing differs between React Toastify builds. Persistence
        // is verified by the caller after refresh, which is the authoritative
        // result; use the loader only as a short synchronization aid here.
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
