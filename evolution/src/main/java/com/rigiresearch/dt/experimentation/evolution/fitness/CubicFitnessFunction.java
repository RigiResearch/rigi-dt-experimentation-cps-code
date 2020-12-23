package com.rigiresearch.dt.experimentation.evolution.fitness;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Defines a cubic fitness function to reward minimizing certain variable.
 * @author Miguel Jimenez (miguel@uvic.ca)
 * @version $Id$
 * @since 0.1.0
 */
@RequiredArgsConstructor
public final class CubicFitnessFunction
    implements FitnessFunction<CubicFitnessFunction.CubicFunctionArgument> {

    /**
     * Error message for unimplemented methods.
     */
    private static final String ERROR = "Not implemented on purpose";

    /**
     * The lower bound in the x axis.
     */
    private final double a;

    /**
     * A value between a and c;
     */
    private final double b;

    /**
     * The upper bound in the x axis.
     */
    private final double c;

    /**
     * Evaluates the function {@code -10(x-b)^3}, where {@code a<=x<=c} and
     * {@code a<=b<=c}. This function translates {@code -X^3} to the right and
     * stretches it so that {@code f(x)} is a positive number and is greater
     * when {@code x} tends to {@code a}, and is a negative number and smaller
     * when it tends to {@code b}.
     *
     * <p>When {@code x<a} or {@code x>c}, this function returns negative or
     * positive infinity, respectively.</p>
     *
     * <p>Visit https://www.wolframalpha.com/input/?i=plot+-10%28x-18%29%5E3+from+0+to+36
     * to see a plot of this function.</p>
     *
     * @param args One value on the x axis to evaluate the function
     * @return A negative or positive number, or 0 when x = b
     */
    @Override
    public double evaluate(final double... args) {
        final double x = args[0];
        final double y;
        if (this.a <= x && x <= this.c) {
            y = -10.0 * StrictMath.pow(x-this.b, 3.0);
        } else if (x < this.a) {
            y = Double.NEGATIVE_INFINITY;
        } else {
            y = Double.POSITIVE_INFINITY;
        }
        return y;
    }

    /**
     * Same as {@link #evaluate(double[])} but normalized. When {@code x<a} or
     * {@code x>b}, this function returns 0.
     * @param args One value on the x axis to evaluate the function
     * @return A number between 0 and 1
     */
    @Override
    public double evaluateNormalized(final double... args) {
        final double x = args[0];
        final double normalized;
        if (this.a <= x && x <= this.c) {
            final double y = this.evaluate(x);
            final double min = this.evaluate(this.a);
            final double max = this.evaluate(this.c);
            // Switch min and max so that when x=a, y=1 and when x=c, y=-1
            normalized = FitnessFunction.normalizeInRange(y, max, min, -1.0, 1.0);
        } else {
            normalized = -1.0;
        }
        return normalized;
    }

    @Override
    public double evaluate(final FitnessFunction.Argument... args) {
        throw new UnsupportedOperationException(CubicFitnessFunction.ERROR);
    }

    @Override
    public double evaluateNormalized(final FitnessFunction.Argument... args) {
        throw new UnsupportedOperationException(CubicFitnessFunction.ERROR);
    }

    @Override
    public Class<CubicFitnessFunction.CubicFunctionArgument> argumentType() {
        return CubicFitnessFunction.CubicFunctionArgument.class;
    }

    /**
     * A valid argument for this function.
     */
    @Accessors(fluent = true)
    @Getter
    public static final class CubicFunctionArgument
        implements FitnessFunction.Argument {

        /**
         * Valid argument values.
         */
        private final double[] values;

        /**
         * Default constructor.
         * @param values Valid argument values
         */
        public CubicFunctionArgument(final double... values) {
            this.values = values;
        }

    }

}
