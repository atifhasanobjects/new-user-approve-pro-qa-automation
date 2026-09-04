package com.nuapro.pages;

import com.nuapro.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.io.File;

public class ImportCodesPage {

    private final WebDriver driver;
    private final WaitUtils waitUtils;

    private final By openImportDialogButton = By.cssSelector(".invitation_code_subtabs button.addImport");
    private final By importDialog = By.cssSelector(".MuiDialog-root.openNuaTabsModal");
    private final By fileInput = By.cssSelector(".MuiDialog-root.openNuaTabsModal input[name='import-csv'], .MuiDialog-root.openNuaTabsModal input[type='file']");
    private final By importButton = By.cssSelector(".MuiDialog-root.openNuaTabsModal button.importBtn[name='import_csv']");
    private final By downloadSampleCsvButton = By.cssSelector(".MuiDialog-root.openNuaTabsModal button[name='sample_csv']");

    public ImportCodesPage(WebDriver driver) {
        this.driver = driver;
        this.waitUtils = new WaitUtils(driver);
    }

    public ImportCodesPage openImportDialog() {
        waitUtils.clickElement(openImportDialogButton);
        waitUtils.waitForVisibility(importDialog);
        return this;
    }

    public void uploadCsvFile(File csvFile) {
        waitUtils.uploadFile(fileInput, csvFile.getAbsolutePath());
    }

    public void clickImport() {
        waitUtils.clickElement(importButton);
        waitUtils.waitForLoaderToDisappear();
    }

    public void clickDownloadSampleCsv() {
        waitUtils.clickElement(downloadSampleCsvButton);
    }
}
