package com.rigiresearch.dt.experimentation.simulation;

import com.rigiresearch.dt.experimentation.simulation.graph.Line;
import com.rigiresearch.dt.experimentation.simulation.graph.Segment;
import com.rigiresearch.dt.experimentation.simulation.graph.Stop;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import jsl.modeling.elements.variable.RandomVariable;
import jsl.modeling.queue.Queue;
import jsl.simulation.JSLEvent;
import jsl.simulation.SchedulingElement;
import lombok.Getter;
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
    private final Map<Line, LineStopSchedulingElement> models;

    /**
     * Random variables based on the lines' service time distributions.
     */
    private final Map<Line, RandomVariable> services;

    /**
     * The parent model.
     */
    @Getter
    private final StationSchedulingElement parent;

    /**
     * Default constructor.
     * @param parent The parent model
     * @param stop The stop's graph node
     * @param config The simulation configuration
     */
    public StopSchedulingElement(final StationSchedulingElement parent,
        final Stop stop, final Configuration config) {
        super(parent, stop.getName());
        this.parent = parent;
        this.config = config;
        this.node = stop;
        this.service =
            new Queue<>(this, String.format("ST-%s", stop.getName()));
        this.service.setInitialDiscipline(Queue.Discipline.FIFO);
        // First, find lines stopping at this stop
        final Set<Line> lines = stop.getStation()
            .getMetadata()
            .stream()
            .filter(Segment.class::isInstance)
            .map(Segment.class::cast)
            .filter(tmp -> tmp.getFrom().equals(stop))
            .map(Segment::getLine)
            .collect(Collectors.toSet());
        this.models = this.initializeModels(lines);
        this.services = this.initializeServiceTimeVars(lines);
    }

    /**
     * Updates the next stop for each model, so that they can simulate the bus
     * arrival.
     */
    public void updateLinks() {
        for (final LineStopSchedulingElement model : this.models.values()) {
            final Segment segment = model.getNode();
            // Get the next segment from the line
            final Optional<Segment> next = segment.getLine().segment(segment.getTo());
            // Then, get the model corresponding to that stop and update the current model
            next.ifPresent(value -> {
                final LineStopSchedulingElement tmp = this.parent.getParent()
                    .model(value.getFrom().getStation())
                    .getStops()
                    .get(value.getLine())
                    .models
                    .get(value.getLine());
                if (tmp != null) {
                    model.setNext(tmp.getParent().parent);
                }
                final Optional<Segment> last = tmp.getNode()
                    .getLine()
                    .segment(tmp.getNode().getTo());
                if (!last.isPresent()) {
                    // This is the end of the line
                    // Take the next station from the same segment
                    tmp.setNext(this.parent.getParent()
                        .model(value.getTo().getStation()));
                }
            });
        }
    }

    /**
     * Enqueues a bus arriving at this stop.
     * @param bus The arriving bus
     */
    public void handleBusArrival(final Bus bus) {
        final boolean empty = this.service.isEmpty();
        this.service.enqueue(bus);
        if (empty) {
            DtSimulation.log(
                StopSchedulingElement.LOGGER,
                this.getTime(),
                bus.getLine(),
                this.node.getStation(),
                this.node,
                "Bus %s is ready to onboard passengers",
                bus.getName()
            );
            this.scheduleEvent(
                this::handleBusDeparture,
                this.services.get(bus.getLine()),
                bus
            );
        }
    }

    /**
     * Dequeues the given bus from the service queue
     * @param event The event containing the bus departing from this stop
     */
    public void handleBusDeparture(final JSLEvent<Bus> event) {
        final Bus bus = event.getMessage();
        if (this.service.isNotEmpty()) {
            final Bus next = this.service.removeFirst();
            if (bus.equals(next)) {
                this.models.get(bus.getLine())
                    .handleBusDeparture(bus);
                if (this.service.isNotEmpty()) {
                    DtSimulation.log(
                        StopSchedulingElement.LOGGER,
                        this.getTime(),
                        this.service.peekFirst().getLine(),
                        this.node.getStation(),
                        this.node,
                        "Bus %s is ready to onboard passengers",
                        this.service.peekFirst().getName()
                    );
                    this.scheduleEvent(
                        this::handleBusDeparture,
                        this.services.get(this.service.peekFirst().getLine()),
                        this.service.peekFirst()
                    );
                }
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
        } else {
            throw new IllegalStateException(
                String.format(
                    "Bus %s arrived but the service queue was empty",
                    bus
                )
            );
        }
    }

    /**
     * Creates a line-stop model for each line that stops at this stop.
     * @param lines The lines stopping at this stop
     * @return A list of line-stop models
     */
    private Map<Line, LineStopSchedulingElement> initializeModels(
        final Collection<Line> lines) {
        return lines.stream().collect(
            Collectors.toMap(
                Function.identity(),
                line -> new LineStopSchedulingElement(
                    this,
                    line.segment(this.node).get(),
                    this.config
                )
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
                        .get(line, name, this.config, this.node.getName())
                        .apply(this)
                )
            );
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
