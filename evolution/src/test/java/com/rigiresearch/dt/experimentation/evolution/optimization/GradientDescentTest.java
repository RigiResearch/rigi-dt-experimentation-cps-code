package com.rigiresearch.dt.experimentation.evolution.optimization;

import cc.mallet.optimize.Optimizable;
import cc.mallet.optimize.Optimizer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tests {@link GradientDescent}.
 * @author Miguel Jimenez (miguel@uvic.ca)
 * @version $Id$
 * @since 0.1.0
 */
class GradientDescentTest {

    /**
     * The logger.
     */
    private static final Logger LOGGER =
        LoggerFactory.getLogger(GradientDescentTest.class);

    /**
     * A constant for comparing doubles.
     */
    private static final double EPSILON = 0.0001;

    private static final DifferentiableFunction FUNCTION = new DifferentiableFunction() {
        @Override
        public double value(final double[] parameters) {
            // sqrt((x - 5)^2 + (sqrt(x) - 0)^2)
            // This function represents the distance between f(x) = sqrt(x) and (5, 0)
            final double x = parameters[0];
            return Math.sqrt(StrictMath.pow(x - 5.0, 2.0) +
                StrictMath.pow(Math.sqrt(x) - 0.0, 2.0));
        }

        @Override
        public double[] gradient(final double[] parameters) {
            final double[] gradient = new double[1];
            final double x = parameters[0];
            gradient[0] = (x - 4.5) / Math.sqrt(StrictMath.pow(x - 5.0, 2.0) + x);
            return gradient;
        }
    };

    @Test
    void simpleTest() {
        final double epsilon = 0.00001;
        final double step = 0.0001;
        final Optimizable.ByGradientValue function =
            new OptimizableFunction(GradientDescentTest.FUNCTION, new double[]{10.0});
        final Optimizer optimizer = new GradientDescent(function, epsilon, step);
        final boolean converged = optimizer.optimize();
        final double[] gradient = new double[1];
        function.getValueGradient(gradient);

        GradientDescentTest.LOGGER.info("Converged? {}", converged);
        GradientDescentTest.LOGGER.info("x: {}", function.getParameter(0));
        GradientDescentTest.LOGGER.info("f(x): {}", function.getValue());
        GradientDescentTest.LOGGER.info("f'(x): {}", gradient[0]);

        Assertions.assertTrue(
            converged,
            "It should converge"
        );
        Assertions.assertTrue(
            Math.abs(function.getParameter(0) - 4.5) <
                GradientDescentTest.EPSILON,
            "Incorrect x"
        );
        Assertions.assertTrue(
            gradient[0] < epsilon,
            "Incorrect f'(x)"
        );
    }

}
