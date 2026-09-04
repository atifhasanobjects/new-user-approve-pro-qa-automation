package com.nuapro.tests;

import com.nuapro.base.BaseTest;
import com.nuapro.pages.RegistrationPage;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Independent, non-mutating check for the native WordPress registration page.
 * It runs even when dashboard configuration tests fail first.
 */
public class RegistrationPageTest extends BaseTest {

    @Test(groups = {"smoke", "registration"}, description = "TC060: WordPress registration form is available")
    public void testWordPressRegistrationFormIsAvailable() {
        RegistrationPage registrationPage = new RegistrationPage(driver).open();

        Assert.assertTrue(driver.getCurrentUrl().contains("wp-login.php?action=register"),
                "The browser should navigate to the WordPress registration URL");
        Assert.assertTrue(registrationPage.isRegistrationFormAvailable(),
                "Registration form must expose username, email, and Register controls. "
                        + "Verify WordPress Settings > General > Membership > Anyone can register.");
    }
}
