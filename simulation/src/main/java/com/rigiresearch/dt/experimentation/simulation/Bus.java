package com.rigiresearch.dt.experimentation.simulation;

import com.rigiresearch.dt.experimentation.simulation.graph.Line;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import jsl.modeling.elements.entity.Entity;
import jsl.modeling.elements.entity.EntityType;
import lombok.Getter;

/**
 * A queue object representing a simulated bus.
 * @author Miguel Jimenez (miguel@uvic.ca)
 * @version $Id$
 * @since 0.1.0
 */
@Getter
public final class Bus extends Entity {

    /**
     * The line with which this bus is associated.
     */
    private final Line line;

    /**
     * The maximum passenger capacity.
     */
    private final int capacity;

    /**
     * The passengers in this bus.
     */
    private List<Passenger> passengers;

    /**
     * Default constructor.
     * @param type The type associated with this entity
     * @param line The line with which this bus is associated
     * @param name A unique name
     * @param capacity The maximum passenger capacity
     */
    public Bus(final EntityType type, final Line line, final String name,
        final int capacity) {
        super(type, name);
        this.line = line;
        this.capacity = capacity;
        this.passengers = new ArrayList<>(capacity);
    }

    /**
     * Updates the passengers of the bus
     * @param boarding The passengers boarding this bus
     */
    public void updateOccupation(final Collection<Passenger> boarding) {
        if (this.passengers.size() + boarding.size() > this.capacity) {
            throw new IllegalArgumentException("Too many passengers");
        }
        this.passengers.addAll(boarding);
    }

    @Override
    public String toString() {
        return String.format(
            "%s(name: %s, capacity: %d, occupation: %d)",
            this.getClass().getSimpleName(),
            this.getName(),
            this.capacity,
            this.occupation()
        );
    }

    /**
     * The current occupation of this bus.
     * @return A positive number
     */
    public int occupation() {
        return this.passengers.size();
    }

    /**
     * The current availability of this bus.
     * @return A positive number
     */
    public int availableSeats() {
        return this.capacity - this.occupation();
    }

    /**
     * Calls the dispose method on all passengers.
     */
    public void disposePassengers() {
        this.passengers.forEach(Passenger::dispose);
    }
}
