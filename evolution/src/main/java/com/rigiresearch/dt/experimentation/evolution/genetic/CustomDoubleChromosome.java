package com.rigiresearch.dt.experimentation.evolution.genetic;

import io.jenetics.DoubleChromosome;
import io.jenetics.DoubleGene;
import io.jenetics.util.ISeq;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CustomDoubleChromosome extends DoubleChromosome {

    /**
     * The line id.
     */
    private String lineId;

    /**
     * Constructor of the class.
     *
     * @param lineId     The line id.
     * @param chromosome The chromosome.
     */
    public CustomDoubleChromosome(String lineId, DoubleChromosome chromosome) {
        super(CustomDoubleChromosome.getGenes(chromosome), chromosome.lengthRange());
        this.lineId = lineId;
    }

    /**
     * Allows to get a sequence of genes from a chromosome.
     *
     * @param chromosome The chromosome.
     * @return A sequence of genes from a chromosome.
     */
    private static ISeq<DoubleGene> getGenes(DoubleChromosome chromosome) {
        Iterator<DoubleGene> iterator = chromosome.iterator();
        List<DoubleGene> list = new ArrayList<DoubleGene>(chromosome.length());
        while (iterator.hasNext()) {
            DoubleGene gene = iterator.next();
            list.add(gene);
        }
        return ISeq.of(list);
    }

    /**
     * Allows to get the lineId attribute.
     * @return the lineId attribute.
     */
    public String getLineId() {
        return lineId;
    }
}
