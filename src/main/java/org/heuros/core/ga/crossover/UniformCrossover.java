package org.heuros.core.ga.crossover;

import java.util.Random;

import org.heuros.core.ga.chromosome.Chromosome;

/**
 * Uniform crossover implementation class.
 * 
 * @author Ba
 */
public class UniformCrossover<T> implements Crossover<T> {
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

        double motherQuality = Math.abs(worstFitness - mother.getFitness());
        double fatherQuality = Math.abs(worstFitness - father.getFitness());

        double ratio = motherQuality / (motherQuality + fatherQuality);

        for (int i = 0; i < mother.getChromosomeLength(); i++) {
            if (random.nextDouble() > ratio) {
                child1.setGeneValue(i, father.getGeneValue(i));
                child2.setGeneValue(i, mother.getGeneValue(i));
            }
        }

        population[res] = child1;
        res++;
        population[res] = child2;
        res++;
        return res;
    }

    /**
     * Clones the this object.
     * 
     * @return Object new cloned object.
     * 
     * @throws CloneNotSupportedException is trown if this object doesnt
     * implements Cloneable interface.
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
