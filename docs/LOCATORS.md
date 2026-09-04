# Locators Map — New User Approve Premium

This document details all UI elements, their purpose, locator type, exact locator expression, and the rendered attributes used for Selenium WebDriver automation.

## 1. WordPress Authentication

| Page | Element | Purpose | Locator Type | Locator | Source File | Notes |
| --- | --- | --- | --- | --- | --- | --- |
| Login | Username Input | Enter admin username | id / name | `user_login` | WordPress core | Stable |
| Login | Password Input | Enter admin password | id / name | `user_pass` | WordPress core | Stable |
| Login | Submit Button | Submit login form | id | `wp-submit` | WordPress core | Stable |
| Login | Error Message | Verify login failure | id | `login_error` | WordPress core | Conditional |

---

## 2. Dashboard Header & Navigation Tabs

| Page | Element | Purpose | Locator Type | Locator | Source File | Notes |
| --- | --- | --- | --- | --- | --- | --- |
| Header | Topbar Title | Verify plugin header | CSS | `.nua-dash-header h1, .topbar_title` | `topbar.jsx` | React root |
| Navigation | Dashboard Tab | Navigate to Dashboard main | CSS / XPath | `button[dataTarget='dashboard']` | `tabs.jsx` | React Tab |
| Navigation | Users Tab | Navigate to Users tab | CSS / XPath | `button[datatarget='users']` | `tabs.jsx` | React Tab |
| Navigation | Role Editor Tab | Navigate to Role Editor | CSS / XPath | `button[value='action=role-editor']` | `tabs.jsx` | React Tab |
| Navigation | Invitation Code Tab | Navigate to Invitation Codes | CSS / XPath | `button[datatarget='inv code']` | `tabs.jsx` | React Tab |
| Navigation | Auto Approve Tab | Navigate to Auto Approve | CSS / XPath | `button[datatarget='auto approve']` | `tabs.jsx` | React Tab |
| Navigation | Integrations Tab | Navigate to Integrations | CSS / XPath | `button[value='action=integrations']` | `tabs.jsx` | React Tab |
| Navigation | Mobile App Tab | Navigate to Mobile App | CSS / XPath | `button[value='action=mobile-app']` | `tabs.jsx` | React Tab |
| Navigation | Settings Tab | Navigate to Settings | CSS / XPath | `button[value='action=settings']` | `tabs.jsx` | React Tab |

Automation note: `LoginPage.loginAsAdmin()` explicitly opens the NUA plugin dashboard and waits for the React dashboard before page-object navigation begins. Tests must not assume that the WordPress login redirect lands on the plugin page.

Main-tab selectors use the existing rendered `value`/`datatarget` attributes first and visible tab text as a fallback. If a failure reports `matches=0`, inspect the diagnostic URL, title, and body text to distinguish a WordPress redirect, missing capability, stale plugin assets, or a selector regression.

---

## 3. Auto Approval (`#/action=auto-approve/*`)

| Page | Element | Purpose | Locator Type | Locator | Source File | Notes |
| --- | --- | --- | --- | --- | --- | --- |
| Auto Approve | Whitelist Tab | Switch to Whitelist view | XPath | `//button[contains(text(),'WhiteList') or contains(text(),'Whitelist')]` | `auto-approve.jsx` | Tab |
| Auto Approve | Blacklist Tab | Switch to Blacklist view | XPath | `//button[contains(text(),'BlackList') or contains(text(),'Blacklist')]` | `auto-approve.jsx` | Tab |
| Auto Approve | Whitelist Enable Toggle | Enable/Disable Whitelist | CSS | `input#isWhitelistEnabled` (input), `label[for='isWhitelistEnabled'] .nua_slider` (click) | `auto-approve.jsx` | Toggle Switch |
| Auto Approve | Blacklist Enable Toggle | Enable/Disable Blacklist | CSS | `input#isBlacklistEnabled` (input), `label[for='isBlacklistEnabled'] .nua_slider` (click) | `auto-approve.jsx` | Toggle Switch |
| Auto Approve | Whitelist Domains Textarea | Domains for auto approval | CSS | `textarea#whitelist, textarea[name='whitelist']` | `auto-approve.jsx` | Textarea |
| Auto Approve | Blacklist Domains Textarea | Domains to block/deny | CSS | `textarea#blacklist, textarea[name='blacklist']` | `auto-approve.jsx` | Textarea |
| Auto Approve | Custom Blacklist Message | Blocked registration notice | CSS | `input[name='customMessage']` | `auto-approve.jsx` | Form Input |
| Auto Approve | Save Changes Button | Save Whitelist/Blacklist rules | CSS | `button.auto-approve-save-btn, button.save-changes` | `auto-approve.jsx` | Button |

---

## 4. Settings (`#/action=settings/*`)

| Page | Element | Purpose | Locator Type | Locator | Source File | Notes |
| --- | --- | --- | --- | --- | --- | --- |
| Settings | General Tab | Switch to General settings | XPath | `//button[contains(text(),'General') or @value='tab=general']` | `general.jsx` | Sub-tab |
| Settings | Registration Tab | Switch to Registration settings | XPath | `//button[contains(text(),'Registration') or @value='tab=registration']` | `general.jsx` | Sub-tab |
| Settings | Admin Notification Tab | Switch to Admin Notification | XPath | `//button[contains(text(),'Admin Notification') or @value='tab=admin_notification']` | `general.jsx` | Sub-tab |
| Settings | User Notification Tab | Switch to User Notification | XPath | `//button[contains(text(),'User Notification') or @value='tab=user_notification']` | `general.jsx` | Sub-tab |
| Settings | Auto Approve Toggle | Enable/Disable Auto Approve | CSS | `input#nua_enable_auto_approve` (input), `label[for='nua_enable_auto_approve'] .nua_slider` (click) | `general.jsx` | Toggle Switch |
| Settings | Enable Invitation Code Toggle | Enable/Disable Invitation Code | CSS | `input#nua_enable_invitation_code` (input), `label[for='nua_enable_invitation_code'] .nua_slider` (click) | `general.jsx` | Toggle Switch |
| Settings | Required Invitation Code Toggle | Make Code Required | CSS | `input#nua_make_invitation_code_required` (input), `label[for='nua_make_invitation_code_required'] .nua_slider` (click) | `general.jsx` | Toggle Switch |
| Settings | Admin Email Address | Set sender email | CSS | `input#nua_admin_email_address` | `general.jsx` | Text Input |
| Settings | Save Changes Button | Save Settings | CSS | `button.save-changes, .setting-save-btn button` | `general.jsx` | Button |
