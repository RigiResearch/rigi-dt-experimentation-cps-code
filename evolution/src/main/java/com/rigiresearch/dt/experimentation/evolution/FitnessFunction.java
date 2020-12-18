package com.rigiresearch.dt.experimentation.evolution;

/**
 * A fitness function to either minimize or maximize a value.
 * @param <T> The type of input value
 *
 * @author Miguel Jimenez (miguel@uvic.ca)
 * @version $Id$
 * @since 0.1.0
 */
public interface FitnessFunction<T extends FitnessFunction.Argument> {

    /**
     * An object-oriented argument contract.
     */
    interface Argument {
        /**
         * Returns this argument's value.
         * @return A double number
         */
        double[] values();
    }

    /**
     * Evaluate this function.
     * @param args The arguments passed to this function
     * @return A positive or negative number, including 0
     */
    default double evaluate(final double... args) {
        return 0.0;
    }

    /**
     * Evaluate this function normalizing the output.
     * @param args The arguments passed to this function
     * @return A number between 0 and 1
     */
    default double evaluateNormalized(final double... args) {
        return 0.0;
    }

    /**
     * Evaluate this function.
     * @param args The value(s) to evaluate
     * @return A positive or negative number, including 0
     */
    default double evaluate(final FitnessFunction.Argument... args) {
        return 0.0;
    }

    /**
     * Evaluate this function normalizing the output.
     * @param args The value(s) to evaluate
     * @return A number between 0 and 1
     */
    default double evaluateNormalized(final FitnessFunction.Argument... args) {
        return 0.0;
    }

    /**
     * The type of argument accepted by this function.
     * @return A class
     */
    Class<T> argumentType();

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
