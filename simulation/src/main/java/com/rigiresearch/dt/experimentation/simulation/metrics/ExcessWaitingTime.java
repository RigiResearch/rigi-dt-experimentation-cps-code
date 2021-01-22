package com.rigiresearch.dt.experimentation.simulation.metrics;

import com.rigiresearch.dt.experimentation.simulation.DtSimulation;
import com.rigiresearch.dt.experimentation.simulation.graph.Line;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import jsl.utilities.statistic.Statistic;

/**
 * Implementation of the excess waiting time metric.
 * @author Miguel Jimenez (miguel@uvic.ca)
 * @version $Id$
 * @since 0.1.0
 */
public final class ExcessWaitingTime implements SimulationMetric<Statistic> {

    /**
     * The simulation from which this metric is computed.
     */
    private final DtSimulation simulation;

    /**
     * The headway designs.
     */
    private final Map<Line, Double> headways;

    /**
     * The observed waiting time.
     */
    private final ObservedWaitingTime observed;

    /**
     * Default constructor.
     * @param simulation The simulation from which this metric is computed
     * @param headways The headway designs
     */
    public ExcessWaitingTime(final DtSimulation simulation,
        final Map<Line, Double> headways) {
        this.simulation = simulation;
        this.headways = headways;
        this.observed = new ObservedWaitingTime(this.simulation);
    }

    @Override
    public Statistic value(final Line line) {
        final double headway = this.headways.get(line);
        final double[] data = this.observed.values(line)
            .stream()
            .map(statistic -> {
                // Data per stop
                final double[] ratios = Arrays.stream(statistic.getSavedData())
                    .mapToObj(Double.class::cast)
                    .map(value -> value / headway * 100.0)
                    .mapToDouble(value -> value)
                    .toArray();
                final Statistic tmp = new Statistic(ratios);
                final double VHrb = tmp.getVariance();
                final double UHrb = tmp.getAverage();
                final double hob = statistic.getAverage();
                return VHrb / (2.0 * UHrb * 100.0) * hob;
            })
            .mapToDouble(value -> value)
            .toArray();
        final Statistic tmp = new Statistic(data);
        tmp.setSaveOption(true);
        return tmp;
    }

    @Override
    public List<Statistic> values(final Line line) {
        throw new UnsupportedOperationException(
            "The excess waiting time is not defined for a single stop"
        );
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("Excess waiting time:");
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
                builder.append("Variance: ");
                builder.append(statistic.getVariance());
                builder.append('\n');
                builder.append('\n');
            });
        return builder.toString();
    }

}
