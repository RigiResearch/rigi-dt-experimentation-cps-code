package com.rigiresearch.dt.experimentation.evolution.fitness;

import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;

/**
 * Defines a normalized fitness function to reward minimizing certain variable.
 * @author Miguel Jimenez (miguel@uvic.ca)
 * @version $Id$
 * @since 0.1.0
 */
@RequiredArgsConstructor
public final class NormalizedFitnessFunction implements FitnessFunction {

    /**
     * The minimum acceptable value.
     */
    private final double min;

    /**
     * The maximum acceptable value.
     */
    private final double max;

    /**
     * A unique name for the argument handled by this function.
     */
    private final String argument;

    /**
     * Evaluates the function {@code -x+1}, where {@code 0<=x<=max}. f(x) is
     * greater when {@code x} tends to 0, and is smaller when it tends to
     * {@code max}.
     * <p>Visit https://www.wolframalpha.com/input/?i=plot+-x+%2B+1+from+0+to+1
     * to see a plot of this function.</p>
     * @param arguments The arguments passed to this function
     * @return a double value between -1.0 and 1.0
     */
    @Override
    public double evaluate(final FitnessFunction.NamedArgument... arguments) {
        this.checkArguments(arguments);
        return FitnessFunction.normalizeInRange(
            arguments[0].getValue(),
            this.max,
            this.min,
            -1.0,
            1.0
        );
    }

    @Override
    public List<String> arguments() {
        return Collections.singletonList(this.argument);
    }

    /**
     * Checks preconditions on the arguments.
     * @param args The arguments
     */
    private void checkArguments(final FitnessFunction.NamedArgument... args) {
        if (args.length != 1) {
            throw new IllegalArgumentException(
                String.format(
                    "Function %s accepts one argument only, %d found",
                    this.getClass().getSimpleName(),
                    args.length
                )
            );
        }
        if (!args[0].getName().equals(this.argument)) {
            throw new IllegalArgumentException(
                String.format(
                    "Function %s requires argument \"%s\"",
                    this.getClass().getSimpleName(),
                    this.argument
                )
            );
        }
        if (args[0].getValue() > this.max || args[0].getValue() < 0.0) {
            throw new IllegalArgumentException(
                String.format("Value %f is out of bounds", args[0].getValue())
            );
        }
    }

    @Override
    public String toString() {
        return String.format(
            "%s(argument: %s, min: %f, max: %f)",
            this.getClass().getSimpleName(),
            this.argument,
            this.min,
            this.max
        );
    }

}
