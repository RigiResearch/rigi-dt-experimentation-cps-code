package com.rigiresearch.dt.experimentation.simulation;

import com.rigiresearch.middleware.graph.GraphParser;
import java.io.File;
import javax.xml.bind.JAXBException;
import jsl.simulation.Simulation;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;

/**
 * The main class.
 * @author Miguel Jimenez (miguel@uvic.ca)
 * @version $Id$
 * @since 0.1.0
 */
public final class Application {

    /**
     * Loads the configuration file.
     * @param file A path to the properties file
     * @return A configuration based on the properties file
     * @throws ConfigurationException If there is a problem loading the file
     */
    private static Configuration config(final String file)
        throws ConfigurationException {
        return new FileBasedConfigurationBuilder<FileBasedConfiguration>(
            PropertiesConfiguration.class
        ).configure(
            new Parameters()
                .fileBased()
                .setFile(new File(file))
        ).getConfiguration();
    }

    /**
     * Main entry point.
     * @param args The application arguments
     * @throws ConfigurationException In case there is a problem loading the
     *  properties file
     * @throws JAXBException In case there is a problem loading the input graph
     */
    public static void main(final String... args)
        throws ConfigurationException, JAXBException {
        if (args.length < 2) {
            throw new IllegalArgumentException(
                "Expected two arguments: a path to the input graph, and a path"
                    + " to the properties file"
            );
        }
        final Simulation simulation = new DtSimulation(
            new GraphParser()
                .withBindings("bindings.xml")
                .instance(new File(args[0])),
            Application.config(args[1])
        );
        simulation.setNumberOfReplications(1);
        // simulation.setLengthOfReplication(200000.0);
        // simulation.setLengthOfWarmUp(50000.0);
        // run the simulation
        simulation.run();
        simulation.printHalfWidthSummaryReport();
    }

}
