package org.heuros.core.ga.mutation;

import org.heuros.core.ga.chromosome.IChromosome;

public interface Mutator<T, M> {
    /**
     * Performs mutation operation according to the algorithm in the 
     * implementing class.
     * 
     * @param chromosome to be mutated.
     * @param mutationRatio ratio of the mutation operation to be performed.
     * @param random Randomization class to be used.
     * 
     * @return IChromosome<T, M> new mutated chromosome.
     */
    public IChromosome<T, M> mutate(IChromosome<T, M> chromosome,
                                    int iteration,
                                    float mutationRatio) throws CloneNotSupportedException;
}
