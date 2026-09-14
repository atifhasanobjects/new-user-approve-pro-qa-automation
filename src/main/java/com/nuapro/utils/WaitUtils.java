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

    public WebElement waitForPresence(By locator) {
        return wait.until(currentDriver -> currentDriver.findElements(locator)
                .stream()
                .findFirst()
                .orElse(null));
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
        WebElement checkbox = findCheckboxAssociatedWithLabel(
                checkboxLocator, visibleLabelLocator);
        if (checkbox != null && checkbox.isSelected() == expectedState) {
            return;
        }

        for (int attempt = 0; attempt < 2; attempt++) {
            WebElement label = waitForClickable(visibleLabelLocator);
            try {
                if (attempt == 0) {
                    label.click();
                } else {
                    // React can replace the label immediately after the first
                    // click. A fresh JS click avoids a stale/covered label
                    // while still dispatching the native label activation.
                    ((JavascriptExecutor) driver).executeScript(
                            "arguments[0].click();", label);
                }
            } catch (ElementNotInteractableException e) {
                ((JavascriptExecutor) driver).executeScript(
                        "arguments[0].click();", label);
            }

            try {
                wait.until(currentDriver -> {
                    WebElement associated = findCheckboxAssociatedWithLabel(
                            checkboxLocator, visibleLabelLocator);
                    if (associated != null) {
                        return associated.isSelected() == expectedState;
                    }
                    WebElement fallback = firstEnabled(
                            currentDriver.findElements(checkboxLocator));
                    return fallback != null && fallback.isSelected() == expectedState;
                });
                return;
            } catch (TimeoutException ignored) {
                // Retry once with a fresh label in case the React component
                // re-rendered between the click and state assertion.
            }
        }

        throw new TimeoutException("Checkbox did not reach expected state "
                + expectedState + " for " + checkboxLocator
                + ". " + diagnostics());
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
        StaleElementReferenceException lastStale = null;
        for (int attempt = 0; attempt < 3; attempt++) {
            WebElement element = waitForClickable(locator);
            try {
                element.click();
                return;
            } catch (StaleElementReferenceException stale) {
                // React/MUI can replace the matched node immediately after
                // the wait returns. Re-find it before trying the click again.
                lastStale = stale;
            } catch (ElementNotInteractableException intercepted) {
                // WordPress's fixed #wpadminbar can cover a tab after browser
                // scrolling, even though the tab is visible and enabled. Keyboard
                // activation is attempted first; a MUI portal can still report the
                // control as non-interactable while its visible overlay is settling.
                try {
                    element.sendKeys(Keys.ENTER);
                    return;
                } catch (ElementNotInteractableException retry) {
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
                    return;
                } catch (StaleElementReferenceException stale) {
                    lastStale = stale;
                }
            }
        }
        throw lastStale;
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

    private WebElement findCheckboxAssociatedWithLabel(By checkboxLocator,
                                                        By labelLocator) {
        for (WebElement label : driver.findElements(labelLocator)) {
            try {
                if (!label.isDisplayed() || !label.isEnabled()) {
                    continue;
                }
                List<WebElement> nestedInputs = label.findElements(
                        By.cssSelector("input[type='checkbox']"));
                if (!nestedInputs.isEmpty() && nestedInputs.get(0).isEnabled()) {
                    return nestedInputs.get(0);
                }

                String forValue = label.getAttribute("for");
                if (forValue != null && !forValue.isBlank()) {
                    for (WebElement associated : driver.findElements(By.id(forValue))) {
                        if (associated.isEnabled()) {
                            return associated;
                        }
                    }
                }
            } catch (StaleElementReferenceException ignored) {
                // React replaced this label; inspect the fresh match instead.
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
