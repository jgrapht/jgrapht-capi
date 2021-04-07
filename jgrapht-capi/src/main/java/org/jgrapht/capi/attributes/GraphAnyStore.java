package org.jgrapht.capi.attributes;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jgrapht.capi.graph.ExternalRef;

/**
 * Class for storing vertex, edge and graph attributes with external references
 * for keys and values.
 */
public class GraphAnyStore<V, E> {

	private Map<V, Map<ExternalRef, ExternalRef>> vertexAttributes;
	private Map<E, Map<ExternalRef, ExternalRef>> edgeAttributes;
	private Map<ExternalRef, ExternalRef> graphAttributes;

	public GraphAnyStore() {
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

	public Iterator<ExternalRef> graphAttributesKeysIterator() {
		return graphAttributes.keySet().stream().iterator();
	}

	public Iterator<ExternalRef> vertexAttributesKeysIterator(V element) {
		return getSafeVertexMap(element).keySet().iterator();
	}

	public Iterator<ExternalRef> edgeAttributesKeysIterator(E element) {
		return getSafeEdgeMap(element).keySet().iterator();
	}

	public ExternalRef getVertexAttribute(V element, ExternalRef key) {
		return getSafeVertexMap(element).get(key);
	}

	public ExternalRef getEdgeAttribute(E element, ExternalRef key) {
		return getSafeEdgeMap(element).get(key);
	}

	public ExternalRef getGraphAttribute(ExternalRef key) {
		return graphAttributes.get(key);
	}

	public ExternalRef putVertexAttribute(V element, ExternalRef key, ExternalRef value) {
		return getSafeVertexMap(element).put(key, value);
	}

	public ExternalRef putEdgeAttribute(E element, ExternalRef key, ExternalRef value) {
		return getSafeEdgeMap(element).put(key, value);
	}

	public ExternalRef putGraphAttribute(ExternalRef key, ExternalRef value) {
		return graphAttributes.put(key, value);
	}

	public ExternalRef removeVertexAttribute(V element, ExternalRef key) {
		return getSafeVertexMap(element).remove(key);
	}

	public ExternalRef removeEdgeAttribute(E element, ExternalRef key) {
		return getSafeEdgeMap(element).remove(key);
	}

	public ExternalRef removeGraphAttribute(ExternalRef name) {
		return graphAttributes.remove(name);
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

	private Map<ExternalRef, ExternalRef> getSafeVertexMap(V element) {
		Map<ExternalRef, ExternalRef> attrs = vertexAttributes.get(element);
		if (attrs == null) {
			attrs = new LinkedHashMap<>();
			vertexAttributes.put(element, attrs);
		}
		return attrs;
	}

	private Map<ExternalRef, ExternalRef> getSafeEdgeMap(E element) {
		Map<ExternalRef, ExternalRef> attrs = edgeAttributes.get(element);
		if (attrs == null) {
			attrs = new LinkedHashMap<>();
			edgeAttributes.put(element, attrs);
		}
		return attrs;
	}

}
