package com.rigiresearch.dt.experimentation.simulation;

import com.rigiresearch.dt.experimentation.simulation.graph.Station;
import com.rigiresearch.middleware.graph.Graph;
import com.rigiresearch.middleware.graph.Node;
import java.security.SecureRandom;
import java.util.Random;
import jsl.simulation.Simulation;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.configuration2.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A Digital Twin simulation for the Transportation case study.
 * @author Miguel Jimenez (miguel@uvic.ca)
 * @version $Id$
 * @since 0.1.0
 */
public final class DtSimulation extends Simulation {

    /**
     * The logger.
     */
    private static final Logger LOGGER =
        LoggerFactory.getLogger(DtSimulation.class);

    /**
     * A random number generator.
     */
    public static final Random RANDOM = new SecureRandom("seed".getBytes());

    /**
     * Default constructor.
     * @param graph The input graph
     * @param config The configuration options
     */
    public DtSimulation(final Graph<Node> graph, final Configuration config) {
        super("DT Simulation");
        graph.getNodes()
            .stream()
            .filter(Station.class::isInstance)
            .map(Station.class::cast)
            .forEach(station -> {
                final StationSchedulingElement element =
                    new StationSchedulingElement(this.getModel(), station, config);
                DtSimulation.LOGGER.info("Instantiated station model {}", element.getName());
            });
    }

    /**
     * Constants for the configured variables.
     */
    @Getter
    @RequiredArgsConstructor
    public enum VariableType {
        BUS_ARRIVAL("arrival"),
        CAPACITY("capacity"),
        FLEET("fleet"),
        PASSENGER_ARRIVAL("passenger"),
        SERVICE_TIME("service"),
        TRANSPORTATION_TIME("transportation");

        /**
         * A user-friendly name for this type of variable.
         */
        private final String name;
    }

}
