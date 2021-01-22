package com.rigiresearch.dt.experimentation.simulation.metrics;

import com.rigiresearch.dt.experimentation.simulation.DtSimulation;
import com.rigiresearch.dt.experimentation.simulation.graph.Line;
import java.util.Arrays;
import java.util.List;
import jsl.utilities.statistic.Statistic;
import lombok.RequiredArgsConstructor;

/**
 * Summarizes the observed waiting time for a given simulation.
 * @author Miguel Jimenez (miguel@uvic.ca)
 * @version $Id$
 * @since 0.1.0
 */
@RequiredArgsConstructor
public final class ObservedWaitingTime implements SimulationMetric<Statistic> {

    /**
     * The simulation from which this metric is computed.
     */
    private final DtSimulation simulation;

    @Override
    public Statistic value(final Line line) {
        return SimulationMetric.consolidated(
            this.simulation.waitingTimes()
                .get(line)
        );
    }

    @Override
    public List<Statistic> values(final Line line) {
        return this.simulation.waitingTimes().get(line);
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("Observed waiting time per line:");
        builder.append('\n');
        this.simulation.getGraph()
            .getNodes()
            .stream()
            .filter(Line.class::isInstance)
            .map(Line.class::cast)
            .forEach(line -> {
                final Statistic statistic = this.value(line);
                builder.append(line.getName());
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
