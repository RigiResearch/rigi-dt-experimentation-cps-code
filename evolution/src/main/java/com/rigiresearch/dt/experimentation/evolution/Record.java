package com.rigiresearch.dt.experimentation.evolution;

import java.util.HashMap;
import java.util.stream.Collectors;

/**
 * Defines a record of the inputs and outputs for each simulation.
 *
 * @author Felipe Rivera (rivera@uvic.ca)
 * @version $Id$
 * @since 0.1.0
 */
public final class Record extends HashMap<String, Object> {

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = 756532385482446155L;

    /**
     * Allows to get the headers (keys) of a record.
     *
     * @return the headers (keys) of a record.
     */
    public String asCSVHeader() {
        final StringBuilder builder = new StringBuilder();
        if (!this.isEmpty()) {
            boolean firstTime = true;
            for (final String key : this.keySet()) {
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
        final StringBuilder builder = new StringBuilder();
        if (!this.isEmpty()) {
            boolean firstTime = true;
            for (final String key : this.keySet()) {
                final String value = this.get(key).toString();
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

    /**
     * Returns a log-friendly string listing each key:value pair in this record.
     * @return A non-null, possibly empty string
     */
    public String asLog() {
        return this.keySet().stream()
            .map(key -> String.format("%s: %s", key, this.get(key)))
            .collect(Collectors.joining(", "));
    }

}
