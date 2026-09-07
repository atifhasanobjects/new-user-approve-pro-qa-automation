package com.nuapro.pages;

import com.nuapro.config.Config;
import com.nuapro.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class ThemeMyLoginRegistrationPage {

    private final WebDriver driver;
    private final WaitUtils waitUtils;

    private final By usernameInput = By.id("user_login");
    private final By emailInput = By.id("user_email");
    private final By passwordInput = By.xpath("//*[@id='pass1' or @name='user_pass1']");
    private final By confirmPasswordInput = By.xpath("//*[@id='pass2' or @name='user_pass2']");
    private final By submitButton = By.cssSelector("button.tml-button[type='submit']");

    // Theme My Login renders alerts for registration errors inside .tml-alerts.
    private final By errorAlert = By.cssSelector(".tml-alerts .tml-alert, .tml-alerts .tml-alert-error, .tml-alerts .tml-alert-warning");

    public ThemeMyLoginRegistrationPage(WebDriver driver) {
        this.driver = driver;
        this.waitUtils = new WaitUtils(driver);
    }

    public ThemeMyLoginRegistrationPage open() {
        driver.get(Config.getBaseUrl() + "/register/");
        return this;
    }

    public boolean isRegistrationFormAvailable() {
        try {
            return waitUtils.waitForVisibility(usernameInput).isDisplayed()
                    && waitUtils.waitForVisibility(emailInput).isDisplayed()
                    && waitUtils.waitForVisibility(passwordInput).isDisplayed()
                    && waitUtils.waitForVisibility(submitButton).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public ThemeMyLoginRegistrationPage registerUser(String username, String email, String password) {
        open();
        if (!isRegistrationFormAvailable()) {
            throw new AssertionError("Theme My Login registration form is unavailable at " + driver.getCurrentUrl());
        }
        waitUtils.sendKeys(usernameInput, username);
        waitUtils.sendKeys(emailInput, email);
        waitUtils.sendKeys(passwordInput, password);

        // Theme My Login may keep the confirm-password input hidden until
        // after interaction. Populate it via JS so the form still submits.
        try {
            org.openqa.selenium.WebElement confirm = driver.findElement(confirmPasswordInput);
            if (confirm.isDisplayed()) {
                confirm.clear();
                confirm.sendKeys(password);
            } else {
                ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                        "arguments[0].value = arguments[1];"
                                + "arguments[0].dispatchEvent(new Event('input', {bubbles: true}));"
                                + "arguments[0].dispatchEvent(new Event('change', {bubbles: true}));",
                        confirm, password);
            }
        } catch (Exception ignored) {
            // If the confirm field is not present, Theme My Login may still
            // accept registration; validation will be caught on error alerts.
        }

        waitUtils.clickElement(submitButton);
        return this;
    }

    public boolean isRegistrationErrorDisplayed() {
        try {
            return waitUtils.waitForVisibility(errorAlert).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public String getRegistrationErrorText() {
        try {
            return waitUtils.waitForVisibility(errorAlert).getText();
        } catch (Exception e) {
            return "";
        }
    }
}

