package org.heuros.core.ga;

public interface ISolutionCost {
	public boolean doesPerformBetterThan(ISolutionCost c);
	public double getDistance(ISolutionCost worst);
}
