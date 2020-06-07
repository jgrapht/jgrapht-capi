package org.jgrapht.capi.graph;

import java.util.function.Function;

import org.jgrapht.Graph;
import org.jgrapht.graph.AsWeightedGraph;

public class CapiAsWeightedGraph extends AsWeightedGraph<Integer, Integer>
		implements CapiGraphWrapper<Integer, Integer> {
	private static final long serialVersionUID = 1L;

	private Graph<Integer, Integer> wrappedGraph;

	public CapiAsWeightedGraph(Graph<Integer, Integer> g, Function<Integer, Double> weightFunction, boolean cacheWeights,
	        boolean writeWeightsThrough) {
		super(g, weightFunction, cacheWeights, writeWeightsThrough);
		this.wrappedGraph = g;
	}
	
	@Override
	public Graph<Integer, Integer> getWrappedGraph() {
		return wrappedGraph;
	}

}
