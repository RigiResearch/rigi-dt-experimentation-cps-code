package com.rigiresearch.dt.experimentation.simulation.graph;

import com.rigiresearch.middleware.graph.Graph;
import com.rigiresearch.middleware.graph.Node;
import com.rigiresearch.middleware.graph.Parameter;
import com.rigiresearch.middleware.graph.Property;
import java.util.Set;
import javax.xml.bind.annotation.XmlType;

/**
 * A graph node for bus stations.
 * @author Miguel Jimenez (miguel@uvic.ca)
 * @version $Id$
 * @since 0.1.0
 */
@XmlType(
    name = "station",
    namespace = Graph.NAMESPACE
)
public final class Station extends Node {

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = -9131687590315352929L;

    /**
     * Empty constructor.
     */
    public Station() {
        super();
    }

    /**
     * Default constructor.
     * @param name This station's name
     * @param stops The stops within this station
     * @param segments The segments connecting this station with other stations
     */
    public Station(final String name, final Set<Parameter> stops,
        final Set<Property> segments) {
        super(name, stops, segments);
    }

    /**
     * A string representation of this station.
     * @return A non-null string
     */
    @Override
    public String toString() {
        return new StringBuilder()
            .append(this.getClass().getSimpleName())
            .append('(')
            .append(this.getName())
            .append(", ")
            .append("stops: ")
            .append(this.getParameters(false).toString())
            .append(", ")
            .append("segments: ")
            .append(this.getMetadata().toString())
            .append(')')
            .toString();
    }

}
