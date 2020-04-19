package org.jgrapht.capi.impl;

import java.util.Iterator;
import java.util.Map;

import org.graalvm.nativeimage.IsolateThread;
import org.graalvm.nativeimage.ObjectHandle;
import org.graalvm.nativeimage.ObjectHandles;
import org.graalvm.nativeimage.c.function.CEntryPoint;
import org.graalvm.word.WordFactory;
import org.jgrapht.Graph;
import org.jgrapht.alg.interfaces.VertexCoverAlgorithm.VertexCover;
import org.jgrapht.alg.vertexcover.GreedyVCImpl;
import org.jgrapht.capi.Constants;
import org.jgrapht.capi.Errors;
import org.jgrapht.capi.Status;

public class VertexCoverAPI {

	private static ObjectHandles globalHandles = ObjectHandles.getGlobal();

	@CEntryPoint(name = Constants.LIB_PREFIX + "vertexcover_exec_greedy_uniform")
	public static ObjectHandle executeVertexCoverGreedyUniform(IsolateThread thread, ObjectHandle graphHandle) {
		try {
			Graph<Long, Long> g = globalHandles.get(graphHandle);
			if (!g.getType().isUndirected()) {
				Errors.setError(Status.GRAPH_NOT_UNDIRECTED, "Only undirected graph supported");
				return WordFactory.nullPointer();
			}
			VertexCover<Long> vertexCover = new GreedyVCImpl<>(g).getVertexCover();
			return globalHandles.create(vertexCover);
		} catch (IllegalArgumentException e) {
			Errors.setError(Status.ILLEGAL_ARGUMENT, e.getMessage());
		} catch (Exception e) {
			Errors.setError(Status.ERROR, e.getMessage());
		}
		return WordFactory.nullPointer();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "vertexcover_exec_greedy_weighted")
	public static ObjectHandle executeVertexCoverGreedyWeighted(IsolateThread thread, ObjectHandle graphHandle,
			ObjectHandle mapHandle) {
		try {
			Graph<Long, Long> g = globalHandles.get(graphHandle);
			Map<Long, Double> vertexWeights = globalHandles.get(mapHandle);
			if (!g.getType().isUndirected()) {
				Errors.setError(Status.GRAPH_NOT_UNDIRECTED, "Only undirected graph supported");
				return WordFactory.nullPointer();
			}

			VertexCover<Long> vertexCover = new GreedyVCImpl<>(g, vertexWeights).getVertexCover();
			return globalHandles.create(vertexCover);
		} catch (IllegalArgumentException e) {
			Errors.setError(Status.ILLEGAL_ARGUMENT, e.getMessage());
		} catch (Exception e) {
			Errors.setError(Status.ERROR, e.getMessage());
		}
		return WordFactory.nullPointer();
	}

	/**
	 * Get the weight of an vertex cover.
	 * 
	 * @param thread   the thread
	 * @param vcHandle the vertex cover handle
	 * @return the weight
	 */
	@CEntryPoint(name = Constants.LIB_PREFIX + "vertexcover_get_weight")
	public static double getVertexCoverWeight(IsolateThread thread, ObjectHandle vcHandle) {
		try {
			VertexCover<Long> vc = globalHandles.get(vcHandle);
			return vc.getWeight();
		} catch (IllegalArgumentException e) {
			Errors.setError(Status.ILLEGAL_ARGUMENT, e.getMessage());
		} catch (Exception e) {
			Errors.setError(Status.ERROR, e.getMessage());
		}
		return 0d;
	}

	/**
	 * Get a vertex iterator for the vertex cover
	 * 
	 * @param thread   the thread
	 * @param vcHandle the vertex cover handle
	 * @return the vertex iterator
	 */
	@CEntryPoint(name = Constants.LIB_PREFIX + "vertexcover_create_vit")
	public static ObjectHandle createMSTEdgeIterator(IsolateThread thread, ObjectHandle vcHandle) {
		try {
			VertexCover<Long> vc = globalHandles.get(vcHandle);
			Iterator<Long> it = vc.iterator();
			return globalHandles.create(it);
		} catch (IllegalArgumentException e) {
			Errors.setError(Status.ILLEGAL_ARGUMENT, e.getMessage());
		} catch (Exception e) {
			Errors.setError(Status.ERROR, e.getMessage());
		}
		return WordFactory.nullPointer();
	}

}
