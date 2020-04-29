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
import org.jgrapht.capi.enums.Status;
import org.jgrapht.capi.error.StatusReturnExceptionHandler;

public class MatchingApi {

	private static ObjectHandles globalHandles = ObjectHandles.getGlobal();

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "matching_exec_greedy_general_max_card", exceptionHandler = StatusReturnExceptionHandler.class)
	public static Status executeGreedyMaximumCardinality(IsolateThread thread, ObjectHandle graphHandle,
			CDoublePointer weightRes, WordPointer res) {
		return exec(thread, graphHandle, weightRes, res, (g) -> new GreedyMaximumCardinalityMatching<>(g, false));
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "matching_exec_custom_greedy_general_max_card", exceptionHandler = StatusReturnExceptionHandler.class)
	public static Status executeCustomGreedyMaximumCardinality(IsolateThread thread, ObjectHandle graphHandle,
			boolean sort, CDoublePointer weightRes, WordPointer res) {
		return exec(thread, graphHandle, weightRes, res, (g) -> new GreedyMaximumCardinalityMatching<>(g, sort));
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "matching_exec_edmonds_general_max_card_dense", exceptionHandler = StatusReturnExceptionHandler.class)
	public static Status executeEdmondsMCMDense(IsolateThread thread, ObjectHandle graphHandle, CDoublePointer weightRes,
			WordPointer res) {
		return exec(thread, graphHandle, weightRes, res, (g) -> new DenseEdmondsMaximumCardinalityMatching<>(g));
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "matching_exec_edmonds_general_max_card_sparse", exceptionHandler = StatusReturnExceptionHandler.class)
	public static Status executeEdmondsMCMSparse(IsolateThread thread, ObjectHandle graphHandle, CDoublePointer weightRes,
			WordPointer res) {
		return exec(thread, graphHandle, weightRes, res, (g) -> new SparseEdmondsMaximumCardinalityMatching<>(g));
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "matching_exec_greedy_general_max_weight", exceptionHandler = StatusReturnExceptionHandler.class)
	public static Status executeGreedyMaximumWeightedMatching(IsolateThread thread, ObjectHandle graphHandle,
			CDoublePointer weightRes, WordPointer res) {
		return exec(thread, graphHandle, weightRes, res, (g) -> new GreedyWeightedMatching<>(g, false));
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "matching_exec_custom_greedy_general_max_weight", exceptionHandler = StatusReturnExceptionHandler.class)
	public static Status executeCustomGreedyMaximumWeightedMatching(IsolateThread thread, ObjectHandle graphHandle,
			boolean normalizeEdgeCosts, double epsilon, CDoublePointer weightRes, WordPointer res) {
		return exec(thread, graphHandle, weightRes, res,
				(g) -> new GreedyWeightedMatching<>(g, normalizeEdgeCosts, epsilon));
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "matching_exec_pathgrowing_max_weight", exceptionHandler = StatusReturnExceptionHandler.class)
	public static Status executePathGrowingMaximumWeightedMatching(IsolateThread thread, ObjectHandle graphHandle,
			CDoublePointer weightRes, WordPointer res) {
		return exec(thread, graphHandle, weightRes, res, (g) -> new PathGrowingWeightedMatching<>(g, true));
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "matching_exec_blossom5_general_max_weight", exceptionHandler = StatusReturnExceptionHandler.class)
	public static Status executeBlossom5MaxWeight(IsolateThread thread, ObjectHandle graphHandle, CDoublePointer weightRes,
			WordPointer res) {
		return exec(thread, graphHandle, weightRes, res,
				(g) -> new KolmogorovWeightedMatching<>(g, ObjectiveSense.MAXIMIZE));
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "matching_exec_blossom5_general_min_weight", exceptionHandler = StatusReturnExceptionHandler.class)
	public static Status executeBlossom5MinWeight(IsolateThread thread, ObjectHandle graphHandle, CDoublePointer weightRes,
			WordPointer res) {
		return exec(thread, graphHandle, weightRes, res,
				(g) -> new KolmogorovWeightedMatching<>(g, ObjectiveSense.MINIMIZE));
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "matching_exec_blossom5_general_perfect_max_weight", exceptionHandler = StatusReturnExceptionHandler.class)
	public static Status executeBlossom5PerfectMaxWeight(IsolateThread thread, ObjectHandle graphHandle,
			CDoublePointer weightRes, WordPointer res) {
		return exec(thread, graphHandle, weightRes, res,
				(g) -> new KolmogorovWeightedPerfectMatching<>(g, ObjectiveSense.MAXIMIZE));
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "matching_exec_blossom5_general_perfect_min_weight", exceptionHandler = StatusReturnExceptionHandler.class)
	public static Status executeBlossom5PerfectMinWeight(IsolateThread thread, ObjectHandle graphHandle,
			CDoublePointer weightRes, WordPointer res) {
		return exec(thread, graphHandle, weightRes, res,
				(g) -> new KolmogorovWeightedPerfectMatching<>(g, ObjectiveSense.MINIMIZE));
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "matching_exec_bipartite_max_card", exceptionHandler = StatusReturnExceptionHandler.class)
	public static Status executeBipartiteMaximumCardinalityMatching(IsolateThread thread, ObjectHandle graphHandle,
			CDoublePointer weightRes, WordPointer res) {
		Graph<Long, Long> g = globalHandles.get(graphHandle);
		BipartitePartitioning<Long, Long> partAlg = new BipartitePartitioning<>(g);
		if (!partAlg.isBipartite()) {
			throw new IllegalArgumentException("Graph is not bipartite");
		}
		Partitioning<Long> part = partAlg.getPartitioning();
		Set<Long> part0 = part.getPartition(0);
		Set<Long> part1 = part.getPartition(1);
		MatchingAlgorithm<Long, Long> alg = new HopcroftKarpMaximumCardinalityBipartiteMatching<>(g, part0, part1);
		Matching<Long, Long> result = alg.getMatching();
		if (weightRes.isNonNull()) {
			weightRes.write(result.getWeight());
		}
		if (res.isNonNull()) {
			res.write(globalHandles.create(result.getEdges()));
		}
		return Status.STATUS_SUCCESS;
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "matching_exec_bipartite_perfect_min_weight", exceptionHandler = StatusReturnExceptionHandler.class)
	public static Status executeBipartitePerfectMinimumWeight(IsolateThread thread, ObjectHandle graphHandle,
			ObjectHandle part1Handle, ObjectHandle part2Handle, CDoublePointer weightRes, WordPointer res) {
		Graph<Long, Long> g = globalHandles.get(graphHandle);
		Set<Long> part1 = globalHandles.get(part1Handle);
		Set<Long> part2 = globalHandles.get(part2Handle);
		MatchingAlgorithm<Long, Long> alg = new KuhnMunkresMinimalWeightBipartitePerfectMatching<>(g, part1, part2);
		Matching<Long, Long> result = alg.getMatching();
		if (weightRes.isNonNull()) {
			weightRes.write(result.getWeight());
		}
		if (res.isNonNull()) {
			res.write(globalHandles.create(result.getEdges()));
		}
		return Status.STATUS_SUCCESS;
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "matching_exec_bipartite_max_weight", exceptionHandler = StatusReturnExceptionHandler.class)
	public static Status executeBipartiteMaximumWeightedMatching(IsolateThread thread, ObjectHandle graphHandle,
			CDoublePointer weightRes, WordPointer res) {
		Graph<Long, Long> g = globalHandles.get(graphHandle);
		System.out.println("Partitioning");
		BipartitePartitioning<Long, Long> partAlg = new BipartitePartitioning<>(g);
		if (!partAlg.isBipartite()) {
			throw new IllegalArgumentException("Graph is not bipartite");
		}
		Partitioning<Long> part = partAlg.getPartitioning();
		Set<Long> part0 = part.getPartition(0);
		Set<Long> part1 = part.getPartition(1);
		MatchingAlgorithm<Long, Long> alg = new MaximumWeightBipartiteMatching<>(g, part0, part1);
		Matching<Long, Long> result = alg.getMatching();

		// fix bug in JGraphT 1.4.0 which returns the wrong weight here
		double weight = 0d;
		for (Long e : result.getEdges()) {
			weight += g.getEdgeWeight(e);
		}

		if (weightRes.isNonNull()) {
			weightRes.write(weight);
		}
		if (res.isNonNull()) {
			res.write(globalHandles.create(result.getEdges()));
		}
		return Status.STATUS_SUCCESS;
	}

	private static Status exec(IsolateThread thread, ObjectHandle graphHandle, CDoublePointer weightRes, WordPointer res,
			Function<Graph<Long, Long>, MatchingAlgorithm<Long, Long>> algProvider) {
		Graph<Long, Long> g = globalHandles.get(graphHandle);
		MatchingAlgorithm<Long, Long> alg = algProvider.apply(g);
		Matching<Long, Long> result = alg.getMatching();
		double weight = result.getWeight();
		Set<Long> edges = result.getEdges();
		if (weightRes.isNonNull()) {
			weightRes.write(weight);
		}
		if (res.isNonNull()) {
			res.write(globalHandles.create(edges));
		}
		return Status.STATUS_SUCCESS;
	}

}
