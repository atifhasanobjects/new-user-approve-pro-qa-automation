package com.nuapro.utils;

public class TestData {

    public static String generateUniqueId() {
        return String.valueOf(System.currentTimeMillis());
    }

    public static String generateUsername() {
        return "nua_auto_" + generateUniqueId();
    }

    public static String generateEmail() {
        return "nua_auto_" + generateUniqueId() + "@example.test";
    }

    public static String generateDomainEmail(String domain) {
        return "nua_auto_" + generateUniqueId() + "@" + domain;
    }

    public static String generateInvitationCode() {
        return "AUTO-" + System.currentTimeMillis();
    }

    public static String generateRoleName() {
        return "qa_role_" + System.currentTimeMillis();
    }

    public static String generateDomain() {
        return "autodomain" + System.currentTimeMillis() + ".test";
    }
}
