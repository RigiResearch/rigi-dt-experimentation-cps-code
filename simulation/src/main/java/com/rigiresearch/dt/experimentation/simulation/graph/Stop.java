package com.rigiresearch.dt.experimentation.simulation.graph;

import com.rigiresearch.middleware.graph.Graph;
import com.rigiresearch.middleware.graph.Parameter;
import javax.xml.bind.annotation.XmlType;

/**
 * A bus stop within a station.
 * @author Miguel Jimenez (miguel@uvic.ca)
 * @version $Id$
 * @since 0.1.0
 */
@XmlType(
    name = "stop",
    namespace = Graph.NAMESPACE
)
public final class Stop extends Parameter {

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = -4492912001110903582L;

    /**
     * Empty constructor.
     */
    public Stop() {
        super();
    }

    /**
     * Default constructor.
     * @param name This stop's unique name
     */
    public Stop (final String name) {
        super(name);
    }

    /**
     * Creates a new instance with the same name.
     * @return A non-null {@link Stop} instance
     */
    public Stop duplicate() {
        return new Stop(this.getName());
    }

}
