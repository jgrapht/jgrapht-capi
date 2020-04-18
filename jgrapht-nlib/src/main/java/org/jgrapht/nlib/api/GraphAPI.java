package org.jgrapht.nlib.api;

import java.util.Iterator;

import org.graalvm.nativeimage.IsolateThread;
import org.graalvm.nativeimage.ObjectHandle;
import org.graalvm.nativeimage.ObjectHandles;
import org.graalvm.nativeimage.c.function.CEntryPoint;
import org.graalvm.word.WordBase;
import org.graalvm.word.WordFactory;
import org.jgrapht.Graph;
import org.jgrapht.graph.builder.GraphTypeBuilder;
import org.jgrapht.nlib.Constants;
import org.jgrapht.nlib.Errors;
import org.jgrapht.nlib.GraphLookupException;
import org.jgrapht.nlib.Status;
import org.jgrapht.util.SupplierUtil;

/**
 * Basic graph operations
 */
public class GraphAPI {

	private static ObjectHandles globalHandles = ObjectHandles.getGlobal();

	/**
	 * Lookup a graph from its handle.
	 * 
	 * @param graphHandle the graph handle
	 * @return the graph
	 */
	public static Graph<Long, Long> getGraph(ObjectHandle graphHandle) {
		try {
			Graph<Long, Long> g = globalHandles.get(graphHandle);
			return g;
		} catch (Exception e) {
			throw new GraphLookupException(e);
		}
	}

	/**
	 * Create a graph and return its handle.
	 * 
	 * @return the graph handle
	 */
	public static ObjectHandle createGraph(boolean directed, boolean allowingSelfLoops, boolean allowingMultipleEdges, boolean weighted) {
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

	/**
	 * Create a graph and return its handle.
	 *
	 * @param thread the thread isolate
	 * @return the graph handle
	 */
	@CEntryPoint(name = Constants.LIB_PREFIX + "create_graph")
	public static WordBase createGraph(IsolateThread thread, boolean directed, boolean allowingSelfLoops,
			boolean allowingMultipleEdges, boolean weighted) {
		try {
			return createGraph(directed, allowingSelfLoops, allowingMultipleEdges, weighted);
		} catch (Exception e) {
			Errors.setError(Status.GRAPH_CREATION_ERROR);
			return WordFactory.nullPointer();
		}
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "graph_vertices_count")
	public static long verticesCount(IsolateThread thread, ObjectHandle graphHandle) {
		try {
			return getGraph(graphHandle).vertexSet().size();
		} catch (GraphLookupException e) {
			Errors.setError(Status.INVALID_GRAPH);
		} catch (Exception e) {
			Errors.setError(Status.GENERIC_ERROR);
		}
		return Constants.LONG_NO_RESULT;
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "graph_edges_count")
	public static long edgesCount(IsolateThread thread, ObjectHandle graphHandle) {
		try {
			return getGraph(graphHandle).edgeSet().size();
		} catch (GraphLookupException e) {
			Errors.setError(Status.INVALID_GRAPH);
		} catch (Exception e) {
			Errors.setError(Status.GENERIC_ERROR);
		}
		return Constants.LONG_NO_RESULT;
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "graph_add_vertex")
	public static long addVertex(IsolateThread thread, ObjectHandle graphHandle) {
		try {
			return getGraph(graphHandle).addVertex();
		} catch (GraphLookupException e) {
			Errors.setError(Status.INVALID_GRAPH);
		} catch (Exception e) {
			Errors.setError(Status.GENERIC_ERROR);
		}
		return Constants.LONG_NO_RESULT;
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "graph_remove_vertex")
	public static boolean removeVertex(IsolateThread thread, ObjectHandle graphHandle, long vertex) {
		try {
			return getGraph(graphHandle).removeVertex(vertex);
		} catch (GraphLookupException e) {
			Errors.setError(Status.INVALID_GRAPH);
		} catch (Exception e) {
			Errors.setError(Status.GENERIC_ERROR);
		}
		return false;
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "graph_contains_vertex")
	public static boolean containsVertex(IsolateThread thread, ObjectHandle graphHandle, long vertex) {
		try {
			return getGraph(graphHandle).containsVertex(vertex);
		} catch (GraphLookupException e) {
			Errors.setError(Status.INVALID_GRAPH);
		} catch (Exception e) {
			Errors.setError(Status.GENERIC_ERROR);
		}
		return false;
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "graph_add_edge")
	public static long addEdge(IsolateThread thread, ObjectHandle graphHandle, long source, long target) {
		try {
			Graph<Long, Long> graph = getGraph(graphHandle);
			return graph.addEdge(source, target);
		} catch (GraphLookupException e) {
			Errors.setError(Status.INVALID_GRAPH);
		} catch (IllegalArgumentException e) {
			Errors.setError(Status.INVALID_VERTEX);
		} catch (Exception e) {
			Errors.setError(Status.GENERIC_ERROR);
		}
		return Constants.LONG_NO_RESULT;
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "graph_remove_edge")
	public static boolean removeEdge(IsolateThread thread, ObjectHandle graphHandle, long edge) {
		try {
			Graph<Long, Long> graph = getGraph(graphHandle);
			return graph.removeEdge(edge);
		} catch (GraphLookupException e) {
			Errors.setError(Status.INVALID_GRAPH);
		} catch (Exception e) {
			Errors.setError(Status.GENERIC_ERROR);
		}
		return false;
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "graph_contains_edge")
	public static boolean containsEdge(IsolateThread thread, ObjectHandle graphHandle, long edge) {
		try {
			return getGraph(graphHandle).containsEdge(edge);
		} catch (GraphLookupException e) {
			Errors.setError(Status.INVALID_GRAPH);
		} catch (Exception e) {
			Errors.setError(Status.GENERIC_ERROR);
		}
		return false;
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "graph_degree_of")
	public static long degreeOf(IsolateThread thread, ObjectHandle graphHandle, long vertex) {
		try {
			return getGraph(graphHandle).degreeOf(vertex);
		} catch (GraphLookupException e) {
			Errors.setError(Status.INVALID_GRAPH);
		} catch (IllegalArgumentException e) {
			Errors.setError(Status.INVALID_VERTEX);
		} catch (Exception e) {
			Errors.setError(Status.GENERIC_ERROR);
		}
		return Constants.LONG_NO_RESULT;
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "graph_indegree_of")
	public static long inDegreeOf(IsolateThread thread, ObjectHandle graphHandle, long vertex) {
		try {
			return getGraph(graphHandle).inDegreeOf(vertex);
		} catch (GraphLookupException e) {
			Errors.setError(Status.INVALID_GRAPH);
		} catch (IllegalArgumentException e) {
			Errors.setError(Status.INVALID_VERTEX);
		} catch (Exception e) {
			Errors.setError(Status.GENERIC_ERROR);
		}
		return Constants.LONG_NO_RESULT;
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "graph_outdegree_of")
	public static long outDegreeOf(IsolateThread thread, ObjectHandle graphHandle, long vertex) {
		try {
			return getGraph(graphHandle).outDegreeOf(vertex);
		} catch (GraphLookupException e) {
			Errors.setError(Status.INVALID_GRAPH);
		} catch (IllegalArgumentException e) {
			Errors.setError(Status.INVALID_VERTEX);
		} catch (Exception e) {
			Errors.setError(Status.GENERIC_ERROR);
		}
		return Constants.LONG_NO_RESULT;
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "graph_edge_source")
	public static long edgeSource(IsolateThread thread, ObjectHandle graphHandle, long edge) {
		try {
			return getGraph(graphHandle).getEdgeSource(edge);
		} catch (GraphLookupException e) {
			Errors.setError(Status.INVALID_GRAPH);
		} catch (IllegalArgumentException e) {
			Errors.setError(Status.INVALID_EDGE);
		} catch (Exception e) {
			Errors.setError(Status.GENERIC_ERROR);
		}
		return Constants.LONG_NO_RESULT;
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "graph_edge_target")
	public static long edgeTarget(IsolateThread thread, ObjectHandle graphHandle, long edge) {
		try {
			return getGraph(graphHandle).getEdgeTarget(edge);
		} catch (GraphLookupException e) {
			Errors.setError(Status.INVALID_GRAPH);
		} catch (IllegalArgumentException e) {
			Errors.setError(Status.INVALID_EDGE);
		} catch (Exception e) {
			Errors.setError(Status.GENERIC_ERROR);
		}
		return Constants.LONG_NO_RESULT;
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "graph_is_weighted")
	public static boolean isWeighted(IsolateThread thread, ObjectHandle graphHandle) {
		try {
			return getGraph(graphHandle).getType().isWeighted();
		} catch (GraphLookupException e) {
			Errors.setError(Status.INVALID_GRAPH);
		} catch (Exception e) {
			Errors.setError(Status.GENERIC_ERROR);
		}
		return false;
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "graph_is_directed")
	public static boolean isDirected(IsolateThread thread, ObjectHandle graphHandle) {
		try {
			return getGraph(graphHandle).getType().isDirected();
		} catch (GraphLookupException e) {
			Errors.setError(Status.INVALID_GRAPH);
		} catch (Exception e) {
			Errors.setError(Status.GENERIC_ERROR);
		}
		return false;
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "graph_is_undirected")
	public static boolean isUndirected(IsolateThread thread, ObjectHandle graphHandle) {
		try {
			return getGraph(graphHandle).getType().isUndirected();
		} catch (GraphLookupException e) {
			Errors.setError(Status.INVALID_GRAPH);
		} catch (Exception e) {
			Errors.setError(Status.GENERIC_ERROR);
		}
		return false;
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "graph_is_allowing_selfloops")
	public static boolean allowSelfLoops(IsolateThread thread, ObjectHandle graphHandle) {
		try {
			return getGraph(graphHandle).getType().isAllowingSelfLoops();
		} catch (GraphLookupException e) {
			Errors.setError(Status.INVALID_GRAPH);
		} catch (Exception e) {
			Errors.setError(Status.GENERIC_ERROR);
		}
		return false;
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "graph_is_allowing_multipleedges")
	public static boolean allowMultipleEdges(IsolateThread thread, ObjectHandle graphHandle) {
		try {
			return getGraph(graphHandle).getType().isAllowingMultipleEdges();
		} catch (GraphLookupException e) {
			Errors.setError(Status.INVALID_GRAPH);
		} catch (Exception e) {
			Errors.setError(Status.GENERIC_ERROR);
		}
		return false;
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "graph_get_edge_weight")
	public static double allowMultipleEdges(IsolateThread thread, ObjectHandle graphHandle, long edge) {
		try {
			return getGraph(graphHandle).getEdgeWeight(edge);
		} catch (GraphLookupException e) {
			Errors.setError(Status.INVALID_GRAPH);
		} catch (IllegalArgumentException e) {
			Errors.setError(Status.INVALID_EDGE);
		} catch (Exception e) {
			Errors.setError(Status.GENERIC_ERROR);
		}
		return Graph.DEFAULT_EDGE_WEIGHT;
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "graph_set_edge_weight")
	public static void allowMultipleEdges(IsolateThread thread, ObjectHandle graphHandle, long edge, double weight) {
		try {
			getGraph(graphHandle).setEdgeWeight(edge, weight);
		} catch (GraphLookupException e) {
			Errors.setError(Status.INVALID_GRAPH);
		} catch (IllegalArgumentException e) {
			Errors.setError(Status.INVALID_EDGE);
		} catch (UnsupportedOperationException e) {
			Errors.setError(Status.UNSUPPORTED_OPERATION);
		} catch (Exception e) {
			Errors.setError(Status.GENERIC_ERROR);
		}
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "graph_create_all_vit")
	public static ObjectHandle createAllVerticesIterator(IsolateThread thread, ObjectHandle graphHandle) {
		try {
			Iterator<Long> it = getGraph(graphHandle).vertexSet().iterator();
			return ObjectHandles.getGlobal().create(it);
		} catch (GraphLookupException e) {
			Errors.setError(Status.INVALID_GRAPH);
		} catch (Exception e) {
			Errors.setError(Status.GENERIC_ERROR);
		}
		return WordFactory.nullPointer();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "graph_create_all_eit")
	public static ObjectHandle createAllEdgesIterator(IsolateThread thread, ObjectHandle graphHandle) {
		try {
			Iterator<Long> it = getGraph(graphHandle).edgeSet().iterator();
			return ObjectHandles.getGlobal().create(it);
		} catch (GraphLookupException e) {
			Errors.setError(Status.INVALID_GRAPH);
		} catch (Exception e) {
			Errors.setError(Status.GENERIC_ERROR);
		}
		return WordFactory.nullPointer();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "graph_vertex_create_eit")
	public static ObjectHandle createVertexEdgesOfIterator(IsolateThread thread, ObjectHandle graphHandle,
			long vertex) {
		try {
			Iterator<Long> it = getGraph(graphHandle).edgesOf(vertex).iterator();
			return ObjectHandles.getGlobal().create(it);
		} catch (GraphLookupException e) {
			Errors.setError(Status.INVALID_GRAPH);
		} catch (IllegalArgumentException e) {
			Errors.setError(Status.INVALID_VERTEX);
		} catch (Exception e) {
			Errors.setError(Status.GENERIC_ERROR);
		}
		return WordFactory.nullPointer();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "graph_vertex_create_out_eit")
	public static ObjectHandle createVertexOutEdgesOfIterator(IsolateThread thread, ObjectHandle graphHandle,
			long vertex) {
		try {
			Iterator<Long> it = getGraph(graphHandle).outgoingEdgesOf(vertex).iterator();
			return ObjectHandles.getGlobal().create(it);
		} catch (GraphLookupException e) {
			Errors.setError(Status.INVALID_GRAPH);
		} catch (IllegalArgumentException e) {
			Errors.setError(Status.INVALID_VERTEX);
		} catch (Exception e) {
			Errors.setError(Status.GENERIC_ERROR);
		}
		return WordFactory.nullPointer();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "graph_vertex_create_in_eit")
	public static ObjectHandle createVertexInEdgesOfIterator(IsolateThread thread, ObjectHandle graphHandle,
			long vertex) {
		try {
			Iterator<Long> it = getGraph(graphHandle).incomingEdgesOf(vertex).iterator();
			return globalHandles.create(it);
		} catch (GraphLookupException e) {
			Errors.setError(Status.INVALID_GRAPH);
		} catch (IllegalArgumentException e) {
			Errors.setError(Status.INVALID_VERTEX);
		} catch (Exception e) {
			Errors.setError(Status.GENERIC_ERROR);
		}
		return WordFactory.nullPointer();
	}

}
