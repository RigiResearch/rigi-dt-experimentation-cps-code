package com.rigiresearch.dt.experimentation.simulation;

import com.rigiresearch.dt.experimentation.simulation.graph.Segment;
import jsl.modeling.queue.QObject;
import jsl.modeling.queue.Queue;
import jsl.simulation.Model;

/**
 * A simulation model for the station concept.
 * @author Miguel Jimenez (miguel@uvic.ca)
 * @version $Id$
 * @since 0.1.0
 */
public final class StationModel extends Model {

    /**
     * Queue for bus service times.
     */
    private final Queue<QObject> service;

    /**
     * Queue for passenger waiting times.
     */
    private final Queue<QObject> wait;

    /**
     * Queue for bus transportation times.
     */
    private final Queue<QObject> transport;

    /**
     * Default constructor.
     * @param segment The segment's graph node
     */
    public StationModel(final Segment segment) {
        super(segment.getFrom().getStation().getName());
        this.service = new Queue<>(
            this,
            String.format(
                "ST-%s-%s",
                segment.getFrom().getName(),
                segment.getLine().getName()
            )
        );
        this.wait = new Queue<>(
            this,
            String.format(
                "WT-%s-%s",
                segment.getFrom().getName(),
                segment.getLine().getName()
            )
        );
        this.transport = new Queue<>(
            this,
            String.format(
                "TT-%s-%s-%s",
                segment.getFrom().getName(),
                segment.getTo().getName(),
                segment.getLine().getName()
            )
        );
    }

    @Override
    public String toString() {
        return String.format(
            "%s(serviceQ: %s, waitQ: %s, transportQ: %s)",
            this.getClass()
                .getSimpleName(),
            this.service.getName(),
            this.wait.getName(),
            this.transport.getName()
        );
    }

}
