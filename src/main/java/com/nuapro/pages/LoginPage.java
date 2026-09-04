package com.nuapro.pages;

import com.nuapro.config.Config;
import com.nuapro.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class LoginPage {

    private final WebDriver driver;
    private final WaitUtils waitUtils;

    private final By usernameInput = By.id("user_login");
    private final By passwordInput = By.id("user_pass");
    private final By submitButton = By.id("wp-submit");
    private final By loginError = By.id("login_error");

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.waitUtils = new WaitUtils(driver);
    }

    public LoginPage open() {
        driver.get(Config.getBaseUrl() + "/wp-login.php");
        return this;
    }

    public DashboardPage login(String username, String password) {
        waitUtils.sendKeys(usernameInput, username);
        waitUtils.sendKeys(passwordInput, password);
        waitUtils.clickElement(submitButton);
        return new DashboardPage(driver);
    }

    public DashboardPage loginAsAdmin() {
        open();
        login(Config.getAdminUser(), Config.getAdminPass());
        DashboardPage dashboardPage = new DashboardPage(driver);
        dashboardPage.open();
        dashboardPage.waitUntilLoaded();
        return dashboardPage;
    }

    public boolean isErrorMessageDisplayed() {
        try {
            return waitUtils.waitForVisibility(loginError).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}
