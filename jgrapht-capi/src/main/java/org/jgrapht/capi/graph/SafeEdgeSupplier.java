package org.jgrapht.capi.graph;

import java.util.function.Supplier;

import org.jgrapht.Graph;

public class SafeEdgeSupplier implements Supplier<Integer> {

	private int nextEdge;
	private Graph<Integer, Integer> graph;

	public SafeEdgeSupplier() {
		this.nextEdge = 0;
		this.graph = null;
	}

	public Graph<Integer, Integer> getGraph() {
		return graph;
	}

	public void setGraph(Graph<Integer, Integer> graph) {
		this.graph = graph;
	}

	public int getNextEdge() {
		return nextEdge;
	}

	public void setNextEdge(int nextEdge) {
		this.nextEdge = nextEdge;
	}

	@Override
	public Integer get() {
		while (true) {
			int candidate = nextEdge++;
			if (!graph.containsEdge(candidate)) {
				return candidate;
			}
		}
	}

}
