package org.jgrapht.capi.graph;

import java.util.function.Supplier;

import org.jgrapht.Graph;

public class SafeVertexSupplier implements Supplier<Long> {

	private long nextVertex;
	private Graph<Long, Long> graph;

	public SafeVertexSupplier() {
		this.nextVertex = 0;
		this.graph = null;
	}

	public Graph<Long, Long> getGraph() {
		return graph;
	}

	public void setGraph(Graph<Long, Long> graph) {
		this.graph = graph;
	}

	public long getNextVertex() {
		return nextVertex;
	}

	public void setNextVertex(long nextVertex) {
		this.nextVertex = nextVertex;
	}

	@Override
	public Long get() {
		while (true) {
			long candidate = nextVertex++;
			if (!graph.containsVertex(candidate)) {
				return candidate;
			}
		}
	}

}
