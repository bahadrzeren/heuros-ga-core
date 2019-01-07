package org.heuros.core.ga.mutation;

import java.util.Random;

import org.heuros.core.ga.chromosome.Chromosome;

/**
 * Integer mutation operator that changes gene values as not to exceed a specific limit that is designated by maxGeneValueExc parameter.
 * 
 * @author bahadrzeren
 *
 */
public class SwapGeneMutator implements Mutator<Integer> {

	private static Random random = new Random();
	private static int range = 100;

	@SuppressWarnings("unchecked")
	@Override
	public Chromosome<Integer> mutate(Chromosome<Integer> chromosome, int iteration, float mutationRatio)
			throws CloneNotSupportedException {
		Chromosome<Integer> mutatedChromosome = null;
		for (int i = 0; i < chromosome.getChromosomeLength(); i++) {
			if (SwapGeneMutator.random.nextDouble() < mutationRatio) {
				if (mutatedChromosome == null)
					mutatedChromosome = (Chromosome<Integer>) chromosome.clone();
				int h = mutatedChromosome.getGeneValue(i);
				int swpNdx = i + random.nextInt(range);
				if (swpNdx >= chromosome.getChromosomeLength())
					swpNdx = i - random.nextInt(range);
				mutatedChromosome.setGeneValue(i, mutatedChromosome.getGeneValue(swpNdx));
				mutatedChromosome.setGeneValue(swpNdx, h);
			}
		}
		return mutatedChromosome;
	}

}
