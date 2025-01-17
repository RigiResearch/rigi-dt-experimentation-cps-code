package com.rigiresearch.dt.experimentation.evolution.fitness;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Tests {@link CompositeFitnessFunction}.
 * @author Miguel Jimenez (miguel@uvic.ca)
 * @version $Id$
 * @since 0.1.0
 */
@Tag("integration")
class CompositeFitnessFunctionTest {

    /**
     * A small number to compare doubles.
     */
    private static final double EPSILON = 0.000001;

    @Test
    void testWithASingleFunction() {
        final String arg = "x";
        final CompositeFitnessFunction function = new CompositeFitnessFunction()
            .withFunction(new CubicFitnessFunction(0.0, 25.0, 50.0, arg), 1.0)
            .validate();
        Assertions.assertTrue(
            1.0 + function.evaluate(new FitnessFunction.NamedArgument(arg, 50.0))
                < CompositeFitnessFunctionTest.EPSILON,
            "Should be technically -1.0"
        );
        Assertions.assertTrue(
            0.0 - function.evaluate(new FitnessFunction.NamedArgument(arg, 25.0))
                < CompositeFitnessFunctionTest.EPSILON,
            "Should be technically 0.0"
        );
        Assertions.assertTrue(
            1.0 - function.evaluate(new FitnessFunction.NamedArgument(arg, 0.0))
                < CompositeFitnessFunctionTest.EPSILON,
            "Should be technically 1.0"
        );
    }

    @Test
    void testWithTwoFunctions() {
        final CompositeFitnessFunction function = new CompositeFitnessFunction()
            .withFunction(new CubicFitnessFunction(0.0, 25.0, 50.0, "x"), 0.4)
            .withFunction(new NormalizedFitnessFunction(0.0, 30.0, "y"), 0.6)
            .validate();
        Assertions.assertTrue(
            1.0 + function.evaluate(
                new FitnessFunction.NamedArgument("x", 50.0),
                new FitnessFunction.NamedArgument("y", 30.0))
                < CompositeFitnessFunctionTest.EPSILON,
            "Should be technically -1.0"
        );
        Assertions.assertTrue(
            1.0 - function.evaluate(
                new FitnessFunction.NamedArgument("x", 0.0),
                new FitnessFunction.NamedArgument("y", 0.0))
                < CompositeFitnessFunctionTest.EPSILON,
            "Should be technically 1.0"
        );
        Assertions.assertTrue(
            0.0 - function.evaluate(
                new FitnessFunction.NamedArgument("x", 25.0),
                new FitnessFunction.NamedArgument("y", 15.0))
                < CompositeFitnessFunctionTest.EPSILON,
            "Should be technically 0.0"
        );
    }

    @Test
    void testWithComposedFunctions() {
        final CompositeFitnessFunction function1 = new CompositeFitnessFunction()
            .withFunction(new CubicFitnessFunction(0.0, 25.0, 50.0, "x1"), 0.4)
            .withFunction(new NormalizedFitnessFunction(0.0, 30.0, "y1"), 0.6)
            .validate();
        final CompositeFitnessFunction function2 = new CompositeFitnessFunction()
            .withFunction(new CubicFitnessFunction(0.0, 25.0, 50.0, "x2"), 0.4)
            .withFunction(new NormalizedFitnessFunction(0.0, 30.0, "y2"), 0.6)
            .validate();
        final CompositeFitnessFunction function = new CompositeFitnessFunction()
            .withFunction(function1, 0.3)
            .withFunction(function2, 0.7)
            .validate();
        final double value = function.evaluate(
            new FitnessFunction.NamedArgument("x1", 0.0),
            new FitnessFunction.NamedArgument("y1", 0.0),
            new FitnessFunction.NamedArgument("x2", 0.0),
            new FitnessFunction.NamedArgument("y2", 0.0)
        );
        System.out.println(value);
    }

    @Test
    void testWithWrongWeights() {
        Assertions.assertThrows(IllegalStateException.class, () ->
            new CompositeFitnessFunction()
                .withFunction(new CubicFitnessFunction(0.0, 12.5, 25.0, "x"), 0.9)
                .validate()
        );
        Assertions.assertThrows(IllegalStateException.class, () ->
            new CompositeFitnessFunction()
                .withFunction(new CubicFitnessFunction(0.0, 12.5, 25.0, "x"), 0.7)
                .withFunction(new NormalizedFitnessFunction(0.0, 10.0, "y"), 0.6)
                .validate()
        );
    }

    @Test
    void testWithDuplicateFunctions() {
        Assertions.assertThrows(IllegalStateException.class, () ->
            new CompositeFitnessFunction()
                .withFunction(new CubicFitnessFunction(0.0, 25.0, 50.0, "a"), 0.3)
                .withFunction(new CubicFitnessFunction(0.0, 12.5, 25.0, "a"), 0.3)
                .withFunction(new NormalizedFitnessFunction(0.0, 10.0, "b"), 0.4)
                .validate()
        );
    }

}
