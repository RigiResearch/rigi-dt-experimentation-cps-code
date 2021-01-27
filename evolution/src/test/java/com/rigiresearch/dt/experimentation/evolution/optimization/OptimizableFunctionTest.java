package com.rigiresearch.dt.experimentation.evolution.optimization;

import cc.mallet.optimize.Optimizer;
import java.security.SecureRandom;
import java.util.Arrays;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tests {@link OptimizableFunction}.
 * @author Miguel Jimenez (miguel@uvic.ca)
 * @version $Id$
 * @since 0.1.0
 */
class OptimizableFunctionTest {

    /**
     * The logger.
     */
    private static final Logger LOGGER =
        LoggerFactory.getLogger(OptimizableFunctionTest.class);

    /**
     * A constant for comparing doubles.
     */
    private static final double EPSILON = 0.0001;

    /**
     * The function sqrt(x) + y^2.
     * https://www.wolframalpha.com/input/?i=derivative+of+sqrt%28x%29+%2B+y%5E2+-+y
     */
    private static final DifferentiableFunction FUNCTION = new DifferentiableFunction() {
        @Override
        public double value(final double[] parameters) {
            final double x = parameters[0];
            final double y = parameters[1];
            return Math.sqrt(x) + StrictMath.pow(y, 2.0) - y;
        }

        @Override
        public double[] gradient(final double[] parameters) {
            final double[] gradient = new double[2];
            gradient[0] = 1.0 / 2.0 * Math.sqrt(parameters[0]);
            gradient[1] = 2.0 * parameters[1] - 1.0;
            return gradient;
        }
    };

    /**
     * https://www.wolframalpha.com/input/?i=optimize+sqrt%28x%29+%2B+y%5E2+-+y
     */
    @Test
    void testWithAKnownFunction() {
        final double[] initial = {0.0, 0.0};
        final OptimizableFunction optimizable =
            new OptimizableFunction(OptimizableFunctionTest.FUNCTION, initial);
        final Optimizer optimizer = new GradientDescent(optimizable, 0.00001, 0.0001);
        boolean converged = optimizer.optimize(500_000);

        final double[] min = {
            optimizable.getParameter(0),
            optimizable.getParameter(1)
        };

        if (converged) {
            final double value = OptimizableFunctionTest.FUNCTION.value(min);
            final double[] gradient = OptimizableFunctionTest.FUNCTION.gradient(min);
            OptimizableFunctionTest.LOGGER.info("The optimizer converged (known function)");
            OptimizableFunctionTest.LOGGER.info("x: {}", min[0]);
            OptimizableFunctionTest.LOGGER.info("y: {}", min[1]);
            OptimizableFunctionTest.LOGGER.info("f: {}", value);
            OptimizableFunctionTest.LOGGER.info("∇f: {}", Arrays.toString(gradient));
        } else {
            OptimizableFunctionTest.LOGGER.info("The optimizer did not converged");
        }

        Assertions.assertTrue(
            min[0] < OptimizableFunctionTest.EPSILON,
            "Incorrect x, expected 0.0"
        );
        Assertions.assertTrue(
            min[1] - 0.5 < OptimizableFunctionTest.EPSILON,
            "Incorrect y, expected 0.5"
        );
    }

    // TODO The interpolated function is not good enough (It returns NaN)
    //  Explore another method, such as symbolic regression
    @Disabled
    @Test
    void testWithAnInterpolatedFunction() {
        final DifferentiableInterpolatedFunction function =
            OptimizableFunctionTest.interpolatedFunction();
        final double[] initial = {0.0, 0.0};
        final OptimizableFunction optimizable =
            new OptimizableFunction(function, initial);
        final Optimizer optimizer = new GradientDescent(optimizable, 0.00001, 0.0001);
        final boolean converged = optimizer.optimize(500_000);

        if (converged) {
            final double[] optimum = {
                optimizable.getParameter(0),
                optimizable.getParameter(1)
            };
            final double value = function.value(optimum);
            final double[] gradient = function.gradient(optimum);
            OptimizableFunctionTest.LOGGER.info("The optimizer converged (interpolated)");
            OptimizableFunctionTest.LOGGER.info("x: {}", optimum[0]);
            OptimizableFunctionTest.LOGGER.info("y: {}", optimum[1]);
            OptimizableFunctionTest.LOGGER.info("f: {}", value);
            OptimizableFunctionTest.LOGGER.info("∇f: {}", Arrays.toString(gradient));
        } else {
            OptimizableFunctionTest.LOGGER.info("The optimizer did not converged (interpolated)");
            OptimizableFunctionTest.LOGGER.info("{}", function);
        }
    }

    /**
     * Generates random data using the known function.
     * The data ranges from -500 to 500.
     * @return An interpolated function based on the sampled data
     */
    private static DifferentiableInterpolatedFunction interpolatedFunction() {
        final int n = 1000;
        final double[] x = new double[n];
        final double[] y = new double[n];
        final double[] f = new double[n];
        final SecureRandom random = new SecureRandom("seed".getBytes());
        double factor = n/2.0 * -1.0;
        int i = 0;
        while (factor < n && i < n) {
            x[i] = random.nextDouble() * factor;
            y[i] = random.nextDouble() * factor;
            f[i] = OptimizableFunctionTest.FUNCTION.value(new double[]{x[i], y[i]});
            factor += 1.0;
            i += 1;
        }
        return new DifferentiableInterpolatedFunction(x, y, f);
    }

}
