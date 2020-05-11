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
import java.util.Random;

import org.graalvm.nativeimage.IsolateThread;
import org.graalvm.nativeimage.ObjectHandle;
import org.graalvm.nativeimage.ObjectHandles;
import org.graalvm.nativeimage.c.function.CEntryPoint;
import org.graalvm.nativeimage.c.type.CIntPointer;
import org.graalvm.nativeimage.c.type.WordPointer;
import org.jgrapht.Graph;
import org.jgrapht.alg.clustering.KSpanningTreeClustering;
import org.jgrapht.alg.clustering.LabelPropagationClustering;
import org.jgrapht.alg.interfaces.ClusteringAlgorithm;
import org.jgrapht.alg.interfaces.ClusteringAlgorithm.Clustering;
import org.jgrapht.capi.Constants;
import org.jgrapht.capi.JGraphTContext.Status;
import org.jgrapht.capi.error.StatusReturnExceptionHandler;

public class ClusteringApi {

	private static ObjectHandles globalHandles = ObjectHandles.getGlobal();

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "clustering_exec_k_spanning_tree", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeKSpanningTree(IsolateThread thread, ObjectHandle graphHandle, int k, WordPointer res) {
		Graph<Integer, Integer> g = globalHandles.get(graphHandle);
		ClusteringAlgorithm<Integer> alg = new KSpanningTreeClustering<>(g, k);
		Clustering<Integer> clustering = alg.getClustering();
		if (res.isNonNull()) {
			res.write(globalHandles.create(clustering));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "clustering_exec_label_propagation", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeLabelPropagationTree(IsolateThread thread, ObjectHandle graphHandle, int maxIterations,
			long seed, WordPointer res) {
		Graph<Integer, Integer> g = globalHandles.get(graphHandle);
		ClusteringAlgorithm<Integer> alg = new LabelPropagationClustering<>(g, maxIterations, new Random(seed));
		Clustering<Integer> clustering = alg.getClustering();
		if (res.isNonNull()) {
			res.write(globalHandles.create(clustering));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "clustering_get_number_clusters", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int getNumberOfClusters(IsolateThread thread, ObjectHandle cHandle, CIntPointer res) {
		Clustering<Integer> c = globalHandles.get(cHandle);
		if (res.isNonNull()) {
			res.write(c.getNumberClusters());
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "clustering_ith_cluster_vit", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int getClusterWithIndexVertexIterator(IsolateThread thread, ObjectHandle cHandle, int i,
			WordPointer res) {
		Clustering<Integer> c = globalHandles.get(cHandle);
		Iterator<Integer> it = c.getClusters().get(i).iterator();
		if (res.isNonNull()) {
			res.write(globalHandles.create(it));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

}
