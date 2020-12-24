package org.jgrapht.capi.graph;

import org.jgrapht.Graph;
import org.jgrapht.graph.AsUnweightedGraph;

public class CapiAsUnweightedGraph<V, E> extends AsUnweightedGraph<V, E>
		implements CapiGraphWrapper<V, E> {

	private static final long serialVersionUID = 1L;

	private Graph<V, E> wrappedGraph;

	public CapiAsUnweightedGraph(Graph<V, E> g) {
		super(g);
		this.wrappedGraph = g;
	}

	@Override
	public Graph<V, E> getWrappedGraph() {
		return wrappedGraph;
	}

}
