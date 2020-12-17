package com.rigiresearch.dt.experimentation;

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
    void dummyData() {
        final Double[] treatment1 = {45.5, 51.3, 42.9, 40.5, 55.2};
        final Double[] treatment2 = {66.0, 64.8, 74.9, 81.3, 76.2};
        final Double[] control = {66.0, 64.8, 74.9, 81.3, 76.2};
        final Map<String, Double[]> samples = new HashMap<>(2);
        samples.put("treat1", treatment1);
        samples.put("treat2", treatment2);
        samples.put("control", control);
        final double alpha = 0.05;
        final Experiment experiment = new OneVarMultiGroupExperiment(samples, alpha);
        OneVarMultiGroupExperimentTest.LOGGER.info("\n{}", experiment.result());
    }

}
