# New User Approve Premium — Automation Architecture Guide

## 1. Overview & Tech Stack

This repository provides an automated end-to-end regression testing framework for the **New User Approve Premium** WordPress plugin using Java, Selenium WebDriver, TestNG, Maven, WebDriverManager, and the Page Object Model (POM).

### Stack Components:
- **Language**: Java 21
- **Testing Framework**: TestNG 7.10.2
- **Build Tool**: Maven
- **Browser Automation**: Selenium WebDriver 4.27.0
- **Driver Management**: WebDriverManager 5.9.2
- **Design Pattern**: Page Object Model (POM) + Component Wrappers
- **Synchronization**: Explicit Waits (`WebDriverWait`, `ExpectedConditions`)
- **Reporting & Artifacts**: TestNG Listener for failure screenshots (`target/test-artifacts/`)

---

## 2. Directory Structure

```text
c:\Users\Objects\IdeaProjects\NUAPro\
├── docs/
│   ├── LOCATORS.md
│   ├── AUTOMATION_ARCHITECTURE.md
│   └── TEST_SCENARIOS.md
├── pom.xml
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/
│   │           └── nuapro/
│   │               ├── config/
│   │               │   └── Config.java
│   │               ├── driver/
│   │               │   └── DriverFactory.java
│   │               ├── pages/
│   │               │   ├── LoginPage.java
│   │               │   ├── DashboardPage.java
│   │               │   ├── UsersPage.java
│   │               │   ├── InvitationCodesPage.java
│   │               │   ├── ImportCodesPage.java
│   │               │   ├── InvitationEmailPage.java
│   │               │   ├── AutoApprovePage.java
│   │               │   ├── SettingsPage.java
│   │               │   ├── RoleEditorPage.java
│   │               │   ├── IntegrationsPage.java
│   │               │   └── MobileAppPage.java
│   │               ├── components/
│   │               │   ├── UserTableComponent.java
│   │               │   ├── ConfirmationModal.java
│   │               │   ├── ToastComponent.java
│   │               │   └── ReactLoaderComponent.java
│   │               └── utils/
│   │                   ├── WaitUtils.java
│   │                   ├── TestData.java
│   │                   ├── ScreenshotUtils.java
│   │                   ├── CsvUtils.java
│   │                   └── FileDownloadUtils.java
│   └── test/
│       ├── java/
│       │   └── com/
│       │       └── nuapro/
│       │           ├── base/
│       │           │   └── BaseTest.java
│       │           ├── reporting/
│       │           │   └── TestListener.java
│       │           └── tests/
│       │               ├── AdminSmokeTest.java
│       │               ├── AdminAuthenticationTest.java
│       │               ├── UserApprovalTest.java
│       │               ├── UserDenialTest.java
│       │               ├── UserBlockTest.java
│       │               ├── UserDeleteTest.java
│       │               ├── BulkUserActionTest.java
│       │               ├── UserTableTest.java
│       │               ├── InvitationCodeTest.java
│       │               ├── InvitationCodeImportTest.java
│       │               ├── AutoApprovalTest.java
│       │               ├── SettingsTest.java
│       │               ├── RoleEditorTest.java
│       │               ├── DashboardNavigationTest.java
│       │               └── IntegrationTest.java
│       └── resources/
│           └── testng.xml
```

---

## 3. Configuration Management

Environment settings are managed dynamically via `com.nuapro.config.Config`. Settings are loaded from environment variables (or fallbacks):

| Variable Name | Default Value | Description |
| --- | --- | --- |
| `SELENIUM_BASE_URL` | `http://nuaproautomation.local` | Target WordPress URL |
| `SELENIUM_ADMIN_USER` | `atifhasan` | Admin username |
| `SELENIUM_ADMIN_PASS` | `google12345` | Admin password |
| `SELENIUM_BROWSER` | `chrome` | Target browser (`chrome`, `edge`, `firefox`) |
| `SELENIUM_HEADLESS` | `false` | Headless execution toggle (`true`/`false`) |

---

## 4. Driver Management & Headless Execution

`DriverFactory` handles browser initialization, ChromeOptions configuration, download directory configuration (`target/downloads`), window sizing, headless mode, and implicit wait setup.

---

## 5. ReactJS-Aware Synchronization (`WaitUtils`)

Because New User Approve Premium is built with ReactJS using `/wp-admin/admin.php?page=new-user-approve-admin#/action=...`), traditional page reloads do not always occur. `WaitUtils` provides explicit synchronization methods:

- `waitForElementVisible(By locator)`
- `waitForElementClickable(By locator)`
- `waitForElementInvisible(By locator)`
- `waitForLoaderToDisappear()`
- `waitForToast()`
- `waitForUrlContains(String text)`
- `waitForRowByEmail(String email)`
- `waitForRowStatus(String email, String expectedStatus)`

Admin login is followed by an explicit navigation to
`/wp-admin/admin.php?page=new-user-approve-admin` and a wait for the NUA dashboard
root. This avoids relying on WordPress's post-login redirect, which normally
lands on the generic WordPress admin home rather than the plugin dashboard.
Navigation failures include the current URL, page title, element match count,
and a shortened body-text diagnostic.

---

## 6. Page Object Model & Dynamic Re-rendering

To prevent `StaleElementReferenceException` during React component re-renders, Page Objects store `By` locators rather than caching `WebElement` objects. WebElements are acquired dynamically inside action methods.

---

## 7. Failure Artifacts & Test Reporting

`TestListener` implements `ITestListener`. When a test fails:
1. `ScreenshotUtils` captures a full-page screenshot.
2. The current page HTML source is saved.
3. Artifacts are written to `target/test-artifacts/<TestName>_<timestamp>/`.
