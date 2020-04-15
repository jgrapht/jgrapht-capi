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

public class Shared {

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
	@CEntryPoint(name = "create_graph")
	public static WordBase createGraph(IsolateThread thread) {
		Graph<Long, Long> graph = GraphTypeBuilder.directed().allowingMultipleEdges(true).allowingSelfLoops(true)
				.vertexSupplier(SupplierUtil.createLongSupplier()).edgeSupplier(SupplierUtil.createLongSupplier())
				.buildGraph();
		graphs.add(graph);
		int id = graphs.size();
		return WordFactory.signed(id);
	}

	/**
	 * Delete a graph
	 * 
	 * @param thread the thread isolate
	 * @return the graph handle
	 */
	@CEntryPoint(name = "delete_graph")
	public static void deleteGraph(IsolateThread thread, WordBase graphHandle) {
		int graphId = Long.valueOf(graphHandle.rawValue()).intValue();
		graphs.set(graphId, null);
	}

	@CEntryPoint(name = "add_vertex")
	public static long addVertex(IsolateThread thread, WordBase graphHandle) {
		return getGraph(graphHandle).addVertex();
	}

	@CEntryPoint(name = "add_edge")
	public static long addEdge(IsolateThread thread, WordBase graphHandle, long source, long target) {
		return getGraph(graphHandle).addEdge(source, target);
	}

}
