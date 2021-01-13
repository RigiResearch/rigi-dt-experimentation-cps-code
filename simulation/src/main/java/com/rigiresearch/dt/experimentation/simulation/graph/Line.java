package com.rigiresearch.dt.experimentation.simulation.graph;

import com.rigiresearch.middleware.graph.Graph;
import com.rigiresearch.middleware.graph.Node;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Optional;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

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
    @XmlAttribute(required = true)
    private Station from;

    /**
     * The station where this line ends.
     */
    @XmlIDREF
    @XmlAttribute(required = true)
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
        super(name, Collections.emptySet(), Collections.emptyList());
        this.from = from;
        this.to = to;
    }

    /**
     * Constructs a sequence of stops that represent this line's journey from
     * {@code from} to {@code to}.
     * @return A non-null linked-list
     */
    public LinkedList<Stop> journey() {
        final LinkedList<Stop> list = new LinkedList<>();
        Optional<Segment> segment = this.segment(this.from);
        segment.ifPresent(value -> list.add(value.getFrom()));
        while (segment.isPresent()) {
            list.add(segment.get().getTo());
            segment = this.segment(segment.get().getTo());
        }
        if (!list.getLast().getStation().equals(this.to)) {
            throw new IllegalStateException(
                String.format("Incomplete journey for line %s", this.getName())
            );
        }
        return list;
    }

    /**
     * Finds a segment containing this line at the given stop.
     * @param stop The stop associated with the line
     * @return The segment, or empty if the line does not stop at the given stop
     */
    public Optional<Segment> segment(final Stop stop) {
        return stop.getStation().getMetadata()
            .stream()
            .filter(Segment.class::isInstance)
            .map(Segment.class::cast)
            .filter(tmp -> tmp.getLine().equals(this))
            .filter(tmp -> tmp.getFrom().equals(stop))
            .findFirst();
    }

    /**
     * Finds a segment containing this line at the given station.
     * @param station The station associated with the line
     * @return The segment, or empty if the line does not stop at the given station
     */
    public Optional<Segment> segment(final Station station) {
        return station.getMetadata()
            .stream()
            .filter(Segment.class::isInstance)
            .map(Segment.class::cast)
            .filter(tmp -> tmp.getLine().equals(this))
            .filter(tmp -> tmp.getFrom().getStation().equals(station))
            .findFirst();
    }

    public Station getFrom() {
        return this.from;
    }

    public Station getTo() {
        return this.to;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        final Line line = (Line) o;
        return new EqualsBuilder()
            .appendSuper(super.equals(o))
            .append(this.getName(), this.getName())
            .append(this.from, line.from)
            .append(this.to, line.to)
            .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
            .appendSuper(super.hashCode())
            .append(this.getName())
            .append(this.from)
            .append(this.to)
            .toHashCode();
    }

    @Override
    public int compareTo(final Node other) {
        final int result;
        if (other instanceof Line) {
            final Line tmp = (Line) other;
            result = this.getName().compareTo(tmp.getName());
        } else {
            result = 1;
        }
        return result;
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
