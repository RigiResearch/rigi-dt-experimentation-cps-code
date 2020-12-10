package com.rigiresearch.dt.experimentation;

import com.rigiresearch.middleware.metamodels.EmfResource;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.Resource.Factory;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.epsilon.emc.emf.EmfModel;
import org.eclipse.epsilon.eol.EolModule;
import org.eclipse.epsilon.eol.IEolModule;
import org.eclipse.epsilon.eol.exceptions.models.EolModelLoadingException;
import org.eclipse.epsilon.eol.models.CachedModel;
import org.eclipse.epsilon.flexmi.FlexmiResourceFactory;

/**
 * Load and run Epsilon models.
 * @author Miguel Jimenez (miguel@uvic.ca)
 * @version $Id$
 * @since 0.1.0
 */
@RequiredArgsConstructor
public final class Epsilon {

    /**
     * A Flexmi factory.
     */
    private static final Resource.Factory ECORE_FACTORY = new EcoreResourceFactoryImpl();

    /**
     * A Flexmi factory.
     */
    private static final Resource.Factory FLEXMI_FACTORY = new FlexmiResourceFactory();

    /**
     * A Flexmi factory.
     */
    private static final Resource.Factory XMI_FACTORY = new XMIResourceFactoryImpl();

    /**
     * Ecore's file extension.
     */
    private static final String ECORE_EXT = "ecore";

    /**
     * Flexmi's file extension.
     */
    private static final String FLEXMI_EXT = "flexmi";

    /**
     * XMI's file extension.
     */
    private static final String XMI_EXT = "xmi";

    /**
     * The path to the Ecore metamodel on which the input models are based.
     */
    private final String metamodelPath;

    /**
     * Loads a Flexmi model.
     * @param modelPath The path to the Flexmi model
     * @return The loaded resource
     * @throws IOException If there is a problem loading the model
     */
    public Resource flexmi(final String modelPath) throws IOException {
        final Map<String, Factory> factories = new HashMap<>(3);
        factories.put(Epsilon.ECORE_EXT, Epsilon.ECORE_FACTORY);
        factories.put(Epsilon.XMI_EXT, Epsilon.XMI_FACTORY);
        factories.put(Epsilon.FLEXMI_EXT, Epsilon.FLEXMI_FACTORY);
        final Resource metamodel = new EmfResource(
            new File(this.metamodelPath),
            Collections.singletonList(EcorePackage.eINSTANCE.eResource()),
            factories
        ).asResource();
        return new EmfResource(
            new File(modelPath),
            Collections.singletonList(metamodel),
            factories
        ).asResource();
    }

    /**
     * Runs an EOL program.
     * @param programPath The path to the EOL program
     * @param inputs The input models to the program, if any
     * @throws Exception If there is a problem running the program
     */
    public void eol(final String programPath, final Iterable<EmfModel> inputs)
        throws Exception {
        final IEolModule module = new EolModule();
        module.parse(new File(programPath));
        // Make the input models available to the program
        inputs.forEach(model -> module.getContext()
            .getModelRepository()
            .addModel(model));
        module.execute();
        // Saves any changes to the models and unloads them from memory
        inputs.forEach(CachedModel::dispose);
    }

    /**
     * Instantiates an EMF model to serve as input of an Epsilon program.
     * @param modelPath The path to the input model
     * @param resource The already loaded model resource
     * @return The EMF model instance
     * @throws EolModelLoadingException If there is a problem loading the model
     */
    public EmfModel inputModel(final String modelPath, final Resource resource)
        throws EolModelLoadingException {
        this.register(Epsilon.FLEXMI_EXT, Epsilon.FLEXMI_FACTORY);
        final EmfModel model = new EmfModel();
        model.setMetamodelFile(this.metamodelPath);
        // Set the URI to prevent a NullPointerException
        model.setModelFile(modelPath);
        model.setResource(resource);
        model.load();
        return model;
    }

    /**
     * Registers a resource factory globally.
     * @param extension The file extension associated with the factory
     * @param factory The factory instance
     */
    public void register(final String extension,
        final Resource.Factory factory) {
        if (!Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap()
            .containsKey(extension)) {
            Resource.Factory.Registry.INSTANCE
                .getExtensionToFactoryMap()
                .put(extension, factory);
        }
    }

}
