package com.rigiresearch.dt.experimentation.evolution.fitness;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

/**
 * A linear fitness function with a lower bound.
 * @author Miguel Jimenez (miguel@uvic.ca)
 * @version $Id$
 * @since 0.1.0
 */
@RequiredArgsConstructor
public final class LinearFitnessFunction implements FitnessFunction {

    /**
     * A constant to compare doubles.
     */
    private static final double EPSILON = 0.00001;

    /**
     * The minimum acceptable value. Values less than the minimum will be Double.MAX_VALUE.
     */
    private final double min;

    /**
     * A unique name for the argument handled by this function.
     */
    private final String argument;

    @Override
    public double evaluate(final FitnessFunction.NamedArgument... arguments) {
        final Optional<FitnessFunction.NamedArgument> arg =
            FitnessFunction.argument(this.argument, arguments);
        if (!arg.isPresent()) {
            throw new IllegalArgumentException(
                String.format("Argument '%s' not found", this.argument)
            );
        }
        final double result;
        if (arg.get().getValue() - this.min >= LinearFitnessFunction.EPSILON) {
            result = arg.get().getValue();
        } else {
            result = Double.MAX_VALUE;
        }
        return result;
    }

    @Override
    public List<String> arguments() {
        return Collections.singletonList(this.argument);
    }

    @Override
    public String toString() {
        return String.format(
            "%s(argument: %s, min: %f)",
            this.getClass().getSimpleName(),
            this.argument,
            this.min
        );
    }

}
