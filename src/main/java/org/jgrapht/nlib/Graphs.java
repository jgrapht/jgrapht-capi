package org.jgrapht.nlib;

import java.util.ArrayList;
import java.util.List;

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
	public static Graph<Long, Long> getGraph(WordBase graphHandle) {
		int graphId = Long.valueOf(graphHandle.rawValue()).intValue();
		try {
			Graph<Long, Long> graph = graphs.get(graphId);
			if (graph == null) {
				throw new GraphLookupException();
			}
			return graph;
		} catch (IndexOutOfBoundsException e) {
			throw new GraphLookupException();
		}
	}

	/**
	 * Create a graph and return its handle.
	 * 
	 * @return the graph handle
	 */
	public static WordBase createGraph() {
		Graph<Long, Long> graph = GraphTypeBuilder.directed().allowingMultipleEdges(true).allowingSelfLoops(true)
				.vertexSupplier(SupplierUtil.createLongSupplier()).edgeSupplier(SupplierUtil.createLongSupplier())
				.buildGraph();
		int id = graphs.size();
		graphs.add(graph);
		return WordFactory.signed(id);
	}

	/**
	 * Delete a graph
	 * 
	 * @param graphHandle the graph handle
	 */
	public static void deleteGraph(WordBase graphHandle) {
		try {
			int graphId = Long.valueOf(graphHandle.rawValue()).intValue();
			graphs.set(graphId, null);
		} catch (IndexOutOfBoundsException e) {
			throw new GraphLookupException();
		}
	}

}
