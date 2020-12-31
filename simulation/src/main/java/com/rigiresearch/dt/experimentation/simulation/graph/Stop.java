package com.rigiresearch.dt.experimentation.simulation.graph;

import com.rigiresearch.middleware.graph.Graph;
import com.rigiresearch.middleware.graph.Parameter;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import lombok.Getter;

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
@Getter
public final class Stop extends Parameter {

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = -4492912001110903582L;

    /**
     * Object to recognize the "null" station.
     */
    private static final Station STATION_PILL = new Station();

    /**
     * This stop's containing station.
     */
    private Station station;

    /**
     * Empty constructor.
     */
    public Stop() {
        this("");
    }

    /**
     * Default constructor.
     * @param name This stop's unique name
     */
    public Stop (final String name) {
        super(name);
        this.station = Stop.STATION_PILL;
    }

    /**
     * Creates a new instance with the same name.
     * @return A non-null {@link Stop} instance
     */
    public Stop duplicate() {
        return new Stop(this.getName());
    }

    /**
     * Updates the station.
     * @param station The new station
     */
    @XmlTransient
    public void setStation(final Station station) {
        this.station = station;
    }

    @Override
    public String toString() {
        return String.format(
            "%s(name: %s, station: %s)",
            this.getClass()
                .getSimpleName(),
            this.getName(),
            this.station.getName()
        );
    }

}
