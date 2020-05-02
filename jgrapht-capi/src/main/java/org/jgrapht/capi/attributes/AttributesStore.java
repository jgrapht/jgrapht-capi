package org.jgrapht.capi.attributes;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jgrapht.nio.Attribute;

/**
 * Class for storing vertex and edge attributes. Mostly used for the exporters
 * in order to read vertex and edge attributes.
 */
public class AttributesStore {

	private Map<Long, Map<String, Attribute>> attributes;

	public AttributesStore() {
		this.attributes = new HashMap<>();
	}

	public Attribute getAttribute(long element, String name) {
		return getSafeMap(element).get(name);
	}

	public void putAttribute(long element, String name, Attribute value) {
		getSafeMap(element).put(name, value);
	}

	public void removeAttribute(long element, String name) {
		getSafeMap(element).remove(name);
	}

	public Map<String, Attribute> getAttributes(long element) {
		return Collections.unmodifiableMap(getSafeMap(element));
	}

	private Map<String, Attribute> getSafeMap(long element) {
		Map<String, Attribute> attrs = attributes.get(element);
		if (attrs == null) {
			attrs = new LinkedHashMap<>();
			attributes.put(element, attrs);
		}
		return attrs;
	}

}
