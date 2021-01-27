package com.rigiresearch.dt.experimentation.evolution.optimization;

import cc.mallet.optimize.Optimizable;
import cc.mallet.optimize.Optimizer;
import lombok.RequiredArgsConstructor;

/**
 * Implementation of the steepest descent / gradient method.
 * @author Miguel Jimenez (miguel@uvic.ca)
 * @version $Id$
 * @since 0.1.0
 */
@RequiredArgsConstructor
public final class GradientDescent implements Optimizer {

    /**
     * The function to optimize.
     */
    private final Optimizable.ByGradientValue function;

    /**
     * The minimum acceptable difference to stop the algorithm.
     */
    private final double epsilon;

    /**
     * The step size.
     */
    private final double step;

    /**
     * Whether this optimizer has converged.
     */
    private boolean converged = false;

    @Override
    public boolean optimize() {
        return this.optimize(2147483647);
    }

    @Override
    public boolean optimize(final int iterations) {
        final double[] parameters = new double[this.function.getNumParameters()];
        final double[] gradient = new double[this.function.getNumParameters()];
        int iteration = 1;
        do {
            this.function.getParameters(parameters);
            this.function.getValueGradient(gradient);
            // Update the parameters based on the gradient
            for (int count = 0; count < parameters.length; count++) {
                parameters[count] -= this.step * gradient[count];
            }
            this.function.setParameters(parameters);
            this.converged = this.isConverged(gradient);
            ++iteration;
        } while (!this.converged && iteration < iterations);
        return this.converged;
    }

    /**
     * Determines whether the stop condition has been met.
     * @param gradient The gradient evaluated on the current parameter values
     * @return {@code true} if all values of the gradient are less than epsilon,
     *  {@code false} otherwise
     */
    private boolean isConverged(final double[] gradient) {
        double max = Double.MIN_VALUE;
        for (final double value : gradient) {
            final double current = Math.abs(value);
            if (current > max) {
                max = current;
            }
        }
        return max < this.epsilon;
    }

    @Override
    public boolean isConverged() {
        return this.converged;
    }

    @Override
    public Optimizable getOptimizable() {
        return this.function;
    }

}
