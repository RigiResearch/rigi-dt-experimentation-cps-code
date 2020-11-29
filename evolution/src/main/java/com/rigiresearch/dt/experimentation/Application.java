package com.rigiresearch.dt.experimentation;

import com.rigiresearch.dt.experimentation.templates.MonitoringTemplate;
import com.rigiresearch.middleware.graph.Graph;
import com.rigiresearch.middleware.graph.GraphParser;
import com.rigiresearch.middleware.graph.Input;
import com.rigiresearch.middleware.graph.Node;
import com.rigiresearch.middleware.graph.Parameter;
import com.rigiresearch.middleware.metamodels.AtlTransformation;
import com.rigiresearch.middleware.metamodels.monitoring.LocatedProperty;
import com.rigiresearch.middleware.metamodels.monitoring.MonitoringPackage;
import com.rigiresearch.middleware.metamodels.monitoring.Root;
import edu.uoc.som.openapi.OpenAPIPackage;
import edu.uoc.som.openapi.io.OpenAPIImporter;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import javax.xml.bind.JAXBException;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The main class.
 * @author Miguel Jimenez (miguel@uvic.ca)
 * @version $Id$
 * @since 0.1.0
 */
@Accessors(fluent = true)
@RequiredArgsConstructor
@SuppressWarnings("checkstyle:ClassDataAbstractionCoupling")
public final class Application {

    /**
     * The logger.
     */
    private static final Logger LOGGER =
        LoggerFactory.getLogger(Application.class);

    /**
     * The bindings resource name.
     */
    private static final String BINDINGS = "bindings.xml";

    /**
     * The main entry point.
     * @param args The application arguments
     */
    public static void main(final String... args) {
        Application.LOGGER.info("Hello world");
    }

    /**
     * Transforms the OpenAPI specification into a monitoring model.
     * @param specification An OpenAPI specification
     * @return The model instance
     * @throws UnsupportedEncodingException If the encoding is not supported
     * @throws IOException If there is an error with the jar file or saving the
     *  resource
     */
    @SuppressWarnings("PMD.UnusedPrivateMethod")
    private Root model(final File specification)
        throws UnsupportedEncodingException, IOException {
        final edu.uoc.som.openapi.Root root = new OpenAPIImporter()
            .createOpenAPIModelFromJson(specification);
        final String output = "OUT";
        final String module = "atl/OpenAPI2Monitoring.atl";
        final File directory = new ResourceCopy()
            .copyResourcesToTempDir(true, module, "atl/OpenAPI2Monitoring.emftvm");
        return (Root) new AtlTransformation.Builder()
            .withMetamodel(MonitoringPackage.eINSTANCE)
            .withMetamodel(OpenAPIPackage.eINSTANCE)
            .withModel(AtlTransformation.ModelType.INPUT, "IN", root)
            .withModel(AtlTransformation.ModelType.OUTPUT, output, "monitoring.xmi")
            .withTransformation(String.format("%s/%s", directory.getAbsolutePath(), module))
            .build()
            .run()
            .get(output)
            .getResource()
            .getContents()
            .get(0);
    }

    /**
     * Generates a gradle project containing the monitoring code for a
     * particular API specification.
     * @param model The monitoring model
     * @param directory The output directory
     * @throws JAXBException If there is an exception during the graph
     *  marshalling
     * @throws IOException If there is an error with the jar file or saving the
     *  resource
     */
    @SuppressWarnings("PMD.UnusedPrivateMethod")
    private void generateProject(final Root model, final File directory)
        throws JAXBException, IOException {
        final File parent = new File(directory, "src/main/resources/");
        if (!parent.mkdirs() && !parent.exists()) {
            throw new IOException(
                String.format(
                    "Could not create directory %s",
                    parent.getAbsolutePath()
                )
            );
        }
        final GraphParser parser = new GraphParser()
            .withBindings(Application.BINDINGS);
        final Graph<Node> graph = this.monitoringGraph(model);
        parser.write(graph, new File(parent, "configuration.xml"));
        new MonitoringTemplate().generateFiles(model, directory);
    }

    /**
     * Transforms a monitoring model into a graph.
     * @param model The monitoring model
     * @return The graph instance
     */
    private Graph<Node> monitoringGraph(final Root model) {
        return new Graph<Node>(
            model.getMonitors()
                .stream()
                .map(monitor -> {
                    final Set<Parameter> parameters = monitor.getPath()
                        .getParameters()
                        .stream()
                        .filter(LocatedProperty::isRequired)
                        .map(property -> new Input(property.getName(), ""))
                        .collect(Collectors.toSet());
                    return new Node(
                        monitor.getPath().getId(),
                        parameters,
                        Collections.emptySet()
                    );
                })
                .collect(Collectors.toSet())
        );
    }

}
