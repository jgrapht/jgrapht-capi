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

import java.util.Random;

import org.graalvm.nativeimage.IsolateThread;
import org.graalvm.nativeimage.ObjectHandle;
import org.graalvm.nativeimage.ObjectHandles;
import org.graalvm.nativeimage.c.function.CEntryPoint;
import org.graalvm.nativeimage.c.type.WordPointer;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.interfaces.HamiltonianCycleAlgorithm;
import org.jgrapht.alg.tour.ChristofidesThreeHalvesApproxMetricTSP;
import org.jgrapht.alg.tour.GreedyHeuristicTSP;
import org.jgrapht.alg.tour.HeldKarpTSP;
import org.jgrapht.alg.tour.NearestInsertionHeuristicTSP;
import org.jgrapht.alg.tour.NearestNeighborHeuristicTSP;
import org.jgrapht.alg.tour.PalmerHamiltonianCycle;
import org.jgrapht.alg.tour.RandomTourTSP;
import org.jgrapht.alg.tour.TwoApproxMetricTSP;
import org.jgrapht.alg.tour.TwoOptHeuristicTSP;
import org.jgrapht.capi.Constants;
import org.jgrapht.capi.Status;
import org.jgrapht.capi.error.StatusReturnExceptionHandler;

public class TourApi {

	private static ObjectHandles globalHandles = ObjectHandles.getGlobal();

	@CEntryPoint(name = Constants.LIB_PREFIX + "tour_tsp_random", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int randomTSP(IsolateThread thread, ObjectHandle handle, long seed, WordPointer res) {
		Graph<Long, Long> g = globalHandles.get(handle);
		HamiltonianCycleAlgorithm<Long, Long> alg = new RandomTourTSP<>(new Random(seed));
		GraphPath<Long, Long> result = alg.getTour(g);
		if (res.isNonNull()) {
			res.write(globalHandles.create(result));
		}
		return Status.SUCCESS.toCEnum();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "tour_tsp_greedy_heuristic", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int greedyHeuristic(IsolateThread thread, ObjectHandle handle, WordPointer res) {
		Graph<Long, Long> g = globalHandles.get(handle);
		HamiltonianCycleAlgorithm<Long, Long> alg = new GreedyHeuristicTSP<>();
		GraphPath<Long, Long> result = alg.getTour(g);
		if (res.isNonNull()) {
			res.write(globalHandles.create(result));
		}
		return Status.SUCCESS.toCEnum();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "tour_tsp_nearest_insertion_heuristic", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int nearestInsertionHeuristic(IsolateThread thread, ObjectHandle handle, WordPointer res) {
		Graph<Long, Long> g = globalHandles.get(handle);
		HamiltonianCycleAlgorithm<Long, Long> alg = new NearestInsertionHeuristicTSP<>();
		GraphPath<Long, Long> result = alg.getTour(g);
		if (res.isNonNull()) {
			res.write(globalHandles.create(result));
		}
		return Status.SUCCESS.toCEnum();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "tour_tsp_nearest_neighbor_heuristic", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int nearestNeighborHeuristic(IsolateThread thread, ObjectHandle handle, long seed, WordPointer res) {
		Graph<Long, Long> g = globalHandles.get(handle);
		HamiltonianCycleAlgorithm<Long, Long> alg = new NearestNeighborHeuristicTSP<>(new Random(seed));
		GraphPath<Long, Long> result = alg.getTour(g);
		if (res.isNonNull()) {
			res.write(globalHandles.create(result));
		}
		return Status.SUCCESS.toCEnum();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "tour_metric_tsp_christofides", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int christofides(IsolateThread thread, ObjectHandle handle, WordPointer res) {
		Graph<Long, Long> g = globalHandles.get(handle);
		HamiltonianCycleAlgorithm<Long, Long> alg = new ChristofidesThreeHalvesApproxMetricTSP<>();
		GraphPath<Long, Long> result = alg.getTour(g);
		if (res.isNonNull()) {
			res.write(globalHandles.create(result));
		}
		return Status.SUCCESS.toCEnum();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "tour_metric_tsp_two_approx", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int twoApprox(IsolateThread thread, ObjectHandle handle, WordPointer res) {
		Graph<Long, Long> g = globalHandles.get(handle);
		HamiltonianCycleAlgorithm<Long, Long> alg = new TwoApproxMetricTSP<>();
		GraphPath<Long, Long> result = alg.getTour(g);
		if (res.isNonNull()) {
			res.write(globalHandles.create(result));
		}
		return Status.SUCCESS.toCEnum();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "tour_tsp_held_karp", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int heldKarp(IsolateThread thread, ObjectHandle handle, WordPointer res) {
		Graph<Long, Long> g = globalHandles.get(handle);
		HamiltonianCycleAlgorithm<Long, Long> alg = new HeldKarpTSP<>();
		GraphPath<Long, Long> result = alg.getTour(g);
		if (res.isNonNull()) {
			res.write(globalHandles.create(result));
		}
		return Status.SUCCESS.toCEnum();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "tour_hamiltonian_palmer", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int palmer(IsolateThread thread, ObjectHandle handle, WordPointer res) {
		Graph<Long, Long> g = globalHandles.get(handle);
		HamiltonianCycleAlgorithm<Long, Long> alg = new PalmerHamiltonianCycle<>();
		GraphPath<Long, Long> result = alg.getTour(g);
		if (res.isNonNull()) {
			res.write(globalHandles.create(result));
		}
		return Status.SUCCESS.toCEnum();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "tour_tsp_two_opt_heuristic", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int twoOptHeuristic(IsolateThread thread, ObjectHandle handle, int k, double minCostImprovement,
			long seed, WordPointer res) {
		Graph<Long, Long> g = globalHandles.get(handle);
		HamiltonianCycleAlgorithm<Long, Long> alg = new TwoOptHeuristicTSP<>(k, new Random(seed), minCostImprovement);
		GraphPath<Long, Long> result = alg.getTour(g);
		if (res.isNonNull()) {
			res.write(globalHandles.create(result));
		}
		return Status.SUCCESS.toCEnum();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "tour_tsp_two_opt_heuristic_improve", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int twoOptHeuristicImprove(IsolateThread thread, ObjectHandle handle, double minCostImprovement,
			long seed, WordPointer res) {
		GraphPath<Long, Long> tour = globalHandles.get(handle);
		TwoOptHeuristicTSP<Long, Long> alg = new TwoOptHeuristicTSP<>(1, new Random(seed), minCostImprovement);
		GraphPath<Long, Long> result = alg.improveTour(tour);
		if (res.isNonNull()) {
			res.write(globalHandles.create(result));
		}
		return Status.SUCCESS.toCEnum();
	}

}
