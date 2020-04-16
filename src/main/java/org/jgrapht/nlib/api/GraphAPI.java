package org.jgrapht.nlib.api;

import org.graalvm.nativeimage.IsolateThread;
import org.graalvm.nativeimage.c.function.CEntryPoint;
import org.graalvm.word.WordBase;
import org.graalvm.word.WordFactory;
import org.jgrapht.Graph;
import org.jgrapht.nlib.Constants;
import org.jgrapht.nlib.Errors;
import org.jgrapht.nlib.GraphLookupException;
import org.jgrapht.nlib.Graphs;
import org.jgrapht.nlib.Status;

/**
 * Basic graph operations
 */
public class GraphAPI {

	/**
	 * Create a graph and return its handle.
	 *
	 * @param thread the thread isolate
	 * @return the graph handle
	 */
	@CEntryPoint(name = Constants.LIB_PREFIX + "create_graph")
	public static WordBase createGraph(IsolateThread thread) {
		try {
			return Graphs.createGraph();
		} catch (Exception e) {
			Errors.setError(Status.GRAPH_CREATION_ERROR);
			return WordFactory.nullPointer();
		}
	}

	/**
	 * Release a graph
	 * 
	 * @param thread the thread isolate
	 * @return the graph handle
	 */
	@CEntryPoint(name = Constants.LIB_PREFIX + "delete_graph")
	public static void deleteGraph(IsolateThread thread, WordBase graphHandle) {
		try {
			Graphs.deleteGraph(graphHandle);
		} catch (GraphLookupException e) {
			Errors.setError(Status.INVALID_GRAPH);
		} catch (Exception e) {
			Errors.setError(Status.GENERIC_ERROR);
		}
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "graph_vertices_count")
	public static long verticesCount(IsolateThread thread, WordBase graphHandle) {
		try {
			return Graphs.getGraph(graphHandle).vertexSet().size();
		} catch (GraphLookupException e) {
			Errors.setError(Status.INVALID_GRAPH);
		} catch (Exception e) {
			Errors.setError(Status.GENERIC_ERROR);
		}
		return Constants.LONG_NO_RESULT;
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "graph_edges_count")
	public static long edgesCount(IsolateThread thread, WordBase graphHandle) {
		try {
			return Graphs.getGraph(graphHandle).edgeSet().size();
		} catch (GraphLookupException e) {
			Errors.setError(Status.INVALID_GRAPH);
		} catch (Exception e) {
			Errors.setError(Status.GENERIC_ERROR);
		}
		return Constants.LONG_NO_RESULT;
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "graph_add_vertex")
	public static long addVertex(IsolateThread thread, WordBase graphHandle) {
		try {
			System.out.println("Add vertex to graph " + graphHandle.rawValue());
			return Graphs.getGraph(graphHandle).addVertex();
		} catch (GraphLookupException e) {
			Errors.setError(Status.INVALID_GRAPH);
		} catch (Exception e) {
			Errors.setError(Status.GENERIC_ERROR);
		}
		return Constants.LONG_NO_RESULT;
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "graph_remove_vertex")
	public static boolean removeVertex(IsolateThread thread, WordBase graphHandle, long vertex) {
		try {
			return Graphs.getGraph(graphHandle).removeVertex(vertex);
		} catch (GraphLookupException e) {
			Errors.setError(Status.INVALID_GRAPH);
		} catch (Exception e) {
			Errors.setError(Status.GENERIC_ERROR);
		}
		return false;
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "graph_contains_vertex")
	public static boolean containsVertex(IsolateThread thread, WordBase graphHandle, long vertex) {
		try {
			return Graphs.getGraph(graphHandle).containsVertex(vertex);
		} catch (GraphLookupException e) {
			Errors.setError(Status.INVALID_GRAPH);
		} catch (Exception e) {
			Errors.setError(Status.GENERIC_ERROR);
		}
		return false;
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "graph_add_edge")
	public static long addEdge(IsolateThread thread, WordBase graphHandle, long source, long target) {
		try {
			Graph<Long, Long> graph = Graphs.getGraph(graphHandle);
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
	public static boolean removeEdge(IsolateThread thread, WordBase graphHandle, long edge) {
		try {
			Graph<Long, Long> graph = Graphs.getGraph(graphHandle);
			return graph.removeEdge(edge);
		} catch (GraphLookupException e) {
			Errors.setError(Status.INVALID_GRAPH);
		} catch (Exception e) {
			Errors.setError(Status.GENERIC_ERROR);
		}
		return false;
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "graph_contains_edge")
	public static boolean containsEdge(IsolateThread thread, WordBase graphHandle, long edge) {
		try {
			return Graphs.getGraph(graphHandle).containsEdge(edge);
		} catch (GraphLookupException e) {
			Errors.setError(Status.INVALID_GRAPH);
		} catch (Exception e) {
			Errors.setError(Status.GENERIC_ERROR);
		}
		return false;
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "graph_degree_of")
	public static long degreeOf(IsolateThread thread, WordBase graphHandle, long vertex) {
		try {
			return Graphs.getGraph(graphHandle).degreeOf(vertex);
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
	public static long inDegreeOf(IsolateThread thread, WordBase graphHandle, long vertex) {
		try {
			return Graphs.getGraph(graphHandle).inDegreeOf(vertex);
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
	public static long outDegreeOf(IsolateThread thread, WordBase graphHandle, long vertex) {
		try {
			return Graphs.getGraph(graphHandle).outDegreeOf(vertex);
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
	public static long edgeSource(IsolateThread thread, WordBase graphHandle, long edge) {
		try {
			return Graphs.getGraph(graphHandle).getEdgeSource(edge);
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
	public static long edgeTarget(IsolateThread thread, WordBase graphHandle, long edge) {
		try {
			return Graphs.getGraph(graphHandle).getEdgeTarget(edge);
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
	public static boolean isWeighted(IsolateThread thread, WordBase graphHandle) {
		try {
			return Graphs.getGraph(graphHandle).getType().isWeighted();
		} catch (GraphLookupException e) {
			Errors.setError(Status.INVALID_GRAPH);
		} catch (Exception e) {
			Errors.setError(Status.GENERIC_ERROR);
		}
		return false;
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "graph_is_directed")
	public static boolean isDirected(IsolateThread thread, WordBase graphHandle) {
		try {
			return Graphs.getGraph(graphHandle).getType().isDirected();
		} catch (GraphLookupException e) {
			Errors.setError(Status.INVALID_GRAPH);
		} catch (Exception e) {
			Errors.setError(Status.GENERIC_ERROR);
		}
		return false;
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "graph_is_undirected")
	public static boolean isUndirected(IsolateThread thread, WordBase graphHandle) {
		try {
			return Graphs.getGraph(graphHandle).getType().isUndirected();
		} catch (GraphLookupException e) {
			Errors.setError(Status.INVALID_GRAPH);
		} catch (Exception e) {
			Errors.setError(Status.GENERIC_ERROR);
		}
		return false;
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "graph_is_allowing_selfloops")
	public static boolean allowSelfLoops(IsolateThread thread, WordBase graphHandle) {
		try {
			return Graphs.getGraph(graphHandle).getType().isAllowingSelfLoops();
		} catch (GraphLookupException e) {
			Errors.setError(Status.INVALID_GRAPH);
		} catch (Exception e) {
			Errors.setError(Status.GENERIC_ERROR);
		}
		return false;
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "graph_is_allowing_multipleedges")
	public static boolean allowMultipleEdges(IsolateThread thread, WordBase graphHandle) {
		try {
			return Graphs.getGraph(graphHandle).getType().isAllowingMultipleEdges();
		} catch (GraphLookupException e) {
			Errors.setError(Status.INVALID_GRAPH);
		} catch (Exception e) {
			Errors.setError(Status.GENERIC_ERROR);
		}
		return false;
	}

}
