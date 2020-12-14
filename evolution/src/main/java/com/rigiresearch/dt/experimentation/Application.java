package com.rigiresearch.dt.experimentation;

import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The main class.
 * @author Miguel Jimenez (miguel@uvic.ca)
 * @version $Id$
 * @since 0.1.0
 */
@Accessors(fluent = true)
@RequiredArgsConstructor
public final class Application {

    /**
     * The logger.
     */
    private static final Logger LOGGER =
        LoggerFactory.getLogger(Application.class);

    /**
     * The main entry point.
     * @param args The application arguments
     * @throws Exception If there is a problem running the Epsilon examples
     */
    public static void main(final String... args) throws Exception {
        Application.LOGGER.info("Nothing to do here...");
    }

}
