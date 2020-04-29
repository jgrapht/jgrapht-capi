/*
 * (C) Copyright 2020, by Dimitrios Michail.
 *
 * JGraphT C-API
 *
 * See the CONTRIBUTORS.md file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the
 * GNU Lesser General Public License v2.1 or later
 * which is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1-standalone.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR LGPL-2.1-or-later
 */
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
import org.jgrapht.capi.enums.Status;
import org.jgrapht.capi.error.StatusReturnExceptionHandler;

public class ColoringApi {

	private static ObjectHandles globalHandles = ObjectHandles.getGlobal();

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "coloring_exec_greedy", exceptionHandler = StatusReturnExceptionHandler.class)
	public static Status executeGreedyColoring(IsolateThread thread, ObjectHandle graphHandle, CLongPointer resColors,
			WordPointer resColorsMap) {
		return executeColoring(thread, graphHandle, g -> new GreedyColoring<>(g), resColors, resColorsMap);
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "coloring_exec_greedy_smallestdegreelast", exceptionHandler = StatusReturnExceptionHandler.class)
	public static Status executeSmallestDegreeLastColoring(IsolateThread thread, ObjectHandle graphHandle,
			CLongPointer resColors, WordPointer resColorsMap) {
		return executeColoring(thread, graphHandle, g -> new SmallestDegreeLastColoring<>(g), resColors, resColorsMap);
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "coloring_exec_backtracking_brown", exceptionHandler = StatusReturnExceptionHandler.class)
	public static Status executeBacktrackingBrown(IsolateThread thread, ObjectHandle graphHandle, CLongPointer resColors,
			WordPointer resColorsMap) {
		return executeColoring(thread, graphHandle, g -> new BrownBacktrackColoring<>(g), resColors, resColorsMap);
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "coloring_exec_greedy_largestdegreefirst", exceptionHandler = StatusReturnExceptionHandler.class)
	public static Status executeLargestDegreeFirstColoring(IsolateThread thread, ObjectHandle graphHandle,
			CLongPointer resColors, WordPointer resColorsMap) {
		return executeColoring(thread, graphHandle, g -> new LargestDegreeFirstColoring<>(g), resColors, resColorsMap);
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "coloring_exec_greedy_random", exceptionHandler = StatusReturnExceptionHandler.class)
	public static Status executeRandomGreedyWithSeed(IsolateThread thread, ObjectHandle graphHandle,
			CLongPointer resColors, WordPointer resColorsMap) {
		return executeColoring(thread, graphHandle, g -> new RandomGreedyColoring<>(g), resColors, resColorsMap);
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "coloring_exec_greedy_random_with_seed", exceptionHandler = StatusReturnExceptionHandler.class)
	public static Status executeRandomGreedy(IsolateThread thread, ObjectHandle graphHandle, long seed,
			CLongPointer resColors, WordPointer resColorsMap) {
		return executeColoring(thread, graphHandle, g -> new RandomGreedyColoring<>(g, new Random(seed)), resColors,
				resColorsMap);
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "coloring_exec_greedy_dsatur", exceptionHandler = StatusReturnExceptionHandler.class)
	public static Status executeGreedyDSatur(IsolateThread thread, ObjectHandle graphHandle, CLongPointer resColors,
			WordPointer resColorsMap) {
		return executeColoring(thread, graphHandle, g -> new SaturationDegreeColoring<>(g), resColors, resColorsMap);
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "coloring_exec_color_refinement", exceptionHandler = StatusReturnExceptionHandler.class)
	public static Status executeColorRefinement(IsolateThread thread, ObjectHandle graphHandle, CLongPointer resColors,
			WordPointer resColorsMap) {
		return executeColoring(thread, graphHandle, g -> new ColorRefinementAlgorithm<>(g), resColors, resColorsMap);
	}

	private static Status executeColoring(IsolateThread thread, ObjectHandle graphHandle,
			Function<Graph<Long, Long>, VertexColoringAlgorithm<Long>> algProvider, CLongPointer resColors,
			WordPointer resColorsMap) {
		Graph<Long, Long> g = globalHandles.get(graphHandle);
		VertexColoringAlgorithm<Long> alg = algProvider.apply(g);
		Coloring<Long> coloring = alg.getColoring();

		Map<Long, Long> colors = new LinkedHashMap<>();
		coloring.getColors().entrySet().stream().forEach(e -> {
			colors.put(e.getKey(), (long) e.getValue());
		});

		if (resColors.isNonNull()) {
			resColors.write(coloring.getNumberColors());
		}
		if (resColorsMap.isNonNull()) {
			resColorsMap.write(globalHandles.create(colors));
		}
		return Status.SUCCESS;
	}
}
