package org.jgrapht.capi.impl;

import java.util.Iterator;

import org.graalvm.nativeimage.IsolateThread;
import org.graalvm.nativeimage.ObjectHandle;
import org.graalvm.nativeimage.ObjectHandles;
import org.graalvm.nativeimage.c.function.CEntryPoint;
import org.graalvm.word.WordFactory;
import org.jgrapht.Graph;
import org.jgrapht.alg.clustering.KSpanningTreeClustering;
import org.jgrapht.alg.interfaces.ClusteringAlgorithm;
import org.jgrapht.alg.interfaces.ClusteringAlgorithm.Clustering;
import org.jgrapht.capi.Constants;
import org.jgrapht.capi.Errors;
import org.jgrapht.capi.Status;

public class ClusteringAPI {

	private static ObjectHandles globalHandles = ObjectHandles.getGlobal();

	@CEntryPoint(name = Constants.LIB_PREFIX + "clustering_exec_k_spanning_tree")
	public static ObjectHandle executeKSpanningTree(IsolateThread thread, ObjectHandle graphHandle, int k) {
		try {
			Graph<Long, Long> g = globalHandles.get(graphHandle);
			ClusteringAlgorithm<Long> alg = new KSpanningTreeClustering<>(g, k);
			Clustering<Long> clustering = alg.getClustering();
			return globalHandles.create(clustering);
		} catch (IllegalArgumentException e) {
			Errors.setError(Status.ILLEGAL_ARGUMENT, e.getMessage());
		} catch (Exception e) {
			Errors.setError(Status.ERROR, e.getMessage());
		}
		return WordFactory.nullPointer();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "clustering_get_number_clusters")
	public static long getNumberOfClusters(IsolateThread thread, ObjectHandle cHandle) {
		try {
			Clustering<Long> c = globalHandles.get(cHandle);
			return c.getNumberClusters();
		} catch (IllegalArgumentException e) {
			Errors.setError(Status.ILLEGAL_ARGUMENT, e.getMessage());
		} catch (Exception e) {
			Errors.setError(Status.ERROR, e.getMessage());
		}
		return 0L;
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "clustering_ith_cluster_vit")
	public static ObjectHandle getColorMap(IsolateThread thread, ObjectHandle cHandle, int i) {
		try {
			Clustering<Long> c = globalHandles.get(cHandle);
			if (i < 0 || i >= c.getNumberClusters()) {
				throw new IllegalArgumentException("Cluster " + i + " does not exist");
			}
			Iterator<Long> it = c.getClusters().get(i).iterator();
			return globalHandles.create(it);
		} catch (IllegalArgumentException e) {
			Errors.setError(Status.ILLEGAL_ARGUMENT, e.getMessage());
		} catch (Exception e) {
			Errors.setError(Status.ERROR, e.getMessage());
		}
		return WordFactory.nullPointer();
	}

}
