package org.jgrapht.capi.graph;

import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.capi.attributes.GraphAttributesStore;
import org.jgrapht.graph.GraphDelegator;

public class CapiEdgeReversedGraphWithAttributes<V, E> extends GraphDelegator<V, E>
		implements GraphWithAttributes<V, E>, CapiGraphWrapper<V, E> {

	private static final long serialVersionUID = 1L;

	private GraphWithAttributes<V, E> wrappedGraph;

	public CapiEdgeReversedGraphWithAttributes(GraphWithAttributes<V, E> g) {
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