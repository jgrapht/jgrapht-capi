package org.jgrapht.capi.graph;

import java.util.Iterator;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.ListenableGraph;
import org.jgrapht.capi.attributes.GraphAttributesStore;
import org.jgrapht.event.GraphListener;
import org.jgrapht.event.VertexSetListener;
import org.jgrapht.graph.DirectedAcyclicGraph;
import org.jgrapht.graph.GraphDelegator;

public class DefaultCapiGraph<V, E> extends GraphDelegator<V, E> implements CapiGraph<V, E> {

	private static final long serialVersionUID = 1L;

	protected Graph<V, E> graph;
	protected GraphAttributesStore<V, E> store;

	public DefaultCapiGraph(Graph<V, E> graph) {
		super(graph);
		this.graph = graph;
		this.store = new GraphAttributesStore<>();
	}

	@Override
	public GraphAttributesStore<V, E> getStore() {
		return store;
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
	public void addGraphListener(GraphListener<V, E> l) {
		if (graph instanceof ListenableGraph) {
			((ListenableGraph<V, E>) graph).addGraphListener(l);
		}
		throw new UnsupportedOperationException("Graph is not listenable");
	}

	@Override
	public void addVertexSetListener(VertexSetListener<V> l) {
		if (graph instanceof ListenableGraph) {
			((ListenableGraph<V, E>) graph).addVertexSetListener(l);
		}
		throw new UnsupportedOperationException("Graph is not listenable");
	}

	@Override
	public void removeGraphListener(GraphListener<V, E> l) {
		if (graph instanceof ListenableGraph) {
			((ListenableGraph<V, E>) graph).removeGraphListener(l);
		}
		throw new UnsupportedOperationException("Graph is not listenable");
	}

	@Override
	public void removeVertexSetListener(VertexSetListener<V> l) {
		if (graph instanceof ListenableGraph) {
			((ListenableGraph<V, E>) graph).removeVertexSetListener(l);
		}
		throw new UnsupportedOperationException("Graph is not listenable");
	}

	@Override
	public Set<V> getAncestors(V vertex) {
		if (graph instanceof DirectedAcyclicGraph) {
			return ((DirectedAcyclicGraph<V, E>) graph).getAncestors(vertex);
		}
		throw new UnsupportedOperationException("Graph is not a dag (by type)");
	}

	@Override
	public Set<V> getDescendants(V vertex) {
		if (graph instanceof DirectedAcyclicGraph) {
			return ((DirectedAcyclicGraph<V, E>) graph).getDescendants(vertex);
		}
		throw new UnsupportedOperationException("Graph is not a dag (by type)");
	}

	@Override
	public Iterator<V> iterator() {
		if (graph instanceof DirectedAcyclicGraph) {
			return ((DirectedAcyclicGraph<V, E>) graph).iterator();
		}
		return graph.vertexSet().iterator();
	}

}
