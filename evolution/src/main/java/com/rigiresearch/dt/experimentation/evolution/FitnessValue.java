package com.rigiresearch.dt.experimentation.evolution;

import com.rigiresearch.dt.experimentation.evolution.fitness.CompositeFitnessFunction;
import com.rigiresearch.dt.experimentation.evolution.fitness.CubicFitnessFunction;
import com.rigiresearch.dt.experimentation.evolution.fitness.FitnessFunction;
import com.rigiresearch.dt.experimentation.evolution.fitness.NormalizedFitnessFunction;
import com.rigiresearch.dt.experimentation.simulation.DtSimulation;
import com.rigiresearch.dt.experimentation.simulation.graph.Line;
import com.rigiresearch.dt.experimentation.simulation.metrics.ExcessWaitingTime;
import com.rigiresearch.dt.experimentation.simulation.metrics.HeadwayCoefficientOfVariation;
import com.rigiresearch.dt.experimentation.simulation.metrics.ObservedLineHeadway;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import jsl.utilities.statistic.Statistic;
import org.apache.commons.configuration2.Configuration;

/**
 * A decorator that computes metrics for the original simulation.
 * @author Miguel Jimenez (miguel@uvic.ca)
 * @version $Id$
 * @since 0.1.0
 */
public final class FitnessValue {

    /**
     * Name for the buses argument.
     */
    private static final String BUSES = "buses";

    /**
     * Name for the headway coefficient of variation.
     */
    private static final String HCV = "hcv";

    /**
     * Name for the variance of excess waiting time.
     */
    private static final String VEWT = "vewt";

    /**
     * Name for the excess waiting time.
     */
    private static final String EWT = "ewt";

    /**
     * Name for the observed line headway.
     */
    private static final String OLH = "olh";

    /**
     * The decorated simulation.
     */
    private final DtSimulation simulation;

    /**
     * The excess waiting time metric.
     */
    private final ExcessWaitingTime ewt;

    /**
     * The headway coefficient of variation metric.
     */
    private final HeadwayCoefficientOfVariation hcv;

    /**
     * The observed line headway.
     */
    private final ObservedLineHeadway olh;

    /**
     * The fitness function.
     */
    final Function<Double, CompositeFitnessFunction> fitness;

    /**
     * The simulation configuration data.
     */
    private final Configuration config;

    /**
     * Default constructor.
     * @param simulation The input graph
     * @param config The configuration options
     */
    public FitnessValue(final DtSimulation simulation,
        final Configuration config) {
        this.config = config;
        this.simulation = simulation;
        final Map<Line, Double> headways = simulation.getGraph()
            .getNodes()
            .stream()
            .filter(Line.class::isInstance)
            .map(Line.class::cast)
            .collect(
                Collectors.toMap(
                    Function.identity(),
                    line -> config.getDouble(
                        String.format("%s.headway", line.getName())
                    )
                )
            );
        this.ewt = new ExcessWaitingTime(simulation, headways);
        this.hcv = new HeadwayCoefficientOfVariation(simulation);
        this.olh = new ObservedLineHeadway(simulation);
        // FIXME Define a new type of function with a minimum value but without a maximum value
        this.fitness = x ->
            new CompositeFitnessFunction()
                .withFunction(new CubicFitnessFunction(0.0, 26.0, 34.0, FitnessValue.BUSES), 0.2)
                .withFunction(new NormalizedFitnessFunction(0.0, x, FitnessValue.OLH), 0.3)
                .withFunction(new NormalizedFitnessFunction(0.0, 1.0, FitnessValue.HCV), 0.1)
                .withFunction(new NormalizedFitnessFunction(0.0, 1.0, FitnessValue.VEWT), 0.1)
                .withFunction(new NormalizedFitnessFunction(0.0, 30.0, FitnessValue.EWT), 0.3)
                .validate();
    }

    /**
     * Computes the fitness value for the given line.
     * @param line The line of interest
     * @return A double between {@code -1} and {@code 1}
     */
    public double asDouble(final Line line) {
        // Configured number of buses
        final double buses = this.config.getDouble(
            String.format("%s.fleet", line.getName())
        );
        final double headway = this.config.getDouble(
            String.format("%s.headway", line.getName())
        );
        // Excess waiting time
        final Statistic ewt = this.ewt.value(line);
        final double ewta = ewt.getAverage();
        final double ewtv = ewt.getVariance();
        // Headway coefficient of variation
        final Double hcv = this.hcv.value(line);
        // Observed line headway
        final double olh = this.olh.value(line).getAverage();
        return this.fitness.apply(headway)
            .evaluate(
                new FitnessFunction.NamedArgument(FitnessValue.BUSES, buses),
                new FitnessFunction.NamedArgument(FitnessValue.OLH, olh),
                new FitnessFunction.NamedArgument(FitnessValue.HCV, hcv),
                new FitnessFunction.NamedArgument(FitnessValue.VEWT, ewtv),
                new FitnessFunction.NamedArgument(FitnessValue.EWT, ewta)
            );
    }

    /**
     * Computes the fitness value for the given simulation.
     * @return The sum of fitness values
     */
    public double asDouble() {
        return this.simulation.getGraph()
            .getNodes()
            .stream()
            .filter(Line.class::isInstance)
            .map(Line.class::cast)
            .map(this::asDouble)
            .mapToDouble(value -> value)
            .sum();
    }

}
