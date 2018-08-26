package org.heuros.core.ga;

import java.util.EventListener;

/**
 * Listener class which is triggered when a genetic iteration is completed.
 * 
 */
public interface GeneticIterationListener extends EventListener {

    /**
     * Metod which is called after each progress.
     * 
     * @param iteration iteration number.
     * @param elapsedTime elapsed time until this iteration.
     * @param info String representation of the best individual.
     */
    public void onProgress(int iteration,
                            double elapsedTime,
                            String info);

    /**
     * Metod which is called if an exception occured.
     * 
     * @param ex exception thrown.
     */
    public void onException(Exception  ex);
}
