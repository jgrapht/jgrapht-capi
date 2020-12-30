/*
 * (C) Copyright 2003-2020, by Barak Naveh and Contributors.
 *
 * JGraphT : a free Java graph-theory library
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

import java.util.Collection;
import java.util.Set;

import org.jgrapht.GraphType;

public class CapiGraphAsUnmodifiableGraph<V, E> extends CapiGraphDelegator<V, E> implements CapiGraph<V, E> {

	private static final long serialVersionUID = 1L;
	private static final String UNMODIFIABLE = "this graph is unmodifiable";

	public CapiGraphAsUnmodifiableGraph(CapiGraph<V, E> g) {
		super(g);
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