package org.heuros.core.ga;

import org.heuros.core.ga.chromosome.IChromosomeFactory;
import org.heuros.core.ga.chromosome.IChromosome;
import org.heuros.core.ga.selection.Selector;
import org.heuros.core.ga.crossover.ICrossoverOperator;
import org.heuros.core.ga.mutation.Mutator;

/**
 * The Main genetic optimizer class.
 * 
 * T is the type of the class which is used to represent one single gene.
 * 
 * Runs all functions according to chosen parameters and implementation classes.
 *
 */
public class GeneticOptimizer<T, M> {

    private IChromosomeFactory<T, M> chromosomeFactory = null;
    private Selector<T, M> selector = null;
    private ICrossoverOperator<T, M> crossoverOperator = null;
    private Mutator<T, M> mutator = null;

    private long maxElapsedTimeInNanoSecs = 60000000000l;
    private int maxNumOfIterations = 500;
    private int maxNumOfIterationsWOProgress = 200;
    private int populationSize = 100;
    private int minNumOfChildren = 20;
    private int numOfEliteChromosomes = 4;
    private boolean allowDublicateChromosomes = false;

    private IChromosome<T, M> best = null;

    private IChromosome<T, M> population[] = null;
    private IChromosome<T, M> children[] = null;

    private float mutationRate = 0.01f;

    private GeneticIterationListener<T, M> geneticIterationListener = null;

    private boolean initializePopulation() {

        if (chromosomeFactory == null)
            return false;

        int i = 0;

        IChromosome<T, M> chromosome = null;
        boolean addable = true;

        while (i < populationSize) {
            chromosome = chromosomeFactory.createChromosome();
            chromosome.initializeChromosome();

            addable = true;

            if (!allowDublicateChromosomes) {
                for (int j = i - 1; j >= 0; j--)
                    if (chromosome.isEqual(population[j]))
                        addable = false;
            }

            if (addable) {
                population[i] = chromosome;
                i++;
            }
        }
        return true;
    }

    private void orderPopulation() {
        IChromosome<T, M> chromosome = null;
        IChromosome<T, M> iChromosome = null;

        for (int i = 0; i < populationSize - 1; i++) {
            for (int j = populationSize - 1; j > i; j--) {
                chromosome = population[i];
                iChromosome = population[j];

                if (chromosome.getFitness() > iChromosome.getFitness()) {
                    population[i] = iChromosome;
                    population[j] = chromosome;
                }
            }
        }
    }

    private void generateChildren(int iteration, int numOfIterationsWOProgress) throws CloneNotSupportedException,
                                                                                    InterruptedException {

        IChromosome<T, M> mother = null;
        IChromosome<T, M> father = null;

        while (numOfChildrenGeneratedInLastRound < minNumOfChildren) {

            mother = selector.selectChromosomeForReproduction(population, populationSize);
            father = selector.selectChromosomeForReproduction(population, populationSize);

            while (mother == father) {
                father = selector.selectChromosomeForReproduction(population, populationSize);
            }

            numOfChildrenGeneratedInLastRound = crossoverOperator.crossover(children,
                                                                            numOfChildrenGeneratedInLastRound,
                                                                            mother,
                                                                            father,
                                                                            population[populationSize - 1].getFitness());
        }
    }

    private void mutateChildren(int iteration, int numOfIterationsWOProgress) throws CloneNotSupportedException,
                                                                                    	InterruptedException {

        IChromosome<T, M> child = null;
        IChromosome<T, M> mutatedChild = null;

        /*
         * Half of the children will be generated using crossover operator.
         * Rest of them will be generated using mutation operator.
         * 
         */
        int numOfMutations = 0;

        while (numOfMutations < minNumOfChildren) {

            int ndxChild = 0;
            int lastNdxChild = numOfChildrenGeneratedInLastRound;

            while (ndxChild < lastNdxChild) {

                child = children[ndxChild];

                mutatedChild = null;

                if (mutationRate > 0.0) {
                    mutatedChild = mutator.mutate(child, iteration, mutationRate);
                    if (mutatedChild != null) {
                        children[lastNdxChild + numOfMutations] = mutatedChild;
                        numOfMutations++;
                        numOfChildrenGeneratedInLastRound++;
                    }
                }

                ndxChild++;
            }
        }
    }

    private void replacePopulation() {

        int extendedPopSize = populationSize;

        IChromosome<T, M> child = null;
        boolean canBeAdded = true;

        for (int i = 0; i < numOfChildrenGeneratedInLastRound; i++) {
            child = children[i];

            canBeAdded = true;

            if (!allowDublicateChromosomes) {
                for (int j = 0; j < extendedPopSize; j++) {
                    if (child.isEqual(population[j])) {
                        canBeAdded = false;
                        break;
                    }
                }
            }

            if (canBeAdded) {
                population[extendedPopSize] = child;
                extendedPopSize++;
            }
        }

        /*
         * Order population and children chromosome objects list ol.
         * 
         */

        IChromosome<T, M> chromosome = null;
        IChromosome<T, M> iChromosome = null;

        for (int i = 0; i < extendedPopSize - 1; i++) {
            for (int j = extendedPopSize - 1; j > i; j--) {
                chromosome = population[i];
                iChromosome = population[j];

                if (chromosome.getFitness() > iChromosome.getFitness()) {
                    population[i] = iChromosome;
                    population[j] = chromosome;
                }
            }
        }

        /*
         * Replace.
         * 
         */
        for (int i = numOfEliteChromosomes; i < populationSize; i++) {
            selector.replaceChromosomeForSurvival(population, i, extendedPopSize);
        }

//        ArrayList<IChromosome<T, M>> ol = new ArrayList<IChromosome<T, M>>();
//        IChromosome<T, M> child = null;
//        boolean canBeAdded = true;
//
//        for (int i = 0; i < numOfChildrenGeneratedInLastRound; i++) {
//            child = children[i];
//
//            canBeAdded = true;
//
//            if (!allowDublicateChromosomes) {
//                for (int j = 0; j < populationSize; j++) {
//                    if (child.isEqual(population[j])) {
//                        canBeAdded = false;
//                        break;
//                    }
//                }
//
//                for (int j = 0; j < ol.size(); j++) {
//                    if (child.isEqual(ol.get(j))) {
//                        canBeAdded = false;
//                        break;
//                    }
//                }
//            }
//
//            if (canBeAdded) {
//                ol.add(child);
//            }
//        }
//
//        for (int i = 0; i < populationSize; i++) {
//            ol.add(population[i]);
//        }
//
//        /*
//         * Order population and children chromosome objects list ol.
//         * 
//         */
//        int olSize = ol.size();
//
//        IChromosome<T, M> chromosome = null;
//        IChromosome<T, M> iChromosome = null;
//
//        for (int i = 0; i < olSize - 1; i++) {
//            for (int j = olSize - 1; j > i; j--) {
//                chromosome = ol.get(i);
//                iChromosome = ol.get(j);
//
//                if (chromosome.getFitness() > iChromosome.getFitness()) {
//                    ol.set(i, iChromosome);
//                    ol.set(j, chromosome);
//                }
//            }
//        }
//
//        /*
//         * Keep first chromosomes as elite ones.
//         * 
//         */
//        for (int i = 0; i < numOfEliteChromosomes; i++) {
//            population[i] = ol.get(0);
//            ol.remove(0);
//        }
//
//        int selectedChromosomeIndex = 0;
//        for (int i = numOfEliteChromosomes; i < populationSize; i++) {
//            selectedChromosomeIndex = selector.selectChromosomeIndexForSurvival(ol, random);
//            population[i] = ol.get(selectedChromosomeIndex);
//            ol.remove(selectedChromosomeIndex);
//        }
//
//        ol.clear();
//
//        /*
//         * Add migrant.
//         */
//        int popIndex = populationSize - 1;
//        while (!migrantArrays.isEmpty()) {
//System.out.println("notEmpty");
//            MigrantChromosome<M>[] candMigrants = migrantArrays.poll();
//            for (int i = 0; i < candMigrants.length; i++) {
//                IChromosome<T, M> migrantC = chromosomeFactory.createChromosome();
//                migrantC.initializeChromosome(candMigrants[i].getChromosomeData());
//
//                canBeAdded = true;
//
//                if (!allowDublicateChromosomes) {
//                    for (int j = 0; j < ol.size(); j++) {
//                        if (migrantC.isEqual(ol.get(j))) {
//                            canBeAdded = false;
//                            break;
//                        }
//                    }
//                }
//
//                if (canBeAdded) {
//                    population[popIndex--] = migrantC;
//                }
//            }
//        }
    }

    /**
     * Gives the best individual in the current population.
     * 
     * @return IChromosome the fittest individual in the current population.
     */
    private IChromosome<T, M> getFittestIndividual() {
        return population[0];
    }

    /**
     * Gives the best individual of the genetic optimization process.
     * 
     * @return IChromosome the fittest individual of the genetic optimization 
     * process.
     */
    public IChromosome<T, M> getBest() {
        return best;
    }

    private int numOfChildrenGeneratedInLastRound = 0;

    @SuppressWarnings("unchecked")
	protected void doMinimize() {

        population = new IChromosome[populationSize + 2 * minNumOfChildren];
        children = new IChromosome[2 * minNumOfChildren];

        int numOfIterationsWOProgress = 0;

        long optStartTime = System.nanoTime();

        try {
            if (initializePopulation()) {

                orderPopulation();

                best = getFittestIndividual();

                geneticIterationListener.onProgress(0, (System.nanoTime() - optStartTime) / 1000000000.0, String.valueOf(best.getFitness()));

                IChromosome<T, M> ch = null;

long nano1 = 0l;
long nano2 = 0l;
long nanoGenTot = 0l;
long nanoMutTot = 0l;
long nanoRepTot = 0l;

                for (int i = 1; i <= maxNumOfIterations; i++) {

                	numOfChildrenGeneratedInLastRound = 0;

nano1 = System.nanoTime();

                    generateChildren(i, numOfIterationsWOProgress);

nano2 = System.nanoTime();
nanoGenTot += nano2 - nano1;
nano1 = System.nanoTime();

					mutateChildren(i, numOfIterationsWOProgress);

nano2 = System.nanoTime();
nanoMutTot += nano2 - nano1;
nano1 = System.nanoTime();

                    replacePopulation();

nano2 = System.nanoTime();
nanoRepTot += nano2 - nano1;

                    ch = getFittestIndividual();

                    if (best.getFitness() > ch.getFitness()) {
                        best = (IChromosome<T, M>) ch.clone();
                        numOfIterationsWOProgress = 0;
                    } else
                        numOfIterationsWOProgress++;

                    if ((numOfIterationsWOProgress >= maxNumOfIterationsWOProgress)
                            || ((System.nanoTime() - optStartTime) >= maxElapsedTimeInNanoSecs))
                        break;

nano2 = System.nanoTime();
                }

System.out.println("gen-" + nanoGenTot / maxNumOfIterations +
					", mut-" + nanoMutTot / maxNumOfIterations +
                    ", rep-" + nanoRepTot / maxNumOfIterations);

                geneticIterationListener.onProgress(maxNumOfIterations, (System.nanoTime() - optStartTime) / 1000000000.0, String.valueOf(best.getFitness()));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            geneticIterationListener.onException(ex.getMessage());
        }
    }

    /**
     * @return the numOfEliteChromosomes
     */
    public int getNumOfEliteChromosomes() {
        return numOfEliteChromosomes;
    }

    /**
     * @param numOfEliteChromosomes the numOfEliteChromosomes to set
     */
    public void setNumOfEliteChromosomes(int numOfEliteChromosomes) {
        this.numOfEliteChromosomes = numOfEliteChromosomes;
    }

    /**
     * @return the allowDublicateChromosomes
     */
    public boolean isAllowDublicateChromosomes() {
        return allowDublicateChromosomes;
    }

    /**
     * @param allowDublicateChromosomes the allowDublicateChromosomes to set
     */
    public void setAllowDublicateChromosomes(boolean allowDublicateChromosomes) {
        this.allowDublicateChromosomes = allowDublicateChromosomes;
    }

    /**
     * @return the crossoverOperator
     */
    public ICrossoverOperator<T, M> getCrossoverOperator() {
        return crossoverOperator;
    }

    /**
     * @param crossoverOperator the crossoverOperator to set
     */
    public void setCrossoverOperator(ICrossoverOperator<T, M> crossoverOperator) {
        this.crossoverOperator = crossoverOperator;
    }

    /**
     * @return the mutator
     */
    public Mutator<T, M> getMutator() {
        return mutator;
    }

    /**
     * @param mutator the mutator to set
     */
    public void setMutator(Mutator<T, M> mutator) {
        this.mutator = mutator;
    }

    /**
     * @return the chromosomeFactory
     */
    public IChromosomeFactory<T, M> getChromosomeFactory() {
        return chromosomeFactory;
    }

    /**
     * @param chromosomeFactory the chromosomeFactory to set
     */
    public void setChromosomeFactory(IChromosomeFactory<T, M> chromosomeFactory) {
        this.chromosomeFactory = chromosomeFactory;
    }

    /**
     * @return the selector
     */
    public Selector<T, M> getSelector() {
        return selector;
    }

    /**
     * @param selector the selector to set
     */
    public void setSelector(Selector<T, M> selector) {
        this.selector = selector;
    }

    /**
     * @return the maxNumOfIterations
     */
    public int getMaxNumOfIterations() {
        return maxNumOfIterations;
    }

    /**
     * @param maxNumOfIterations the maxNumOfIterations to set
     */
    public void setMaxNumOfIterations(int maxNumOfIterations) {
        this.maxNumOfIterations = maxNumOfIterations;
    }

    /**
     * @return the maxNumOfUselessIterations
     */
    public int getMaxNumOfIterationsWOProgress() {
        return maxNumOfIterationsWOProgress;
    }

    /**
     * @param maxNumOfIterationsWOProgress the maxNumOfIterationsWOProgress to set
     */
    public void setMaxNumOfIterationsWOProgress(int maxNumOfIterationsWOProgress) {
        this.maxNumOfIterationsWOProgress = maxNumOfIterationsWOProgress;
    }

    /**
     * @return the populationSize
     */
    public int getPopulationSize() {
        return populationSize;
    }

    /**
     * @param populationSize the populationSize to set
     */
    public void setPopulationSize(int populationSize) {
        this.populationSize = populationSize;
    }

    /**
     * @return the minNumOfChildren
     */
    public int getMinNumOfChildren() {
        return minNumOfChildren;
    }

    /**
     * @param minNumOfChildren the minNumOfChildren to set
     */
    public void setMinNumOfChildren(int minNumOfChildren) {
        this.minNumOfChildren = minNumOfChildren;
    }

    /**
     * @return the mutationRate
     */
    public float getMutationRate() {
        return mutationRate;
    }

    /**
     * @param mutationRate the mutationRate to set
     */
    public void setMutationRate(float mutationRate) {
        this.mutationRate = mutationRate;
    }

    /**
     * @return the geneticIterationListener
     */
    public GeneticIterationListener<T, M> getGeneticIterationListener() {
        return geneticIterationListener;
    }

    /**
     * @param geneticIterationListener the geneticIterationListener to set
     */
    public void setGeneticIterationListener(GeneticIterationListener<T, M> geneticIterationListener) {
        this.geneticIterationListener = geneticIterationListener;
    }

    /**
     * @return the maxElapsedTimeInNanoSecs
     */
    public long getMaxElapsedTimeInNanoSecs() {
        return maxElapsedTimeInNanoSecs;
    }

    /**
     * @param maxElapsedTimeInNanoSecs the maxElapsedTimeInNanoSecs to set
     */
    public void setMaxElapsedTimeInNanoSecs(long maxElapsedTimeInNanoSecs) {
        this.maxElapsedTimeInNanoSecs = maxElapsedTimeInNanoSecs;
    }

}
