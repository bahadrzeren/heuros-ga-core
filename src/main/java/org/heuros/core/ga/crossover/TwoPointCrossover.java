package org.heuros.core.ga.crossover;

import java.util.Random;

import org.heuros.core.ga.chromosome.Chromosome;

/**
 * Two point crossover implementation class.
 * 
 * @author bahadrzeren
 * 
 * @param <T> Type of the class which is used to represent one single gene.
 */
public class TwoPointCrossover<T> implements Crossover<T> {
	private static Random random = new Random();

	@SuppressWarnings("unchecked")
	@Override
    public int crossover(Chromosome<T> population[],
                            int startingChildIndex,
                            Chromosome<T> mother,
                            Chromosome<T> father,
                            double worstFitness) throws CloneNotSupportedException {
        int res = startingChildIndex;

        Chromosome<T> child1 = (Chromosome<T>) mother.clone();
        Chromosome<T> child2 = (Chromosome<T>) father.clone();

        int crossoverPosition1 = (int) Math.floor(random.nextDouble() * mother.getChromosomeLength());
        int crossoverPosition2 = (int) Math.floor(random.nextDouble() * (mother.getChromosomeLength() - crossoverPosition1)) + crossoverPosition1;

        for (int i = crossoverPosition1; i < crossoverPosition2; i++) {
            child1.setGeneValue(i, father.getGeneValue(i));
            child2.setGeneValue(i, mother.getGeneValue(i));
        }

        population[res] = child1;
        res++;
        population[res] = child2;
        res++;
        return res;
    }
}
