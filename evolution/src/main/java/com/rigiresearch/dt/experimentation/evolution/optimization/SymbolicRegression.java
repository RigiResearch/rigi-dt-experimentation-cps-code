package com.rigiresearch.dt.experimentation.evolution.optimization;

import io.jenetics.Mutator;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.engine.Limits;
import io.jenetics.ext.SingleNodeCrossover;
import io.jenetics.ext.util.TreeNode;
import io.jenetics.prog.ProgramGene;
import io.jenetics.prog.op.MathExpr;
import io.jenetics.prog.op.MathOp;
import io.jenetics.prog.op.Op;
import io.jenetics.prog.regression.Error;
import io.jenetics.prog.regression.LossFunction;
import io.jenetics.prog.regression.Regression;
import io.jenetics.prog.regression.Sample;
import io.jenetics.util.ISeq;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Value;

/**
 * Finds a function based on a finite array of samples using symbolic regression.
 * @author Miguel Jimenez (miguel@uvic.ca)
 * @version $Id$
 * @since 0.1.0
 */
public final class SymbolicRegression {

    /**
     * The data points to come up with a function.
     */
    private final Double[][] data;

    /**
     * The functions to include.
     */
    private static final ISeq<Op<Double>> OPERATIONS = ISeq.of(
        // MathOp.ABS,
        // MathOp.ACOS,
        MathOp.ADD,
        // MathOp.ASIN,
        // MathOp.ATAN,
        // MathOp.COS,
        // MathOp.DIV,
        // MathOp.EXP,
        // MathOp.LOG,
        // MathOp.LOG10,
        MathOp.MUL,
        MathOp.POW,
        // MathOp.SIN,
        MathOp.SQRT,
        MathOp.SUB
        // MathOp.TAN
    );

    /**
     * The variables to use.
     */
    private final Op<Double>[] terminals;

    /**
     * A regression config object.
     */
    private final Regression<Double> regression;

    /**
     * Default constructor.
     * @param data The data points to come up with a function
     * @param terminals The terminals to use
     */
    public SymbolicRegression(final Double[][] data,
        final Op<Double>... terminals) {
        this.data = data.clone();
        this.terminals = terminals.clone();
        this.regression = Regression.of(
            Regression.codecOf(
                SymbolicRegression.OPERATIONS,
                ISeq.of(this.terminals),
                5
            ),
            Error.of(LossFunction::mse),
            this.samples()
        );
    }

    /**
     * Runs the genetic algorithm.
     * @param threshold A stop condition based on a threshold
     * @param generations The maximum number of generations
     * @return The algorithm's result
     */
    public SymbolicRegression.Result result(final double threshold,
        final long generations) {
        final TreeNode<Op<Double>> tree = this.tree(threshold, generations);
        MathExpr.rewrite(tree); // Simplify the resulting program
        return new SymbolicRegression.Result(
            new MathExpr(tree),
            this.regression.error(tree)
        );
    }

    /**
     * Runs the genetic algorithm to find a function.
     * @param threshold A stop condition based on a threshold
     * @param generations The maximum number of generations
     * @return An abstract tree representing the function
     */
    private TreeNode<Op<Double>> tree(final double threshold,
        final long generations) {
        final Engine<ProgramGene<Double>, Double> engine = Engine
            .builder(this.regression)
            .minimizing()
            .alterers(
                new SingleNodeCrossover<>(0.1),
                new Mutator<>())
            .build();
        final EvolutionResult<ProgramGene<Double>, Double> result = engine
            .stream()
            .limit(Limits.byFitnessThreshold(threshold))
            .limit(Limits.byFixedGeneration(generations))
            .limit(Limits.byExecutionTime(Duration.ofMinutes(1L)))
            .collect(EvolutionResult.toBestEvolutionResult());
        return result.bestPhenotype()
            .genotype()
            .gene()
            .toTreeNode();
    }

    /**
     * Creates samples based on the provided data.
     * @return A non-null, possibly empty list
     */
    private List<Sample<Double>> samples() {
        return Arrays.stream(this.data)
            .map(Sample::of)
            .collect(Collectors.toList());
    }

    @Value
    public static class Result {
        /**
         * A mathematical expression.
         */
        MathExpr expression;

        /**
         * The corresponding error.
         */
        double error;

        @Override
        public String toString() {
            return String.format(
                "Expression: %s\nError: %f",
                this.expression,
                this.error
            );
        }
    }

}
