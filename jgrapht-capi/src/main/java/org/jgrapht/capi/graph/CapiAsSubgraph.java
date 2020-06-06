package org.jgrapht.capi.graph;

import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.graph.AsSubgraph;

public class CapiAsSubgraph extends AsSubgraph<Integer, Integer>
		implements CapiGraphWrapper<Integer, Integer> {
	private static final long serialVersionUID = 1L;

	private Graph<Integer, Integer> wrappedGraph;

	public CapiAsSubgraph(Graph<Integer, Integer> g, Set<? extends Integer> vertexSubset, Set<? extends Integer> edgeSubset) {
		super(g, vertexSubset, edgeSubset);
		this.wrappedGraph = g;
	}

	@Override
	public Graph<Integer, Integer> getWrappedGraph() {
		return wrappedGraph;
	}

}
