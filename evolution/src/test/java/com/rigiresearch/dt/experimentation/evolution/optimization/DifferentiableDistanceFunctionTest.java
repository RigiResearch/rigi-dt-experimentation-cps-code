package com.rigiresearch.dt.experimentation.evolution.optimization;

import cc.mallet.optimize.Optimizer;
import com.rigiresearch.dt.expression.tokenizer.AbstractTreeBuilder;
import com.rigiresearch.dt.expression.tokenizer.TokenizerException;
import java.util.LinkedList;
import nilgiri.math.DoubleReal;
import nilgiri.math.DoubleRealFactory;
import nilgiri.math.autodiff.DifferentialFunction;
import nilgiri.math.autodiff.DifferentialRealFunctionFactory;
import nilgiri.math.autodiff.Variable;
import nilgiri.math.autodiff.VariableVector;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tests {@link DifferentiableDistanceFunction}.
 * @author Miguel Jimenez (miguel@uvic.ca)
 * @version $Id$
 * @since 0.1.0
 */
class DifferentiableDistanceFunctionTest {

    /**
     * The logger.
     */
    private static final Logger LOGGER =
        LoggerFactory.getLogger(DifferentiableDistanceFunctionTest.class);

    /**
     * A constant for comparing doubles.
     */
    private static final double EPSILON = 0.0001;

    /**
     * A real numbers' factory.
     */
    private final DoubleRealFactory reals = DoubleRealFactory.instance();

    /**
     * A differential functions' factory.
     */
    private final DifferentialRealFunctionFactory<DoubleReal> factory =
        new DifferentialRealFunctionFactory<>(this.reals);

    @Test
    void test() throws TokenizerException {
        // Define the variables
        final VariableVector<DoubleReal> setpoint = this.factory.var(
            "setpoint",
            new DoubleReal(5.0),
            new DoubleReal(0.0)
        );
        final Variable<DoubleReal> x = this.factory.var("x", new DoubleReal(0.0));

        // Define a function manually: y = sqrt(x)
        // final DifferentialFunction<DoubleReal> function = this.factory.sqrt(x);

        // Define a function using a expression
        final DifferentialFunction<DoubleReal> function =
            new AbstractTreeBuilder<>("sqrt(x)", this.factory, x).getTree();

        // Define the distance function
        final LinkedList<Variable<DoubleReal>> variables = new LinkedList<>();
        variables.add(x);
        final DifferentiableDistanceFunction distance =
            new DifferentiableDistanceFunction(setpoint, function, variables);
        DifferentiableDistanceFunctionTest.LOGGER.info("f: {}", distance);

        // Optimize the distance between the function and the set point
        final double[] initial = {10.0};
        final OptimizableFunction optimizable = new OptimizableFunction(distance, initial);
        final Optimizer optimizer = new GradientDescent(optimizable, 0.00001, 0.0001);
        final boolean converged = optimizer.optimize(500_000);
        final double[] gradient = new double[1];
        optimizable.getValueGradient(gradient);

        if (converged) {
            DifferentiableDistanceFunctionTest.LOGGER
                .info("x: {}", optimizable.getParameter(0));
            DifferentiableDistanceFunctionTest.LOGGER
                .info("f(x): {}", optimizable.getValue());
            DifferentiableDistanceFunctionTest.LOGGER
                .info("f'(x): {}", gradient[0]);
        } else {
            DifferentiableDistanceFunctionTest.LOGGER
                .info("The optimizer did not converge");
        }

        Assertions.assertTrue(
            converged,
            "It should converge"
        );
        Assertions.assertTrue(
            Math.abs(optimizable.getParameter(0) - 4.5) <
                DifferentiableDistanceFunctionTest.EPSILON,
            "Incorrect x"
        );
    }

}
