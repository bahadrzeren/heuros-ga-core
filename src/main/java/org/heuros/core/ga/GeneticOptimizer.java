package org.heuros.core.ga;

import org.heuros.core.ga.chromosome.ChromosomeFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;
import org.heuros.core.ga.chromosome.Chromosome;
import org.heuros.core.ga.selection.Selector;
import org.heuros.core.ga.crossover.Crossover;
import org.heuros.core.ga.decoder.Decoder;
import org.heuros.core.ga.decoder.DecoderCaller;
import org.heuros.core.ga.mutation.Mutator;

/**
 * Main genetic optimizer class.
 * Runs all functions according to chosen parameters and implementation classes.
 * 
 * @author bahadrzeren
 *
 * @param <T> Type of the class which is used to represent one single gene.
 * @param <O> Type of the output data list.
 */
public class GeneticOptimizer<T, O> {

	private static Logger logger = Logger.getLogger(GeneticOptimizer.class);

	private ChromosomeFactory<T> chromosomeFactory = null;
    private Selector<T> selector = null;
    private Crossover<T> crossoverOperator = null;
    private Mutator<T> mutator = null;
    private Decoder<T, O> decoder = null;

    private long maxElapsedTimeInNanoSecs = 60000000000l;
    private int maxNumOfIterations = 500;
    private int maxNumOfIterationsWOProgress = 200;
    private int populationSize = 100;
    private int minNumOfChildren = 20;
    private int numOfEliteChromosomes = 4;
    private boolean allowDublicateChromosomes = false;

    private Chromosome<T> best = null;

    private Chromosome<T> population[] = null;
    private Chromosome<T> children[] = null;

    private float mutationRate = 0.01f;

    private boolean runParallel = false;
	private ExecutorService executorService = null;

    private GeneticIterationListener<T> geneticIterationListener = null;

    private boolean initializePopulation() {

        if (chromosomeFactory == null)
            return false;

        int i = 0;

        Chromosome<T> chromosome = null;
        boolean addable = true;

        List<Future<List<O>>> futures = null;
        if (runParallel)
        	futures = new ArrayList<Future<List<O>>>();

        while (i < populationSize) {
            chromosome = chromosomeFactory.createChromosome();

            addable = true;

            if (!allowDublicateChromosomes) {
                for (int j = i - 1; j >= 0; j--)
                    if (chromosome.isEqual(population[j]))
                        addable = false;
            }

            if (addable) {
                population[i] = chromosome;
                if (runParallel)
                	futures.add(this.executorService.submit(new DecoderCaller<T, O>(this.decoder, chromosome)));
                else
                	this.decoder.decode(chromosome);
                i++;
            }
        }

        if (runParallel) {
        	for (int j = 0; j < futures.size(); j++) {
        		try {
					futures.get(j).get();
				} catch (InterruptedException e) {
					logger.error(e);
				} catch (ExecutionException e) {
					logger.error(e);
				}
        	}
        }

        return true;
    }

    private void orderPopulation() {
        Chromosome<T> chromosome = null;
        Chromosome<T> iChromosome = null;

        for (int i = 0; i < populationSize - 1; i++) {
            for (int j = populationSize - 1; j > i; j--) {
                chromosome = population[i];
                iChromosome = population[j];

                if (iChromosome.getFitness().doesPerformBetterThan(chromosome.getFitness())) {
                    population[i] = iChromosome;
                    population[j] = chromosome;
                }
            }
        }
    }

    private void generateChildren(int iteration, int numOfIterationsWOProgress) throws CloneNotSupportedException,
                                                                                    InterruptedException {

        Chromosome<T> mother = null;
        Chromosome<T> father = null;

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

        Chromosome<T> child = null;
        Chromosome<T> mutatedChild = null;

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

    private void decode() {
        List<Future<List<O>>> futures = null;
        if (runParallel)
        	futures = new ArrayList<Future<List<O>>>();

        for (int i = 0; i < numOfChildrenGeneratedInLastRound; i++) {
            if (runParallel)
            	futures.add(this.executorService.submit(new DecoderCaller<T, O>(this.decoder, children[i])));
            else
        		this.decoder.decode(children[i]);
    	}

        if (runParallel) {
        	for (int j = 0; j < futures.size(); j++) {
        		try {
					futures.get(j).get();
				} catch (InterruptedException e) {
					logger.error(e);
				} catch (ExecutionException e) {
					logger.error(e);
				}
        	}
        }
    }

    private void replacePopulation() {

        int extendedPopSize = populationSize;

        Chromosome<T> child = null;
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

        Chromosome<T> chromosome = null;
        Chromosome<T> iChromosome = null;

        for (int i = 0; i < extendedPopSize - 1; i++) {
            for (int j = extendedPopSize - 1; j > i; j--) {
                chromosome = population[i];
                iChromosome = population[j];

                if (iChromosome.getFitness().doesPerformBetterThan(chromosome.getFitness())) {
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
    }

    /**
     * Gives the best individual in the current population.
     * 
     * @return IChromosome the fittest individual in the current population.
     */
    private Chromosome<T> getFittestIndividual() {
        return population[0];
    }

    /**
     * Gives the best individual of the genetic optimization process.
     * 
     * @return IChromosome the fittest individual of the genetic optimization 
     * process.
     */
    public Chromosome<T> getBest() {
        return best;
    }

    private int numOfChildrenGeneratedInLastRound = 0;

    @SuppressWarnings("unchecked")
	protected void doMinimize() {

        population = new Chromosome[populationSize + 2 * minNumOfChildren];
        children = new Chromosome[2 * minNumOfChildren];

        executorService = Executors.newFixedThreadPool(minNumOfChildren * 2);

        int numOfIterationsWOProgress = 0;

        long optStartTime = System.nanoTime();

        try {
            if (initializePopulation()) {

                this.orderPopulation();

                best = this.getFittestIndividual();

                this.geneticIterationListener.onIterate(0, (System.nanoTime() - optStartTime) / 1000000000.0, best);

                Chromosome<T> ch = null;

long nano1 = 0l;
long nano2 = 0l;
long nanoGenTot = 0l;
long nanoMutTot = 0l;
long nanoFitTot = 0l;
long nanoRepTot = 0l;

                for (int i = 1; i <= this.maxNumOfIterations; i++) {

                	this.numOfChildrenGeneratedInLastRound = 0;

nano1 = System.nanoTime();

					this.generateChildren(i, numOfIterationsWOProgress);

//System.out.println("Crossover phase is completed!");

nano2 = System.nanoTime();
nanoGenTot += nano2 - nano1;
nano1 = System.nanoTime();

					this.mutateChildren(i, numOfIterationsWOProgress);

//System.out.println("Mutation phase is completed!");

nano2 = System.nanoTime();
nanoMutTot += nano2 - nano1;
nano1 = System.nanoTime();

					this.decode();

//System.out.println("Decoding phase is completed!");

nano2 = System.nanoTime();
nanoFitTot += nano2 - nano1;
nano1 = System.nanoTime();

					this.replacePopulation();

//System.out.println("Population replace is completed!");

nano2 = System.nanoTime();
nanoRepTot += nano2 - nano1;

					ch = getFittestIndividual();

                    if (ch.getFitness().doesPerformBetterThan(this.best.getFitness())) {
                    	this.best = (Chromosome<T>) ch.clone();
                    	this.geneticIterationListener.onProgress(i, (System.nanoTime() - optStartTime) / 1000000000.0, this.best);
                    	numOfIterationsWOProgress = 0;
                    } else
                    	numOfIterationsWOProgress++;

                    this.geneticIterationListener.onIterate(i, (System.nanoTime() - optStartTime) / 1000000000.0, this.best);

                    if ((numOfIterationsWOProgress >= this.maxNumOfIterationsWOProgress)
                            || ((System.nanoTime() - optStartTime) >= this.maxElapsedTimeInNanoSecs))
                        break;
                }

System.out.println("gen-" + nanoGenTot / this.maxNumOfIterations +
					", mut-" + nanoMutTot / this.maxNumOfIterations +
					", fit-" + nanoFitTot / this.maxNumOfIterations +
                    ", rep-" + nanoRepTot / this.maxNumOfIterations);

				this.geneticIterationListener.onIterate(this.maxNumOfIterations, (System.nanoTime() - optStartTime) / 1000000000.0, this.best);
            }
        } catch (Exception ex) {
            geneticIterationListener.onException(ex);
        }
        executorService.shutdown();
    }

	public ChromosomeFactory<T> getChromosomeFactory() {
		return chromosomeFactory;
	}

	public GeneticOptimizer<T, O> setChromosomeFactory(ChromosomeFactory<T> chromosomeFactory) {
		this.chromosomeFactory = chromosomeFactory;
		return this;
	}

	public Selector<T> getSelector() {
		return selector;
	}

	public GeneticOptimizer<T, O> setSelector(Selector<T> selector) {
		this.selector = selector;
		return this;
	}

	public Crossover<T> getCrossoverOperator() {
		return crossoverOperator;
	}

	public GeneticOptimizer<T, O> setCrossoverOperator(Crossover<T> crossoverOperator) {
		this.crossoverOperator = crossoverOperator;
		return this;
	}

	public Mutator<T> getMutator() {
		return mutator;
	}

	public GeneticOptimizer<T, O> setMutator(Mutator<T> mutator) {
		this.mutator = mutator;
		return this;
	}

	public Decoder<T, O> getDecoder() {
		return decoder;
	}

	public GeneticOptimizer<T, O> setDecoder(Decoder<T, O> decoder) {
		this.decoder = decoder;
		return this;
	}

	public long getMaxElapsedTimeInNanoSecs() {
		return maxElapsedTimeInNanoSecs;
	}

	public GeneticOptimizer<T, O> setMaxElapsedTimeInNanoSecs(long maxElapsedTimeInNanoSecs) {
		this.maxElapsedTimeInNanoSecs = maxElapsedTimeInNanoSecs;
		return this;
	}

	public int getMaxNumOfIterations() {
		return maxNumOfIterations;
	}

	public GeneticOptimizer<T, O> setMaxNumOfIterations(int maxNumOfIterations) {
		this.maxNumOfIterations = maxNumOfIterations;
		return this;
	}

	public int getMaxNumOfIterationsWOProgress() {
		return maxNumOfIterationsWOProgress;
	}

	public GeneticOptimizer<T, O> setMaxNumOfIterationsWOProgress(int maxNumOfIterationsWOProgress) {
		this.maxNumOfIterationsWOProgress = maxNumOfIterationsWOProgress;
		return this;
	}

	public int getPopulationSize() {
		return populationSize;
	}

	public GeneticOptimizer<T, O> setPopulationSize(int populationSize) {
		this.populationSize = populationSize;
		return this;
	}

	public int getMinNumOfChildren() {
		return minNumOfChildren;
	}

	public GeneticOptimizer<T, O> setMinNumOfChildren(int minNumOfChildren) {
		this.minNumOfChildren = minNumOfChildren;
		return this;
	}

	public int getNumOfEliteChromosomes() {
		return numOfEliteChromosomes;
	}

	public GeneticOptimizer<T, O> setNumOfEliteChromosomes(int numOfEliteChromosomes) {
		this.numOfEliteChromosomes = numOfEliteChromosomes;
		return this;
	}

	public boolean isAllowDublicateChromosomes() {
		return allowDublicateChromosomes;
	}

	public GeneticOptimizer<T, O> setAllowDublicateChromosomes(boolean allowDublicateChromosomes) {
		this.allowDublicateChromosomes = allowDublicateChromosomes;
		return this;
	}

	public float getMutationRate() {
		return mutationRate;
	}

	public GeneticOptimizer<T, O> setMutationRate(float mutationRate) {
		this.mutationRate = mutationRate;
		return this;
	}

	public boolean isRunParallel() {
		return runParallel;
	}

	public GeneticOptimizer<T, O> setRunParallel(boolean runParallel) {
		this.runParallel = runParallel;
		return this;
	}

	public GeneticIterationListener<T> getGeneticIterationListener() {
		return geneticIterationListener;
	}

	public GeneticOptimizer<T, O> setGeneticIterationListener(GeneticIterationListener<T> geneticIterationListener) {
		this.geneticIterationListener = geneticIterationListener;
		return this;
	}
}
