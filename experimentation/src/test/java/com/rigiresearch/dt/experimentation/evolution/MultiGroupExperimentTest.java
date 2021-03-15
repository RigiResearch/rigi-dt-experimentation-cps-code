package com.rigiresearch.dt.experimentation.evolution;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tests {@link MultiGroupExperiment}.
 * @author Miguel Jimenez (miguel@uvic.ca)
 * @version $Id$
 * @since 0.1.0
 */
class MultiGroupExperimentTest {

    /**
     * The logger.
     */
    private static final Logger LOGGER =
        LoggerFactory.getLogger(MultiGroupExperimentTest.class);

    @Test
    void testNormalData() {
        final Double[] control = {66.0, 64.8, 74.9};
        final Double[] treatment1 = control;
        final Double[] treatment2 = control;
        final Map<String, Double[]> samples = new HashMap<>(3);
        samples.put("treat1", treatment1);
        samples.put("treat2", treatment2);
        samples.put("control", control);
        final ExperimentResult result =
            new MultiGroupExperiment(1, samples, 0.05)
            .result();
        MultiGroupExperimentTest.LOGGER.info("\n{}", result);
    }

    @Test
    void testWithSignificantDifferenceAndNormalData() {
        final Double[] control = {66.0, 64.8, 74.9, 81.3, 76.2};
        final Double[] treatment1 = {45.5, 51.3, 42.9, 40.5, 55.2};
        final Double[] treatment2 = {65.0, 66.8, 74.9, 80.3, 75.2};
        final Map<String, Double[]> samples = new HashMap<>(3);
        samples.put("treat1", treatment1);
        samples.put("treat2", treatment2);
        samples.put("control", control);
        final ExperimentResult result =
            new MultiGroupExperiment(1, samples, 0.05)
                .result();
        MultiGroupExperimentTest.LOGGER.info("\n{}", result);
    }

    @Test
    void testWithDataFromCsvFile() throws IOException {
        // FIXME Replace system-specific path
        final Map<String, Double[]> samples = this.readData(
            "/Users/miguel/Development/repositories/" +
                "dt-experimentation-code/evolution/R/replicas-ewt.csv"
        );
        final ExperimentResult result =
            new MultiGroupExperiment(1, samples, 0.05)
                .result();
        MultiGroupExperimentTest.LOGGER.info("\n{}", result);
    }

    private Map<String, Double[]> readData(final String path) throws IOException {
        final Map<String, Double[]> map = new HashMap<>();
        final List<String> lines = Files.readAllLines(Paths.get(path));
        // Remove the header
        lines.remove(0);
        String current = "";
        int position = -1;
        for (final String line : lines) {
            final String[] columns = line.split(",");
            if (!current.equals(columns[0])) {
                current = columns[0];
                map.putIfAbsent(columns[0], new Double[10]);
                position = 0;
            }
            map.get(columns[0])[position] = Double.valueOf(columns[1]);
            position++;
        }
        return map;
    }

}
