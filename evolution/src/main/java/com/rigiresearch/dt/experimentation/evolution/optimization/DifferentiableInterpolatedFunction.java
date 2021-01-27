package com.rigiresearch.dt.experimentation.evolution.optimization;

/**
 * An interpolated function computed from sample data.
 * @author Miguel Jimenez (miguel@uvic.ca)
 * @version $Id$
 * @since 0.1.0
 */
public final class DifferentiableInterpolatedFunction
    implements DifferentiableFunction {

    /**
     * Sample data for the first parameter.
     */
    private final double[] x;

    /**
     * Sample data for the second parameter.
     */
    private final double[] y;

    /**
     * The original function's values evaluated on x and y.
     */
    private final double[] f;

    /**
     * Default constructor.
     * @param x Sample data for the first parameter
     * @param y Sample data for the second parameter
     * @param f The original function's values evaluated on x and y
     */
    public DifferentiableInterpolatedFunction(final double[] x, final double[] y,
        final double[] f) {
        this.x = x;
        this.y = y;
        this.f = f;
    }

    @Override
    public double value(final double[] parameters) {
        throw new UnsupportedOperationException("#value(double[])");
    }

    @Override
    public double[] gradient(final double[] parameters) {
        throw new UnsupportedOperationException("#gradient(double[])");
    }

}
