package com.rigiresearch.dt.experimentation.evolution.optimization;

import cc.mallet.optimize.Optimizable;
import cc.mallet.optimize.Optimizer;
import com.rigiresearch.dt.expression.tokenizer.AbstractTreeBuilder;
import com.rigiresearch.dt.expression.tokenizer.TokenizerException;
import nilgiri.math.DoubleReal;
import nilgiri.math.DoubleRealFactory;
import nilgiri.math.autodiff.DifferentialFunction;
import nilgiri.math.autodiff.DifferentialRealFunctionFactory;
import nilgiri.math.autodiff.Variable;
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

        private final double x1 = 750.0;

        private final double y1 = 100.0;

        /**
         * A real numbers' factory.
         */
        private final DoubleRealFactory RF = DoubleRealFactory.instance();

        /**
         * A differential functions' factory.
         */
        private final DifferentialRealFunctionFactory<DoubleReal> DF =
            new DifferentialRealFunctionFactory<>(this.RF);

        private final Variable<DoubleReal> X = this.DF.var("x", new DoubleReal(0.0));

        private final DifferentialFunction<DoubleReal> F = differentiableFunction();

        private DifferentialFunction<DoubleReal> differentiableFunction() {
            final String f = "sqrt(9.0 + (((7.0 + 2.0*x) + (x + sqrt(6.0 + ((8.0 + 2.0*x) + ((4.0 + 2.0*x) + (x + ((4.0 + 2.0*x) + 9.0))))))) + (6.0 + ((9.0 + (x + ((6.0 + 2.0*x) + 6.0))) + 2.0*x))))";
            final String expression = "sqrt((x - " + this.x1 + ")^2 + (" + f + " - " + this.y1 + ")^2)";
            System.out.println(expression);
            try {
                final DifferentialFunction<DoubleReal> tree = new AbstractTreeBuilder<>(
                    expression,
                    DF,
                    X
                ).getTree();
                System.out.println(tree);
                return tree;
            } catch (TokenizerException e) {
                System.out.println("ERROR creating function");
                throw new IllegalStateException(e);
            }
        }

        @Override
        public double value(final double[] parameters) {
            // sqrt((x - 5)^2 + (sqrt(x) - 0)^2)
            // This function represents the distance between f(x) = sqrt(x) and (5, 0)
            final double x = parameters[0];
            X.set(new DoubleReal(x));
            return F.getValue().doubleValue();
        }

        @Override
        public double[] gradient(final double[] parameters) {
            final double[] gradient = new double[1];
            final double x = parameters[0];
            X.set(new DoubleReal(x));
            gradient[0] = F.diff(X).getValue().doubleValue();
            return gradient;
        }
    };

    @Test
    void testWithSymbolicRegressionApproximatedFunction() {
        final double epsilon = 0.00001;
        final double step = 0.0001;
        final Optimizable.ByGradientValue function =
            new OptimizableFunction(FUNCTION, new double[]{10.0});
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
            gradient[0] < epsilon,
            "Incorrect f'(x)"
        );
    }

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
