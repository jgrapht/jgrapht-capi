/*
 * (C) Copyright 2020-2021, by Dimitrios Michail.
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

import org.jgrapht.Graph;
import org.jgrapht.capi.attributes.GraphAnyStore;

/**
 * A graph which also has ExternalRefs.
 */
public interface GraphWithAnyStore<V, E> extends Graph<V, E> {

	abstract GraphAnyStore<V, E> getStore();

	default int getGraphAttributesSize() {
		return getStore().getGraphAttributesSize();
	}

	default int getVertexAttributesSize(V element) {
		if (!containsVertex(element)) {
			throw new IllegalArgumentException("no such vertex in graph: " + element.toString());
		}
		return getStore().getVertexAttributesSize(element);
	}

	default int getEdgeAttributesSize(E element) {
		if (!containsEdge(element)) {
			throw new IllegalArgumentException("no such edge in graph: " + element.toString());
		}
		return getStore().getEdgeAttributesSize(element);
	}

	default Iterator<ExternalRef> graphAttributesKeysIterator() {
		return getStore().graphAttributesKeysIterator();
	}

	default Iterator<ExternalRef> vertexAttributesKeysIterator(V element) {
		if (!containsVertex(element)) {
			throw new IllegalArgumentException("no such vertex in graph: " + element.toString());
		}
		return getStore().vertexAttributesKeysIterator(element);
	}

	default Iterator<ExternalRef> edgeAttributesKeysIterator(E element) {
		if (!containsEdge(element)) {
			throw new IllegalArgumentException("no such edge in graph: " + element.toString());
		}
		return getStore().edgeAttributesKeysIterator(element);
	}

	default ExternalRef getVertexAttribute(V element, ExternalRef key) {
		if (!containsVertex(element)) {
			throw new IllegalArgumentException("no such vertex in graph: " + element.toString());
		}
		return getStore().getVertexAttribute(element, key);
	}

	default ExternalRef getEdgeAttribute(E element, ExternalRef key) {
		if (!containsEdge(element)) {
			throw new IllegalArgumentException("no such edge in graph: " + element.toString());
		}
		return getStore().getEdgeAttribute(element, key);
	}

	default ExternalRef getGraphAttribute(ExternalRef key) {
		return getStore().getGraphAttribute(key);
	}

	default ExternalRef putVertexAttribute(V element, ExternalRef key, ExternalRef value) {
		if (!containsVertex(element)) {
			throw new IllegalArgumentException("no such vertex in graph: " + element.toString());
		}
		return getStore().putVertexAttribute(element, key, value);
	}

	default ExternalRef putEdgeAttribute(E element, ExternalRef key, ExternalRef value) {
		if (!containsEdge(element)) {
			throw new IllegalArgumentException("no such edge in graph: " + element.toString());
		}
		return getStore().putEdgeAttribute(element, key, value);
	}

	default ExternalRef putGraphAttribute(ExternalRef key, ExternalRef value) {
		return getStore().putGraphAttribute(key, value);
	}

	default ExternalRef removeVertexAttribute(V element, ExternalRef key) {
		if (!containsVertex(element)) {
			throw new IllegalArgumentException("no such vertex in graph: " + element.toString());
		}
		return getStore().removeVertexAttribute(element, key);
	}

	default ExternalRef removeEdgeAttribute(E element, ExternalRef key) {
		if (!containsEdge(element)) {
			throw new IllegalArgumentException("no such edge in graph: " + element.toString());
		}
		return getStore().removeEdgeAttribute(element, key);
	}

	default ExternalRef removeGraphAttribute(ExternalRef key) {
		return getStore().removeGraphAttribute(key);
	}

	default void clearVertexAttributes(V vertex) {
		getStore().clearVertexAttributes(vertex);
	}

	default void clearEdgeAttributes(E edge) {
		getStore().clearEdgeAttributes(edge);
	}

	default void clearGraphAttributes() {
		getStore().clearGraphAttributes();
	}

}
