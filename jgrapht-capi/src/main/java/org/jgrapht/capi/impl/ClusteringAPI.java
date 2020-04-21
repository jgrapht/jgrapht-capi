package org.jgrapht.capi.impl;

import java.util.Iterator;

import org.graalvm.nativeimage.IsolateThread;
import org.graalvm.nativeimage.ObjectHandle;
import org.graalvm.nativeimage.ObjectHandles;
import org.graalvm.nativeimage.c.function.CEntryPoint;
import org.graalvm.nativeimage.c.type.CLongPointer;
import org.graalvm.nativeimage.c.type.WordPointer;
import org.jgrapht.Graph;
import org.jgrapht.alg.clustering.KSpanningTreeClustering;
import org.jgrapht.alg.interfaces.ClusteringAlgorithm;
import org.jgrapht.alg.interfaces.ClusteringAlgorithm.Clustering;
import org.jgrapht.capi.Constants;
import org.jgrapht.capi.Status;
import org.jgrapht.capi.error.StatusReturnExceptionHandler;

public class ClusteringAPI {

	private static ObjectHandles globalHandles = ObjectHandles.getGlobal();

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "clustering_exec_k_spanning_tree", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeKSpanningTree(IsolateThread thread, ObjectHandle graphHandle, int k, WordPointer res) {
		Graph<Long, Long> g = globalHandles.get(graphHandle);
		ClusteringAlgorithm<Long> alg = new KSpanningTreeClustering<>(g, k);
		Clustering<Long> clustering = alg.getClustering();
		if (res.isNonNull()) {
			res.write(globalHandles.create(clustering));
		}
		return Status.SUCCESS.toCEnum();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "clustering_get_number_clusters", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int getNumberOfClusters(IsolateThread thread, ObjectHandle cHandle, CLongPointer res) {
		Clustering<Long> c = globalHandles.get(cHandle);
		if (res.isNonNull()) {
			res.write(c.getNumberClusters());
		}
		return Status.SUCCESS.toCEnum();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "clustering_ith_cluster_vit", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int getClusterWithIndexVertexIterator(IsolateThread thread, ObjectHandle cHandle, int i,
			WordPointer res) {
		Clustering<Long> c = globalHandles.get(cHandle);
		Iterator<Long> it = c.getClusters().get(i).iterator();
		if (res.isNonNull()) {
			res.write(globalHandles.create(it));
		}
		return Status.SUCCESS.toCEnum();
	}

}
