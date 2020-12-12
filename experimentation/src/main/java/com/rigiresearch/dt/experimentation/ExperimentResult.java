package com.rigiresearch.dt.experimentation;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.Builder;

/**
 * The result of an experiment.
 * @author Miguel Jimenez (miguel@uvic.ca)
 * @version $Id$
 * @since 0.1.0
 */
@Builder
public final class ExperimentResult {

    /**
     * Average data grouped by group id.
     */
    private final Map<String, Double> averages;

    /**
     * Group ids grouped by average.
     * <p>These clusters represent groups whose means are similar. That is,
     * their difference is not statistically significant. The averages used as key are the average
     * of the groups' averages.</p>
     */
    private final Map<Double, List<String>> clusters;

    /**
     * Finds the best performing cluster based on the average. Note that this method assumes that
     * the best cluster is the one with the smallest average.
     * @return A possibly empty result
     */
    public Optional<Map.Entry<Double, List<String>>> bestCluster() {
        return this.clusters.entrySet()
            .stream()
            .min(Map.Entry.comparingByKey());
    }

    /**
     * Computes a textual representation of this experiment result.
     * @return A non-null, non-empty string
     */
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        final Optional<Map.Entry<Double, List<String>>> best = this.bestCluster();
        ExperimentResult.toString(builder, "Best Performant Cluster");
        best.ifPresent(entry ->
            ExperimentResult
                .toString(builder, entry, "Average", "Groups")
        );
        ExperimentResult.toString(builder, "Averages");
        this.averages.entrySet()
            .forEach(entry ->
                ExperimentResult
                    .toString(builder, entry, "Group", "Average")
            );
        ExperimentResult.toString(builder, "Clusters");
        this.clusters.entrySet()
            .forEach(entry ->
                ExperimentResult
                    .toString(builder, entry, "Average", "Groups")
            );
        return builder.toString();
    }

    /**
     * Renders a title to a heading.
     * @param builder The String builder to append te resulting string
     * @param title The title to render
     */
    private static void toString(final StringBuilder builder,
        final String title) {
        builder.append(title);
        builder.append('\n');
        for (int counter = 0; counter < title.length(); counter++) {
            builder.append('-');
        }
        builder.append('\n');
    }

    /**
     * Renders a map entry to a String.
     * @param builder The String builder to append te resulting string
     * @param entry The map entry to render
     * @param key The label associated with the entry's key
     * @param value The label associated with the entry's value
     */
    private static void toString(final StringBuilder builder,
        final Map.Entry<?, ?> entry, final String key, final String value) {
        builder.append(key);
        builder.append(": ");
        builder.append(entry.getKey());
        builder.append('\n');
        builder.append(value);
        builder.append(": ");
        builder.append(entry.getValue());
        builder.append('\n');
        builder.append("--");
        builder.append('\n');
    }
}
