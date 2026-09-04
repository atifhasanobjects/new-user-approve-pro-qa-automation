package com.nuapro.utils;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class CsvUtils {

    public static File createTempCsv(String[] headers, List<String[]> rows) throws IOException {
        File tempFile = File.createTempFile("nua_invitation_codes_", ".csv");
        tempFile.deleteOnExit();

        try (Writer writer = new FileWriter(tempFile);
             CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.builder().setHeader(headers).build())) {
            for (String[] row : rows) {
                csvPrinter.printRecord((Object[]) row);
            }
            csvPrinter.flush();
        }

        return tempFile;
    }

    public static List<CSVRecord> readCsvRecords(File csvFile) throws IOException {
        List<CSVRecord> records = new ArrayList<>();
        try (Reader reader = new FileReader(csvFile)) {
            Iterable<CSVRecord> iterable = CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).build().parse(reader);
            for (CSVRecord record : iterable) {
                records.add(record);
            }
        }
        return records;
    }
}
