package org.jgrapht.capi.impl;

import java.util.Iterator;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.graalvm.nativeimage.IsolateThread;
import org.graalvm.nativeimage.ObjectHandle;
import org.graalvm.nativeimage.ObjectHandles;
import org.graalvm.nativeimage.c.function.CEntryPoint;
import org.graalvm.word.WordFactory;
import org.jgrapht.Graph;
import org.jgrapht.alg.interfaces.VertexCoverAlgorithm;
import org.jgrapht.alg.interfaces.VertexCoverAlgorithm.VertexCover;
import org.jgrapht.alg.vertexcover.BarYehudaEvenTwoApproxVCImpl;
import org.jgrapht.alg.vertexcover.ClarksonTwoApproxVCImpl;
import org.jgrapht.alg.vertexcover.EdgeBasedTwoApproxVCImpl;
import org.jgrapht.alg.vertexcover.GreedyVCImpl;
import org.jgrapht.alg.vertexcover.RecursiveExactVCImpl;
import org.jgrapht.capi.Constants;
import org.jgrapht.capi.Errors;
import org.jgrapht.capi.Status;

public class VertexCoverAPI {

	private static ObjectHandles globalHandles = ObjectHandles.getGlobal();

	@CEntryPoint(name = Constants.LIB_PREFIX + "vertexcover_exec_greedy")
	public static ObjectHandle executeVertexCoverGreedyWeighted(IsolateThread thread, ObjectHandle graphHandle) {
		return executeVertexCover(thread, graphHandle, g -> new GreedyVCImpl<>(g));
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "vertexcover_exec_greedy_weighted")
	public static ObjectHandle executeVertexCoverGreedyWeighted(IsolateThread thread, ObjectHandle graphHandle,
			ObjectHandle mapHandle) {
		return executeVertexCoverWeighted(thread, graphHandle, mapHandle,
				(g, weights) -> new GreedyVCImpl<>(g, weights));
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "vertexcover_exec_clarkson")
	public static ObjectHandle executeVertexCoverClarkson(IsolateThread thread, ObjectHandle graphHandle) {
		return executeVertexCover(thread, graphHandle, g -> new ClarksonTwoApproxVCImpl<>(g));
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "vertexcover_exec_clarkson_weighted")
	public static ObjectHandle executeVertexCoverClarksonWeighted(IsolateThread thread, ObjectHandle graphHandle,
			ObjectHandle mapHandle) {
		return executeVertexCoverWeighted(thread, graphHandle, mapHandle,
				(g, weights) -> new ClarksonTwoApproxVCImpl<>(g, weights));
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "vertexcover_exec_edgebased")
	public static ObjectHandle executeVertexCoverEdgeBased(IsolateThread thread, ObjectHandle graphHandle) {
		return executeVertexCover(thread, graphHandle, g -> new EdgeBasedTwoApproxVCImpl<>(g));
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "vertexcover_exec_baryehudaeven")
	public static ObjectHandle executeVertexCoverBarYehudaEven(IsolateThread thread, ObjectHandle graphHandle) {
		return executeVertexCover(thread, graphHandle, g -> new BarYehudaEvenTwoApproxVCImpl<>(g));
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "vertexcover_exec_baryehudaeven_weighted")
	public static ObjectHandle executeVertexCoverBarYehudaEvenWeighted(IsolateThread thread, ObjectHandle graphHandle,
			ObjectHandle mapHandle) {
		return executeVertexCoverWeighted(thread, graphHandle, mapHandle,
				(g, weights) -> new BarYehudaEvenTwoApproxVCImpl<>(g, weights));
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "vertexcover_exec_exact")
	public static ObjectHandle executeVertexCoverExact(IsolateThread thread, ObjectHandle graphHandle) {
		return executeVertexCover(thread, graphHandle, g -> new RecursiveExactVCImpl<>(g));
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "vertexcover_exec_exact_weighted")
	public static ObjectHandle executeVertexCoverExactWeighted(IsolateThread thread, ObjectHandle graphHandle,
			ObjectHandle mapHandle) {
		return executeVertexCoverWeighted(thread, graphHandle, mapHandle,
				(g, weights) -> new RecursiveExactVCImpl<>(g, weights));
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

	private static ObjectHandle executeVertexCover(IsolateThread thread, ObjectHandle graphHandle,
			Function<Graph<Long, Long>, VertexCoverAlgorithm<Long>> algProvider) {
		try {
			Graph<Long, Long> g = globalHandles.get(graphHandle);
			if (!g.getType().isUndirected()) {
				Errors.setError(Status.GRAPH_NOT_UNDIRECTED, "Only undirected graph supported");
				return WordFactory.nullPointer();
			}
			VertexCoverAlgorithm<Long> alg = algProvider.apply(g);
			VertexCover<Long> vertexCover = alg.getVertexCover();
			return globalHandles.create(vertexCover);
		} catch (IllegalArgumentException e) {
			Errors.setError(Status.ILLEGAL_ARGUMENT, e.getMessage());
		} catch (Exception e) {
			Errors.setError(Status.ERROR, e.getMessage());
		}
		return WordFactory.nullPointer();
	}

	private static ObjectHandle executeVertexCoverWeighted(IsolateThread thread, ObjectHandle graphHandle,
			ObjectHandle mapHandle,
			BiFunction<Graph<Long, Long>, Map<Long, Double>, VertexCoverAlgorithm<Long>> algProvider) {
		try {
			Graph<Long, Long> g = globalHandles.get(graphHandle);
			Map<Long, Double> vertexWeights = globalHandles.get(mapHandle);
			if (!g.getType().isUndirected()) {
				Errors.setError(Status.GRAPH_NOT_UNDIRECTED, "Only undirected graph supported");
				return WordFactory.nullPointer();
			}
			VertexCoverAlgorithm<Long> alg = algProvider.apply(g, vertexWeights);
			VertexCover<Long> vertexCover = alg.getVertexCover();
			return globalHandles.create(vertexCover);
		} catch (IllegalArgumentException e) {
			Errors.setError(Status.ILLEGAL_ARGUMENT, e.getMessage());
		} catch (Exception e) {
			Errors.setError(Status.ERROR, e.getMessage());
		}
		return WordFactory.nullPointer();
	}

}
