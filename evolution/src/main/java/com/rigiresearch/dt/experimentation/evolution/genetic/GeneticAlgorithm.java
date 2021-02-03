package com.rigiresearch.dt.experimentation.evolution.genetic;

import com.rigiresearch.dt.experimentation.evolution.FitnessValue;
import com.rigiresearch.dt.experimentation.evolution.Record;
import com.rigiresearch.dt.experimentation.simulation.DtSimulation;
import com.rigiresearch.middleware.graph.Graph;
import com.rigiresearch.middleware.graph.Node;
import io.jenetics.Chromosome;
import io.jenetics.DoubleChromosome;
import io.jenetics.DoubleGene;
import io.jenetics.Genotype;
import io.jenetics.IntegerChromosome;
import io.jenetics.Mutator;
import io.jenetics.Phenotype;
import io.jenetics.RouletteWheelSelector;
import io.jenetics.SinglePointCrossover;
import io.jenetics.engine.Engine;
import static io.jenetics.engine.EvolutionResult.toBestPhenotype;
import io.jenetics.engine.EvolutionStatistics;
import io.jenetics.engine.Limits;
import io.jenetics.stat.DoubleMomentStatistics;
import io.jenetics.util.DoubleRange;
import io.jenetics.util.IntRange;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import lombok.Getter;
import org.apache.commons.configuration2.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Defines a the genetic algorithm to search new experimentation scenarios.
 *
 * @author Felipe Rivera (rivera@uvic.ca)
 * @version $Id$
 * @since 0.1.0
 */
@SuppressWarnings("unchecked")
public final class GeneticAlgorithm {

    /**
     * The logger.
     */
    private static final Logger LOGGER =
        LoggerFactory.getLogger(GeneticAlgorithm.class);

    /**
     * The ids of the lines defined in the configuration file.
     */
    @Getter
    private List<String> lineIds;

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
    public final static int LENGTH_REPLICATION = 100;

    /**
     * The length of the replication of the warm up of the simulations.
     */
    public final static int LENGTH_WARM_UP = 100;

    /**
     * The simulation recoreds.
     */
    private List<Record> simulationRecords;

    /**
     * The encoding used to characterize a solution in the problem.
     */
    private Genotype encoding;

    /**
     * Constructor of the class.
     *
     * @param config         The set f properties used to configure the simulation and the genetic algorithm.
     * @param graph          The graph containing the stations to be used in the simulation.
     * @param numGenerations The number of generations of the algorithm.
     */
    public GeneticAlgorithm(Configuration config, Graph<Node> graph, int numGenerations) {
        this.config = config;
        this.graph = graph;
        this.numGenerations = numGenerations;
        //lineIds = config.getList("lines").stream().map(String.class::cast).collect(Collectors.toSet());
        lineIds = config.getList("lines").stream().map(String.class::cast).collect(Collectors.toList());
        simulationRecords = new ArrayList<Record>(numGenerations);
        generateEncoding();
    }

    /**
     * Allows to generate the appropriate encoding for the genetic algorithm.
     */
    private void generateEncoding() {
        if (config != null) {
            if (lineIds != null) {
                if (lineIds.size() > 0) {
                    final List<Chromosome> chromosomes = new ArrayList<Chromosome>(lineIds.size() * 2);
                    for (String lineId : lineIds) {
                        final DoubleRange headwayRange = DoubleRange.of(config.getDouble(lineId.concat(".").concat("headway.min")), config.getDouble(lineId.concat(".").concat("headway.max")));
                        chromosomes.add(DoubleChromosome.of(headwayRange));
                        final IntRange busRange = IntRange.of(1, config.getInt(lineId.concat(".").concat("fleet")));
                        chromosomes.add(IntegerChromosome.of(busRange));
                    }
                    encoding = Genotype.of((Iterable) chromosomes);
                }
            }
        }
    }

    /**
     * The fitness function that defines the effectiveness of a transit system's configuration (chromosome).
     *
     * @param genotype The generated genotype in a generation.
     * @return the effectiveness of a transit system's configuration (chromosome).
     */
    private Double fitness(Genotype genotype) {
        int lineId = 0;
        // Adjusting properties for the simulation
        for (int i = 0; i < genotype.length(); i++) {
            if (i % 2 == 0) {
                //Double Chromosome
                final DoubleChromosome headwayChromosome = (DoubleChromosome) genotype.get(i);
                config.setProperty(lineIds.get(lineId).concat(".").concat(EvolvingProperties.HEADWAY.getId()), headwayChromosome.get(0).doubleValue());
            } else {
                //Integer Chromosome
                final IntegerChromosome busesChromosome = (IntegerChromosome) genotype.get(i);
                config.setProperty(lineIds.get(lineId).concat(".").concat(EvolvingProperties.NUM_BUSES.getId()), busesChromosome.get(0).intValue());
                lineId++;
            }
        }
        // Configuration and execution of the simulation;
        final DtSimulation simulation = new DtSimulation(graph, config);
        simulation.setLengthOfReplication(LENGTH_REPLICATION);
        simulation.setLengthOfWarmUp(LENGTH_WARM_UP);
        simulation.run();
        // Collection of metrics
        final FitnessValue metrics = new FitnessValue(simulation, config);
        // Obtaining the record
        final Collection<Record> records = metrics.asRecords();
        this.simulationRecords.addAll(records);
        return records.stream()
            .map(record -> {
                GeneticAlgorithm.LOGGER.info(record.asLog());
                return record.get(EvolvingProperties.SIM_FITNESS.getId());
            })
            .map(Double.class::cast)
            .mapToDouble(value -> value)
            .sum();
    }

    /***
     * Allows to evolve the genetic algorithm and produce results.
     * @param populationSize The size of the population.
     * @param steadyNumber The number of steady evolutions before ending the algorithm.
     * @param mutationProb The mutation probability.
     * @param crossoverProb The crossover probability.
     * @return The evolution results.
     */
    public EvolutionResults evolve(int populationSize, int steadyNumber, double mutationProb, double crossoverProb) {
        // Obtain the Jenetics engine for the generation.
        final Engine<DoubleGene, Double> engine = Engine.builder(this::fitness, encoding)
                .populationSize(populationSize)
                .maximizing()
                .selector(new RouletteWheelSelector<>())
                .alterers(
                        new Mutator<>(mutationProb),
                        new SinglePointCrossover<>(crossoverProb))
                .executor(Executors.newSingleThreadExecutor())
                .build();

        // Define the statistics to be collected.
        final EvolutionStatistics<Double, DoubleMomentStatistics> statistics = EvolutionStatistics.ofNumber();
        // Run the algorithm
        final Phenotype<DoubleGene, Double> phenotypeResult = engine.stream().limit((Limits.bySteadyFitness(steadyNumber))).limit(numGenerations).peek(statistics).collect(toBestPhenotype());
        return new EvolutionResults(phenotypeResult, statistics, simulationRecords);

    }

}


