package com.nuapro.pages;

import com.nuapro.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class InvitationEmailPage {

    private final WebDriver driver;
    private final WaitUtils waitUtils;

    private final By emailRecipientInput = By.cssSelector("input[name='invite_email_to'], [data-testid='nua-invite-email-input']");
    private final By sendEmailButton = By.cssSelector(".send_invite_email_btn, [data-testid='nua-send-email-btn']");

    public InvitationEmailPage(WebDriver driver) {
        this.driver = driver;
        this.waitUtils = new WaitUtils(driver);
    }

    public void sendInvitationEmail(String recipientEmail) {
        waitUtils.sendKeys(emailRecipientInput, recipientEmail);
        waitUtils.clickElement(sendEmailButton);
        waitUtils.waitForLoaderToDisappear();
    }
}
