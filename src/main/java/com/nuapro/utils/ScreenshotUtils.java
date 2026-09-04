package com.nuapro.utils;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ScreenshotUtils {

    public static String captureFailureArtifacts(WebDriver driver, String testName) {
        if (driver == null) return null;

        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String dirPath = "target/test-artifacts/" + testName + "_" + timestamp;

        try {
            Path pathDir = Paths.get(dirPath);
            Files.createDirectories(pathDir);

            // Screenshot
            File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            File destScreenshot = new File(dirPath + "/screenshot.png");
            Files.copy(screenshot.toPath(), destScreenshot.toPath());

            // Page source
            String pageSource = driver.getPageSource();
            File destSource = new File(dirPath + "/page_source.html");
            try (FileOutputStream fos = new FileOutputStream(destSource)) {
                fos.write(pageSource.getBytes());
            }

            // Info details
            String currentUrl = driver.getCurrentUrl();
            String info = "Test: " + testName + "\nURL: " + currentUrl + "\nTimestamp: " + timestamp + "\n";
            File destInfo = new File(dirPath + "/info.txt");
            try (FileOutputStream fos = new FileOutputStream(destInfo)) {
                fos.write(info.getBytes());
            }

            return destScreenshot.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
