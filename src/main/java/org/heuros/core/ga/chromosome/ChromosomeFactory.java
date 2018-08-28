package org.heuros.core.ga.chromosome;

/**
 * Factory class for the chromosome objects.
 * 
 * @author bahadrzeren
 *
 * @param <T> Type of the class which is used to represent one single gene.
 */
public interface ChromosomeFactory<T> {

	public ChromosomeFactory<T> setChromosomeLength(int value);
    public int getChromosomeLength();

    public Chromosome<T> createChromosome();
}
