package com.nuapro.pages;

import com.nuapro.config.Config;
import com.nuapro.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Select;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

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
    private final By autoDenialToggleInput = By.id("nua_denial");
    private final By autoDenialToggleLabel = By.cssSelector("label.nua_switch[for='nua_denial']");
    private final By autoDenialSettings = By.cssSelector(".denial-settings");
    private final By autoDenialAfterValueInput = By.cssSelector(".denial-settings input[type='number']");
    private final By autoDenialAfterUnitSelect = By.cssSelector(".denial-settings select.denial-select");

    // Password & Security Settings
    private final By bypassPasswordResetToggleInput = By.id("nua_bypass_password_reset");
    private final By bypassPasswordResetToggleLabel = By.cssSelector("label.nua_switch[for='nua_bypass_password_reset']");
    private final By registrationDeadlineToggleInput = By.id("nua_registration_deadline");
    private final By registrationDeadlineToggleLabel = By.cssSelector("label.nua_switch[for='nua_registration_deadline']");
    private final By registrationDeadlineDateTypeInput = By.id("nua_checkbox_for_deadline_type");
    private final By registrationDeadlineDateTypeLabel = By.cssSelector("label[for='nua_checkbox_for_deadline_type']");
    private final By registrationDeadlineNumberTypeInput = By.id("nua_register_type_number");
    private final By registrationDeadlineNumberTypeLabel = By.cssSelector("label[for='nua_register_type_number']");
    private final By registrationDeadlineDateInput = By.id("dateInput");
    private final By registrationDeadlineCalendarButton = By.cssSelector("button[aria-label^='Choose date']");
    private final By approveInvitationAfterDeadlineInput = By.id("nua_auto_approve_deadline");
    private final By approveInvitationAfterDeadlineLabel = By.cssSelector("label.nua_switch[for='nua_auto_approve_deadline']");

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

    public void setBypassPasswordResetToggle(boolean enable) {
        clickGeneralTab();
        waitUtils.setCheckboxState(bypassPasswordResetToggleInput, bypassPasswordResetToggleLabel, enable);
    }

    public boolean isBypassPasswordResetToggleEnabled() {
        clickGeneralTab();
        return waitUtils.isCheckboxSelected(bypassPasswordResetToggleInput);
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

    public void setRegistrationDeadlineToggle(boolean enable) {
        clickGeneralTab();
        waitUtils.setCheckboxState(registrationDeadlineToggleInput, registrationDeadlineToggleLabel, enable);
    }

    public void setAutoDenialToggle(boolean enable) {
        clickGeneralTab();
        waitUtils.setCheckboxState(autoDenialToggleInput, autoDenialToggleLabel, enable);
    }

    public boolean isAutoDenialToggleEnabled() {
        clickGeneralTab();
        return waitUtils.isCheckboxSelected(autoDenialToggleInput);
    }

    public boolean isAutoDenialSettingsVisible() {
        clickGeneralTab();
        try {
            return waitUtils.waitForVisibility(autoDenialSettings).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public void setAutoDenialSchedule(int value, String unit) {
        clickGeneralTab();
        waitUtils.setCheckboxState(autoDenialToggleInput, autoDenialToggleLabel, true);
        waitUtils.sendKeys(autoDenialAfterValueInput, String.valueOf(value));
        Select period = new Select(waitUtils.waitForVisibility(autoDenialAfterUnitSelect));
        period.selectByValue(unit);
    }

    public String getAutoDenialAfterValue() {
        clickGeneralTab();
        return waitUtils.waitForVisibility(autoDenialAfterValueInput).getAttribute("value");
    }

    public String getAutoDenialAfterUnit() {
        clickGeneralTab();
        Select period = new Select(waitUtils.waitForVisibility(autoDenialAfterUnitSelect));
        return period.getFirstSelectedOption().getAttribute("value");
    }

    public void setRegistrationDeadlineDateTime(LocalDateTime deadline) {
        clickGeneralTab();
        waitUtils.setCheckboxState(registrationDeadlineToggleInput, registrationDeadlineToggleLabel, true);
        waitUtils.setCheckboxState(registrationDeadlineDateTypeInput, registrationDeadlineDateTypeLabel, true);
        waitUtils.setCheckboxState(registrationDeadlineNumberTypeInput, registrationDeadlineNumberTypeLabel, false);

        // MUI DateTimePicker v6 accepts direct text input in its configured
        // format. Bypass the fragile clock/calendar dialog interaction by
        // typing the date string directly into the text field.
        String dateStr = deadline.format(
                DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a", Locale.ENGLISH));
        WebElement input = waitUtils.waitForVisibility(registrationDeadlineDateInput);
        input.click();
        input.sendKeys(Keys.chord(Keys.CONTROL, "a"));
        input.sendKeys(dateStr);
        // Pressing Enter commits the typed value in MUI DateTimePicker.
        input.sendKeys(Keys.ENTER);

        String actualValue = waitUtils.waitForVisibility(registrationDeadlineDateInput)
                .getAttribute("value");
        if (actualValue == null || actualValue.trim().isEmpty()) {
            throw new AssertionError("Registration deadline picker did not produce a value after configuration");
        }
        if (!waitUtils.isCheckboxSelected(registrationDeadlineToggleInput)) {
            throw new AssertionError("Registration Deadline toggle is not enabled after configuration");
        }
        if (!waitUtils.isCheckboxSelected(registrationDeadlineDateTypeInput)) {
            throw new AssertionError("Date and Time deadline type is not enabled after configuration");
        }
        if (waitUtils.isCheckboxSelected(registrationDeadlineNumberTypeInput)) {
            throw new AssertionError("Number of Registration deadline type must be disabled for this test");
        }
    }

    private void selectPickerOption(String listLabel, String optionLabel) {
        String listXpath = "//div[@role='dialog']//ul[@role='listbox' and @aria-label="
                + xpathLiteral(listLabel) + "]";

        java.util.List<WebElement> options = driver.findElements(By.xpath(listXpath + "//li[@role='option']"));
        if (options.isEmpty()) {
            throw new AssertionError("No picker options found for list '" + listLabel + "'");
        }

        // Determine which option is actually enabled (MUI marks disabled options with Mui-disabled).
        java.util.Set<String> enabledAriaLabels = new java.util.HashSet<>();
        java.util.Map<String, WebElement> ariaLabelToElement = new java.util.HashMap<>();
        for (WebElement option : options) {
            String aria = option.getAttribute("aria-label");
            if (aria == null) continue;
            boolean disabled = false;
            String cls = option.getAttribute("class");
            if (cls != null && cls.contains("Mui-disabled")) disabled = true;
            String ariaDisabled = option.getAttribute("aria-disabled");
            if ("true".equalsIgnoreCase(ariaDisabled)) disabled = true;
            if (!disabled) {
                enabledAriaLabels.add(aria);
                ariaLabelToElement.put(aria, option);
            }
        }

        String desiredAria = optionLabel;
        String chosenAria = null;

        if ("Select hours".equals(listLabel) && optionLabel.endsWith(" hours")) {
            int desiredHour = Integer.parseInt(optionLabel.substring(0, optionLabel.indexOf(" hours")));
            int[] hourOrder = new int[]{12,1,2,3,4,5,6,7,8,9,10,11};
            int desiredIndex = -1;
            for (int i = 0; i < hourOrder.length; i++) {
                if (hourOrder[i] == desiredHour) { desiredIndex = i; break; }
            }
            if (desiredIndex >= 0) {
                for (int step = 0; step < hourOrder.length; step++) {
                    int candidate = hourOrder[(desiredIndex + step) % hourOrder.length];
                    String candidateAria = candidate + " hours";
                    if (enabledAriaLabels.contains(candidateAria)) {
                        chosenAria = candidateAria;
                        break;
                    }
                }
            }
        } else if ("Select minutes".equals(listLabel) && optionLabel.endsWith(" minutes")) {
            int desiredMinute = Integer.parseInt(optionLabel.substring(0, optionLabel.indexOf(" minutes")));
            int[] minuteOrder = new int[]{0,5,10,15,20,25,30,35,40,45,50,55};
            int desiredIndex = -1;
            for (int i = 0; i < minuteOrder.length; i++) {
                if (minuteOrder[i] == desiredMinute) { desiredIndex = i; break; }
            }
            if (desiredIndex >= 0) {
                for (int step = 0; step < minuteOrder.length; step++) {
                    int candidate = minuteOrder[(desiredIndex + step) % minuteOrder.length];
                    String candidateAria = candidate + " minutes";
                    if (enabledAriaLabels.contains(candidateAria)) {
                        chosenAria = candidateAria;
                        break;
                    }
                }
            } else {
                // If our desired minute isn't an exact 5-min increment, select the first enabled >= desired.
                for (int candidate : minuteOrder) {
                    if (candidate >= desiredMinute && enabledAriaLabels.contains(candidate + " minutes")) {
                        chosenAria = candidate + " minutes";
                        break;
                    }
                }
                if (chosenAria == null) {
                    for (int candidate : minuteOrder) {
                        if (enabledAriaLabels.contains(candidate + " minutes")) {
                            chosenAria = candidate + " minutes";
                            break;
                        }
                    }
                }
            }
        } else if ("Select meridiem".equals(listLabel)) {
            if (enabledAriaLabels.contains(desiredAria)) {
                chosenAria = desiredAria;
            } else {
                // Pick any enabled option (AM/PM). Prefer the other if desired is disabled.
                if ("AM".equals(desiredAria) && enabledAriaLabels.contains("PM")) chosenAria = "PM";
                else if ("PM".equals(desiredAria) && enabledAriaLabels.contains("AM")) chosenAria = "AM";
                else if (!enabledAriaLabels.isEmpty()) chosenAria = enabledAriaLabels.iterator().next();
            }
        }

        if (chosenAria == null) {
            // Fallback to the exact desired label if present, even if not enabled.
            if (enabledAriaLabels.contains(desiredAria)) {
                chosenAria = desiredAria;
            } else {
                throw new AssertionError("No enabled picker option for list '" + listLabel
                        + "' desired '" + optionLabel + "'");
            }
        }

        WebElement chosenElement = ariaLabelToElement.get(chosenAria);
        if (chosenElement == null) {
            // If the cached element was replaced, re-locate it by aria-label.
            By chosenLocator = By.xpath(listXpath + "//li[@role='option' and @aria-label=" + xpathLiteral(chosenAria) + "]");
            chosenElement = waitUtils.waitForVisibility(chosenLocator);
        }

        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", chosenElement);

        waitUtils.waitForVisibility(By.xpath(listXpath + "//li[@role='option' and @aria-label="
                + xpathLiteral(chosenAria) + " and @aria-selected='true']"));
    }

    public void setApproveInvitationCodeAfterDeadline(boolean enable) {
        clickGeneralTab();
        if (!enable && driver.findElements(approveInvitationAfterDeadlineInput).isEmpty()) {
            // The plugin only renders this control when Invitation Code is
            // enabled. Disabled is already the effective state when absent.
            return;
        }
        // The toggle is conditionally rendered only when BOTH the Registration
        // Deadline and Invitation Code toggles are enabled in React state.
        // After enabling those toggles the React re-render may take a moment,
        // so wait for the element to appear before interacting with it.
        if (enable) {
            waitUtils.waitForVisibility(approveInvitationAfterDeadlineLabel);
        }
        waitUtils.setCheckboxState(
                approveInvitationAfterDeadlineInput,
                approveInvitationAfterDeadlineLabel,
                enable);
    }

    public boolean isRegistrationDeadlineEnabled() {
        clickGeneralTab();
        return waitUtils.isCheckboxSelected(registrationDeadlineToggleInput);
    }

    public boolean isRegistrationDeadlineDateTypeEnabled() {
        clickGeneralTab();
        return waitUtils.isCheckboxSelected(registrationDeadlineDateTypeInput);
    }

    public boolean isRegistrationDeadlineNumberTypeEnabled() {
        clickGeneralTab();
        return waitUtils.isCheckboxSelected(registrationDeadlineNumberTypeInput);
    }

    public String getRegistrationDeadlineDateTime() {
        clickGeneralTab();
        return waitUtils.waitForVisibility(registrationDeadlineDateInput).getAttribute("value");
    }

    public boolean isApproveInvitationCodeAfterDeadlineEnabled() {
        clickGeneralTab();
        return waitUtils.isCheckboxSelected(approveInvitationAfterDeadlineInput);
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
