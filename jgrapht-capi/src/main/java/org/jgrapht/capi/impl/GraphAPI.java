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
import org.jgrapht.Graph;
import org.jgrapht.capi.Constants;
import org.jgrapht.capi.Status;
import org.jgrapht.capi.error.ObjectHandleExceptionHandler;
import org.jgrapht.capi.error.StatusReturnExceptionHandler;
import org.jgrapht.graph.AsUndirectedGraph;
import org.jgrapht.graph.AsUnmodifiableGraph;
import org.jgrapht.graph.AsUnweightedGraph;
import org.jgrapht.graph.EdgeReversedGraph;
import org.jgrapht.graph.builder.GraphTypeBuilder;
import org.jgrapht.util.SupplierUtil;

/**
 * Basic graph operations
 */
public class GraphAPI {

	private static ObjectHandles globalHandles = ObjectHandles.getGlobal();

	/**
	 * Create a graph and return its handle.
	 *
	 * @param thread the thread isolate
	 * @return the graph handle
	 */
	@CEntryPoint(name = Constants.LIB_PREFIX + "graph_create", exceptionHandler = ObjectHandleExceptionHandler.class)
	public static ObjectHandle createGraph(IsolateThread thread, boolean directed, boolean allowingSelfLoops,
			boolean allowingMultipleEdges, boolean weighted) {
		Graph<Long, Long> graph;
		if (directed) {
			graph = GraphTypeBuilder.directed().weighted(weighted).allowingMultipleEdges(allowingMultipleEdges)
					.allowingSelfLoops(allowingSelfLoops).vertexSupplier(SupplierUtil.createLongSupplier())
					.edgeSupplier(SupplierUtil.createLongSupplier()).buildGraph();
		} else {
			graph = GraphTypeBuilder.undirected().weighted(weighted).allowingMultipleEdges(allowingMultipleEdges)
					.allowingSelfLoops(allowingSelfLoops).vertexSupplier(SupplierUtil.createLongSupplier())
					.edgeSupplier(SupplierUtil.createLongSupplier()).buildGraph();
		}
		return globalHandles.create(graph);
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "graph_vertices_count", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int verticesCount(IsolateThread thread, ObjectHandle graphHandle, CLongPointer res) {
		Graph<Long, Long> g = globalHandles.get(graphHandle);
		long result = g.vertexSet().size();
		if (res.isNonNull()) {
			res.write(result);
		}
		return Status.SUCCESS.toCEnum();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "graph_edges_count", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int edgesCount(IsolateThread thread, ObjectHandle graphHandle, CLongPointer res) {
		Graph<Long, Long> g = globalHandles.get(graphHandle);
		long result = g.edgeSet().size();
		if (res.isNonNull()) {
			res.write(result);
		}
		return Status.SUCCESS.toCEnum();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "graph_add_vertex", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int addVertex(IsolateThread thread, ObjectHandle graphHandle, CLongPointer res) {
		Graph<Long, Long> g = globalHandles.get(graphHandle);
		long result = g.addVertex();
		if (res.isNonNull()) {
			res.write(result);
		}
		return Status.SUCCESS.toCEnum();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "graph_remove_vertex", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int removeVertex(IsolateThread thread, ObjectHandle graphHandle, long vertex, CIntPointer res) {
		Graph<Long, Long> g = globalHandles.get(graphHandle);
		boolean result = g.removeVertex(vertex);
		if (res.isNonNull()) {
			res.write(result ? 1 : 0);
		}
		return Status.SUCCESS.toCEnum();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "graph_contains_vertex", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int containsVertex(IsolateThread thread, ObjectHandle graphHandle, long vertex, CIntPointer res) {
		Graph<Long, Long> g = globalHandles.get(graphHandle);
		boolean result = g.containsVertex(vertex);
		if (res.isNonNull()) {
			res.write(result ? 1 : 0);
		}
		return Status.SUCCESS.toCEnum();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "graph_add_edge", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int addEdge(IsolateThread thread, ObjectHandle graphHandle, long source, long target,
			CLongPointer res) {
		Graph<Long, Long> g = globalHandles.get(graphHandle);
		long result = g.addEdge(source, target);
		if (res.isNonNull()) {
			res.write(result);
		}
		return Status.SUCCESS.toCEnum();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "graph_remove_edge", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int removeEdge(IsolateThread thread, ObjectHandle graphHandle, long edge, CIntPointer res) {
		Graph<Long, Long> g = globalHandles.get(graphHandle);
		boolean result = g.removeEdge(edge);
		if (res.isNonNull()) {
			res.write(result ? 1 : 0);
		}
		return Status.SUCCESS.toCEnum();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "graph_contains_edge", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int containsEdge(IsolateThread thread, ObjectHandle graphHandle, long edge, CIntPointer res) {
		Graph<Long, Long> g = globalHandles.get(graphHandle);
		boolean result = g.containsEdge(edge);
		if (res.isNonNull()) {
			res.write(result ? 1 : 0);
		}
		return Status.SUCCESS.toCEnum();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "graph_contains_edge_between", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int containsEdgeBetween(IsolateThread thread, ObjectHandle graphHandle, long source, long target,
			CIntPointer res) {
		Graph<Long, Long> g = globalHandles.get(graphHandle);
		boolean result = g.containsEdge(source, target);
		if (res.isNonNull()) {
			res.write(result ? 1 : 0);
		}
		return Status.SUCCESS.toCEnum();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "graph_degree_of", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int degreeOf(IsolateThread thread, ObjectHandle graphHandle, long vertex, CLongPointer res) {
		Graph<Long, Long> g = globalHandles.get(graphHandle);
		long result = g.degreeOf(vertex);
		if (res.isNonNull()) {
			res.write(result);
		}
		return Status.SUCCESS.toCEnum();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "graph_indegree_of", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int inDegreeOf(IsolateThread thread, ObjectHandle graphHandle, long vertex, CLongPointer res) {
		Graph<Long, Long> g = globalHandles.get(graphHandle);
		long result = g.inDegreeOf(vertex);
		if (res.isNonNull()) {
			res.write(result);
		}
		return Status.SUCCESS.toCEnum();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "graph_outdegree_of", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int outDegreeOf(IsolateThread thread, ObjectHandle graphHandle, long vertex, CLongPointer res) {
		Graph<Long, Long> g = globalHandles.get(graphHandle);
		long result = g.outDegreeOf(vertex);
		if (res.isNonNull()) {
			res.write(result);
		}
		return Status.SUCCESS.toCEnum();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "graph_edge_source", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int edgeSource(IsolateThread thread, ObjectHandle graphHandle, long edge, CLongPointer res) {
		Graph<Long, Long> g = globalHandles.get(graphHandle);
		long result = g.getEdgeSource(edge);
		if (res.isNonNull()) {
			res.write(result);
		}
		return Status.SUCCESS.toCEnum();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "graph_edge_target", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int edgeTarget(IsolateThread thread, ObjectHandle graphHandle, long edge, CLongPointer res) {
		Graph<Long, Long> g = globalHandles.get(graphHandle);
		long result = g.getEdgeTarget(edge);
		if (res.isNonNull()) {
			res.write(result);
		}
		return Status.SUCCESS.toCEnum();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "graph_is_weighted", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int isWeighted(IsolateThread thread, ObjectHandle graphHandle, CIntPointer res) {
		Graph<Long, Long> g = globalHandles.get(graphHandle);
		boolean result = g.getType().isWeighted();
		if (res.isNonNull()) {
			res.write(result ? 1 : 0);
		}
		return Status.SUCCESS.toCEnum();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "graph_is_directed", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int isDirected(IsolateThread thread, ObjectHandle graphHandle, CIntPointer res) {
		Graph<Long, Long> g = globalHandles.get(graphHandle);
		boolean result = g.getType().isDirected();
		if (res.isNonNull()) {
			res.write(result ? 1 : 0);
		}
		return Status.SUCCESS.toCEnum();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "graph_is_undirected", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int isUndirected(IsolateThread thread, ObjectHandle graphHandle, CIntPointer res) {
		Graph<Long, Long> g = globalHandles.get(graphHandle);
		boolean result = g.getType().isUndirected();
		if (res.isNonNull()) {
			res.write(result ? 1 : 0);
		}
		return Status.SUCCESS.toCEnum();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "graph_is_allowing_selfloops", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int allowSelfLoops(IsolateThread thread, ObjectHandle graphHandle, CIntPointer res) {
		Graph<Long, Long> g = globalHandles.get(graphHandle);
		boolean result = g.getType().isAllowingSelfLoops();
		if (res.isNonNull()) {
			res.write(result ? 1 : 0);
		}
		return Status.SUCCESS.toCEnum();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "graph_is_allowing_multipleedges", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int allowMultipleEdges(IsolateThread thread, ObjectHandle graphHandle, CIntPointer res) {
		Graph<Long, Long> g = globalHandles.get(graphHandle);
		boolean result = g.getType().isAllowingMultipleEdges();
		if (res.isNonNull()) {
			res.write(result ? 1 : 0);
		}
		return Status.SUCCESS.toCEnum();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "graph_get_edge_weight", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int allowMultipleEdges(IsolateThread thread, ObjectHandle graphHandle, long edge,
			CDoublePointer res) {
		Graph<Long, Long> g = globalHandles.get(graphHandle);
		double result = g.getEdgeWeight(edge);
		if (res.isNonNull()) {
			res.write(result);
		}
		return Status.SUCCESS.toCEnum();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "graph_set_edge_weight", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int allowMultipleEdges(IsolateThread thread, ObjectHandle graphHandle, long edge, double weight) {
		Graph<Long, Long> g = globalHandles.get(graphHandle);
		g.setEdgeWeight(edge, weight);
		return Status.SUCCESS.toCEnum();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "graph_create_all_vit", exceptionHandler = ObjectHandleExceptionHandler.class)
	public static ObjectHandle createAllVerticesIterator(IsolateThread thread, ObjectHandle graphHandle) {
		Graph<Long, Long> g = globalHandles.get(graphHandle);
		Iterator<Long> it = g.vertexSet().iterator();
		return ObjectHandles.getGlobal().create(it);
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "graph_create_all_eit", exceptionHandler = ObjectHandleExceptionHandler.class)
	public static ObjectHandle createAllEdgesIterator(IsolateThread thread, ObjectHandle graphHandle) {
		Graph<Long, Long> g = globalHandles.get(graphHandle);
		Iterator<Long> it = g.edgeSet().iterator();
		return ObjectHandles.getGlobal().create(it);
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "graph_create_between_eit", exceptionHandler = ObjectHandleExceptionHandler.class)
	public static ObjectHandle createEdgesBetweenIterator(IsolateThread thread, ObjectHandle graphHandle, long source,
			long target) {
		Graph<Long, Long> g = globalHandles.get(graphHandle);
		Set<Long> edges = g.getAllEdges(source, target);
		if (edges == null) {
			throw new IllegalArgumentException("Unknown vertex " + source + " or " + target);
		}
		Iterator<Long> it = edges.iterator();
		return ObjectHandles.getGlobal().create(it);
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "graph_vertex_create_eit", exceptionHandler = ObjectHandleExceptionHandler.class)
	public static ObjectHandle createVertexEdgesOfIterator(IsolateThread thread, ObjectHandle graphHandle,
			long vertex) {
		Graph<Long, Long> g = globalHandles.get(graphHandle);
		Iterator<Long> it = g.edgesOf(vertex).iterator();
		return ObjectHandles.getGlobal().create(it);
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "graph_vertex_create_out_eit", exceptionHandler = ObjectHandleExceptionHandler.class)
	public static ObjectHandle createVertexOutEdgesOfIterator(IsolateThread thread, ObjectHandle graphHandle,
			long vertex) {
		Graph<Long, Long> g = globalHandles.get(graphHandle);
		Iterator<Long> it = g.outgoingEdgesOf(vertex).iterator();
		return ObjectHandles.getGlobal().create(it);
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "graph_vertex_create_in_eit", exceptionHandler = ObjectHandleExceptionHandler.class)
	public static ObjectHandle createVertexInEdgesOfIterator(IsolateThread thread, ObjectHandle graphHandle,
			long vertex) {
		Graph<Long, Long> g = globalHandles.get(graphHandle);
		Iterator<Long> it = g.incomingEdgesOf(vertex).iterator();
		return globalHandles.create(it);
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "graph_as_undirected", exceptionHandler = ObjectHandleExceptionHandler.class)
	public static ObjectHandle asUndirected(IsolateThread thread, ObjectHandle graphHandle) {
		Graph<Long, Long> gIn = globalHandles.get(graphHandle);
		Graph<Long, Long> gOut = new AsUndirectedGraph<>(gIn);
		return globalHandles.create(gOut);
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "graph_as_unmodifiable", exceptionHandler = ObjectHandleExceptionHandler.class)
	public static ObjectHandle asUnmodifiable(IsolateThread thread, ObjectHandle graphHandle) {
		Graph<Long, Long> gIn = globalHandles.get(graphHandle);
		Graph<Long, Long> gOut = new AsUnmodifiableGraph<>(gIn);
		return globalHandles.create(gOut);
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "graph_as_unweighted", exceptionHandler = ObjectHandleExceptionHandler.class)
	public static ObjectHandle asUnweighted(IsolateThread thread, ObjectHandle graphHandle) {
		Graph<Long, Long> gIn = globalHandles.get(graphHandle);
		Graph<Long, Long> gOut = new AsUnweightedGraph<>(gIn);
		return globalHandles.create(gOut);
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "graph_as_edgereversed", exceptionHandler = ObjectHandleExceptionHandler.class)
	public static ObjectHandle asEdgeReversed(IsolateThread thread, ObjectHandle graphHandle) {
		Graph<Long, Long> gIn = globalHandles.get(graphHandle);
		Graph<Long, Long> gOut = new EdgeReversedGraph<>(gIn);
		return globalHandles.create(gOut);
	}

}
