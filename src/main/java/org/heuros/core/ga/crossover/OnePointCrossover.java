package org.heuros.core.ga.crossover;

import org.heuros.core.ga.ISolutionCost;
import org.heuros.core.ga.chromosome.Chromosome;
import java.util.Random;

/**
 * One point crossover implementation class.
 * 
 * @author bahadrzeren
 * 
 * @param <T> Type of the class which is used to represent one single gene.
 */
public class OnePointCrossover<T> implements Crossover<T> {
	private static Random random = new Random();

	@SuppressWarnings("unchecked")
	@Override
    public int crossover(Chromosome<T> population[],
                            int startingChildIndex,
                            Chromosome<T> mother,
                            Chromosome<T> father,
                            ISolutionCost worstFitness) throws CloneNotSupportedException {
        int res = startingChildIndex;

        Chromosome<T> child1 = (Chromosome<T>) mother.clone();
        Chromosome<T> child2 = (Chromosome<T>) father.clone();

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
