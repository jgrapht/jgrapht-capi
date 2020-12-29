package org.jgrapht.capi.graph;

import java.util.Iterator;

import org.graalvm.nativeimage.c.type.CTypeConversion.CCharPointerHolder;
import org.jgrapht.Graph;
import org.jgrapht.capi.attributes.GraphAttributesStore;
import org.jgrapht.nio.Attribute;

/**
 * A graph which also has attributes.
 */
public interface GraphWithAttributes<V, E> extends Graph<V, E> {

	abstract GraphAttributesStore<V, E> getStore();

	default int getGraphAttributesSize() { 
		return getStore().getGraphAttributesSize();
	}
	
	default int getVertexAttributesSize(V element) { 
		if (!containsVertex(element)) {
			throw new IllegalArgumentException("no such vertex in graph: " + element.toString());
		}
		return getStore().getVertexAttributesSize(element);
	}
	
	default int getEdgeAttributesSize(E element) { 
		if (!containsEdge(element)) {
			throw new IllegalArgumentException("no such edge in graph: " + element.toString());
		}
		return getStore().getEdgeAttributesSize(element);
	}
	
	default Iterator<CCharPointerHolder> graphAttributesKeysIterator() { 
		return getStore().graphAttributesKeysIterator();
	}
	
	default Iterator<CCharPointerHolder> vertexAttributesKeysIterator(V element) {
		if (!containsVertex(element)) {
			throw new IllegalArgumentException("no such vertex in graph: " + element.toString());
		}		
		return getStore().vertexAttributesKeysIterator(element);
	}
	
	default Iterator<CCharPointerHolder> edgeAttributesKeysIterator(E element) {
		if (!containsEdge(element)) {
			throw new IllegalArgumentException("no such edge in graph: " + element.toString());
		}		
		return getStore().edgeAttributesKeysIterator(element);
	}
	
	default Attribute getVertexAttribute(V element, String name) {
		if (!containsVertex(element)) {
			throw new IllegalArgumentException("no such vertex in graph: " + element.toString());
		}
		return getStore().getVertexAttribute(element, name);
	}

	default Attribute getEdgeAttribute(E element, String name) {
		if (!containsEdge(element)) {
			throw new IllegalArgumentException("no such edge in graph: " + element.toString());
		}
		return getStore().getEdgeAttribute(element, name);
	}

	default Attribute getGraphAttribute(String name) {
		return getStore().getGraphAttribute(name);
	}

	default void putVertexAttribute(V element, String name, Attribute value) {
		if (!containsVertex(element)) {
			throw new IllegalArgumentException("no such vertex in graph: " + element.toString());
		}
		getStore().putVertexAttribute(element, name, value);
	}

	default void putEdgeAttribute(E element, String name, Attribute value) {
		if (!containsEdge(element)) {
			throw new IllegalArgumentException("no such edge in graph: " + element.toString());
		}
		getStore().putEdgeAttribute(element, name, value);
	}

	default void putGraphAttribute(String name, Attribute value) {
		getStore().putGraphAttribute(name, value);
	}

	default void removeVertexAttribute(V element, String name) {
		if (!containsVertex(element)) {
			throw new IllegalArgumentException("no such vertex in graph: " + element.toString());
		}
		getStore().removeVertexAttribute(element, name);
	}

	default void removeEdgeAttribute(E element, String name) {
		if (!containsEdge(element)) {
			throw new IllegalArgumentException("no such edge in graph: " + element.toString());
		}
		getStore().removeEdgeAttribute(element, name);
	}

	default void removeGraphAttribute(String name) {
		getStore().removeGraphAttribute(name);
	}

	default void clearVertexAttributes(V vertex) {
		getStore().clearVertexAttributes(vertex);
	}

	default void clearEdgeAttributes(E edge) {
		getStore().clearEdgeAttributes(edge);
	}

	default void clearGraphAttributes() {
		getStore().clearGraphAttributes();
	}

}
