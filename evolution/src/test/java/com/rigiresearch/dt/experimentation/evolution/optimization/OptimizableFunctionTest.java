package com.rigiresearch.dt.experimentation.evolution.optimization;

import cc.mallet.optimize.LimitedMemoryBFGS;
import cc.mallet.optimize.Optimizer;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.logging.LogManager;
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

    static {
        // Turn Mallet's logging off
        LogManager.getLogManager().reset();
    }

    /**
     * The logger.
     */
    private static final Logger LOGGER =
        LoggerFactory.getLogger(OptimizableFunctionTest.class);

    /**
     * The function -3x^2 - 4y^2 + 2x - 4y + 18.
     */
    private static final DifferentiableFunction FUNCTION = new DifferentiableFunction() {
        @Override
        public double value(final double[] parameters) {
            double x = parameters[0];
            double y = parameters[1];
            return -3.0*x*x - 4.0*y*y + 2.0*x - 4.0*y + 18.0;
        }

        @Override
        public double[] gradient(final double[] parameters) {
            final double[] gradient = new double[2];
            gradient[0] = -6.0 * parameters[0] + 2.0;
            gradient[1] = -8.0 * parameters[1] - 4.0;
            return gradient;
        }
    };

    @Test
    void testWithAKnownFunction() {
        final double[] initial = {0.0, 0.0};
        final OptimizableFunction optimizable =
            new OptimizableFunction(OptimizableFunctionTest.FUNCTION, initial);
        final Optimizer optimizer = new LimitedMemoryBFGS(optimizable);
        boolean converged = false;
        try {
            converged = optimizer.optimize();
        } catch (final IllegalArgumentException ignored) {
            // This exception may be thrown if L-BFGS
            //  cannot step in the current direction.
            // This condition does not necessarily mean that
            //  the optimizer has failed, but it doesn't want
            //  to claim to have succeeded...
        }
        if (converged) {
            final double[] optimum = {
                optimizable.getParameter(0),
                optimizable.getParameter(1)
            };
            final double value = OptimizableFunctionTest.FUNCTION.value(optimum);
            final double[] gradient = OptimizableFunctionTest.FUNCTION.gradient(optimum);
            OptimizableFunctionTest.LOGGER.info("The optimizer converged (known function)");
            OptimizableFunctionTest.LOGGER.info("x: {}", optimum[0]);
            OptimizableFunctionTest.LOGGER.info("y: {}", optimum[1]);
            OptimizableFunctionTest.LOGGER.info("f: {}", value);
            OptimizableFunctionTest.LOGGER.info("∇f: {}", Arrays.toString(gradient));
        } else {
            OptimizableFunctionTest.LOGGER.info("The optimizer did not converged");
        }
    }

    // TODO The interpolated function is not good enough. Explore another method,
    //  such as symbolic regression
    @Test
    void testWithAnInterpolatedFunction() {
        final DifferentiableInterpolatedFunction function =
            OptimizableFunctionTest.interpolatedFunction();
        final double[] initial = {0.0, 0.0};
        final OptimizableFunction optimizable =
            new OptimizableFunction(function, initial);
        final Optimizer optimizer = new LimitedMemoryBFGS(optimizable);
        boolean converged = false;
        try {
            converged = optimizer.optimize();
        } catch (final IllegalArgumentException ignored) {
            // This exception may be thrown if L-BFGS
            //  cannot step in the current direction.
            // This condition does not necessarily mean that
            //  the optimizer has failed, but it doesn't want
            //  to claim to have succeeded...
        }
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
     * @return An interpolated function based on the sampled data
     */
    private static DifferentiableInterpolatedFunction interpolatedFunction() {
        final int n = 50;
        final double[] x = new double[n];
        final double[] y = new double[n];
        final double[] f = new double[n];
        final SecureRandom random = new SecureRandom("seed".getBytes());
        for (int i = 0; i < n; i++) {
            x[i] = random.nextDouble() * i;
            y[i] = random.nextDouble() * i;
            f[i] = OptimizableFunctionTest.FUNCTION.value(new double[]{x[i], y[i]});
        }
        return new DifferentiableInterpolatedFunction(x, y, f);
    }

}
