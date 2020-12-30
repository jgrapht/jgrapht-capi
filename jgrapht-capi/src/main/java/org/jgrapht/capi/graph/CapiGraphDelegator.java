package org.jgrapht.capi.graph;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

import org.jgrapht.Graph;
import org.jgrapht.GraphType;
import org.jgrapht.capi.attributes.GraphAttributesStore;
import org.jgrapht.event.GraphListener;
import org.jgrapht.event.VertexSetListener;
import org.jgrapht.graph.AbstractGraph;

public class CapiGraphDelegator<V, E> extends AbstractGraph<V, E> implements CapiGraph<V, E>, Serializable {

	private static final long serialVersionUID = -215068279981825448L;

	/*
	 * The graph to which operations are delegated.
	 */
	protected final CapiGraph<V, E> delegate;

	/**
	 * Constructor
	 *
	 * @param graph the backing graph (the delegate).
	 */
	public CapiGraphDelegator(CapiGraph<V, E> graph) {
		super();
		this.delegate = Objects.requireNonNull(graph, "graph must not be null");
	}

	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * Returns the delegator's vertex supplier or the backing graph's vertex
	 * supplier in case of null.
	 */
	@Override
	public Supplier<V> getVertexSupplier() {
		return delegate.getVertexSupplier();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * Returns the delegator's edge supplier or the backing graph's edge supplier in
	 * case of null.
	 */
	@Override
	public Supplier<E> getEdgeSupplier() {
		return delegate.getEdgeSupplier();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<E> getAllEdges(V sourceVertex, V targetVertex) {
		return delegate.getAllEdges(sourceVertex, targetVertex);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public E getEdge(V sourceVertex, V targetVertex) {
		return delegate.getEdge(sourceVertex, targetVertex);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public E addEdge(V sourceVertex, V targetVertex) {
		return delegate.addEdge(sourceVertex, targetVertex);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean addEdge(V sourceVertex, V targetVertex, E e) {
		return delegate.addEdge(sourceVertex, targetVertex, e);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public V addVertex() {
		return delegate.addVertex();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean addVertex(V v) {
		return delegate.addVertex(v);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean containsEdge(E e) {
		return delegate.containsEdge(e);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean containsVertex(V v) {
		return delegate.containsVertex(v);
	}

	/**
	 * Returns the degree of the specified vertex.
	 *
	 * @param vertex vertex whose degree is to be calculated
	 * @return the degree of the specified vertex
	 */
	public int degreeOf(V vertex) {
		return delegate.degreeOf(vertex);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<E> edgeSet() {
		return delegate.edgeSet();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<E> edgesOf(V vertex) {
		return delegate.edgesOf(vertex);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int inDegreeOf(V vertex) {
		return delegate.inDegreeOf(vertex);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<E> incomingEdgesOf(V vertex) {
		return delegate.incomingEdgesOf(vertex);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int outDegreeOf(V vertex) {
		return delegate.outDegreeOf(vertex);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<E> outgoingEdgesOf(V vertex) {
		return delegate.outgoingEdgesOf(vertex);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean removeEdge(E e) {
		return delegate.removeEdge(e);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public E removeEdge(V sourceVertex, V targetVertex) {
		return delegate.removeEdge(sourceVertex, targetVertex);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean removeVertex(V v) {
		return delegate.removeVertex(v);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return delegate.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<V> vertexSet() {
		return delegate.vertexSet();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public V getEdgeSource(E e) {
		return delegate.getEdgeSource(e);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public V getEdgeTarget(E e) {
		return delegate.getEdgeTarget(e);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public double getEdgeWeight(E e) {
		return delegate.getEdgeWeight(e);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setEdgeWeight(E e, double weight) {
		delegate.setEdgeWeight(e, weight);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GraphType getType() {
		return delegate.getType();
	}

	/**
	 * Return the backing graph (the delegate).
	 * 
	 * @return the backing graph (the delegate)
	 */
	protected Graph<V, E> getDelegate() {
		return delegate;
	}

	@Override
	public GraphAttributesStore<V, E> getStore() {
		return delegate.getStore();
	}

	@Override
	public void addGraphListener(GraphListener<V, E> l) {
		delegate.addGraphListener(l);
	}

	@Override
	public void addVertexSetListener(VertexSetListener<V> l) {
		delegate.addVertexSetListener(l);
	}

	@Override
	public void removeGraphListener(GraphListener<V, E> l) {
		delegate.removeGraphListener(l);
	}

	@Override
	public void removeVertexSetListener(VertexSetListener<V> l) {
		delegate.removeVertexSetListener(l);
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
