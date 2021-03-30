/*
 * (C) Copyright 2020, by Dimitrios Michail.
 *
 * JGraphT C-API
 *
 * See the CONTRIBUTORS.md file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the
 * GNU Lesser General Public License v2.1 or later
 * which is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1-standalone.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR LGPL-2.1-or-later
 */
package org.jgrapht.capi.graph;

import java.util.Iterator;
import java.util.Set;

import org.graalvm.word.PointerBase;
import org.jgrapht.Graph;
import org.jgrapht.ListenableGraph;
import org.jgrapht.capi.attributes.GraphAttributesStore;
import org.jgrapht.event.GraphListener;
import org.jgrapht.event.VertexSetListener;
import org.jgrapht.graph.DirectedAcyclicGraph;
import org.jgrapht.graph.GraphDelegator;

public class DefaultCapiGraph<V, E> extends GraphDelegator<V, E> implements CapiGraph<V, E>, HashAndEqualsResolver {

	private static final long serialVersionUID = 1L;

	protected Graph<V, E> graph;
	protected GraphAttributesStore<V, E> store;
	protected HashAndEqualsResolver hashAndEqualsResolver;

	/**
	 * Create a graph.
	 * 
	 * @param graph the actual graph.
	 */
	public DefaultCapiGraph(Graph<V, E> graph) {
		super(graph);
		this.graph = graph;
		this.store = new GraphAttributesStore<>();
	}

	@Override
	public GraphAttributesStore<V, E> getStore() {
		return store;
	}

	public HashAndEqualsResolver getHashAndEqualsResolver() {
		return hashAndEqualsResolver;
	}

	public void setHashAndEqualsResolver(HashAndEqualsResolver hashAndEqualsResolver) {
		this.hashAndEqualsResolver = hashAndEqualsResolver;
	}

	@Override
	public ExternalRef toExternalRef(PointerBase ptr) {
		return hashAndEqualsResolver.toExternalRef(ptr);
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
	}

	@Override
	public void addVertexSetListener(VertexSetListener<V> l) {
		if (graph instanceof ListenableGraph) {
			((ListenableGraph<V, E>) graph).addVertexSetListener(l);
		}
	}

	@Override
	public void removeGraphListener(GraphListener<V, E> l) {
		if (graph instanceof ListenableGraph) {
			((ListenableGraph<V, E>) graph).removeGraphListener(l);
		}
	}

	@Override
	public void removeVertexSetListener(VertexSetListener<V> l) {
		if (graph instanceof ListenableGraph) {
			((ListenableGraph<V, E>) graph).removeVertexSetListener(l);
		}
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
