package com.nuapro.tests;

import com.nuapro.pages.DashboardPage;
import com.nuapro.pages.SettingsPage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class NotificationSettingsPersistenceTest extends NotificationTestBase {

    @Test(groups = {"settings", "notification"},
            description = "Admin notification subjects and messages persist after refresh")
    public void testAdminNotificationTemplatePersistence() {
        DashboardPage dashboardPage = loginAsAdmin();
        SettingsPage settingsPage = dashboardPage.clickSettingsTab();

        settingsPage.clickAdminNotifTab();
        String originalSubject = settingsPage.getAdminNotificationSubject();
        String originalMessage = settingsPage.getAdminNotificationMessage();
        String subject = marker("ADMIN_TEMPLATE");
        String message = marker("ADMIN_TEMPLATE_BODY");

        try {
            settingsPage.setAdminNotificationSubject(subject);
            settingsPage.setAdminNotificationMessage(message);
            settingsPage.saveSettings();
            driver.navigate().refresh();

            Assert.assertEquals(settingsPage.getAdminNotificationSubject(), subject,
                    "Admin notification subject should persist after refresh");
            Assert.assertTrue(settingsPage.getAdminNotificationMessage().contains(message),
                    "Admin notification message should persist after refresh");
        } finally {
            settingsPage.setAdminNotificationSubject(originalSubject);
            settingsPage.setAdminNotificationMessage(originalMessage);
            settingsPage.saveSettings();
        }
    }

    @Test(groups = {"settings", "notification"},
            description = "User notification subjects, messages, and suppression persist after refresh")
    public void testUserNotificationTemplatePersistence() {
        DashboardPage dashboardPage = loginAsAdmin();
        SettingsPage settingsPage = dashboardPage.clickSettingsTab();

        settingsPage.clickUserNotifTab();
        String originalApprovalSubject = settingsPage.getUserApprovalNotificationSubject();
        String originalApprovalMessage = settingsPage.getUserApprovalNotificationMessage();
        String originalDenialSubject = settingsPage.getUserDenialNotificationSubject();
        String originalDenialMessage = settingsPage.getUserDenialNotificationMessage();
        boolean originalSuppressed = settingsPage.isSuppressDenialNotificationEnabled();
        String approvalSubject = marker("USER_APPROVAL_TEMPLATE");
        String approvalMessage = marker("USER_APPROVAL_TEMPLATE_BODY");
        String denialSubject = marker("USER_DENIAL_TEMPLATE");
        String denialMessage = marker("USER_DENIAL_TEMPLATE_BODY");

        try {
            settingsPage.setUserApprovalNotification(
                    approvalSubject, approvalMessage, false);
            settingsPage.setUserDenialNotification(
                    denialSubject, denialMessage, false);
            settingsPage.setSuppressDenialNotification(true);
            settingsPage.saveSettings();
            driver.navigate().refresh();

            Assert.assertEquals(settingsPage.getUserApprovalNotificationSubject(), approvalSubject,
                    "Approval subject should persist after refresh");
            Assert.assertTrue(settingsPage.getUserApprovalNotificationMessage().contains(approvalMessage),
                    "Approval message should persist after refresh");
            Assert.assertEquals(settingsPage.getUserDenialNotificationSubject(), denialSubject,
                    "Denial subject should persist after refresh");
            Assert.assertTrue(settingsPage.getUserDenialNotificationMessage().contains(denialMessage),
                    "Denial message should persist after refresh");
            Assert.assertTrue(settingsPage.isSuppressDenialNotificationEnabled(),
                    "Denial suppression should persist after refresh");
        } finally {
            settingsPage.setUserApprovalNotification(
                    originalApprovalSubject, originalApprovalMessage, false);
            settingsPage.setUserDenialNotification(
                    originalDenialSubject, originalDenialMessage, false);
            settingsPage.setSuppressDenialNotification(originalSuppressed);
            settingsPage.saveSettings();
        }
    }
}
