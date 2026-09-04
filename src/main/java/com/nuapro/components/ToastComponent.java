package com.nuapro.components;

import com.nuapro.utils.WaitUtils;
import org.openqa.selenium.WebDriver;

public class ToastComponent {

    private final WaitUtils waitUtils;

    public ToastComponent(WebDriver driver) {
        this.waitUtils = new WaitUtils(driver);
    }

    public boolean isToastPresent() {
        return waitUtils.waitForToast();
    }

    public String getToastMessage() {
        return waitUtils.getToastText();
    }
}
