package com.rigiresearch.dt.experimentation.simulation.graph;

import com.rigiresearch.middleware.graph.Graph;
import com.rigiresearch.middleware.graph.Property;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * A line segment definition.
 * @author Miguel Jimenez (miguel@uvic.ca)
 * @version $Id$
 * @since 0.1.0
 */
@XmlType(
    name = "segment",
    namespace = Graph.NAMESPACE,
    propOrder = {"from", "to", "line"}
)
public final class Segment extends Property {

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = -7648461534480012856L;

    /**
     * Object to recognize the "null" stop.
     */
    private static final Stop STOP_PILL = new Stop();

    /**
     * Object to recognize the "null" line.
     */
    private static final Line LINE_PILL = new Line();

    /**
     * The stop where the segment starts.
     */
    @XmlIDREF
    @XmlAttribute(required = true)
    private Stop from;

    /**
     * The stop where the segment ends.
     */
    @XmlIDREF
    @XmlAttribute(required = true)
    private Stop to;

    /**
     * The line associated with this segment.
     */
    @XmlIDREF
    @XmlAttribute(required = true)
    private Line line;

    /**
     * Empty constructor.
     */
    public Segment() {
        super();
        this.from = Segment.STOP_PILL;
        this.to = Segment.STOP_PILL;
        this.line = Segment.LINE_PILL;
    }

    /**
     * Default constructor.
     * @param from The stop where the segment starts
     * @param to The stop where the segment ends
     * @param line The line associated with this segment
     */
    public Segment(final Stop from, final Stop to, final Line line) {
        super(String.format("%s-%s-%s", line.getName(), from.getName(), to.getName()), "");
        this.from = from;
        this.to = to;
        this.line = line;
    }

    public Stop getFrom() {
        return this.from;
    }

    public Stop getTo() {
        return this.to;
    }

    public Line getLine() {
        return this.line;
    }

    @Override
    public int compareTo(final Property other) {
        final int result;
        if (other instanceof Segment) {
            final Segment tmp = (Segment) other;
            final String str1 = String.format(
                "%s-%s-%s",
                this.line.getName(),
                this.from.getName(),
                this.to.getName()
            );
            final String str2 = String.format(
                "%s-%s-%s",
                tmp.getLine().getName(),
                tmp.getFrom().getName(),
                tmp.getTo().getName()
            );
            result = str1.compareTo(str2);
        } else {
            result = 1;
        }
        return result;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        final Segment segment = (Segment) o;
        return new EqualsBuilder().appendSuper(super.equals(o))
            .append(this.from, segment.from)
            .append(this.to, segment.to)
            .append(this.line, segment.line)
            .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
            .appendSuper(super.hashCode())
            .append(this.from)
            .append(this.to)
            .append(this.line)
            .toHashCode();
    }

    @Override
    public String toString() {
        return String.format(
            "%s(from: %s.%s, to: %s.%s, line: %s)",
            this.getClass()
                .getSimpleName(),
            this.from.getStation()
                .getName(),
            this.from.getName(),
            this.to.getStation()
                .getName(),
            this.to.getName(),
            this.line.getName()
        );
    }

}
