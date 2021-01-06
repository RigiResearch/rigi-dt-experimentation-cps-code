package com.rigiresearch.dt.experimentation.simulation;

import com.rigiresearch.dt.experimentation.simulation.graph.Line;
import jsl.modeling.elements.entity.Entity;
import jsl.modeling.elements.entity.EntityType;

/**
 * A queue object representing a simulated passenger.
 * @author Miguel Jimenez (miguel@uvic.ca)
 * @version $Id$
 * @since 0.1.0
 */
public final class Passenger extends Entity {

    /**
     * Default constructor.
     * @param type The type associated with this entity
     * @param line The line associated with this passenger
     */
    public Passenger(final EntityType type, final Line line) {
        super(
            type,
            String.format(
                "P-%s-%d",
                line.getName(),
                DtSimulation.RANDOM.nextInt()
            )
        );
    }

    @Override
    public String toString() {
        return String.format(
            "%s(name: %s)",
            this.getClass().getSimpleName(),
            this.getName()
        );
    }

}
