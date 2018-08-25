package org.heuros.core.ga.decoder;

import java.util.List;

import org.heuros.core.ga.chromosome.Chromosome;

public interface Decoder<T, O> {

	public List<O> decode(Chromosome<T> chromosome);

}
