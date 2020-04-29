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
import org.jgrapht.capi.enums.Status;
import org.jgrapht.capi.error.StatusReturnExceptionHandler;

public class VertexCoverApi {

	private static ObjectHandles globalHandles = ObjectHandles.getGlobal();

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "vertexcover_exec_greedy", exceptionHandler = StatusReturnExceptionHandler.class)
	public static Status executeVertexCoverGreedyWeighted(IsolateThread thread, ObjectHandle graphHandle,
			CDoublePointer weightRes, WordPointer res) {
		return executeVertexCover(thread, graphHandle, g -> new GreedyVCImpl<>(g), weightRes, res);
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "vertexcover_exec_greedy_weighted", exceptionHandler = StatusReturnExceptionHandler.class)
	public static Status executeVertexCoverGreedyWeighted(IsolateThread thread, ObjectHandle graphHandle,
			ObjectHandle mapHandle, CDoublePointer weightRes, WordPointer res) {
		return executeVertexCoverWeighted(thread, graphHandle, mapHandle,
				(g, weights) -> new GreedyVCImpl<>(g, weights), weightRes, res);
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "vertexcover_exec_clarkson", exceptionHandler = StatusReturnExceptionHandler.class)
	public static Status executeVertexCoverClarkson(IsolateThread thread, ObjectHandle graphHandle,
			CDoublePointer weightRes, WordPointer res) {
		return executeVertexCover(thread, graphHandle, g -> new ClarksonTwoApproxVCImpl<>(g), weightRes, res);
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "vertexcover_exec_clarkson_weighted", exceptionHandler = StatusReturnExceptionHandler.class)
	public static Status executeVertexCoverClarksonWeighted(IsolateThread thread, ObjectHandle graphHandle,
			ObjectHandle mapHandle, CDoublePointer weightRes, WordPointer res) {
		return executeVertexCoverWeighted(thread, graphHandle, mapHandle,
				(g, weights) -> new ClarksonTwoApproxVCImpl<>(g, weights), weightRes, res);
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "vertexcover_exec_edgebased", exceptionHandler = StatusReturnExceptionHandler.class)
	public static Status executeVertexCoverEdgeBased(IsolateThread thread, ObjectHandle graphHandle,
			CDoublePointer weightRes, WordPointer res) {
		return executeVertexCover(thread, graphHandle, g -> new EdgeBasedTwoApproxVCImpl<>(g), weightRes, res);
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "vertexcover_exec_baryehudaeven", exceptionHandler = StatusReturnExceptionHandler.class)
	public static Status executeVertexCoverBarYehudaEven(IsolateThread thread, ObjectHandle graphHandle,
			CDoublePointer weightRes, WordPointer res) {
		return executeVertexCover(thread, graphHandle, g -> new BarYehudaEvenTwoApproxVCImpl<>(g), weightRes, res);
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "vertexcover_exec_baryehudaeven_weighted", exceptionHandler = StatusReturnExceptionHandler.class)
	public static Status executeVertexCoverBarYehudaEvenWeighted(IsolateThread thread, ObjectHandle graphHandle,
			ObjectHandle mapHandle, CDoublePointer weightRes, WordPointer res) {
		return executeVertexCoverWeighted(thread, graphHandle, mapHandle,
				(g, weights) -> new BarYehudaEvenTwoApproxVCImpl<>(g, weights), weightRes, res);
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "vertexcover_exec_exact", exceptionHandler = StatusReturnExceptionHandler.class)
	public static Status executeVertexCoverExact(IsolateThread thread, ObjectHandle graphHandle, CDoublePointer weightRes,
			WordPointer res) {
		return executeVertexCover(thread, graphHandle, g -> new RecursiveExactVCImpl<>(g), weightRes, res);
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "vertexcover_exec_exact_weighted", exceptionHandler = StatusReturnExceptionHandler.class)
	public static Status executeVertexCoverExactWeighted(IsolateThread thread, ObjectHandle graphHandle,
			ObjectHandle mapHandle, CDoublePointer weightRes, WordPointer res) {
		return executeVertexCoverWeighted(thread, graphHandle, mapHandle,
				(g, weights) -> new RecursiveExactVCImpl<>(g, weights), weightRes, res);
	}

	private static Status executeVertexCover(IsolateThread thread, ObjectHandle graphHandle,
			Function<Graph<Long, Long>, VertexCoverAlgorithm<Long>> algProvider, CDoublePointer weightRes,
			WordPointer res) {
		Graph<Long, Long> g = globalHandles.get(graphHandle);
		VertexCoverAlgorithm<Long> alg = algProvider.apply(g);
		VertexCover<Long> vertexCover = alg.getVertexCover();
		if (weightRes.isNonNull()) {
			weightRes.write(vertexCover.getWeight());
		}
		if (res.isNonNull()) {
			res.write(globalHandles.create(vertexCover));
		}
		return Status.SUCCESS;
	}

	private static Status executeVertexCoverWeighted(IsolateThread thread, ObjectHandle graphHandle,
			ObjectHandle mapHandle,
			BiFunction<Graph<Long, Long>, Map<Long, Double>, VertexCoverAlgorithm<Long>> algProvider,
			CDoublePointer weightRes, WordPointer res) {
		Graph<Long, Long> g = globalHandles.get(graphHandle);
		Map<Long, Double> vertexWeights = globalHandles.get(mapHandle);
		VertexCoverAlgorithm<Long> alg = algProvider.apply(g, vertexWeights);
		VertexCover<Long> vertexCover = alg.getVertexCover();
		if (weightRes.isNonNull()) {
			weightRes.write(vertexCover.getWeight());
		}
		if (res.isNonNull()) {
			res.write(globalHandles.create(vertexCover));
		}
		return Status.SUCCESS;
	}

}
