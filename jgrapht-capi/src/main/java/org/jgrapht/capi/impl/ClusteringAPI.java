package org.jgrapht.capi.impl;

import java.util.Iterator;

import org.graalvm.nativeimage.IsolateThread;
import org.graalvm.nativeimage.ObjectHandle;
import org.graalvm.nativeimage.ObjectHandles;
import org.graalvm.nativeimage.c.function.CEntryPoint;
import org.jgrapht.Graph;
import org.jgrapht.alg.clustering.KSpanningTreeClustering;
import org.jgrapht.alg.interfaces.ClusteringAlgorithm;
import org.jgrapht.alg.interfaces.ClusteringAlgorithm.Clustering;
import org.jgrapht.capi.Constants;
import org.jgrapht.capi.error.LongExceptionHandler;
import org.jgrapht.capi.error.ObjectHandleExceptionHandler;

public class ClusteringAPI {

	private static ObjectHandles globalHandles = ObjectHandles.getGlobal();

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "clustering_exec_k_spanning_tree", exceptionHandler = ObjectHandleExceptionHandler.class)
	public static ObjectHandle executeKSpanningTree(IsolateThread thread, ObjectHandle graphHandle, int k) {
		Graph<Long, Long> g = globalHandles.get(graphHandle);
		ClusteringAlgorithm<Long> alg = new KSpanningTreeClustering<>(g, k);
		Clustering<Long> clustering = alg.getClustering();
		return globalHandles.create(clustering);
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "clustering_get_number_clusters", exceptionHandler = LongExceptionHandler.class)
	public static long getNumberOfClusters(IsolateThread thread, ObjectHandle cHandle) {
		Clustering<Long> c = globalHandles.get(cHandle);
		return c.getNumberClusters();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "clustering_ith_cluster_vit", exceptionHandler = ObjectHandleExceptionHandler.class)
	public static ObjectHandle getClusterWithIndexVertexIterator(IsolateThread thread, ObjectHandle cHandle, int i) {
		Clustering<Long> c = globalHandles.get(cHandle);
		Iterator<Long> it = c.getClusters().get(i).iterator();
		return globalHandles.create(it);
	}

}
