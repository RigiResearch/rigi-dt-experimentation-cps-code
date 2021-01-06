package com.rigiresearch.dt.experimentation.simulation;

import com.rigiresearch.dt.experimentation.simulation.graph.Line;
import com.rigiresearch.dt.experimentation.simulation.graph.Segment;
import com.rigiresearch.dt.experimentation.simulation.graph.Station;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import jsl.modeling.elements.entity.EntityType;
import jsl.modeling.elements.variable.RandomVariable;
import jsl.simulation.JSLEvent;
import jsl.simulation.ModelElement;
import jsl.simulation.SchedulingElement;
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
     * Default constructor.
     * @param parent The parent model
     * @param station The station's graph node
     * @param config The simulation configuration
     */
    public StationSchedulingElement(final ModelElement parent, final Station station,
        final Configuration config) {
        super(parent, station.getName());
        this.config = config;
        final List<Segment> segments = station.getMetadata()
            .stream()
            .filter(Segment.class::isInstance)
            .map(Segment.class::cast)
            .collect(Collectors.toList());
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
                            DtSimulation.VariableType.BUS_ARRIVAL.getName(),
                            config
                        )
                        .apply(this)
                )
            );
        this.buses = segments.stream()
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
                        final LinkedList<Bus> list = new LinkedList<>();
                        for (int count = 1; count <= fleet; count++) {
                            list.add(
                                new Bus(
                                    new EntityType(
                                        this,
                                        String.format(
                                            "%s-bus-%d",
                                            segment.getLine().getName(),
                                            count
                                        )
                                    ),
                                    segment.getLine(),
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
     * Schedule bus arrivals per stop.
     */
    @Override
    public void initialize() {
        this.stops.forEach(
            (line, stop) -> {
                final int capacity = this.config.getInt(
                    String.format(
                        "%s.%s",
                        line.getName(),
                        DtSimulation.VariableType.CAPACITY.getName()
                    )
                );
                StationSchedulingElement.LOGGER.debug(
                    "Scheduling buses for line {} at station {} (capacity: {})",
                    line.getName(),
                    this.getName(),
                    capacity
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
        final Bus bus = event.getMessage();
        StationSchedulingElement.LOGGER.debug(
            "Bus {} arrived at station {} (time: {})",
            bus.getName(),
            this.getName(),
            this.getTime()
        );
        this.stops.get(bus.getLine())
            .handleBusArrival(bus);
    }

}
