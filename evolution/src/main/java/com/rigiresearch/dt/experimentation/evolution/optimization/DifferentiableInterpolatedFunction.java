package com.rigiresearch.dt.experimentation.evolution.optimization;

import flanagan.interpolation.BiCubicSplineFirstDerivative;

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
     * The interpolated function.
     */
    private final BiCubicSplineFirstDerivative function;

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
        this.function = this.interpolatedFunction();
    }

    /**
     * Computes an interpolated function for the given data using cubic splines.
     * @return A non-null function if all constraints are met
     */
    private BiCubicSplineFirstDerivative interpolatedFunction() {
        final double[][] tab = new double[this.x.length][this.y.length];
        for(int i = 0; i < this.x.length; i++){
            for(int j = 0; j < this.y.length; j++){
                tab[i][j] = this.f[i];
            }
        }
        return new BiCubicSplineFirstDerivative(this.x, this.y, tab);
    }

    @Override
    public double value(final double[] parameters) {
        return this.function.interpolate(parameters[0], parameters[1])[0];
    }

    @Override
    public double[] gradient(final double[] parameters) {
        final double[] result = this.function.interpolate(parameters[0], parameters[1]);
        return new double[]{result[0], result[1]};
    }

    @Override
    public String toString() {
        final double[] limits = this.function.getLimits();
        return String.format(
            "min(x): %f, max(x): %f, min(y): %f, max(y): %f",
            limits[0],
            limits[1],
            limits[2],
            limits[3]
        );
    }

}
