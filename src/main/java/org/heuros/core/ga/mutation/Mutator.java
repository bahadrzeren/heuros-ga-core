package org.heuros.core.ga.mutation;

import org.heuros.core.ga.chromosome.Chromosome;

/**
 * Interface for mutation implementations.
 * 
 * @author bahadrzeren
 *
 * @param <T> Type of the class which is used to represent one single gene.
 */
public interface Mutator<T> {
    /**
     * Performs mutation operation according to the algorithm in the 
     * implementing class.
     * 
     * @param chromosome to be mutated.
     * @param mutationRatio ratio of the mutation operation to be performed.
     * 
     * @return IChromosome<T> new mutated chromosome.
     */
    public Chromosome<T> mutate(Chromosome<T> chromosome,
                                    int iteration,
                                    float mutationRatio) throws CloneNotSupportedException;
}
