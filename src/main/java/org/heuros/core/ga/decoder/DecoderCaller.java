package org.heuros.core.ga.decoder;

import java.util.List;
import java.util.concurrent.Callable;

import org.heuros.core.ga.chromosome.Chromosome;

public class DecoderCaller<T, O> implements Callable<List<O>> {

	private Decoder<T, O> decoder = null;
	private Chromosome<T> chromosome = null;

	public DecoderCaller(Decoder<T, O> decoder,
								Chromosome<T> chromosome) {
		this.decoder = decoder;
		this.chromosome = chromosome;
	}

	@Override
	public List<O> call() throws Exception {
		return this.decoder.decode(this.chromosome);
	}

	public Chromosome<T> getChromosome() {
		return chromosome;
	}
}
