package com.nuapro.pages;

import com.nuapro.config.Config;
import com.nuapro.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class RegistrationPage {

    private final WebDriver driver;
    private final WaitUtils waitUtils;

    private final By usernameInput = By.id("user_login");
    private final By emailInput = By.id("user_email");
    private final By invitationCodeInput = By.cssSelector("input[name='nua_invitation_code'], input[name='invitation_code'], #invitation_code");
    private final By submitButton = By.id("wp-submit");
    private final By successMessage = By.cssSelector(".message, .register-success, .nua-success-msg");
    private final By errorMessage = By.cssSelector("#login_error, .error, .nua-error-msg");

    public RegistrationPage(WebDriver driver) {
        this.driver = driver;
        this.waitUtils = new WaitUtils(driver);
    }

    public RegistrationPage open() {
        driver.get(Config.getBaseUrl() + "/wp-login.php?action=register");
        return this;
    }

    public boolean isRegistrationFormAvailable() {
        try {
            return waitUtils.waitForVisibility(usernameInput).isDisplayed()
                    && waitUtils.waitForVisibility(emailInput).isDisplayed()
                    && waitUtils.waitForClickable(submitButton).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public RegistrationPage waitUntilRegistrationFormAvailable() {
        if (!isRegistrationFormAvailable()) {
            throw new AssertionError("WordPress registration form is unavailable at " + driver.getCurrentUrl()
                    + ". Verify WordPress Settings > General > Membership > Anyone can register.");
        }
        return this;
    }

    public void registerUser(String username, String email) {
        open();
        waitUntilRegistrationFormAvailable();
        waitUtils.sendKeys(usernameInput, username);
        waitUtils.sendKeys(emailInput, email);
        waitUtils.clickElement(submitButton);
    }

    public void registerUserWithInvitationCode(String username, String email, String invitationCode) {
        open();
        waitUntilRegistrationFormAvailable();
        waitUtils.sendKeys(usernameInput, username);
        waitUtils.sendKeys(emailInput, email);
        // NUA renders this field as name=nua_invitation_code on the native
        // WordPress registration form. Do not suppress a missing field: a
        // functional invitation-code test would otherwise pass without ever
        // submitting the code.
        waitUtils.sendKeys(invitationCodeInput, invitationCode);
        waitUtils.clickElement(submitButton);
    }

    public boolean isRegistrationSuccessful() {
        try {
            return waitUtils.waitForVisibility(successMessage).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isRegistrationFailed() {
        try {
            return waitUtils.waitForVisibility(errorMessage).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public String getRegistrationErrorText() {
        return waitUtils.waitForVisibility(errorMessage).getText();
    }
}
