package org.jgrapht.nlib.api;

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
import org.jgrapht.nlib.Constants;
import org.jgrapht.nlib.Errors;
import org.jgrapht.nlib.GraphLookupException;
import org.jgrapht.nlib.Status;

public class VertexCoverAPI {

	private static ObjectHandles globalHandles = ObjectHandles.getGlobal();

	@CEntryPoint(name = Constants.LIB_PREFIX + "vertexcover_exec_greedy_uniform")
	public static ObjectHandle executeVertexCoverGreedyUniform(IsolateThread thread, ObjectHandle graphHandle) {
		try {
			Graph<Long, Long> graph = GraphAPI.getGraph(graphHandle);
			if (!graph.getType().isUndirected()) {
				Errors.setError(Status.GRAPH_NOT_UNDIRECTED);
				return WordFactory.nullPointer();
			}
			VertexCover<Long> vertexCover = new GreedyVCImpl<>(graph).getVertexCover();
			return globalHandles.create(vertexCover);
		} catch (GraphLookupException e) {
			Errors.setError(Status.INVALID_GRAPH);
		} catch (IllegalArgumentException e) {
			Errors.setError(Status.ILLEGAL_ARGUMENT);
		} catch (Exception e) {
			Errors.setError(Status.GENERIC_ERROR);
		}
		return WordFactory.nullPointer();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "vertexcover_exec_greedy_weighted")
	public static ObjectHandle executeVertexCoverGreedyWeighted(IsolateThread thread, ObjectHandle graphHandle,
			ObjectHandle mapHandle) {
		try {
			Map<Long, Double> vertexWeights = globalHandles.get(mapHandle);
			Graph<Long, Long> graph = GraphAPI.getGraph(graphHandle);
			if (!graph.getType().isUndirected()) {
				Errors.setError(Status.GRAPH_NOT_UNDIRECTED);
				return WordFactory.nullPointer();
			}

			VertexCover<Long> vertexCover = new GreedyVCImpl<>(graph, vertexWeights).getVertexCover();
			return globalHandles.create(vertexCover);
		} catch (GraphLookupException e) {
			Errors.setError(Status.INVALID_GRAPH);
		} catch (IllegalArgumentException e) {
			Errors.setError(Status.ILLEGAL_ARGUMENT);
		} catch (Exception e) {
			Errors.setError(Status.GENERIC_ERROR);
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
			Errors.setError(Status.ILLEGAL_ARGUMENT);
		} catch (Exception e) {
			Errors.setError(Status.GENERIC_ERROR);
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
			Errors.setError(Status.ILLEGAL_ARGUMENT);
		} catch (Exception e) {
			Errors.setError(Status.GENERIC_ERROR);
		}
		return WordFactory.nullPointer();
	}

}
