package com.rigiresearch.dt.experimentation.simulation;

import com.rigiresearch.dt.experimentation.simulation.graph.Segment;
import com.rigiresearch.dt.experimentation.simulation.graph.Stop;
import jsl.modeling.elements.variable.RandomVariable;
import jsl.modeling.queue.Queue;
import jsl.simulation.JSLEvent;
import jsl.simulation.ModelElement;
import jsl.simulation.SchedulingElement;
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
     * Variable following the distribution of passenger arrival times.
     */
    private final RandomVariable passengers;

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
    private final Stop node;

    /**
     * Default constructor.
     * @param parent The parent model
     * @param segment The segment representing this model
     * @param config The simulation configuration
     */
    public LineStopSchedulingElement(final ModelElement parent,
        final Segment segment, final Configuration config) {
        super(
            parent,
            String.format(
                "%s-%s",
                segment.getLine().getName(),
                segment.getFrom().getStation().getName()
            )
        );
        this.node = segment.getFrom();
        this.passengers = RandomVariableFactory.get(
            segment.getLine(),
            DtSimulation.VariableType.PASSENGER_ARRIVAL.getName(),
            config
        ).apply(this);
        this.transportation = RandomVariableFactory.get(
            segment.getLine(),
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
    }

    /**
     * Updates the bus's current occupation with passengers that have been
     * waiting, and schedules the transportation time to the next stop.
     * @param bus The simulated bus
     */
    public void handleBusDeparture(final Bus bus) {
        // TODO Update the current occupation of the bus
        final int boarding = 10;
        final int leaving = 2;
        bus.updateOccupation(boarding, leaving);
        DtSimulation.log(
            LineStopSchedulingElement.LOGGER,
            this.getTime(),
            bus.getLine(),
            this.node.getStation(),
            this.node,
            "%d passengers just got onboard, and %d passengers left bus %s",
            boarding,
            leaving,
            bus.getName()
        );
        DtSimulation.log(
            LineStopSchedulingElement.LOGGER,
            this.getTime(),
            bus.getLine(),
            this.node.getStation(),
            this.node,
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
     * Send bus to next stop. If there is no next stop, the bus has finished its
     * journey. Then, issue the end of the journey by disposing the bus.
     * @param event The event containing the simulated bus
     */
    private void handleBusArrivalAtNextStop(final JSLEvent<Bus> event) {
        final Bus bus = event.getMessage();
        DtSimulation.log(
            LineStopSchedulingElement.LOGGER,
            this.getTime(),
            bus.getLine(),
            this.node.getStation(),
            this.node,
            "Bus %s just arrived at its next stop",
            bus.getName()
        );
        // TODO Send the bus to the next stop (not line-stop) in the bus's journey
        event.getMessage().dispose();
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append(this.getClass().getSimpleName());
        builder.append('(');
        builder.append("serviceQ: ");
        builder.append(this.wait.getName());
        builder.append(')');
        return builder.toString();
    }

}
