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
package org.jgrapht.capi.impl;

import java.util.Iterator;

import org.graalvm.nativeimage.IsolateThread;
import org.graalvm.nativeimage.ObjectHandle;
import org.graalvm.nativeimage.ObjectHandles;
import org.graalvm.nativeimage.c.function.CEntryPoint;
import org.graalvm.nativeimage.c.type.CIntPointer;
import org.graalvm.nativeimage.c.type.WordPointer;
import org.graalvm.word.PointerBase;
import org.graalvm.word.WordFactory;
import org.jgrapht.capi.Constants;
import org.jgrapht.capi.JGraphTContext.Status;
import org.jgrapht.capi.Types;
import org.jgrapht.capi.error.StatusReturnExceptionHandler;
import org.jgrapht.capi.graph.DefaultCapiGraph;
import org.jgrapht.capi.graph.ExternalRef;
import org.jgrapht.capi.graph.GraphWithAnyStore;

/**
 * Support for attributes directly on the graph.
 */
public class GraphWithAttributesApi {

	private static ObjectHandles globalHandles = ObjectHandles.getGlobal();

	// ---------------- GET ----------------

	@CEntryPoint(name = Constants.LIB_PREFIX + Types.ANY_ANY_DREF_DREF
			+ "graph_attrs_get", exceptionHandler = StatusReturnExceptionHandler.class)
	public static <V, E> int getLongAttribute(IsolateThread thread, ObjectHandle graphHandle, PointerBase keyPtr,
			WordPointer res) {
		DefaultCapiGraph<V, E> graph = globalHandles.get(graphHandle);
		ExternalRef key = graph.toExternalRef(keyPtr);
		ExternalRef value = graph.getGraphAttribute(key);
		if (value == null) {
			throw new IllegalArgumentException("Key " + key + " not found");
		}
		if (res.isNonNull()) {
			res.write(value.getPtr());
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Types.INT_ANY_DREF_DREF
			+ "graph_attrs_vertex_get", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int getLongVertexAttribute(IsolateThread thread, ObjectHandle graphHandle, int element,
			PointerBase keyPtr, WordPointer res) {
		DefaultCapiGraph<Integer, ?> graph = globalHandles.get(graphHandle);
		ExternalRef key = graph.toExternalRef(keyPtr);
		ExternalRef value = graph.getVertexAttribute(element, key);
		if (value == null) {
			throw new IllegalArgumentException("Key " + key + " not found");
		}
		if (res.isNonNull()) {
			res.write(value.getPtr());
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Types.LONG_ANY_DREF_DREF
			+ "graph_attrs_vertex_get", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int getLongVertexAttribute(IsolateThread thread, ObjectHandle graphHandle, long element,
			PointerBase keyPtr, WordPointer res) {
		DefaultCapiGraph<Long, ?> graph = globalHandles.get(graphHandle);
		ExternalRef key = graph.toExternalRef(keyPtr);
		ExternalRef value = graph.getVertexAttribute(element, key);
		if (value == null) {
			throw new IllegalArgumentException("Key " + key + " not found");
		}
		if (res.isNonNull()) {
			res.write(value.getPtr());
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Types.DREF_ANY_DREF_DREF
			+ "graph_attrs_vertex_get", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int getVertexAttribute(IsolateThread thread, ObjectHandle graphHandle, PointerBase elementPtr,
			PointerBase keyPtr, WordPointer res) {
		DefaultCapiGraph<ExternalRef, ?> graph = globalHandles.get(graphHandle);
		ExternalRef element = graph.toExternalRef(elementPtr);
		ExternalRef key = graph.toExternalRef(keyPtr);
		ExternalRef value = graph.getVertexAttribute(element, key);
		if (value == null) {
			throw new IllegalArgumentException("Key " + key + " not found");
		}
		if (res.isNonNull()) {
			res.write(value.getPtr());
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Types.ANY_INT_DREF_DREF
			+ "graph_attrs_edge_get", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int getEdgeAttribute(IsolateThread thread, ObjectHandle graphHandle, int element, PointerBase keyPtr,
			WordPointer res) {
		DefaultCapiGraph<?, Integer> graph = globalHandles.get(graphHandle);
		ExternalRef key = graph.toExternalRef(keyPtr);
		ExternalRef value = graph.getEdgeAttribute(element, key);
		if (value == null) {
			throw new IllegalArgumentException("Key " + key + " not found");
		}
		if (res.isNonNull()) {
			res.write(value.getPtr());
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Types.ANY_LONG_DREF_DREF
			+ "graph_attrs_edge_get", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int getEdgeAttribute(IsolateThread thread, ObjectHandle graphHandle, long element, PointerBase keyPtr,
			WordPointer res) {
		DefaultCapiGraph<?, Long> graph = globalHandles.get(graphHandle);
		ExternalRef key = graph.toExternalRef(keyPtr);
		ExternalRef value = graph.getEdgeAttribute(element, key);
		if (value == null) {
			throw new IllegalArgumentException("Key " + key + " not found");
		}
		if (res.isNonNull()) {
			res.write(value.getPtr());
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Types.ANY_DREF_DREF_DREF
			+ "graph_attrs_edge_get", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int getEdgeAttribute(IsolateThread thread, ObjectHandle graphHandle, PointerBase elementPtr,
			PointerBase keyPtr, WordPointer res) {
		DefaultCapiGraph<?, ExternalRef> graph = globalHandles.get(graphHandle);
		ExternalRef element = graph.toExternalRef(elementPtr);
		ExternalRef key = graph.toExternalRef(keyPtr);
		ExternalRef value = graph.getEdgeAttribute(element, key);
		if (value == null) {
			throw new IllegalArgumentException("Key " + key + " not found");
		}
		if (res.isNonNull()) {
			res.write(value.getPtr());
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	// ---------------- STORE ----------------

	@CEntryPoint(name = Constants.LIB_PREFIX + Types.ANY_ANY_DREF_DREF
			+ "graph_attrs_put", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int putAttribute(IsolateThread thread, ObjectHandle graphHandle, PointerBase keyPtr,
			PointerBase valuePtr, WordPointer res) {
		DefaultCapiGraph<?, ?> graph = globalHandles.get(graphHandle);
		ExternalRef key = graph.toExternalRef(keyPtr);
		ExternalRef value = graph.toExternalRef(valuePtr);
		ExternalRef oldValue = graph.putGraphAttribute(key, value);
		if (res.isNonNull()) {
			if (oldValue == null) {
				res.write(WordFactory.nullPointer());
			} else {
				res.write(oldValue.getPtr());
			}
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Types.INT_ANY_DREF_DREF
			+ "graph_attrs_vertex_put", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int putVertexLongAttribute(IsolateThread thread, ObjectHandle graphHandle, int element,
			PointerBase keyPtr, PointerBase valuePtr, WordPointer res) {
		DefaultCapiGraph<Integer, ?> graph = globalHandles.get(graphHandle);
		ExternalRef key = graph.toExternalRef(keyPtr);
		ExternalRef value = graph.toExternalRef(valuePtr);
		ExternalRef oldValue = graph.putVertexAttribute(element, key, value);
		if (res.isNonNull()) {
			if (oldValue == null) {
				res.write(WordFactory.nullPointer());
			} else {
				res.write(oldValue.getPtr());
			}
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Types.LONG_ANY_DREF_DREF
			+ "graph_attrs_vertex_put", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int putVertexAttribute(IsolateThread thread, ObjectHandle graphHandle, long element,
			PointerBase keyPtr, PointerBase valuePtr, WordPointer res) {
		DefaultCapiGraph<Long, ?> graph = globalHandles.get(graphHandle);
		ExternalRef key = graph.toExternalRef(keyPtr);
		ExternalRef value = graph.toExternalRef(valuePtr);
		ExternalRef oldValue = graph.putVertexAttribute(element, key, value);
		if (res.isNonNull()) {
			if (oldValue == null) {
				res.write(WordFactory.nullPointer());
			} else {
				res.write(oldValue.getPtr());
			}
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Types.DREF_ANY_DREF_DREF
			+ "graph_attrs_vertex_put", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int putVertexAttribute(IsolateThread thread, ObjectHandle graphHandle, PointerBase elementPtr,
			PointerBase keyPtr, PointerBase valuePtr, WordPointer res) {
		DefaultCapiGraph<ExternalRef, ?> graph = globalHandles.get(graphHandle);
		ExternalRef element = graph.toExternalRef(elementPtr);
		ExternalRef key = graph.toExternalRef(keyPtr);
		ExternalRef value = graph.toExternalRef(valuePtr);
		ExternalRef oldValue = graph.putVertexAttribute(element, key, value);
		if (res.isNonNull()) {
			if (oldValue == null) {
				res.write(WordFactory.nullPointer());
			} else {
				res.write(oldValue.getPtr());
			}
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Types.ANY_INT_DREF_DREF
			+ "graph_attrs_edge_put", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int putEdgeAttribute(IsolateThread thread, ObjectHandle graphHandle, int element, PointerBase keyPtr,
			PointerBase valuePtr, WordPointer res) {
		DefaultCapiGraph<?, Integer> graph = globalHandles.get(graphHandle);
		ExternalRef key = graph.toExternalRef(keyPtr);
		ExternalRef value = graph.toExternalRef(valuePtr);
		ExternalRef oldValue = graph.putEdgeAttribute(element, key, value);
		if (res.isNonNull()) {
			if (oldValue == null) {
				res.write(WordFactory.nullPointer());
			} else {
				res.write(oldValue.getPtr());
			}
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Types.ANY_LONG_DREF_DREF
			+ "graph_attrs_edge_put", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int putEdgeAttribute(IsolateThread thread, ObjectHandle graphHandle, long element, PointerBase keyPtr,
			PointerBase valuePtr, WordPointer res) {
		DefaultCapiGraph<?, Long> graph = globalHandles.get(graphHandle);
		ExternalRef key = graph.toExternalRef(keyPtr);
		ExternalRef value = graph.toExternalRef(valuePtr);
		ExternalRef oldValue = graph.putEdgeAttribute(element, key, value);
		if (res.isNonNull()) {
			if (oldValue == null) {
				res.write(WordFactory.nullPointer());
			} else {
				res.write(oldValue.getPtr());
			}
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Types.ANY_DREF_DREF_DREF
			+ "graph_attrs_edge_put", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int putEdgeAttribute(IsolateThread thread, ObjectHandle graphHandle, PointerBase elementPtr,
			PointerBase keyPtr, PointerBase valuePtr, WordPointer res) {
		DefaultCapiGraph<?, ExternalRef> graph = globalHandles.get(graphHandle);
		ExternalRef key = graph.toExternalRef(keyPtr);
		ExternalRef value = graph.toExternalRef(valuePtr);
		ExternalRef element = graph.toExternalRef(elementPtr);
		ExternalRef oldValue = graph.putEdgeAttribute(element, key, value);
		if (res.isNonNull()) {
			if (oldValue == null) {
				res.write(WordFactory.nullPointer());
			} else {
				res.write(oldValue.getPtr());
			}
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	// ---------------- REMOVE ----------------

	@CEntryPoint(name = Constants.LIB_PREFIX + Types.ANY_ANY_DREF_DREF
			+ "graph_attrs_remove", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int removeAttribute(IsolateThread thread, ObjectHandle graphHandle, PointerBase keyPtr,
			WordPointer res) {
		DefaultCapiGraph<?, ?> graph = globalHandles.get(graphHandle);
		ExternalRef key = graph.toExternalRef(keyPtr);
		ExternalRef oldValue = graph.removeGraphAttribute(key);
		if (res.isNonNull()) {
			if (oldValue == null) {
				res.write(WordFactory.nullPointer());
			} else {
				res.write(oldValue.getPtr());
			}
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Types.INT_ANY_DREF_DREF
			+ "graph_attrs_vertex_remove", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int removeVertexAttribute(IsolateThread thread, ObjectHandle graphHandle, int element,
			PointerBase keyPtr, WordPointer res) {
		DefaultCapiGraph<Integer, ?> graph = globalHandles.get(graphHandle);
		ExternalRef key = graph.toExternalRef(keyPtr);
		ExternalRef oldValue = graph.removeVertexAttribute(element, key);
		if (res.isNonNull()) {
			if (oldValue == null) {
				res.write(WordFactory.nullPointer());
			} else {
				res.write(oldValue.getPtr());
			}
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Types.LONG_ANY_DREF_DREF
			+ "graph_attrs_vertex_remove", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int removeVertexAttribute(IsolateThread thread, ObjectHandle graphHandle, long element,
			PointerBase keyPtr, WordPointer res) {
		DefaultCapiGraph<Long, ?> graph = globalHandles.get(graphHandle);
		ExternalRef key = graph.toExternalRef(keyPtr);
		ExternalRef oldValue = graph.removeVertexAttribute(element, key);
		if (res.isNonNull()) {
			if (oldValue == null) {
				res.write(WordFactory.nullPointer());
			} else {
				res.write(oldValue.getPtr());
			}
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Types.DREF_ANY_DREF_DREF
			+ "graph_attrs_vertex_remove", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int removeVertexAttribute(IsolateThread thread, ObjectHandle graphHandle, PointerBase elementPtr,
			PointerBase keyPtr, WordPointer res) {
		DefaultCapiGraph<ExternalRef, ?> graph = globalHandles.get(graphHandle);
		ExternalRef element = graph.toExternalRef(elementPtr);
		ExternalRef key = graph.toExternalRef(keyPtr);
		ExternalRef oldValue = graph.removeVertexAttribute(element, key);
		if (res.isNonNull()) {
			if (oldValue == null) {
				res.write(WordFactory.nullPointer());
			} else {
				res.write(oldValue.getPtr());
			}
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Types.ANY_INT_DREF_DREF
			+ "graph_attrs_edge_remove", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int removeEdgeAttribute(IsolateThread thread, ObjectHandle graphHandle, int element,
			PointerBase keyPtr, WordPointer res) {
		DefaultCapiGraph<?, Integer> graph = globalHandles.get(graphHandle);
		ExternalRef key = graph.toExternalRef(keyPtr);
		ExternalRef oldValue = graph.removeEdgeAttribute(element, key);
		if (res.isNonNull()) {
			if (oldValue == null) {
				res.write(WordFactory.nullPointer());
			} else {
				res.write(oldValue.getPtr());
			}
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Types.ANY_LONG_DREF_DREF
			+ "graph_attrs_edge_remove", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int removeEdgeAttribute(IsolateThread thread, ObjectHandle graphHandle, long element,
			PointerBase keyPtr, WordPointer res) {
		DefaultCapiGraph<?, Long> graph = globalHandles.get(graphHandle);
		ExternalRef key = graph.toExternalRef(keyPtr);
		ExternalRef oldValue = graph.removeEdgeAttribute(element, key);
		if (res.isNonNull()) {
			if (oldValue == null) {
				res.write(WordFactory.nullPointer());
			} else {
				res.write(oldValue.getPtr());
			}
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Types.ANY_DREF_DREF_DREF
			+ "graph_attrs_edge_remove", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int removeEdgeAttribute(IsolateThread thread, ObjectHandle graphHandle, PointerBase elementPtr,
			PointerBase keyPtr, WordPointer res) {
		DefaultCapiGraph<?, ExternalRef> graph = globalHandles.get(graphHandle);
		ExternalRef element = graph.toExternalRef(elementPtr);
		ExternalRef key = graph.toExternalRef(keyPtr);
		ExternalRef oldValue = graph.removeEdgeAttribute(element, key);
		if (res.isNonNull()) {
			if (oldValue == null) {
				res.write(WordFactory.nullPointer());
			} else {
				res.write(oldValue.getPtr());
			}
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	// ---------------- CONTAINS ----------------

	@CEntryPoint(name = Constants.LIB_PREFIX + Types.ANY_ANY_DREF_DREF
			+ "graph_attrs_contains", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int containsAttribute(IsolateThread thread, ObjectHandle graphHandle, PointerBase keyPtr,
			CIntPointer res) {
		DefaultCapiGraph<?, Long> graph = globalHandles.get(graphHandle);
		ExternalRef key = graph.toExternalRef(keyPtr);
		boolean contains = graph.getGraphAttribute(key) != null;
		if (res.isNonNull()) {
			res.write(contains ? 1 : 0);
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Types.INT_ANY_DREF_DREF
			+ "graph_attrs_vertex_contains", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int containsVertexAttribute(IsolateThread thread, ObjectHandle graphHandle, int element,
			PointerBase keyPtr, CIntPointer res) {
		DefaultCapiGraph<Integer, ?> graph = globalHandles.get(graphHandle);
		ExternalRef key = graph.toExternalRef(keyPtr);
		boolean contains = graph.getVertexAttribute(element, key) != null;
		if (res.isNonNull()) {
			res.write(contains ? 1 : 0);
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Types.LONG_ANY_DREF_DREF
			+ "graph_attrs_vertex_contains", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int containsVertexAttribute(IsolateThread thread, ObjectHandle graphHandle, long element,
			PointerBase keyPtr, CIntPointer res) {
		DefaultCapiGraph<Long, ?> graph = globalHandles.get(graphHandle);
		ExternalRef key = graph.toExternalRef(keyPtr);
		boolean contains = graph.getVertexAttribute(element, key) != null;
		if (res.isNonNull()) {
			res.write(contains ? 1 : 0);
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Types.DREF_ANY_DREF_DREF
			+ "graph_attrs_vertex_contains", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int containsVertexAttribute(IsolateThread thread, ObjectHandle graphHandle, PointerBase elementPtr,
			PointerBase keyPtr, CIntPointer res) {
		DefaultCapiGraph<ExternalRef, ?> graph = globalHandles.get(graphHandle);
		ExternalRef key = graph.toExternalRef(keyPtr);
		ExternalRef element = graph.toExternalRef(elementPtr);
		boolean contains = graph.getVertexAttribute(element, key) != null;
		if (res.isNonNull()) {
			res.write(contains ? 1 : 0);
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Types.ANY_INT_DREF_DREF
			+ "graph_attrs_edge_contains", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int containsEdgeAttribute(IsolateThread thread, ObjectHandle graphHandle, int element,
			PointerBase keyPtr, CIntPointer res) {
		DefaultCapiGraph<?, Integer> graph = globalHandles.get(graphHandle);
		ExternalRef key = graph.toExternalRef(keyPtr);
		boolean contains = graph.getEdgeAttribute(element, key) != null;
		if (res.isNonNull()) {
			res.write(contains ? 1 : 0);
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Types.ANY_LONG_DREF_DREF
			+ "graph_attrs_edge_contains", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int containsEdgeAttribute(IsolateThread thread, ObjectHandle graphHandle, long element,
			PointerBase keyPtr, CIntPointer res) {
		DefaultCapiGraph<?, Long> graph = globalHandles.get(graphHandle);
		ExternalRef key = graph.toExternalRef(keyPtr);
		boolean contains = graph.getEdgeAttribute(element, key) != null;
		if (res.isNonNull()) {
			res.write(contains ? 1 : 0);
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Types.ANY_DREF_DREF_DREF
			+ "graph_attrs_edge_contains", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int containsEdgeAttribute(IsolateThread thread, ObjectHandle graphHandle, PointerBase elementPtr,
			PointerBase keyPtr, CIntPointer res) {
		DefaultCapiGraph<?, ExternalRef> graph = globalHandles.get(graphHandle);
		ExternalRef key = graph.toExternalRef(keyPtr);
		ExternalRef element = graph.toExternalRef(elementPtr);
		boolean contains = graph.getEdgeAttribute(element, key) != null;
		if (res.isNonNull()) {
			res.write(contains ? 1 : 0);
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	// ---------------- ITERATOR ----------------

	@CEntryPoint(name = Constants.LIB_PREFIX + Types.ANY_ANY_DREF_ANY
			+ "graph_attrs_keys_iterator", exceptionHandler = StatusReturnExceptionHandler.class)
	public static <V, E> int graphAttributesIterator(IsolateThread thread, ObjectHandle graphHandle, WordPointer res) {
		DefaultCapiGraph<V, E> graph = globalHandles.get(graphHandle);
		Iterator<ExternalRef> it = graph.graphAttributesKeysIterator();
		if (res.isNonNull()) {
			res.write(globalHandles.create(it));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Types.INT_ANY_DREF_ANY
			+ "graph_attrs_vertex_keys_iterator", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int vertexAttributesIterator(IsolateThread thread, ObjectHandle graphHandle, int element,
			WordPointer res) {
		DefaultCapiGraph<Integer, ?> graph = globalHandles.get(graphHandle);
		Iterator<ExternalRef> it = graph.vertexAttributesKeysIterator(element);
		if (res.isNonNull()) {
			res.write(globalHandles.create(it));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Types.LONG_ANY_DREF_ANY
			+ "graph_attrs_vertex_keys_iterator", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int longVertexAttributesIterator(IsolateThread thread, ObjectHandle graphHandle, long element,
			WordPointer res) {
		DefaultCapiGraph<Long, ?> graph = globalHandles.get(graphHandle);
		Iterator<ExternalRef> it = graph.vertexAttributesKeysIterator(element);
		if (res.isNonNull()) {
			res.write(globalHandles.create(it));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Types.DREF_ANY_DREF_ANY
			+ "graph_attrs_vertex_keys_iterator", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int longVertexAttributesIterator(IsolateThread thread, ObjectHandle graphHandle,
			PointerBase elementPtr, WordPointer res) {
		DefaultCapiGraph<ExternalRef, ?> graph = globalHandles.get(graphHandle);
		ExternalRef element = graph.toExternalRef(elementPtr);
		Iterator<ExternalRef> it = graph.vertexAttributesKeysIterator(element);
		if (res.isNonNull()) {
			res.write(globalHandles.create(it));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Types.ANY_INT_DREF_ANY
			+ "graph_attrs_edge_keys_iterator", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int edgeAttributesIterator(IsolateThread thread, ObjectHandle graphHandle, int element,
			WordPointer res) {
		DefaultCapiGraph<?, Integer> graph = globalHandles.get(graphHandle);
		Iterator<ExternalRef> it = graph.edgeAttributesKeysIterator(element);
		if (res.isNonNull()) {
			res.write(globalHandles.create(it));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Types.ANY_LONG_DREF_ANY
			+ "graph_attrs_edge_keys_iterator", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int longEdgeAttributesIterator(IsolateThread thread, ObjectHandle graphHandle, long element,
			WordPointer res) {
		DefaultCapiGraph<?, Long> graph = globalHandles.get(graphHandle);
		Iterator<ExternalRef> it = graph.edgeAttributesKeysIterator(element);
		if (res.isNonNull()) {
			res.write(globalHandles.create(it));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Types.ANY_DREF_DREF_ANY
			+ "graph_attrs_edge_keys_iterator", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int longEdgeAttributesIterator(IsolateThread thread, ObjectHandle graphHandle, PointerBase elementPtr,
			WordPointer res) {
		DefaultCapiGraph<?, ExternalRef> graph = globalHandles.get(graphHandle);
		ExternalRef element = graph.toExternalRef(elementPtr);
		Iterator<ExternalRef> it = graph.edgeAttributesKeysIterator(element);
		if (res.isNonNull()) {
			res.write(globalHandles.create(it));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	// ---------------- LENGTH ----------------

	@CEntryPoint(name = Constants.LIB_PREFIX + Types.ANY_ANY_ANY_ANY
			+ "graph_attrs_size", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int graphAttributesSize(IsolateThread thread, ObjectHandle graphHandle, CIntPointer res) {
		GraphWithAnyStore<?, ?> graph = globalHandles.get(graphHandle);
		if (res.isNonNull()) {
			res.write(graph.getGraphAttributesSize());
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Types.INT_ANY_ANY_ANY
			+ "graph_attrs_vertex_size", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int graphVertexAttributesSize(IsolateThread thread, ObjectHandle graphHandle, int element,
			CIntPointer res) {
		DefaultCapiGraph<Integer, ?> graph = globalHandles.get(graphHandle);
		if (res.isNonNull()) {
			res.write(graph.getVertexAttributesSize(element));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Types.LONG_ANY_ANY_ANY
			+ "graph_attrs_vertex_size", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int graphVertexAttributesSize(IsolateThread thread, ObjectHandle graphHandle, long element,
			CIntPointer res) {
		DefaultCapiGraph<Long, ?> graph = globalHandles.get(graphHandle);
		if (res.isNonNull()) {
			res.write(graph.getVertexAttributesSize(element));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Types.DREF_ANY_ANY_ANY
			+ "graph_attrs_vertex_size", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int graphVertexAttributesSize(IsolateThread thread, ObjectHandle graphHandle, PointerBase elementPtr,
			CIntPointer res) {
		DefaultCapiGraph<ExternalRef, ?> graph = globalHandles.get(graphHandle);
		ExternalRef element = graph.toExternalRef(elementPtr);
		if (res.isNonNull()) {
			res.write(graph.getVertexAttributesSize(element));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Types.ANY_INT_ANY_ANY
			+ "graph_attrs_edge_size", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int graphEdgeAttributesSize(IsolateThread thread, ObjectHandle graphHandle, int element,
			CIntPointer res) {
		DefaultCapiGraph<?, Integer> graph = globalHandles.get(graphHandle);
		if (res.isNonNull()) {
			res.write(graph.getEdgeAttributesSize(element));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Types.ANY_LONG_ANY_ANY
			+ "graph_attrs_edge_size", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int graphEdgeAttributesSize(IsolateThread thread, ObjectHandle graphHandle, long element,
			CIntPointer res) {
		DefaultCapiGraph<?, Long> graph = globalHandles.get(graphHandle);
		if (res.isNonNull()) {
			res.write(graph.getEdgeAttributesSize(element));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Types.ANY_DREF_ANY_ANY
			+ "graph_attrs_edge_size", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int graphEdgeAttributesSize(IsolateThread thread, ObjectHandle graphHandle, PointerBase elementPtr,
			CIntPointer res) {
		DefaultCapiGraph<?, ExternalRef> graph = globalHandles.get(graphHandle);
		ExternalRef element = graph.toExternalRef(elementPtr);
		if (res.isNonNull()) {
			res.write(graph.getEdgeAttributesSize(element));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	// ---------------- CLEAR ----------------

	@CEntryPoint(name = Constants.LIB_PREFIX + Types.ANY_ANY_ANY_ANY
			+ "graph_attrs_clear", exceptionHandler = StatusReturnExceptionHandler.class)
	public static <V, E> int clearGraphAttribute(IsolateThread thread, ObjectHandle graphHandle) {
		DefaultCapiGraph<V, E> graph = globalHandles.get(graphHandle);
		graph.clearGraphAttributes();
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Types.INT_ANY_ANY_ANY
			+ "graph_attrs_vertex_clear", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int clearVertexGraphAttribute(IsolateThread thread, ObjectHandle graphHandle, int element) {
		DefaultCapiGraph<Integer, ?> graph = globalHandles.get(graphHandle);
		graph.clearVertexAttributes(element);
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Types.LONG_ANY_ANY_ANY
			+ "graph_attrs_vertex_clear", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int clearVertexGraphAttribute(IsolateThread thread, ObjectHandle graphHandle, long element) {
		DefaultCapiGraph<Long, ?> graph = globalHandles.get(graphHandle);
		graph.clearVertexAttributes(element);
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Types.DREF_ANY_ANY_ANY
			+ "graph_attrs_vertex_clear", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int clearVertexGraphAttribute(IsolateThread thread, ObjectHandle graphHandle,
			PointerBase elementPtr) {
		DefaultCapiGraph<ExternalRef, ?> graph = globalHandles.get(graphHandle);
		ExternalRef element = graph.toExternalRef(elementPtr);
		graph.clearVertexAttributes(element);
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Types.ANY_INT_ANY_ANY
			+ "graph_attrs_edge_clear", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int clearEdgeGraphAttribute(IsolateThread thread, ObjectHandle graphHandle, int element) {
		DefaultCapiGraph<?, Integer> graph = globalHandles.get(graphHandle);
		graph.clearEdgeAttributes(element);
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Types.ANY_LONG_ANY_ANY
			+ "graph_attrs_edge_clear", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int clearEdgeGraphAttribute(IsolateThread thread, ObjectHandle graphHandle, long element) {
		DefaultCapiGraph<?, Long> graph = globalHandles.get(graphHandle);
		graph.clearEdgeAttributes(element);
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Types.ANY_DREF_ANY_ANY
			+ "graph_attrs_edge_clear", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int clearEdgeGraphAttribute(IsolateThread thread, ObjectHandle graphHandle, PointerBase elementPtr) {
		DefaultCapiGraph<?, ExternalRef> graph = globalHandles.get(graphHandle);
		ExternalRef element = graph.toExternalRef(elementPtr);
		graph.clearEdgeAttributes(element);
		return Status.STATUS_SUCCESS.getCValue();
	}

}
