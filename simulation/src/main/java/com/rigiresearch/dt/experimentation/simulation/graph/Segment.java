package com.rigiresearch.dt.experimentation.simulation.graph;

import com.rigiresearch.middleware.graph.Graph;
import com.rigiresearch.middleware.graph.Property;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * A line segment definition.
 * @author Miguel Jimenez (miguel@uvic.ca)
 * @version $Id$
 * @since 0.1.0
 */
@XmlType(
    name = "segment",
    namespace = Graph.NAMESPACE,
    propOrder = {"from", "to", "station", "line"}
)
@EqualsAndHashCode(callSuper = false)
@Getter
@ToString(of = {"from", "to", "station", "line"})
public final class Segment extends Property {

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = -7648461534480012856L;

    /**
     * Object to recognize the "null" stop.
     */
    private static Stop STOP_PILL = new Stop();

    /**
     * Object to recognize the "null" station.
     */
    private static Station STATION_PILL = new Station();

    /**
     * Object to recognize the "null" line.
     */
    private static Line LINE_PILL = new Line();

    /**
     * The stop where the segment starts.
     */
    @XmlIDREF
    @XmlAttribute
    private final Stop from;

    /**
     * The stop where the segment ends.
     */
    @XmlIDREF
    @XmlAttribute
    private final Stop to;

    /**
     * The station where segment ends.
     */
    @XmlIDREF
    @XmlAttribute
    private final Station station;

    /**
     * The line associated with this segment.
     */
    @XmlIDREF
    @XmlAttribute
    private final Line line;

    /**
     * Empty constructor.
     */
    public Segment() {
        super();
        this.from = Segment.STOP_PILL;
        this.to = Segment.STOP_PILL;
        this.station = Segment.STATION_PILL;
        this.line = Segment.LINE_PILL;
    }

    /**
     * Default constructor.
     * @param from The stop where the segment starts
     * @param to The stop where the segment ends
     * @param station The station where segment ends
     * @param line The line associated with this segment
     */
    public Segment(final Stop from, final Stop to, final Station station,
        final Line line) {
        super();
        this.from = from;
        this.to = to;
        this.station = station;
        this.line = line;
    }

}
