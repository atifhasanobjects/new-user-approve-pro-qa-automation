package com.nuapro.utils;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

public class FileDownloadUtils {

    public static void cleanDownloadDir() {
        File dir = new File("target/downloads");
        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File f : files) {
                    f.delete();
                }
            }
        }
    }

    public static File waitForFileDownload(String fileExtension, int timeoutSeconds) throws InterruptedException {
        File dir = new File("target/downloads");
        long endTime = System.currentTimeMillis() + (timeoutSeconds * 1000L);

        while (System.currentTimeMillis() < endTime) {
            if (dir.exists() && dir.isDirectory()) {
                File[] files = dir.listFiles((d, name) -> name.endsWith(fileExtension) && !name.endsWith(".crdownload") && !name.endsWith(".tmp"));
                if (files != null && files.length > 0) {
                    Arrays.sort(files, Comparator.comparingLong(File::lastModified).reversed());
                    File newestFile = files[0];
                    if (newestFile.exists() && newestFile.length() > 0) {
                        return newestFile;
                    }
                }
            }
            Thread.sleep(500);
        }
        return null;
    }
}
