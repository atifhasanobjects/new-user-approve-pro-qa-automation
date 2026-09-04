package com.nuapro.pages;

import com.nuapro.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class IntegrationsPage {

    private final WebDriver driver;
    private final WaitUtils waitUtils;

    private final By integrationsContainer = By.cssSelector(".integration-main-tabpanel, .integrations-wrapper");

    public IntegrationsPage(WebDriver driver) {
        this.driver = driver;
        this.waitUtils = new WaitUtils(driver);
    }

    public boolean isLoaded() {
        try {
            return waitUtils.waitForVisibility(integrationsContainer).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}
