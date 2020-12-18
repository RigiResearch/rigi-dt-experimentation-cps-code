package com.rigiresearch.dt.experimentation.evolution.fitness;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests {@link FrequencyFitnessFunction}.
 * @author Miguel Jimenez (miguel@uvic.ca)
 * @version $Id$
 * @since 0.1.0
 */
class FrequencyFitnessFunctionTest {

    /**
     * A small number to compare doubles.
     */
    private static final double EPSILON = 0.000001;

    @Test
    void testNonNormalized() {
        final FrequencyFitnessFunction function = new FrequencyFitnessFunction(20.0);
        Assertions.assertTrue(
            function.evaluate(10.0) - 0.0 < FrequencyFitnessFunctionTest.EPSILON,
            "Should be technically 0"
        );
        Assertions.assertTrue(
            function.evaluate(0.0) > 0.0,
            "Should be greater than 0"
        );
        Assertions.assertTrue(
            function.evaluate(20.0) < 0.0,
            "Should be less than 0"
        );
        Assertions.assertTrue(
            function.evaluate(5.0) > function.evaluate(6.0),
            "Should increase when x tends to 0"
        );
        Assertions.assertTrue(
            function.evaluate(15.0) > function.evaluate(16.0),
            "Should decrease when x tends to b"
        );
    }

    @Test
    void testNormalized() {
        final double b = 10.0;
        final FrequencyFitnessFunction function = new FrequencyFitnessFunction(b);
        Assertions.assertTrue(
            function.evaluateNormalized(b/2.0) - 0.5 <
                FrequencyFitnessFunctionTest.EPSILON,
            "Should be technically 0.5"
        );
        Assertions.assertTrue(
            function.evaluateNormalized(b) - 0.0 <
                FrequencyFitnessFunctionTest.EPSILON,
            "Should be technically 0"
        );
        Assertions.assertTrue(
            function.evaluateNormalized(0.0) - 1.0 <
                FrequencyFitnessFunctionTest.EPSILON,
            "Should be technically 1"
        );
    }

}
