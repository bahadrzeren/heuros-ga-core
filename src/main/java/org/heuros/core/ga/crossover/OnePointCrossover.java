package org.heuros.core.ga.crossover;

import org.heuros.core.ga.chromosome.IChromosome;
import java.util.Random;

/**
 * One point crossover implementation class.
 * 
 * @author Ba
 */
public class OnePointCrossover<T, M> implements ICrossoverOperator<T, M> {
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

        int crossoverPosition = (int) Math.floor(random.nextDouble() * mother.getChromosomeLength());

        for (int i = crossoverPosition; i < father.getChromosomeLength(); i++) {
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
