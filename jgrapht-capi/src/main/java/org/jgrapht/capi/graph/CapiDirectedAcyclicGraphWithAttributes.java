package org.jgrapht.capi.graph;

import java.util.Iterator;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.capi.attributes.GraphAttributesStore;
import org.jgrapht.graph.DirectedAcyclicGraph;
import org.jgrapht.graph.GraphDelegator;

public class CapiDirectedAcyclicGraphWithAttributes<V, E> extends GraphDelegator<V, E>
		implements DirectedAcyclicGraphWithAttributes<V, E>, CapiGraphWrapper<V, E> {

	private static final long serialVersionUID = 1L;

	protected DirectedAcyclicGraph<V, E> delegate;
	protected GraphAttributesStore<V, E> store;

	public CapiDirectedAcyclicGraphWithAttributes(DirectedAcyclicGraph<V, E> graph) {
		super(graph);
		this.delegate = graph;
		this.store = new GraphAttributesStore<>();
	}

	@Override
	public GraphAttributesStore<V, E> getStore() {
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

	@Override
	public Set<V> getAncestors(V vertex) {
		return delegate.getAncestors(vertex);
	}

	@Override
	public Set<V> getDescendants(V vertex) {
		return delegate.getDescendants(vertex);
	}

	@Override
	public Iterator<V> iterator() {
		return delegate.iterator();
	}

}
