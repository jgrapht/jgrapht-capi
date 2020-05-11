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
package org.jgrapht.capi.impl;

import org.graalvm.nativeimage.IsolateThread;
import org.graalvm.nativeimage.ObjectHandle;
import org.graalvm.nativeimage.ObjectHandles;
import org.graalvm.nativeimage.c.function.CEntryPoint;
import org.graalvm.nativeimage.c.type.CDoublePointer;
import org.graalvm.nativeimage.c.type.WordPointer;
import org.jgrapht.Graph;
import org.jgrapht.alg.interfaces.SpanningTreeAlgorithm.SpanningTree;
import org.jgrapht.alg.spanning.BoruvkaMinimumSpanningTree;
import org.jgrapht.alg.spanning.KruskalMinimumSpanningTree;
import org.jgrapht.alg.spanning.PrimMinimumSpanningTree;
import org.jgrapht.capi.Constants;
import org.jgrapht.capi.JGraphTContext.Status;
import org.jgrapht.capi.error.StatusReturnExceptionHandler;

public class MstApi {

	private static ObjectHandles globalHandles = ObjectHandles.getGlobal();

	/**
	 * Execute MST kruskal on a graph
	 * 
	 * @param thread      the thread
	 * @param graphHandle the graph handle
	 * @return a handle on the result
	 */
	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "mst_exec_kruskal", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeMSTKruskal(IsolateThread thread, ObjectHandle graph, CDoublePointer weightRes,
			WordPointer res) {
		Graph<Integer, Integer> g = globalHandles.get(graph);
		SpanningTree<Integer> mst = new KruskalMinimumSpanningTree<>(g).getSpanningTree();
		if (weightRes.isNonNull()) {
			weightRes.write(mst.getWeight());
		}
		if (res.isNonNull()) {
			res.write(globalHandles.create(mst.getEdges()));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	/**
	 * Execute MST Prim on a graph
	 * 
	 * @param thread      the thread
	 * @param graphHandle the graph handle
	 * @return a handle on the result
	 */
	@CEntryPoint(name = Constants.LIB_PREFIX + "mst_exec_prim", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeMSTPrim(IsolateThread thread, ObjectHandle graph, CDoublePointer weightRes,
			WordPointer res) {
		Graph<Integer, Integer> g = globalHandles.get(graph);
		SpanningTree<Integer> mst = new PrimMinimumSpanningTree<>(g).getSpanningTree();
		if (weightRes.isNonNull()) {
			weightRes.write(mst.getWeight());
		}
		if (res.isNonNull()) {
			res.write(globalHandles.create(mst.getEdges()));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	/**
	 * Execute MST Boruvka on a graph
	 * 
	 * @param thread      the thread
	 * @param graphHandle the graph handle
	 * @return a handle on the result
	 */
	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "mst_exec_boruvka", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeMSTBoruvka(IsolateThread thread, ObjectHandle graph, CDoublePointer weightRes,
			WordPointer res) {
		Graph<Integer, Integer> g = globalHandles.get(graph);
		SpanningTree<Integer> mst = new BoruvkaMinimumSpanningTree<>(g).getSpanningTree();
		if (weightRes.isNonNull()) {
			weightRes.write(mst.getWeight());
		}
		if (res.isNonNull()) {
			res.write(globalHandles.create(mst.getEdges()));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

}
