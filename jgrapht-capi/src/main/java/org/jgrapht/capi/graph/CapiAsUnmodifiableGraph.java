package org.jgrapht.capi.graph;

import org.jgrapht.Graph;
import org.jgrapht.graph.AsUnmodifiableGraph;

public class CapiAsUnmodifiableGraph<V, E> extends AsUnmodifiableGraph<V, E> implements CapiGraphWrapper<V, E> {

	private static final long serialVersionUID = 1L;

	private Graph<V, E> wrappedGraph;

	public CapiAsUnmodifiableGraph(Graph<V, E> g) {
		super(g);
		this.wrappedGraph = g;
	}

	@Override
	public Graph<V, E> getWrappedGraph() {
		return wrappedGraph;
	}

}
