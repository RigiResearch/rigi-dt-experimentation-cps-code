package com.rigiresearch.dt.experimentation.simulation.graph;

import com.rigiresearch.middleware.graph.Graph;
import com.rigiresearch.middleware.graph.GraphParser;
import com.rigiresearch.middleware.graph.Node;
import com.rigiresearch.middleware.graph.Parameter;
import com.rigiresearch.middleware.graph.Property;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javax.xml.bind.JAXBException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests the graph bindings and new graph classes.
 * @author Miguel Jimenez (miguel@uvic.ca)
 * @version $Id$
 * @since 0.1.0
 */
class GraphBindingsTest {

    /**
     * The name of the bindings resource.
     */
    private static final String BINDINGS = "bindings.xml";

    /**
     * The name of the demo graph resource.
     */
    private static final String RESOURCE = "demo-graph.xml";

    @Test
    void testGeneratingADemoGraph() throws JAXBException {
        final Graph<Node> graph = GraphBindingsTest.graph();
        final OutputStream output = new ByteArrayOutputStream();
        new GraphParser()
            .withBindings(GraphBindingsTest.BINDINGS)
            .write(graph, output);
        Assertions.assertEquals(
            GraphBindingsTest.xml(),
            output.toString(),
            "The output XML should be the same as the contents of the file"
        );
    }

    @Test
    void testLoadingADemoGraph() throws JAXBException {
        Assertions.assertEquals(
            GraphBindingsTest.graph(),
            new GraphParser()
                .withBindings(GraphBindingsTest.BINDINGS)
                .instance(GraphBindingsTest.xml()),
            "The loaded graph should be equal to the graph instance"
        );
    }

    /**
     * Loads the demo graph from the resources.
     * @return An XML-formatted graph
     */
    private static String xml() {
        return new BufferedReader(
            new InputStreamReader(
                Objects.requireNonNull(
                    Thread.currentThread()
                        .getContextClassLoader()
                        .getResourceAsStream(GraphBindingsTest.RESOURCE)
                ),
                StandardCharsets.UTF_8)
        )
            .lines()
            .collect(Collectors.joining("\n"));
    }

    /**
     * Creates a demo graph.
     * @return A non-null {@link Graph} instance
     */
    private static Graph<Node> graph() {
        final Line T31n = new Line("T31n");
        final Line T31s = new Line("T31s");
        final Set<Node> nodes = new HashSet<>(3);
        final Station salomia = GraphBindingsTest.station("Salomia");
        final Station flora = GraphBindingsTest.station("Flora Industrial");
        T31n.setFrom(salomia);
        T31n.setTo(flora);
        T31s.setFrom(flora);
        T31s.setTo(salomia);
        salomia.getMetadata()
            .add(
                new Segment(
                    salomia.getParameter(false, "SP1", Stop.class),
                    flora.getParameter(false, "FP1", Stop.class),
                    flora,
                    T31n
                )
            );
        flora.getMetadata()
            .add(
                new Segment(
                    flora.getParameter(false, "FP1", Stop.class),
                    salomia.getParameter(false, "SP1", Stop.class),
                    salomia,
                    T31s
                )
            );
        nodes.add(salomia);
        nodes.add(flora);
        nodes.add(T31n);
        nodes.add(T31s);
        return new Graph<>(nodes);
    }

    /**
     * Creates a station.
     * @param name The station name
     * @return A non-null {@link Station} instance
     */
    private static Station station(final String name) {
        final Set<Parameter> stops = new HashSet<>(2);
        stops.add(new Stop(String.format("%sP1", name.charAt(0))));
        stops.add(new Stop(String.format("%sP2", name.charAt(0))));
        final Set<Property> segments = new HashSet<>(2);
        return new Station(name, stops, segments);
    }

}
