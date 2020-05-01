package org.jgrapht.capi.attributes;

import java.util.HashMap;
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
		return getMap(element).get(name);
	}

	public void putAttribute(long element, String name, Attribute value) {
		getMap(element).put(name, value);
	}

	public void removeAttribute(long element, String name) {
		getMap(element).remove(name);
	}

	private Map<String, Attribute> getMap(long element) {
		Map<String, Attribute> attrs = attributes.get(element);
		if (attrs == null) {
			attrs = new HashMap<>();
			attributes.put(element, attrs);
		}
		return attrs;
	}

}
