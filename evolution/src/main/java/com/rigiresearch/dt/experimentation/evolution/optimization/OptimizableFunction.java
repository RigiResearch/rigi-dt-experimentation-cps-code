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
     * @param x Initial value for parameter x
     * @param y Initial value for parameter y
     */
    public OptimizableFunction(final DifferentiableFunction function,
        final double x, final double y) {
        this.function = function;
        this.parameters = new double[]{x, y};
    }

    @Override
    public void getValueGradient(final double[] result) {
        final double[] value = this.function.gradient(this.parameters);
        result[0] = value[0];
        result[1] = value[1];
    }

    @Override
    public double getValue() {
        return this.function.value(this.parameters);
    }

    @Override
    public int getNumParameters() {
        return 2;
    }

    @Override
    public void getParameters(final double[] result) {
        result[0] = this.parameters[0];
        result[1] = this.parameters[1];
    }

    @Override
    public double getParameter(final int i) {
        return this.parameters[i];
    }

    @Override
    public void setParameters(final double[] values) {
        this.parameters = values;
    }

    @Override
    public void setParameter(final int i, final double value) {
        this.parameters[i] = value;
    }

}
