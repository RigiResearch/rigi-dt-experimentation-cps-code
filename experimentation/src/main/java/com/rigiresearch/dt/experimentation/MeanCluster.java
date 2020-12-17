package com.rigiresearch.dt.experimentation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;

/**
 * Creates a cluster of groups based on mean.
 * @author Miguel Jimenez (miguel@uvic.ca)
 * @version $Id$
 * @since 0.1.0
 */
@RequiredArgsConstructor
public final class MeanCluster {

    /**
     * The input data.
     */
    private final Map<String, Double[]> data;

    /**
     * The alpha level.
     */
    private final double alpha;

    /**
     * Function to evaluate whether the Null hypothesis should be rejected;
     */
    private final Function<Double, Boolean> evaluation;

    /**
     * Iterate the P values and comparisons and builds the clusters.
     * @param values The adjusted P values
     * @param comparisons The comparisons
     * @return A non-null, possibly empty map
     */
    public Map<Mean, List<String>> result(final double[] values,
        final String[][] comparisons) {
        final Map<Mean, List<String>> map = new HashMap<>(comparisons.length);
        final List<List<String>> clusters = this.clusters(values, comparisons);
        for (final List<String> cluster : clusters) {
            int size = 0;
            for (final String group : cluster) {
                size += this.data.get(group).length;
            }
            final Double[] concatenation = new Double[size];
            for (int j = 0, start = 0; j < cluster.size(); j++) {
                final Double[] observations = this.data.get(cluster.get(j));
                System.arraycopy(observations, 0, concatenation, start, observations.length);
                start += observations.length;
            }
            map.put(new Mean(concatenation, this.alpha), cluster);
        }
        return map;
    }

    /**
     * Iterates over the comparisons and builds the clusters based on the P values.
     * @param values The adjusted P values
     * @param comparisons The comparisons
     * @return A non-null, possibly empty list
     */
    private List<List<String>> clusters(final double[] values,
        final String[][] comparisons) {
        final List<List<String>> clusters = new ArrayList<>(comparisons.length);
        for (int i = 0; i < comparisons.length; i++) {
            final String[] groups = comparisons[i];
            final double value = values[i];
            // Null hypothesis: there is no difference between groups
            if (this.evaluation.apply(value)) {
                // Reject the null hypothesis (the means are different)
                MeanCluster.addToClusters(groups[0], clusters);
                MeanCluster.addToClusters(groups[1], clusters);
            } else {
                // Accept the null hypothesis
                final Optional<List<String>> optional0 =
                    MeanCluster.findInClusters(groups[0], clusters);
                if (optional0.isPresent()) {
                    optional0.get().add(groups[1]);
                } else {
                    final Optional<List<String>> optional1 =
                        MeanCluster.findInClusters(groups[1], clusters);
                    if (optional1.isPresent()) {
                        optional1.get().add(groups[0]);
                    } else {
                        final List<String> cluster =
                            MeanCluster.addToClusters(groups[0], clusters);
                        cluster.add(groups[1]);
                    }
                }
            }
        }
        return clusters;
    }

    /**
     * Adds the given group if it wasn't added before.
     * @param group The group
     * @param clusters Existing cluster
     * @return The group's cluster
     */
    private static List<String> addToClusters(final String group,
        final List<List<String>> clusters) {
        final Optional<List<String>> optional =
            MeanCluster.findInClusters(group, clusters);
        final List<String> cluster;
        if (optional.isPresent()) {
            cluster = optional.get();
            // Do nothing because it is already in the cluster
        } else {
            cluster = new ArrayList<>();
            cluster.add(group);
            clusters.add(cluster);
        }
        return cluster;
    }

    /**
     * Finds the cluster containing the given group.
     * @param group The group
     * @param clusters Existing clusters
     * @return The cluster if it exists, empty otherwise
     */
    private static Optional<List<String>> findInClusters(final String group,
        final List<List<String>> clusters) {
        for (int i = 0; i < clusters.size(); i++) {
            final List<String> cluster = clusters.get(i);
            if (cluster.contains(group)) {
                return Optional.of(cluster);
            }
        }
        return Optional.empty();
    }

}
