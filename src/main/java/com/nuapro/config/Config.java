package com.nuapro.config;

public class Config {

    public static String getBaseUrl() {
        String url = System.getenv("SELENIUM_BASE_URL");
        if (url == null || url.trim().isEmpty()) {
            url = System.getProperty("SELENIUM_BASE_URL", "http://nuaproautomation.local");
        }
        return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    }

    public static String getAdminUser() {
        String user = System.getenv("SELENIUM_ADMIN_USER");
        if (user == null || user.trim().isEmpty()) {
            user = System.getProperty("SELENIUM_ADMIN_USER", "atifhasan");
        }
        return user;
    }

    public static String getAdminPass() {
        String pass = System.getenv("SELENIUM_ADMIN_PASS");
        if (pass == null || pass.trim().isEmpty()) {
            pass = System.getProperty("SELENIUM_ADMIN_PASS", "google12345");
        }
        return pass;
    }

    public static String getBrowser() {
        String browser = System.getenv("SELENIUM_BROWSER");
        if (browser == null || browser.trim().isEmpty()) {
            browser = System.getProperty("SELENIUM_BROWSER", "chrome");
        }
        return browser.toLowerCase();
    }

    public static boolean isHeadless() {
        String headless = System.getenv("SELENIUM_HEADLESS");
        if (headless == null || headless.trim().isEmpty()) {
            headless = System.getProperty("SELENIUM_HEADLESS", "false");
        }
        return Boolean.parseBoolean(headless);
    }

    public static int getTimeoutSeconds() {
        String timeout = System.getenv("SELENIUM_TIMEOUT");
        if (timeout == null || timeout.trim().isEmpty()) {
            timeout = System.getProperty("SELENIUM_TIMEOUT", "15");
        }
        try {
            return Integer.parseInt(timeout);
        } catch (NumberFormatException e) {
            return 15;
        }
    }
}
