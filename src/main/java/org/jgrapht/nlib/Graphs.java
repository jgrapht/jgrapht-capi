package org.jgrapht.nlib;

import java.util.ArrayList;
import java.util.List;

import org.graalvm.nativeimage.IsolateThread;
import org.graalvm.nativeimage.c.function.CEntryPoint;
import org.graalvm.word.WordBase;
import org.graalvm.word.WordFactory;
import org.jgrapht.Graph;
import org.jgrapht.graph.builder.GraphTypeBuilder;
import org.jgrapht.util.SupplierUtil;

/**
 * Basic graph operations
 */
public class Graphs {

	/**
	 * The actual graphs.
	 */
	private static List<Graph<Long, Long>> graphs;

	static {
		graphs = new ArrayList<>();
	}

	/**
	 * Lookup a graph from its handle.
	 * 
	 * @param graphHandle the graph handle
	 * @return the graph
	 */
	private static Graph<Long, Long> getGraph(WordBase graphHandle) {
		int graphId = Long.valueOf(graphHandle.rawValue()).intValue();
		return graphs.get(graphId);
	}

	/**
	 * Create a graph and return its handle.
	 *
	 * @param thread the thread isolate
	 * @return the graph handle
	 */
	@CEntryPoint(name = Constants.LIB_PREFIX + "create_graph")
	public static WordBase createGraph(IsolateThread thread) {
		try {
			Graph<Long, Long> graph = GraphTypeBuilder.directed().allowingMultipleEdges(true).allowingSelfLoops(true)
					.vertexSupplier(SupplierUtil.createLongSupplier()).edgeSupplier(SupplierUtil.createLongSupplier())
					.buildGraph();
			int id = graphs.size();
			graphs.add(graph);
			return WordFactory.signed(id);
		} catch (Exception e) {
			Error.setError(Status.GRAPH_CREATION_ERROR);
			return WordFactory.nullPointer();
		}
	}

	/**
	 * Release a graph
	 * 
	 * @param thread the thread isolate
	 * @return the graph handle
	 */
	@CEntryPoint(name = Constants.LIB_PREFIX + "release_graph")
	public static void releaseGraph(IsolateThread thread, WordBase graphHandle) {
		try {
			int graphId = Long.valueOf(graphHandle.rawValue()).intValue();
			graphs.set(graphId, null);
		} catch (IndexOutOfBoundsException e) {
			Error.setError(Status.INVALID_GRAPH);
		} catch (Exception e) {
			Error.setError(Status.GENERIC_ERROR);
		}
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "graph_vertices_count")
	public static long verticesCount(IsolateThread thread, WordBase graphHandle) {
		try {
			return getGraph(graphHandle).vertexSet().size();
		} catch (IndexOutOfBoundsException e) {
			Error.setError(Status.INVALID_GRAPH);
		} catch (Exception e) {
			Error.setError(Status.GENERIC_ERROR);
		}
		return Constants.LONG_NO_RESULT;
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "graph_edges_count")
	public static long edgesCount(IsolateThread thread, WordBase graphHandle) {
		try {
			return getGraph(graphHandle).edgeSet().size();
		} catch (IndexOutOfBoundsException e) {
			Error.setError(Status.INVALID_GRAPH);
		} catch (Exception e) {
			Error.setError(Status.GENERIC_ERROR);
		}
		return Constants.LONG_NO_RESULT;
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "graph_add_vertex")
	public static long addVertex(IsolateThread thread, WordBase graphHandle) {
		try {
			System.out.println("Add vertex to graph " + graphHandle.rawValue());
			return getGraph(graphHandle).addVertex();
		} catch (IndexOutOfBoundsException e) {
			Error.setError(Status.INVALID_GRAPH);
		} catch (Exception e) {
			Error.setError(Status.GENERIC_ERROR);
		}
		return Constants.LONG_NO_RESULT;
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "graph_remove_vertex")
	public static boolean addVertex(IsolateThread thread, WordBase graphHandle, long vertex) {
		try {
			return getGraph(graphHandle).removeVertex(vertex);
		} catch (IndexOutOfBoundsException e) {
			Error.setError(Status.INVALID_GRAPH);
		} catch (Exception e) {
			Error.setError(Status.GENERIC_ERROR);
		}
		return false;
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "graph_add_edge")
	public static long addEdge(IsolateThread thread, WordBase graphHandle, long source, long target) {
		try {
			Graph<Long, Long> graph = getGraph(graphHandle);
			return graph.addEdge(source, target);
		} catch (IndexOutOfBoundsException e) {
			Error.setError(Status.INVALID_GRAPH);
		} catch (IllegalArgumentException e) {
			Error.setError(Status.INVALID_VERTEX);
		} catch (Exception e) {
			Error.setError(Status.GENERIC_ERROR);
		}
		return Constants.LONG_NO_RESULT;
	}

}
