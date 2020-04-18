package org.jgrapht.nlib.api;

import java.util.Iterator;

import org.graalvm.nativeimage.IsolateThread;
import org.graalvm.nativeimage.ObjectHandle;
import org.graalvm.nativeimage.ObjectHandles;
import org.graalvm.nativeimage.c.function.CEntryPoint;
import org.graalvm.word.WordFactory;
import org.jgrapht.Graph;
import org.jgrapht.alg.interfaces.SpanningTreeAlgorithm.SpanningTree;
import org.jgrapht.alg.spanning.KruskalMinimumSpanningTree;
import org.jgrapht.alg.spanning.PrimMinimumSpanningTree;
import org.jgrapht.nlib.Constants;
import org.jgrapht.nlib.Errors;
import org.jgrapht.nlib.GraphLookupException;
import org.jgrapht.nlib.Status;

public class MstAPI {

	private static ObjectHandles globalHandles = ObjectHandles.getGlobal();

	/**
	 * Execute MST kruskal on a graph
	 * 
	 * @param thread      the thread
	 * @param graphHandle the graph handle
	 * @return a handle on the result
	 */
	@CEntryPoint(name = Constants.LIB_PREFIX + "mst_exec_kruskal")
	public static ObjectHandle executeMSTKruskal(IsolateThread thread, ObjectHandle graphHandle) {
		try {
			Graph<Long, Long> graph = GraphAPI.getGraph(graphHandle);
			SpanningTree<Long> mst = new KruskalMinimumSpanningTree<>(graph).getSpanningTree();
			return globalHandles.create(mst);
		} catch (GraphLookupException e) {
			Errors.setError(Status.INVALID_GRAPH);
		} catch (Exception e) {
			Errors.setError(Status.GENERIC_ERROR);
		}
		return WordFactory.nullPointer();
	}

	/**
	 * Execute MST Prim on a graph
	 * 
	 * @param thread      the thread
	 * @param graphHandle the graph handle
	 * @return a handle on the result
	 */
	@CEntryPoint(name = Constants.LIB_PREFIX + "mst_exec_prim")
	public static ObjectHandle executeMSTPrim(IsolateThread thread, ObjectHandle graphHandle) {
		try {
			Graph<Long, Long> graph = GraphAPI.getGraph(graphHandle);
			SpanningTree<Long> mst = new PrimMinimumSpanningTree<>(graph).getSpanningTree();
			return globalHandles.create(mst);
		} catch (GraphLookupException e) {
			Errors.setError(Status.INVALID_GRAPH);
		} catch (Exception e) {
			Errors.setError(Status.GENERIC_ERROR);
		}
		return WordFactory.nullPointer();
	}

	/**
	 * Get the weight of an MST.
	 * 
	 * @param thread    the thread
	 * @param mstHandle the mst handle
	 * @return the weight
	 */
	@CEntryPoint(name = Constants.LIB_PREFIX + "mst_get_weight")
	public static double getMSTWeight(IsolateThread thread, ObjectHandle mstHandle) {
		try {
			SpanningTree<Long> mst = globalHandles.get(mstHandle);
			return mst.getWeight();
		} catch (IllegalArgumentException e) {
			Errors.setError(Status.ILLEGAL_ARGUMENT);
		} catch (Exception e) {
			Errors.setError(Status.GENERIC_ERROR);
		}
		return 0d;
	}

	/**
	 * Get an edge iterator for the MST.
	 * 
	 * @param thread    the thread
	 * @param mstHandle the mst handle
	 * @return the edge iterator
	 */
	@CEntryPoint(name = Constants.LIB_PREFIX + "mst_create_eit")
	public static ObjectHandle createMSTEdgeIterator(IsolateThread thread, ObjectHandle mstHandle) {
		try {
			SpanningTree<Long> mst = globalHandles.get(mstHandle);
			Iterator<Long> it = mst.iterator();
			return globalHandles.create(it);
		} catch (IllegalArgumentException e) {
			Errors.setError(Status.ILLEGAL_ARGUMENT);
		} catch (Exception e) {
			Errors.setError(Status.GENERIC_ERROR);
		}
		return WordFactory.nullPointer();
	}

}
