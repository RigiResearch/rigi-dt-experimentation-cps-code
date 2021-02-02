package com.rigiresearch.dt.experimentation.evolution.genetic;

import com.rigiresearch.dt.experimentation.simulation.graph.Line;
import com.rigiresearch.dt.experimentation.simulation.graph.Station;
import com.rigiresearch.middleware.graph.Graph;
import com.rigiresearch.middleware.graph.GraphParser;
import com.rigiresearch.middleware.graph.Node;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.convert.DefaultListDelimiterHandler;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.io.FileHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.Collection;
import java.util.Objects;

/**
 * Tests {@link GeneticAlgorithm}.
 *
 * @author Felipe Rivera (rivera@uvic.ca)
 * @version $Id$
 * @since 0.1.0
 */
public class GeneticAlgorithmTest {

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

    /**
     * The number of generations for the genetic algorithm.
     */
    private final int NUM_GENERATIONS = 10;

    /**
     * The minimum value for a gene in a chromosome.
     */
    private final double CHROMOSOME_MIN = 5.0;

    /**
     * The maximum value for a gene in a chromosome.
     */
    private final double CHROMOSOME_MAX = 10.0;

    /**
     * The number of properties that will vary during the execution of the genetic algorithm (e.g., headway design and number of buses).
     */
    private final int CHROMOSOME_LENGTH = 2;

    /**
     * The size of the population.
     */
    private final int POPULATION_SIZE = 20;

    /**
     * The number of consecutive evolutions that produce similar results before stopping the algorithm.
     */
    private final int STEADY_NUMBER = 7;

    /**
     * The mutation probability.
     */
    private final double MUTATION_PROB = 0.10;

    /**
     * The crossover probability.
     */
    private final double CROSSOVER_PROB = 0.80;

    @Test
    void testGetLinesFromConfig() throws ConfigurationException {

        final GeneticAlgorithm ga = new GeneticAlgorithm(config(), null, -1);
        Assertions.assertEquals(2L, ga.getLineIds().size());
    }

    @Test
    void testItRuns() throws ConfigurationException, JAXBException, IOException {

        final Graph<Node> graph = loadGraph();

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

        final Configuration config = config();

        GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm(config, graph, NUM_GENERATIONS);

        EvolutionResults results = geneticAlgorithm.evolve(CHROMOSOME_MIN,CHROMOSOME_MAX,CHROMOSOME_LENGTH,POPULATION_SIZE,STEADY_NUMBER,NUM_GENERATIONS,MUTATION_PROB,CROSSOVER_PROB);

    }

    /**
     * Loads the configuration file based on a resource.
     *
     * @return A configuration instance
     * @throws ConfigurationException If there is a problem loading the resource
     */
    private Configuration config() throws ConfigurationException {
        final FileBasedConfigurationBuilder<FileBasedConfiguration> builder =
                new FileBasedConfigurationBuilder<FileBasedConfiguration>(
                        PropertiesConfiguration.class
                ).configure(new Parameters().fileBased().setListDelimiterHandler(new DefaultListDelimiterHandler(',')));
        final FileHandler handler = new FileHandler(builder.getConfiguration());
        handler.load(
                Thread.currentThread()
                        .getContextClassLoader()
                        .getResourceAsStream(GeneticAlgorithmTest.PROPERTIES_FILE)
        );
        return builder.getConfiguration();
    }

    /**
     * Allows to load a graph containing the stations of the transit system.
     * @return a graph containing the stations of the transit system.
     * @throws JAXBException Parsing exception.
     * @throws IOException Load exception.
     */
    private Graph<Node> loadGraph() throws JAXBException, IOException {
        Graph<Node> graph = new GraphParser()
                .withBindings(GeneticAlgorithmTest.BINDINGS)
                .instance(
                        Objects.requireNonNull(
                                Thread.currentThread()
                                        .getContextClassLoader()
                                        .getResourceAsStream(GeneticAlgorithmTest.GRAPH)
                        )
                );
        return graph;
    }

}
