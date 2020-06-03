package org.jgrapht.capi.graph;

import java.util.function.Predicate;

import org.jgrapht.Graph;
import org.jgrapht.graph.MaskSubgraph;

public class CapiAsMaskSubgraph extends MaskSubgraph<Integer, Integer>
		implements CapiGraphWrapper<Integer, Integer> {
	private static final long serialVersionUID = 1L;

	private Graph<Integer, Integer> wrappedGraph;

	public CapiAsMaskSubgraph(Graph<Integer, Integer> g, Predicate<Integer> vertexMask, Predicate<Integer> edgeMask) {
		super(g, vertexMask, edgeMask);
		this.wrappedGraph = g;
	}

	@Override
	public Graph<Integer, Integer> getWrappedGraph() {
		return wrappedGraph;
	}

}
