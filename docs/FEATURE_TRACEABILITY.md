# React Code & Backend Feature Traceability Matrix

This document maps all core features of **New User Approve Premium** from the ReactJS front-end UI down through event handlers, JS API functions, REST/AJAX endpoints, PHP backend logic, WordPress database state, and Selenium automated test verification.

---

## Traceability Table

| Feature | React Component | React Function / Event Handler | `src/functions.js` Function | Backend Endpoint / Action | PHP Handler File | Selenium Test Class & Method |
| --- | --- | --- | --- | --- | --- | --- |
| **Approve Pending User** | `pending-users.jsx` | `handleMenuAction` | `update_user_status` | `POST /nua-api/v1/update-users` (`update_users`) | `includes/end-points/users-api.php` (`update_users_status()`) | `UserApprovalTest.testApprovePendingUser` |
| **Deny Pending User** | `pending-users.jsx` | `handleMenuAction` | `update_user_status` | `POST /nua-api/v1/update-users` (`update_users`) | `includes/end-points/users-api.php` (`update_users_status()`) | `UserApprovalTest.testDenyPendingUser` |
| **Denied -> Approved Transition** | `denied-users.jsx` | `handleMenuAction` | `update_user_status` | `POST /nua-api/v1/update-users` (`update_users`) | `includes/end-points/users-api.php` (`update_users_status()`) | `UserApprovalTest.testDeniedUserCanBeApproved` |
| **Approved -> Denied Transition** | `approved-users.jsx` | `handleMenuAction` | `update_user_status` | `POST /nua-api/v1/update-users` (`update_users`) | `includes/end-points/users-api.php` (`update_users_status()`) | `UserApprovalTest.testApprovedUserCanBeDenied` |
| **Block User** | `pending-users.jsx` | `handleOpenDeleteModal`, `handleBlockClick` | `delete_nua_user` | `POST /nua-api/v1/delete-user` (`delete_nua_user`) | `includes/end-points/users-api.php` (`delete_user()`) | `UserBlockDeleteTest.testBlockUser` |
| **Delete User** | `pending-users.jsx` | `handleCloseDeleteModal` | `delete_nua_user` | `POST /nua-api/v1/delete-user` (`delete_nua_user`) | `includes/end-points/users-api.php` (`delete_user()`) | `UserBlockDeleteTest.testDeleteUser` |
| **Bulk Approve Users** | `pending-users.jsx` | `handleBulk("approve")` | `update_user_status` | `POST /nua-api/v1/update-users` (`update_users`) | `includes/end-points/users-api.php` (`update_users_status()`) | `BulkUserActionTest.testBulkApproveUsers` |
| **Bulk Deny Users** | `pending-users.jsx` | `handleBulk("deny")` | `update_user_status` | `POST /nua-api/v1/update-users` (`update_users`) | `includes/end-points/users-api.php` (`update_users_status()`) | `BulkUserActionTest.testBulkDenyUsers` |
| **Auto Approve Whitelist** | `auto-approve.jsx` | `handleToggleChange`, `handleSubmit` | `update_domains_list` | `POST /nua-api/v1/update-whitelist-domains` | `includes/end-points/settings-api.php` (`update_whitelist_domains()`) | `AutoApprovalTest.testWhitelistDomainAutoApproval` |
| **Auto Approve Blacklist** | `auto-approve.jsx` | `handleToggleChange`, `handleSubmit` | `update_domains_list` | `POST /nua-api/v1/update-blacklist-domains` | `includes/end-points/settings-api.php` (`update_blacklist_domains()`) | `AutoApprovalTest.testBlacklistDomainBehavior` |
| **Manual Invitation Code** | `add-code-subtabs.jsx` | `handleSubmit` | `save_invitation_codes` | `POST /nua-api/v1/save-invitation-codes` | `includes/end-points/invitation-code-api.php` (`save_invitation_codes()`) | `InvitationCodeTest.testCreateManualInvitationCode` |
| **Auto-Generate Invitation Codes** | `add-code-subtabs.jsx` | `handleGenerateAutoCodes` | `save_invitation_auto_codes` | `POST /nua-api/v1/save-invitation-auto-codes` | `includes/end-points/invitation-code-api.php` (`save_auto_invitation_codes()`) | `InvitationCodeTest.testAutoGenerateInvitationCodes` |
| **Delete Invitation Code** | `add-code-subtabs.jsx` | `handleDeleteCode` | `delete_invCode` | `POST /nua-api/v1/delete-invitation-code` | `includes/end-points/invitation-code-api.php` (`delete_invitation_code()`) | `InvitationCodeTest.testDeleteInvitationCode` |
| **Import Invitation CSV** | `import-codes.jsx` | `handleCsvImport` | `import_csv_codes` | `POST /nua-api/v1/import-csv-codes` | `includes/end-points/invitation-code-api.php` (`import_csv_codes()`) | `InvitationCodeImportTest.testImportValidCsv` |
| **Download Sample CSV** | `import-codes.jsx` | Link direct click | browser `fetch()` | `GET /nua-api/v1/sample-code-csv` | `includes/end-points/invitation-code-api.php` (`download_sample_csv()`) | `InvitationCodeImportTest.testDownloadSampleCsv` |
| **General Settings Save** | `general.jsx` | `handleSaveChange` | `update_general_settings` | `POST /nua-api/v1/update-general-settings` | `includes/end-points/settings-api.php` (`update_general_settings()`) | `SettingsTest.testGeneralSettingsPersistence` |
| **Registration Settings Save** | `registration.jsx` | `handleSaveChange` | `update_registration_settings` | `POST /nua-api/v1/update-registration-settings` | `includes/end-points/settings-api.php` (`update_registration_settings()`) | `SettingsTest.testRegistrationSettingsPersistence` |
| **Admin Notification Settings** | `admin-notification.jsx` | `handleSaveChange` | `update_admin_notification_settings` | `POST /nua-api/v1/update-admin-notification-settings` | `includes/end-points/settings-api.php` (`update_admin_notification_settings()`) | `SettingsTest.testAdminNotificationSettingsPersistence` |
| **User Notification Settings** | `user-notification.jsx` | `handleSaveChange` | `update_user_notification_settings` | `POST /nua-api/v1/update-user-notification-settings` | `includes/end-points/settings-api.php` (`update_user_notification_settings()`) | `SettingsTest.testUserNotificationSettingsPersistence` |
| **Create Custom Role** | `role-editor.jsx` | `handleSaveRole` | `update_user_role` | `POST /nua-api/v1/update-user-role` | `includes/end-points/role-editor-api.php` (`update_user_role()`) | `RoleEditorTest.testCreateCustomRole` |
| **Delete Role** | `roles-list.jsx` | `handleDeleteRole` | `delete_user_role` | `POST /nua-api/v1/delete-user-role` | `includes/end-points/role-editor-api.php` (`delete_user_role()`) | `RoleEditorTest.testDeleteRole` |
| **Sort Role Columns** | `roles-list.jsx` | Header `onClick` | state sort | Client-side React state sort | `roles-list.jsx` | `RoleEditorTest.testSortRoleColumn` |

---

## Data & Behavioral Flow Example: Auto Approval

```text
React Component (auto-approve.jsx)
        ↓
React Handler (handleToggleChange & handleSubmit)
        ↓
src/functions.js (update_domains_list({ endPoint, domainData }))
        ↓
REST Endpoint (POST /nua-api/v1/update-whitelist-domains)
        ↓
PHP Handler (includes/end-points/settings-api.php -> update_whitelist_domains())
        ↓
Database (wp_options -> nua_whitelist_domains)
        ↓
Frontend User Registration Flow (pw-new-user-approve.php -> pw_new_user_approve_auto_approve())
        ↓
Selenium Automated Verification (AutoApprovalFunctionalTest.java)
```
