package com.rigiresearch.dt.experimentation;

import com.rigiresearch.middleware.metamodels.EcorePrinter;
import java.util.Collections;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.epsilon.egl.launch.EgxRunConfiguration;
import org.eclipse.epsilon.emc.emf.EmfModel;
import org.eclipse.epsilon.eol.launch.EolRunConfiguration;
import org.eclipse.epsilon.etl.launch.EtlRunConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Test Xcore and Epsilon integration.
 * @author Miguel Jimenez (miguel@uvic.ca)
 * @version $Id$
 * @since 0.1.0
 */
public class TestEpsilon {

    /**
     * The logger.
     */
    private static final Logger LOGGER =
        LoggerFactory.getLogger(TestEpsilon.class);

    TestEpsilon() {
        // Nothing to do here
    }

    public void run() throws Exception {
        final String scenario = "../metamodels/scenario/model-gen/Scenario.ecore";
        final String simulation = "../metamodels/simulation/model-gen/Simulation.ecore";
        final String model = "model/demo.scenario.flexmi";
        final Epsilon epsilon = new Epsilon();

        // 1. Load a Flexmi model and print it out
        final Resource resource = epsilon.flexmi(scenario, model);
        final EmfModel input = epsilon.input("Source", scenario, model, resource);
        final EObject eobject1 = resource.getContents().get(0);
        TestEpsilon.LOGGER.info("Epsilon Flexmi");
        TestEpsilon.LOGGER.info("{}", new EcorePrinter(eobject1).asPrettyString());

        // 2. Load and run an EOL program
        TestEpsilon.LOGGER.info("Epsilon Object Language");
        EolRunConfiguration.Builder()
            .withScript("model/demo.query.eol")
            .withModel(input)
            .build()
            .run();

        // 3. Load and run an ETL program
        final EmfModel output = epsilon.output("Target", simulation, "../tmp/simulation.xmi");
        EtlRunConfiguration.Builder()
            .withScript("model/demo.transformation.etl")
            .withModel(input)
            .withModel(output)
            .build()
            .run();
        output.getResource().save(Collections.emptyMap());
        final EObject eobject2 = output.getResource().getContents().get(0);
        TestEpsilon.LOGGER.info("Epsilon Transformation Language");
        TestEpsilon.LOGGER.info("{}", new EcorePrinter(eobject2).asPrettyString());

        // 4. Load and run an EGX program
        TestEpsilon.LOGGER.info("Epsilon Co-Ordination Language");
        EgxRunConfiguration.Builder()
            .withScript("model/demo.coordination.egx")
            .withModel(output)
            .withParallelism(-1)
            .build()
            .run();
    }

}
