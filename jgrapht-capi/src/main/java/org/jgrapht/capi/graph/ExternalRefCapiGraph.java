/*
 * (C) Copyright 2020-2021, by Dimitrios Michail.
 *
 * JGraphT C-API
 *
 * See the CONTRIBUTORS.md file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the
 * GNU Lesser General Public License v2.1 or later
 * which is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1-standalone.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR LGPL-2.1-or-later
 */
package org.jgrapht.capi.graph;

import java.util.HashMap;
import java.util.Map;

import org.graalvm.word.PointerBase;
import org.graalvm.word.WordFactory;
import org.jgrapht.Graph;

public class ExternalRefCapiGraph extends DefaultCapiGraph<ExternalRef, ExternalRef> {

	private static final long serialVersionUID = 1L;

	protected Map<ExternalRef, Long> vertexRefs;
	protected Map<ExternalRef, Long> edgeRefs;

	public ExternalRefCapiGraph(Graph<ExternalRef, ExternalRef> graph) {
		super(graph);
		this.vertexRefs = new HashMap<>();
		this.edgeRefs = new HashMap<>();
	}

	@Override
	public PointerBase getVertexAddress(ExternalRef v) {
		Long address = vertexRefs.get(v);
		if (address == null) {
			return WordFactory.nullPointer();
		} else {
			return WordFactory.pointer(address);
		}
	}

	@Override
	public PointerBase getEdgeAddress(ExternalRef e) {
		Long address = edgeRefs.get(e);
		if (address == null) {
			return WordFactory.nullPointer();
		} else {
			return WordFactory.pointer(address);
		}
	}

	@Override
	public ExternalRef addVertex() {
		ExternalRef v = super.addVertex();
		if (v != null) {
			vertexRefs.put(v, v.getPtr().rawValue());
		}
		return v;
	}

	@Override
	public boolean addVertex(ExternalRef v) {
		boolean added = super.addVertex(v);
		if (added) {
			vertexRefs.put(v, v.getPtr().rawValue());
		}
		return added;
	}

	@Override
	public ExternalRef addEdge(ExternalRef sourceVertex, ExternalRef targetVertex) {
		ExternalRef e = super.addEdge(sourceVertex, targetVertex);
		if (e != null) {
			edgeRefs.put(e, e.getPtr().rawValue());
		}
		return e;
	}

	@Override
	public boolean addEdge(ExternalRef sourceVertex, ExternalRef targetVertex, ExternalRef e) {
		boolean added = super.addEdge(sourceVertex, targetVertex, e);
		if (added) {
			edgeRefs.put(e, e.getPtr().rawValue());
		}
		return added;
	}

	@Override
	public ExternalRef removeEdge(ExternalRef sourceVertex, ExternalRef targetVertex) {
		ExternalRef e = super.getEdge(sourceVertex, targetVertex);
		if (e != null) {
			if (super.removeEdge(e)) {
				edgeRefs.remove(e);
			}
		}
		return e;
	}

	@Override
	public boolean removeEdge(ExternalRef e) {
		boolean modified = super.removeEdge(e);
		if (modified) {
			edgeRefs.remove(e);
		}
		return modified;
	}

	@Override
	public boolean removeVertex(ExternalRef v) {
		boolean modified = super.removeVertex(v);
		if (modified) {
			vertexRefs.remove(v);
		}
		return modified;
	}

}
