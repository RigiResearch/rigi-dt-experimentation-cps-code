package com.rigiresearch.dt.experimentation.evolution;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests {@link FitnessFunction}.
 * @author Miguel Jimenez (miguel@uvic.ca)
 * @version $Id$
 * @since 0.1.0
 */
class FitnessFunctionTest {

    /**
     * A small number to compare doubles.
     */
    private static final double EPSILON = 0.000001;

    @Test
    void testNonNormalized() {
        final double b = 20.0;
        Assertions.assertTrue(
            FitnessFunction.evaluate(b, 10.0) - 0.0 < FitnessFunctionTest.EPSILON,
            "Should be technically 0"
        );
        Assertions.assertTrue(
            FitnessFunction.evaluate(b, 0.0) > 0.0,
            "Should be greater than 0"
        );
        Assertions.assertTrue(
            FitnessFunction.evaluate(b, 20.0) < 0.0,
            "Should be less than 0"
        );
        Assertions.assertTrue(
            FitnessFunction.evaluate(b, 5.0) > FitnessFunction.evaluate(b, 6.0),
            "Should increase when x tends to 0"
        );
        Assertions.assertTrue(
            FitnessFunction.evaluate(b, 15.0) > FitnessFunction.evaluate(b, 16.0),
            "Should decrease when x tends to b"
        );
    }

    @Test
    void testNormalized() {
        final double b = 10.0;
        Assertions.assertTrue(
            FitnessFunction.evaluateNormalized(b, b/2.0) - 0.5 <
                FitnessFunctionTest.EPSILON,
            "Should be technically 0.5"
        );
        Assertions.assertTrue(
            FitnessFunction.evaluateNormalized(b, b) - 0.0 <
                FitnessFunctionTest.EPSILON,
            "Should be technically 0"
        );
        Assertions.assertTrue(
            FitnessFunction.evaluateNormalized(b, 0.0) - 1.0 <
                FitnessFunctionTest.EPSILON,
            "Should be technically 1"
        );
    }

}
