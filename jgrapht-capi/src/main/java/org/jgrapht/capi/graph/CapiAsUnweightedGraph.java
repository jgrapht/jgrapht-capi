package org.jgrapht.capi.graph;

import org.jgrapht.Graph;
import org.jgrapht.graph.AsUnweightedGraph;

public class CapiAsUnweightedGraph extends AsUnweightedGraph<Integer, Integer> 
implements CapiGraphWrapper<Integer, Integer> 
{

	private static final long serialVersionUID = 1L;
	
	private Graph<Integer, Integer> wrappedGraph;
	
	public CapiAsUnweightedGraph(Graph<Integer, Integer> g) {
		super(g);
		this.wrappedGraph = g;
	}

	@Override
	public Graph<Integer, Integer> getWrappedGraph() {
		return wrappedGraph;
	}

}
