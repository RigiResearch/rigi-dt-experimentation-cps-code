package com.rigiresearch.dt.experimentation.simulation;

import com.rigiresearch.dt.experimentation.simulation.graph.Line;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import jsl.utilities.statistic.Statistic;

/**
 * A simulation metric.
 * @author Miguel Jimenez (miguel@uvic.ca)
 * @version $Id$
 * @since 0.1.0
 */
public interface SimulationMetric {

    /**
     * Computes this metric for the given line.
     * @param line The line of interest
     * @return A statistic object
     */
    Statistic value(Line line);

    /**
     * Computes this metric per stop for the given line.
     * @param line The line of interest
     * @return A list of statistic objects
     */
    List<Statistic> values(Line line);

    /**
     * Consolidates a list of statistics into a single instance.
     * @param statistics The list of statistics
     * @return A non-null statistics
     */
    public static Statistic consolidated(final Iterable<Statistic> statistics) {
        final List<Double> samples = new ArrayList<>();
        statistics.forEach(statistic ->
            samples.addAll(
                Arrays.stream(statistic.getSavedData())
                    .mapToObj(Double.class::cast)
                    .collect(Collectors.toList())
            )
        );
        final Statistic statistic = new Statistic();
        final Double[] boxed = samples.toArray(DtSimulation.EMPTY_ARRAY);
        final double[] unboxed = Stream.of(boxed)
            .mapToDouble(Double::doubleValue)
            .toArray();
        statistic.collect(unboxed);
        return statistic;
    }

}
