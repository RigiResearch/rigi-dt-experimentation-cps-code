package com.rigiresearch.dt.experimentation;

import com.rigiresearch.middleware.metamodels.EcorePrinter;
import com.rigiresearch.middleware.metamodels.EmfResource;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.epsilon.emc.emf.EmfModel;
import org.eclipse.epsilon.eol.EolModule;
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

    public static void main(final String... args) throws Exception {
        // 1. Load a Flexmi model and print it out
        final Resource scenario = TestEpsilon.flexmi();

        // 2. Load and run an EOL program
        TestEpsilon.eol(scenario);
    }

    private static Resource flexmi() throws IOException {
        final Map<String, Resource.Factory> factories = new HashMap<>(3);
        factories.put("ecore", new EcoreResourceFactoryImpl());
        factories.put("xmi", new XMIResourceFactoryImpl());
        factories.put("flexmi", new FlexmiResourceFactory());
        final Resource metamodel = new EmfResource(
            new File("metamodels/scenario/model-gen/Scenario.ecore"),
            Collections.singletonList(EcorePackage.eINSTANCE.eResource()),
            factories
        ).asResource();
        final Resource model = new EmfResource(
            new File("evolution/model/demo.scenario.flexmi"),
            Collections.singletonList(metamodel),
            factories
        ).asResource();
        final String out = new EcorePrinter(model.getContents().get(0)).asPrettyString();
        TestEpsilon.LOGGER.info("{}", out);
        return model;
    }

    private static void register(final String extension, final Resource.Factory factory) {
        if (!Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().containsKey(extension)) {
            Resource.Factory.Registry.INSTANCE
                .getExtensionToFactoryMap()
                .put(extension, factory);
        }
    }

    private static void eol(final Resource scenario) throws Exception {
        // Flexmi Must be registered globally
        TestEpsilon.register("flexmi", new FlexmiResourceFactory());

        // Loads the EMF model
        scenario.getResourceSet()
            .getResourceFactoryRegistry()
            .getExtensionToFactoryMap()
            .put("ecore", new EcoreResourceFactoryImpl());
        EmfModel model = new EmfModel();
        model.setMetamodelFile("metamodels/scenario/model-gen/Scenario.ecore");
        // Set the URI to prevent a NullPointerException
        model.setModelFile("evolution/model/demo.scenario.flexmi");
        model.setResource(scenario);
        model.load();

        // Parses and executes the EOL program
        EolModule module = new EolModule();
        module.parse(new File("evolution/model/demo.query.eol"));
        // Makes the model accessible from the program
        module.getContext().getModelRepository().addModel(model);
        module.execute();

        // Saves any changes to the model
        // and unloads it from memory
        model.dispose();
    }

}
