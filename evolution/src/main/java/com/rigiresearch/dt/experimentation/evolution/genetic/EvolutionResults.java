package com.rigiresearch.dt.experimentation.evolution.genetic;

import io.jenetics.DoubleGene;
import io.jenetics.Phenotype;
import io.jenetics.engine.EvolutionStatistics;
import io.jenetics.stat.DoubleMomentStatistics;
import lombok.Getter;

/**
 * Defines a the genetic algorithm to search new experimentation scenarios.
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
     * Constructor of the class.
     * @param phenotype The phenotype that was obtained from evolving the algorithm.
     * @param statistics The statistics of the evolution process.
     */
    public EvolutionResults(Phenotype<DoubleGene, Double> phenotype, EvolutionStatistics<Double, DoubleMomentStatistics> statistics){
        this.phenotype = phenotype;
        this.statistics = statistics;
    }
}
