package org.heuros.core.ga;

import java.util.EventListener;

import org.heuros.core.ga.chromosome.Chromosome;

/**
 * Listener class which is triggered when a genetic iteration is completed with an improvement.
 * 
 * @author bahadrzeren
 *
 */
public interface GeneticIterationListener<T> extends EventListener {

    /**
     * Metod which is called after each progress.
     * 
     * @param iteration iteration number.
     * @param elapsedTime elapsed time until this iteration.
     * @param info String representation of the best individual.
     */
    public void onProgress(int iteration,
                            double elapsedTime,
                            Chromosome<T> best);
    /**
     * Metod which is called after each iteration.
     * 
     * @param iteration iteration number.
     * @param elapsedTime elapsed time until this iteration.
     * @param info String representation of the best individual.
     */
    public void onIterate(int iteration,
    						double elapsedTime,
    						Chromosome<T> best);


    /**
     * Metod which is called if an exception occurs.
     * 
     * @param ex Exception thrown.
     */
    public void onException(Exception  ex);
}
