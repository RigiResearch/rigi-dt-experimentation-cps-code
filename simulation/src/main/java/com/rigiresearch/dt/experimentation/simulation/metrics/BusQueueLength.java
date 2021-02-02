package com.rigiresearch.dt.experimentation.simulation.metrics;

import com.rigiresearch.dt.experimentation.simulation.DtSimulation;
import com.rigiresearch.dt.experimentation.simulation.graph.Line;
import com.rigiresearch.dt.experimentation.simulation.graph.Stop;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import jsl.utilities.statistic.Statistic;
import lombok.RequiredArgsConstructor;

/**
 * Bus queue length per stop.
 * @author Miguel Jimenez (miguel@uvic.ca)
 * @version $Id$
 * @since 0.1.0
 */
@RequiredArgsConstructor
public final class BusQueueLength implements SimulationMetric<Statistic> {

    /**
     * The simulation from which this metric is computed.
     */
    private final DtSimulation simulation;

    /**
     * Statistic for a particular stop.
     * @param stop The stop of interest
     * @return A non-null statistic, unless it is an isolated stop
     */
    public Statistic value(final Stop stop) {
        return this.simulation.busQueueLengths().get(stop);
    }

    @Override
    public Statistic value(final Line line) {
        final Statistic tmp = new Statistic();
        tmp.setSaveOption(true);
        line.journey()
            .forEach(stop -> tmp.collect(this.value(stop).getSavedData()));
        return tmp;
    }

    @Override
    public List<Statistic> values(final Line line) {
        final Deque<Stop> journey = line.journey();
        final List<Statistic> list = new ArrayList<>(journey.size());
        journey.forEach(stop -> list.add(this.value(stop)));
        return list;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("Bus queue length per stop:");
        builder.append('\n');
        this.simulation.busQueueLengths()
            .forEach((stop, statistic) -> {
                builder.append(stop.getName());
                builder.append('\n');
                builder.append("Average: ");
                builder.append(statistic.getAverage());
                builder.append('\n');
                builder.append("Standard deviation: ");
                builder.append(statistic.getStandardDeviation());
                builder.append('\n');
                builder.append("Samples: ");
                builder.append(Arrays.toString(statistic.getSavedData()));
                builder.append('\n');
                builder.append('\n');
            });
        return builder.toString();
    }

}
