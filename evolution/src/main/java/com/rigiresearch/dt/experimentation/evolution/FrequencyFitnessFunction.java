package com.rigiresearch.dt.experimentation.evolution;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Defines the fitness function to reward minimizing the bus frequency.
 * @author Miguel Jimenez (miguel@uvic.ca)
 * @version $Id$
 * @since 0.1.0
 */
@RequiredArgsConstructor
public final class FrequencyFitnessFunction
    implements FitnessFunction<FrequencyFitnessFunction.FrequencyArgument> {

    /**
     * Error message for unimplemented methods.
     */
    private static final String ERROR = "Not implemented on purpose";

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
     * @param args One value on the x axis to evaluate the function
     * @return A negative or positive number, or 0 when x = a
     */
    @Override
    public double evaluate(final double... args) {
        final double x = args[0];
        final double a = this.b/2.0;
        final double y = -(1.0/a)* StrictMath.pow(x-a, 3.0);
        return y;
    }

    /**
     * Same as {@link #evaluate(double[])} but normalized.
     * @param args One value on the x axis to evaluate the function
     * @return A number between 0 and 1
     */
    @Override
    public double evaluateNormalized(final double... args) {
        final double x = args[0];
        final double y = this.evaluate(x);
        final double min = this.evaluate(this.b);
        final double max = this.evaluate(0.0);
        // Switch min and max so that when x=0, y=1 and when x=b, y=0
        return FitnessFunction.normalize(y, max, min);
    }

    @Override
    public double evaluate(final FitnessFunction.Argument... args) {
        throw new UnsupportedOperationException(FrequencyFitnessFunction.ERROR);
    }

    @Override
    public double evaluateNormalized(final FitnessFunction.Argument... args) {
        throw new UnsupportedOperationException(FrequencyFitnessFunction.ERROR);
    }

    @Override
    public Class<FrequencyFitnessFunction.FrequencyArgument> argumentType() {
        return FrequencyFitnessFunction.FrequencyArgument.class;
    }

    /**
     * A valid argument for this function.
     */
    @Accessors(fluent = true)
    @Getter
    public static final class FrequencyArgument
        implements FitnessFunction.Argument {

        /**
         * Valid argument values.
         */
        private final double[] values;

        /**
         * Default constructor.
         * @param values Valid argument values
         */
        public FrequencyArgument(final double... values) {
            this.values = values;
        }

    }

}
