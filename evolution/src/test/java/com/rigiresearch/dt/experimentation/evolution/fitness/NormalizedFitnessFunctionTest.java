package com.rigiresearch.dt.experimentation.evolution.fitness;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests {@link NormalizedFitnessFunction}.
 * @author Miguel Jimenez (miguel@uvic.ca)
 * @version $Id$
 * @since 0.1.0
 */
class NormalizedFitnessFunctionTest {

    /**
     * A small number to compare doubles.
     */
    private static final double EPSILON = 0.000001;

    @Test
    void simpleTest() {
        final String arg = "x";
        final FitnessFunction function =
            new NormalizedFitnessFunction(0.0, 30.0, arg);
        Assertions.assertTrue(
            1.0 - function.evaluate(new FitnessFunction.NamedArgument(arg, 0.0)) <
                NormalizedFitnessFunctionTest.EPSILON,
            "Should be technically 1.0"
        );
        Assertions.assertTrue(
            0.0 - function.evaluate(new FitnessFunction.NamedArgument(arg, 15.0)) <
                NormalizedFitnessFunctionTest.EPSILON,
            "Should be technically 0.0"
        );
        Assertions.assertTrue(
            1.0 + function.evaluate(new FitnessFunction.NamedArgument(arg, 30.0)) <
                NormalizedFitnessFunctionTest.EPSILON,
            "Should be technically -1.0"
        );
    }

}
