package org.jgrapht.capi.attributes;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Class for storing vertex, edge and graph attributes with external references
 * for keys and values.
 */
public class GraphAnyAnyStore<V, E, KK, VV> {

	private Map<V, Map<KK, VV>> vertexAttributes;
	private Map<E, Map<KK, VV>> edgeAttributes;
	private Map<KK, VV> graphAttributes;

	public GraphAnyAnyStore() {
		this.vertexAttributes = new HashMap<>();
		this.edgeAttributes = new HashMap<>();
		this.graphAttributes = new HashMap<>();
	}

	public int getGraphAttributesSize() {
		return graphAttributes.size();
	}

	public int getVertexAttributesSize(V element) {
		return getSafeVertexMap(element).size();
	}

	public int getEdgeAttributesSize(E element) {
		return getSafeEdgeMap(element).size();
	}

	public Iterator<KK> graphAttributesKeysIterator() {
		return graphAttributes.keySet().stream().iterator();
	}

	public Iterator<KK> vertexAttributesKeysIterator(V element) {
		return getSafeVertexMap(element).keySet().iterator();
	}

	public Iterator<KK> edgeAttributesKeysIterator(E element) {
		return getSafeEdgeMap(element).keySet().iterator();
	}

	public VV getVertexAttribute(V element, String name) {
		return getSafeVertexMap(element).get(name);
	}

	public VV getEdgeAttribute(E element, String name) {
		return getSafeEdgeMap(element).get(name);
	}

	public VV getGraphAttribute(String name) {
		return graphAttributes.get(name);
	}

	public void putVertexAttribute(V element, KK key, VV value) {
		getSafeVertexMap(element).put(key, value);
	}

	public void putEdgeAttribute(E element, KK key, VV value) {
		getSafeEdgeMap(element).put(key, value);
	}

	public void putGraphAttribute(KK key, VV value) {
		graphAttributes.put(key, value);
	}

	public void removeVertexAttribute(V element, String name) {
		getSafeVertexMap(element).remove(name);
	}

	public void removeEdgeAttribute(E element, String name) {
		getSafeEdgeMap(element).remove(name);
	}

	public void removeGraphAttribute(String name) {
		graphAttributes.remove(name);
	}

	public void clearVertexAttributes(V element) {
		vertexAttributes.remove(element);
	}

	public void clearEdgeAttributes(E element) {
		edgeAttributes.remove(element);
	}

	public void clearAllVerticesAttributes() {
		vertexAttributes.clear();
	}

	public void clearAllEdgesAttributes() {
		edgeAttributes.clear();
	}

	public void clearGraphAttributes() {
		graphAttributes.clear();
	}

	private Map<KK, VV> getSafeVertexMap(V element) {
		Map<KK, VV> attrs = vertexAttributes.get(element);
		if (attrs == null) {
			attrs = new LinkedHashMap<>();
			vertexAttributes.put(element, attrs);
		}
		return attrs;
	}

	private Map<KK, VV> getSafeEdgeMap(E element) {
		Map<KK, VV> attrs = edgeAttributes.get(element);
		if (attrs == null) {
			attrs = new LinkedHashMap<>();
			edgeAttributes.put(element, attrs);
		}
		return attrs;
	}

}
