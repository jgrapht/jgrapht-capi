package org.jgrapht.capi.impl;

import org.graalvm.nativeimage.IsolateThread;
import org.graalvm.nativeimage.ObjectHandle;
import org.graalvm.nativeimage.ObjectHandles;
import org.graalvm.nativeimage.c.function.CEntryPoint;
import org.jgrapht.Graph;
import org.jgrapht.GraphTests;
import org.jgrapht.capi.Constants;
import org.jgrapht.capi.error.BooleanExceptionHandler;

public class GraphTestsApi {

	private static ObjectHandles globalHandles = ObjectHandles.getGlobal();

	@CEntryPoint(name = Constants.LIB_PREFIX + "graph_test_is_empty", exceptionHandler = BooleanExceptionHandler.class)
	public static boolean isEmpty(IsolateThread thread, ObjectHandle graphHandle) {
		Graph<Long, Long> g = globalHandles.get(graphHandle);
		return GraphTests.isEmpty(g);
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "graph_test_is_simple", exceptionHandler = BooleanExceptionHandler.class)
	public static boolean isSimple(IsolateThread thread, ObjectHandle graphHandle) {
		Graph<Long, Long> g = globalHandles.get(graphHandle);
		return GraphTests.isSimple(g);
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "graph_test_has_selfloops", exceptionHandler = BooleanExceptionHandler.class)
	public static boolean hasSelfLoops(IsolateThread thread, ObjectHandle graphHandle) {
		Graph<Long, Long> g = globalHandles.get(graphHandle);
		return GraphTests.hasSelfLoops(g);
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "graph_test_has_multipleedges", exceptionHandler = BooleanExceptionHandler.class)
	public static boolean hasMultipleEdges(IsolateThread thread, ObjectHandle graphHandle) {
		Graph<Long, Long> g = globalHandles.get(graphHandle);
		return GraphTests.hasMultipleEdges(g);
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "graph_test_is_complete", exceptionHandler = BooleanExceptionHandler.class)
	public static boolean isComplete(IsolateThread thread, ObjectHandle graphHandle) {
		Graph<Long, Long> g = globalHandles.get(graphHandle);
		return GraphTests.isComplete(g);
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "graph_test_is_weekly_connected", exceptionHandler = BooleanExceptionHandler.class)
	public static boolean isWeaklyConnected(IsolateThread thread, ObjectHandle graphHandle) {
		Graph<Long, Long> g = globalHandles.get(graphHandle);
		return GraphTests.isWeaklyConnected(g);
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "graph_test_is_strongly_connected", exceptionHandler = BooleanExceptionHandler.class)
	public static boolean isStronglyConnected(IsolateThread thread, ObjectHandle graphHandle) {
		Graph<Long, Long> g = globalHandles.get(graphHandle);
		return GraphTests.isStronglyConnected(g);
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "graph_test_is_tree", exceptionHandler = BooleanExceptionHandler.class)
	public static boolean isTree(IsolateThread thread, ObjectHandle graphHandle) {
		Graph<Long, Long> g = globalHandles.get(graphHandle);
		return GraphTests.isTree(g);
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "graph_test_is_forest", exceptionHandler = BooleanExceptionHandler.class)
	public static boolean isForest(IsolateThread thread, ObjectHandle graphHandle) {
		Graph<Long, Long> g = globalHandles.get(graphHandle);
		return GraphTests.isForest(g);
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "graph_test_is_overfull", exceptionHandler = BooleanExceptionHandler.class)
	public static boolean isOverfull(IsolateThread thread, ObjectHandle graphHandle) {
		Graph<Long, Long> g = globalHandles.get(graphHandle);
		return GraphTests.isOverfull(g);
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "graph_test_is_split", exceptionHandler = BooleanExceptionHandler.class)
	public static boolean isSplit(IsolateThread thread, ObjectHandle graphHandle) {
		Graph<Long, Long> g = globalHandles.get(graphHandle);
		return GraphTests.isSplit(g);
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "graph_test_is_bipartite", exceptionHandler = BooleanExceptionHandler.class)
	public static boolean isBipartite(IsolateThread thread, ObjectHandle graphHandle) {
		Graph<Long, Long> g = globalHandles.get(graphHandle);
		return GraphTests.isBipartite(g);
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "graph_test_is_cubic", exceptionHandler = BooleanExceptionHandler.class)
	public static boolean isCubic(IsolateThread thread, ObjectHandle graphHandle) {
		Graph<Long, Long> g = globalHandles.get(graphHandle);
		return GraphTests.isCubic(g);
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "graph_test_is_eulerian", exceptionHandler = BooleanExceptionHandler.class)
	public static boolean isEulerian(IsolateThread thread, ObjectHandle graphHandle) {
		Graph<Long, Long> g = globalHandles.get(graphHandle);
		return GraphTests.isEulerian(g);
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "graph_test_is_chordal", exceptionHandler = BooleanExceptionHandler.class)
	public static boolean isChordal(IsolateThread thread, ObjectHandle graphHandle) {
		Graph<Long, Long> g = globalHandles.get(graphHandle);
		return GraphTests.isChordal(g);
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "graph_test_is_weakly_chordal", exceptionHandler = BooleanExceptionHandler.class)
	public static boolean isWeaklyChordal(IsolateThread thread, ObjectHandle graphHandle) {
		Graph<Long, Long> g = globalHandles.get(graphHandle);
		return GraphTests.isWeaklyChordal(g);
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "graph_test_has_ore", exceptionHandler = BooleanExceptionHandler.class)
	public static boolean hasOreProperty(IsolateThread thread, ObjectHandle graphHandle) {
		Graph<Long, Long> g = globalHandles.get(graphHandle);
		return GraphTests.hasOreProperty(g);
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "graph_test_is_trianglefree", exceptionHandler = BooleanExceptionHandler.class)
	public static boolean isTriangleFree(IsolateThread thread, ObjectHandle graphHandle) {
		Graph<Long, Long> g = globalHandles.get(graphHandle);
		return GraphTests.isTriangleFree(g);
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "graph_test_is_perfect", exceptionHandler = BooleanExceptionHandler.class)
	public static boolean isPerfect(IsolateThread thread, ObjectHandle graphHandle) {
		Graph<Long, Long> g = globalHandles.get(graphHandle);
		return GraphTests.isPerfect(g);
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "graph_test_is_planar", exceptionHandler = BooleanExceptionHandler.class)
	public static boolean isPlanar(IsolateThread thread, ObjectHandle graphHandle) {
		Graph<Long, Long> g = globalHandles.get(graphHandle);
		return GraphTests.isPlanar(g);
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "graph_test_is_kuratowski_subdivision", exceptionHandler = BooleanExceptionHandler.class)
	public static boolean isKuratowskiSubdivision(IsolateThread thread, ObjectHandle graphHandle) {
		Graph<Long, Long> g = globalHandles.get(graphHandle);
		return GraphTests.isKuratowskiSubdivision(g);
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "graph_test_is_k33_subdivision", exceptionHandler = BooleanExceptionHandler.class)
	public static boolean isK33Subdivision(IsolateThread thread, ObjectHandle graphHandle) {
		Graph<Long, Long> g = globalHandles.get(graphHandle);
		return GraphTests.isK33Subdivision(g);
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "graph_test_is_k5_subdivision", exceptionHandler = BooleanExceptionHandler.class)
	public static boolean isK5Subdivision(IsolateThread thread, ObjectHandle graphHandle) {
		Graph<Long, Long> g = globalHandles.get(graphHandle);
		return GraphTests.isK5Subdivision(g);
	}

}
