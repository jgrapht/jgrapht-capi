/*
 * (C) Copyright 2020, by Dimitrios Michail.
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

import java.util.function.Supplier;

import org.jgrapht.Graph;

public class SafeLongVertexSupplier implements Supplier<Long> {

	private long nextVertex;
	private Graph<Long, Long> graph;

	public SafeLongVertexSupplier() {
		this.nextVertex = 0;
		this.graph = null;
	}

	public Graph<Long, Long> getGraph() {
		return graph;
	}

	public void setGraph(Graph<Long, Long> graph) {
		this.graph = graph;
	}

	public long getNextVertex() {
		return nextVertex;
	}

	public void setNextVertex(long nextVertex) {
		this.nextVertex = nextVertex;
	}

	@Override
	public Long get() {
		while (true) {
			long candidate = nextVertex++;
			if (!graph.containsVertex(candidate)) {
				return candidate;
			}
		}
	}

}
