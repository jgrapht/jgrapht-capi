package org.jgrapht.capi.graph;

import org.jgrapht.Graph;
import org.jgrapht.GraphType;
import org.jgrapht.capi.attributes.GraphAttributesStore;
import org.jgrapht.graph.GraphDelegator;

public class CapiAsUnweightedGraphWithAttributes<V, E> extends GraphDelegator<V, E>
		implements GraphWithAttributes<V, E>, CapiGraphWrapper<V, E> {

	private static final long serialVersionUID = 1L;
	private static final String EDGE_WEIGHT_IS_NOT_SUPPORTED = "Edge weight is not supported";

	private GraphWithAttributes<V, E> wrappedGraph;

	public CapiAsUnweightedGraphWithAttributes(GraphWithAttributes<V, E> g) {
		super(g);
		this.wrappedGraph = g;
	}

	@Override
	public Graph<V, E> getWrappedGraph() {
		return wrappedGraph;
	}

	@Override
	public GraphAttributesStore<V, E> getStore() {
		return wrappedGraph.getStore();
	}

	@Override
	public double getEdgeWeight(E e) {
		return Graph.DEFAULT_EDGE_WEIGHT;
	}

	@Override
	public void setEdgeWeight(E e, double weight) {
		throw new UnsupportedOperationException(EDGE_WEIGHT_IS_NOT_SUPPORTED);
	}

	@Override
	public GraphType getType() {
		return super.getType().asUnweighted();
	}

}