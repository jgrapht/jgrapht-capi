/*
 * (C) Copyright 2018, by Lukas Harzenetter and Contributors.
 *
 * JGraphT : a free Java graph-theory library
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

import org.jgrapht.Graph;
import org.jgrapht.GraphType;

public class CapiGraphAsUnweightedGraph<V, E> extends CapiGraphDelegator<V, E> implements CapiGraph<V, E> {

	private static final long serialVersionUID = 1L;
	private static final String EDGE_WEIGHT_IS_NOT_SUPPORTED = "Edge weight is not supported";

	public CapiGraphAsUnweightedGraph(CapiGraph<V, E> g) {
		super(g);
	}

	@Override
	public double getEdgeWeight(E e) {
		return Graph.DEFAULT_EDGE_WEIGHT;
	}

	@Override
	public void setEdgeWeight(E e, double weight) {
		throw new UnsupportedOperationException(EDGE_WEIGHT_IS_NOT_SUPPORTED);
	}

	@Override
	public GraphType getType() {
		return super.getType().asUnweighted();
	}

}