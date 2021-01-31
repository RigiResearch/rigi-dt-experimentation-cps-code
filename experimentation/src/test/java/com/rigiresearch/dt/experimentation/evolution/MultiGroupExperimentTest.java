package com.rigiresearch.dt.experimentation.evolution;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tests {@link OneVarMultiGroupExperiment}.
 * @author Miguel Jimenez (miguel@uvic.ca)
 * @version $Id$
 * @since 0.1.0
 */
class OneVarMultiGroupExperimentTest {

    /**
     * The logger.
     */
    private static final Logger LOGGER =
        LoggerFactory.getLogger(OneVarMultiGroupExperimentTest.class);

    @Test
    void testNormalData() {
        final Double[] control = {66.0, 64.8, 74.9};
        final Double[] treatment1 = control;
        final Double[] treatment2 = control;
        final Map<String, Double[]> samples = new HashMap<>(3);
        samples.put("treat1", treatment1);
        samples.put("treat2", treatment2);
        samples.put("control", control);
        final ExperimentResult result =
            new OneVarMultiGroupExperiment(samples, 0.05)
            .result();
        OneVarMultiGroupExperimentTest.LOGGER.info("\n{}", result);
    }

    @Test
    void testWithSignificantDifferenceAndNormalData() {
        final Double[] control = {66.0, 64.8, 74.9, 81.3, 76.2};
        final Double[] treatment1 = {45.5, 51.3, 42.9, 40.5, 55.2};
        final Double[] treatment2 = {65.0, 66.8, 74.9, 80.3, 75.2};
        final Map<String, Double[]> samples = new HashMap<>(3);
        samples.put("treat1", treatment1);
        samples.put("treat2", treatment2);
        samples.put("control", control);
        final ExperimentResult result =
            new OneVarMultiGroupExperiment(samples, 0.05)
                .result();
        OneVarMultiGroupExperimentTest.LOGGER.info("\n{}", result);
    }

}
