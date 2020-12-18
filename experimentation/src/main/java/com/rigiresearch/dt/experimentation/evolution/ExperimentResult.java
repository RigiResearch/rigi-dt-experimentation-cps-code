package com.rigiresearch.dt.experimentation.evolution;

import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * The result of an experiment.
 * @author Miguel Jimenez (miguel@uvic.ca)
 * @version $Id$
 * @since 0.1.0
 */
@Accessors(fluent = true)
@Getter
@Builder
public final class ExperimentResult {

    /**
     * Whether each group is normally distributed.
     */
    private final Map<String, Boolean> normal;

    /**
     * Mean data grouped by group id.
     */
    private final Map<String, Mean> means;

    /**
     * Group ids grouped by mean.
     * <p>These clusters represent groups whose means are similar. That is,
     * their difference is not statistically significant. The averages used as
     * key are the average of the groups' averages.</p>
     */
    private final Map<Mean, Set<String>> clusters;

    /**
     * Finds the best performing cluster based on the average.
     * Note that this method assumes that the best cluster is the one with the
     * smallest average.
     * @return A possibly empty result
     */
    public Optional<Map.Entry<Mean, Set<String>>> bestCluster() {
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
        final Optional<Map.Entry<Mean, Set<String>>> best =
            this.bestCluster();
        ExperimentResult.appendMap(
            this.normal, builder, "Normal distribution", "Group", "Normally distributed?");
        ExperimentResult.appendTitle(builder, "Best Performant Cluster");
        best.ifPresent(entry ->
            ExperimentResult
                .appendEntry(builder, entry, "Mean", "Cluster")
        );
        ExperimentResult.appendMap(this.means, builder, "Means", "Group", "Mean");
        ExperimentResult.appendMap(this.clusters, builder, "Clusters", "Mean", "Cluster");
        return builder.toString();
    }

    /**
     * Renders and append the given map to the String builder.
     * @param data The map to render
     * @param builder The String builder
     * @param title The title to render before the data
     * @param key The label associated with the key element
     * @param value The label associated with the value element
     */
    private static void appendMap(final Map<?, ?> data, final StringBuilder builder,
        final String title, final String key, final String value) {
        ExperimentResult.appendTitle(builder, title);
        data.entrySet()
            .forEach(entry ->
                ExperimentResult
                    .appendEntry(builder, entry, key, value)
            );
    }

    /**
     * Renders a title to a heading-like string and appends it to the String builder.
     * @param builder The String builder to append te resulting string
     * @param title The title to render
     */
    private static void appendTitle(final StringBuilder builder,
        final String title) {
        builder.append('\n');
        builder.append(title.toUpperCase(Locale.getDefault()));
        builder.append('\n');
        builder.append("=".repeat(title.length()));
        builder.append('\n');
    }

    /**
     * Renders a map entry to a String and appends it to the String builder.
     * @param builder The String builder to append te resulting string
     * @param entry The map entry to render
     * @param key The label associated with the entry's key
     * @param value The label associated with the entry's value
     */
    private static void appendEntry(final StringBuilder builder,
        final Map.Entry<?, ?> entry, final String key, final String value) {
        builder.append(key);
        builder.append(": ");
        builder.append(entry.getKey());
        builder.append('\t');
        builder.append(value);
        builder.append(": ");
        builder.append(entry.getValue());
        builder.append('\n');
    }
}
