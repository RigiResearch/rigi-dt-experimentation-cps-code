package com.rigiresearch.dt.experimentation.evolution.genetic;

import com.rigiresearch.dt.experimentation.evolution.Record;
import io.jenetics.DoubleGene;
import io.jenetics.Phenotype;
import io.jenetics.engine.EvolutionStatistics;
import io.jenetics.stat.DoubleMomentStatistics;
import lombok.Getter;

import java.util.List;

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
    private Phenotype<DoubleGene, Double> phenotype;

    /**
     * The statistics of the evolution process.
     */
    private EvolutionStatistics<Double, DoubleMomentStatistics> statistics;

    /**
     * The simulation records.
     */
    private List<Record> records;

    /**
     * Constructor of the class.
     *
     * @param phenotype  The phenotype that was obtained from evolving the algorithm.
     * @param statistics The statistics of the evolution process.
     * @param records    The simulation records.
     */
    public EvolutionResults(Phenotype<DoubleGene, Double> phenotype, EvolutionStatistics<Double, DoubleMomentStatistics> statistics, List<Record> records) {
        this.phenotype = phenotype;
        this.statistics = statistics;
        this.records = records;
    }
}
