package org.heuros.core.ga.crossover;

import java.util.Random;

import org.heuros.core.ga.chromosome.IChromosome;

/**
 * Two point crossover implementation class.
 * 
 * @author Ba
 */
public class TwoPointCrossover<T, M> implements ICrossoverOperator<T, M> {
	private static Random random = new Random();

	@SuppressWarnings("unchecked")
	@Override
    public int crossover(IChromosome<T, M> population[],
                            int startingChildIndex,
                            IChromosome<T, M> mother,
                            IChromosome<T, M> father,
                            double worstFitness) throws CloneNotSupportedException {
        int res = startingChildIndex;

        IChromosome<T, M> child1 = (IChromosome<T, M>) mother.clone();
        IChromosome<T, M> child2 = (IChromosome<T, M>) father.clone();

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
