package com.nuapro.tests;

import com.nuapro.utils.EmailCatcherClient;
import org.testng.Assert;
import org.testng.annotations.Test;

public class UserNotificationTest extends NotificationTestBase {

    @Test(groups = {"email", "notification", "user-notification"},
            description = "User receives a welcome email after registration")
    public void testUserReceivesWelcomeEmailAfterRegistration() {
        requireEmailTestEnvironment();

        var dashboardPage = loginAsAdmin();
        configurePendingRegistration(dashboardPage);

        String subjectMarker = marker("USER_WELCOME");
        String bodyMarker = marker("USER_WELCOME_BODY");
        configureUserWelcomeNotification(dashboardPage, subjectMarker, bodyMarker);
        emailClient.clearMessages();

        PendingUser user = registerPendingUser();

        EmailCatcherClient.CapturedEmail email = emailClient.awaitMessage(
                user.email(), subjectMarker, bodyMarker);
        Assert.assertTrue(email.subject().contains(subjectMarker),
                "Welcome email should contain the configured subject marker");
    }

    @Test(groups = {"email", "notification", "user-notification"},
            description = "User receives an approval email after approval")
    public void testUserReceivesApprovalEmail() {
        requireEmailTestEnvironment();

        var dashboardPage = loginAsAdmin();
        configurePendingRegistration(dashboardPage);

        String subjectMarker = marker("USER_APPROVAL");
        String bodyMarker = marker("USER_APPROVAL_BODY");
        configureUserApprovalNotification(dashboardPage, subjectMarker, bodyMarker);
        PendingUser user = registerPendingUser();

        emailClient.clearMessages();
        approveUser(user);

        EmailCatcherClient.CapturedEmail email = emailClient.awaitMessage(
                user.email(), subjectMarker, bodyMarker, user.username(), user.email());
        Assert.assertTrue(email.body().contains(user.username()),
                "Approval email should identify the approved user");
    }

    @Test(groups = {"email", "notification", "user-notification"},
            description = "User receives a denial email after denial")
    public void testUserReceivesDenialEmail() {
        requireEmailTestEnvironment();

        var dashboardPage = loginAsAdmin();
        configurePendingRegistration(dashboardPage);

        String subjectMarker = marker("USER_DENIAL");
        String bodyMarker = marker("USER_DENIAL_BODY");
        configureUserDenialNotification(dashboardPage, subjectMarker, bodyMarker, false);
        PendingUser user = registerPendingUser();

        emailClient.clearMessages();
        denyUser(user);

        EmailCatcherClient.CapturedEmail email = emailClient.awaitMessage(
                user.email(), subjectMarker, bodyMarker, user.username(), user.email());
        Assert.assertTrue(email.body().contains(user.email()),
                "Denial email should identify the denied user");
    }

    @Test(groups = {"email", "notification", "user-notification"},
            description = "Suppress denial message prevents a denial email")
    public void testSuppressedDenialEmailIsNotSent() {
        requireEmailTestEnvironment();

        var dashboardPage = loginAsAdmin();
        configurePendingRegistration(dashboardPage);

        String subjectMarker = marker("USER_DENIAL_SUPPRESSED");
        String bodyMarker = marker("USER_DENIAL_SUPPRESSED_BODY");
        configureUserDenialNotification(dashboardPage, subjectMarker, bodyMarker, true);
        PendingUser user = registerPendingUser();

        emailClient.clearMessages();
        denyUser(user);

        emailClient.assertNoMessage(
                user.email(), subjectMarker, bodyMarker, user.username(), user.email());
    }
}
