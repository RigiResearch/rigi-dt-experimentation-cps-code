package com.rigiresearch.dt.experimentation;

import com.datumbox.framework.common.dataobjects.TransposeDataCollection;
import com.datumbox.framework.core.statistics.anova.Anova;
import com.datumbox.framework.core.statistics.nonparametrics.independentsamples.KruskalWallis;
import com.datumbox.framework.core.statistics.nonparametrics.onesample.ShapiroWilk;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An experiment.
 * @author Miguel Jimenez (miguel@uvic.ca)
 * @version $Id$
 * @since 0.1.0
 */
@RequiredArgsConstructor
public final class OneVarMultiGroupExperiment implements Experiment {

    /**
     * The logger.
     */
    private static final Logger LOGGER =
        LoggerFactory.getLogger(OneVarMultiGroupExperiment.class);

    /**
     * The collected samples grouped by group id. Each measurement corresponds
     * to one execution of the measured system (same variable).
     */
    private final Map<String, Double[]> data;

    /**
     * The alpha level to do hypothesis testing.
     */
    private final double alpha;

    @Override
    public ExperimentResult result() {
        if (!this.sameLength()) {
            throw new IllegalArgumentException("Groups must be the same length");
        }
        final Map<String, Boolean> normality = this.normality();
        final Map<Mean, Set<String>> clusters;
        final boolean normal = normality.values()
            .stream()
            .reduce(true, (before, value) -> before && value);
        if (this.significantDifference(normal)) {
            if (normal) {
                clusters = new TukeyHSD(this.data, this.alpha).test();
            } else {
                clusters = new DunnTest(this.data, this.alpha).test();
            }
        } else {
            OneVarMultiGroupExperiment.LOGGER.warn(
                "There is no significant difference among groups"
            );
            clusters = new HashMap<>(0);
        }
        return ExperimentResult.builder()
            .normal(normality)
            .means(this.means())
            .clusters(clusters)
            .build();
    }

    /**
     * Ensures that all the collected sample arrays are the same length.
     * @return {@code true} if their size match, {@code false} otherwise
     */
    private boolean sameLength() {
        return this.data.values()
            .stream()
            .map(samples -> samples.length)
            .collect(Collectors.toSet())
            .size() <= 1;
    }

    /**
     * Runs the normality test on the collected data.
     * @return A non-null, possibly empty map
     */
    private Map<String, Boolean> normality() {
        final Map<String, Boolean> map = new HashMap<>(this.data.size());
        this.data.forEach((key, value) -> {
            final boolean result = this.isNormalNonParametric(value);
            map.put(key, result);
        });
        return map;
    }

    /**
     * Computes the mean on the collected data.
     * @return A non-null, possibly empty map
     */
    private Map<String, Mean> means() {
        final Map<String, Mean> map = new HashMap<>(this.data.size());
        this.data.forEach((key, value) -> {
            final Mean mean = new Mean(value, this.alpha);
            map.put(key, mean);
        });
        return map;
    }

    /**
     * Determines whether the samples are normally distributed, using a non
     * parametric test.
     * @param samples The collected samples
     * @return Either {@code true} or {@code false}
     */
    private boolean isNormalNonParametric(final Double[] samples) {
        // The Shapiro–Wilk test tests the null hypothesis that a sample x1,...,xn
        //  came from a normally distributed population
        return !ShapiroWilk.test(Experiment.data(samples), this.alpha);
    }

    /**
     * Determines whether the samples' averages are significantly different.
     * Note that this test assumes that only one variable was observed and that
     * the groups are independent.
     * @param normal Whether the data is normally distributed
     * @return Either {@code true} or {@code false}
     */
    private boolean significantDifference(final boolean normal) {
        final boolean  significant;
        final TransposeDataCollection collection = Experiment.data(this.data);
        if (normal) {
            // A one-way ANOVA is a type of statistical test that compares the
            // variance in the group means within a sample whilst considering
            // only one independent variable or factor. It is a hypothesis-based
            // test, meaning that it aims to evaluate multiple mutually exclusive
            // theories about our data.
            significant = Anova.oneWayTestEqualVars(collection, this.alpha);
        } else {
            // The null hypothesis states that the population medians are all equal
            significant = KruskalWallis.test(collection, this.alpha);
        }
        return significant;
    }

}
