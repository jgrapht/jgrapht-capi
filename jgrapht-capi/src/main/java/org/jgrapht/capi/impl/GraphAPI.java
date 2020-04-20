package org.jgrapht.capi.impl;

import java.util.Iterator;
import java.util.Set;

import org.graalvm.nativeimage.IsolateThread;
import org.graalvm.nativeimage.ObjectHandle;
import org.graalvm.nativeimage.ObjectHandles;
import org.graalvm.nativeimage.c.function.CEntryPoint;
import org.graalvm.word.WordFactory;
import org.jgrapht.Graph;
import org.jgrapht.capi.Constants;
import org.jgrapht.capi.Errors;
import org.jgrapht.capi.Status;
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
	@CEntryPoint(name = Constants.LIB_PREFIX + "graph_create")
	public static ObjectHandle createGraph(IsolateThread thread, boolean directed, boolean allowingSelfLoops,
			boolean allowingMultipleEdges, boolean weighted) {
		try {
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
		} catch (Exception e) {
			Errors.setError(Status.ERROR, e.getMessage());
			return WordFactory.nullPointer();
		}
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "graph_vertices_count")
	public static long verticesCount(IsolateThread thread, ObjectHandle graphHandle) {
		try {
			Graph<Long, Long> g = globalHandles.get(graphHandle);
			return g.vertexSet().size();
		} catch (IllegalArgumentException e) {
			Errors.setError(Status.ILLEGAL_ARGUMENT, e.getMessage());
		} catch (Exception e) {
			Errors.setError(Status.ERROR, e.getMessage());
		}
		return 0L;
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "graph_edges_count")
	public static long edgesCount(IsolateThread thread, ObjectHandle graphHandle) {
		try {
			Graph<Long, Long> g = globalHandles.get(graphHandle);
			return g.edgeSet().size();
		} catch (IllegalArgumentException e) {
			Errors.setError(Status.ILLEGAL_ARGUMENT, e.getMessage());
		} catch (Exception e) {
			Errors.setError(Status.ERROR, e.getMessage());
		}
		return 0L;
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "graph_add_vertex")
	public static long addVertex(IsolateThread thread, ObjectHandle graphHandle) {
		try {
			Graph<Long, Long> g = globalHandles.get(graphHandle);
			return g.addVertex();
		} catch (IllegalArgumentException e) {
			Errors.setError(Status.ILLEGAL_ARGUMENT, e.getMessage());
		} catch (Exception e) {
			Errors.setError(Status.ERROR, e.getMessage());
		}
		return 0L;
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "graph_remove_vertex")
	public static boolean removeVertex(IsolateThread thread, ObjectHandle graphHandle, long vertex) {
		try {
			Graph<Long, Long> g = globalHandles.get(graphHandle);
			return g.removeVertex(vertex);
		} catch (IllegalArgumentException e) {
			Errors.setError(Status.ILLEGAL_ARGUMENT, e.getMessage());
		} catch (Exception e) {
			Errors.setError(Status.ERROR, e.getMessage());
		}
		return false;
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "graph_contains_vertex")
	public static boolean containsVertex(IsolateThread thread, ObjectHandle graphHandle, long vertex) {
		try {
			Graph<Long, Long> g = globalHandles.get(graphHandle);
			return g.containsVertex(vertex);
		} catch (IllegalArgumentException e) {
			Errors.setError(Status.ILLEGAL_ARGUMENT, e.getMessage());
		} catch (Exception e) {
			Errors.setError(Status.ERROR, e.getMessage());
		}
		return false;
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "graph_add_edge")
	public static long addEdge(IsolateThread thread, ObjectHandle graphHandle, long source, long target) {
		try {
			Graph<Long, Long> g = globalHandles.get(graphHandle);
			return g.addEdge(source, target);
		} catch (IllegalArgumentException e) {
			Errors.setError(Status.ILLEGAL_ARGUMENT, e.getMessage());
		} catch (Exception e) {
			Errors.setError(Status.ERROR, e.getMessage());
		}
		return 0L;
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "graph_remove_edge")
	public static boolean removeEdge(IsolateThread thread, ObjectHandle graphHandle, long edge) {
		try {
			Graph<Long, Long> g = globalHandles.get(graphHandle);
			return g.removeEdge(edge);
		} catch (IllegalArgumentException e) {
			Errors.setError(Status.ILLEGAL_ARGUMENT, e.getMessage());
		} catch (Exception e) {
			Errors.setError(Status.ERROR, e.getMessage());
		}
		return false;
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "graph_contains_edge")
	public static boolean containsEdge(IsolateThread thread, ObjectHandle graphHandle, long edge) {
		try {
			Graph<Long, Long> g = globalHandles.get(graphHandle);
			return g.containsEdge(edge);
		} catch (IllegalArgumentException e) {
			Errors.setError(Status.ILLEGAL_ARGUMENT, e.getMessage());
		} catch (Exception e) {
			Errors.setError(Status.ERROR, e.getMessage());
		}
		return false;
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "graph_contains_edge_between")
	public static boolean containsEdgeBetween(IsolateThread thread, ObjectHandle graphHandle, long source,
			long target) {
		try {
			Graph<Long, Long> g = globalHandles.get(graphHandle);
			return g.containsEdge(source, target);
		} catch (IllegalArgumentException e) {
			Errors.setError(Status.ILLEGAL_ARGUMENT, e.getMessage());
		} catch (Exception e) {
			Errors.setError(Status.ERROR, e.getMessage());
		}
		return false;
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "graph_degree_of")
	public static long degreeOf(IsolateThread thread, ObjectHandle graphHandle, long vertex) {
		try {
			Graph<Long, Long> g = globalHandles.get(graphHandle);
			return g.degreeOf(vertex);
		} catch (IllegalArgumentException e) {
			Errors.setError(Status.ILLEGAL_ARGUMENT, e.getMessage());
		} catch (Exception e) {
			Errors.setError(Status.ERROR, e.getMessage());
		}
		return 0L;
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "graph_indegree_of")
	public static long inDegreeOf(IsolateThread thread, ObjectHandle graphHandle, long vertex) {
		try {
			Graph<Long, Long> g = globalHandles.get(graphHandle);
			return g.inDegreeOf(vertex);
		} catch (IllegalArgumentException e) {
			Errors.setError(Status.ILLEGAL_ARGUMENT, e.getMessage());
		} catch (Exception e) {
			Errors.setError(Status.ERROR, e.getMessage());
		}
		return 0L;
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "graph_outdegree_of")
	public static long outDegreeOf(IsolateThread thread, ObjectHandle graphHandle, long vertex) {
		try {
			Graph<Long, Long> g = globalHandles.get(graphHandle);
			return g.outDegreeOf(vertex);
		} catch (IllegalArgumentException e) {
			Errors.setError(Status.ILLEGAL_ARGUMENT, e.getMessage());
		} catch (Exception e) {
			Errors.setError(Status.ERROR, e.getMessage());
		}
		return 0L;
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "graph_edge_source")
	public static long edgeSource(IsolateThread thread, ObjectHandle graphHandle, long edge) {
		try {
			Graph<Long, Long> g = globalHandles.get(graphHandle);
			return g.getEdgeSource(edge);
		} catch (IllegalArgumentException e) {
			Errors.setError(Status.ILLEGAL_ARGUMENT, e.getMessage());
		} catch (Exception e) {
			Errors.setError(Status.ERROR, e.getMessage());
		}
		return 0L;
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "graph_edge_target")
	public static long edgeTarget(IsolateThread thread, ObjectHandle graphHandle, long edge) {
		try {
			Graph<Long, Long> g = globalHandles.get(graphHandle);
			return g.getEdgeTarget(edge);
		} catch (IllegalArgumentException e) {
			Errors.setError(Status.ILLEGAL_ARGUMENT, e.getMessage());
		} catch (Exception e) {
			Errors.setError(Status.ERROR, e.getMessage());
		}
		return 0L;
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "graph_is_weighted")
	public static boolean isWeighted(IsolateThread thread, ObjectHandle graphHandle) {
		try {
			Graph<Long, Long> g = globalHandles.get(graphHandle);
			return g.getType().isWeighted();
		} catch (IllegalArgumentException e) {
			Errors.setError(Status.ILLEGAL_ARGUMENT, e.getMessage());
		} catch (Exception e) {
			Errors.setError(Status.ERROR, e.getMessage());
		}
		return false;
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "graph_is_directed")
	public static boolean isDirected(IsolateThread thread, ObjectHandle graphHandle) {
		try {
			Graph<Long, Long> g = globalHandles.get(graphHandle);
			return g.getType().isDirected();
		} catch (IllegalArgumentException e) {
			Errors.setError(Status.ILLEGAL_ARGUMENT, e.getMessage());
		} catch (Exception e) {
			Errors.setError(Status.ERROR, e.getMessage());
		}
		return false;
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "graph_is_undirected")
	public static boolean isUndirected(IsolateThread thread, ObjectHandle graphHandle) {
		try {
			Graph<Long, Long> g = globalHandles.get(graphHandle);
			return g.getType().isUndirected();
		} catch (IllegalArgumentException e) {
			Errors.setError(Status.ILLEGAL_ARGUMENT, e.getMessage());
		} catch (Exception e) {
			Errors.setError(Status.ERROR, e.getMessage());
		}
		return false;
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "graph_is_allowing_selfloops")
	public static boolean allowSelfLoops(IsolateThread thread, ObjectHandle graphHandle) {
		try {
			Graph<Long, Long> g = globalHandles.get(graphHandle);
			return g.getType().isAllowingSelfLoops();
		} catch (IllegalArgumentException e) {
			Errors.setError(Status.ILLEGAL_ARGUMENT, e.getMessage());
		} catch (Exception e) {
			Errors.setError(Status.ERROR, e.getMessage());
		}
		return false;
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "graph_is_allowing_multipleedges")
	public static boolean allowMultipleEdges(IsolateThread thread, ObjectHandle graphHandle) {
		try {
			Graph<Long, Long> g = globalHandles.get(graphHandle);
			return g.getType().isAllowingMultipleEdges();
		} catch (IllegalArgumentException e) {
			Errors.setError(Status.ILLEGAL_ARGUMENT, e.getMessage());
		} catch (Exception e) {
			Errors.setError(Status.ERROR, e.getMessage());
		}
		return false;
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "graph_get_edge_weight")
	public static double allowMultipleEdges(IsolateThread thread, ObjectHandle graphHandle, long edge) {
		try {
			Graph<Long, Long> g = globalHandles.get(graphHandle);
			return g.getEdgeWeight(edge);
		} catch (IllegalArgumentException e) {
			Errors.setError(Status.ILLEGAL_ARGUMENT, e.getMessage());
		} catch (Exception e) {
			Errors.setError(Status.ERROR, e.getMessage());
		}
		return Graph.DEFAULT_EDGE_WEIGHT;
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "graph_set_edge_weight")
	public static void allowMultipleEdges(IsolateThread thread, ObjectHandle graphHandle, long edge, double weight) {
		try {
			Graph<Long, Long> g = globalHandles.get(graphHandle);
			g.setEdgeWeight(edge, weight);
		} catch (IllegalArgumentException e) {
			Errors.setError(Status.ILLEGAL_ARGUMENT, e.getMessage());
		} catch (UnsupportedOperationException e) {
			Errors.setError(Status.UNSUPPORTED_OPERATION, e.getMessage());
		} catch (Exception e) {
			Errors.setError(Status.ERROR, e.getMessage());
		}
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "graph_create_all_vit")
	public static ObjectHandle createAllVerticesIterator(IsolateThread thread, ObjectHandle graphHandle) {
		try {
			Graph<Long, Long> g = globalHandles.get(graphHandle);
			Iterator<Long> it = g.vertexSet().iterator();
			return ObjectHandles.getGlobal().create(it);
		} catch (IllegalArgumentException e) {
			Errors.setError(Status.ILLEGAL_ARGUMENT, e.getMessage());
		} catch (Exception e) {
			Errors.setError(Status.ERROR, e.getMessage());
		}
		return WordFactory.nullPointer();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "graph_create_all_eit")
	public static ObjectHandle createAllEdgesIterator(IsolateThread thread, ObjectHandle graphHandle) {
		try {
			Graph<Long, Long> g = globalHandles.get(graphHandle);
			Iterator<Long> it = g.edgeSet().iterator();
			return ObjectHandles.getGlobal().create(it);
		} catch (IllegalArgumentException e) {
			Errors.setError(Status.ILLEGAL_ARGUMENT, e.getMessage());
		} catch (Exception e) {
			Errors.setError(Status.ERROR, e.getMessage());
		}
		return WordFactory.nullPointer();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "graph_create_between_eit")
	public static ObjectHandle createEdgesBetweenIterator(IsolateThread thread, ObjectHandle graphHandle, long source,
			long target) {
		try {
			Graph<Long, Long> g = globalHandles.get(graphHandle);
			Set<Long> edges = g.getAllEdges(source, target);
			if (edges == null) {
				Errors.setError(Status.INVALID_VERTEX, "One or both source and target vertices are missing");
				return WordFactory.nullPointer();
			}
			Iterator<Long> it = edges.iterator();
			return ObjectHandles.getGlobal().create(it);
		} catch (IllegalArgumentException e) {
			Errors.setError(Status.ILLEGAL_ARGUMENT, e.getMessage());
		} catch (Exception e) {
			Errors.setError(Status.ERROR, e.getMessage());
		}
		return WordFactory.nullPointer();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "graph_vertex_create_eit")
	public static ObjectHandle createVertexEdgesOfIterator(IsolateThread thread, ObjectHandle graphHandle,
			long vertex) {
		try {
			Graph<Long, Long> g = globalHandles.get(graphHandle);
			Iterator<Long> it = g.edgesOf(vertex).iterator();
			return ObjectHandles.getGlobal().create(it);
		} catch (IllegalArgumentException e) {
			Errors.setError(Status.ILLEGAL_ARGUMENT, e.getMessage());
		} catch (Exception e) {
			Errors.setError(Status.ERROR, e.getMessage());
		}
		return WordFactory.nullPointer();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "graph_vertex_create_out_eit")
	public static ObjectHandle createVertexOutEdgesOfIterator(IsolateThread thread, ObjectHandle graphHandle,
			long vertex) {
		try {
			Graph<Long, Long> g = globalHandles.get(graphHandle);
			Iterator<Long> it = g.outgoingEdgesOf(vertex).iterator();
			return ObjectHandles.getGlobal().create(it);
		} catch (IllegalArgumentException e) {
			Errors.setError(Status.ILLEGAL_ARGUMENT, e.getMessage());
		} catch (Exception e) {
			Errors.setError(Status.ERROR, e.getMessage());
		}
		return WordFactory.nullPointer();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "graph_vertex_create_in_eit")
	public static ObjectHandle createVertexInEdgesOfIterator(IsolateThread thread, ObjectHandle graphHandle,
			long vertex) {
		try {
			Graph<Long, Long> g = globalHandles.get(graphHandle);
			Iterator<Long> it = g.incomingEdgesOf(vertex).iterator();
			return globalHandles.create(it);
		} catch (IllegalArgumentException e) {
			Errors.setError(Status.ILLEGAL_ARGUMENT, e.getMessage());
		} catch (Exception e) {
			Errors.setError(Status.ERROR, e.getMessage());
		}
		return WordFactory.nullPointer();
	}

}
