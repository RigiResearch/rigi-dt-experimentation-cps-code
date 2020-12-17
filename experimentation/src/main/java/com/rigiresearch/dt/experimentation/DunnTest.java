package com.rigiresearch.dt.experimentation;

import com.github.rcaller.datatypes.DataFrame;
import com.github.rcaller.rstuff.RCaller;
import com.github.rcaller.rstuff.RCode;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of Dunn's test using a bridge to R.
 * @author Miguel Jimenez (miguel@uvic.ca)
 * @version $Id$
 * @since 0.1.0
 */
@RequiredArgsConstructor
public final class DunnTest {

    /**
     * The logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(DunnTest.class);

    /**
     * Label associated with the group name in the dataframe.
     */
    private static final String GROUP = "group";

    /**
     * Label associated with observed values in the dataframe.
     */
    private static final String OBSERVATION = "observation";

    /**
     * The variable name for adjusted P values.
     */
    private static final String VALUES = "values";

    /**
     * The variable name for the comparisons array.
     */
    private static final String COMPARISONS = "comparisons";

    /**
     * The variabe name for the differences array.
     */
    private static final String DIFFERENCES = "differences";

    /**
     * Pattern to replace in the output.
     */
    private static final Pattern PATTERN_OUTPUT = Pattern.compile("Output:");

    /**
     * Pattern to separate comparisons.
     */
    private static final Pattern PATTERN_COMP = Pattern.compile(" - ");

    /**
     * The input data.
     */
    private final Map<String, Double[]> data;

    /**
     * The alpha level for accepting or rejecting the null hypothesis.
     */
    private final double alpha;

    /**
     * Run Dunn's test and return the resulting clusters.
     * @return A non-null, possibly empty map
     */
    public Map<Mean, List<String>> test() {
        final OutputStream useless = new ByteArrayOutputStream();
        final OutputStream output = new ByteArrayOutputStream();
        // We run the test twice to get the adjusted P values and the comparisons
        // This is a limitation of the RCaller library
        final double[] values = this.test0(DunnTest.VALUES, useless)
            .getParser().getAsDoubleArray(DunnTest.VALUES);
        final String[] comparisons = this.test0(DunnTest.COMPARISONS, output)
            .getParser().getAsStringArray(DunnTest.COMPARISONS);
        DunnTest.LOGGER.debug("\n{}", DunnTest.PATTERN_OUTPUT
            .matcher(output.toString())
            .replaceAll(""));
        return this.result(values, comparisons);
    }

    /**
     * Iterate the P values and comparisons and builds the clusters.
     * @param values The adjusted P values
     * @param comparisons The comparisons
     * @return A non-null, possibly empty map
     */
    private Map<Mean, List<String>> result(final double[] values,
        final String[] comparisons) {
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
        final String[] comparisons) {
        final List<List<String>> clusters = new ArrayList<>(comparisons.length);
        for (int i = 0; i < comparisons.length; i++) {
            final String[] groups = DunnTest.PATTERN_COMP.split(comparisons[i]);
            final double value = values[i];
            // Null hypothesis: there is no difference between groups
            if (value <= this.alpha/2.0) {
                // Reject the null hypothesis (the means are different)
                DunnTest.addToClusters(groups[0], clusters);
                DunnTest.addToClusters(groups[1], clusters);
            } else {
                // Accept the null hypothesis
                final Optional<List<String>> optional0 =
                    DunnTest.findInClusters(groups[0], clusters);
                if (optional0.isPresent()) {
                    optional0.get().add(groups[1]);
                } else {
                    final Optional<List<String>> optional1 =
                        DunnTest.findInClusters(groups[1], clusters);
                    if (optional1.isPresent()) {
                        optional1.get().add(groups[0]);
                    } else {
                        final List<String> cluster =
                            DunnTest.addToClusters(groups[0], clusters);
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
            DunnTest.findInClusters(group, clusters);
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

    /**
     * Runs Dunn's test.
     * @param variable The variable name to return from R
     * @param output A stream to append the output
     * @return The RCaller instance
     */
    private RCaller test0(final String variable, final OutputStream output) {
        this.preconditions();
        final RCaller caller = RCaller.create();
        final RCode code = RCode.create();
        code.R_require("dunn.test");
        code.addDataFrame("df", this.dataframe());
        code.addRCode("attach(df)");
        code.addRCode(
            String.format(
                "result <- dunn.test(%s, %s, method=\"%s\", list=TRUE)",
                DunnTest.OBSERVATION,
                DunnTest.GROUP,
                "hochberg"
            )
        );
        code.addRCode(String.format("%s <- result$comparisons", DunnTest.COMPARISONS));
        code.addRCode(String.format("%s <- result$P.adjust", DunnTest.VALUES));
        code.addRCode(String.format("%s <- result$Z", DunnTest.DIFFERENCES));
        caller.setRCode(code);
        caller.redirectROutputToStream(output);
        caller.runAndReturnResult(variable);
        return caller;
    }

    /**
     * Evaluates preconditions for the experiment analysis to work.
     */
    private void preconditions() {
        if (this.data.keySet().size() <= 2) {
            throw new IllegalArgumentException(
                "Dunn.test only works with more than 2 groups"
            );
        }
    }

    /**
     * Loads the data into a dataframe.
     * @return A non-null, possibly empty dataframe
     */
    private DataFrame dataframe() {
        final List<Entry<String, Double[]>> entries =
            new ArrayList<>(this.data.entrySet());
        final int records = entries.size() * entries.get(0).getValue().length;
        final Object[][] objects = new Object[2][records];
        for (int i = 0, j = 0; i < entries.size() && j < records; i++) {
            final Entry<String, Double[]> entry = entries.get(i);
            for (int k = 0; k < entry.getValue().length; k++, j++) {
                objects[0][j] = entry.getKey();
                objects[1][j] = entry.getValue()[k];
            }
        }
        return DataFrame.create(
            objects,
            new String[]{DunnTest.GROUP, DunnTest.OBSERVATION}
        );
    }

}
