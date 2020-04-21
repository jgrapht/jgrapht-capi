package org.jgrapht.capi.impl;

import java.util.Iterator;

import org.graalvm.nativeimage.IsolateThread;
import org.graalvm.nativeimage.ObjectHandle;
import org.graalvm.nativeimage.ObjectHandles;
import org.graalvm.nativeimage.c.function.CEntryPoint;
import org.graalvm.nativeimage.c.type.CDoublePointer;
import org.graalvm.nativeimage.c.type.WordPointer;
import org.jgrapht.Graph;
import org.jgrapht.alg.interfaces.SpanningTreeAlgorithm.SpanningTree;
import org.jgrapht.alg.spanning.KruskalMinimumSpanningTree;
import org.jgrapht.alg.spanning.PrimMinimumSpanningTree;
import org.jgrapht.capi.Constants;
import org.jgrapht.capi.Status;
import org.jgrapht.capi.error.StatusReturnExceptionHandler;

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
			+ "mst_exec_kruskal", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeMSTKruskal(IsolateThread thread, ObjectHandle graph, WordPointer res) {
		Graph<Long, Long> g = globalHandles.get(graph);
		SpanningTree<Long> mst = new KruskalMinimumSpanningTree<>(g).getSpanningTree();
		if (res.isNonNull()) {
			res.write(globalHandles.create(mst));
		}
		return Status.SUCCESS.toCEnum();
	}

	/**
	 * Execute MST Prim on a graph
	 * 
	 * @param thread      the thread
	 * @param graphHandle the graph handle
	 * @return a handle on the result
	 */
	@CEntryPoint(name = Constants.LIB_PREFIX + "mst_exec_prim", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeMSTPrim(IsolateThread thread, ObjectHandle graph, WordPointer res) {
		Graph<Long, Long> g = globalHandles.get(graph);
		SpanningTree<Long> mst = new PrimMinimumSpanningTree<>(g).getSpanningTree();
		if (res.isNonNull()) {
			res.write(globalHandles.create(mst));
		}
		return Status.SUCCESS.toCEnum();
	}

	/**
	 * Get the weight of an MST.
	 * 
	 * @param thread    the thread
	 * @param mstHandle the mst handle
	 * @return the weight
	 */
	@CEntryPoint(name = Constants.LIB_PREFIX + "mst_get_weight", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int getMSTWeight(IsolateThread thread, ObjectHandle mstHandle, CDoublePointer res) {
		SpanningTree<Long> mst = globalHandles.get(mstHandle);
		if (res.isNonNull()) {
			res.write(mst.getWeight());
		}
		return Status.SUCCESS.toCEnum();
	}

	/**
	 * Get an edge iterator for the MST.
	 * 
	 * @param thread    the thread
	 * @param mstHandle the mst handle
	 * @return the edge iterator
	 */
	@CEntryPoint(name = Constants.LIB_PREFIX + "mst_create_eit", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int createMSTEdgeIterator(IsolateThread thread, ObjectHandle mstHandle, WordPointer res) {
		SpanningTree<Long> mst = globalHandles.get(mstHandle);
		Iterator<Long> it = mst.iterator();
		if (res.isNonNull()) {
			res.write(globalHandles.create(it));
		}
		return Status.SUCCESS.toCEnum();
	}

}
