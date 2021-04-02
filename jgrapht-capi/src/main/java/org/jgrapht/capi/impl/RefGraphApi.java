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
import java.util.Set;

import org.graalvm.nativeimage.IsolateThread;
import org.graalvm.nativeimage.ObjectHandle;
import org.graalvm.nativeimage.ObjectHandles;
import org.graalvm.nativeimage.c.function.CEntryPoint;
import org.graalvm.nativeimage.c.type.CDoublePointer;
import org.graalvm.nativeimage.c.type.CIntPointer;
import org.graalvm.nativeimage.c.type.CLongPointer;
import org.graalvm.nativeimage.c.type.WordPointer;
import org.graalvm.word.PointerBase;
import org.jgrapht.Graph;
import org.jgrapht.capi.Constants;
import org.jgrapht.capi.JGraphTContext.PtrToEqualsFunctionPointer;
import org.jgrapht.capi.JGraphTContext.PtrToHashFunctionPointer;
import org.jgrapht.capi.JGraphTContext.Status;
import org.jgrapht.capi.JGraphTContext.VToPFunctionPointer;
import org.jgrapht.capi.error.StatusReturnExceptionHandler;
import org.jgrapht.capi.graph.DefaultCapiGraph;
import org.jgrapht.capi.graph.DefaultHashAndEqualsResolver;
import org.jgrapht.capi.graph.ExternalRef;
import org.jgrapht.capi.graph.ExternalRefSupplier;
import org.jgrapht.capi.graph.HashAndEqualsResolver;
import org.jgrapht.graph.builder.GraphTypeBuilder;

/**
 * Basic graph operations
 */
public class RefGraphApi {

	private static ObjectHandles globalHandles = ObjectHandles.getGlobal();

	/**
	 * Create a graph with external references.
	 * 
	 * @param directed              directed or not
	 * @param allowingSelfLoops     allowing self loops or not
	 * @param allowingMultipleEdges allowing multiple edges or not
	 * @param weighted              weighted or not
	 * @param vertexSupplier        supplier for vertices. It should supply a
	 *                              pointer to a new object.
	 * @param edgeSupplier          supplier for edges. It should supply a pointer
	 *                              to a new object.
	 * @param hashLookup            function which looks up the hash method for each
	 *                              object. It accepts an object pointer and must
	 *                              return a function pointer. The function pointer
	 *                              must point to a function which accepts an object
	 *                              pointer and returns a long hash.
	 * @param equalsLookup          function which looks up the equals method for
	 *                              each object. It accepts an object pointer and
	 *                              must return a function pointer. The function
	 *                              pointer must point to a function which accepts
	 *                              two object pointers and returns an integer. Zero
	 *                              means not equal, one means equal and -1 means
	 *                              error.
	 * @return
	 */
	public static DefaultCapiGraph<ExternalRef, ExternalRef> createRefGraph(boolean directed, boolean allowingSelfLoops,
			boolean allowingMultipleEdges, boolean weighted, VToPFunctionPointer vertexSupplier,
			VToPFunctionPointer edgeSupplier, HashAndEqualsResolver hashEqualsResolver) {

		ExternalRefSupplier vSupplier = new ExternalRefSupplier(vertexSupplier, null);
		ExternalRefSupplier eSupplier = new ExternalRefSupplier(edgeSupplier, null);

		Graph<ExternalRef, ExternalRef> graph;
		if (directed) {
			graph = GraphTypeBuilder.directed().weighted(weighted).allowingMultipleEdges(allowingMultipleEdges)
					.allowingSelfLoops(allowingSelfLoops).vertexSupplier(vSupplier).edgeSupplier(eSupplier)
					.buildGraph();
		} else {
			graph = GraphTypeBuilder.undirected().weighted(weighted).allowingMultipleEdges(allowingMultipleEdges)
					.allowingSelfLoops(allowingSelfLoops).vertexSupplier(vSupplier).edgeSupplier(eSupplier)
					.buildGraph();
		}

		// wrap in order to support all methods
		DefaultCapiGraph<ExternalRef, ExternalRef> wrappedGraph = new DefaultCapiGraph<>(graph);

		// replace default hash and equals resolver
		if (hashEqualsResolver != null) {
			wrappedGraph.setHashAndEqualsResolver(hashEqualsResolver);
		}

		// provide graph in order to perform the hash and equals lookup
		vSupplier.setGraph(wrappedGraph);
		eSupplier.setGraph(wrappedGraph);

		return wrappedGraph;
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.DREF_DREF
			+ "graph_hash_equals_resolver_create", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int createHashEqualsResolver(IsolateThread thread, PtrToHashFunctionPointer hashLookup,
			PtrToEqualsFunctionPointer equalsLookup, WordPointer res) {
		HashAndEqualsResolver resolver = new DefaultHashAndEqualsResolver(hashLookup, equalsLookup);
		if (res.isNonNull()) {
			res.write(globalHandles.create(resolver));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	/**
	 * Create a graph and return its handle.
	 *
	 * @param thread the thread isolate
	 * @return the graph handle
	 */
	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.DREF_DREF
			+ "graph_create", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int createGraph(IsolateThread thread, boolean directed, boolean allowingSelfLoops,
			boolean allowingMultipleEdges, boolean weighted, VToPFunctionPointer vertexSupplier,
			VToPFunctionPointer edgeSupplier, ObjectHandle hashEqualsResolverHandle, WordPointer res) {
		HashAndEqualsResolver hashEqualsResolver = globalHandles.get(hashEqualsResolverHandle);
		DefaultCapiGraph<ExternalRef, ExternalRef> graph = createRefGraph(directed, allowingSelfLoops,
				allowingMultipleEdges, weighted, vertexSupplier, edgeSupplier, hashEqualsResolver);
		if (res.isNonNull()) {
			res.write(globalHandles.create(graph));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.DREF_ANY
			+ "graph_add_vertex", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int addVertex(IsolateThread thread, ObjectHandle graphHandle, WordPointer res) {
		Graph<ExternalRef, ?> g = globalHandles.get(graphHandle);
		ExternalRef v = g.addVertex();
		if (res.isNonNull()) {
			res.write(v.getPtr());
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.DREF_ANY
			+ "graph_add_given_vertex", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int addGivenVertex(IsolateThread thread, ObjectHandle graphHandle, PointerBase vertex,
			CIntPointer res) {
		DefaultCapiGraph<ExternalRef, ?> g = globalHandles.get(graphHandle);
		ExternalRef ref = g.toExternalRef(vertex);

		boolean result = g.addVertex(ref);
		if (res.isNonNull()) {
			res.write(result ? 1 : 0);
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.DREF_ANY
			+ "graph_remove_vertex", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int removeVertex(IsolateThread thread, ObjectHandle graphHandle, PointerBase vertex,
			CIntPointer res) {
		DefaultCapiGraph<ExternalRef, ?> g = globalHandles.get(graphHandle);
		ExternalRef ref = g.toExternalRef(vertex);
		boolean result = g.removeVertex(ref);
		if (res.isNonNull()) {
			res.write(result ? 1 : 0);
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.DREF_ANY
			+ "graph_contains_vertex", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int containsVertex(IsolateThread thread, ObjectHandle graphHandle, PointerBase vertex,
			CIntPointer res) {
		DefaultCapiGraph<ExternalRef, ?> g = globalHandles.get(graphHandle);
		ExternalRef ref = g.toExternalRef(vertex);
		boolean result = g.containsVertex(ref);
		if (res.isNonNull()) {
			res.write(result ? 1 : 0);
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.DREF_DREF
			+ "graph_add_edge", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int addEdge(IsolateThread thread, ObjectHandle graphHandle, PointerBase source, PointerBase target,
			WordPointer res) {
		DefaultCapiGraph<ExternalRef, ExternalRef> g = globalHandles.get(graphHandle);
		ExternalRef sourceRef = g.toExternalRef(source);
		ExternalRef targetRef = g.toExternalRef(target);
		ExternalRef result = g.addEdge(sourceRef, targetRef);
		if (result == null) {
			throw new IllegalArgumentException("Graph does not allow multiple edges");
		}
		if (res.isNonNull()) {
			res.write(result.getPtr());
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.DREF_DREF
			+ "graph_add_given_edge", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int addGivenEdge(IsolateThread thread, ObjectHandle graphHandle, PointerBase source,
			PointerBase target, PointerBase edge, CIntPointer res) {
		DefaultCapiGraph<ExternalRef, ExternalRef> g = globalHandles.get(graphHandle);
		ExternalRef sourceRef = g.toExternalRef(source);
		ExternalRef targetRef = g.toExternalRef(target);
		ExternalRef edgeRef = g.toExternalRef(edge);
		boolean result = g.addEdge(sourceRef, targetRef, edgeRef);
		if (res.isNonNull()) {
			res.write(result ? 1 : 0);
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.ANY_DREF
			+ "graph_remove_edge", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int removeEdge(IsolateThread thread, ObjectHandle graphHandle, PointerBase edge, CIntPointer res) {
		DefaultCapiGraph<?, ExternalRef> g = globalHandles.get(graphHandle);
		ExternalRef ref = g.toExternalRef(edge);
		boolean result = g.removeEdge(ref);
		if (res.isNonNull()) {
			res.write(result ? 1 : 0);
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.ANY_DREF
			+ "graph_contains_edge", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int containsEdge(IsolateThread thread, ObjectHandle graphHandle, PointerBase edge, CIntPointer res) {
		DefaultCapiGraph<?, ExternalRef> g = globalHandles.get(graphHandle);
		ExternalRef ref = g.toExternalRef(edge);
		boolean result = g.containsEdge(ref);
		if (res.isNonNull()) {
			res.write(result ? 1 : 0);
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.DREF_ANY
			+ "graph_contains_edge_between", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int containsEdgeBetween(IsolateThread thread, ObjectHandle graphHandle, PointerBase source,
			PointerBase target, CIntPointer res) {
		DefaultCapiGraph<ExternalRef, ?> g = globalHandles.get(graphHandle);
		ExternalRef sourceRef = g.toExternalRef(source);
		ExternalRef targetRef = g.toExternalRef(target);
		boolean result = g.containsEdge(sourceRef, targetRef);
		if (res.isNonNull()) {
			res.write(result ? 1 : 0);
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.DREF_ANY
			+ "graph_degree_of", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int degreeOf(IsolateThread thread, ObjectHandle graphHandle, PointerBase vertex, CLongPointer res) {
		DefaultCapiGraph<ExternalRef, ?> g = globalHandles.get(graphHandle);
		ExternalRef ref = g.toExternalRef(vertex);
		long result = g.iterables().degreeOf(ref);
		if (res.isNonNull()) {
			res.write(result);
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.DREF_ANY
			+ "graph_indegree_of", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int indegreeOf(IsolateThread thread, ObjectHandle graphHandle, PointerBase vertex, CLongPointer res) {
		DefaultCapiGraph<ExternalRef, ?> g = globalHandles.get(graphHandle);
		ExternalRef ref = g.toExternalRef(vertex);
		long result = g.iterables().inDegreeOf(ref);
		if (res.isNonNull()) {
			res.write(result);
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.DREF_ANY
			+ "graph_outdegree_of", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int outdegreeOf(IsolateThread thread, ObjectHandle graphHandle, PointerBase vertex,
			CLongPointer res) {
		DefaultCapiGraph<ExternalRef, ?> g = globalHandles.get(graphHandle);
		ExternalRef ref = g.toExternalRef(vertex);
		long result = g.iterables().outDegreeOf(ref);
		if (res.isNonNull()) {
			res.write(result);
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.DREF_DREF
			+ "graph_edge_source", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int edgeSource(IsolateThread thread, ObjectHandle graphHandle, PointerBase edge, WordPointer res) {
		DefaultCapiGraph<ExternalRef, ExternalRef> g = globalHandles.get(graphHandle);
		ExternalRef ref = g.toExternalRef(edge);
		ExternalRef result = g.getEdgeSource(ref);
		if (res.isNonNull()) {
			res.write(result.getPtr());
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.DREF_DREF
			+ "graph_edge_target", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int edgeTarget(IsolateThread thread, ObjectHandle graphHandle, PointerBase edge, WordPointer res) {
		DefaultCapiGraph<ExternalRef, ExternalRef> g = globalHandles.get(graphHandle);
		ExternalRef ref = g.toExternalRef(edge);
		ExternalRef result = g.getEdgeTarget(ref);
		if (res.isNonNull()) {
			res.write(result.getPtr());
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.ANY_DREF
			+ "graph_get_edge_weight", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int getEdgeWeight(IsolateThread thread, ObjectHandle graphHandle, PointerBase edge,
			CDoublePointer res) {
		DefaultCapiGraph<?, ExternalRef> g = globalHandles.get(graphHandle);
		ExternalRef ref = g.toExternalRef(edge);
		double result = g.getEdgeWeight(ref);
		if (res.isNonNull()) {
			res.write(result);
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.ANY_DREF
			+ "graph_set_edge_weight", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int setEdgeWeight(IsolateThread thread, ObjectHandle graphHandle, PointerBase edge, double weight) {
		DefaultCapiGraph<?, ExternalRef> g = globalHandles.get(graphHandle);
		ExternalRef ref = g.toExternalRef(edge);
		g.setEdgeWeight(ref, weight);
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.DREF_ANY
			+ "graph_create_between_eit", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int createEdgesBetweenIterator(IsolateThread thread, ObjectHandle graphHandle, PointerBase source,
			PointerBase target, WordPointer res) {
		DefaultCapiGraph<ExternalRef, ?> g = globalHandles.get(graphHandle);
		ExternalRef sourceRef = g.toExternalRef(source);
		ExternalRef targetRef = g.toExternalRef(target);
		Set<?> edges = g.getAllEdges(sourceRef, targetRef);
		if (edges == null) {
			throw new IllegalArgumentException("Unknown vertex " + source.rawValue() + " or " + target.rawValue());
		}
		Iterator<?> it = edges.iterator();
		if (res.isNonNull()) {
			res.write(globalHandles.create(it));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.DREF_ANY
			+ "graph_vertex_create_eit", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int createVertexEdgesOfIterator(IsolateThread thread, ObjectHandle graphHandle, PointerBase vertex,
			WordPointer res) {
		DefaultCapiGraph<ExternalRef, ?> g = globalHandles.get(graphHandle);
		ExternalRef ref = g.toExternalRef(vertex);
		Iterator<?> it = g.edgesOf(ref).iterator();
		if (res.isNonNull()) {
			res.write(globalHandles.create(it));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.DREF_ANY
			+ "graph_vertex_create_out_eit", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int createVertexOutEdgesOfIterator(IsolateThread thread, ObjectHandle graphHandle, PointerBase vertex,
			WordPointer res) {
		DefaultCapiGraph<ExternalRef, ?> g = globalHandles.get(graphHandle);
		ExternalRef ref = g.toExternalRef(vertex);
		Iterator<?> it = g.outgoingEdgesOf(ref).iterator();
		if (res.isNonNull()) {
			res.write(globalHandles.create(it));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.DREF_ANY
			+ "graph_vertex_create_in_eit", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int createVertexInEdgesOfIterator(IsolateThread thread, ObjectHandle graphHandle, PointerBase vertex,
			WordPointer res) {
		DefaultCapiGraph<ExternalRef, ?> g = globalHandles.get(graphHandle);
		ExternalRef ref = g.toExternalRef(vertex);
		Iterator<?> it = g.incomingEdgesOf(ref).iterator();
		if (res.isNonNull()) {
			res.write(globalHandles.create(it));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

}
