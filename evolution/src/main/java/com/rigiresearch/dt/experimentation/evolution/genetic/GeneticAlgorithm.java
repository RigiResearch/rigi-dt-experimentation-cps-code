package com.rigiresearch.dt.experimentation.evolution.genetic;

import com.rigiresearch.dt.experimentation.evolution.FitnessValue;
import com.rigiresearch.dt.experimentation.simulation.DtSimulation;
import com.rigiresearch.middleware.graph.Graph;
import com.rigiresearch.middleware.graph.Node;
import io.jenetics.*;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionStatistics;
import io.jenetics.engine.Limits;
import io.jenetics.stat.DoubleMomentStatistics;
import io.jenetics.util.Factory;
import org.apache.commons.configuration2.Configuration;

import static io.jenetics.engine.EvolutionResult.toBestPhenotype;

/**
 * Defines a the genetic algorithm to search new experimentation scenarios.
 *
 * @author Felipe Rivera (rivera@uvic.ca)
 * @version $Id$
 * @since 0.1.0
 */
public final class GeneticAlgorithm {

    /**
     * The simulation to be used in the fitness function.
     */
    private DtSimulation dtSimulation;

    /**
     * The set f properties used to configure the simulation and the genetic algorithm.
     */
    private Configuration config;

    /**
     * The graph containing the stations to be used in the simulation.
     */
    private Graph<Node> graph;

    /**
     * The number of generations of the algorithm.
     */
    private int numGenerations;

    /**
     * The length of the replication of the simulations.
     */
    private final int LENGTH_REPLICATION = 100;

    /**
     * The length of the replication of the warm up of the simulations.
     */
    private final int LENGTH_WARM_UP = 100;

    /**
     * Constructor of the class.
     *
     * @param dtSimulation   The simulation to be used in the fitness function.
     * @param config         The set f properties used to configure the simulation and the genetic algorithm.
     * @param graph          The graph containing the stations to be used in the simulation.
     * @param numGenerations The number of generations of the algorithm.
     */
    public GeneticAlgorithm(DtSimulation dtSimulation, Configuration config, Graph<Node> graph, int numGenerations) {
        this.dtSimulation = dtSimulation;
        this.config = config;
        this.graph = graph;
        this.numGenerations = numGenerations;
    }

    /**
     * Obtains a factory for the creation of genotypes.
     *
     * @param chromosomeMin    The min value of the genes of a chromosome.
     * @param chromosomeMax    The max value of the genes of a chromosome.
     * @param chromosomeLength The length of the chromosome.
     * @param numRoutes        The size of the genotype (i.e., the number of routes).
     * @return Factory for the creation of genotypes.
     */
    private Factory<Genotype<DoubleGene>> getFactory(double chromosomeMin, double chromosomeMax, int chromosomeLength, int numRoutes) {
        Factory<Genotype<DoubleGene>> gtf = Genotype.of(DoubleChromosome.of(chromosomeMin, chromosomeMax, chromosomeLength), numRoutes);
        return gtf;
    }

    /**
     * The fitness function that defines the effectiveness of a transit system's configuration (chromosome).
     *
     * @param genotype The gerated genotype in a generation.
     * @return the effectiveness of a transit system's configuration (chromosome).
     */
    private Double fitness(Genotype<DoubleGene> genotype) {

        //TODO Adjust the simulation configuration based on the genotype.
        //config.setProperty();
        //config.setProperty();

        // Configuration and execution of the simulation;
        final DtSimulation simulation = new DtSimulation(graph, config);
        simulation.setLengthOfReplication(LENGTH_REPLICATION);
        simulation.setLengthOfWarmUp(LENGTH_WARM_UP);
        simulation.run();

        // Collection of metrics
        final FitnessValue metrics = new FitnessValue(simulation, config);

        return metrics.asDouble();
    }

    /***
     * Allows to evolve the genetic algorithm and produce results.
     * @param chromosomeMin The min value of the genes of a chromosome.
     * @param chromosomeMax The max value of the genes of a chromosome.
     * @param chromosomeLength The length of the chromosome.
     * @param numRoutes The size of the genotype (i.e., the number of routes).
     * @param populationSize The size of the population.
     * @param steadyNumber The number of steady evolutions before ending the algorithm.
     * @param numGenerations The max num of generations.
     * @return The evolution results.
     */
    public EvolutionResults evolve(double chromosomeMin, double chromosomeMax, int chromosomeLength, int numRoutes, int populationSize, int steadyNumber, int numGenerations) {

        // Obtain the Jenetics engine for the generation.
        final Engine<DoubleGene, Double> engine = Engine.builder(genotype -> this.fitness(genotype), getFactory(chromosomeMin, chromosomeMax, chromosomeLength, numRoutes))
                .populationSize(populationSize)
                .selector(new RouletteWheelSelector<>())
                .alterers(
                        new Mutator<>(0.55),
                        new SinglePointCrossover<>(0.06))
                .build();

        // Define the statistics to be collected.
        final EvolutionStatistics<Double, DoubleMomentStatistics> statistics = EvolutionStatistics.ofNumber();

        // Run the algorithm
        final Phenotype<DoubleGene, Double> phenotypeResult = engine.stream().limit((Limits.bySteadyFitness(steadyNumber))).limit(numGenerations).peek(statistics).collect(toBestPhenotype());

        return new EvolutionResults(phenotypeResult, statistics);

    }

}


