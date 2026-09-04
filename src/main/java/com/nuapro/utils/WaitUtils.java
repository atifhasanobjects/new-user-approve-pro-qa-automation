package com.nuapro.utils;

import com.nuapro.config.Config;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class WaitUtils {

    private final WebDriver driver;
    private final WebDriverWait wait;

    public WaitUtils(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(Config.getTimeoutSeconds()));
    }

    public WaitUtils(WebDriver driver, int timeoutSeconds) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
    }

    public WebElement waitForVisibility(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public WebElement waitForClickable(By locator) {
        // React/MUI can keep an old, hidden copy of a component in the DOM while
        // replacing it.  ExpectedConditions.elementToBeClickable(locator) uses
        // the first match, which makes a valid visible control look unavailable.
        return wait.until(currentDriver -> firstVisibleEnabled(currentDriver.findElements(locator)));
    }

    /**
     * Returns an enabled form control even when CSS intentionally hides it.
     * NUA's switch inputs are 0x0 and transparent; visibility is therefore an
     * invalid condition for reading their checked state.
     */
    public WebElement waitForEnabledControl(By locator) {
        return wait.until(currentDriver -> firstEnabled(currentDriver.findElements(locator)));
    }

    public boolean isCheckboxSelected(By checkboxLocator) {
        return waitForEnabledControl(checkboxLocator).isSelected();
    }

    public void clickEnabledControl(By locator) {
        WebElement element = waitForEnabledControl(locator);
        clickEnabledControl(element);
    }

    public void clickEnabledControl(WebElement element) {
        try {
            element.click();
        } catch (ElementNotInteractableException e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
        }
    }

    /**
     * Changes a native checkbox through its visible label and proves that React
     * committed the requested state before returning. This intentionally throws
     * on a missed click, preventing dependent test steps from running on an
     * unchanged configuration.
     */
    public void setCheckboxState(By checkboxLocator, By visibleLabelLocator, boolean expectedState) {
        if (isCheckboxSelected(checkboxLocator) == expectedState) {
            return;
        }

        clickElement(visibleLabelLocator);
        wait.until(currentDriver -> {
            WebElement checkbox = firstEnabled(currentDriver.findElements(checkboxLocator));
            return checkbox != null && checkbox.isSelected() == expectedState;
        });
    }

    public boolean waitForInvisibility(By locator) {
        return wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    public boolean waitForText(By locator, String text) {
        return wait.until(ExpectedConditions.textToBePresentInElementLocated(locator, text));
    }

    public boolean waitForUrlContains(String fraction) {
        return wait.until(ExpectedConditions.urlContains(fraction));
    }

    public String diagnostics() {
        try {
            String bodyText = driver.findElement(By.tagName("body")).getText();
            if (bodyText.length() > 2000) {
                bodyText = bodyText.substring(0, 2000) + "...";
            }
            return "url=" + driver.getCurrentUrl()
                    + ", title=" + driver.getTitle()
                    + ", bodyText=" + bodyText;
        } catch (Exception e) {
            return "url=" + driver.getCurrentUrl() + ", title=" + driver.getTitle()
                    + ", diagnosticsError=" + e.getMessage();
        }
    }

    public void waitForLoaderToDisappear() {
        By loaderLocator = By.cssSelector(".nua-spinner, .new-user-approve-loading");
        // Loaders are also rendered inside unrelated forms and action buttons.
        // Do not wait for a loader to appear: an operation can complete before
        // Selenium observes it, and an unrelated spinner must not block routing.
        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(3));
        try {
            shortWait.until(currentDriver -> currentDriver.findElements(loaderLocator)
                    .stream()
                    .noneMatch(this::isDisplayed));
        } catch (TimeoutException ignored) {
            // The caller's page-specific ready locator is the authoritative wait.
        }
    }

    public boolean waitForToast() {
        By toastLocator = By.cssSelector(".Toastify__toast-body, .toastContainer");
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(toastLocator));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    public String getToastText() {
        By toastLocator = By.cssSelector(".Toastify__toast-body, .toastContainer");
        try {
            WebElement toast = wait.until(ExpectedConditions.visibilityOfElementLocated(toastLocator));
            return toast.getText();
        } catch (TimeoutException e) {
            return "";
        }
    }

    public String waitForToastContaining(String expectedText) {
        By toastLocator = By.cssSelector(".Toastify__toast-body, .toastContainer");
        return wait.until(currentDriver -> currentDriver.findElements(toastLocator).stream()
                .filter(this::isDisplayed)
                .map(WebElement::getText)
                .filter(text -> text.contains(expectedText))
                .findFirst()
                .orElse(null));
    }

    public void clickElement(By locator) {
        WebElement element = waitForClickable(locator);
        try {
            element.click();
        } catch (ElementNotInteractableException intercepted) {
            // WordPress's fixed #wpadminbar can cover a tab after browser
            // scrolling, even though the tab is visible and enabled. Keyboard
            // activation is attempted first; a MUI portal can still report the
            // control as non-interactable while its visible overlay is settling.
            try {
                element.sendKeys(Keys.ENTER);
            } catch (ElementNotInteractableException retry) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
            }
        }
    }

    public void sendKeys(By locator, String text) {
        WebElement element = waitForVisibility(locator);
        element.clear();
        element.sendKeys(text);
    }

    public void uploadFile(By locator, String absolutePath) {
        // MUI's file input can be visually hidden by its TextField wrapper. It
        // remains a real enabled input, so presence, not CSS visibility, is the
        // correct condition before sending a file path.
        WebElement input = waitForEnabledControl(locator);
        input.sendKeys(absolutePath);
    }

    private WebElement firstVisibleEnabled(List<WebElement> elements) {
        for (WebElement element : elements) {
            try {
                if (element.isDisplayed() && element.isEnabled()) {
                    return element;
                }
            } catch (StaleElementReferenceException ignored) {
                // React replaced the element between polling attempts.
            }
        }
        return null;
    }

    private WebElement firstEnabled(List<WebElement> elements) {
        for (WebElement element : elements) {
            try {
                if (element.isEnabled()) {
                    return element;
                }
            } catch (StaleElementReferenceException ignored) {
                // React replaced the element between polling attempts.
            }
        }
        return null;
    }

    private boolean isDisplayed(WebElement element) {
        try {
            return element.isDisplayed();
        } catch (StaleElementReferenceException ignored) {
            return false;
        }
    }
}
