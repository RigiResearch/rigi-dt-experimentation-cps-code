package com.rigiresearch.dt.experimentation.evolution;

/**
 * Defines the fitness function to use in the genetic algorithm.
 * @author Miguel Jimenez (miguel@uvic.ca)
 * @version $Id$
 * @since 0.1.0
 */
public final class FitnessFunction {

    /**
     * Evaluates the function -1/a(x-a)^3, where 0<=x<=b and a=b/2.
     * This function translates -X^3 to the right and stretches it so that
     * f(x) is a positive number and is greater when x tends to 0, and is a
     * negative number and smaller when x tends to b.
     *
     * @param b The upper bound in the x axis
     * @param x The value on the x axis to evaluate the function
     * @return A negative or positive number, or 0 when x = a
     */
    public static double evaluate(final double b, final double x) {
        final double a = b/2.0;
        final double y = -(1.0/a)* StrictMath.pow(x-a, 3.0);
        return y;
    }

    /**
     * Same as {@link #evaluate(double, double)} but normalized.
     * @param b The upper bound in the x axis
     * @param x The value on the x axis to evaluate the function
     * @return A number between 0 and 1
     */
    public static double evaluateNormalized(final double b, final double x) {
        final double y = FitnessFunction.evaluate(b, x);
        final double min = FitnessFunction.evaluate(b, b);
        final double max = FitnessFunction.evaluate(b, 0.0);
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
