# New User Approve Premium — Selenium Java QA Automation Analysis & Codex Implementation Prompt

## 1. Purpose

This document is an automation-focused analysis of the WordPress plugin repository:

`atifhasanobjects/new-user-approve-premium`

The goal is to implement end-to-end UI automation using:

- Java
- Selenium WebDriver
- TestNG
- Maven
- WebDriverManager
- Page Object Model (POM)
- Explicit waits
- Reusable test-data utilities

The plugin backend dashboard is implemented largely in ReactJS inside the WordPress admin area, while WordPress/PHP provides the plugin bootstrap, REST endpoints, user-state operations, settings persistence, and integrations.

---

## 2. Repository Architecture Relevant to QA Automation

Important repository areas:

```text
new-user-approve-premium/
├── admin/
├── assets/
├── build/
├── includes/
├── premium-files/
├── src/
│   ├── App.js
│   ├── functions.js
│   ├── index.js
│   ├── style.css
│   └── components/
│       ├── dashboard/
│       ├── invitation-code/
│       ├── role-editor/
│       ├── settings/
│       └── wp-editor/
├── new-user-approve.php
├── pw-new-user-approve.php
├── package.json
└── readme.txt
```

### React routing

`src/App.js` uses `HashRouter`.

Important routes identified:

```text
#/
#/action=users/*
#/action=user-roles
#/action=inv-codes/*
#/action=import-codes
#/action=email
#/action=auto-approve/*
#/action=integrations
#/action=settings/*
#/action=mobile-app
#/action=role-editor/*
```

Because React uses hash routing, Selenium should not assume every navigation causes a full browser page load. Test code must wait for React-rendered elements/state changes after route transitions.

---

## 3. Major Functional Areas Found

### 3.1 Dashboard / User Management

The React dashboard contains components including:

```text
dashboard.jsx
tabs.jsx
recent-users.jsx
recent_users_table.jsx
auto-approve.jsx
counter-list.jsx
integrations/
mobile-app.jsx
fetch_users/
```

The current plugin changelog and React functions show support for:

- Pending users
- Approved users
- Denied users
- Blocked users
- Approve user
- Deny user
- Bulk approve
- Bulk deny
- Delete user
- Block user
- Unblock user
- User counters
- Recent users
- Activity log
- User role changes
- User table pagination/filtering
- Approval queue position
- Auto user denial after configured period

### 3.2 Invitation Codes

React source includes:

```text
add-code-subtabs.jsx
edit-invitation-code.jsx
import-codes.jsx
invitation-code-main-tabs.jsx
invitation-email.jsx
nua-invitation-layout.jsx
```

Functionality includes:

- Add invitation code manually
- Auto-generate invitation codes
- Edit invitation code
- Delete invitation code
- Bulk delete invitation codes
- Enable/disable code
- Import codes through CSV
- Download sample CSV
- Invitation code usage/remaining uses
- Send invitation codes by email
- Validate duplicate invitation codes

### 3.3 Settings

React settings components include:

```text
settings.jsx
settings-tabs.jsx
tabs/general.jsx
tabs/registration.jsx
tabs/admin-notification.jsx
tabs/user-notification.jsx
tabs/notification.jsx
tabs/help.jsx
```

Settings-related scenarios should cover persisted configuration rather than only UI presence.

Potential groups:

- General settings
- Registration configuration
- Registration deadline
- Time picker/deadline behavior
- Approval/denial messages
- Administrator notifications
- User notifications
- Sender/admin email configuration
- Notification role targeting
- HTML email/template settings
- Help/support UI

### 3.4 Auto Approval / Auto Denial

The plugin has `auto-approve.jsx` and documented functionality for:

- Auto-approval for specific user roles
- Whitelist domains
- Blacklist domains
- Auto-approval using invitation codes
- Registration deadline
- Invitation code auto-approval after deadline
- Automatic user denial after a configured duration

### 3.5 Role Editor

The React application exposes:

```text
#/action=role-editor/*
```

The plugin changelog states that Role Editor supports:

- Create custom role
- Edit role
- Delete role
- Manage capabilities
- Bulk capability toggling
- Sort Role column
- Sort Users column
- Sort Capabilities column

### 3.6 Integrations / Mobile App

React routes exist for:

```text
#/action=integrations
#/action=mobile-app
```

The changelog also mentions integrations/compatibility with:

- WooCommerce
- MemberPress
- BuddyPress
- Gravity Forms
- JetFormBuilder
- User Role Editor
- Zapier

These should be treated as a separate integration suite because some require additional plugins.

---

## 4. React/API Behavior Important for Selenium

`src/functions.js` performs WordPress REST calls using browser-side `fetch()` and sends the WordPress nonce:

```text
X-WP-Nonce: wpApiSettings.nonce
```

Operations found include concepts such as:

```text
update_users
get_activity_log
get_user_roles
update_user_role
get_whitelist_domains
get_blacklist_domains
update_whitelist_domains
update_blacklist_domains
get_api_key
update_api_key
update_invitation_code
delete_nua_user
unblock_nua_user
delete_invCode
save_invitation_auto_codes
save_invitation_codes
status_update_invCode
import_csv_codes
sample_code_csv
invite_email
get_nua_invite_codes
get_remaining_uses
```

### Selenium implication

Do not use `Thread.sleep()` as the main synchronization strategy.

React may update UI only after asynchronous REST responses. Selenium must use explicit waits such as:

```java
WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
wait.until(ExpectedConditions.elementToBeClickable(locator));
wait.until(ExpectedConditions.invisibilityOfElementLocated(loader));
wait.until(driver -> expectedReactStateIsVisible());
```

For actions that change table data, wait for a state condition such as:

- row disappears,
- status text changes,
- toast appears,
- button set changes,
- counter changes,
- table finishes re-rendering.

---

## 5. Locator Strategy for ReactJS Pages

Do not create fragile Selenium locators based on React implementation details.

### Preferred order

1. Stable `data-testid` / `data-qa`
2. Stable HTML `id`
3. Stable `name`
4. Semantic accessible selector
5. Stable class plus scoped container
6. Text-based XPath only when the visible text is a real product contract
7. Positional XPath only as last resort

Recommended product-side QA attributes:

```html
data-testid="nua-user-table"
data-testid="nua-user-row-123"
data-testid="approve-user-123"
data-testid="deny-user-123"
data-testid="block-user-123"
data-testid="delete-user-123"
data-testid="bulk-action"
data-testid="bulk-apply"
data-testid="settings-save"
data-testid="invitation-code-input"
data-testid="role-name-input"
```

### Avoid

```text
div:nth-child(...)
absolute XPath
generated CSS-module class names
React-generated class combinations
locating only by button color
hardcoded row number
```

---

## 6. Recommended Java Automation Project

```text
selenium-automation/
├── pom.xml
├── testng.xml
└── src/
    ├── main/java/
    │   ├── config/
    │   │   └── Config.java
    │   ├── driver/
    │   │   └── DriverFactory.java
    │   ├── pages/
    │   │   ├── LoginPage.java
    │   │   ├── DashboardPage.java
    │   │   ├── UsersPage.java
    │   │   ├── InvitationCodesPage.java
    │   │   ├── ImportCodesPage.java
    │   │   ├── InvitationEmailPage.java
    │   │   ├── AutoApprovePage.java
    │   │   ├── SettingsPage.java
    │   │   ├── RoleEditorPage.java
    │   │   ├── IntegrationsPage.java
    │   │   └── MobileAppPage.java
    │   ├── components/
    │   │   ├── UserTable.java
    │   │   ├── Toast.java
    │   │   ├── ConfirmationModal.java
    │   │   └── ReactLoader.java
    │   └── utils/
    │       ├── WaitUtils.java
    │       ├── TestData.java
    │       ├── ScreenshotUtils.java
    │       └── CsvUtils.java
    └── test/java/
        ├── base/
        │   └── BaseTest.java
        ├── auth/
        │   └── WordPressLoginTest.java
        ├── users/
        │   ├── UserStatusTest.java
        │   ├── BulkUserActionsTest.java
        │   ├── BlockUserTest.java
        │   └── DeleteUserTest.java
        ├── invitation/
        │   ├── InvitationCodeCrudTest.java
        │   ├── InvitationCodeImportTest.java
        │   └── InvitationEmailTest.java
        ├── settings/
        │   ├── GeneralSettingsTest.java
        │   ├── RegistrationSettingsTest.java
        │   └── NotificationSettingsTest.java
        ├── autoapproval/
        │   └── AutoApprovalTest.java
        └── roles/
            └── RoleEditorTest.java
```

---

## 7. Configuration

Use environment variables, Maven properties, or a local uncommitted properties file.

Example:

```properties
base.url=http://newuserapprove.local
admin.username=admin
admin.password=change-me
browser=chrome
headless=false
default.timeout=15
```

Never commit real credentials.

Support environment variables such as:

```text
SELENIUM_BASE_URL
SELENIUM_ADMIN_USER
SELENIUM_ADMIN_PASS
SELENIUM_BROWSER
SELENIUM_HEADLESS
```

---

## 8. Recommended Maven Dependencies

Use current compatible stable versions available in the development environment.

Core dependencies:

```text
selenium-java
testng
webdrivermanager
slf4j-simple
commons-csv (optional for CSV tests)
```

Do not unnecessarily introduce Cucumber unless BDD is specifically required.

---

# 9. Automation Scenarios

## P0 — Smoke / Critical

### TC-001 Admin login

**Precondition:** WordPress admin exists.

Steps:

1. Open `/wp-login.php`.
2. Enter admin username/password.
3. Submit login.
4. Verify WordPress admin dashboard loads.

Expected:

- Authenticated session is created.
- No login error is displayed.

---

### TC-002 New registered user is Pending

Steps:

1. Register a unique frontend user.
2. Login as admin.
3. Open New User Approve > Users.
4. Search/filter for the generated user.
5. Inspect current status.

Expected:

- New user is present.
- Status is Pending unless an enabled auto-approval rule applies.

---

### TC-003 Approve pending user

Steps:

1. Create a pending user.
2. Open the plugin Users page.
3. Locate row by unique email.
4. Click Approve.
5. Confirm action if required.
6. Wait for React table update.
7. Refresh/reopen page.

Expected:

- User status becomes Approved.
- Persistence survives refresh.
- Available row actions change according to Approved state.

---

### TC-004 Deny pending user

Expected:

- Pending user becomes Denied.
- Persistence survives refresh.

---

### TC-005 Approved user can be denied

Expected:

- Approved -> Denied transition succeeds.

---

### TC-006 Denied user can be approved

Expected:

- Denied -> Approved transition succeeds.

---

## P0 — User Management

### TC-007 Block user

Expected:

- User changes to Blocked state.
- UI provides appropriate blocked-state action(s).

### TC-008 Unblock user

Expected:

- Blocked user is successfully unblocked.
- UI refreshes without stale status.

### TC-009 Delete user

Expected:

- Confirmation flow is shown if implemented.
- User is removed from the plugin user table.
- User no longer exists where the product contract requires deletion.

### TC-010 Cancel delete user

Expected:

- User remains unchanged.

---

## P1 — Bulk User Actions

### TC-011 Bulk approve multiple pending users

Expected:

- All selected users become Approved.
- Unselected users remain unchanged.

### TC-012 Bulk deny multiple pending users

Expected:

- All selected users become Denied.

### TC-013 Bulk action with no selection

Expected:

- No destructive operation occurs.
- Validation/message is displayed if designed.

---

## P1 — User Table

### TC-014 User search/filter

Expected:

- Correct matching records remain.

### TC-015 Status filtering

Test:

- Pending
- Approved
- Denied
- Blocked

### TC-016 Pagination

Expected:

- Next/previous page works.
- Data does not duplicate incorrectly.

### TC-017 Users-per-page selector

Expected:

- Selected page size is applied.

### TC-018 User counters

Expected:

- Dashboard counters correspond to current fixture state.

### TC-019 Approval queue counter

Expected:

- Pending user queue position is displayed according to plugin rules.

---

# 10. Invitation Code Scenarios

## P0/P1

### TC-020 Add manual invitation code

Expected:

- Code is created.
- Code appears in list.

### TC-021 Duplicate invitation code

Expected:

- Duplicate is rejected.
- Existing code remains unchanged.

### TC-022 Auto-generate invitation codes

Expected:

- Requested codes are generated.
- Generated values satisfy product validation.

### TC-023 Edit invitation code

Expected:

- Updated code properties persist after refresh.

### TC-024 Enable/disable invitation code

Expected:

- Active state changes and persists.

### TC-025 Delete one invitation code

Expected:

- Code disappears.

### TC-026 Bulk delete invitation codes

Expected:

- Selected codes are removed.
- Unselected codes remain.

### TC-027 Import valid CSV

Expected:

- Valid codes are imported.
- Summary/success feedback appears.

### TC-028 Import invalid CSV

Examples:

- bad format
- missing required column
- duplicate values

Expected:

- Appropriate validation.
- No corrupt partial state unless explicitly designed.

### TC-029 Download sample CSV

Expected:

- A file download is initiated.
- Downloaded file exists and is non-empty.

### TC-030 Invitation code remaining uses

Expected:

- Remaining usage value is displayed and changes correctly after consumption.

---

# 11. Registration / Auto Approval

### TC-031 Auto approve by whitelisted domain

Steps:

1. Add a unique domain to whitelist.
2. Register a user using that domain.
3. Check user status.

Expected:

- User is automatically Approved.

### TC-032 Non-whitelisted domain remains pending

Expected:

- No unintended auto approval.

### TC-033 Blacklisted domain behavior

Expected:

- Registration/status follows documented blacklist rule.

### TC-034 Auto approval based on user role

Expected:

- Matching configured role triggers approval.
- Non-matching role does not.

### TC-035 Registration deadline

Test boundary conditions:

- before deadline
- exactly near deadline
- after deadline

Avoid exact second-level assumptions unless product semantics define them.

### TC-036 Auto denial after configured duration

This is time-sensitive and can become slow/flaky.

Recommendation:

- Keep one true E2E test if the minimum duration is practical.
- Prefer backend fixture/time manipulation for broader coverage if the application permits it.

---

# 12. Settings Scenarios

## General

### TC-037 Save General settings

Expected:

- Success state shown.
- Values persist after refresh.

### TC-038 Toggle setting ON/OFF

Expected:

- Both states persist.

### TC-039 Invalid setting input

Expected:

- Validation appears.
- Invalid value does not corrupt saved state.

## Registration

### TC-040 Save registration settings

Expected:

- Configuration persists.

### TC-041 Time/deadline picker

Expected:

- Date/time can be selected.
- Saved value reloads accurately.

## Admin Notification

### TC-042 Save admin notification subject/body

Expected:

- Text persists.

### TC-043 Notification recipient roles

Expected:

- Selected roles persist.

## User Notification

### TC-044 Save approval template

Expected:

- Subject/body persist.

### TC-045 Save denial template

Expected:

- Subject/body persist.

### TC-046 HTML template mode

Expected:

- Toggle persists.
- Editor behaves according to selected mode.

Note: UI automation should verify configuration and invocation where practical. Delivery to an external mailbox should be a separate integration test, not a requirement for every Selenium run.

---

# 13. Role Editor Scenarios

### TC-047 Create custom role

Expected:

- Role appears in roles table.

### TC-048 Duplicate role validation

Expected:

- Duplicate/invalid role is rejected safely.

### TC-049 Edit custom role

Expected:

- Changes persist.

### TC-050 Delete custom role

Expected:

- Role is removed after confirmation.

### TC-051 Cancel role deletion

Expected:

- Role remains.

### TC-052 Add capability to role

Expected:

- Capability persists.

### TC-053 Remove capability

Expected:

- Capability is removed.

### TC-054 Bulk toggle capabilities

Expected:

- Intended capability set changes consistently.

### TC-055 Sort Role column

Expected:

- Ascending/descending order works.

### TC-056 Sort Users column

Expected:

- Numeric ordering is correct.

### TC-057 Sort Capabilities column

Expected:

- Sorting works according to product behavior.

---

# 14. Dashboard / Navigation Tests

### TC-058 Dashboard loads React app

Expected:

- Main React root renders.
- No permanent loading indicator.
- No visible fatal/error state.

### TC-059 Navigate every main plugin tab

Verify:

```text
Users
User Roles
Invitation Codes
Import Codes
Invitation Email
Auto Approve
Integrations
Settings
Mobile App
Role Editor
```

Expected:

- Correct React view loads.
- URL hash changes appropriately.
- Browser back/forward remains functional where expected.

### TC-060 Direct hash-route access

Example:

```text
/wp-admin/admin.php?page=<plugin-page>#/action=settings/...
```

Expected:

- Requested React screen is rendered after authenticated direct navigation.

---

# 15. Integration Tests

Keep these in separate TestNG groups because they require extra plugins/services.

Suggested groups:

```java
@Test(groups = {"integration", "woocommerce"})
@Test(groups = {"integration", "gravityforms"})
@Test(groups = {"integration", "jetformbuilder"})
@Test(groups = {"integration", "memberpress"})
```

Scenarios should verify plugin-specific user registration reaches the expected NUA status flow.

---

# 16. Tests That Should NOT Depend Only on Visual UI

For high-value state-changing operations, verify persistence by reopening/refreshing the UI.

Examples:

```text
Approve/Deny
Block/Unblock
Delete
Settings save
Invitation code create/edit/delete
Role create/edit/delete
Capability changes
Whitelist/blacklist changes
```

A toast message alone is insufficient proof of success.

---

# 17. React Synchronization Rules

Implement a reusable React-aware wait strategy.

Possible conditions:

```java
waitForLoaderToDisappear();
waitForToast();
waitForTableRow(email);
waitForRowStatus(email, "Approved");
waitForRowToDisappear(email);
waitForRouteFragment("action=settings");
waitForButtonEnabled(saveButton);
```

If the table temporarily goes stale during React re-render, reacquire elements instead of caching `WebElement` instances.

Bad:

```java
WebElement row = driver.findElement(...);
// React rerenders
row.findElement(...); // may throw StaleElementReferenceException
```

Better:

```java
By rowLocator = userRowByEmail(email);
wait.until(ExpectedConditions.visibilityOfElementLocated(rowLocator));
driver.findElement(rowLocator);
```

---

# 18. Test Data Strategy

Every run should generate unique data.

Example:

```java
String id = String.valueOf(System.currentTimeMillis());

String username = "nua_auto_" + id;
String email = "nua_auto_" + id + "@example.test";
String inviteCode = "AUTO-" + id;
String roleName = "qa_role_" + id;
```

Do not depend on an existing hardcoded WordPress user.

Tests must clean up the records they create where practical.

---

# 19. Isolation

Avoid one giant test:

```text
create user -> approve -> deny -> block -> delete -> change settings -> role editor
```

Use independent tests/fixtures.

Each destructive test should own its data.

This makes failures diagnosable and allows parallelization later.

---

# 20. Screenshots / Failure Diagnostics

Create a TestNG listener.

On failure save:

```text
screenshot
current URL
test name
browser name
timestamp
```

Optionally save page source for React failures.

Recommended path:

```text
target/test-artifacts/<test-name>/
```

---

# 21. Headless and Browser Support

Initial target:

- Chrome

Framework should allow later use of:

- Edge
- Firefox

Run modes:

```text
local headed
local headless
CI headless
```

Do not hardcode ChromeDriver path. Use WebDriverManager or Selenium Manager.

---

# 22. Proposed TestNG Groups

```text
smoke
users
bulk
invitation
settings
roles
autoapproval
integration
regression
destructive
```

Example:

```java
@Test(groups = {"smoke", "users"})
public void adminCanApprovePendingUser() {
}
```

---

# 23. Implementation Order

## Phase 1 — Framework

1. Maven project
2. Config loader
3. DriverFactory
4. BaseTest
5. WaitUtils
6. screenshot/listener
7. WordPress LoginPage

## Phase 2 — Critical Users Flow

8. UsersPage
9. unique frontend registration utility/page
10. create pending user
11. approve
12. deny
13. block/unblock
14. delete
15. bulk actions

## Phase 3 — Invitation Codes

16. CRUD
17. enable/disable
18. CSV import
19. sample CSV download

## Phase 4 — Settings

20. General
21. Registration
22. Admin notification
23. User notification

## Phase 5 — Role Editor

24. Role CRUD
25. Capabilities
26. Sorting

## Phase 6

27. Auto approval
28. Auto denial
29. Integration suites
30. CI

---

# 24. Acceptance Criteria for the Automation Framework

The initial implementation is acceptable when:

- `mvn test` runs the suite.
- WordPress credentials are externalized.
- Chrome driver is managed automatically.
- Tests use explicit waits.
- No major test uses arbitrary long `Thread.sleep()`.
- Page Object Model is used.
- React route changes are handled.
- Tests locate dynamic users by unique username/email rather than row number.
- State-changing tests verify persistence.
- Failure screenshots are generated.
- TestNG groups exist.
- Cleanup is implemented where practical.
- README documents local execution.

---

# 25. Codex / AI Implementation Prompt

Copy the prompt below and give it to Codex or another coding agent.

```text
You are a senior QA automation engineer and Java/Selenium architect.

Repository under test:
https://github.com/atifhasanobjects/new-user-approve-premium

TASK

Analyze the complete repository before implementing anything.

This is a WordPress plugin named New User Approve Premium. Its WordPress admin UI is largely implemented in ReactJS. I need a production-quality end-to-end automation framework written in JAVA using SELENIUM WEBDRIVER.

Do not implement tests in JavaScript, TypeScript, Playwright, Cypress or Python.

TECH STACK

Use:

- Java
- Maven
- Selenium WebDriver
- TestNG
- WebDriverManager or Selenium Manager
- Page Object Model
- Explicit waits
- TestNG listeners for failure screenshots

Do not use Cucumber unless there is a compelling existing repository requirement.

REPOSITORY ANALYSIS REQUIREMENTS

Before writing automation code, inspect at minimum:

- src/App.js
- src/functions.js
- src/components/dashboard/**
- src/components/invitation-code/**
- src/components/settings/**
- src/components/role-editor/**
- new-user-approve.php
- pw-new-user-approve.php
- includes/**
- premium-files/**
- package.json
- readme.txt

Understand which React controls call which WordPress REST/AJAX operations.

The React app uses HashRouter. Routes currently include concepts such as:

#/
#/action=users/*
#/action=user-roles
#/action=inv-codes/*
#/action=import-codes
#/action=email
#/action=auto-approve/*
#/action=integrations
#/action=settings/*
#/action=mobile-app
#/action=role-editor/*

Do not assume navigation causes a traditional full-page load.

IMPORTANT SELENIUM RULES

1. Use explicit WebDriverWait synchronization.
2. Do NOT use Thread.sleep() as the normal synchronization mechanism.
3. React components can rerender, so do not retain stale WebElement references.
4. Prefer By locators and reacquire elements after React state changes.
5. Locate user rows by unique username/email, never by fixed row position.
6. Verify persisted application state after state-changing actions.
7. Do not consider a toast alone sufficient evidence that an action succeeded.
8. Use resilient selectors.
9. Prefer:
   - data-testid/data-qa
   - id
   - name
   - semantic/accessibility locators
   - scoped CSS
   - stable text XPath
10. Avoid absolute XPath and nth-child selectors.

If stable test attributes are missing in React source, DO NOT silently build fragile locators.

Instead:
- identify the exact affected React elements,
- add minimal data-testid attributes where appropriate,
- do not change business behavior.

CONFIGURATION

The framework must read configuration externally.

Support:

SELENIUM_BASE_URL
SELENIUM_ADMIN_USER
SELENIUM_ADMIN_PASS
SELENIUM_BROWSER
SELENIUM_HEADLESS

Provide reasonable defaults only for non-secret values.

Never commit real credentials.

EXPECTED PROJECT STRUCTURE

Create or use an automation module with a clean structure similar to:

src/main/java/
  config/
  driver/
  pages/
  components/
  utils/

src/test/java/
  base/
  auth/
  users/
  invitation/
  settings/
  autoapproval/
  roles/

Create:

Config
DriverFactory
BaseTest
WaitUtils
ScreenshotUtils
TestNG failure listener
LoginPage
DashboardPage
UsersPage
InvitationCodesPage
ImportCodesPage
InvitationEmailPage
AutoApprovePage
SettingsPage
RoleEditorPage
IntegrationsPage
MobileAppPage

Create reusable components when useful:

UserTable
ConfirmationModal
Toast
ReactLoader

TEST DATA

Generate unique data per test.

For example:

username = nua_auto_<timestamp>
email = nua_auto_<timestamp>@example.test
invitationCode = AUTO-<timestamp>
roleName = qa_role_<timestamp>

Do not depend on manually pre-existing test users other than the WordPress admin.

Where practical, clean up records created by tests.

IMPLEMENT THESE TESTS IN PRIORITY ORDER

P0:

1. WordPress admin login.
2. New frontend registration produces a Pending user when no auto-approval rule applies.
3. Admin approves Pending user.
4. Admin denies Pending user.
5. Denied user can be approved.
6. Approved user can be denied.
7. Block user.
8. Unblock user.
9. Delete user.
10. Cancel delete.

P1 USER TABLE:

11. Bulk approve multiple users.
12. Bulk deny multiple users.
13. Bulk action with no selected users.
14. Search/filter users.
15. Filter by status.
16. Pagination.
17. Users-per-page.
18. User/status counters.
19. Approval queue counter if displayed.

P1 INVITATION CODE:

20. Add manual invitation code.
21. Reject duplicate invitation code.
22. Generate automatic invitation code(s).
23. Edit invitation code.
24. Enable/disable invitation code.
25. Delete invitation code.
26. Bulk delete invitation codes.
27. Import valid CSV.
28. Import invalid CSV.
29. Download sample CSV and verify downloaded file exists.
30. Verify remaining-use behavior where deterministic.

P1 AUTO APPROVAL:

31. Whitelisted domain auto-approves matching registration.
32. Non-whitelisted domain does not receive unintended approval.
33. Blacklisted domain follows expected rule.
34. Auto approval by user role.
35. Registration deadline setting.
36. Auto-denial rule only if it can be tested deterministically without making the normal suite extremely slow.

P1 SETTINGS:

37. Save General settings and verify after refresh.
38. Toggle setting and verify persistence.
39. Invalid setting validation.
40. Registration settings.
41. Date/time/deadline control.
42. Admin notification subject/body.
43. Notification user-role selection.
44. User approval notification template.
45. User denial notification template.
46. HTML/template setting behavior.

P1 ROLE EDITOR:

47. Create custom role.
48. Duplicate/invalid role validation.
49. Edit custom role.
50. Delete role.
51. Cancel role deletion.
52. Add capability.
53. Remove capability.
54. Bulk capability toggle.
55. Role column sorting.
56. Users column sorting.
57. Capabilities column sorting.

NAVIGATION:

58. Verify React dashboard loads.
59. Navigate all main plugin sections.
60. Verify direct authenticated access to important hash routes.

INTEGRATIONS

Create integration TestNG groups, but do not make extra-plugin tests part of the core smoke suite.

Potential integration groups:

woocommerce
gravityforms
jetformbuilder
memberpress
buddypress
zapier

EMAIL

Do not make external email delivery a requirement for every Selenium run.

For core UI tests verify:
- notification settings persist,
- configured actions can be triggered,
- success/failure UI behaves correctly.

If a local mail catcher is present, create a separate email-integration suite.

TESTNG GROUPS

Use useful groups including:

smoke
users
bulk
invitation
settings
roles
autoapproval
regression
destructive
integration

FAILURE DIAGNOSTICS

On test failure save under target/test-artifacts:

- screenshot
- current URL
- test method name
- timestamp

Optionally save page source.

DOWNLOAD TESTING

Configure browser download directory under target/downloads.

For CSV sample download:
- clean download directory before test,
- initiate download,
- explicitly wait for file creation,
- verify file is not empty.

FRONTEND USER REGISTRATION

Inspect the plugin/WordPress registration behavior and implement the most stable possible registration flow.

If frontend registration URL is configurable, keep it configurable instead of hardcoding it.

DATABASE

Do not directly modify the WordPress database from Selenium tests unless a dedicated test-fixture utility is necessary and clearly isolated.

Prefer testing behavior through UI.

OUTPUT

Implement code, not only a plan.

When finished provide:

1. Exact files created/changed.
2. Brief architecture description.
3. Test scenarios implemented.
4. Scenarios not implemented and the exact reason.
5. Exact commands to run:
   - smoke
   - full regression
   - headed
   - headless
6. Required environment variables.
7. Any selectors that required adding data-testid.
8. Any product defect or automation blocker discovered.

QUALITY BAR

The implementation must compile.

Run Maven compilation/tests that are possible in the environment and fix compile errors before stopping.

Do not invent selectors without checking the repository.

Do not rewrite plugin business logic.

Do not weaken assertions simply to make tests pass.

Treat asynchronous React rendering and WordPress REST operations as first-class synchronization concerns.

Start by analyzing the repository, then implement Phase 1 framework and P0 tests first. Continue through P1 where the environment permits.
```

---

# 26. Suggested First Run Commands

Windows PowerShell:

```powershell
$env:SELENIUM_BASE_URL="http://newuserapprove.local"
$env:SELENIUM_ADMIN_USER="admin"
$env:SELENIUM_ADMIN_PASS="your-password"
$env:SELENIUM_BROWSER="chrome"
$env:SELENIUM_HEADLESS="false"

mvn test
```

Smoke:

```powershell
mvn test -Dgroups=smoke
```

Headless:

```powershell
$env:SELENIUM_HEADLESS="false"
mvn test
```

---

# 27. Final Recommendation

Start with the user approval state machine because it is the core business workflow:

```text
Pending -> Approved
Pending -> Denied
Approved -> Denied
Denied -> Approved
User -> Blocked -> Unblocked
User -> Deleted
```

Once that is stable, build invitation-code tests, settings persistence, and Role Editor.

The most important technical requirement is React-aware synchronization. The plugin makes asynchronous REST requests, so a Selenium suite based on arbitrary sleeps or row indexes will become flaky very quickly.
