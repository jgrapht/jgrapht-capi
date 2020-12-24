package org.jgrapht.capi.graph;

import java.util.function.Predicate;

import org.jgrapht.Graph;
import org.jgrapht.graph.MaskSubgraph;

public class CapiAsMaskSubgraph<V,E> extends MaskSubgraph<V, E>
		implements CapiGraphWrapper<V, E> {
	private static final long serialVersionUID = 1L;

	private Graph<V, E> wrappedGraph;

	public CapiAsMaskSubgraph(Graph<V, E> g, Predicate<V> vertexMask, Predicate<E> edgeMask) {
		super(g, vertexMask, edgeMask);
		this.wrappedGraph = g;
	}

	@Override
	public Graph<V, E> getWrappedGraph() {
		return wrappedGraph;
	}

}
