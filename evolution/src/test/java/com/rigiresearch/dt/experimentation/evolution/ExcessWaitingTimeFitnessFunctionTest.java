package com.rigiresearch.dt.experimentation.evolution;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests {@link ExcessWaitingTimeFitnessFunction}.
 * @author Miguel Jimenez (miguel@uvic.ca)
 * @version $Id$
 * @since 0.1.0
 */
class ExcessWaitingTimeFitnessFunctionTest {

    /**
     * A small number to compare doubles.
     */
    private static final double EPSILON = 0.000001;

    @Test
    void simpleTest() {
        final FitnessFunction function =
            new ExcessWaitingTimeFitnessFunction(30.0);
        Assertions.assertTrue(
            1.0 - function.evaluate(0.0)
                < ExcessWaitingTimeFitnessFunctionTest.EPSILON,
            "Should be technically 1.0"
        );
        Assertions.assertTrue(
            0.5 - function.evaluate(15.0)
                < ExcessWaitingTimeFitnessFunctionTest.EPSILON,
            "Should be technically 0.5"
        );
        Assertions.assertTrue(
            0.0 - function.evaluate(30.0)
                < ExcessWaitingTimeFitnessFunctionTest.EPSILON,
            "Should be technically 0.0"
        );
    }

}
