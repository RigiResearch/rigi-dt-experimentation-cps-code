package com.rigiresearch.dt.experimentation.evolution;

import lombok.RequiredArgsConstructor;

/**
 * Defines the fitness function to reward minimizing a value.
 * @author Miguel Jimenez (miguel@uvic.ca)
 * @version $Id$
 * @since 0.1.0
 */
@RequiredArgsConstructor
public final class FitnessFunction {

    /**
     * The upper bound in the x axis.
     */
    private final double b;

    /**
     * Evaluates the function -1/a(x-a)^3, where 0<=x<=b and a=b/2.
     * This function translates -X^3 to the right and stretches it so that
     * f(x) is a positive number and is greater when x tends to 0, and is a
     * negative number and smaller when x tends to b.
     *
     * <p>Visit https://www.wolframalpha.com/input/?i=plot+-1%2F10%28x-10%29%5E3+from+0+to+20
     * to see a plot of this function.</p>
     *
     * @param x The value on the x axis to evaluate the function
     * @return A negative or positive number, or 0 when x = a
     */
    public double evaluate(final double x) {
        final double a = this.b/2.0;
        final double y = -(1.0/a)* StrictMath.pow(x-a, 3.0);
        return y;
    }

    /**
     * Same as {@link #evaluate(double)} but normalized.
     * @param x The value on the x axis to evaluate the function
     * @return A number between 0 and 1
     */
    public double evaluateNormalized(final double x) {
        final double y = this.evaluate(x);
        final double min = this.evaluate(this.b);
        final double max = this.evaluate(0.0);
        // Switch min and max so that when x=0, y=1 and when x=b, y=0
        return FitnessFunction.normalize(y, max, min);
    }

    /**
     * Calculates a value between 0 and 1, given the precondition that value
     * is between min and max. 0 means value = max, and 1 means value = min.
     * @param value The value to scale
     * @param min The minimum value in the domain
     * @param max The maximum value in the domain
     * @return A double between 0 and 1
     */
    private static double normalize(final double value, final double min,
        final double max) {
        return 1.0-(value-min)/(max-min);
    }

}
