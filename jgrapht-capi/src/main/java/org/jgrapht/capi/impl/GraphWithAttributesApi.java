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

import org.graalvm.nativeimage.IsolateThread;
import org.graalvm.nativeimage.ObjectHandle;
import org.graalvm.nativeimage.ObjectHandles;
import org.graalvm.nativeimage.c.function.CEntryPoint;
import org.graalvm.nativeimage.c.type.CCharPointer;
import org.graalvm.nativeimage.c.type.CIntPointer;
import org.graalvm.nativeimage.c.type.CLongPointer;
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
	public static int getLongAttribute(IsolateThread thread, ObjectHandle graphHandle, CCharPointer namePtr,
			CLongPointer res) {
		GraphWithAttributes<Integer, Integer> graph = globalHandles.get(graphHandle);
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
	
	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.INTINT
			+ "graph_attrs_vertex_get_long", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int getLongVertexAttribute(IsolateThread thread, ObjectHandle graphHandle, CCharPointer namePtr, int element,
			CLongPointer res) {
		GraphWithAttributes<Integer, Integer> graph = globalHandles.get(graphHandle);
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
	
	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.LONGLONG
			+ "graph_attrs_vertex_get_long", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int getLongVertexAttribute(IsolateThread thread, ObjectHandle graphHandle, CCharPointer namePtr, long element,
			CLongPointer res) {
		GraphWithAttributes<Long, Long> graph = globalHandles.get(graphHandle);
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
	
	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.INTINT
			+ "graph_attrs_edge_get_long", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int getLongEdgeAttribute(IsolateThread thread, ObjectHandle graphHandle, CCharPointer namePtr, int element,
			CLongPointer res) {
		GraphWithAttributes<Integer, Integer> graph = globalHandles.get(graphHandle);
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
	
	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.LONGLONG
			+ "graph_attrs_edge_get_long", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int getLongEdgeAttribute(IsolateThread thread, ObjectHandle graphHandle, CCharPointer namePtr, long element,
			CLongPointer res) {
		GraphWithAttributes<Long, Long> graph = globalHandles.get(graphHandle);
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

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.INTINT
			+ "graph_attrs_vertex_put_long", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int putVertexLongAttribute(IsolateThread thread, ObjectHandle graphHandle, int element,
			CCharPointer namePtr, long value) {
		GraphWithAttributes<Integer, Integer> graph = globalHandles.get(graphHandle);
		String name = StringUtils.toJavaStringFromUtf8(namePtr);
		graph.putVertexAttribute(element, name, DefaultAttribute.createAttribute(value));
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.LONGLONG
			+ "graph_attrs_vertex_put_long", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int putVertexLongAttribute(IsolateThread thread, ObjectHandle graphHandle, long element,
			CCharPointer namePtr, long value) {
		GraphWithAttributes<Long, Long> graph = globalHandles.get(graphHandle);
		String name = StringUtils.toJavaStringFromUtf8(namePtr);
		graph.putVertexAttribute(element, name, DefaultAttribute.createAttribute(value));
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.INTINT
			+ "graph_attrs_edge_put_long", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int putEdgeLongAttribute(IsolateThread thread, ObjectHandle graphHandle, int element,
			CCharPointer namePtr, long value) {
		GraphWithAttributes<Integer, Integer> graph = globalHandles.get(graphHandle);
		String name = StringUtils.toJavaStringFromUtf8(namePtr);
		graph.putEdgeAttribute(element, name, DefaultAttribute.createAttribute(value));
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.LONGLONG
			+ "graph_attrs_edge_put_long", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int putEdgeLongAttribute(IsolateThread thread, ObjectHandle graphHandle, long element,
			CCharPointer namePtr, long value) {
		GraphWithAttributes<Long, Long> graph = globalHandles.get(graphHandle);
		String name = StringUtils.toJavaStringFromUtf8(namePtr);
		graph.putEdgeAttribute(element, name, DefaultAttribute.createAttribute(value));
		return Status.STATUS_SUCCESS.getCValue();
	}

	// ---------------- REMOVE ----------------

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.INTINT
			+ "graph_attrs_remove", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int removeAttribute(IsolateThread thread, ObjectHandle graphHandle, CCharPointer namePtr) {
		GraphWithAttributes<Integer, Integer> graph = globalHandles.get(graphHandle);
		String name = StringUtils.toJavaStringFromUtf8(namePtr);
		graph.removeGraphAttribute(name);
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.INTINT
			+ "graph_attrs_vertex_remove", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int removeVertexAttribute(IsolateThread thread, ObjectHandle graphHandle, int element,
			CCharPointer namePtr) {
		GraphWithAttributes<Integer, Integer> graph = globalHandles.get(graphHandle);
		String name = StringUtils.toJavaStringFromUtf8(namePtr);
		graph.removeVertexAttribute(element, name);
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.LONGLONG
			+ "graph_attrs_vertex_remove", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int removeVertexAttribute(IsolateThread thread, ObjectHandle graphHandle, long element,
			CCharPointer namePtr) {
		GraphWithAttributes<Long, Long> graph = globalHandles.get(graphHandle);
		String name = StringUtils.toJavaStringFromUtf8(namePtr);
		graph.removeVertexAttribute(element, name);
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.INTINT
			+ "graph_attrs_edge_remove", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int removeEdgeAttribute(IsolateThread thread, ObjectHandle graphHandle, int element,
			CCharPointer namePtr) {
		GraphWithAttributes<Integer, Integer> graph = globalHandles.get(graphHandle);
		String name = StringUtils.toJavaStringFromUtf8(namePtr);
		graph.removeEdgeAttribute(element, name);
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.LONGLONG
			+ "graph_attrs_edge_remove", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int removeEdgeAttribute(IsolateThread thread, ObjectHandle graphHandle, long element,
			CCharPointer namePtr) {
		GraphWithAttributes<Long, Long> graph = globalHandles.get(graphHandle);
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

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.INTINT
			+ "graph_attrs_vertex_contains", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int containsVertexAttribute(IsolateThread thread, ObjectHandle graphHandle, int element,
			CCharPointer namePtr, CIntPointer res) {
		GraphWithAttributes<Integer, Integer> graph = globalHandles.get(graphHandle);
		String name = StringUtils.toJavaStringFromUtf8(namePtr);
		boolean contains = graph.getVertexAttribute(element, name) != null;
		if (res.isNonNull()) {
			res.write(contains ? 1 : 0);
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.LONGLONG
			+ "graph_attrs_vertex_contains", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int containsVertexAttribute(IsolateThread thread, ObjectHandle graphHandle, long element,
			CCharPointer namePtr, CIntPointer res) {
		GraphWithAttributes<Long, Long> graph = globalHandles.get(graphHandle);
		String name = StringUtils.toJavaStringFromUtf8(namePtr);
		boolean contains = graph.getVertexAttribute(element, name) != null;
		if (res.isNonNull()) {
			res.write(contains ? 1 : 0);
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.INTINT
			+ "graph_attrs_edge_contains", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int containsEdgeAttribute(IsolateThread thread, ObjectHandle graphHandle, int element,
			CCharPointer namePtr, CIntPointer res) {
		GraphWithAttributes<Integer, Integer> graph = globalHandles.get(graphHandle);
		String name = StringUtils.toJavaStringFromUtf8(namePtr);
		boolean contains = graph.getEdgeAttribute(element, name) != null;
		if (res.isNonNull()) {
			res.write(contains ? 1 : 0);
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.LONGLONG
			+ "graph_attrs_edge_contains", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int containsEdgeAttribute(IsolateThread thread, ObjectHandle graphHandle, long element,
			CCharPointer namePtr, CIntPointer res) {
		GraphWithAttributes<Long, Long> graph = globalHandles.get(graphHandle);
		String name = StringUtils.toJavaStringFromUtf8(namePtr);
		boolean contains = graph.getEdgeAttribute(element, name) != null;
		if (res.isNonNull()) {
			res.write(contains ? 1 : 0);
		}
		return Status.STATUS_SUCCESS.getCValue();
	}
}
