package org.jgrapht.capi.graph;

import java.util.Collection;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.GraphType;
import org.jgrapht.capi.attributes.GraphAttributesStore;
import org.jgrapht.graph.GraphDelegator;

public class CapiAsUnmodifiableGraphWithAttributes<V, E> extends GraphDelegator<V, E>
		implements GraphWithAttributes<V, E>, CapiGraphWrapper<V, E> {

	private static final long serialVersionUID = 1L;
	private static final String UNMODIFIABLE = "this graph is unmodifiable";

	private GraphWithAttributes<V, E> wrappedGraph;

	public CapiAsUnmodifiableGraphWithAttributes(GraphWithAttributes<V, E> g) {
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
	public E addEdge(V sourceVertex, V targetVertex) {
		throw new UnsupportedOperationException(UNMODIFIABLE);
	}

	@Override
	public boolean addEdge(V sourceVertex, V targetVertex, E e) {
		throw new UnsupportedOperationException(UNMODIFIABLE);
	}

	@Override
	public V addVertex() {
		throw new UnsupportedOperationException(UNMODIFIABLE);
	}

	@Override
	public boolean addVertex(V v) {
		throw new UnsupportedOperationException(UNMODIFIABLE);
	}

	@Override
	public boolean removeAllEdges(Collection<? extends E> edges) {
		throw new UnsupportedOperationException(UNMODIFIABLE);
	}

	@Override
	public Set<E> removeAllEdges(V sourceVertex, V targetVertex) {
		throw new UnsupportedOperationException(UNMODIFIABLE);
	}

	@Override
	public boolean removeAllVertices(Collection<? extends V> vertices) {
		throw new UnsupportedOperationException(UNMODIFIABLE);
	}

	@Override
	public boolean removeEdge(E e) {
		throw new UnsupportedOperationException(UNMODIFIABLE);
	}

	@Override
	public E removeEdge(V sourceVertex, V targetVertex) {
		throw new UnsupportedOperationException(UNMODIFIABLE);
	}

	@Override
	public boolean removeVertex(V v) {
		throw new UnsupportedOperationException(UNMODIFIABLE);
	}

	@Override
	public GraphType getType() {
		return super.getType().asUnmodifiable();
	}

}