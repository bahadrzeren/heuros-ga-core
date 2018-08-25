package org.heuros.core.ga.heuristic;

public interface Heuristic<I, O> {
	public O fetch(I i);
}
