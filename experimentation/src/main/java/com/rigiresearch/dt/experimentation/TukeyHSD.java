package com.rigiresearch.dt.experimentation;

import com.github.rcaller.rstuff.RCaller;
import com.github.rcaller.rstuff.RCode;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of Tukey HSD using a bridge to R.
 * @author Miguel Jimenez (miguel@uvic.ca)
 * @version $Id$
 * @since 0.1.0
 */
@RequiredArgsConstructor
public final class TukeyHSD {

    /**
     * The logger;
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(TukeyHSD.class);

    /**
     * Pattern to replace in the output.
     */
    private static final Pattern PATTERN_OUTPUT = Pattern.compile("Output:");

    /**
     * Pattern to separate comparisons.
     */
    private static final Pattern PATTERN_COMP = Pattern.compile("-");

    /**
     * The variable name for the adjusted P values array.
     */
    private static final String VALUES = "values";

    /**
     * The variable name for the comparisons array.
     */
    private static final String COMPARISON = "comparison";

    /**
     * The input data.
     */
    private final Map<String, Double[]> data;

    /**
     * The alpha level.
     */
    private final double alpha;

    /**
     * Runs the test and builds the mean clusters based on the results.
     * @return A non-null, possibly empty map
     */
    public Map<Mean, Set<String>> test() {
        final OutputStream useless = new ByteArrayOutputStream();
        final OutputStream output = new ByteArrayOutputStream();
        // We run the test twice to get the adjusted P values and the comparisons
        // This is a limitation of the RCaller library
        final double[] values = this.test0(TukeyHSD.VALUES, useless)
            .getParser()
            .getAsDoubleArray(TukeyHSD.VALUES);
        final String[] comparisons = this.test0(TukeyHSD.COMPARISON, output)
            .getParser()
            .getAsStringArray(TukeyHSD.COMPARISON);
        TukeyHSD.LOGGER.debug("\n{}", TukeyHSD.PATTERN_OUTPUT
            .matcher(output.toString())
            .replaceAll(""));
        // Split the comparisons (e.g., group1-group2)
        final String[][] groups = new String[comparisons.length][2];
        for (int i = 0; i < comparisons.length; i++) {
            groups[i] = TukeyHSD.PATTERN_COMP.split(comparisons[i]);
        }
        return new MeanCluster(
            this.data,
            this.alpha,
            value -> value <= this.alpha
        ).result(values, groups);
    }

    /**
     * Runs Tukey HSD test.
     * @param output A stream to append the output
     * @return The contents of the output variable
     */
    private RCaller test0(final String variable, final OutputStream output) {
        final RCaller caller = RCaller.create();
        final RCode code = RCode.create();
        code.addDataFrame("df", new EDataFrame(this.data).dataframe());
        code.addRCode("attach(df)");
        code.addRCode(
            String.format(
                "model <- aov(%s ~ %s)",
                EDataFrame.OBSERVATION,
                EDataFrame.GROUP
            )
        );
        code.addRCode("summary(model)");
        code.addRCode(
            String.format(
                "result <- TukeyHSD(model, conf.level=%f)",
                1.0 - this.alpha
            )
        );
        code.addRCode("rdf <- as.data.frame(result$group)");
        code.addRCode("rdf <- cbind(rownames(rdf), rdf)");
        code.addRCode("rownames(rdf) <- NULL");
        code.addRCode("colnames(rdf) <- c(\"comparison\", \"diff\", \"lwr\", \"upr\", \"p.adj\")");
        code.addRCode("rdf");
        code.addRCode(String.format("%s <- rdf$comparison", TukeyHSD.COMPARISON));
        code.addRCode(String.format("%s <- rdf$p.adj", TukeyHSD.VALUES));
        caller.setRCode(code);
        caller.redirectROutputToStream(output);
        caller.runAndReturnResult(variable);
        return caller;
    }

}
