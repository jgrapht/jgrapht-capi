package org.jgrapht.capi.graph;

import org.jgrapht.Graph;
import org.jgrapht.graph.AsUndirectedGraph;

public class CapiAsUndirectedGraph extends AsUndirectedGraph<Integer, Integer> 
implements CapiGraphWrapper<Integer, Integer> 
{

	private static final long serialVersionUID = 1L;
	
	private Graph<Integer, Integer> wrappedGraph;
	
	public CapiAsUndirectedGraph(Graph<Integer, Integer> g) {
		super(g);
		this.wrappedGraph = g;
	}

	@Override
	public Graph<Integer, Integer> getWrappedGraph() {
		return wrappedGraph;
	}

}
