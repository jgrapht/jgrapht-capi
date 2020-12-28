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

import java.util.function.Supplier;

import org.graalvm.nativeimage.IsolateThread;
import org.graalvm.nativeimage.ObjectHandle;
import org.graalvm.nativeimage.ObjectHandles;
import org.graalvm.nativeimage.c.function.CEntryPoint;
import org.graalvm.nativeimage.c.type.WordPointer;
import org.jgrapht.Graph;
import org.jgrapht.capi.Constants;
import org.jgrapht.capi.JGraphTContext.Status;
import org.jgrapht.capi.JGraphTContext.VoidToLongFunctionPointer;
import org.jgrapht.capi.error.StatusReturnExceptionHandler;
import org.jgrapht.capi.graph.CapiGraphWrapper;
import org.jgrapht.capi.graph.SafeEdgeSupplier;
import org.jgrapht.capi.graph.SafeLongEdgeSupplier;
import org.jgrapht.capi.graph.SafeLongVertexSupplier;
import org.jgrapht.capi.graph.SafeVertexSupplier;
import org.jgrapht.graph.DirectedAcyclicGraph;

public class GraphDagApi {

	private static ObjectHandles globalHandles = ObjectHandles.getGlobal();

	/**
	 * Create a dag and return its handle.
	 *
	 * @param thread the thread isolate
	 * @return the graph handle
	 */
	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.INTINT
			+ "graph_dag_create", exceptionHandler = StatusReturnExceptionHandler.class, documentation = {
					"Create a (int-int) dag and return its handle." })
	public static int createDag(IsolateThread thread, boolean allowMultipleEdges, boolean weighted, WordPointer res) {
		SafeVertexSupplier vSupplier = new SafeVertexSupplier();
		SafeEdgeSupplier eSupplier = new SafeEdgeSupplier();

		Graph<Integer, Integer> graph = new DirectedAcyclicGraph<>(vSupplier, eSupplier, weighted, allowMultipleEdges);

		vSupplier.setGraph(graph);
		eSupplier.setGraph(graph);

		if (res.isNonNull()) {
			res.write(globalHandles.create(graph));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	/**
	 * Create a dag and return its handle.
	 *
	 * @param thread the thread isolate
	 * @return the graph handle
	 */
	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.LONGLONG
			+ "graph_dag_create", exceptionHandler = StatusReturnExceptionHandler.class, documentation = {
					"Create a (long-long) dag and return its handle." })
	public static int createLongDag(IsolateThread thread, boolean allowMultipleEdges, boolean weighted,
			WordPointer res) {
		SafeLongVertexSupplier vSupplier = new SafeLongVertexSupplier();
		SafeLongEdgeSupplier eSupplier = new SafeLongEdgeSupplier();

		Graph<Long, Long> graph = new DirectedAcyclicGraph<>(vSupplier, eSupplier, weighted, allowMultipleEdges);

		vSupplier.setGraph(graph);
		eSupplier.setGraph(graph);

		if (res.isNonNull()) {
			res.write(globalHandles.create(graph));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.LONGLONG
			+ "graph_dag_create_with_suppliers", exceptionHandler = StatusReturnExceptionHandler.class, documentation = {
					"Create a (long-long) dag with suppliers and return its handle." })
	public static int createLongDag(IsolateThread thread, boolean allowMultipleEdges, boolean weighted,
			VoidToLongFunctionPointer vertexSupplier, VoidToLongFunctionPointer edgeSupplier, WordPointer res) {

		if (vertexSupplier.isNull()) {
			throw new IllegalArgumentException("Vertex supplier cannot be null.");
		}

		if (edgeSupplier.isNull()) {
			throw new IllegalArgumentException("Edge supplier cannot be null.");
		}

		Supplier<Long> vSupplier = () -> vertexSupplier.invoke();
		Supplier<Long> eSupplier = () -> edgeSupplier.invoke();

		Graph<Long, Long> graph = new DirectedAcyclicGraph<>(vSupplier, eSupplier, weighted, allowMultipleEdges);

		if (res.isNonNull()) {
			res.write(globalHandles.create(graph));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.ANYANY
			+ "graph_dag_topological_it", exceptionHandler = StatusReturnExceptionHandler.class, documentation = {
					"Given a dag create a topological iterator and return its handle." })
	public static <V, E> int createTopoIterator(IsolateThread thread, ObjectHandle graphHandle, WordPointer res) {
		DirectedAcyclicGraph<V, E> graph = getSafeWrappedDag(graphHandle);
		if (res.isNonNull()) {
			res.write(globalHandles.create(graph.iterator()));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.INTINT
			+ "graph_dag_vertex_descendants", exceptionHandler = StatusReturnExceptionHandler.class, documentation = "Given a (int-int) dag and a vertex returns its descendants.")
	public static int createVertexDescendants(IsolateThread thread, ObjectHandle graphHandle, int vertex,
			WordPointer res) {
		DirectedAcyclicGraph<Integer, Integer> graph = getSafeWrappedDag(graphHandle);
		if (res.isNonNull()) {
			res.write(globalHandles.create(graph.getDescendants(vertex)));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.LONGLONG
			+ "graph_dag_vertex_descendants", exceptionHandler = StatusReturnExceptionHandler.class, documentation = "Given a (long-long) dag and a vertex returns its descendants.")
	public static int createVertexDescendants(IsolateThread thread, ObjectHandle graphHandle, long vertex,
			WordPointer res) {
		DirectedAcyclicGraph<Long, Long> graph = getSafeWrappedDag(graphHandle);
		if (res.isNonNull()) {
			res.write(globalHandles.create(graph.getDescendants(vertex)));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.INTINT
			+ "graph_dag_vertex_ancestors", exceptionHandler = StatusReturnExceptionHandler.class, documentation = "Given a (int-int) dag and a vertex returns its ancestors.")
	public static int createVertexAncestors(IsolateThread thread, ObjectHandle graphHandle, int vertex,
			WordPointer res) {
		DirectedAcyclicGraph<Integer, Integer> graph = getSafeWrappedDag(graphHandle);
		if (res.isNonNull()) {
			res.write(globalHandles.create(graph.getAncestors(vertex)));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.LONGLONG
			+ "graph_dag_vertex_ancestors", exceptionHandler = StatusReturnExceptionHandler.class, documentation = "Given a (long-long) dag and a vertex returns its ancestors.")
	public static int createVertexAncestors(IsolateThread thread, ObjectHandle graphHandle, long vertex,
			WordPointer res) {
		DirectedAcyclicGraph<Long, Long> graph = getSafeWrappedDag(graphHandle);
		if (res.isNonNull()) {
			res.write(globalHandles.create(graph.getAncestors(vertex)));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@SuppressWarnings("unchecked")
	private static <V, E> DirectedAcyclicGraph<V, E> getSafeWrappedDag(Graph<V, E> graph) {
		if (graph instanceof DirectedAcyclicGraph) {
			return (DirectedAcyclicGraph<V, E>) graph;
		} else if (graph instanceof CapiGraphWrapper) {
			CapiGraphWrapper<V, E> wrapper = (CapiGraphWrapper<V, E>) graph;
			return getSafeWrappedDag(wrapper.getWrappedGraph());
		}
		throw new IllegalArgumentException("Graph does not look like a DAG");
	}

	private static <V, E> DirectedAcyclicGraph<V, E> getSafeWrappedDag(ObjectHandle graphHandle) {
		Graph<V, E> graph = globalHandles.get(graphHandle);
		return getSafeWrappedDag(graph);
	}

}
