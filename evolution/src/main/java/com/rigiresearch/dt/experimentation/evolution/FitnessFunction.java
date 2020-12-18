package com.rigiresearch.dt.experimentation.evolution;

/**
 * A fitness function to either minimize or maximize a value.
 * @author Miguel Jimenez (miguel@uvic.ca)
 * @version $Id$
 * @since 0.1.0
 */
public interface FitnessFunction {

    /**
     * Evaluate this function.
     * @param value The value to evaluate
     * @return A positive or negative number, including 0
     */
    double evaluate(double value);

    /**
     * Evaluate this function normalizing the output.
     * @param value The value to evaluate
     * @return A number between 0 and 1
     */
    double evaluateNormalized(double value);

    /**
     * Calculates a value between 0 and 1, given the precondition that value
     * is between min and max. 0 means value = max, and 1 means value = min.
     * @param value The value to scale
     * @param min The minimum value in the domain
     * @param max The maximum value in the domain
     * @return A double between 0 and 1
     */
    static double normalize(final double value, final double min,
        final double max) {
        return 1.0-(value-min)/(max-min);
    }

}
