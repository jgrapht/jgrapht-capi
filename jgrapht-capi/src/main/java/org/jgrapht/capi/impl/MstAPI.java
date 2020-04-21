package org.jgrapht.capi.impl;

import java.util.Iterator;

import org.graalvm.nativeimage.IsolateThread;
import org.graalvm.nativeimage.ObjectHandle;
import org.graalvm.nativeimage.ObjectHandles;
import org.graalvm.nativeimage.c.function.CEntryPoint;
import org.jgrapht.Graph;
import org.jgrapht.alg.interfaces.SpanningTreeAlgorithm.SpanningTree;
import org.jgrapht.alg.spanning.KruskalMinimumSpanningTree;
import org.jgrapht.alg.spanning.PrimMinimumSpanningTree;
import org.jgrapht.capi.Constants;
import org.jgrapht.capi.error.DoubleExceptionHandler;
import org.jgrapht.capi.error.ObjectHandleExceptionHandler;

public class MstAPI {

	private static ObjectHandles globalHandles = ObjectHandles.getGlobal();

	/**
	 * Execute MST kruskal on a graph
	 * 
	 * @param thread      the thread
	 * @param graphHandle the graph handle
	 * @return a handle on the result
	 */
	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "mst_exec_kruskal", exceptionHandler = ObjectHandleExceptionHandler.class)
	public static ObjectHandle executeMSTKruskal(IsolateThread thread, ObjectHandle graphHandle) {
		Graph<Long, Long> g = globalHandles.get(graphHandle);
		SpanningTree<Long> mst = new KruskalMinimumSpanningTree<>(g).getSpanningTree();
		return globalHandles.create(mst);
	}

	/**
	 * Execute MST Prim on a graph
	 * 
	 * @param thread      the thread
	 * @param graphHandle the graph handle
	 * @return a handle on the result
	 */
	@CEntryPoint(name = Constants.LIB_PREFIX + "mst_exec_prim", exceptionHandler = ObjectHandleExceptionHandler.class)
	public static ObjectHandle executeMSTPrim(IsolateThread thread, ObjectHandle graphHandle) {
		Graph<Long, Long> g = globalHandles.get(graphHandle);
		SpanningTree<Long> mst = new PrimMinimumSpanningTree<>(g).getSpanningTree();
		return globalHandles.create(mst);
	}

	/**
	 * Get the weight of an MST.
	 * 
	 * @param thread    the thread
	 * @param mstHandle the mst handle
	 * @return the weight
	 */
	@CEntryPoint(name = Constants.LIB_PREFIX + "mst_get_weight", exceptionHandler = DoubleExceptionHandler.class)
	public static double getMSTWeight(IsolateThread thread, ObjectHandle mstHandle) {
		SpanningTree<Long> mst = globalHandles.get(mstHandle);
		return mst.getWeight();
	}

	/**
	 * Get an edge iterator for the MST.
	 * 
	 * @param thread    the thread
	 * @param mstHandle the mst handle
	 * @return the edge iterator
	 */
	@CEntryPoint(name = Constants.LIB_PREFIX + "mst_create_eit", exceptionHandler = ObjectHandleExceptionHandler.class)
	public static ObjectHandle createMSTEdgeIterator(IsolateThread thread, ObjectHandle mstHandle) {
		SpanningTree<Long> mst = globalHandles.get(mstHandle);
		Iterator<Long> it = mst.iterator();
		return globalHandles.create(it);
	}

}
