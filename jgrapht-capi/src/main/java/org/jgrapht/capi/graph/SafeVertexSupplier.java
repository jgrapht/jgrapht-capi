package org.jgrapht.capi.graph;

import java.util.function.Supplier;

import org.jgrapht.Graph;

public class SafeVertexSupplier implements Supplier<Integer> {

	private int nextVertex;
	private Graph<Integer, Integer> graph;

	public SafeVertexSupplier() {
		this.nextVertex = 0;
		this.graph = null;
	}

	public Graph<Integer, Integer> getGraph() {
		return graph;
	}

	public void setGraph(Graph<Integer, Integer> graph) {
		this.graph = graph;
	}

	public int getNextVertex() {
		return nextVertex;
	}

	public void setNextVertex(int nextVertex) {
		this.nextVertex = nextVertex;
	}

	@Override
	public Integer get() {
		while (true) {
			int candidate = nextVertex++;
			if (!graph.containsVertex(candidate)) {
				return candidate;
			}
		}
	}

}
