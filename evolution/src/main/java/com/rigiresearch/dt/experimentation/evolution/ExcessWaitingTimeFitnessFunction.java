package com.rigiresearch.dt.experimentation.evolution;

import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * Defines the fitness function to reward minimizing the excess waiting time.
 * @author Miguel Jimenez (miguel@uvic.ca)
 * @version $Id$
 * @since 0.1.0
 */
public final class ExcessWaitingTimeFitnessFunction
    implements FitnessFunction<ExcessWaitingTimeFitnessFunction.TimeArgument> {

    @Override
    public double evaluate(final double... args) {
        throw new UnsupportedOperationException("#evaluate()");
    }

    @Override
    public double evaluateNormalized(final double... args) {
        throw new UnsupportedOperationException("#evaluateNormalized()");
    }

    @Override
    public Class<ExcessWaitingTimeFitnessFunction.TimeArgument> argumentType() {
        return ExcessWaitingTimeFitnessFunction.TimeArgument.class;
    }

    /**
     * A valid argument for this function.
     */
    @Accessors(fluent = true)
    @Getter
    public static final class TimeArgument
        implements FitnessFunction.Argument {

        /**
         * Valid argument values.
         */
        private final double[] values;

        /**
         * Default constructor.
         * @param values Valid argument values
         */
        public TimeArgument(final double... values) {
            this.values = values;
        }

    }

}
