package com.rigiresearch.dt.experimentation.simulation;

import com.rigiresearch.dt.experimentation.simulation.graph.Line;
import com.rigiresearch.dt.experimentation.simulation.graph.Station;
import com.rigiresearch.dt.experimentation.simulation.graph.Stop;
import com.rigiresearch.middleware.graph.Graph;
import com.rigiresearch.middleware.graph.Node;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import jsl.simulation.Simulation;
import jsl.utilities.statistic.Statistic;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.configuration2.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A Digital Twin simulation for the Transportation case study.
 * @author Miguel Jimenez (miguel@uvic.ca)
 * @version $Id$
 * @since 0.1.0
 */
public final class DtSimulation extends Simulation {

    /**
     * The logger.
     */
    private static final Logger LOGGER =
        LoggerFactory.getLogger(DtSimulation.class);

    /**
     * An empty double array.
     */
    public static final Double[] EMPTY_ARRAY = new Double[0];

    /**
     * Map of station-models.
     */
    private final Map<Station, StationSchedulingElement> models;

    /**
     * The graph on which this simulation is based.
     */
    @Getter
    private final Graph<Node> graph;

    /**
     * Default constructor.
     * @param graph The input graph
     * @param config The configuration options
     */
    public DtSimulation(final Graph<Node> graph, final Configuration config) {
        super("DT Simulation");
        this.graph = graph;
        this.models = new HashMap<>(graph.getNodes().size());
        graph.getNodes()
            .stream()
            .filter(Station.class::isInstance)
            .map(Station.class::cast)
            .forEach(station -> {
                final StationSchedulingElement model =
                    new StationSchedulingElement(this, station, config);
                DtSimulation.LOGGER.debug("Instantiated station model {}", model.getName());
                this.models.put(station, model);
            });
        this.models.values().forEach(StationSchedulingElement::updateLinks);
    }

    /**
     * Returns the passenger waiting times per line.
     * @return A non-null, possibly empty map
     */
    public Map<Line, List<Statistic>> waitingTimes() {
        return this.modelToMap(StationSchedulingElement::observedWaitingTimes);
    }

    /**
     * Returns the observed headway times per line.
     * @return A non-null, possibly empty map
     */
    public Map<Line, List<Statistic>> observedHeadways() {
        return this.modelToMap(StationSchedulingElement::observedHeadways);
    }

    /**
     * Returns the statistics collected from lines passing through each model.
     * @param function The function mapping from model to statistics per line
     * @return A non-null, possibly empty map
     */
    private Map<Line, List<Statistic>> modelToMap(
        Function<StationSchedulingElement, Map<Line, Statistic>> function) {
        final Map<Line, List<Statistic>> statistics = new HashMap<>(this.models.size());
        this.models.values().forEach(model -> {
            final Map<Line, Statistic> map = function.apply(model);
            map.forEach((line, statistic) -> {
                statistics.putIfAbsent(line, new ArrayList<>());
                statistics.get(line).add(statistic);
            });
        });
        return statistics;
    }

    /**
     * Get a station model.
     * @param station The station node
     * @return The model or null
     */
    public StationSchedulingElement model(final Station station) {
        return this.models.get(station);
    }

    /**
     * Logs information using a standard format.
     * @param logger The logger being used
     * @param time The current simulation time
     * @param line The line associated with the log trace
     * @param station The station associated with the log trace
     * @param stop The stop associated with the log trace
     * @param format A format string
     * @param args Arguments to use in the format string
     */
    public static void log(final Logger logger, final double time,
        final Line line, final Station station, final Stop stop,
        final String format, final Object... args) {
        logger.debug(
            "[{}]\t{}\t{}\t{}\t{}",
            time,
            line.getName(),
            station.getName(),
            stop.getName(),
            String.format(format, args)
        );
    }

    /**
     * Logs information using a standard format.
     * @param logger The logger being used
     * @param time The current simulation time
     * @param line The line associated with the log trace
     * @param station The station associated with the log trace
     * @param format A format string
     * @param args Arguments to use in the format string
     */
    public static void log(final Logger logger, final double time,
        final Line line, final Station station, final String format,
        final Object... args) {
        logger.debug(
            "[{}]\t{}\t{}\t\t{}",
            time,
            line.getName(),
            station.getName(),
            String.format(format, args)
        );
    }

    /**
     * Constants for the configured variables.
     */
    @Getter
    @RequiredArgsConstructor
    public enum VariableType {
        BUS_ARRIVAL("arrival"),
        CAPACITY("capacity"),
        FLEET("fleet"),
        PASSENGER_ARRIVAL("passenger"),
        SERVICE_TIME("service"),
        TRANSPORTATION_TIME("transportation");

        /**
         * A user-friendly name for this type of variable.
         */
        private final String name;
    }

}
