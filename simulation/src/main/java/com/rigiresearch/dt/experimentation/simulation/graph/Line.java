package com.rigiresearch.dt.experimentation.simulation.graph;

import com.rigiresearch.middleware.graph.Graph;
import com.rigiresearch.middleware.graph.Node;
import java.util.Collections;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlType;
import lombok.Setter;

/**
 * A graph node for bus lines.
 * @author Miguel Jimenez (miguel@uvic.ca)
 * @version $Id$
 * @since 0.1.0
 */
@XmlType(
    name = "line",
    namespace = Graph.NAMESPACE,
    propOrder = {"name", "from", "to"}
)
@Setter
public final class Line extends Node {

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = -502402205007529405L;

    /**
     * Object to recognize the "null" station.
     */
    private static final Station STATION_PILL = new Station();

    /**
     * The station where this line starts.
     */
    @XmlIDREF
    @XmlAttribute
    private Station from;

    /**
     * The station where this line ends.
     */
    @XmlIDREF
    @XmlAttribute
    private Station to;

    /**
     * Empty constructor.
     */
    public Line() {
        super();
        this.from = Line.STATION_PILL;
        this.to = Line.STATION_PILL;
    }

    /**
     * Secondary constructor.
     */
    public Line(final String name) {
        this(name, Line.STATION_PILL, Line.STATION_PILL);
    }

    /**
     * Default constructor.
     * @param name This line's unique name
     * @param from The station where this line starts
     * @param to The station where this line ends
     */
    public Line(final String name, final Station from, final Station to) {
        super(name, Collections.emptySet(), Collections.emptySet());
        this.from = from;
        this.to = to;
    }

    @Override
    public String toString() {
        return new StringBuilder()
            .append(this.getClass().getSimpleName())
            .append('(')
            .append(this.getName())
            .append(", from: ")
            .append(this.from.getName())
            .append(", to: ")
            .append(this.to.getName())
            .append(')')
            .toString();
    }

}
