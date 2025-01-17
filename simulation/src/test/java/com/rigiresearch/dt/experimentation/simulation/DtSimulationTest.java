package com.rigiresearch.dt.experimentation.simulation;

import com.rigiresearch.dt.experimentation.simulation.graph.Line;
import com.rigiresearch.dt.experimentation.simulation.graph.Station;
import com.rigiresearch.dt.experimentation.simulation.metrics.BusQueueLength;
import com.rigiresearch.dt.experimentation.simulation.metrics.ExcessWaitingTime;
import com.rigiresearch.dt.experimentation.simulation.metrics.HeadwayCoefficientOfVariation;
import com.rigiresearch.dt.experimentation.simulation.metrics.ObservedLineHeadway;
import com.rigiresearch.dt.experimentation.simulation.metrics.ObservedWaitingTime;
import com.rigiresearch.dt.experimentation.simulation.metrics.PassengerQueueLength;
import com.rigiresearch.middleware.graph.Graph;
import com.rigiresearch.middleware.graph.GraphParser;
import com.rigiresearch.middleware.graph.Node;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.xml.bind.JAXBException;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.io.FileHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tests {@link DtSimulation}.
 * @author Miguel Jimenez (miguel@uvic.ca)
 * @version $Id$
 * @since 0.1.0
 */
class DtSimulationTest {

    /**
     * The logger.
     */
    private static final Logger LOGGER =
        LoggerFactory.getLogger(DtSimulationTest.class);

    /**
     * The name of the bindings resource.
     */
    private static final String BINDINGS = "bindings.xml";

    /**
     * The name of the demo graph resource.
     */
    private static final String GRAPH = "stations-graph.xml";

    /**
     * The simulation configuration file.
     */
    private static final String PROPERTIES_FILE = "simulation.properties";

    @Test
    void testItRuns()
        throws JAXBException, IOException, ConfigurationException {
        final Graph<Node> graph = new GraphParser()
            .withBindings(DtSimulationTest.BINDINGS)
            .instance(
                Objects.requireNonNull(
                    Thread.currentThread()
                        .getContextClassLoader()
                        .getResourceAsStream(DtSimulationTest.GRAPH)
                )
            );
        Assertions.assertEquals(
            2L,
            graph.getNodes()
                .stream()
                .filter(Line.class::isInstance)
                .map(Line.class::cast)
                .count(),
            "Expected 2 lines"
        );
        Assertions.assertEquals(
            3L,
            graph.getNodes()
                .stream()
                .filter(Station.class::isInstance)
                .map(Station.class::cast)
                .count(),
            "Expected 3 stations"
        );
        Assertions.assertEquals(
            4L,
            graph.getNodes()
                .stream()
                .filter(Station.class::isInstance)
                .map(Station.class::cast)
                .map(Station::getMetadata)
                .mapToLong(Collection::size)
                .sum(),
            "Expected 4 segments"
        );
        final Configuration config = DtSimulationTest.config();
        final DtSimulation simulation = new DtSimulation(graph, config);
        simulation.setLengthOfReplication(100.0);
        simulation.setLengthOfWarmUp(100.0);
        // simulation.setNumberOfReplications(5);
        simulation.run();

        final Map<Line, Double> headways = graph.getNodes()
            .stream()
            .filter(Line.class::isInstance)
            .map(Line.class::cast)
            .collect(
                Collectors.toMap(
                    Function.identity(),
                    line -> config.getDouble(
                        String.format("%s.headway", line.getName())
                    )
                )
            );
        DtSimulationTest.LOGGER.info("{}", new ExcessWaitingTime(simulation, headways));
        DtSimulationTest.LOGGER.info("{}", new HeadwayCoefficientOfVariation(simulation));
        DtSimulationTest.LOGGER.info("{}", new ObservedLineHeadway(simulation));
        DtSimulationTest.LOGGER.info("{}", new ObservedWaitingTime(simulation));
        DtSimulationTest.LOGGER.info("{}", new PassengerQueueLength(simulation));
        DtSimulationTest.LOGGER.info("{}", new BusQueueLength(simulation));
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
