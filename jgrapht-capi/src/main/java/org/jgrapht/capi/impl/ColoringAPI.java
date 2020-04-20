package org.jgrapht.capi.impl;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;

import org.graalvm.nativeimage.IsolateThread;
import org.graalvm.nativeimage.ObjectHandle;
import org.graalvm.nativeimage.ObjectHandles;
import org.graalvm.nativeimage.c.function.CEntryPoint;
import org.graalvm.word.WordFactory;
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
import org.jgrapht.capi.Errors;
import org.jgrapht.capi.Status;

public class ColoringAPI {

	private static ObjectHandles globalHandles = ObjectHandles.getGlobal();

	@CEntryPoint(name = Constants.LIB_PREFIX + "coloring_exec_greedy")
	public static ObjectHandle executeGreedyColoring(IsolateThread thread, ObjectHandle graphHandle) {
		return executeColoring(thread, graphHandle, g -> new GreedyColoring<>(g));
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "coloring_exec_greedy_smallestdegreelast")
	public static ObjectHandle executeSmallestDegreeLastColoring(IsolateThread thread, ObjectHandle graphHandle) {
		return executeColoring(thread, graphHandle, g -> new SmallestDegreeLastColoring<>(g));
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "coloring_exec_backtracking_brown")
	public static ObjectHandle executeBacktrackingBrown(IsolateThread thread, ObjectHandle graphHandle) {
		return executeColoring(thread, graphHandle, g -> new BrownBacktrackColoring<>(g));
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "coloring_exec_greedy_largestdegreefirst")
	public static ObjectHandle executeLargestDegreeFirstColoring(IsolateThread thread, ObjectHandle graphHandle) {
		return executeColoring(thread, graphHandle, g -> new LargestDegreeFirstColoring<>(g));
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "coloring_exec_greedy_random_with_seed")
	public static ObjectHandle executeRandomGreedyWithSeed(IsolateThread thread, ObjectHandle graphHandle, long seed) {
		return executeColoring(thread, graphHandle, g -> new RandomGreedyColoring<>(g));
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "coloring_exec_greedy_random")
	public static ObjectHandle executeRandomGreedy(IsolateThread thread, ObjectHandle graphHandle, long seed) {
		return executeColoring(thread, graphHandle, g -> new RandomGreedyColoring<>(g, new Random(seed)));
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "coloring_exec_greedy_dsatur")
	public static ObjectHandle executeGreedyDSatur(IsolateThread thread, ObjectHandle graphHandle) {
		return executeColoring(thread, graphHandle, g -> new SaturationDegreeColoring<>(g));
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "coloring_exec_color_refinement")
	public static ObjectHandle executeColorRefinement(IsolateThread thread, ObjectHandle graphHandle) {
		return executeColoring(thread, graphHandle, g -> new ColorRefinementAlgorithm<>(g));
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "coloring_get_number_colors")
	public static long getNumberOfColors(IsolateThread thread, ObjectHandle cHandle) {
		try {
			Coloring<Long> c = globalHandles.get(cHandle);
			return c.getNumberColors();
		} catch (IllegalArgumentException e) {
			Errors.setError(Status.ILLEGAL_ARGUMENT, e.getMessage());
		} catch (Exception e) {
			Errors.setError(Status.ERROR, e.getMessage());
		}
		return 0L;
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "coloring_get_vertex_color_map")
	public static ObjectHandle getColorMap(IsolateThread thread, ObjectHandle cHandle) {
		try {
			Coloring<Long> c = globalHandles.get(cHandle);
			Map<Long, Long> colors = new LinkedHashMap<>();
			c.getColors().entrySet().stream().forEach(e -> {
				colors.put(e.getKey(), (long) e.getValue());
			});
			return globalHandles.create(colors);
		} catch (IllegalArgumentException e) {
			Errors.setError(Status.ILLEGAL_ARGUMENT, e.getMessage());
		} catch (Exception e) {
			Errors.setError(Status.ERROR, e.getMessage());
		}
		return WordFactory.nullPointer();
	}

	private static ObjectHandle executeColoring(IsolateThread thread, ObjectHandle graphHandle,
			Function<Graph<Long, Long>, VertexColoringAlgorithm<Long>> algProvider) {
		try {
			Graph<Long, Long> g = globalHandles.get(graphHandle);
			if (!g.getType().isUndirected()) {
				Errors.setError(Status.GRAPH_NOT_UNDIRECTED, "Only undirected graph supported");
				return WordFactory.nullPointer();
			}
			VertexColoringAlgorithm<Long> alg = algProvider.apply(g);
			Coloring<Long> coloring = alg.getColoring();
			return globalHandles.create(coloring);
		} catch (IllegalArgumentException e) {
			Errors.setError(Status.ILLEGAL_ARGUMENT, e.getMessage());
		} catch (Exception e) {
			Errors.setError(Status.ERROR, e.getMessage());
		}
		return WordFactory.nullPointer();
	}
}
