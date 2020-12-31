package com.rigiresearch.dt.experimentation.simulation;

import com.rigiresearch.middleware.graph.GraphParser;
import java.io.IOException;
import java.util.Objects;
import javax.xml.bind.JAXBException;
import org.junit.jupiter.api.Test;

/**
 * Tests {@link DtSimulation}.
 * @author Miguel Jimenez (miguel@uvic.ca)
 * @version $Id$
 * @since 0.1.0
 */
class DtSimulationTest {

    /**
     * The name of the bindings resource.
     */
    private static final String BINDINGS = "bindings.xml";

    /**
     * The name of the demo graph resource.
     */
    private static final String GRAPH = "demo-graph.xml";

    @Test
    void testItInstantiatesTheStationModel() throws JAXBException, IOException {
        new DtSimulation()
            .run(
                new GraphParser()
                    .withBindings(DtSimulationTest.BINDINGS)
                    .instance(
                        Objects.requireNonNull(
                            Thread.currentThread()
                                .getContextClassLoader()
                                .getResourceAsStream(DtSimulationTest.GRAPH)
                        )
                    )
            );
    }

}
