package com.nuapro.tests;

import com.nuapro.base.BaseTest;
import com.nuapro.pages.LoginPage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class AdminAuthenticationTest extends BaseTest {

    @Test(groups = {"auth"}, description = "TC001: Valid credentials login success")
    public void testValidLogin() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.loginAsAdmin();
        Assert.assertTrue(driver.getCurrentUrl().contains("wp-admin"), "User should navigate to wp-admin");
    }

    @Test(groups = {"auth"}, description = "Invalid credentials show error")
    public void testInvalidLogin() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.open();
        loginPage.login("invalid_user_qa", "invalid_pass_qa");
        Assert.assertTrue(loginPage.isErrorMessageDisplayed(), "Login error message should be displayed for invalid credentials");
    }
}
