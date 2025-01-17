package com.rigiresearch.dt.experimentation.simulation.metrics;

import com.rigiresearch.dt.experimentation.simulation.graph.Line;
import java.util.Arrays;
import java.util.List;
import jsl.utilities.statistic.Statistic;

/**
 * A simulation metric.
 * @param <T> The computed value's type
 * @author Miguel Jimenez (miguel@uvic.ca)
 * @version $Id$
 * @since 0.1.0
 */
public interface SimulationMetric<T> {

    /**
     * Computes this metric for the given line.
     * @param line The line of interest
     * @return The computed value
     */
    T value(Line line);

    /**
     * Computes this metric per stop for the given line, if defined.
     * @param line The line of interest
     * @return A list of computed values
     */
    List<T> values(Line line);

    /**
     * Consolidates a list of statistics into a single instance.
     * @param statistics The list of statistics
     * @return A non-null statistics
     */
    static Statistic consolidated(final Iterable<Statistic> statistics) {
        final Statistic result = new Statistic();
        result.setSaveOption(true);
        statistics.forEach(statistic -> {
            Arrays.stream(statistic.getSavedData())
                .forEach(result::collect);
        });
        return result;
    }

}
