package org.heuros.core.ga.chromosome;

/**
 * Interface for the chromosome objects.
 * 
 */
public interface IChromosome<T, M> extends Cloneable {

    /**
     * Initializes the IChromosome object.
     * 
     * @param random Randomization class to be used.
     */
    public void initializeChromosome();

    /**
     * Clones the IChromosome object.
     * 
     * @return IChromosome new cloned IChromosome object.
     * 
     * @throws CloneNotSupportedException is trown if IChromosome object doesnt
     * implements Cloneable interface.
     */
    public Object clone() throws CloneNotSupportedException;

    /**
     * Evaluates whether cand is equal to this chromosome object.
     * 
     * @param cand IChromosome object to compare with this one.
     * 
     * @return boolean true if this chromosome object equals to cand.
     */
    public boolean isEqual(IChromosome<T, M> cand);

    /**
     * Gives the current fitness value.
     * 
     * @return double fitness value of this IChromosome object.
     */
    public double getFitness();

    /**
     * Triggers the fitness calculation function of this IChromosome object.
     * 
     */
    public void calculateFitness();

    /**
     * Reverts fitness value to the previously saved one.
     * This is put for improver operator to be able to revert chromosome
     * fitness value fast.
     * 
     */
    public void revertFitness();

    /**
     * If this IChromosome object is the fittest individual in the population it
     * gives the result of the genetic optimization process.
     * 
     * @return Object generated solution to the optimization process by this 
     * IChromosome object.
     */
    public Object getSolution();

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

    /**
     * Gives all genes.
     * 
     * @return returns all genes.
     */
    public M getGenes();

    /**
     * String representation of this IChromosome object.
     * 
     * @return String information about this IChromosome object.
     */
    @Override
    public String toString();
}
