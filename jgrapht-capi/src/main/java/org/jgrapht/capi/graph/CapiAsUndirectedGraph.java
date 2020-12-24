package org.jgrapht.capi.graph;

import org.jgrapht.Graph;
import org.jgrapht.graph.AsUndirectedGraph;

public class CapiAsUndirectedGraph<V, E> extends AsUndirectedGraph<V, E> implements CapiGraphWrapper<V, E> {

	private static final long serialVersionUID = 1L;

	private Graph<V, E> wrappedGraph;

	public CapiAsUndirectedGraph(Graph<V, E> g) {
		super(g);
		this.wrappedGraph = g;
	}

	@Override
	public Graph<V, E> getWrappedGraph() {
		return wrappedGraph;
	}

}
