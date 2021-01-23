package com.rigiresearch.dt.experimentation.evolution.genetic;
import com.rigiresearch.dt.experimentation.simulation.DtSimulation;
import io.jenetics.*;
import io.jenetics.util.Factory;

/**
 * Defines a the genetic algorithm to search new experimentation scenarios.
 * @author Felipe Rivera (rivera@uvic.ca)
 * @version $Id$
 * @since 0.1.0
 */
public class GeneticAlgorithm {

    /**
     * The simulation to be used in the fitness function.
     */
    private DtSimulation dtSimulation;


    /**
     * Constructor of the class
     */
    public GeneticAlgorithm (DtSimulation dtSimulation, ) {
        this.dtSimulation = dtSimulation;
    }

    /**
     * Obtains a factory for the creation of genotypes.
     * @param chromosomeMin The min value of the genes of a chromosome.
     * @param chromosomeMax The max value of the genes of a chromosome.
     * @param chromosomeLength The length of the chromosome.
     * @param numRoutes The size of the genotype (i.e., the number of routes).
     * @return Factory for the creation of genotypes.
     */
    private Factory<Genotype<DoubleGene>> getFactory(double chromosomeMin, double chromosomeMax, int chromosomeLength, int numRoutes) {
        Factory<Genotype<DoubleGene>> gtf = Genotype.of(DoubleChromosome.of(chromosomeMin,chromosomeMax,chromosomeLength),numRoutes);
        return gtf;
    }

    private double fitness(){
        return -1;
    }



    }


