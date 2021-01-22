package com.rigiresearch.dt.experimentation.simulation.metrics;

import com.rigiresearch.dt.experimentation.simulation.DtSimulation;
import com.rigiresearch.dt.experimentation.simulation.graph.Line;
import java.util.List;
import jsl.utilities.statistic.Statistic;

/**
 * The headway coefficient of variation metric.
 * @author Miguel Jimenez (miguel@uvic.ca)
 * @version $Id$
 * @since 0.1.0
 */
public final class HeadwayCoefficientOfVariation
    implements SimulationMetric<Double> {

    /**
     * The simulation from which this metric is computed.
     */
    private final DtSimulation simulation;

    /**
     * The observed line headways.
     */
    private final ObservedLineHeadway observed;

    /**
     * Default constructor.
     * @param simulation The simulation from which this metric is computed
     */
    public HeadwayCoefficientOfVariation(final DtSimulation simulation) {
        this.simulation = simulation;
        this.observed = new ObservedLineHeadway(this.simulation);
    }

    @Override
    public Double value(final Line line) {
        final Statistic statistic = this.observed.value(line);
        return statistic.getStandardDeviation() / statistic.getAverage();
    }

    @Override
    public List<Double> values(final Line line) {
        throw new UnsupportedOperationException(
            "The headway coefficient of variation is not defined for a single stop"
        );
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("Headway coefficient of variation:");
        builder.append('\n');
        this.simulation.getGraph()
            .getNodes()
            .stream()
            .filter(Line.class::isInstance)
            .map(Line.class::cast)
            .forEach(line -> {
                builder.append(line.getName());
                builder.append(": ");
                builder.append(this.value(line));
                builder.append('\n');
            });
        return builder.toString();
    }

}
