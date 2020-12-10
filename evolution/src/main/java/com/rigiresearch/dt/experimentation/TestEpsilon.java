package com.rigiresearch.dt.experimentation;

import com.rigiresearch.middleware.metamodels.EcorePrinter;
import java.util.Collections;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
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
        final String metamodel = "metamodels/scenario/model-gen/Scenario.ecore";
        final String model = "evolution/model/demo.scenario.flexmi";
        final String program = "evolution/model/demo.query.eol";

        final Epsilon epsilon = new Epsilon(metamodel);
        // 1. Load a Flexmi model and print it out
        final Resource resource = epsilon.flexmi(model);
        final EObject scenario = resource.getContents().get(0);
        TestEpsilon.LOGGER.info("{}", new EcorePrinter(scenario).asPrettyString());
        // 2. Load and run an EOL program
        epsilon.eol(program, Collections.singletonList(epsilon.inputModel(model, resource)));
    }

}
