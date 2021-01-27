package com.rigiresearch.dt.experimentation.evolution.optimization;

import java.util.LinkedList;
import nilgiri.math.DoubleReal;
import nilgiri.math.DoubleRealFactory;
import nilgiri.math.autodiff.DifferentialFunction;
import nilgiri.math.autodiff.DifferentialRealFunctionFactory;
import nilgiri.math.autodiff.PolynomialTerm;
import nilgiri.math.autodiff.Variable;
import nilgiri.math.autodiff.VariableVector;

/**
 * A differentiable distance function that computes the distance between a set
 * point and a given function defined in terms of X and Y (2 parameters).
 * @author Miguel Jimenez (miguel@uvic.ca)
 * @version $Id$
 * @since 0.1.0
 */
public final class DifferentiableDistanceFunction implements DifferentiableFunction {

    /**
     * A real numbers' factory.
     */
    private static final DoubleRealFactory RF = DoubleRealFactory.instance();

    /**
     * A differential functions' factory.
     */
    private static final DifferentialRealFunctionFactory<DoubleReal> DF =
        new DifferentialRealFunctionFactory<>(DifferentiableDistanceFunction.RF);

    /**
     * The set point to approximate.
     */
    private final VariableVector<DoubleReal> setpoint;

    /**
     * Variables used in the function we want to approximate.
     */
    private final LinkedList<Variable<DoubleReal>> variables;

    /**
     * The function we want to approximate to the set point, which is defined in
     * terms of X and Y.
     */
    private final DifferentialFunction<DoubleReal> function;

    /**
     * The distance function.
     */
    private final DifferentialFunction<DoubleReal> distance;

    /**
     * Default constructor.
     * This class assumes that the last element in the set point is f(x1... xn).
     * @param setpoint The set point whose distance to the original function we
     *  must minimize
     * @param function The function we want to approximate to the set point
     * @param variables Variables used in the function we want to approximate
     */
    public DifferentiableDistanceFunction(final VariableVector<DoubleReal> setpoint,
        final DifferentialFunction<DoubleReal> function,
        final LinkedList<Variable<DoubleReal>> variables) {
        this.setpoint = setpoint;
        this.variables = variables;
        this.function = function;
        this.distance = this.distanceFunction();
    }

    /**
     * Creates the distance function using variables X and Y, and the function
     * we want to approximate.
     * @return A differential function.
     */
    private DifferentialFunction<DoubleReal> distanceFunction() {
        // First create the polynomial terms and then the square root:
        // d = srqt((x_0 - setpoint_0)^2 + ... + (f(x_0... x_k) - setpoint_k)^2)
        // Instantiate the first term manually
        DifferentialFunction<DoubleReal> inner =
            this.variables.getFirst().minus(this.setpoint.get(0));
        DifferentialFunction<DoubleReal> tmp = new PolynomialTerm<>(1L, inner, 2);
        // Instantiate the other terms
        for (int count = 1; count < this.variables.size(); count++) {
            inner = this.variables.get(count).minus(this.setpoint.get(count));
            tmp = tmp.plus(new PolynomialTerm<>(1L, inner, 2));
        }
        // Instantiate the last term
        inner = this.function.minus(this.setpoint.get(this.setpoint.size() - 1));
        tmp = tmp.plus(new PolynomialTerm<>(1L, inner, 2));
        return DifferentiableDistanceFunction.DF.sqrt(tmp);
    }

    @Override
    public double value(final double[] parameters) {
        this.updateVariables(parameters);
        return this.distance.getValue().doubleValue();
    }

    @Override
    public double[] gradient(final double[] parameters) {
        this.updateVariables(parameters);
        final double[] vector = new double[this.variables.size()];
        for (int count = 0; count < vector.length; count++) {
            final Variable<DoubleReal> variable = this.variables.get(count);
            vector[count] = this.distance.diff(variable).getValue().doubleValue();
        }
        return vector;
    }

    /**
     * Updates the current variable values.
     * @param parameters The values
     */
    private void updateVariables(final double[] parameters) {
        for (int count = 0; count < parameters.length; count++) {
            this.variables.get(count).set(new DoubleReal(parameters[count]));
        }
    }

    /**
     * Returns the inner differential distance function.
     * @return A non-null function
     */
    public DifferentialFunction<DoubleReal> asDifferentialFunction() {
        return this.distance;
    }

    @Override
    public String toString() {
       return this.distance.toString();
    }

}
