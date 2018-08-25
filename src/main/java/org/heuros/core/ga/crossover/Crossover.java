package org.heuros.core.ga.crossover;

import org.heuros.core.ga.chromosome.Chromosome;

public interface Crossover<T> {
    /**
     * Performs crossover operation according to the algorithm in the 
     * implementing class.
     * 
     * @param population list of whole population.
     * @param startingChildIndex starting index for newly generated children.
     * @param mother first candidate of the crossover operation.
     * @param father second candidate of the crossover operation.
     * @param worstFitness the worst fitness value of the current population.
     * 
     * @return last free index of children list.
     * 
     * @throws CloneNotSupportedException is trown if chromosome objects do not
     * implement Cloneable interface
     */
    public int crossover(Chromosome<T> population[],
                            int startingChildIndex,
                            Chromosome<T> mother,
                            Chromosome<T> father,
                            double worstFitness) throws CloneNotSupportedException;
}
