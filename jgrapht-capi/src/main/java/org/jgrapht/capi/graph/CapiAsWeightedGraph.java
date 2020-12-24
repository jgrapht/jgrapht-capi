package org.jgrapht.capi.graph;

import java.util.function.Function;

import org.jgrapht.Graph;
import org.jgrapht.graph.AsWeightedGraph;

public class CapiAsWeightedGraph<V, E> extends AsWeightedGraph<V, E> implements CapiGraphWrapper<V, E> {
	private static final long serialVersionUID = 1L;

	private Graph<V, E> wrappedGraph;
	private boolean superHasWeightFunction;
	private boolean superHasCacheWeights;

	public CapiAsWeightedGraph(Graph<V, E> g, Function<E, Double> weightFunction, boolean cacheWeights,
			boolean writeWeightsThrough) {
		super(g, weightFunction, cacheWeights, writeWeightsThrough);
		this.superHasCacheWeights = cacheWeights;
		this.superHasWeightFunction = weightFunction != null;
		this.wrappedGraph = g;
	}

	@Override
	public void setEdgeWeight(E e, double weight) {
		if (superHasWeightFunction && !superHasCacheWeights) {
			throw new UnsupportedOperationException(
					"Cannot set an edge weight when a weight function is used and caching is disabled");
		}
		super.setEdgeWeight(e, weight);
	}

	@Override
	public Graph<V, E> getWrappedGraph() {
		return wrappedGraph;
	}

}
