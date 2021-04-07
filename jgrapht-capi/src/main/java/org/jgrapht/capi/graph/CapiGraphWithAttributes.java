package org.jgrapht.capi.graph;

import org.jgrapht.Graph;
import org.jgrapht.capi.attributes.GraphStringAttributeStore;
import org.jgrapht.graph.GraphDelegator;

public class CapiGraphWithAttributes<V, E> extends GraphDelegator<V, E> implements GraphWithAttributes<V, E>, CapiGraphWrapper<V, E> {

	private static final long serialVersionUID = 1L;

	protected Graph<V, E> delegate;
	protected GraphStringAttributeStore<V, E> store;

	public CapiGraphWithAttributes(Graph<V, E> graph) {
		super(graph);
		this.delegate = graph;
		this.store = new GraphStringAttributeStore<>();
	}

	@Override
	public GraphStringAttributeStore<V, E> getStore() {
		return store;
	}

	@Override
	public Graph<V, E> getWrappedGraph() {
		return delegate;
	}

	@Override
	public E removeEdge(V sourceVertex, V targetVertex) {
		E e = super.getEdge(sourceVertex, targetVertex);
		if (e != null) {
			if (super.removeEdge(e)) {
				clearEdgeAttributes(e);
			}
		}
		return e;
	}

	@Override
	public boolean removeEdge(E e) {
		boolean modified = super.removeEdge(e);
		if (modified) {
			clearEdgeAttributes(e);
		}
		return modified;
	}

	@Override
	public boolean removeVertex(V v) {
		boolean modified = super.removeVertex(v);
		if (modified) {
			clearVertexAttributes(v);
		}
		return modified;
	}

}
