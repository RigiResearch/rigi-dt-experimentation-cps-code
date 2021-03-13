package com.rigiresearch.dt.experimentation.evolution;

import com.rigiresearch.dt.experimentation.evolution.fitness.CompositeFitnessFunction;
import com.rigiresearch.dt.experimentation.evolution.fitness.CubicFitnessFunction;
import com.rigiresearch.dt.experimentation.evolution.fitness.FitnessFunction;
import com.rigiresearch.dt.experimentation.evolution.fitness.LinearFitnessFunction;
import com.rigiresearch.dt.experimentation.evolution.fitness.NormalizedFitnessFunction;
import com.rigiresearch.dt.experimentation.evolution.genetic.EvolvingProperties;
import com.rigiresearch.dt.experimentation.simulation.DtSimulation;
import com.rigiresearch.dt.experimentation.simulation.graph.Line;
import com.rigiresearch.dt.experimentation.simulation.metrics.ExcessWaitingTime;
import com.rigiresearch.dt.experimentation.simulation.metrics.HeadwayCoefficientOfVariation;
import com.rigiresearch.dt.experimentation.simulation.metrics.ObservedLineHeadway;
import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import jsl.utilities.statistic.Statistic;
import org.apache.commons.configuration2.Configuration;

/**
 * A decorator that computes metrics for the original simulation.
 *
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
    final FitnessValue.TriFunction<Double, Double, Double, Double, CompositeFitnessFunction>
            fitness;

    /**
     * The simulation configuration data.
     */
    private final Configuration config;

    /**
     * Default constructor.
     *
     * @param simulation The input graph
     * @param config     The configuration options
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
        final double maxEwt = config.getDouble("fitness.params.ewt.max");
        this.ewt = new ExcessWaitingTime(simulation, headways);
        this.hcv = new HeadwayCoefficientOfVariation(simulation);
        this.olh = new ObservedLineHeadway(simulation);
        this.fitness = (minHeadway, maxHeadway, fleet, plannedBuses) ->
            new CompositeFitnessFunction()
                .withFunction(
                    new CubicFitnessFunction(0.0, plannedBuses, fleet, FitnessValue.BUSES), 0.2)
                .withFunction(new NormalizedFitnessFunction(minHeadway, maxHeadway, FitnessValue.OLH), 0.3)
                .withFunction(new LinearFitnessFunction(0.0, FitnessValue.HCV), 0.1)
                .withFunction(new LinearFitnessFunction(0.0, FitnessValue.VEWT), 0.1)
                .withFunction(new NormalizedFitnessFunction(0.0, maxEwt, FitnessValue.EWT), 0.3)
                .validate();
    }

    /**
     * Computes the fitness value for the given line.
     *
     * @param line The line of interest
     * @return A double between {@code -1} and {@code 1}
     */
    public double asDouble(final Line line) {
        return (double) this.asRecord(line)
            .get(EvolvingProperties.SIM_FITNESS.getId());
    }

    /**
     * Computes the fitness value for the given simulation.
     *
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

    /**
     * Computes the metrics for the given line.
     *
     * @param line The line of interest
     * @return A record with all the computed data
     */
    public Record asRecord(final Line line) {
        final Record record = new Record();
        record.put("line", line.getName());
        final double fleet = this.config.getDouble(
            String.format("%s.fleet", line.getName())
        );
        record.put("fleet", fleet);
        final double plannedBuses = this.config.getDouble(
            String.format("%s.planned.buses", line.getName())
        );
        record.put("planned.buses", plannedBuses);
        final double buses = this.config.getDouble(
            String.format("%s.buses", line.getName())
        );
        record.put("buses", buses);
        record.put(
            "headway",
            this.config.getDouble(
                String.format("%s.headway", line.getName())
            )
        );
        final double maxHeadway = this.config.getDouble(
            String.format("%s.headway.max", line.getName())
        );
        record.put("headway.max", maxHeadway);
        final double minHeadway = this.config.getDouble(
            String.format("%s.headway.min", line.getName())
        );
        record.put("headway.min", minHeadway);
        // Excess waiting time
        final Statistic ewt = this.ewt.value(line);
        final double ewta = ewt.getAverage();
        record.put("ewt.a", ewta);
        final double ewtv = ewt.getVariance();
        record.put("ewt.v", ewtv);
        // Headway coefficient of variation
        final Double hcv = this.hcv.value(line);
        record.put("hcv", hcv);
        // Observed line headway
        final double olh = this.olh.value(line).getAverage();
        record.put("headway.observed", olh);
        final double fitness =
            this.fitness.apply(minHeadway, maxHeadway, fleet, plannedBuses)
                .evaluate(
                    new FitnessFunction.NamedArgument(FitnessValue.BUSES, buses),
                    new FitnessFunction.NamedArgument(FitnessValue.OLH, olh),
                    new FitnessFunction.NamedArgument(FitnessValue.HCV, hcv),
                    new FitnessFunction.NamedArgument(FitnessValue.VEWT, ewtv),
                    new FitnessFunction.NamedArgument(FitnessValue.EWT, ewta)
                );
        record.put(EvolvingProperties.SIM_FITNESS.getId(), fitness);
        return record;
    }

    /***
     * Allows ot obtain a record containing the inputs and outputs of a simulation.
     * @return a record containing the inputs and outputs of a simulation.
     */
    public Collection<Record> asRecords() {
        return this.simulation.getGraph()
            .getNodes()
            .stream()
            .filter(Line.class::isInstance)
            .map(Line.class::cast)
            .map(this::asRecord)
            .collect(Collectors.toList());
    }

    /**
     * A function with three parameters.
     *
     * @param <X1> The first parameter of this function
     * @param <X2> The second parameter of this function
     * @param <X3> The third parameter of this function
     * @param <Y>  The type of result
     */
    @FunctionalInterface
    public interface TriFunction<X1, X2, X3, X4, Y> {

        /**
         * Applies this function.
         *
         * @param arg1 The first argument
         * @param arg2 The second argument
         * @param arg3 The third argument
         * @param arg4 The fourth argument
         * @return The result
         */
        Y apply(X1 arg1, X2 arg2, X3 arg3, X4 arg4);
    }

}
