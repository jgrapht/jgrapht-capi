/*
 * (C) Copyright 2020-2021, by Dimitrios Michail.
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

import org.graalvm.nativeimage.IsolateThread;
import org.graalvm.nativeimage.ObjectHandle;
import org.graalvm.nativeimage.ObjectHandles;
import org.graalvm.nativeimage.c.function.CEntryPoint;
import org.graalvm.nativeimage.c.type.CDoublePointer;
import org.graalvm.nativeimage.c.type.WordPointer;
import org.graalvm.word.PointerBase;
import org.jgrapht.Graph;
import org.jgrapht.alg.StoerWagnerMinimumCut;
import org.jgrapht.alg.flow.GusfieldGomoryHuCutTree;
import org.jgrapht.alg.flow.PadbergRaoOddMinimumCutset;
import org.jgrapht.capi.Constants;
import org.jgrapht.capi.JGraphTContext.Status;
import org.jgrapht.capi.JGraphTContext.VToPFunctionPointer;
import org.jgrapht.capi.JGraphTContext.VoidToIntegerFunctionPointer;
import org.jgrapht.capi.JGraphTContext.VoidToLongFunctionPointer;
import org.jgrapht.capi.error.StatusReturnExceptionHandler;
import org.jgrapht.capi.graph.CapiGraph;
import org.jgrapht.capi.graph.ExternalRef;
import org.jgrapht.capi.graph.HashAndEqualsResolver;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

public class CutApi {

	private static ObjectHandles globalHandles = ObjectHandles.getGlobal();

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.ANY_ANY
			+ "cut_mincut_exec_stoer_wagner", exceptionHandler = StatusReturnExceptionHandler.class)
	public static <V, E> int executeStoerWagner(IsolateThread thread, ObjectHandle graphHandle, CDoublePointer valueRes,
			WordPointer cutSourcePartitionRes) {
		Graph<V, E> g = globalHandles.get(graphHandle);

		StoerWagnerMinimumCut<V, E> alg = new StoerWagnerMinimumCut<>(g);
		double weight = alg.minCutWeight();
		Set<V> cutSourcePartition = alg.minCut();

		if (valueRes.isNonNull()) {
			valueRes.write(weight);
		}
		if (cutSourcePartitionRes.isNonNull()) {
			cutSourcePartitionRes.write(globalHandles.create(cutSourcePartition));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.ANY_ANY
			+ "cut_gomoryhu_exec_gusfield", exceptionHandler = StatusReturnExceptionHandler.class)
	public static <V, E> int executeGomoryHuGusfield(IsolateThread thread, ObjectHandle graphHandle, WordPointer res) {
		Graph<V, E> g = globalHandles.get(graphHandle);
		GusfieldGomoryHuCutTree<V, E> alg = new GusfieldGomoryHuCutTree<>(g);
		if (res.isNonNull()) {
			res.write(globalHandles.create(alg));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.INT_ANY
			+ "cut_gomoryhu_min_st_cut", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int gomoryHuSTCut(IsolateThread thread, ObjectHandle gomoryHu, int source, int sink,
			CDoublePointer valueRes, WordPointer cutSourcePartitionRes) {
		GusfieldGomoryHuCutTree<Integer, ?> alg = globalHandles.get(gomoryHu);
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

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.LONG_ANY
			+ "cut_gomoryhu_min_st_cut", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int gomoryHuSTCut(IsolateThread thread, ObjectHandle gomoryHu, long source, long sink,
			CDoublePointer valueRes, WordPointer cutSourcePartitionRes) {
		GusfieldGomoryHuCutTree<Long, ?> alg = globalHandles.get(gomoryHu);
		double cutValue = alg.calculateMinCut(source, sink);
		Set<Long> sourcePartition = alg.getSourcePartition();
		if (valueRes.isNonNull()) {
			valueRes.write(cutValue);
		}
		if (cutSourcePartitionRes.isNonNull()) {
			cutSourcePartitionRes.write(globalHandles.create(sourcePartition));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.DREF_ANY
			+ "cut_gomoryhu_min_st_cut", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int gomoryHuSTCut(IsolateThread thread, ObjectHandle gomoryHu, PointerBase sourcePtr,
			PointerBase sinkPtr, ObjectHandle hashEqualsResolverHandle, CDoublePointer valueRes,
			WordPointer cutSourcePartitionRes) {
		GusfieldGomoryHuCutTree<ExternalRef, ?> alg = globalHandles.get(gomoryHu);
		HashAndEqualsResolver resolver = globalHandles.get(hashEqualsResolverHandle);
		ExternalRef source = resolver.toExternalRef(sourcePtr);
		ExternalRef sink = resolver.toExternalRef(sinkPtr);

		double cutValue = alg.calculateMinCut(source, sink);
		Set<ExternalRef> sourcePartition = alg.getSourcePartition();
		if (valueRes.isNonNull()) {
			valueRes.write(cutValue);
		}
		if (cutSourcePartitionRes.isNonNull()) {
			cutSourcePartitionRes.write(globalHandles.create(sourcePartition));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.ANY_ANY
			+ "cut_gomoryhu_min_cut", exceptionHandler = StatusReturnExceptionHandler.class)
	public static <V, E> int gomoryHuMinCut(IsolateThread thread, ObjectHandle gomoryHu, CDoublePointer valueRes,
			WordPointer cutSourcePartitionRes) {
		GusfieldGomoryHuCutTree<V, E> alg = globalHandles.get(gomoryHu);
		double cutValue = alg.calculateMinCut();
		Set<V> sourcePartition = alg.getSourcePartition();
		if (valueRes.isNonNull()) {
			valueRes.write(cutValue);
		}
		if (cutSourcePartitionRes.isNonNull()) {
			cutSourcePartitionRes.write(globalHandles.create(sourcePartition));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.INT_INT
			+ "cut_gomoryhu_tree", exceptionHandler = StatusReturnExceptionHandler.class, documentation = {
					"Given an instance of the GomoryHu cut tree from Gusfield's algorithm, compute",
					"the actual tree as a graph. The new graph will reuse the vertex set from the original graph",
					"but will have new edges which will be constructed by the provided edge supplier." })
	public static int llGomoryHuTree(IsolateThread thread, ObjectHandle gomoryHu,
			VoidToIntegerFunctionPointer vertexSupplier, VoidToIntegerFunctionPointer edgeSupplier,
			WordPointer treeRes) {
		GusfieldGomoryHuCutTree<Integer, ?> alg = globalHandles.get(gomoryHu);
		SimpleWeightedGraph<Integer, DefaultWeightedEdge> origTree = alg.getGomoryHuTree();

		// convert to integer vertices/edges
		CapiGraph<Integer, Integer> tree = GraphApi.createGraph(false, false, false, true, vertexSupplier,
				edgeSupplier);

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

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.LONG_LONG
			+ "cut_gomoryhu_tree", exceptionHandler = StatusReturnExceptionHandler.class, documentation = {
					"Given an instance of the GomoryHu cut tree from Gusfield's algorithm, compute",
					"the actual tree as a graph. The new graph will reuse the vertex set from the original graph",
					"but will have new edges which will be constructed by the provided edge supplier." })
	public static int llGomoryHuTree(IsolateThread thread, ObjectHandle gomoryHu,
			VoidToLongFunctionPointer vertexSupplier, VoidToLongFunctionPointer edgeSupplier, WordPointer treeRes) {
		GusfieldGomoryHuCutTree<Long, ?> alg = globalHandles.get(gomoryHu);
		SimpleWeightedGraph<Long, DefaultWeightedEdge> origTree = alg.getGomoryHuTree();

		// convert to integer vertices/edges
		CapiGraph<Long, Long> tree = GraphApi.createLongGraph(false, false, false, true, vertexSupplier,
				edgeSupplier);

		for (Long v : origTree.vertexSet()) {
			tree.addVertex(v);
		}
		for (DefaultWeightedEdge e : origTree.edgeSet()) {
			long s = origTree.getEdgeSource(e);
			long t = origTree.getEdgeTarget(e);
			double w = origTree.getEdgeWeight(e);
			tree.setEdgeWeight(tree.addEdge(s, t), w);
		}

		// write result
		if (treeRes.isNonNull()) {
			treeRes.write(globalHandles.create(tree));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.DREF_DREF
			+ "cut_gomoryhu_tree", exceptionHandler = StatusReturnExceptionHandler.class, documentation = {
					"Given an instance of the GomoryHu cut tree from Gusfield's algorithm, compute",
					"the actual tree as a graph. The new graph will reuse the vertex set from the original graph",
					"but will have new edges which will be constructed by the provided edge supplier." })
	public static int llGomoryHuTree(IsolateThread thread, ObjectHandle gomoryHu, VToPFunctionPointer vertexSupplier,
			VToPFunctionPointer edgeSupplier, ObjectHandle hashEqualsResolverHandle, WordPointer treeRes) {

		HashAndEqualsResolver resolver = globalHandles.get(hashEqualsResolverHandle);
		GusfieldGomoryHuCutTree<ExternalRef, ?> alg = globalHandles.get(gomoryHu);
		SimpleWeightedGraph<ExternalRef, DefaultWeightedEdge> origTree = alg.getGomoryHuTree();

		// convert to ref vertices/edges
		CapiGraph<ExternalRef, ExternalRef> tree = RefGraphApi.createRefGraph(false, false, false, true,
				vertexSupplier, edgeSupplier, resolver);

		for (ExternalRef v : origTree.vertexSet()) {
			tree.addVertex(v);
		}
		for (DefaultWeightedEdge e : origTree.edgeSet()) {
			ExternalRef s = origTree.getEdgeSource(e);
			ExternalRef t = origTree.getEdgeTarget(e);
			double w = origTree.getEdgeWeight(e);
			tree.setEdgeWeight(tree.addEdge(s, t), w);
		}

		// write result
		if (treeRes.isNonNull()) {
			treeRes.write(globalHandles.create(tree));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.ANY_ANY
			+ "cut_oddmincutset_exec_padberg_rao", exceptionHandler = StatusReturnExceptionHandler.class)
	public static <V, E> int executePadbergRao(IsolateThread thread, ObjectHandle graphHandle,
			ObjectHandle oddVerticesHandle, boolean useTreeCompression, CDoublePointer valueRes,
			WordPointer sourcePartitionRes) {
		Graph<V, E> g = globalHandles.get(graphHandle);
		Set<V> oddVertices = globalHandles.get(oddVerticesHandle);
		PadbergRaoOddMinimumCutset<V, E> alg = new PadbergRaoOddMinimumCutset<>(g);
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
