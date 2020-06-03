package org.jgrapht.capi.graph;

import org.jgrapht.Graph;

public interface CapiGraphWrapper<V,E> {

	Graph<V, E> getWrappedGraph();
	
}
