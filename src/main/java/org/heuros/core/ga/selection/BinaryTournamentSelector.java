/**
 *
 * © Bahadir Zeren 2011
 * bzeren@gmail.com
 * No warranties, express or implied, are made for this program.
 *
 */

package org.heuros.core.ga.selection;

import java.util.Random;

import org.heuros.core.ga.chromosome.Chromosome;

/**
 * Binary tournament selection implementation of the ISelection interface.
 * 
 * @author bahadrzeren
 * 
 * @see Selector
 */
public class BinaryTournamentSelector<T> implements Selector<T> {

	private static Random random = new Random();

	/**
     * {@inheritDoc}
     */
    @Override
    public Chromosome<T> selectChromosomeForReproduction(Chromosome<T>[] population,
                                                                int range) {

        Chromosome<T> c1 = population[(int) Math.floor(random.nextDouble() * range)];
        Chromosome<T> c2 = population[(int) Math.floor(random.nextDouble() * range)];

        while ((c1 == c2) || c1.isEqual(c2))
            c2 = population[(int) Math.floor(random.nextDouble() * range)];

        if (c1.getFitness().doesPerformBetterThan(c2.getFitness()))
            return c1;
        else
            return c2;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void replaceChromosomeForSurvival(Chromosome<T>[] population,
                                                int rangeStart,
                                                int rangeEnd) {

        int range = rangeEnd - rangeStart;

        if (range > 1) {
            int firstCandidateIndex = rangeStart + (int) Math.floor(random.nextDouble() * range);
            Chromosome<T> c1 = population[firstCandidateIndex];

            int secondCandidateIndex = rangeStart + (int) Math.floor(random.nextDouble() * range);
            while (firstCandidateIndex == secondCandidateIndex) {
                secondCandidateIndex = rangeStart + (int) Math.floor(random.nextDouble() * range);
            }
            Chromosome<T> c2 = population[secondCandidateIndex];

            if (c1.getFitness().doesPerformBetterThan(c2.getFitness())) {
                c2 = population[rangeStart];
                population[rangeStart] = c1;
                population[firstCandidateIndex] = c2;
            } else {
                c1 = population[rangeStart];
                population[rangeStart] = c2;
                population[secondCandidateIndex] = c1;
            }
        }
    }
}
