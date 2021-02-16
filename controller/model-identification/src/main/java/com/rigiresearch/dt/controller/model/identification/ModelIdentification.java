package com.rigiresearch.dt.controller.model.identification;

import com.rigiresearch.dt.experimentation.evolution.Record;
import com.rigiresearch.dt.experimentation.evolution.genetic.EvolutionResults;
import com.rigiresearch.dt.experimentation.evolution.genetic.GeneticAlgorithm;
import com.rigiresearch.dt.experimentation.evolution.optimization.DifferentiableFunction;
import com.rigiresearch.middleware.graph.Graph;
import com.rigiresearch.middleware.graph.Node;
import java.util.Comparator;
import lombok.RequiredArgsConstructor;
import org.apache.commons.configuration2.Configuration;

/**
 * A basic implementation of the model identification component.
 * @author Miguel Jimenez (miguel@uvic.ca)
 * @version $Id$
 * @since 0.1.0
 */
@RequiredArgsConstructor
public final class ModelIdentification {

    /**
     * The maximum number of generations.
     */
    private static final int MAX_GENERATIONS = 200;

    /**
     * The size of the population.
     */
    private static final int POPULATION_SIZE = 20;

    /**
     * The number of consecutive evolutions that produce similar results before stopping the algorithm.
     */
    private static final int STEADY_NUMBER = 7;

    /**
     * The mutation probability.
     */
    private static final double MUTATION_PROB = 0.10;

    /**
     * The crossover probability.
     */
    private static final double CROSSOVER_PROB = 0.80;

    /**
     * The number of results to collect.
     */
    private static final int NUMBER_RESULTS = 100;

    /**
     * Initial configuration of the simulation and graph models.
     */
    private final Configuration config;

    /**
     * The results of running the genetic algorithm.
     */
    private EvolutionResults results;

    /**
     * Triggers the model identification process and returns the resulting
     * function.
     * @param graph The topology model
     * @return A non-null function (may be inaccurate)
     */
    public DifferentiableFunction model(final Graph<Node> graph) {
        final GeneticAlgorithm algorithm =
            new GeneticAlgorithm(this.config, graph, ModelIdentification.MAX_GENERATIONS);
        this.results = algorithm.evolve(
            ModelIdentification.POPULATION_SIZE,
            ModelIdentification.STEADY_NUMBER,
            ModelIdentification.MUTATION_PROB,
            ModelIdentification.CROSSOVER_PROB,
            ModelIdentification.NUMBER_RESULTS
        );
        final Comparator<Record> comparator = (r1, r2) -> {
            final String key = "fitness.value";
            return ((Double) r1.get(key)).compareTo((Double) r2.get(key));
        };
        // TODO Put all replicas into a single collection
        // this.results.getRecords()
        //     .stream()
        //     .map()
        //     .sorted(comparator)
        //     .limit(100L);
        return null;
    }

    /**
     * The evolution results returned by the genetic algorithm.
     * @return Non-null object
     */
    public EvolutionResults result() {
        if (this.results == null) {
            throw new IllegalStateException("You must run the #model() method first");
        }
        return this.results;
    }

    // private DifferentialFunction<DoubleReal> function(final String expression) {
    //     return new AbstractTreeBuilder<>(
    //         expression,
    //         AbstractTreeBuilderTest.DF,
    //         x,
    //         y
    //     ).getTree();
    // }

}
