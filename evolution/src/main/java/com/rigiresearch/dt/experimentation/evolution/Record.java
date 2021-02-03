package com.rigiresearch.dt.experimentation.evolution;

import java.util.HashMap;

/**
 * Defines a record of the inputs and outputs for each simulation.
 *
 * @author Felipe Rivera (rivera@uvic.ca)
 * @version $Id$
 * @since 0.1.0
 */
public class Record extends HashMap<String, Object> {

    /**
     * Allows to get the headers (keys) of a record.
     *
     * @return the headers (keys) of a record.
     */
    public String asCSVHeader() {
        StringBuilder builder = new StringBuilder();
        if (!isEmpty()) {
            boolean firstTime = true;
            for (String key : keySet()) {
                if (firstTime) {
                    builder.append(key);
                    firstTime = false;
                } else {
                    builder.append(",".concat(key));
                }
            }
        }
        return builder.toString();
    }

    /**
     * Allows to obtain the record in a CSV format.
     *
     * @return the record in a CSV format.
     */
    public String asCSVRecord() {
        StringBuilder builder = new StringBuilder();
        if (!isEmpty()) {
            boolean firstTime = true;
            for (String key : keySet()) {
                String value = get(key).toString();
                if (firstTime) {
                    builder.append(value);
                    firstTime = false;
                } else {
                    builder.append(",".concat(value));
                }
            }
        }
        return builder.toString();
    }

}
