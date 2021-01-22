package com.rigiresearch.dt.experimentation.evolution.fitness;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * Tests {@link CubicFitnessFunction}.
 * @author Miguel Jimenez (miguel@uvic.ca)
 * @version $Id$
 * @since 0.1.0
 */
class CubicFitnessFunctionTest {

    /**
     * A small number to compare doubles.
     */
    private static final double EPSILON = 0.000001;

    @Test
    void testNonNormalized() {
        final String arg = "x";
        final CubicFitnessFunction function =
            new CubicFitnessFunction(0.0, 10.0, 20.0, arg);
        Assertions.assertTrue(
            function.evaluateNonNormalized(10.0) - 0.0
                < CubicFitnessFunctionTest.EPSILON,
            "Should be technically 0"
        );
        Assertions.assertTrue(
            function.evaluateNonNormalized(0.0) > 0.0,
            "Should be greater than 0"
        );
        Assertions.assertTrue(
            function.evaluateNonNormalized(20.0) < 0.0,
            "Should be less than 0"
        );
        Assertions.assertTrue(
            function.evaluateNonNormalized(5.0)
                > function.evaluateNonNormalized(6.0),
            "Should increase when x tends to 0"
        );
        Assertions.assertTrue(
            function.evaluateNonNormalized(15.0)
                > function.evaluateNonNormalized(16.0),
            "Should decrease when x tends to b"
        );
    }

    @Test
    void testNormalized() {
        final String arg = "x";
        final double c = 10.0;
        final CubicFitnessFunction function =
            new CubicFitnessFunction(0.0, c/3.0, c, arg);
        Assertions.assertTrue(
            0.0 - function.evaluate(new FitnessFunction.NamedArgument(arg, c/3.0)) <
                CubicFitnessFunctionTest.EPSILON,
            "Should be technically 0.0"
        );
        Assertions.assertTrue(
            1.0 + function.evaluate(new FitnessFunction.NamedArgument(arg, c)) <
                CubicFitnessFunctionTest.EPSILON,
            "Should be technically -1.0"
        );
        Assertions.assertTrue(
            1.0 - function.evaluate(new FitnessFunction.NamedArgument(arg, 0.0)) <
                CubicFitnessFunctionTest.EPSILON,
            "Should be technically 1"
        );
    }

    /**
     * TODO Enable this test
     */
    @Disabled
    @Test
    void testXIsOutsideTheDomain() {
        final String arg = "x";
        final double a = 0.0;
        final double b = 18.0;
        final double c = 24.0;
        final CubicFitnessFunction function = new CubicFitnessFunction(a, b, c, arg);
        Assertions.assertEquals(
            Double.NEGATIVE_INFINITY,
            function.evaluate(new FitnessFunction.NamedArgument(arg, -1.0)),
            "Should be negative infinitive"
        );
        Assertions.assertEquals(
            Double.POSITIVE_INFINITY,
            function.evaluate(new FitnessFunction.NamedArgument(arg, c + 1.0)),
            "Should be positive infinitive"
        );
        Assertions.assertTrue(
            1.0 + function.evaluate(new FitnessFunction.NamedArgument(arg, -1.0)) <
                CubicFitnessFunctionTest.EPSILON,
            "Should be technically -1.0"
        );
    }

}
