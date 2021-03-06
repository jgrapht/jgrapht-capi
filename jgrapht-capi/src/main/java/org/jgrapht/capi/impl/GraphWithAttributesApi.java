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
package org.jgrapht.capi.impl;

import java.util.Iterator;

import org.graalvm.nativeimage.IsolateThread;
import org.graalvm.nativeimage.ObjectHandle;
import org.graalvm.nativeimage.ObjectHandles;
import org.graalvm.nativeimage.c.function.CEntryPoint;
import org.graalvm.nativeimage.c.type.CCharPointer;
import org.graalvm.nativeimage.c.type.CIntPointer;
import org.graalvm.nativeimage.c.type.CLongPointer;
import org.graalvm.nativeimage.c.type.CTypeConversion.CCharPointerHolder;
import org.graalvm.nativeimage.c.type.WordPointer;
import org.jgrapht.capi.Constants;
import org.jgrapht.capi.JGraphTContext.Status;
import org.jgrapht.capi.StringUtils;
import org.jgrapht.capi.error.StatusReturnExceptionHandler;
import org.jgrapht.capi.graph.GraphWithAttributes;
import org.jgrapht.nio.Attribute;
import org.jgrapht.nio.DefaultAttribute;

/**
 * Support for attributes directly on the graph.
 */
public class GraphWithAttributesApi {

	private static ObjectHandles globalHandles = ObjectHandles.getGlobal();

	// ---------------- GET ----------------

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.ANYANY
			+ "graph_attrs_get_long", exceptionHandler = StatusReturnExceptionHandler.class)
	public static <V, E> int getLongAttribute(IsolateThread thread, ObjectHandle graphHandle, CCharPointer namePtr,
			CLongPointer res) {
		GraphWithAttributes<V, E> graph = globalHandles.get(graphHandle);
		String name = StringUtils.toJavaStringFromUtf8(namePtr);
		Attribute attr = graph.getGraphAttribute(name);
		if (attr == null) {
			throw new IllegalArgumentException("Key " + name + " not found");
		}
		Long valueAsLong = Long.parseLong(attr.getValue());
		if (res.isNonNull()) {
			res.write(valueAsLong);
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.INTANY
			+ "graph_attrs_vertex_get_long", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int getLongVertexAttribute(IsolateThread thread, ObjectHandle graphHandle, int element,
			CCharPointer namePtr, CLongPointer res) {
		GraphWithAttributes<Integer, ?> graph = globalHandles.get(graphHandle);
		String name = StringUtils.toJavaStringFromUtf8(namePtr);
		Attribute attr = graph.getVertexAttribute(element, name);
		if (attr == null) {
			throw new IllegalArgumentException("Key " + name + " not found");
		}
		Long valueAsLong = Long.parseLong(attr.getValue());
		if (res.isNonNull()) {
			res.write(valueAsLong);
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.LONGANY
			+ "graph_attrs_vertex_get_long", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int getLongVertexAttribute(IsolateThread thread, ObjectHandle graphHandle, long element,
			CCharPointer namePtr, CLongPointer res) {
		GraphWithAttributes<Long, ?> graph = globalHandles.get(graphHandle);
		String name = StringUtils.toJavaStringFromUtf8(namePtr);
		Attribute attr = graph.getVertexAttribute(element, name);
		if (attr == null) {
			throw new IllegalArgumentException("Key " + name + " not found");
		}
		Long valueAsLong = Long.parseLong(attr.getValue());
		if (res.isNonNull()) {
			res.write(valueAsLong);
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.ANYINT
			+ "graph_attrs_edge_get_long", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int getLongEdgeAttribute(IsolateThread thread, ObjectHandle graphHandle, int element,
			CCharPointer namePtr, CLongPointer res) {
		GraphWithAttributes<?, Integer> graph = globalHandles.get(graphHandle);
		String name = StringUtils.toJavaStringFromUtf8(namePtr);
		Attribute attr = graph.getEdgeAttribute(element, name);
		if (attr == null) {
			throw new IllegalArgumentException("Key " + name + " not found");
		}
		Long valueAsLong = Long.parseLong(attr.getValue());
		if (res.isNonNull()) {
			res.write(valueAsLong);
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.ANYLONG
			+ "graph_attrs_edge_get_long", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int getLongEdgeAttribute(IsolateThread thread, ObjectHandle graphHandle, long element,
			CCharPointer namePtr, CLongPointer res) {
		GraphWithAttributes<?, Long> graph = globalHandles.get(graphHandle);
		String name = StringUtils.toJavaStringFromUtf8(namePtr);
		Attribute attr = graph.getEdgeAttribute(element, name);
		if (attr == null) {
			throw new IllegalArgumentException("Key " + name + " not found");
		}
		Long valueAsLong = Long.parseLong(attr.getValue());
		if (res.isNonNull()) {
			res.write(valueAsLong);
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	// ---------------- STORE ----------------

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.ANYANY
			+ "graph_attrs_put_long", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int putLongAttribute(IsolateThread thread, ObjectHandle graphHandle, CCharPointer namePtr,
			long value) {
		GraphWithAttributes<Integer, Integer> graph = globalHandles.get(graphHandle);
		String name = StringUtils.toJavaStringFromUtf8(namePtr);
		graph.putGraphAttribute(name, DefaultAttribute.createAttribute(value));
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.INTANY
			+ "graph_attrs_vertex_put_long", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int putVertexLongAttribute(IsolateThread thread, ObjectHandle graphHandle, int element,
			CCharPointer namePtr, long value) {
		GraphWithAttributes<Integer, ?> graph = globalHandles.get(graphHandle);
		String name = StringUtils.toJavaStringFromUtf8(namePtr);
		graph.putVertexAttribute(element, name, DefaultAttribute.createAttribute(value));
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.LONGANY
			+ "graph_attrs_vertex_put_long", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int putVertexLongAttribute(IsolateThread thread, ObjectHandle graphHandle, long element,
			CCharPointer namePtr, long value) {
		GraphWithAttributes<Long, ?> graph = globalHandles.get(graphHandle);
		String name = StringUtils.toJavaStringFromUtf8(namePtr);
		graph.putVertexAttribute(element, name, DefaultAttribute.createAttribute(value));
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.ANYINT
			+ "graph_attrs_edge_put_long", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int putEdgeLongAttribute(IsolateThread thread, ObjectHandle graphHandle, int element,
			CCharPointer namePtr, long value) {
		GraphWithAttributes<?, Integer> graph = globalHandles.get(graphHandle);
		String name = StringUtils.toJavaStringFromUtf8(namePtr);
		graph.putEdgeAttribute(element, name, DefaultAttribute.createAttribute(value));
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.ANYLONG
			+ "graph_attrs_edge_put_long", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int putEdgeLongAttribute(IsolateThread thread, ObjectHandle graphHandle, long element,
			CCharPointer namePtr, long value) {
		GraphWithAttributes<?, Long> graph = globalHandles.get(graphHandle);
		String name = StringUtils.toJavaStringFromUtf8(namePtr);
		graph.putEdgeAttribute(element, name, DefaultAttribute.createAttribute(value));
		return Status.STATUS_SUCCESS.getCValue();
	}

	// ---------------- REMOVE ----------------

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.ANYANY
			+ "graph_attrs_remove", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int removeAttribute(IsolateThread thread, ObjectHandle graphHandle, CCharPointer namePtr) {
		GraphWithAttributes<?, ?> graph = globalHandles.get(graphHandle);
		String name = StringUtils.toJavaStringFromUtf8(namePtr);
		graph.removeGraphAttribute(name);
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.INTANY
			+ "graph_attrs_vertex_remove", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int removeVertexAttribute(IsolateThread thread, ObjectHandle graphHandle, int element,
			CCharPointer namePtr) {
		GraphWithAttributes<Integer, ?> graph = globalHandles.get(graphHandle);
		String name = StringUtils.toJavaStringFromUtf8(namePtr);
		graph.removeVertexAttribute(element, name);
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.LONGANY
			+ "graph_attrs_vertex_remove", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int removeVertexAttribute(IsolateThread thread, ObjectHandle graphHandle, long element,
			CCharPointer namePtr) {
		GraphWithAttributes<Long, ?> graph = globalHandles.get(graphHandle);
		String name = StringUtils.toJavaStringFromUtf8(namePtr);
		graph.removeVertexAttribute(element, name);
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.ANYINT
			+ "graph_attrs_edge_remove", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int removeEdgeAttribute(IsolateThread thread, ObjectHandle graphHandle, int element,
			CCharPointer namePtr) {
		GraphWithAttributes<?, Integer> graph = globalHandles.get(graphHandle);
		String name = StringUtils.toJavaStringFromUtf8(namePtr);
		graph.removeEdgeAttribute(element, name);
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.ANYLONG
			+ "graph_attrs_edge_remove", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int removeEdgeAttribute(IsolateThread thread, ObjectHandle graphHandle, long element,
			CCharPointer namePtr) {
		GraphWithAttributes<?, Long> graph = globalHandles.get(graphHandle);
		String name = StringUtils.toJavaStringFromUtf8(namePtr);
		graph.removeEdgeAttribute(element, name);
		return Status.STATUS_SUCCESS.getCValue();
	}

	// ---------------- CONTAINS ----------------

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.ANYANY
			+ "graph_attrs_contains", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int containsAttribute(IsolateThread thread, ObjectHandle graphHandle, CCharPointer namePtr,
			CIntPointer res) {
		GraphWithAttributes<?, ?> graph = globalHandles.get(graphHandle);
		String name = StringUtils.toJavaStringFromUtf8(namePtr);
		boolean contains = graph.getGraphAttribute(name) != null;
		if (res.isNonNull()) {
			res.write(contains ? 1 : 0);
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.INTANY
			+ "graph_attrs_vertex_contains", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int containsVertexAttribute(IsolateThread thread, ObjectHandle graphHandle, int element,
			CCharPointer namePtr, CIntPointer res) {
		GraphWithAttributes<Integer, ?> graph = globalHandles.get(graphHandle);
		String name = StringUtils.toJavaStringFromUtf8(namePtr);
		boolean contains = graph.getVertexAttribute(element, name) != null;
		if (res.isNonNull()) {
			res.write(contains ? 1 : 0);
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.LONGANY
			+ "graph_attrs_vertex_contains", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int containsVertexAttribute(IsolateThread thread, ObjectHandle graphHandle, long element,
			CCharPointer namePtr, CIntPointer res) {
		GraphWithAttributes<Long, ?> graph = globalHandles.get(graphHandle);
		String name = StringUtils.toJavaStringFromUtf8(namePtr);
		boolean contains = graph.getVertexAttribute(element, name) != null;
		if (res.isNonNull()) {
			res.write(contains ? 1 : 0);
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.ANYINT
			+ "graph_attrs_edge_contains", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int containsEdgeAttribute(IsolateThread thread, ObjectHandle graphHandle, int element,
			CCharPointer namePtr, CIntPointer res) {
		GraphWithAttributes<?, Integer> graph = globalHandles.get(graphHandle);
		String name = StringUtils.toJavaStringFromUtf8(namePtr);
		boolean contains = graph.getEdgeAttribute(element, name) != null;
		if (res.isNonNull()) {
			res.write(contains ? 1 : 0);
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.ANYLONG
			+ "graph_attrs_edge_contains", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int containsEdgeAttribute(IsolateThread thread, ObjectHandle graphHandle, long element,
			CCharPointer namePtr, CIntPointer res) {
		GraphWithAttributes<?, Long> graph = globalHandles.get(graphHandle);
		String name = StringUtils.toJavaStringFromUtf8(namePtr);
		boolean contains = graph.getEdgeAttribute(element, name) != null;
		if (res.isNonNull()) {
			res.write(contains ? 1 : 0);
		}
		return Status.STATUS_SUCCESS.getCValue();

	}

	// ---------------- ITERATOR ----------------

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.ANYANY
			+ "graph_attrs_keys_iterator", exceptionHandler = StatusReturnExceptionHandler.class)
	public static <V, E> int graphAttributesIterator(IsolateThread thread, ObjectHandle graphHandle, WordPointer res) {
		GraphWithAttributes<V, E> graph = globalHandles.get(graphHandle);
		Iterator<CCharPointerHolder> it = graph.graphAttributesKeysIterator();
		if (res.isNonNull()) {
			res.write(globalHandles.create(it));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.INTANY
			+ "graph_attrs_vertex_keys_iterator", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int vertexAttributesIterator(IsolateThread thread, ObjectHandle graphHandle, int element,
			WordPointer res) {
		GraphWithAttributes<Integer, ?> graph = globalHandles.get(graphHandle);
		if (res.isNonNull()) {
			res.write(globalHandles.create(graph.vertexAttributesKeysIterator(element)));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.LONGANY
			+ "graph_attrs_vertex_keys_iterator", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int vertexAttributesIterator(IsolateThread thread, ObjectHandle graphHandle, long element,
			WordPointer res) {
		GraphWithAttributes<Long, ?> graph = globalHandles.get(graphHandle);
		if (res.isNonNull()) {
			res.write(globalHandles.create(graph.vertexAttributesKeysIterator(element)));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.ANYINT
			+ "graph_attrs_edge_keys_iterator", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int edgeAttributesIterator(IsolateThread thread, ObjectHandle graphHandle, int element,
			WordPointer res) {
		GraphWithAttributes<?, Integer> graph = globalHandles.get(graphHandle);
		if (res.isNonNull()) {
			res.write(globalHandles.create(graph.edgeAttributesKeysIterator(element)));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.ANYLONG
			+ "graph_attrs_edge_keys_iterator", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int edgeAttributesIterator(IsolateThread thread, ObjectHandle graphHandle, long element,
			WordPointer res) {
		GraphWithAttributes<?, Long> graph = globalHandles.get(graphHandle);
		if (res.isNonNull()) {
			res.write(globalHandles.create(graph.edgeAttributesKeysIterator(element)));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	// ---------------- LENGTH ----------------

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.ANYANY
			+ "graph_attrs_size", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int graphAttributesSize(IsolateThread thread, ObjectHandle graphHandle, CIntPointer res) {
		GraphWithAttributes<?, ?> graph = globalHandles.get(graphHandle);
		if (res.isNonNull()) {
			res.write(graph.getGraphAttributesSize());
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.INTANY
			+ "graph_attrs_vertex_size", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int graphVertexAttributesSize(IsolateThread thread, ObjectHandle graphHandle, int element,
			CIntPointer res) {
		GraphWithAttributes<Integer, ?> graph = globalHandles.get(graphHandle);
		if (res.isNonNull()) {
			res.write(graph.getVertexAttributesSize(element));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.LONGANY
			+ "graph_attrs_vertex_size", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int graphVertexAttributesSize(IsolateThread thread, ObjectHandle graphHandle, long element,
			CIntPointer res) {
		GraphWithAttributes<Long, ?> graph = globalHandles.get(graphHandle);
		if (res.isNonNull()) {
			res.write(graph.getVertexAttributesSize(element));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.ANYINT
			+ "graph_attrs_edge_size", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int graphEdgeAttributesSize(IsolateThread thread, ObjectHandle graphHandle, int element,
			CIntPointer res) {
		GraphWithAttributes<?, Integer> graph = globalHandles.get(graphHandle);
		if (res.isNonNull()) {
			res.write(graph.getEdgeAttributesSize(element));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.ANYLONG
			+ "graph_attrs_edge_size", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int graphEdgeAttributesSize(IsolateThread thread, ObjectHandle graphHandle, long element,
			CIntPointer res) {
		GraphWithAttributes<?, Long> graph = globalHandles.get(graphHandle);
		if (res.isNonNull()) {
			res.write(graph.getEdgeAttributesSize(element));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

}
