package org.jgrapht.capi.graph;

import java.util.function.Supplier;

import org.jgrapht.Graph;

public class SafeEdgeSupplier implements Supplier<Long> {

	private long nextEdge;
	private Graph<Long, Long> graph;

	public SafeEdgeSupplier() {
		this.nextEdge = 0;
		this.graph = null;
	}

	public Graph<Long, Long> getGraph() {
		return graph;
	}

	public void setGraph(Graph<Long, Long> graph) {
		this.graph = graph;
	}

	public long getNextEdge() {
		return nextEdge;
	}

	public void setNextEdge(long nextEdge) {
		this.nextEdge = nextEdge;
	}

	@Override
	public Long get() {
		while (true) {
			long candidate = nextEdge++;
			if (!graph.containsEdge(candidate)) {
				return candidate;
			}
		}
	}

}
