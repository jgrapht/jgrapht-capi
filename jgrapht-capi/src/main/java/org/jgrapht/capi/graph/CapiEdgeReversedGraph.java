package org.jgrapht.capi.graph;

import org.jgrapht.Graph;
import org.jgrapht.graph.EdgeReversedGraph;

public class CapiEdgeReversedGraph<V, E> extends EdgeReversedGraph<V, E> implements CapiGraphWrapper<V, E> {

	private static final long serialVersionUID = 1L;

	private Graph<V, E> wrappedGraph;

	public CapiEdgeReversedGraph(Graph<V, E> g) {
		super(g);
		this.wrappedGraph = g;
	}

	@Override
	public Graph<V, E> getWrappedGraph() {
		return wrappedGraph;
	}

}
