package org.jgrapht.capi.graph;

import java.util.Set;

import org.jgrapht.GraphTests;
import org.jgrapht.GraphType;
import org.jgrapht.util.ArrayUnenforcedSet;

public class CapiGraphAsUndirectedGraph<V, E> extends CapiGraphDelegator<V, E> implements CapiGraph<V, E> {

	private static final long serialVersionUID = 1L;
	private static final String NO_EDGE_ADD = "this graph does not support edge addition";

	public CapiGraphAsUndirectedGraph(CapiGraph<V, E> g) {
		super(g);
		GraphTests.requireDirected(g);
	}

	@Override
	public Set<E> getAllEdges(V sourceVertex, V targetVertex) {
		Set<E> forwardList = super.getAllEdges(sourceVertex, targetVertex);

		if (sourceVertex.equals(targetVertex)) {
			// avoid duplicating loops
			return forwardList;
		}

		Set<E> reverseList = super.getAllEdges(targetVertex, sourceVertex);
		Set<E> list = new ArrayUnenforcedSet<>(forwardList.size() + reverseList.size());
		list.addAll(forwardList);
		list.addAll(reverseList);

		return list;
	}

	@Override
	public E getEdge(V sourceVertex, V targetVertex) {
		E edge = super.getEdge(sourceVertex, targetVertex);

		if (edge != null) {
			return edge;
		}

		// try the other direction
		return super.getEdge(targetVertex, sourceVertex);
	}

	@Override
	public E addEdge(V sourceVertex, V targetVertex) {
		throw new UnsupportedOperationException(NO_EDGE_ADD);
	}

	@Override
	public boolean addEdge(V sourceVertex, V targetVertex, E e) {
		throw new UnsupportedOperationException(NO_EDGE_ADD);
	}

	@Override
	public int degreeOf(V vertex) {
		return super.degreeOf(vertex);
	}

	@Override
	public Set<E> incomingEdgesOf(V vertex) {
		return super.edgesOf(vertex);
	}

	@Override
	public int inDegreeOf(V vertex) {
		return super.degreeOf(vertex);
	}

	@Override
	public Set<E> outgoingEdgesOf(V vertex) {
		return super.edgesOf(vertex);
	}

	@Override
	public int outDegreeOf(V vertex) {
		return super.degreeOf(vertex);
	}

	@Override
	public GraphType getType() {
		return super.getType().asUndirected();
	}

	@Override
	public String toString() {
		return super.toStringFromSets(vertexSet(), edgeSet(), false);
	}

}
