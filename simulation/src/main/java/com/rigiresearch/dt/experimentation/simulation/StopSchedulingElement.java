package com.rigiresearch.dt.experimentation.simulation;

import com.rigiresearch.dt.experimentation.simulation.graph.Line;
import com.rigiresearch.dt.experimentation.simulation.graph.Segment;
import com.rigiresearch.dt.experimentation.simulation.graph.Stop;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import jsl.modeling.elements.variable.RandomVariable;
import jsl.modeling.queue.Queue;
import jsl.simulation.JSLEvent;
import jsl.simulation.ModelElement;
import jsl.simulation.SchedulingElement;
import org.apache.commons.configuration2.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A simulation model for the stop concept.
 * @author Miguel Jimenez (miguel@uvic.ca)
 * @version $Id$
 * @since 0.1.0
 */
public final class StopSchedulingElement extends SchedulingElement {

    /**
     * The logger.
     */
    private static final Logger LOGGER =
        LoggerFactory.getLogger(StopSchedulingElement.class);

    /**
     * The simulation configuration.
     */
    private final Configuration config;

    /**
     * The graph node.
     */
    private final Stop node;

    /**
     * Queue for bus service times.
     * May include buses from several lines.
     */
    private final Queue<Bus> service;

    /**
     * Line-Stop models encapsulated in this stop model.
     */
    private final Map<Line, List<LineStopSchedulingElement>> models;

    /**
     * Random variables based on the lines' service time distributions.
     */
    private final Map<Line, RandomVariable> services;

    /**
     * Default constructor.
     * @param parent The parent model
     * @param stop The stop's graph node
     * @param config The simulation configuration
     */
    public StopSchedulingElement(final ModelElement parent, final Stop stop,
        final Configuration config) {
        super(parent, stop.getName());
        this.config = config;
        this.node = stop;
        this.service =
            new Queue<>(this, String.format("ST-%s", stop.getName()));
        // First, find lines stopping at this stop
        final List<Line> lines = stop.getStation()
            .getMetadata()
            .stream()
            .filter(Segment.class::isInstance)
            .map(Segment.class::cast)
            .filter(tmp -> tmp.getFrom().equals(stop))
            .map(Segment::getLine)
            .collect(Collectors.toList());
        // TODO Set the next stop for each model
        this.models = this.initializeModels(lines);
        this.services = this.initializeServiceTimeVars(lines);
    }

    /**
     * Enqueues a bus arriving at this stop.
     * @param bus The arriving bus
     */
    public void handleBusArrival(final Bus bus) {
        StopSchedulingElement.LOGGER.debug(
            "Bus {} arrived at stop {} (Station: {}, time: {})",
            bus.getName(),
            this.getName(),
            this.node.getStation().getName(),
            this.getTime()
        );
        this.service.enqueue(bus);
        this.scheduleEvent(
            this::handleBusDeparture,
            this.services.get(bus.getLine()),
            bus
        );
    }

    /**
     * Dequeues the given bus from the service queue
     * @param event The event containing the bus departing from this stop
     */
    public void handleBusDeparture(final JSLEvent<Bus> event) {
        final Bus bus = event.getMessage();
        StopSchedulingElement.LOGGER.debug(
            "Bus {} is ready to onboard passengers at stop {} (Station: {}, time: {})",
            bus.getName(),
            this.getName(),
            this.node.getStation().getName(),
            this.getTime()
        );
        if (this.service.isNotEmpty()) {
            final Bus next = this.service.removeNext();
            if (bus.equals(next)) {
                this.models.get(bus.getLine())
                    .get(0)
                    .handleBusDeparture(bus);
            } else {
                throw new IllegalStateException(
                    String.format(
                        "Object mismatch at stop %s. The bus leaving the stop "
                            + "was %s, but bus %s was at the front of the queue.",
                        this.getName(),
                        bus.getName(),
                        next.getName()
                    )
                );
            }
        }
    }

    /**
     * Creates a line-stop model for each line that stops at this stop. Each
     * model will be in charge of handling bus and passenger arrivals for the
     * corresponding line, as well as transportation time to the next stop. Once
     * a bus reaches the next stop, the model will invoke the given callback, so
     * that the next model in the line's journey can schedule the bus (i.e.,
     * record its arrival). Based on this, only the first stop needs to know the
     * buses' arrival time. Subsequent stops only need to know their service and
     * transportation times.
     * @param lines The lines stopping at this stop
     * @return A list of line-stop models
     */
    private Map<Line, List<LineStopSchedulingElement>> initializeModels(
        final List<Line> lines) {
        return lines.stream().collect(
            Collectors.toMap(
                Function.identity(),
                line -> {
                    // Create a line-stop model for each pair of stops in
                    //  the line's journey
                    final LinkedList<Stop> journey = line.journey();
                    final List<LineStopSchedulingElement> list =
                        new ArrayList<>(journey.size());
                    Stop from = journey.poll();
                    Stop to = journey.poll();
                    while (from != null && to != null) {
                        list.add(
                            new LineStopSchedulingElement(
                                this,
                                new Segment(from, to, line),
                                this.config
                            )
                        );
                        from = journey.poll();
                        to = journey.poll();
                    }
                    return list;
                }
            )
        );
    }

    /**
     * Initializes the service time variables.
     * @param lines The lines stopping at this stop
     * @return A non-null map
     */
    private Map<Line, RandomVariable> initializeServiceTimeVars(
        final Collection<Line> lines) {
        final String name = DtSimulation.VariableType.SERVICE_TIME.getName();
        return lines.stream()
            .collect(
                Collectors.toMap(
                    Function.identity(),
                    line -> RandomVariableFactory
                        .get(line, name, this.config)
                        .apply(this)
                )
            );
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append(this.getClass().getSimpleName());
        builder.append('(');
        builder.append("serviceQ: ");
        builder.append(this.service.getName());
        builder.append(')');
        builder.append('\n');
        this.models.forEach((line, stops) -> {
            builder.append(line);
            builder.append('\n');
            stops.forEach(builder::append);
        });
        builder.append('\n');
        return builder.toString();
    }

}
