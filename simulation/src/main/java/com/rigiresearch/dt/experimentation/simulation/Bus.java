package com.rigiresearch.dt.experimentation.simulation;

import com.rigiresearch.dt.experimentation.simulation.graph.Line;
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
     * The current occupation.
     */
    private int occupation = 0;

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
    }

    /**
     * Updates the current capacity of the bus
     * @param boarding The number of passengers boarding this bus
     * @param leaving The number of passengers getting off the bus
     */
    public void updateOccupation(final int boarding, final int leaving) {
        this.occupation += boarding;
        this.occupation -= leaving;
    }

    @Override
    public String toString() {
        return String.format(
            "%s(name: %s, capacity: %d, occupation: %d)",
            this.getClass().getSimpleName(),
            this.getName(),
            this.capacity,
            this.occupation
        );
    }

}
