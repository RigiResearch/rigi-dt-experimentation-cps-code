package com.rigiresearch.dt.experimentation;

import com.github.rcaller.rstuff.RCaller;
import com.github.rcaller.rstuff.RCode;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of Dunn's test using a bridge to R.
 * @author Miguel Jimenez (miguel@uvic.ca)
 * @version $Id$
 * @since 0.1.0
 */
@RequiredArgsConstructor
public final class DunnTest {

    /**
     * The logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(DunnTest.class);

    /**
     * The variable name for adjusted P values.
     */
    private static final String VALUES = "values";

    /**
     * The variable name for the comparisons array.
     */
    private static final String COMPARISONS = "comparisons";

    /**
     * Pattern to replace in the output.
     */
    private static final Pattern PATTERN_OUTPUT = Pattern.compile("Output:");

    /**
     * Pattern to separate comparisons.
     */
    private static final Pattern PATTERN_COMP = Pattern.compile(" - ");

    /**
     * The input data.
     */
    private final Map<String, Double[]> data;

    /**
     * The alpha level for accepting or rejecting the null hypothesis.
     */
    private final double alpha;

    /**
     * Run Dunn's test and return the resulting clusters.
     * @return A non-null, possibly empty map
     */
    public Map<Mean, List<String>> test() {
        final OutputStream useless = new ByteArrayOutputStream();
        final OutputStream output = new ByteArrayOutputStream();
        // We run the test twice to get the adjusted P values and the comparisons
        // This is a limitation of the RCaller library
        final double[] values = this.test0(DunnTest.VALUES, useless)
            .getParser().getAsDoubleArray(DunnTest.VALUES);
        final String[] comparisons = this.test0(DunnTest.COMPARISONS, output)
            .getParser().getAsStringArray(DunnTest.COMPARISONS);
        DunnTest.LOGGER.debug("\n{}", DunnTest.PATTERN_OUTPUT
            .matcher(output.toString())
            .replaceAll(""));
        // Split the comparisons (e.g., group1 - group2)
        final String[][] groups = new String[comparisons.length][2];
        for (int i = 0; i < comparisons.length; i++) {
            groups[i] = DunnTest.PATTERN_COMP.split(comparisons[i]);
        }
        return new MeanCluster(
            this.data,
            this.alpha,
            value -> value <= this.alpha/2.0
        ).result(values, groups);
    }

    /**
     * Runs Dunn's test.
     * @param variable The variable name to return from R
     * @param output A stream to append the output
     * @return The RCaller instance
     */
    private RCaller test0(final String variable, final OutputStream output) {
        this.preconditions();
        final RCaller caller = RCaller.create();
        final RCode code = RCode.create();
        code.R_require("dunn.test");
        code.addDataFrame("df", new EDataFrame(this.data).dataframe());
        code.addRCode("attach(df)");
        code.addRCode(
            String.format(
                "result <- dunn.test(%s, %s, method=\"%s\", list=TRUE)",
                EDataFrame.OBSERVATION,
                EDataFrame.GROUP,
                "hochberg"
            )
        );
        code.addRCode(String.format("%s <- result$comparisons", DunnTest.COMPARISONS));
        code.addRCode(String.format("%s <- result$P.adjust", DunnTest.VALUES));
        caller.setRCode(code);
        caller.redirectROutputToStream(output);
        caller.runAndReturnResult(variable);
        return caller;
    }

    /**
     * Evaluates preconditions for the experiment analysis to work.
     */
    private void preconditions() {
        if (this.data.keySet().size() <= 2) {
            throw new IllegalArgumentException(
                "Dunn.test only works with more than 2 groups"
            );
        }
    }

}
