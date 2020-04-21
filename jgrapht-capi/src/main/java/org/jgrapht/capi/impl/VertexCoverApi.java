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

import java.util.Iterator;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.graalvm.nativeimage.IsolateThread;
import org.graalvm.nativeimage.ObjectHandle;
import org.graalvm.nativeimage.ObjectHandles;
import org.graalvm.nativeimage.c.function.CEntryPoint;
import org.graalvm.nativeimage.c.type.CDoublePointer;
import org.graalvm.nativeimage.c.type.WordPointer;
import org.jgrapht.Graph;
import org.jgrapht.alg.interfaces.VertexCoverAlgorithm;
import org.jgrapht.alg.interfaces.VertexCoverAlgorithm.VertexCover;
import org.jgrapht.alg.vertexcover.BarYehudaEvenTwoApproxVCImpl;
import org.jgrapht.alg.vertexcover.ClarksonTwoApproxVCImpl;
import org.jgrapht.alg.vertexcover.EdgeBasedTwoApproxVCImpl;
import org.jgrapht.alg.vertexcover.GreedyVCImpl;
import org.jgrapht.alg.vertexcover.RecursiveExactVCImpl;
import org.jgrapht.capi.Constants;
import org.jgrapht.capi.Status;
import org.jgrapht.capi.error.StatusReturnExceptionHandler;

public class VertexCoverApi {

	private static ObjectHandles globalHandles = ObjectHandles.getGlobal();

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "vertexcover_exec_greedy", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeVertexCoverGreedyWeighted(IsolateThread thread, ObjectHandle graphHandle,
			WordPointer res) {
		return executeVertexCover(thread, graphHandle, g -> new GreedyVCImpl<>(g), res);
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "vertexcover_exec_greedy_weighted", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeVertexCoverGreedyWeighted(IsolateThread thread, ObjectHandle graphHandle,
			ObjectHandle mapHandle, WordPointer res) {
		return executeVertexCoverWeighted(thread, graphHandle, mapHandle,
				(g, weights) -> new GreedyVCImpl<>(g, weights), res);
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "vertexcover_exec_clarkson", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeVertexCoverClarkson(IsolateThread thread, ObjectHandle graphHandle, WordPointer res) {
		return executeVertexCover(thread, graphHandle, g -> new ClarksonTwoApproxVCImpl<>(g), res);
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "vertexcover_exec_clarkson_weighted", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeVertexCoverClarksonWeighted(IsolateThread thread, ObjectHandle graphHandle,
			ObjectHandle mapHandle, WordPointer res) {
		return executeVertexCoverWeighted(thread, graphHandle, mapHandle,
				(g, weights) -> new ClarksonTwoApproxVCImpl<>(g, weights), res);
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "vertexcover_exec_edgebased", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeVertexCoverEdgeBased(IsolateThread thread, ObjectHandle graphHandle, WordPointer res) {
		return executeVertexCover(thread, graphHandle, g -> new EdgeBasedTwoApproxVCImpl<>(g), res);
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "vertexcover_exec_baryehudaeven", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeVertexCoverBarYehudaEven(IsolateThread thread, ObjectHandle graphHandle, WordPointer res) {
		return executeVertexCover(thread, graphHandle, g -> new BarYehudaEvenTwoApproxVCImpl<>(g), res);
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "vertexcover_exec_baryehudaeven_weighted", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeVertexCoverBarYehudaEvenWeighted(IsolateThread thread, ObjectHandle graphHandle,
			ObjectHandle mapHandle, WordPointer res) {
		return executeVertexCoverWeighted(thread, graphHandle, mapHandle,
				(g, weights) -> new BarYehudaEvenTwoApproxVCImpl<>(g, weights), res);
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "vertexcover_exec_exact", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeVertexCoverExact(IsolateThread thread, ObjectHandle graphHandle, WordPointer res) {
		return executeVertexCover(thread, graphHandle, g -> new RecursiveExactVCImpl<>(g), res);
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "vertexcover_exec_exact_weighted", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeVertexCoverExactWeighted(IsolateThread thread, ObjectHandle graphHandle,
			ObjectHandle mapHandle, WordPointer res) {
		return executeVertexCoverWeighted(thread, graphHandle, mapHandle,
				(g, weights) -> new RecursiveExactVCImpl<>(g, weights), res);
	}

	/**
	 * Get the weight of an vertex cover.
	 * 
	 * @param thread   the thread
	 * @param vcHandle the vertex cover handle
	 * @return the weight
	 */
	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "vertexcover_get_weight", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int getVertexCoverWeight(IsolateThread thread, ObjectHandle vcHandle, CDoublePointer res) {
		VertexCover<Long> vc = globalHandles.get(vcHandle);
		double result = vc.getWeight();
		if (res.isNonNull()) {
			res.write(result);
		}
		return Status.SUCCESS.toCEnum();
	}

	/**
	 * Get a vertex iterator for the vertex cover
	 * 
	 * @param thread   the thread
	 * @param vcHandle the vertex cover handle
	 * @return the vertex iterator
	 */
	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "vertexcover_create_vit", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int createMSTEdgeIterator(IsolateThread thread, ObjectHandle vcHandle, WordPointer res) {
		VertexCover<Long> vc = globalHandles.get(vcHandle);
		Iterator<Long> it = vc.iterator();
		if (res.isNonNull()) {
			res.write(globalHandles.create(it));
		}
		return Status.SUCCESS.toCEnum();
	}

	private static int executeVertexCover(IsolateThread thread, ObjectHandle graphHandle,
			Function<Graph<Long, Long>, VertexCoverAlgorithm<Long>> algProvider, WordPointer res) {
		Graph<Long, Long> g = globalHandles.get(graphHandle);
		VertexCoverAlgorithm<Long> alg = algProvider.apply(g);
		VertexCover<Long> vertexCover = alg.getVertexCover();
		if (res.isNonNull()) {
			res.write(globalHandles.create(vertexCover));
		}
		return Status.SUCCESS.toCEnum();
	}

	private static int executeVertexCoverWeighted(IsolateThread thread, ObjectHandle graphHandle,
			ObjectHandle mapHandle,
			BiFunction<Graph<Long, Long>, Map<Long, Double>, VertexCoverAlgorithm<Long>> algProvider, WordPointer res) {
		Graph<Long, Long> g = globalHandles.get(graphHandle);
		Map<Long, Double> vertexWeights = globalHandles.get(mapHandle);
		VertexCoverAlgorithm<Long> alg = algProvider.apply(g, vertexWeights);
		VertexCover<Long> vertexCover = alg.getVertexCover();
		if (res.isNonNull()) {
			res.write(globalHandles.create(vertexCover));
		}
		return Status.SUCCESS.toCEnum();
	}

}
