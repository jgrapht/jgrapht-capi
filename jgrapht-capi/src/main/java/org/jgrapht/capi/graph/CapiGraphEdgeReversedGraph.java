package org.jgrapht.capi.graph;

import java.util.Set;

public class CapiGraphEdgeReversedGraph<V, E> extends CapiGraphDelegator<V, E> implements CapiGraph<V, E> {

	private static final long serialVersionUID = 1L;

	public CapiGraphEdgeReversedGraph(CapiGraph<V, E> g) {
		super(g);
	}

	@Override
	public E getEdge(V sourceVertex, V targetVertex) {
		return super.getEdge(targetVertex, sourceVertex);
	}

	@Override
	public Set<E> getAllEdges(V sourceVertex, V targetVertex) {
		return super.getAllEdges(targetVertex, sourceVertex);
	}

	@Override
	public E addEdge(V sourceVertex, V targetVertex) {
		return super.addEdge(targetVertex, sourceVertex);
	}

	@Override
	public boolean addEdge(V sourceVertex, V targetVertex, E e) {
		return super.addEdge(targetVertex, sourceVertex, e);
	}

	@Override
	public int inDegreeOf(V vertex) {
		return super.outDegreeOf(vertex);
	}

	@Override
	public int outDegreeOf(V vertex) {
		return super.inDegreeOf(vertex);
	}

	@Override
	public Set<E> incomingEdgesOf(V vertex) {
		return super.outgoingEdgesOf(vertex);
	}

	@Override
	public Set<E> outgoingEdgesOf(V vertex) {
		return super.incomingEdgesOf(vertex);
	}

	@Override
	public E removeEdge(V sourceVertex, V targetVertex) {
		return super.removeEdge(targetVertex, sourceVertex);
	}

	@Override
	public V getEdgeSource(E e) {
		return super.getEdgeTarget(e);
	}

	@Override
	public V getEdgeTarget(E e) {
		return super.getEdgeSource(e);
	}

	@Override
	public String toString() {
		return toStringFromSets(vertexSet(), edgeSet(), getType().isDirected());
	}

}