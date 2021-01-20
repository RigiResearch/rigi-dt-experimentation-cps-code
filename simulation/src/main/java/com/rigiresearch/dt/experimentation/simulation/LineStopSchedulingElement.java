package com.rigiresearch.dt.experimentation.simulation;

import com.rigiresearch.dt.experimentation.simulation.graph.Segment;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import jsl.modeling.elements.entity.EntityType;
import jsl.modeling.elements.variable.RandomVariable;
import jsl.modeling.queue.Queue;
import jsl.simulation.JSLEvent;
import jsl.simulation.SchedulingElement;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.configuration2.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A model element to handle events related to the line-stop pair.
 * @author Miguel Jimenez (miguel@uvic.ca)
 * @version $Id$
 * @since 0.1.0
 */
public final class LineStopSchedulingElement extends SchedulingElement {

    /**
     * The logger.
     */
    private static final Logger LOGGER =
        LoggerFactory.getLogger(LineStopSchedulingElement.class);

    /**
     * A bag of passengers created during the initialization phase, to be used
     * while the simulation is running. This is a limitation of the simulation
     * library.
     */
    private final LinkedList<Passenger> passengers;

    /**
     * Variable following the distribution of passenger arrival times.
     */
    private final RandomVariable passenger;

    /**
     * Variable following the distribution of bus transportation times.
     */
    private final RandomVariable transportation;

    /**
     * Queue for passenger waiting times.
     */
    private final Queue<Passenger> wait;

    /**
     * The graph node.
     */
    @Getter
    private final Segment node;

    /**
     * The parent model.
     */
    @Getter
    private final StopSchedulingElement parent;

    /**
     * The next stop.
     */
    @Setter
    private StationSchedulingElement next;

    /**
     * Default constructor.
     * @param parent The parent model
     * @param segment The segment representing this model
     * @param config The simulation configuration
     */
    public LineStopSchedulingElement(final StopSchedulingElement parent,
        final Segment segment, final Configuration config) {
        super(
            parent,
            String.format(
                "%s-%s-%s",
                segment.getLine().getName(),
                segment.getFrom().getStation().getName(),
                segment.getFrom().getName()
            )
        );
        this.parent = parent;
        this.node = segment;
        this.passenger = RandomVariableFactory.get(
            segment.getLine(),
            segment.getFrom(),
            DtSimulation.VariableType.PASSENGER_ARRIVAL.getName(),
            config
        ).apply(this);
        this.transportation = RandomVariableFactory.get(
            segment.getLine(),
            segment.getFrom(),
            DtSimulation.VariableType.TRANSPORTATION_TIME.getName(),
            config
        ).apply(this);
        this.wait = new Queue<>(
            this,
            String.format(
                "WT-%s-%s",
                segment.getFrom().getName(),
                segment.getLine().getName()
            )
        );
        // TODO The number of passengers to create could be a config property
        this.passengers = this.createPassengers(100);
    }

    @Override
    public void initialize() {
        this.schedulePassenger();
    }

    /**
     * Handles a passenger arrival.
     * @param event The JSL event
     */
    private void passengerArrival(final JSLEvent<Passenger> event) {
        this.wait.enqueue(event.getMessage());
        this.schedulePassenger();
    }

    /**
     * Schedules a passenger.
     */
    private void schedulePassenger() {
        this.scheduleEvent(
            this::passengerArrival,
            this.passenger,
            this.passengers.remove()
        );
    }

    /**
     * Updates the bus's current occupation with passengers that have been
     * waiting, and schedules the transportation time to the next stop.
     * @param bus The simulated bus
     */
    public void handleBusDeparture(final Bus bus) {
        final List<Passenger> boarding = this.nextPassengers(bus.availableSeats());
        bus.updateOccupation(boarding);
        DtSimulation.log(
            LineStopSchedulingElement.LOGGER,
            this.getTime(),
            bus.getLine(),
            this.node.getFrom().getStation(),
            this.node.getFrom(),
            "%d passengers just got onboard bus %s (new occupation: %d)",
            boarding.size(),
            bus.getName(),
            bus.occupation()
        );
        DtSimulation.log(
            LineStopSchedulingElement.LOGGER,
            this.getTime(),
            bus.getLine(),
            this.node.getFrom().getStation(),
            this.node.getFrom(),
            "Bus %s is ready to depart",
            bus.getName()
        );
        this.scheduleEvent(
            this::handleBusArrivalAtNextStop,
            this.transportation,
            bus
        );
    }

    /**
     * Dequeues passengers according to the available seats.
     * @param availableSeats Current number of available seats for the current bus
     * @return A non-null, possibly empty list of passengers
     */
    private List<Passenger> nextPassengers(final int availableSeats) {
        final List<Passenger> next = new ArrayList<>(availableSeats);
        for (int i = 0; i < availableSeats; i++) {
            if (this.wait.isEmpty()) {
                break;
            }
            next.add(this.wait.removeNext());
        }
        return next;
    }

    /**
     * Creates passengers for this line/stop.
     * @return A non-null, non-empty linked list of passengers.
     */
    private LinkedList<Passenger> createPassengers(final int n) {
        final LinkedList<Passenger> list = new LinkedList<>();
        for (int count = 1; count <= n; count++) {
            final String name = String.format(
                "passenger-%s-%s-%d",
                this.node.getLine().getName(),
                this.node.getFrom().getName(),
                count
            );
            list.add(new Passenger(new EntityType(this, name)));
        }
        return list;
    }

    /**
     * Send bus to next stop. If there is no next stop, the bus has finished its
     * journey. Then, issue the end of the journey by disposing the bus.
     * @param event The event containing the simulated bus
     */
    private void handleBusArrivalAtNextStop(final JSLEvent<Bus> event) {
        final Bus bus = event.getMessage();
        if (this.next == null) {
            DtSimulation.log(
                LineStopSchedulingElement.LOGGER,
                this.getTime(),
                bus.getLine(),
                this.node.getFrom().getStation(),
                this.node.getFrom(),
                "Bus %s finished its journey",
                bus.getName()
            );
            event.getMessage().disposePassengers();
            event.getMessage().dispose();
        } else {
            // Send the bus to the next station
            this.next.handleBusArrival(bus);
        }
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append(this.getClass().getSimpleName());
        builder.append('(');
        builder.append("serviceQ: ");
        builder.append(this.wait.getName());
        if (this.next != null) {
            builder.append(", next: ");
            builder.append(this.next.getName());
        }
        builder.append(')');
        return builder.toString();
    }

}
