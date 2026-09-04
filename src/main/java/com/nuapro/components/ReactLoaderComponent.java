package com.nuapro.components;

import com.nuapro.utils.WaitUtils;
import org.openqa.selenium.WebDriver;

public class ReactLoaderComponent {

    private final WaitUtils waitUtils;

    public ReactLoaderComponent(WebDriver driver) {
        this.waitUtils = new WaitUtils(driver);
    }

    public void waitUntilDisappeared() {
        waitUtils.waitForLoaderToDisappear();
    }
}
