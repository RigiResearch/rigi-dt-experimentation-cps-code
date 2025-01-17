package com.rigiresearch.dt.experimentation.evolution.optimization;

import io.jenetics.prog.op.EphemeralConst;
import io.jenetics.prog.op.Var;
import io.jenetics.util.RandomRegistry;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.function.Function;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tests {@link SymbolicRegression}.
 * @author Miguel Jimenez (miguel@uvic.ca)
 * @version $Id$
 * @since 0.1.0
 */
final class SymbolicRegressionTest {

    // static {
    //     RandomRegistry.random(new SecureRandom("seed".getBytes()));
    // }

    /**
     * The logger.
     */
    private static final Logger LOGGER =
        LoggerFactory.getLogger(SymbolicRegressionTest.class);

    /**
     * A constant to compare doubles.
     */
    private static final double EPSILON = 0.0001;

    @Test
    void testKnownSimpleFunction() {
        // Acceptable error
        final double error = 0.01;
        final long generations = 10000L;
        final Double[][] samples =
            SymbolicRegressionTest.samples(0.0, 1.5, 100, Math::sqrt);
        final SymbolicRegression.Result result =
            new SymbolicRegression(samples, Var.of("x", 0))
                .result(error, generations);

        SymbolicRegressionTest.LOGGER.info("Original: sqrt(x)");
        SymbolicRegressionTest.LOGGER.info("Found: {}", result.getExpression());
        SymbolicRegressionTest.LOGGER.info("Error: {}", result.getError());

        Assertions.assertTrue(
            result.getError() - error < SymbolicRegressionTest.EPSILON,
            "The resulting error is unacceptable"
        );
    }

    @Test
    void testKnownComplexFunction() {
        // Acceptable error
        final double error = 0.01;
        final long generations = 10_000L;
        final Function<Double, Double> function = x ->
            StrictMath.sqrt(x) + 3.0 * x;
            // 5.5 * StrictMath.pow(x, 2.0) + 10.0 * x;
            // 4.0 * StrictMath.pow(x, 3.0) - 3.0 * StrictMath.pow(x, 2.0) + x;
        final Double[][] samples =
            SymbolicRegressionTest.samples(0.0, 1.0, 100, function);
        final SymbolicRegression.Result result =
            new SymbolicRegression(
                samples,
                Var.of("x", 0),
                EphemeralConst.of(() -> (double) RandomRegistry.random().nextInt(10))
            ).result(error, generations);

        SymbolicRegressionTest.LOGGER.info("Original: 5.5x^2 + 10x");
        SymbolicRegressionTest.LOGGER.info("Found: {}", result.getExpression());
        SymbolicRegressionTest.LOGGER.info("Error: {}", result.getError());

        Assertions.assertTrue(
            result.getError() - error < SymbolicRegressionTest.EPSILON,
            "The resulting error is unacceptable"
        );
    }

    @Test
    void testFromCsvData() throws IOException {
        // Acceptable error
        final double error = 0.5;
        final long generations = 10_000L;
        // FIXME computer-specific file path
        final Double[][] samples =
            SymbolicRegressionTest.samples("/Users/miguel/Development/repositories/" +
                "dt-experimentation-code/evolution/R/averaged-results.csv", 4, 7);

        final SymbolicRegression.Result result =
            new SymbolicRegression(
                samples,
                Var.of("x", 0),
                EphemeralConst.of(() -> (double) RandomRegistry.random().nextInt(10))
            ).result(error, generations);

        SymbolicRegressionTest.LOGGER.info("Found: {}", result.getExpression());
        SymbolicRegressionTest.LOGGER.info("Error: {}", result.getError());

        Assertions.assertTrue(
            result.getError() - error < SymbolicRegressionTest.EPSILON,
            "The resulting error is unacceptable"
        );
    }

    /**
     * Generates samples based on a given function.
     * @param start The starting number
     * @param step The increment size
     * @param n The number of samples to generate
     * @param function The function to generate the samples
     * @return A non-null, possibly empty 2D array
     */
    private static Double[][] samples(final double start, final double step,
        final int n, final Function<Double, Double> function) {
        final Double[][] data = new Double[n][2];
        double value = start;
        for (int count = 0; count < n; count++) {
            data[count][0] = value;
            data[count][1] = function.apply(value);
            value++;
        }
        return data;
    }

    /**
     * Reads samples from a CSV file.
     * @param file The path to the CSV file
     * @param x The column containing X data
     * @param y The column containing Y data
     * @return a new array
     */
    private static Double[][] samples(final String file, final int x,
        final int y) throws IOException {
        final List<String> lines = Files.readAllLines(new File(file).toPath());
        lines.remove(0);
        final Double[][] data = new Double[lines.size()][2];
        for (int i = 0; i < lines.size(); i++) {
            final String line = lines.get(i);
            if (line.trim().isEmpty()) {
                System.err.println("Empty line when i = " + i);
                continue;
            }
            final String[] columns = line.trim().split(",");
            data[i][0] = Double.parseDouble(columns[x]);
            data[i][1] = Double.parseDouble(columns[y]);
        }
        return data;
    }

}
