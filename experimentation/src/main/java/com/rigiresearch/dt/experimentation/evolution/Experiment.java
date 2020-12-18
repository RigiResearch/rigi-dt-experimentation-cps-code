package com.rigiresearch.dt.experimentation.evolution;

import com.datumbox.framework.common.dataobjects.FlatDataCollection;
import com.datumbox.framework.common.dataobjects.TransposeDataCollection;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * An experiment.
 * @author Miguel Jimenez (miguel@uvic.ca)
 * @version $Id$
 * @since 0.1.0
 */
public interface Experiment {

    /**
     * Runs the statistical procedure to determine the best performant cluster
     * and returns a summary of the results.
     * @return A non-null {@link ExperimentResult} instance
     */
    ExperimentResult result();

    /**
     * Instantiates the appropriate data collection.
     * @param samples The collected samples
     * @return A non-null instance
     */
    static FlatDataCollection data(final Double[] samples) {
        return new FlatDataCollection(Arrays.asList(samples));
    }

    /**
     * Instantiates the appropriate data collection.
     * @param samples The collected samples
     * @return A non-null instance
     */
    static TransposeDataCollection data(final Map<String, Double[]> samples) {
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
