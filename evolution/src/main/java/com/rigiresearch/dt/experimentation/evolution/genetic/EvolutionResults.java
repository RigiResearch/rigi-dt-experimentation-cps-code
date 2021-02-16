package com.rigiresearch.dt.experimentation.evolution.genetic;

import com.rigiresearch.dt.experimentation.evolution.Record;
import io.jenetics.DoubleGene;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.engine.EvolutionStatistics;
import io.jenetics.stat.DoubleMomentStatistics;
import io.jenetics.util.ISeq;
import java.util.List;
import lombok.Getter;

/**
 * Defines a the genetic algorithm to search new experimentation scenarios.
 *
 * @author Felipe Rivera (rivera@uvic.ca)
 * @version $Id$
 * @since 0.1.0
 */
@Getter
public final class EvolutionResults {

    /**
     * The phenotype that was obtained from evolving the algorithm.
     */
    private final ISeq<EvolutionResult<DoubleGene, Double>> results;

    /**
     * The statistics of the evolution process.
     */
    private final EvolutionStatistics<Double, DoubleMomentStatistics> statistics;

    /**
     * The simulation records.
     */
    private final List<Record> records;

    /**
     * Constructor of the class.
     *
     * @param results A sequence of evolution results.
     * @param statistics The statistics of the evolution process.
     * @param records    The simulation records.
     */
    public EvolutionResults(final ISeq<EvolutionResult<DoubleGene, Double>> results,
        EvolutionStatistics<Double, DoubleMomentStatistics> statistics,
        List<Record> records) {
        this.results = results;
        this.statistics = statistics;
        this.records = records;
    }
}
