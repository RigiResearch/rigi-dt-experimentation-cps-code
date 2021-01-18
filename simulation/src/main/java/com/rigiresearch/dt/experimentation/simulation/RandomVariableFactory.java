package com.rigiresearch.dt.experimentation.simulation;

import com.rigiresearch.dt.experimentation.simulation.graph.Line;
import com.rigiresearch.dt.experimentation.simulation.graph.Stop;
import java.util.function.Function;
import jsl.modeling.elements.variable.RandomVariable;
import jsl.simulation.ModelElement;
import jsl.utilities.random.rvariable.BernoulliRV;
import jsl.utilities.random.rvariable.BinomialRV;
import jsl.utilities.random.rvariable.ConstantRV;
import jsl.utilities.random.rvariable.DEmpiricalRV;
import jsl.utilities.random.rvariable.DUniformRV;
import jsl.utilities.random.rvariable.GeometricRV;
import jsl.utilities.random.rvariable.NegativeBinomialRV;
import jsl.utilities.random.rvariable.PoissonRV;
import jsl.utilities.random.rvariable.ShiftedGeometricRV;
import jsl.utilities.random.rvariable.VConstantRV;
import org.apache.commons.configuration2.Configuration;

/**
 * Configuration of arrival distribution for a particular variable.
 * @author Miguel Jimenez (miguel@uvic.ca)
 * @version $Id$
 * @since 0.1.0
 */
public final class RandomVariableFactory {

    /**
     * Creates a random variable based on the specified line and variable.
     * @param line The graph node
     * @param variable The variable name to load from the properties configuration
     * @param config The configuration options
     * @param suffix A suffix to make the variable name unique
     * @return A function that will instantiate the variable based on a given
     *  model element
     */
    public static Function<ModelElement, RandomVariable> get(final Line line,
        final String variable, final Configuration config, final String suffix) {
        final String name = String.format(
            "RV-%s-%s-%s",
            variable,
            line.getName(),
            suffix
        );
        final String key = String.format(
            "%s.%s.distribution",
            line.getName(),
            variable
        );
        return RandomVariableFactory.get(
            Distribution.from(config.getString(key)),
            config.subset(key),
            name
        );
    }

    /**
     * Creates a random variable based on the specified line, stop and variable.
     * @param line The graph node
     * @param stop The graph node
     * @param variable The variable name to load from the properties configuration
     * @param config The configuration options
     * @return A function that will instantiate the variable based on a given
     *  model element
     */
    public static Function<ModelElement, RandomVariable> get(final Line line,
        final Stop stop, final String variable, final Configuration config) {
        final String name = String.format(
            "RV-%s-%s-%s",
            variable,
            line.getName(),
            stop.getName()
        );
        final String key = String.format(
            "%s.%s.%s.distribution",
            line.getName(),
            stop.getName(),
            variable
        );
        return RandomVariableFactory.get(
            Distribution.from(config.getString(key)),
            config.subset(key),
            name
        );
    }

    /**
     * Creates a random variable based on the specified distribution.
     * @param distribution The distribution to use
     * @param config The arguments of the distribution
     * @return A function that will instantiate the variable based on a given
     *  model element
     */
    public static Function<ModelElement, RandomVariable> get(
        final Distribution distribution, final Configuration config,
        final String name) {
        final Function<ModelElement, RandomVariable> function;
        switch (distribution) {
            case BERNOULLI:
                function = element ->
                    new RandomVariable(
                        element,
                        new BernoulliRV(config.getDouble("prob")),
                        name
                    );
                break;
            case BINOMIAL:
                function = element ->
                    new RandomVariable(
                        element,
                        new BinomialRV(
                            config.getDouble("prob"),
                            config.getInt("numTrials")
                        ),
                        name
                    );
                break;
            case CONSTANT:
                function = element ->
                    new RandomVariable(
                        element,
                        new ConstantRV(config.getDouble("value")),
                        name
                    );
                break;
            case EMPIRICAL:
                function = element ->
                    new RandomVariable(
                        element,
                        new DEmpiricalRV(
                            (double[]) config.getArray(double.class, "value"),
                            (double[]) config.getArray(double.class, "cdf")
                        ),
                        name
                    );
                break;
            case UNIFORM:
                function = element ->
                    new RandomVariable(
                        element,
                        new DUniformRV(
                            config.getInt("minimum"),
                            config.getInt("maximum")
                        ),
                        name
                    );
                break;
            case GEOMETRIC:
                function = element ->
                    new RandomVariable(
                        element,
                        new GeometricRV(config.getDouble("prob")),
                        name
                    );
                break;
            case NEGATIVE_BINOMIAL:
                function = element ->
                    new RandomVariable(
                        element,
                        new NegativeBinomialRV(
                            config.getDouble("prob"),
                            config.getDouble("numSuccess")
                        ),
                        name
                    );
                break;
            case POISSON:
                function = element ->
                    new RandomVariable(
                        element,
                        new PoissonRV(config.getDouble("mean")),
                        name
                    );
                break;
            case SHIFTED_GEOMETRIC:
                function = element ->
                    new RandomVariable(
                        element,
                        new ShiftedGeometricRV(config.getDouble("prob")),
                        name
                    );
                break;
            case UPDATABLE_CONSTANT:
                function = element ->
                    new RandomVariable(
                        element,
                        new VConstantRV(config.getDouble("value")),
                        name
                    );
                break;
            default:
                throw new IllegalStateException(
                    String.format("Unexpected value %s", distribution)
                );
        }
        return function;
    }

}
