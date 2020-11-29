package com.rigiresearch.dt.experimentation.templates

import com.rigiresearch.dt.experimentation.ResourceCopy;
import com.rigiresearch.middleware.metamodels.monitoring.ApiKeyAuth
import com.rigiresearch.middleware.metamodels.monitoring.AuthRequirement
import com.rigiresearch.middleware.metamodels.monitoring.BasicAuth
import com.rigiresearch.middleware.metamodels.monitoring.Monitor
import com.rigiresearch.middleware.metamodels.monitoring.MonitoringFactory
import com.rigiresearch.middleware.metamodels.monitoring.MonitoringPackage
import com.rigiresearch.middleware.metamodels.monitoring.Oauth2Auth
import com.rigiresearch.middleware.metamodels.monitoring.PropertyLocation
import com.rigiresearch.middleware.metamodels.monitoring.Root
import com.rigiresearch.middleware.metamodels.monitoring.Type
import java.io.File
import java.io.IOException
import java.util.Date
import org.apache.commons.configuration2.PropertiesConfiguration
import org.apache.commons.configuration2.io.FileHandler
import org.eclipse.emf.ecore.EcorePackage

/**
 * Generate classes from the monitoring model. More specifically, from the
 * response schemas.
 *
 * TODO This class could be replaced by an ATL transformation from monitoring
 * to class diagram. Then, the class diagram would be used to generate the
 * classes.
 *
 * @author Miguel Jimenez (miguel@uvic.ca)
 * @date 2019-06-25
 * @version $Id$
 * @since 0.1.0
 */
final class MonitoringTemplate {

    /**
     * Default constructor.
     */
    new() {
    	// Nothing to do
    }

    /**
     * Generates the Java project for monitoring a specific cloud provider.
     * The project contains a properties file and the necessary Gradle files.
     * The target directory (tree) will be created if necessary.
     * @param root The root model element
     * @param target The target directory where the files are created
     */
    def void generateFiles(Root root, File target) throws IOException {
        // Create the directories if necessary
        target.mkdirs
        new File(target, "src/main/resources").mkdirs

        // Copy template files
        val copy = new ResourceCopy();
        copy.copyResourceDirectory(
            copy.jar(MonitoringTemplate).get(),
            "templates/gradle",
            target
        );
        copy.copyResourcesToDir(
            new File(target, "src/main/resources"),
            false,
            "templates/log4j2.xml",
            "templates/logback.xml"
        );

        // Generate new files
        new FileHandler(new PropertiesConfiguration().populateDefaultProperties(root))
            .save(new File(target, "src/main/resources/default.properties"))
        new FileHandler(new PropertiesConfiguration().populateCustomProperties(root))
            .save(new File(target, "src/main/resources/custom.properties"))
    }

	/**
     * Creates a properties file based on the paths and their parameters.
     * @param config A configuration object
     * @param root The root model element
     * @return The corresponding configuration object
     */
    def populateDefaultProperties(PropertiesConfiguration config, Root root) {
        config.layout.globalSeparator = "="
        config.layout.headerComment = '''
        # File generated by Historian («new Date()»)

        ###############################################################################
        # DO NOT MODIFY THIS FILE
        ###############################################################################'''

        config.setProperty("periodicity", "* * * * *")
        config.layout.setBlancLinesBefore("periodicity", 1)
        config.layout.setComment("periodicity", "Cron expression requesting data every minute")

        config.setProperty("base", root.baseUrl)
        config.layout.setBlancLinesBefore("base", 1)
        config.layout.setComment("base", "The base URL")

        config.setProperty("auth", root.authRequirements.map[r|r.method.id].join(", ").toString)
        config.layout.setBlancLinesBefore("auth", 1)
        // TODO It may be necessary to support authentication methods specifically for monitors individually
        config.layout.setComment("auth", "Authentication methods (globally available)")
        for (req : root.authRequirements) {
            config.setProperty('''auth.«req.method.id».input''', req.toParameter.name)
            if (req.method instanceof ApiKeyAuth) {
                config.setProperty('''auth.«req.method.id».periodicity''', "${periodicity}")
                config.setProperty('''auth.«req.method.id».type''', "key")
            } else if (req.method instanceof BasicAuth) {
                config.setProperty('''auth.«req.method.id».type''', "basic")
            } else {
                config.setProperty('''auth.«req.method.id».type''', "oauth")
            }
        }

        for (m : root.monitors) {
            val parameters = newArrayList
            parameters += m.authRequirements.map[r|r.toParameter]
            if (!m.path.parameters.empty) {
                parameters += m.path.parameters
                config.setProperty('''«m.path.id».inputs''', parameters.map[p|p.name].join(", ").toString)
                config.layout.setBlancLinesBefore('''«m.path.id».inputs''', 1)
                for (p : parameters) {
                    if (p.required) {
                        config.setProperty('''«m.path.id».inputs.«p.name».required''', true)
                    }
                    config.setProperty('''«m.path.id».inputs.«p.name».location''', p.location.toString.toUpperCase)
                }
            } else {
                config.layout.setBlancLinesBefore('''«m.path.id».url''', 1)
            }
            config.setProperty(
                '''«m.path.id».url''',
                '''${base}«IF !m.path.url.startsWith("/")»/«ENDIF»«m.path.url»'''.toString
            )
            // TODO This is rather a simplistic way to render the auth requirements.
            //  There could be more complex combinations.
            config.setProperty('''«m.path.id».auth''', m.authRequirements.map[r|r.method.id].join(", "))
        }
        return config
    }

    /**
     * Creates a parameter based on the given authentication requirement.
     * @param requirement The authentication requirement
     * @return The parameter
     */
    def toParameter(AuthRequirement requirement) {
        val method = requirement.method
        switch (method) {
            ApiKeyAuth: {
                method.property
            }
            BasicAuth: {
                EcorePackage.eINSTANCE.eClass
                MonitoringPackage.eINSTANCE.eClass
                val property = MonitoringFactory.eINSTANCE.
                    createLocatedProperty
                val type = MonitoringFactory.eINSTANCE.createDataType
                type.type = Type.STRING
                property.name = "Authorization"
                property.location = PropertyLocation.HEADER
                property.required = true
                property.type = type
                property
            }
            Oauth2Auth: {
                // TODO Add support for oauth2 authentication
                throw new UnsupportedOperationException("Not implemented yet")
            }
        }
    }

    /**
     * Returns the authentication requirements for the given monitor.
     * @param monitor The monitor
     * @return A list of requirements
     */
    def authRequirements(Monitor monitor) {
        val root = monitor.eContainer as Root
        val requirements = if(!monitor.path.authRequirements.empty)
                monitor.path.authRequirements
            else
                root.authRequirements
        return requirements
    }

    /**
     * Creates a properties file for parameter values and configuration.
     * @param config A configuration object
     * @param root The root model element
     * @return The corresponding configuration object
     */
    def populateCustomProperties(PropertiesConfiguration config, Root root) {
        config.layout.globalSeparator = "="
        config.layout.headerComment = '''# File generated by Historian («new Date()»)'''
        val requirements = root.authRequirements
        for (req : requirements) {
            config.setProperty('''auth.«req.method.id».username''', "${env:API_AUTH_USERNAME}")
            config.setProperty('''auth.«req.method.id».password''', "${env:API_AUTH_PASSWORD}")
            config.layout.setBlancLinesBefore('''auth.«req.method.id».username''', 1)
            if (req.method instanceof ApiKeyAuth) {
                config.setProperty('''auth.«req.method.id».periodicity''', "* * * * *")
                config.setProperty('''auth.«req.method.id».url''', "")
                config.setProperty('''auth.«req.method.id».selector''', "")
            }
        }
        return config
    }

    /**
     * Puts together the base URL.
     * @param root The root model element
     * @return The base URL
     */
    def private baseUrl(Root root) {
        var base = '''http«IF root.https»s«ENDIF»://«root.host»«root.basePath»'''
        if (base.endsWith("/")) {
            base = base.substring(0, base.length)
        }
        return base
    }

}
