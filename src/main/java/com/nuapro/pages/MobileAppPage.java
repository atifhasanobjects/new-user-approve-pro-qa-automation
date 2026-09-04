package com.nuapro.pages;

import com.nuapro.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class MobileAppPage {

    private final WebDriver driver;
    private final WaitUtils waitUtils;

    private final By mobileAppContainer = By.cssSelector(".mobileapp-main-tabpanel, .mobile-app-wrapper");

    public MobileAppPage(WebDriver driver) {
        this.driver = driver;
        this.waitUtils = new WaitUtils(driver);
    }

    public boolean isLoaded() {
        try {
            return waitUtils.waitForVisibility(mobileAppContainer).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}
