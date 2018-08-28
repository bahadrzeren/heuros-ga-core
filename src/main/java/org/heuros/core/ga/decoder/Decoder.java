package org.heuros.core.ga.decoder;

import java.util.List;

import org.heuros.core.ga.chromosome.Chromosome;

/**
 * Decodes chromosomes to a meaningful solution and sets fitness value of offsprings.
 * 
 * @author bahadrzeren
 *
 * @param <T> Type of the class which is used to represent one single gene.
 * @param <O> Type of the output instances.
 */
public interface Decoder<T, O> {

	public List<O> decode(Chromosome<T> chromosome);

}
