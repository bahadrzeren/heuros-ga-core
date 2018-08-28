package org.heuros.core.ga.selection;

import org.heuros.core.ga.chromosome.Chromosome;

/**
 * Interface for the selection operators.
 * Used for offspring selection in crossover and population replacement operations. 
 * 
 * @author bahadrzeren
 *
 * @param <T> Type of the class which is used to represent one single gene.
 */
public interface Selector<T> {
    /**
     * Selects a chromosome for reproduction (crossover) according to the 
     * algorithm of the implementing class.
     * 
     * @param population current population  of the genetic optimizer.
     * @param range size of the population
     * 
     * @return IChromosome selected chromosome object.
     */
    public Chromosome<T> selectChromosomeForReproduction(Chromosome<T>[] population, int range);

    /**
     * Selects a chromosome for population replacement (survival) according to the algorithm of the implementing class.
     * 
     * @param chromosomeList current total population (population + children) of 
     * the genetic optimizer.
     * @param rangeStart inclusive start index of the range which is used to choose candidate chromosome.
     * @param rangeEnd exclusive end index of the range which is used to choose candidate chromosome.
     * 
     */
    public void replaceChromosomeForSurvival(Chromosome<T>[] population, int rangeStart, int rangeEnd);
}
