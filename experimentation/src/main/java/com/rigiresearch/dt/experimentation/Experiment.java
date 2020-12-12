package com.rigiresearch.dt.experimentation;

import com.datumbox.framework.common.dataobjects.FlatDataCollection;
import com.datumbox.framework.common.dataobjects.TransposeDataCollection;
import com.datumbox.framework.core.statistics.nonparametrics.independentsamples.KruskalWallis;
import com.datumbox.framework.core.statistics.nonparametrics.onesample.ShapiroWilk;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An experiment.
 * @author Miguel Jimenez (miguel@uvic.ca)
 * @version $Id$
 * @since 0.1.0
 */
@RequiredArgsConstructor
public final class Experiment {

    /**
     * The logger.
     */
    private static final Logger LOGGER =
        LoggerFactory.getLogger(Experiment.class);

    /**
     * The collected samples grouped by group id.
     * TODO Add the multiple runs to this map, so that the average can be
     *  computed.
     */
    private final Map<String, Long[]> data;

    /**
     * Runs the statistical procedure to determine the best performant cluster
     * and returns a summary of the results.
     * @return A non-null {@link ExperimentResult} instance
     */
    public ExperimentResult result() {
        return ExperimentResult.builder()
            .averages(Collections.emptyMap())
            .clusters(Collections.emptyMap())
            .build();
    }

    /**
     * Determines whether the samples are normally distributed, using a non
     * parametric test.
     * @param samples The collected samples
     * @param alpha The confidence level
     * @return Either {@code true} or {@code false}
     */
    private static boolean isNormalNonParametric(final Long[] samples,
        final double alpha) {
        return !ShapiroWilk.test(Experiment.data(samples), alpha);
    }

    /**
     * Determines whether the samples' averages are significantly different.
     * @param normal Whether the data is normally distributed
     * @param samples The collected samples
     * @param alpha The confidence level
     * @return Either {@code true} or {@code false}
     */
    private static boolean isSignificantlyDifferent(final boolean normal,
        final Map<String, Long[]> samples, final double alpha) {
        final boolean  significant;
        if (normal) {
            // TODO Anova One/two way, equal/non equal vars?
            significant = false;
        } else {
            significant = KruskalWallis.test(Experiment.data(samples), alpha);
        }
        return significant;
    }

    /**
     * Instantiates the appropriate data collection.
     * @param samples The collected samples
     * @return A non-null instance
     */
    private static FlatDataCollection data(final Long[] samples) {
        return new FlatDataCollection(Arrays.asList(samples));
    }

    /**
     * Instantiates the appropriate data collection.
     * @param samples The collected samples
     * @return A non-null instance
     */
    private static TransposeDataCollection data(final Map<String, Long[]> samples) {
        return new TransposeDataCollection(
            samples.entrySet()
                .stream()
                .collect(
                    Collectors.toMap(
                        Map.Entry::getKey,
                        e -> Experiment.data(e.getValue())
                    )
                )
        );
    }

}
