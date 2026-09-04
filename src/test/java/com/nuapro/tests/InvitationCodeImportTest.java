package com.nuapro.tests;

import com.nuapro.base.BaseTest;
import com.nuapro.pages.DashboardPage;
import com.nuapro.pages.ImportCodesPage;
import com.nuapro.pages.LoginPage;
import com.nuapro.utils.CsvUtils;
import com.nuapro.utils.FileDownloadUtils;
import com.nuapro.utils.TestData;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class InvitationCodeImportTest extends BaseTest {

    @Test(groups = {"invitation"}, description = "TC027: Import valid CSV invitation codes")
    public void testImportValidCsv() throws IOException {
        LoginPage loginPage = new LoginPage(driver);
        DashboardPage dashboardPage = loginPage.loginAsAdmin();
        dashboardPage.clickInvitationCodesTab();

        ImportCodesPage importPage = new ImportCodesPage(driver).openImportDialog();
        String[] headers = new String[]{"code_title", "uses_limit", "expiry_date"};
        List<String[]> rows = new ArrayList<>();
        rows.add(new String[]{TestData.generateInvitationCode(), "10", "2030-12-31"});

        File csvFile = CsvUtils.createTempCsv(headers, rows);
        importPage.uploadCsvFile(csvFile);
        importPage.clickImport();
    }

    @Test(groups = {"invitation"}, description = "TC029: Download sample CSV template and verify file exists and is non-empty")
    public void testDownloadSampleCsv() throws InterruptedException {
        FileDownloadUtils.cleanDownloadDir();

        LoginPage loginPage = new LoginPage(driver);
        DashboardPage dashboardPage = loginPage.loginAsAdmin();
        dashboardPage.clickInvitationCodesTab();

        ImportCodesPage importPage = new ImportCodesPage(driver).openImportDialog();
        importPage.clickDownloadSampleCsv();

        File downloadedFile = FileDownloadUtils.waitForFileDownload(".csv", 10);
        Assert.assertNotNull(downloadedFile, "Downloaded sample CSV file should exist");
        Assert.assertTrue(downloadedFile.exists() && downloadedFile.length() > 0, "Downloaded CSV file should be non-empty");
    }
}
