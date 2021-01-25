package com.rigiresearch.dt.experimentation.evolution.optimization;

import cc.mallet.optimize.Optimizable;

/**
 * A function optimizable by its gradient.
 * @author Miguel Jimenez (miguel@uvic.ca)
 * @version $Id$
 * @since 0.1.0
 */
public final class OptimizableFunction implements Optimizable.ByGradientValue {

    /**
     * A differentiable function (first derivatives only).
     */
    private final DifferentiableFunction function;

    /**
     * Current parameters.
     */
    private double[] parameters;

    /**
     * Default constructor.
     * @param function A differentiable function
     * @param initial Initial values for the parameters
     */
    public OptimizableFunction(final DifferentiableFunction function,
        final double[] initial) {
        this.function = function;
        this.parameters = initial.clone();
    }

    @Override
    public void getValueGradient(final double[] result) {
        final double[] value = this.function.gradient(this.parameters);
        System.arraycopy(value, 0, result, 0, value.length);
    }

    @Override
    public double getValue() {
        return this.function.value(this.parameters);
    }

    @Override
    public int getNumParameters() {
        return this.parameters.length;
    }

    @Override
    public void getParameters(final double[] result) {
        System.arraycopy(this.parameters, 0, result, 0, this.parameters.length);
    }

    @Override
    public double getParameter(final int parameter) {
        return this.parameters[parameter];
    }

    @Override
    public void setParameters(final double[] values) {
        this.parameters = values.clone();
    }

    @Override
    public void setParameter(final int parameter, final double value) {
        this.parameters[parameter] = value;
    }

}
