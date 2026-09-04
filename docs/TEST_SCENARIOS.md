# Test Scenarios Matrix — New User Approve Premium

This document details all test cases (TC001 through TC060), their descriptions, preconditions, steps, expected results, priorities, and TestNG groups.

| Test ID | Scenario | Preconditions | Steps | Expected Result | Priority | TestNG Group |
| --- | --- | --- | --- | --- | --- | --- |
| TC001 | Admin login | Admin credentials exist | 1. Open wp-login.php<br>2. Submit admin credentials | WordPress admin dashboard loads | P0 | smoke, auth |
| TC002 | Pending user creation | Frontend registration accessible | 1. Register new frontend user with unique email | User appears in NUA Users table with status Pending | P0 | smoke, users |
| TC003 | Approve pending user | Pending user exists | 1. Locate row by email<br>2. Click Approve<br>3. Wait for table update<br>4. Refresh page | Status becomes Approved and persists after refresh | P0 | smoke, users |
| TC004 | Deny pending user | Pending user exists | 1. Locate row by email<br>2. Click Deny<br>3. Refresh page | Status becomes Denied and persists after refresh | P0 | smoke, users |
| TC005 | Denied user can be approved | Denied user exists | 1. Locate denied user<br>2. Click Approve<br>3. Refresh page | Status updates to Approved | P0 | users |
| TC006 | Approved user can be denied | Approved user exists | 1. Locate approved user<br>2. Click Deny<br>3. Refresh page | Status updates to Denied | P0 | users |
| TC007 | User can be blocked | User exists | 1. Open user action modal/checkbox<br>2. Toggle Block | Status updates to Blocked | P0 | users |
| TC008 | Blocked user can be unblocked | Blocked user exists | 1. Locate blocked user<br>2. Toggle Unblock | User is unblocked | P0 | users |
| TC009 | User can be deleted | User exists | 1. Click Delete icon on row<br>2. Confirm in dialog | User row is removed from table | P0 | users, destructive |
| TC010 | Delete operation can be cancelled | User exists | 1. Click Delete icon<br>2. Click Cancel in dialog | User remains unchanged in table | P0 | users |
| TC011 | Search user | Multiple users exist | 1. Enter query in search box<br>2. Wait for filtered table | Table shows only matching records | P1 | users |
| TC012 | Filter by status | Users in various statuses exist | 1. Switch sub-tabs (Pending, Approved, Denied, Blocked) | Table displays records corresponding to active tab | P1 | users |
| TC013 | Pagination | > 10 users exist | 1. Click next page in pagination | Next page records load cleanly | P1 | users |
| TC014 | Users-per-page selector | > 10 users exist | 1. Select 20 from rows per page dropdown | Table displays up to 20 rows | P1 | users |
| TC015 | User counters | Users exist | 1. View header counters | Counter numbers accurately match table count | P1 | users |
| TC016 | Approval queue position | Pending users exist | 1. View pending queue counter | Queue position displays accurately | P1 | users |
| TC017 | Bulk approve users | Multiple pending users exist | 1. Select user checkboxes<br>2. Click Bulk Approve | All selected users become Approved | P1 | bulk, users |
| TC018 | Bulk deny users | Multiple pending users exist | 1. Select user checkboxes<br>2. Click Bulk Deny | All selected users become Denied | P1 | bulk, users |
| TC019 | Bulk action with no selection | Admin on Users tab | 1. Click Bulk Approve with 0 users checked | Warning message shown, no users modified | P1 | bulk, users |
| TC020 | Create manual invitation code | Admin on Invitation Codes tab | 1. Fill code title & uses limit<br>2. Click Save Code | Code appears in active list | P1 | invitation |
| TC021 | Reject duplicate invitation code | Existing code title | 1. Enter duplicate code title<br>2. Submit | Duplicate validation error displayed | P1 | invitation |
| TC022 | Auto-generate invitation codes | Admin on Auto Codes sub-tab | 1. Enter count=5<br>2. Click Generate Codes | 5 new codes generated and displayed | P1 | invitation |
| TC023 | Edit invitation code | Invitation code exists | 1. Click edit icon<br>2. Update limit<br>3. Save | Updated limit persists | P1 | invitation |
| TC024 | Enable/Disable invitation code | Code exists | 1. Toggle status switch<br>2. Refresh page | Status toggle persists | P1 | invitation |
| TC025 | Delete invitation code | Code exists | 1. Click delete icon<br>2. Confirm | Code is removed from list | P1 | invitation, destructive |
| TC026 | Bulk delete invitation codes | Multiple codes exist | 1. Select checkboxes<br>2. Click Bulk Delete | Selected codes removed | P1 | invitation, bulk |
| TC027 | Import valid CSV | Valid CSV file present | 1. Upload valid codes CSV<br>2. Click Import | CSV codes imported into table | P1 | invitation |
| TC028 | Import invalid CSV | Malformed CSV file | 1. Upload malformed CSV<br>2. Click Import | CSV import error banner shown | P1 | invitation |
| TC029 | Download sample CSV | Admin on Import tab | 1. Click Download Sample CSV link | CSV file downloaded to target/downloads | P1 | invitation |
| TC030 | Remaining invitation code uses | Code with limit=1 exists | 1. Inspect remaining uses counter | Uses count decrements accurately after use | P1 | invitation |
| TC031 | Whitelist domain auto approval | Whitelist configuration | 1. Add domain to whitelist<br>2. Register user with domain | User is auto-approved | P1 | autoapproval |
| TC032 | Non-whitelisted domain behavior | Whitelist active | 1. Register user with non-whitelisted domain | User status is Pending | P1 | autoapproval |
| TC033 | Blacklist domain behavior | Blacklist active | 1. Add domain to blacklist<br>2. Register user with domain | Registration denied/blocked according to rule | P1 | autoapproval |
| TC034 | Auto approval by user role | Role rule active | 1. Enable auto-approve for Role X<br>2. Register user as Role X | User status is Approved | P1 | autoapproval |
| TC035 | Registration deadline | Deadline configured | 1. Set deadline<br>2. Register user | Registration respects deadline boundary | P1 | autoapproval |
| TC036 | Auto denial duration | Duration set | 1. Configure auto-deny after duration | Pending user auto-denies after duration | P1 | autoapproval |
| TC037 | General settings persistence | Admin on Settings tab | 1. Modify general toggle<br>2. Click Save<br>3. Refresh | Modified setting value persists | P1 | settings |
| TC038 | Registration settings persistence | Admin on Registration tab | 1. Change registration option<br>2. Save & Refresh | Setting persists | P1 | settings |
| TC039 | Deadline picker persistence | Registration tab | 1. Change deadline date/time<br>2. Save & Refresh | Date/time value persists | P1 | settings |
| TC040 | Admin notification settings | Admin Notif tab | 1. Update email subject/body<br>2. Save & Refresh | Email content persists | P1 | settings |
| TC041 | User notification settings | User Notif tab | 1. Update user approval template<br>2. Save & Refresh | Template text persists | P1 | settings |
| TC042 | Notification recipient roles | Admin Notif tab | 1. Select recipient roles<br>2. Save & Refresh | Role selections persist | P1 | settings |
| TC043 | Approval email template | Email tab | 1. Update approval template<br>2. Save & Refresh | Template persists | P1 | settings |
| TC044 | Denial email template | Email tab | 1. Update denial template<br>2. Save & Refresh | Template persists | P1 | settings |
| TC045 | HTML template mode | Email tab | 1. Toggle HTML mode<br>2. Save & Refresh | HTML mode state persists | P1 | settings |
| TC046 | Create custom role | Role Editor tab | 1. Click Add Role<br>2. Enter name & caps<br>3. Save | Custom role appears in roles table | P1 | roles |
| TC047 | Duplicate role validation | Role exists | 1. Try creating role with duplicate name | Duplicate role error shown | P1 | roles |
| TC048 | Edit custom role | Custom role exists | 1. Click Edit role<br>2. Update capabilities<br>3. Save | Updated role caps persist | P1 | roles |
| TC049 | Delete role | Custom role exists | 1. Click Delete role<br>2. Confirm | Custom role removed from table | P1 | roles, destructive |
| TC050 | Cancel role deletion | Custom role exists | 1. Click Delete<br>2. Cancel in modal | Role remains in table | P1 | roles |
| TC051 | Add capability to role | Role Editor | 1. Check capability box<br>2. Save role | Capability added to role | P1 | roles |
| TC052 | Remove capability from role | Role Editor | 1. Uncheck capability box<br>2. Save role | Capability removed from role | P1 | roles |
| TC053 | Bulk capability selection | Role Editor | 1. Click Select All capabilities | All capability checkboxes selected | P1 | roles |
| TC054 | Sort Role column | Roles list | 1. Click Role column header | Table sorts by Role name | P1 | roles |
| TC055 | Sort Users column | Roles list | 1. Click Users column header | Table sorts by User count | P1 | roles |
| TC056 | Sort Capabilities column | Roles list | 1. Click Capabilities header | Table sorts by Capabilities count | P1 | roles |
| TC057 | React dashboard load | Logged in as Admin | 1. Open plugin main page | Main dashboard renders with zero permanent loader or errors | P0 | smoke, navigation |
| TC058 | Navigate main plugin sections | Logged in as Admin | 1. Click each main React tab in sequence | Correct component renders for each tab | P0 | navigation |
| TC059 | Direct hash route navigation | Logged in as Admin | 1. Direct navigate to #/action=settings/tab=general | React renders Settings page directly | P0 | navigation |
| TC060 | WooCommerce integration group | WooCommerce active | 1. Register user via WooCommerce checkout | NUA workflow intercepts WooCommerce user | P2 | integration, woocommerce |
