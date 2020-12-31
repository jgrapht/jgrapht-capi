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

public class SafeEdgeSupplier implements Supplier<Integer> {

	private int nextEdge;
	private Graph<Integer, Integer> graph;

	public SafeEdgeSupplier() {
		this.nextEdge = 0;
		this.graph = null;
	}

	public Graph<Integer, Integer> getGraph() {
		return graph;
	}

	public void setGraph(Graph<Integer, Integer> graph) {
		this.graph = graph;
	}

	public int getNextEdge() {
		return nextEdge;
	}

	public void setNextEdge(int nextEdge) {
		this.nextEdge = nextEdge;
	}

	@Override
	public Integer get() {
		while (true) {
			int candidate = nextEdge++;
			if (!graph.containsEdge(candidate)) {
				return candidate;
			}
		}
	}

}
