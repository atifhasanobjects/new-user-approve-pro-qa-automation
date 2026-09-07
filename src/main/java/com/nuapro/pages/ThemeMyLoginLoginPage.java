package com.nuapro.pages;

import com.nuapro.config.Config;
import com.nuapro.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class ThemeMyLoginLoginPage {

    private final WebDriver driver;
    private final WaitUtils waitUtils;

    private final By usernameInput = By.id("user_login");
    private final By passwordInput = By.id("user_pass");
    private final By submitButton = By.cssSelector("button.tml-button[type='submit']");
    private final By errorAlert = By.cssSelector(".tml-alerts .tml-alert, .tml-alerts .tml-alert-error, .tml-alerts .tml-alert-warning");

    public ThemeMyLoginLoginPage(WebDriver driver) {
        this.driver = driver;
        this.waitUtils = new WaitUtils(driver);
    }

    public ThemeMyLoginLoginPage open() {
        driver.get(Config.getBaseUrl() + "/login/");
        return this;
    }

    public ThemeMyLoginLoginPage login(String username, String password) {
        open();
        waitUtils.waitForVisibility(usernameInput);
        waitUtils.sendKeys(usernameInput, username);
        waitUtils.sendKeys(passwordInput, password);
        waitUtils.clickElement(submitButton);
        return this;
    }

    public boolean isErrorMessageDisplayed() {
        try {
            return waitUtils.waitForVisibility(errorAlert).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public String getErrorText() {
        try {
            return waitUtils.waitForVisibility(errorAlert).getText();
        } catch (Exception e) {
            return "";
        }
    }
}
