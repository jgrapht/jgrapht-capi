package org.jgrapht.capi.graph;

import java.util.Iterator;
import java.util.Set;

public interface DirectedAcyclicGraphWithAttributes<V, E> extends GraphWithAttributes<V, E> {

	Set<V> getAncestors(V vertex);

	Set<V> getDescendants(V vertex);

	Iterator<V> iterator();

}
