package org.jgrapht.capi.impl;

import java.util.Set;

import org.graalvm.nativeimage.IsolateThread;
import org.graalvm.nativeimage.ObjectHandle;
import org.graalvm.nativeimage.ObjectHandles;
import org.graalvm.nativeimage.c.function.CEntryPoint;
import org.graalvm.nativeimage.c.type.CDoublePointer;
import org.graalvm.nativeimage.c.type.WordPointer;
import org.jgrapht.Graph;
import org.jgrapht.alg.StoerWagnerMinimumCut;
import org.jgrapht.alg.flow.GusfieldGomoryHuCutTree;
import org.jgrapht.alg.flow.PadbergRaoOddMinimumCutset;
import org.jgrapht.capi.Constants;
import org.jgrapht.capi.JGraphTContext.Status;
import org.jgrapht.capi.error.StatusReturnExceptionHandler;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

public class CutApi {

	private static ObjectHandles globalHandles = ObjectHandles.getGlobal();

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "cut_mincut_exec_stoer_wagner", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeStoerWagner(IsolateThread thread, ObjectHandle graphHandle, CDoublePointer valueRes,
			WordPointer cutSourcePartitionRes) {
		Graph<Integer, Integer> g = globalHandles.get(graphHandle);

		StoerWagnerMinimumCut<Integer, Integer> alg = new StoerWagnerMinimumCut<>(g);
		double weight = alg.minCutWeight();
		Set<Integer> cutSourcePartition = alg.minCut();

		if (valueRes.isNonNull()) {
			valueRes.write(weight);
		}
		if (cutSourcePartitionRes.isNonNull()) {
			cutSourcePartitionRes.write(globalHandles.create(cutSourcePartition));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "cut_gomoryhu_exec_gusfield", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeGomoryHuGusfield(IsolateThread thread, ObjectHandle graphHandle, WordPointer res) {
		Graph<Integer, Integer> g = globalHandles.get(graphHandle);
		GusfieldGomoryHuCutTree<Integer, Integer> alg = new GusfieldGomoryHuCutTree<>(g);
		if (res.isNonNull()) {
			res.write(globalHandles.create(alg));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "cut_gomoryhu_min_st_cut", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int gomoryHuSTCut(IsolateThread thread, ObjectHandle gomoryHu, int source, int sink,
			CDoublePointer valueRes, WordPointer cutSourcePartitionRes) {
		GusfieldGomoryHuCutTree<Integer, Integer> alg = globalHandles.get(gomoryHu);
		double cutValue = alg.calculateMinCut(source, sink);
		Set<Integer> sourcePartition = alg.getSourcePartition();
		if (valueRes.isNonNull()) {
			valueRes.write(cutValue);
		}
		if (cutSourcePartitionRes.isNonNull()) {
			cutSourcePartitionRes.write(globalHandles.create(sourcePartition));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "cut_gomoryhu_min_cut", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int gomoryHuMinCut(IsolateThread thread, ObjectHandle gomoryHu, CDoublePointer valueRes,
			WordPointer cutSourcePartitionRes) {
		GusfieldGomoryHuCutTree<Integer, Integer> alg = globalHandles.get(gomoryHu);
		double cutValue = alg.calculateMinCut();
		Set<Integer> sourcePartition = alg.getSourcePartition();
		if (valueRes.isNonNull()) {
			valueRes.write(cutValue);
		}
		if (cutSourcePartitionRes.isNonNull()) {
			cutSourcePartitionRes.write(globalHandles.create(sourcePartition));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "cut_gomoryhu_tree", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int gomoryHuTree(IsolateThread thread, ObjectHandle gomoryHu, WordPointer treeRes) {
		GusfieldGomoryHuCutTree<Integer, Integer> alg = globalHandles.get(gomoryHu);
		SimpleWeightedGraph<Integer, DefaultWeightedEdge> origTree = alg.getGomoryHuTree();

		// convert to integer vertices/edges
		Graph<Integer, Integer> tree = GraphApi.createGraph(false, false, false, true);
		for (Integer v : origTree.vertexSet()) {
			tree.addVertex(v);
		}
		for (DefaultWeightedEdge e : origTree.edgeSet()) {
			int s = origTree.getEdgeSource(e);
			int t = origTree.getEdgeTarget(e);
			double w = origTree.getEdgeWeight(e);
			tree.setEdgeWeight(tree.addEdge(s, t), w);
		}

		// write result
		if (treeRes.isNonNull()) {
			treeRes.write(globalHandles.create(tree));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "cut_oddmincutset_exec_padberg_rao", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executePadbergRao(IsolateThread thread, ObjectHandle graphHandle, ObjectHandle oddVerticesHandle,
			boolean useTreeCompression, CDoublePointer valueRes, WordPointer sourcePartitionRes) {
		Graph<Integer, Integer> g = globalHandles.get(graphHandle);
		Set<Integer> oddVertices = globalHandles.get(oddVerticesHandle);
		PadbergRaoOddMinimumCutset<Integer, Integer> alg = new PadbergRaoOddMinimumCutset<>(g);
		double value = alg.calculateMinCut(oddVertices, useTreeCompression);
		if (valueRes.isNonNull()) {
			valueRes.write(value);
		}
		if (sourcePartitionRes.isNonNull()) {
			sourcePartitionRes.write(globalHandles.create(alg.getSourcePartition()));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

}
