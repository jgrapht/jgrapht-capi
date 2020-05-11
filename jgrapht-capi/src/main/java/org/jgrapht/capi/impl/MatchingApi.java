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

import java.util.Set;
import java.util.function.Function;

import org.graalvm.nativeimage.IsolateThread;
import org.graalvm.nativeimage.ObjectHandle;
import org.graalvm.nativeimage.ObjectHandles;
import org.graalvm.nativeimage.c.function.CEntryPoint;
import org.graalvm.nativeimage.c.type.CDoublePointer;
import org.graalvm.nativeimage.c.type.WordPointer;
import org.jgrapht.Graph;
import org.jgrapht.alg.interfaces.MatchingAlgorithm;
import org.jgrapht.alg.interfaces.MatchingAlgorithm.Matching;
import org.jgrapht.alg.interfaces.PartitioningAlgorithm.Partitioning;
import org.jgrapht.alg.matching.DenseEdmondsMaximumCardinalityMatching;
import org.jgrapht.alg.matching.GreedyMaximumCardinalityMatching;
import org.jgrapht.alg.matching.GreedyWeightedMatching;
import org.jgrapht.alg.matching.HopcroftKarpMaximumCardinalityBipartiteMatching;
import org.jgrapht.alg.matching.KuhnMunkresMinimalWeightBipartitePerfectMatching;
import org.jgrapht.alg.matching.MaximumWeightBipartiteMatching;
import org.jgrapht.alg.matching.PathGrowingWeightedMatching;
import org.jgrapht.alg.matching.SparseEdmondsMaximumCardinalityMatching;
import org.jgrapht.alg.matching.blossom.v5.KolmogorovWeightedMatching;
import org.jgrapht.alg.matching.blossom.v5.KolmogorovWeightedPerfectMatching;
import org.jgrapht.alg.matching.blossom.v5.ObjectiveSense;
import org.jgrapht.alg.partition.BipartitePartitioning;
import org.jgrapht.capi.Constants;
import org.jgrapht.capi.JGraphTContext.Status;
import org.jgrapht.capi.error.StatusReturnExceptionHandler;

public class MatchingApi {

	private static ObjectHandles globalHandles = ObjectHandles.getGlobal();

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "matching_exec_greedy_general_max_card", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeGreedyMaximumCardinality(IsolateThread thread, ObjectHandle graphHandle,
			CDoublePointer weightRes, WordPointer res) {
		return exec(thread, graphHandle, weightRes, res, (g) -> new GreedyMaximumCardinalityMatching<>(g, false));
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "matching_exec_custom_greedy_general_max_card", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeCustomGreedyMaximumCardinality(IsolateThread thread, ObjectHandle graphHandle,
			boolean sort, CDoublePointer weightRes, WordPointer res) {
		return exec(thread, graphHandle, weightRes, res, (g) -> new GreedyMaximumCardinalityMatching<>(g, sort));
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "matching_exec_edmonds_general_max_card_dense", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeEdmondsMCMDense(IsolateThread thread, ObjectHandle graphHandle, CDoublePointer weightRes,
			WordPointer res) {
		return exec(thread, graphHandle, weightRes, res, (g) -> new DenseEdmondsMaximumCardinalityMatching<>(g));
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "matching_exec_edmonds_general_max_card_sparse", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeEdmondsMCMSparse(IsolateThread thread, ObjectHandle graphHandle, CDoublePointer weightRes,
			WordPointer res) {
		return exec(thread, graphHandle, weightRes, res, (g) -> new SparseEdmondsMaximumCardinalityMatching<>(g));
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "matching_exec_greedy_general_max_weight", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeGreedyMaximumWeightedMatching(IsolateThread thread, ObjectHandle graphHandle,
			CDoublePointer weightRes, WordPointer res) {
		return exec(thread, graphHandle, weightRes, res, (g) -> new GreedyWeightedMatching<>(g, false));
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "matching_exec_custom_greedy_general_max_weight", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeCustomGreedyMaximumWeightedMatching(IsolateThread thread, ObjectHandle graphHandle,
			boolean normalizeEdgeCosts, double epsilon, CDoublePointer weightRes, WordPointer res) {
		return exec(thread, graphHandle, weightRes, res,
				(g) -> new GreedyWeightedMatching<>(g, normalizeEdgeCosts, epsilon));
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "matching_exec_pathgrowing_max_weight", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executePathGrowingMaximumWeightedMatching(IsolateThread thread, ObjectHandle graphHandle,
			CDoublePointer weightRes, WordPointer res) {
		return exec(thread, graphHandle, weightRes, res, (g) -> new PathGrowingWeightedMatching<>(g, true));
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "matching_exec_blossom5_general_max_weight", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeBlossom5MaxWeight(IsolateThread thread, ObjectHandle graphHandle, CDoublePointer weightRes,
			WordPointer res) {
		return exec(thread, graphHandle, weightRes, res,
				(g) -> new KolmogorovWeightedMatching<>(g, ObjectiveSense.MAXIMIZE));
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "matching_exec_blossom5_general_min_weight", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeBlossom5MinWeight(IsolateThread thread, ObjectHandle graphHandle, CDoublePointer weightRes,
			WordPointer res) {
		return exec(thread, graphHandle, weightRes, res,
				(g) -> new KolmogorovWeightedMatching<>(g, ObjectiveSense.MINIMIZE));
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "matching_exec_blossom5_general_perfect_max_weight", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeBlossom5PerfectMaxWeight(IsolateThread thread, ObjectHandle graphHandle,
			CDoublePointer weightRes, WordPointer res) {
		return exec(thread, graphHandle, weightRes, res,
				(g) -> new KolmogorovWeightedPerfectMatching<>(g, ObjectiveSense.MAXIMIZE));
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "matching_exec_blossom5_general_perfect_min_weight", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeBlossom5PerfectMinWeight(IsolateThread thread, ObjectHandle graphHandle,
			CDoublePointer weightRes, WordPointer res) {
		return exec(thread, graphHandle, weightRes, res,
				(g) -> new KolmogorovWeightedPerfectMatching<>(g, ObjectiveSense.MINIMIZE));
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "matching_exec_bipartite_max_card", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeBipartiteMaximumCardinalityMatching(IsolateThread thread, ObjectHandle graphHandle,
			CDoublePointer weightRes, WordPointer res) {
		Graph<Integer, Integer> g = globalHandles.get(graphHandle);
		BipartitePartitioning<Integer, Integer> partAlg = new BipartitePartitioning<>(g);
		if (!partAlg.isBipartite()) {
			throw new IllegalArgumentException("Graph is not bipartite");
		}
		Partitioning<Integer> part = partAlg.getPartitioning();
		Set<Integer> part0 = part.getPartition(0);
		Set<Integer> part1 = part.getPartition(1);
		MatchingAlgorithm<Integer, Integer> alg = new HopcroftKarpMaximumCardinalityBipartiteMatching<>(g, part0, part1);
		Matching<Integer, Integer> result = alg.getMatching();
		if (weightRes.isNonNull()) {
			weightRes.write(result.getWeight());
		}
		if (res.isNonNull()) {
			res.write(globalHandles.create(result.getEdges()));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "matching_exec_bipartite_perfect_min_weight", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeBipartitePerfectMinimumWeight(IsolateThread thread, ObjectHandle graphHandle,
			ObjectHandle part1Handle, ObjectHandle part2Handle, CDoublePointer weightRes, WordPointer res) {
		Graph<Integer, Integer> g = globalHandles.get(graphHandle);
		Set<Integer> part1 = globalHandles.get(part1Handle);
		Set<Integer> part2 = globalHandles.get(part2Handle);
		MatchingAlgorithm<Integer, Integer> alg = new KuhnMunkresMinimalWeightBipartitePerfectMatching<>(g, part1, part2);
		Matching<Integer, Integer> result = alg.getMatching();
		if (weightRes.isNonNull()) {
			weightRes.write(result.getWeight());
		}
		if (res.isNonNull()) {
			res.write(globalHandles.create(result.getEdges()));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "matching_exec_bipartite_max_weight", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeBipartiteMaximumWeightedMatching(IsolateThread thread, ObjectHandle graphHandle,
			CDoublePointer weightRes, WordPointer res) {
		Graph<Integer, Integer> g = globalHandles.get(graphHandle);
		System.out.println("Partitioning");
		BipartitePartitioning<Integer, Integer> partAlg = new BipartitePartitioning<>(g);
		if (!partAlg.isBipartite()) {
			throw new IllegalArgumentException("Graph is not bipartite");
		}
		Partitioning<Integer> part = partAlg.getPartitioning();
		Set<Integer> part0 = part.getPartition(0);
		Set<Integer> part1 = part.getPartition(1);
		MatchingAlgorithm<Integer, Integer> alg = new MaximumWeightBipartiteMatching<>(g, part0, part1);
		Matching<Integer, Integer> result = alg.getMatching();

		// fix bug in JGraphT 1.4.0 which returns the wrong weight here
		double weight = 0d;
		for (Integer e : result.getEdges()) {
			weight += g.getEdgeWeight(e);
		}

		if (weightRes.isNonNull()) {
			weightRes.write(weight);
		}
		if (res.isNonNull()) {
			res.write(globalHandles.create(result.getEdges()));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	private static int exec(IsolateThread thread, ObjectHandle graphHandle, CDoublePointer weightRes, WordPointer res,
			Function<Graph<Integer, Integer>, MatchingAlgorithm<Integer, Integer>> algProvider) {
		Graph<Integer, Integer> g = globalHandles.get(graphHandle);
		MatchingAlgorithm<Integer, Integer> alg = algProvider.apply(g);
		Matching<Integer, Integer> result = alg.getMatching();
		double weight = result.getWeight();
		Set<Integer> edges = result.getEdges();
		if (weightRes.isNonNull()) {
			weightRes.write(weight);
		}
		if (res.isNonNull()) {
			res.write(globalHandles.create(edges));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

}
