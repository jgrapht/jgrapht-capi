package org.jgrapht.capi.impl;

import java.util.Iterator;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.graalvm.nativeimage.IsolateThread;
import org.graalvm.nativeimage.ObjectHandle;
import org.graalvm.nativeimage.ObjectHandles;
import org.graalvm.nativeimage.c.function.CEntryPoint;
import org.jgrapht.Graph;
import org.jgrapht.alg.interfaces.VertexCoverAlgorithm;
import org.jgrapht.alg.interfaces.VertexCoverAlgorithm.VertexCover;
import org.jgrapht.alg.vertexcover.BarYehudaEvenTwoApproxVCImpl;
import org.jgrapht.alg.vertexcover.ClarksonTwoApproxVCImpl;
import org.jgrapht.alg.vertexcover.EdgeBasedTwoApproxVCImpl;
import org.jgrapht.alg.vertexcover.GreedyVCImpl;
import org.jgrapht.alg.vertexcover.RecursiveExactVCImpl;
import org.jgrapht.capi.Constants;
import org.jgrapht.capi.error.DoubleExceptionHandler;
import org.jgrapht.capi.error.ObjectHandleExceptionHandler;

public class VertexCoverAPI {

	private static ObjectHandles globalHandles = ObjectHandles.getGlobal();

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "vertexcover_exec_greedy", exceptionHandler = ObjectHandleExceptionHandler.class)
	public static ObjectHandle executeVertexCoverGreedyWeighted(IsolateThread thread, ObjectHandle graphHandle) {
		return executeVertexCover(thread, graphHandle, g -> new GreedyVCImpl<>(g));
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "vertexcover_exec_greedy_weighted", exceptionHandler = ObjectHandleExceptionHandler.class)
	public static ObjectHandle executeVertexCoverGreedyWeighted(IsolateThread thread, ObjectHandle graphHandle,
			ObjectHandle mapHandle) {
		return executeVertexCoverWeighted(thread, graphHandle, mapHandle,
				(g, weights) -> new GreedyVCImpl<>(g, weights));
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "vertexcover_exec_clarkson", exceptionHandler = ObjectHandleExceptionHandler.class)
	public static ObjectHandle executeVertexCoverClarkson(IsolateThread thread, ObjectHandle graphHandle) {
		return executeVertexCover(thread, graphHandle, g -> new ClarksonTwoApproxVCImpl<>(g));
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "vertexcover_exec_clarkson_weighted", exceptionHandler = ObjectHandleExceptionHandler.class)
	public static ObjectHandle executeVertexCoverClarksonWeighted(IsolateThread thread, ObjectHandle graphHandle,
			ObjectHandle mapHandle) {
		return executeVertexCoverWeighted(thread, graphHandle, mapHandle,
				(g, weights) -> new ClarksonTwoApproxVCImpl<>(g, weights));
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "vertexcover_exec_edgebased", exceptionHandler = ObjectHandleExceptionHandler.class)
	public static ObjectHandle executeVertexCoverEdgeBased(IsolateThread thread, ObjectHandle graphHandle) {
		return executeVertexCover(thread, graphHandle, g -> new EdgeBasedTwoApproxVCImpl<>(g));
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "vertexcover_exec_baryehudaeven", exceptionHandler = ObjectHandleExceptionHandler.class)
	public static ObjectHandle executeVertexCoverBarYehudaEven(IsolateThread thread, ObjectHandle graphHandle) {
		return executeVertexCover(thread, graphHandle, g -> new BarYehudaEvenTwoApproxVCImpl<>(g));
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "vertexcover_exec_baryehudaeven_weighted", exceptionHandler = ObjectHandleExceptionHandler.class)
	public static ObjectHandle executeVertexCoverBarYehudaEvenWeighted(IsolateThread thread, ObjectHandle graphHandle,
			ObjectHandle mapHandle) {
		return executeVertexCoverWeighted(thread, graphHandle, mapHandle,
				(g, weights) -> new BarYehudaEvenTwoApproxVCImpl<>(g, weights));
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "vertexcover_exec_exact", exceptionHandler = ObjectHandleExceptionHandler.class)
	public static ObjectHandle executeVertexCoverExact(IsolateThread thread, ObjectHandle graphHandle) {
		return executeVertexCover(thread, graphHandle, g -> new RecursiveExactVCImpl<>(g));
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "vertexcover_exec_exact_weighted", exceptionHandler = ObjectHandleExceptionHandler.class)
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
	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "vertexcover_get_weight", exceptionHandler = DoubleExceptionHandler.class)
	public static double getVertexCoverWeight(IsolateThread thread, ObjectHandle vcHandle) {
		VertexCover<Long> vc = globalHandles.get(vcHandle);
		return vc.getWeight();
	}

	/**
	 * Get a vertex iterator for the vertex cover
	 * 
	 * @param thread   the thread
	 * @param vcHandle the vertex cover handle
	 * @return the vertex iterator
	 */
	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "vertexcover_create_vit", exceptionHandler = ObjectHandleExceptionHandler.class)
	public static ObjectHandle createMSTEdgeIterator(IsolateThread thread, ObjectHandle vcHandle) {
		VertexCover<Long> vc = globalHandles.get(vcHandle);
		Iterator<Long> it = vc.iterator();
		return globalHandles.create(it);
	}

	private static ObjectHandle executeVertexCover(IsolateThread thread, ObjectHandle graphHandle,
			Function<Graph<Long, Long>, VertexCoverAlgorithm<Long>> algProvider) {
		Graph<Long, Long> g = globalHandles.get(graphHandle);
		VertexCoverAlgorithm<Long> alg = algProvider.apply(g);
		VertexCover<Long> vertexCover = alg.getVertexCover();
		return globalHandles.create(vertexCover);
	}

	private static ObjectHandle executeVertexCoverWeighted(IsolateThread thread, ObjectHandle graphHandle,
			ObjectHandle mapHandle,
			BiFunction<Graph<Long, Long>, Map<Long, Double>, VertexCoverAlgorithm<Long>> algProvider) {
		Graph<Long, Long> g = globalHandles.get(graphHandle);
		Map<Long, Double> vertexWeights = globalHandles.get(mapHandle);
		VertexCoverAlgorithm<Long> alg = algProvider.apply(g, vertexWeights);
		VertexCover<Long> vertexCover = alg.getVertexCover();
		return globalHandles.create(vertexCover);
	}

}
