package com.rigiresearch.dt.experimentation.evolution;

import java.util.function.Function;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests {@link CompositeFitnessFunction}.
 * @author Miguel Jimenez (miguel@uvic.ca)
 * @version $Id$
 * @since 0.1.0
 */
class CompositeFitnessFunctionTest {

    /**
     * A small number to compare doubles.
     */
    private static final double EPSILON = 0.000001;

    @Test
    void testWithASingleFunction() {
        final CompositeFitnessFunction function = new CompositeFitnessFunction()
            .withFunction(new FrequencyFitnessFunction(50.0), 1.0)
            .validate();
        final Function<Double, FitnessFunction.Argument[]> args = value ->
            new FitnessFunction.Argument[] {
                new FrequencyFitnessFunction.FrequencyArgument(value)
            };
        Assertions.assertTrue(
            0.0 - function.evaluate(args.apply(50.0))
                < CompositeFitnessFunctionTest.EPSILON,
            "Should be technically 0.0"
        );
        Assertions.assertTrue(
            0.5 - function.evaluate(args.apply(25.0))
                < CompositeFitnessFunctionTest.EPSILON,
            "Should be technically 0.5"
        );
        Assertions.assertTrue(
            1.0 - function.evaluate(args.apply(0.0))
                < CompositeFitnessFunctionTest.EPSILON,
            "Should be technically 1.0"
        );
    }

    @Test
    void testWithWrongWeights() {
        Assertions.assertThrows(IllegalStateException.class, () -> {
            new CompositeFitnessFunction()
                .withFunction(new FrequencyFitnessFunction(25.0), 0.9)
                .validate();
        });
    }

    @Test
    void testWithDuplicateFunctions() {
        Assertions.assertThrows(IllegalStateException.class, () -> {
            new CompositeFitnessFunction()
                .withFunction(new FrequencyFitnessFunction(50.0), 0.5)
                .withFunction(new FrequencyFitnessFunction(25.0), 0.5)
                .validate();
        });
    }

}
