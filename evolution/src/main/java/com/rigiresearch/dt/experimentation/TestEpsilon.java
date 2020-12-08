package com.rigiresearch.dt.experimentation;

import com.rigiresearch.middleware.metamodels.EmfResource;
import java.io.File;
import java.io.IOException;
import java.util.Optional;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.epsilon.flexmi.FlexmiResourceFactory;
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

    public static void main(final String... args) throws IOException {
        final Resource metamodel = new EmfResource(
            new File("metamodels/scenario/model-gen/Scenario.ecore"),
            EcorePackage.eINSTANCE.eResource(),
            new XMIResourceFactoryImpl()
        ).asResource();
        final Resource model = new EmfResource(
            new File("evolution/model/demo.scenario.flexmi"),
            metamodel,
            new FlexmiResourceFactory()
        ).asResource();
        TestEpsilon.LOGGER.info("{}", model.getContents());
    }

}
