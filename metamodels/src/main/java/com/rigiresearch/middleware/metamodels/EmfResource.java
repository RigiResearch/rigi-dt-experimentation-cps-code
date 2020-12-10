package com.rigiresearch.middleware.metamodels;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Loads an EMF resource.
 * @author Miguel Jimenez (miguel@uvic.ca)
 * @version $Id$
 * @since 0.1.0
 */
@RequiredArgsConstructor
public final class EmfResource {

    /**
     * The logger.
     */
    private static final Logger LOGGER =
        LoggerFactory.getLogger(EmfResource.class);

    /**
     * The resource's model.
     */
    private final File model;

    /**
     * The resource's metamodel
     */
    private final List<Resource> metamodels;

    /**
     * A factory to process the model.
     */
    private final Map<String, Resource.Factory> factories;

    /**
     * Loads the Flexmi model and returns the corresponding Ecore resource.
     * @return An Ecore resource
     * @throws IOException If there is a problem loading the model file
     */
    public Resource asResource() throws IOException {
        final ResourceSet set = new ResourceSetImpl();
        this.metamodels.stream()
            .map(resource -> (EPackage) resource.getContents().get(0))
            .forEach(epackage ->
                set.getPackageRegistry()
                    .put(epackage.getNsURI(), epackage)
            );
        set.getResourceFactoryRegistry()
            .getExtensionToFactoryMap()
            .putAll(this.factories);
        final Resource resource = set.createResource(
            URI.createFileURI(this.model.getAbsolutePath())
        );
        resource.load(Collections.emptyMap());
        EmfResource.reportErrors(resource);
        return resource;
    }

    /**
     * Reports errors, if any.
     * @param resource The loaded resource
     */
    private static void reportErrors(final Resource resource) {
        if (!resource.getErrors().isEmpty()) {
            resource.getErrors().forEach(
                error -> EmfResource.LOGGER.error("{}", error)
            );
        }
    }

}
