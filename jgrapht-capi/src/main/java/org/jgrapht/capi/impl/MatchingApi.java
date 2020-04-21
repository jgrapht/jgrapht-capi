package org.jgrapht.capi.impl;

import java.util.Iterator;
import java.util.Set;
import java.util.function.Function;

import org.graalvm.nativeimage.IsolateThread;
import org.graalvm.nativeimage.ObjectHandle;
import org.graalvm.nativeimage.ObjectHandles;
import org.graalvm.nativeimage.c.function.CEntryPoint;
import org.graalvm.nativeimage.c.type.CDoublePointer;
import org.graalvm.nativeimage.c.type.CLongPointer;
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
import org.jgrapht.alg.partition.BipartitePartitioning;
import org.jgrapht.capi.Constants;
import org.jgrapht.capi.Status;
import org.jgrapht.capi.error.StatusReturnExceptionHandler;

public class MatchingApi {

	private static ObjectHandles globalHandles = ObjectHandles.getGlobal();

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "matching_exec_greedy_general_max_card", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeGreedyMaximumCardinality(IsolateThread thread, ObjectHandle graphHandle, WordPointer res) {
		return exec(thread, graphHandle, res, (g) -> new GreedyMaximumCardinalityMatching<>(g, false));
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "matching_exec_custom_greedy_general_max_card", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeCustomGreedyMaximumCardinality(IsolateThread thread, ObjectHandle graphHandle,
			boolean sort, WordPointer res) {
		return exec(thread, graphHandle, res, (g) -> new GreedyMaximumCardinalityMatching<>(g, sort));
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "matching_exec_edmonds_general_max_card_dense", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeEdmondsMCMDense(IsolateThread thread, ObjectHandle graphHandle, WordPointer res) {
		return exec(thread, graphHandle, res, (g) -> new DenseEdmondsMaximumCardinalityMatching<>(g));
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "matching_exec_edmonds_general_max_card_sparse", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeEdmondsMCMSparse(IsolateThread thread, ObjectHandle graphHandle, WordPointer res) {
		return exec(thread, graphHandle, res, (g) -> new SparseEdmondsMaximumCardinalityMatching<>(g));
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "matching_exec_greedy_general_max_weight", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeGreedyMaximumWeightedMatching(IsolateThread thread, ObjectHandle graphHandle,
			WordPointer res) {
		return exec(thread, graphHandle, res, (g) -> new GreedyWeightedMatching<>(g, false));
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "matching_exec_custom_greedy_general_max_weight", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeCustomGreedyMaximumWeightedMatching(IsolateThread thread, ObjectHandle graphHandle,
			boolean normalizeEdgeCosts, double epsilon, WordPointer res) {
		return exec(thread, graphHandle, res, (g) -> new GreedyWeightedMatching<>(g, normalizeEdgeCosts, epsilon));
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "matching_exec_pathgrowing_max_weight", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executePathGrowingMaximumWeightedMatching(IsolateThread thread, ObjectHandle graphHandle,
			WordPointer res) {
		return exec(thread, graphHandle, res, (g) -> new PathGrowingWeightedMatching<>(g, true));
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "matching_exec_bipartite_max_card", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeBipartiteMaximumCardinalityMatching(IsolateThread thread, ObjectHandle graphHandle,
			WordPointer res) {
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
		if (res.isNonNull()) {
			res.write(globalHandles.create(result));
		}
		return Status.SUCCESS.toCEnum();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "matching_exec_bipartite_perfect_min_weight", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeBipartitePerfectMinimumWeight(IsolateThread thread, ObjectHandle graphHandle,
			ObjectHandle part1Handle, ObjectHandle part2Handle, WordPointer res) {
		Graph<Long, Long> g = globalHandles.get(graphHandle);
		Set<Long> part1 = globalHandles.get(part1Handle);
		Set<Long> part2 = globalHandles.get(part2Handle);
		MatchingAlgorithm<Long, Long> alg = new KuhnMunkresMinimalWeightBipartitePerfectMatching<>(g, part1, part2);
		Matching<Long, Long> result = alg.getMatching();
		if (res.isNonNull()) {
			res.write(globalHandles.create(result));
		}
		return Status.SUCCESS.toCEnum();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "matching_exec_bipartite_max_weight", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeBipartiteMaximumWeightedMatching(IsolateThread thread, ObjectHandle graphHandle,
			WordPointer res) {
		Graph<Long, Long> g = globalHandles.get(graphHandle);
		BipartitePartitioning<Long, Long> partAlg = new BipartitePartitioning<>(g);
		if (!partAlg.isBipartite()) {
			throw new IllegalArgumentException("Graph is not bipartite");
		}
		Partitioning<Long> part = partAlg.getPartitioning();
		Set<Long> part0 = part.getPartition(0);
		Set<Long> part1 = part.getPartition(1);
		MatchingAlgorithm<Long, Long> alg = new MaximumWeightBipartiteMatching<>(g, part0, part1);
		Matching<Long, Long> result = alg.getMatching();
		if (res.isNonNull()) {
			res.write(globalHandles.create(result));
		}
		return Status.SUCCESS.toCEnum();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "matching_get_weight", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int getMatchingWeight(IsolateThread thread, ObjectHandle mHandle, CDoublePointer res) {
		Matching<Long, Long> c = globalHandles.get(mHandle);
		double result = c.getWeight();
		if (res.isNonNull()) {
			res.write(result);
		}
		return Status.SUCCESS.toCEnum();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "matching_get_card", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int getNumberOfClusters(IsolateThread thread, ObjectHandle mHandle, CLongPointer res) {
		Matching<Long, Long> c = globalHandles.get(mHandle);
		long result = c.getEdges().size();
		if (res.isNonNull()) {
			res.write(result);
		}
		return Status.SUCCESS.toCEnum();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "matching_create_eit", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int getClusterWithIndexVertexIterator(IsolateThread thread, ObjectHandle mHandle, WordPointer res) {
		Matching<Long, Long> c = globalHandles.get(mHandle);
		Iterator<Long> result = c.iterator();
		if (res.isNonNull()) {
			res.write(globalHandles.create(result));
		}
		return Status.SUCCESS.toCEnum();
	}

	private static int exec(IsolateThread thread, ObjectHandle graphHandle, WordPointer res,
			Function<Graph<Long, Long>, MatchingAlgorithm<Long, Long>> algProvider) {
		Graph<Long, Long> g = globalHandles.get(graphHandle);
		MatchingAlgorithm<Long, Long> alg = algProvider.apply(g);
		Matching<Long, Long> result = alg.getMatching();
		if (res.isNonNull()) {
			res.write(globalHandles.create(result));
		}
		return Status.SUCCESS.toCEnum();
	}

}
