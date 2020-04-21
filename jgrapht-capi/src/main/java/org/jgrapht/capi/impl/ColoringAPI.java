package org.jgrapht.capi.impl;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;

import org.graalvm.nativeimage.IsolateThread;
import org.graalvm.nativeimage.ObjectHandle;
import org.graalvm.nativeimage.ObjectHandles;
import org.graalvm.nativeimage.c.function.CEntryPoint;
import org.graalvm.nativeimage.c.type.CLongPointer;
import org.graalvm.nativeimage.c.type.WordPointer;
import org.jgrapht.Graph;
import org.jgrapht.alg.color.BrownBacktrackColoring;
import org.jgrapht.alg.color.ColorRefinementAlgorithm;
import org.jgrapht.alg.color.GreedyColoring;
import org.jgrapht.alg.color.LargestDegreeFirstColoring;
import org.jgrapht.alg.color.RandomGreedyColoring;
import org.jgrapht.alg.color.SaturationDegreeColoring;
import org.jgrapht.alg.color.SmallestDegreeLastColoring;
import org.jgrapht.alg.interfaces.VertexColoringAlgorithm;
import org.jgrapht.alg.interfaces.VertexColoringAlgorithm.Coloring;
import org.jgrapht.capi.Constants;
import org.jgrapht.capi.Status;
import org.jgrapht.capi.error.StatusReturnExceptionHandler;

public class ColoringAPI {

	private static ObjectHandles globalHandles = ObjectHandles.getGlobal();

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "coloring_exec_greedy", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeGreedyColoring(IsolateThread thread, ObjectHandle graphHandle, WordPointer res) {
		return executeColoring(thread, graphHandle, g -> new GreedyColoring<>(g), res);
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "coloring_exec_greedy_smallestdegreelast", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeSmallestDegreeLastColoring(IsolateThread thread, ObjectHandle graphHandle,
			WordPointer res) {
		return executeColoring(thread, graphHandle, g -> new SmallestDegreeLastColoring<>(g), res);
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "coloring_exec_backtracking_brown", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeBacktrackingBrown(IsolateThread thread, ObjectHandle graphHandle, WordPointer res) {
		return executeColoring(thread, graphHandle, g -> new BrownBacktrackColoring<>(g), res);
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "coloring_exec_greedy_largestdegreefirst", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeLargestDegreeFirstColoring(IsolateThread thread, ObjectHandle graphHandle,
			WordPointer res) {
		return executeColoring(thread, graphHandle, g -> new LargestDegreeFirstColoring<>(g), res);
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "coloring_exec_greedy_random", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeRandomGreedyWithSeed(IsolateThread thread, ObjectHandle graphHandle, WordPointer res) {
		return executeColoring(thread, graphHandle, g -> new RandomGreedyColoring<>(g), res);
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "coloring_exec_greedy_random_with_seed", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeRandomGreedy(IsolateThread thread, ObjectHandle graphHandle, long seed, WordPointer res) {
		return executeColoring(thread, graphHandle, g -> new RandomGreedyColoring<>(g, new Random(seed)), res);
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "coloring_exec_greedy_dsatur", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeGreedyDSatur(IsolateThread thread, ObjectHandle graphHandle, WordPointer res) {
		return executeColoring(thread, graphHandle, g -> new SaturationDegreeColoring<>(g), res);
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "coloring_exec_color_refinement", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeColorRefinement(IsolateThread thread, ObjectHandle graphHandle, WordPointer res) {
		return executeColoring(thread, graphHandle, g -> new ColorRefinementAlgorithm<>(g), res);
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "coloring_get_number_colors", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int getNumberOfColors(IsolateThread thread, ObjectHandle cHandle, CLongPointer res) {
		Coloring<Long> c = globalHandles.get(cHandle);
		if (res.isNonNull()) {
			res.write(c.getNumberColors());
		}
		return Status.SUCCESS.toCEnum();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "coloring_get_vertex_color_map", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int getColorMap(IsolateThread thread, ObjectHandle cHandle, WordPointer res) {
		Coloring<Long> c = globalHandles.get(cHandle);
		Map<Long, Long> colors = new LinkedHashMap<>();
		c.getColors().entrySet().stream().forEach(e -> {
			colors.put(e.getKey(), (long) e.getValue());
		});
		if (res.isNonNull()) {
			res.write(globalHandles.create(colors));
		}
		return Status.SUCCESS.toCEnum();
	}

	private static int executeColoring(IsolateThread thread, ObjectHandle graphHandle,
			Function<Graph<Long, Long>, VertexColoringAlgorithm<Long>> algProvider, WordPointer res) {
		Graph<Long, Long> g = globalHandles.get(graphHandle);
		VertexColoringAlgorithm<Long> alg = algProvider.apply(g);
		Coloring<Long> coloring = alg.getColoring();
		if (res.isNonNull()) {
			res.write(globalHandles.create(coloring));
		}
		return Status.SUCCESS.toCEnum();
	}
}
