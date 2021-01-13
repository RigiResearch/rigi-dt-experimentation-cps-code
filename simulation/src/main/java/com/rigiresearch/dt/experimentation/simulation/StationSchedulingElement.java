package com.rigiresearch.dt.experimentation.simulation;

import com.rigiresearch.dt.experimentation.simulation.graph.Line;
import com.rigiresearch.dt.experimentation.simulation.graph.Segment;
import com.rigiresearch.dt.experimentation.simulation.graph.Station;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import jsl.modeling.elements.entity.EntityType;
import jsl.modeling.elements.variable.RandomVariable;
import jsl.simulation.JSLEvent;
import jsl.simulation.SchedulingElement;
import lombok.Getter;
import org.apache.commons.configuration2.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A model element representing the simulated station.
 * @author Miguel Jimenez (miguel@uvic.ca)
 * @version $Id$
 * @since 0.1.0
 */
public final class StationSchedulingElement extends SchedulingElement {

    /**
     * The logger.
     */
    private static final Logger LOGGER =
        LoggerFactory.getLogger(StationSchedulingElement.class);

    /**
     * Lines and their corresponding stop within this station.
     */
    @Getter
    private final Map<Line, StopSchedulingElement> stops;

    /**
     * Random variables for the arrival times for each line.
     */
    private final Map<Line, RandomVariable> arrivals;

    /**
     * Buses available per line.
     */
    private final Map<Line, LinkedList<Bus>> buses;

    /**
     * The simulation configuration.
     */
    private final Configuration config;

    /**
     * The graph node.
     */
    private final Station node;

    /**
     * The parent element.
     */
    @Getter
    private final DtSimulation parent;

    /**
     * Default constructor.
     * @param parent The parent model
     * @param station The station's graph node
     * @param config The simulation configuration
     */
    public StationSchedulingElement(final DtSimulation parent, final Station station,
        final Configuration config) {
        super(parent.getModel(), station.getName());
        this.parent = parent;
        this.config = config;
        this.node = station;
        final Set<Segment> segments = station.getMetadata()
            .stream()
            .filter(Segment.class::isInstance)
            .map(Segment.class::cast)
            .collect(Collectors.toSet());
        this.stops = segments.stream()
            .collect(
                Collectors.toMap(
                    Segment::getLine,
                    segment ->
                        new StopSchedulingElement(this, segment.getFrom(), config)
                )
            );
        this.arrivals = segments.stream()
            .collect(
                Collectors.toMap(
                    Segment::getLine,
                    segment -> RandomVariableFactory
                        .get(
                            segment.getLine(),
                            segment.getFrom(),
                            DtSimulation.VariableType.BUS_ARRIVAL.getName(),
                            config
                        )
                        .apply(this)
                )
            );
        this.buses = segments.stream()
            // Create buses only if the line starts in this station
            .filter(tmp -> tmp.getLine().getFrom().equals(this.node))
            .collect(
                Collectors.toMap(
                    Segment::getLine,
                    segment -> {
                        final int fleet = this.config.getInt(
                            String.format(
                                "%s.%s",
                                segment.getLine().getName(),
                                DtSimulation.VariableType.FLEET.getName()
                            )
                        );
                        DtSimulation.log(
                            StationSchedulingElement.LOGGER,
                            this.getTime(),
                            segment.getLine(),
                            this.node,
                            "Creating bus fleet of %d buses",
                            fleet
                        );
                        final LinkedList<Bus> list = new LinkedList<>();
                        for (int count = 1; count <= fleet; count++) {
                            final String name = String.format(
                                "%s-bus-%d",
                                segment.getLine().getName(),
                                count
                            );
                            list.add(
                                new Bus(
                                    new EntityType(
                                        this,
                                        name
                                    ),
                                    segment.getLine(),
                                    name,
                                    this.config.getInt(
                                        String.format(
                                            "%s.%s",
                                            segment.getLine().getName(),
                                            DtSimulation.VariableType.CAPACITY.getName()
                                        )
                                    )
                                )
                            );
                        }
                        return list;
                    }
                )
            );
    }

    /**
     * Updates the links of each stop.
     */
    public void updateLinks() {
        this.stops.values().forEach(StopSchedulingElement::updateLinks);
    }

    /**
     * Schedule bus arrivals per stop.
     */
    @Override
    public void initialize() {
        this.stops.forEach(
            (line, stop) -> {
                // Schedule buses only if the line starts at this station
                if (!line.getFrom().equals(this.node)) {
                    return;
                }
                DtSimulation.log(
                    StationSchedulingElement.LOGGER,
                    this.getTime(),
                    line,
                    this.node,
                    "Scheduling buses"
                );
                this.scheduleEvent(
                    this::handleBusArrival,
                    this.arrivals.get(line),
                    this.buses.get(line).poll()
                );
            }
        );
    }

    /**
     * Handles bus arrival according to the associated line.
     * @param event The event containing the bus arriving at this station
     */
    private void handleBusArrival(final JSLEvent<Bus> event) {
        this.handleBusArrival(event.getMessage());
    }

    /**
     * Handles bus arrival according to the associated line.
     * @param bus The bus arriving at this station
     */
    public void handleBusArrival(final Bus bus) {
        DtSimulation.log(
            StationSchedulingElement.LOGGER,
            this.getTime(),
            bus.getLine(),
            this.node,
            "Bus %s arrived",
            bus.getName()
        );
        this.stops.get(bus.getLine())
            .handleBusArrival(bus);
    }

    @Override
    public String toString() {
        return String.format(
            "%s(%s)",
            this.getClass().getSimpleName(),
            this.node.getName()
        );
    }

}