package com.nuapro.components;

import com.nuapro.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class ConfirmationModal {

    private final WebDriver driver;
    private final WaitUtils waitUtils;

    private final By modalDialog = By.cssSelector(".MuiDialog-root, .NuaModal, .delete-inv-modal");
    private final By confirmButton = By.cssSelector(".MuiDialog-root button.MuiButton-contained, button.delete-btn, [data-testid='nua-modal-confirm']");
    private final By cancelButton = By.cssSelector(".MuiDialog-root button.MuiButton-outlined, .nua-modal-close, button.cancel-btn, [data-testid='nua-modal-cancel']");
    private final By blockUserCheckbox = By.cssSelector(".MuiDialog-root input[type='checkbox']");

    public ConfirmationModal(WebDriver driver) {
        this.driver = driver;
        this.waitUtils = new WaitUtils(driver);
    }

    public boolean isDisplayed() {
        try {
            return waitUtils.waitForVisibility(modalDialog).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public void confirm() {
        waitUtils.clickElement(confirmButton);
        waitUtils.waitForLoaderToDisappear();
    }

    public void cancel() {
        waitUtils.clickElement(cancelButton);
        waitUtils.waitForInvisibility(modalDialog);
    }

    public void toggleBlockUser(boolean check) {
        try {
            var element = waitUtils.waitForVisibility(blockUserCheckbox);
            if (element.isSelected() != check) {
                element.click();
            }
        } catch (Exception ignored) {
        }
    }
}
