package org.heuros.core.ga.chromosome;

/**
 * General purpose chromosome interface.
 * 
 * @author bahadrzeren
 *
 * @param <T> Type of the class which is used to represent one single gene.
 */
public interface Chromosome<T> extends Cloneable {

	/**
	 * Called from ChromosomeFactory and supposed to initialize gene array.
	 * 
	 * @param length Length of the chromosome.
	 */
    public void initializeChromosome(int length, T setSize);

    public boolean isEqual(Chromosome<T> cand);
    public void setFitness(double value);
    public double getFitness();

    /**
     * Getter for chromosome brief information. May include fitness and specific KPI values.
     * 
     * @return Returns information about chromosome's fitness and other quality metrics.
     */
    public String getInfo();
    public void setInfo(String value);

    public int getChromosomeLength();

    public T getGeneValue(int index);
    public void setGeneValue(int index, T value);

    public Object clone() throws CloneNotSupportedException;
}
