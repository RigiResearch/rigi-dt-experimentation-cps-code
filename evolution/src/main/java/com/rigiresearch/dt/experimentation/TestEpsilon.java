package com.rigiresearch.dt.experimentation;

import com.rigiresearch.middleware.metamodels.EcorePrinter;
import java.util.Collections;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.epsilon.emc.emf.EmfModel;
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

    private TestEpsilon() {
        // Nothing to do here
    }

    public static void main(final String... args) throws Exception {
        final String scenario = "metamodels/scenario/model-gen/Scenario.ecore";
        final String simulation = "metamodels/simulation/model-gen/Simulation.ecore";
        final String model = "evolution/model/demo.scenario.flexmi";
        final String eol = "evolution/model/demo.query.eol";
        final String etl = "evolution/model/demo.transformation.etl";

        final Epsilon epsilon = new Epsilon(scenario);

        // 1. Load a Flexmi model and print it out
        final Resource resource = epsilon.flexmi(model);
        final EObject eobject1 = resource.getContents().get(0);
        TestEpsilon.LOGGER.info("{}", new EcorePrinter(eobject1).asPrettyString());
        // The Flexmi model as an Epsilon input model
        final EmfModel input = epsilon.input("Source", model, resource);

        // 2. Load and run an EOL program
        epsilon.eol(eol, Collections.singletonList(input));

        // 3. Load and run an ETL program
        final EmfModel output = epsilon.output("Target", simulation, "/tmp/output.simulation");
        EtlRunConfiguration.Builder()
            .withScript(etl)
            .withModel(input)
            .withModel(output)
            .build()
            .run();
        output.getResource().save(Collections.emptyMap());
        final EObject eobject2 = output.getResource().getContents().get(0);
        TestEpsilon.LOGGER.info("{}", new EcorePrinter(eobject2).asPrettyString());
    }

}
