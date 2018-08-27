package org.heuros.core.ga.chromosome;

/**
 * Interface for the chromosome objects.
 * 
 */
public interface Chromosome<T> extends Cloneable {

    /**
     * Initializes the IChromosome object.
     * 
     * @param random Randomization class to be used.
     */
    public void initializeChromosome(int length);

    /**
     * Evaluates whether cand is equal to this chromosome object.
     * 
     * @param cand IChromosome object to compare with this one.
     * 
     * @return boolean true if this chromosome object equals to cand.
     */
    public boolean isEqual(Chromosome<T> cand);

    public void setFitness(double value);

    /**
     * Gives the current fitness value.
     * 
     * @return double fitness value of this IChromosome object.
     */
    public double getFitness();

    public String getInfo();
    public void setInfo(String value);

    /**
     * Gives length of the chromosome.
     * 
     * @return int number of genes in this IChromosome object.
     */
    public int getChromosomeLength();

    /**
     * Gives value of the gene indicated by the param.
     * 
     * @param index of the gene.
     * 
     * @return value of the gene.
     */
    public T getGeneValue(int index);

    /**
     * Sets the value of the gene indicated by index.
     * 
     * @param index of the gene which will be set.
     * 
     * @param value gene value to set
     */
    public void setGeneValue(int index, T value);

//    /**
//     * Gives all genes.
//     * 
//     * @return returns all genes.
//     */
//    public T[] getGenes();

    /**
     * Clones the IChromosome object.
     * 
     * @return IChromosome new cloned IChromosome object.
     * 
     * @throws CloneNotSupportedException is trown if IChromosome object doesnt
     * implements Cloneable interface.
     */
    public Object clone() throws CloneNotSupportedException;

//    /**
//     * String representation of this IChromosome object.
//     * 
//     * @return String information about this IChromosome object.
//     */
//    public String toString();
}
