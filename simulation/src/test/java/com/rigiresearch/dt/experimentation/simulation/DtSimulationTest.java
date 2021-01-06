package com.rigiresearch.dt.experimentation.simulation;

import com.rigiresearch.middleware.graph.GraphParser;
import java.io.IOException;
import java.util.Objects;
import javax.xml.bind.JAXBException;
import jsl.simulation.Simulation;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.io.FileHandler;
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

    /**
     * The simulation configuration file.
     */
    private static final String PROPERTIES_FILE = "simulation.properties";

    @Test
    void testItRuns()
        throws JAXBException, IOException, ConfigurationException {
        final Simulation simulation = new DtSimulation(
            new GraphParser()
                .withBindings(DtSimulationTest.BINDINGS)
                .instance(
                    Objects.requireNonNull(
                        Thread.currentThread()
                            .getContextClassLoader()
                            .getResourceAsStream(DtSimulationTest.GRAPH)
                    )
                ),
            DtSimulationTest.config()
        );
        simulation.run();
    }

    /**
     * Loads the configuration file based on a resource.
     * @return A configuration instance
     * @throws ConfigurationException If there is a problem loading the resource
     */
    private static Configuration config() throws ConfigurationException {
        final FileBasedConfigurationBuilder<FileBasedConfiguration> builder =
            new FileBasedConfigurationBuilder<FileBasedConfiguration>(
                PropertiesConfiguration.class
            ).configure(new Parameters().fileBased());
        final FileHandler handler = new FileHandler(builder.getConfiguration());
        handler.load(
            Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream(DtSimulationTest.PROPERTIES_FILE)
        );
        return builder.getConfiguration();
    }

}
