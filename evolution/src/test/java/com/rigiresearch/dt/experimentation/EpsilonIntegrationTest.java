package com.rigiresearch.dt.experimentation;

import com.rigiresearch.middleware.metamodels.EcorePrinter;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.epsilon.egl.launch.EgxRunConfiguration;
import org.eclipse.epsilon.emc.emf.EmfModel;
import org.eclipse.epsilon.eol.launch.EolRunConfiguration;
import org.eclipse.epsilon.eol.models.IModel;
import org.eclipse.epsilon.etl.launch.EtlRunConfiguration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tests the integration between Xcore and Epsilon.
 * @author Miguel Jimenez (miguel@uvic.ca)
 * @version $Id$
 * @since 0.1.0
 */
class EpsilonIntegrationTest {

    /**
     * The logger.
     */
    private static final Logger LOGGER =
        LoggerFactory.getLogger(EpsilonIntegrationTest.class);

    /**
     * The scenario metamodel.
     */
    private static final String SCENARIO = "Scenario.ecore";

    /**
     * The simulation metamodel
     */
    private static final String SIMULATION = "Simulation.ecore";

    /**
     * The Flexmi model.
     */
    private static final String MODEL = "demo.scenario.flexmi";

    /**
     * The EOL program.
     */
    private static final String EOL = "demo.query.eol";

    /**
     * The ETL program.
     */
    private static final String ETL = "demo.transformation.etl";

    /**
     * The EGL program.
     */
    private static final String EGL = "demo.generation.egl";

    /**
     * The EGX program.
     */
    private static final String EGX = "demo.coordination.egx";

    @Test
    void testAll() throws Exception {
        // Copy the resources to a temporal directory
        final File tmp = new ResourceCopy().copyResourcesToTempDir(
            true,
            EpsilonIntegrationTest.SCENARIO,
            EpsilonIntegrationTest.SIMULATION,
            EpsilonIntegrationTest.MODEL,
            EpsilonIntegrationTest.EOL,
            EpsilonIntegrationTest.ETL,
            EpsilonIntegrationTest.EGL,
            EpsilonIntegrationTest.EGX
        );
        EpsilonIntegrationTest.LOGGER.info("Using temporal directory {}", tmp);
        // Go from a scenario to a simulation, and then generate the simulation textual model
        final EmfModel input = EpsilonIntegrationTest.testLoadingFlexmiModels(tmp);
        EpsilonIntegrationTest.testLoadingAndRunningEol(tmp, input);
        final EmfModel output = EpsilonIntegrationTest.testLoadingAndRunningEtl(tmp, input);
        EpsilonIntegrationTest.testLoadingEgxModels(tmp, output);
    }

    /**
     * Test loading a Flexmi model
     * @param tmp The temporary directory where the model files are stored
     * @return The loaded model
     * @throws Exception If there is a problem loading the resource
     */
    static EmfModel testLoadingFlexmiModels(final File tmp) throws Exception {
        final Epsilon epsilon = new Epsilon();
        final Resource resource = epsilon.flexmi(
            String.format("%s/%s", tmp, EpsilonIntegrationTest.SCENARIO),
            String.format("%s/%s", tmp, EpsilonIntegrationTest.MODEL)
        );
        final EmfModel input = epsilon.input(
            "Source",
            String.format("%s/%s", tmp, EpsilonIntegrationTest.SCENARIO),
            String.format("%s/%s", tmp, EpsilonIntegrationTest.MODEL),
            resource
        );
        Assertions.assertNotNull(resource, "Flexmi resource is null");
        Assertions.assertFalse(resource.getContents().isEmpty(), "Flexmi resource is empty");
        final EObject eobject1 = resource.getContents().get(0);
        final String readable = new EcorePrinter(eobject1).asPrettyString();
        EpsilonIntegrationTest.LOGGER.info("Epsilon Flexmi");
        EpsilonIntegrationTest.LOGGER.info("{}", readable);
        return input;
    }

    /**
     * Test loading and running an EOL program.
     * @param tmp The temporary directory where the model files are stored
     * @param input The input model
     */
    static void testLoadingAndRunningEol(final File tmp, final IModel input) {
        final Epsilon epsilon = new Epsilon();
        EpsilonIntegrationTest.LOGGER.info("Epsilon Object Language");
        EolRunConfiguration.Builder()
            .withScript(String.format("%s/%s", tmp, EpsilonIntegrationTest.EOL))
            .withModel(input)
            .build()
            .run();
    }

    /**
     * Test transforming a model to another model.
     * @param tmp The temporary directory where the model files are stored
     * @param input The input model
     * @return The output model
     * @throws IOException In case there is a problem writing out the model
     */
    static EmfModel testLoadingAndRunningEtl(final File tmp, final IModel input) throws IOException {
        final Epsilon epsilon = new Epsilon();
        final EmfModel output = epsilon.output(
            "Target",
            String.format("%s/%s", tmp, EpsilonIntegrationTest.SIMULATION),
            // Can't use /tmp because of a bug in the library that always tries
            // to create the directory
            "../tmp/simulation.xmi"
        );
        EtlRunConfiguration.Builder()
            .withScript(String.format("%s/%s", tmp, EpsilonIntegrationTest.ETL))
            .withModel(input)
            .withModel(output)
            .build()
            .run();
        output.getResource().save(Collections.emptyMap());
        Assertions.assertFalse(
            output.getResource().getContents().isEmpty(),
            "Output resource is empty"
        );
        final EObject eobject2 = output.getResource().getContents().get(0);
        final String readable = new EcorePrinter(eobject2).asPrettyString();
        EpsilonIntegrationTest.LOGGER.info("Epsilon Transformation Language");
        EpsilonIntegrationTest.LOGGER.info("{}", readable);
        return output;
    }

    /**
     * Test transforming a model to text.
     * @param tmp The temporary directory where the model files are stored
     * @param input The model to transform to text
     */
    static void testLoadingEgxModels(final File tmp, final IModel input) {
        final Epsilon epsilon = new Epsilon();
        EpsilonIntegrationTest.LOGGER.info("Epsilon Co-Ordination Language");
        EgxRunConfiguration.Builder()
            .withScript(String.format("%s/%s", tmp, EpsilonIntegrationTest.EGX))
            .withModel(input)
            .withParallelism(-1)
            .build()
            .run();
    }

}
