package org.jgrapht.capi.graph;

import java.util.Iterator;
import java.util.Set;

import org.jgrapht.ListenableGraph;

/**
 * One interface which incorporates all possible JGraphT interfaces. If
 * something is not supported, we throw an UnsupportedOperationException.
 */
public interface CapiGraph<V, E> extends GraphWithAnyStore<V, E>, ListenableGraph<V, E>, HashAndEqualsResolver {

	Set<V> getAncestors(V vertex);

	Set<V> getDescendants(V vertex);

	Iterator<V> iterator();

}
