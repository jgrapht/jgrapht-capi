package org.jgrapht.capi.graph;

import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.graph.AsSubgraph;

public class CapiAsSubgraph<V,E> extends AsSubgraph<V, E>
		implements CapiGraphWrapper<V, E> {
	private static final long serialVersionUID = 1L;

	private Graph<V, E> wrappedGraph;

	public CapiAsSubgraph(Graph<V, E> g, Set<? extends V> vertexSubset, Set<? extends E> edgeSubset) {
		super(g, vertexSubset, edgeSubset);
		this.wrappedGraph = g;
	}

	@Override
	public Graph<V, E> getWrappedGraph() {
		return wrappedGraph;
	}

}
