package com.rigiresearch.dt.experimentation.evolution.genetic.util;

import com.rigiresearch.dt.experimentation.evolution.Record;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * Utils for reading and writing CSV files.
 *
 * @author Felipe Rivera (rivera@uvic.ca)
 * @version $Id$
 * @since 0.1.0
 */
public final class CSVUtil {
    /**
     * Allows to create a CSV file for a list of records.
     * @param canonicalPath The path of the generated CSV file.
     * @param recordList The list of records to be converted to CSV.
     * @throws IOException if there is an error accesing the file.
     */
    public static void writeCSV(String canonicalPath, List<Record> recordList) throws IOException {
        if(canonicalPath != null && recordList != null) {
            if(canonicalPath.length() > 0 && recordList.size() > 0) {
                FileWriter fileWriter = new FileWriter(canonicalPath);
                PrintWriter printWriter = new PrintWriter(fileWriter);
                printWriter.println(recordList.get(0).asCSVHeader());
                for(Record record : recordList){
                    printWriter.println(record.asCSVRecord());
                }
                printWriter.flush();
                printWriter.close();
            }
        }
    }

}
