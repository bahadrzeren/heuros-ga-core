package org.heuros.core.ga.mutation;

import java.util.Random;

import org.heuros.core.ga.chromosome.Chromosome;

/**
 * Integer mutation operator that changes gene values as not to exceed a specific limit that is designated by maxGeneValueExc parameter.
 * 
 * @author bahadrzeren
 *
 */
public class IntegerGeneMutator implements Mutator<Integer> {

	private static Random random = new Random();

	private int maxGeneValueExc = 3;

	public IntegerGeneMutator setMaxGeneValueExc(int value) {
		this.maxGeneValueExc = value;
		return this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Chromosome<Integer> mutate(Chromosome<Integer> chromosome, int iteration, float mutationRatio)
			throws CloneNotSupportedException {
		Chromosome<Integer> mutatedChromosome = null;
		for (int i = 0; i < chromosome.getChromosomeLength(); i++) {
			if (IntegerGeneMutator.random.nextDouble() < mutationRatio) {
				if (mutatedChromosome == null)
					mutatedChromosome = (Chromosome<Integer>) chromosome.clone();
				int newValue = (mutatedChromosome.getGeneValue(i) + random.nextInt(this.maxGeneValueExc - 1) + 1) % this.maxGeneValueExc;
				mutatedChromosome.setGeneValue(i, newValue);
			}
		}
		return mutatedChromosome;
	}

}
