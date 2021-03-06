package org.heuros.core.ga.crossover;

import java.util.Random;

import org.heuros.core.ga.ISolutionCost;
import org.heuros.core.ga.chromosome.Chromosome;

/**
 * Uniform crossover implementation class.
 * 
 * @author bahadrzeren
 * 
 * @param <T> Type of the class which is used to represent one single gene.
 */
public class UniformCrossover<T> implements Crossover<T> {
	private static Random random = new Random();

	@SuppressWarnings("unchecked")
	@Override
    public int crossover(Chromosome<T> population[],
                            int startingChildIndex,
                            Chromosome<T> mother,
                            Chromosome<T> father,
                            ISolutionCost worstFitness) throws CloneNotSupportedException {
        int res = startingChildIndex;

        Chromosome<T> childM = (Chromosome<T>) mother.clone();
        Chromosome<T> childF = (Chromosome<T>) father.clone();

//      double motherQuality = Math.abs(worstFitness - mother.getFitness());
//      double fatherQuality = Math.abs(worstFitness - father.getFitness());
//      double ratio = motherQuality / (motherQuality + fatherQuality);

        double motherQuality = Math.abs(mother.getFitness().getDistance(worstFitness));
		double fatherQuality = Math.abs(father.getFitness().getDistance(worstFitness));
		double ratio = motherQuality / (motherQuality + fatherQuality);

        for (int i = 0; i < mother.getChromosomeLength(); i++) {
            if (random.nextDouble() > ratio) {
                childM.setGeneValue(i, father.getGeneValue(i));
                childF.setGeneValue(i, mother.getGeneValue(i));
            }
        }

        population[res] = childM;
        res++;
        population[res] = childF;
        res++;
        return res;
    }
}
