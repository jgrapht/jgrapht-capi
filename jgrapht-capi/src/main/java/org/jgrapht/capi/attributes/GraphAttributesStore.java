package org.jgrapht.capi.attributes;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jgrapht.nio.Attribute;

/**
 * Class for storing vertex, edge and graph attributes.
 */
public class GraphAttributesStore<V, E> {

	private Map<V, Map<String, Attribute>> vertexAttributes;
	private Map<E, Map<String, Attribute>> edgeAttributes;
	private Map<String, Attribute> graphAttributes;

	public GraphAttributesStore() {
		this.vertexAttributes = new HashMap<>();
		this.edgeAttributes = new HashMap<>();
		this.graphAttributes = new HashMap<>();
	}

	public Attribute getVertexAttribute(V element, String name) {
		return getSafeVertexMap(element).get(name);
	}

	public Attribute getEdgeAttribute(E element, String name) {
		return getSafeEdgeMap(element).get(name);
	}

	public Attribute getGraphAttribute(String name) {
		return graphAttributes.get(name);
	}

	public void putVertexAttribute(V element, String name, Attribute value) {
		getSafeVertexMap(element).put(name, value);
	}

	public void putEdgeAttribute(E element, String name, Attribute value) {
		getSafeEdgeMap(element).put(name, value);
	}

	public void putGraphAttribute(String name, Attribute value) {
		graphAttributes.put(name, value);
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

	private Map<String, Attribute> getSafeVertexMap(V element) {
		Map<String, Attribute> attrs = vertexAttributes.get(element);
		if (attrs == null) {
			attrs = new LinkedHashMap<>();
			vertexAttributes.put(element, attrs);
		}
		return attrs;
	}

	private Map<String, Attribute> getSafeEdgeMap(E element) {
		Map<String, Attribute> attrs = edgeAttributes.get(element);
		if (attrs == null) {
			attrs = new LinkedHashMap<>();
			edgeAttributes.put(element, attrs);
		}
		return attrs;
	}

}
