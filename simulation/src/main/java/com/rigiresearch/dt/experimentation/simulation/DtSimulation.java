package com.rigiresearch.dt.experimentation.simulation;

import com.rigiresearch.dt.experimentation.simulation.graph.Segment;
import com.rigiresearch.middleware.graph.Graph;
import com.rigiresearch.middleware.graph.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A Digital Twin simulation for the Transportation case study.
 * @author Miguel Jimenez (miguel@uvic.ca)
 * @version $Id$
 * @since 0.1.0
 */
public final class DtSimulation {

    /**
     * The logger.
     */
    private static final Logger LOGGER =
        LoggerFactory.getLogger(DtSimulation.class);

    /**
     * Runs the simulation.
     * @param graph The input graph
     */
    public void run(final Graph<Node> graph) {
        graph.getNodes()
            .forEach(node ->
                node.getMetadata()
                    .stream()
                    .filter(Segment.class::isInstance)
                    .map(Segment.class::cast)
                    .forEach(segment ->
                        DtSimulation.LOGGER.info("{}", new StationModel(segment))
                    )
            );
    }

}
