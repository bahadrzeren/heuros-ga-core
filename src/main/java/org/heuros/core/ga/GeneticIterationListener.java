package org.heuros.core.ga;

import java.util.EventListener;

/**
 * Listener class which is triggered when a genetic iteration is completed.
 * 
 */
public interface GeneticIterationListener<T, M> extends EventListener {

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
     * @param exceptionMessage message of the exception.
     */
    public void onException(String exceptionMessage);
}
