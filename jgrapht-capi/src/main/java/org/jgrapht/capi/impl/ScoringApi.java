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

import java.util.Map;

import org.graalvm.nativeimage.IsolateThread;
import org.graalvm.nativeimage.ObjectHandle;
import org.graalvm.nativeimage.ObjectHandles;
import org.graalvm.nativeimage.c.function.CEntryPoint;
import org.graalvm.nativeimage.c.type.WordPointer;
import org.jgrapht.Graph;
import org.jgrapht.alg.interfaces.VertexScoringAlgorithm;
import org.jgrapht.alg.scoring.AlphaCentrality;
import org.jgrapht.alg.scoring.BetweennessCentrality;
import org.jgrapht.alg.scoring.ClosenessCentrality;
import org.jgrapht.alg.scoring.HarmonicCentrality;
import org.jgrapht.alg.scoring.PageRank;
import org.jgrapht.capi.Constants;
import org.jgrapht.capi.JGraphTContext.Status;
import org.jgrapht.capi.error.StatusReturnExceptionHandler;

public class ScoringApi {

	private static ObjectHandles globalHandles = ObjectHandles.getGlobal();

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "scoring_exec_alpha_centrality", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeAlphaCentrality(IsolateThread thread, ObjectHandle graphHandle, WordPointer res) {
		Graph<Long, Long> g = globalHandles.get(graphHandle);

		VertexScoringAlgorithm<Long, Double> alg = new AlphaCentrality<>(g);
		Map<Long, Double> result = alg.getScores();

		if (res.isNonNull()) {
			res.write(globalHandles.create(result));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "scoring_exec_custom_alpha_centrality", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeCustomAlphaCentrality(IsolateThread thread, ObjectHandle graphHandle, double dampingFactor,
			double exogenousFactor, int maxIterations, double tolerance, WordPointer res) {
		Graph<Long, Long> g = globalHandles.get(graphHandle);

		VertexScoringAlgorithm<Long, Double> alg = new AlphaCentrality<>(g, dampingFactor, exogenousFactor,
				maxIterations, tolerance);
		Map<Long, Double> result = alg.getScores();

		if (res.isNonNull()) {
			res.write(globalHandles.create(result));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "scoring_exec_betweenness_centrality", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeBetweennessCentrality(IsolateThread thread, ObjectHandle graphHandle, WordPointer res) {
		Graph<Long, Long> g = globalHandles.get(graphHandle);

		VertexScoringAlgorithm<Long, Double> alg = new BetweennessCentrality<>(g);
		Map<Long, Double> result = alg.getScores();

		if (res.isNonNull()) {
			res.write(globalHandles.create(result));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "scoring_exec_custom_betweenness_centrality", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeCustomBetweennessCentrality(IsolateThread thread, ObjectHandle graphHandle,
			boolean normalize, WordPointer res) {
		Graph<Long, Long> g = globalHandles.get(graphHandle);

		VertexScoringAlgorithm<Long, Double> alg = new BetweennessCentrality<>(g, normalize);
		Map<Long, Double> result = alg.getScores();

		if (res.isNonNull()) {
			res.write(globalHandles.create(result));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "scoring_exec_closeness_centrality", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeClosenessCentrality(IsolateThread thread, ObjectHandle graphHandle, WordPointer res) {
		Graph<Long, Long> g = globalHandles.get(graphHandle);

		VertexScoringAlgorithm<Long, Double> alg = new ClosenessCentrality<>(g);
		Map<Long, Double> result = alg.getScores();

		if (res.isNonNull()) {
			res.write(globalHandles.create(result));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "scoring_exec_custom_closeness_centrality", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeCustomClosenessCentrality(IsolateThread thread, ObjectHandle graphHandle, boolean incoming,
			boolean normalize, WordPointer res) {
		Graph<Long, Long> g = globalHandles.get(graphHandle);

		VertexScoringAlgorithm<Long, Double> alg = new ClosenessCentrality<>(g, incoming, normalize);
		Map<Long, Double> result = alg.getScores();

		if (res.isNonNull()) {
			res.write(globalHandles.create(result));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "scoring_exec_harmonic_centrality", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeHarmonicCentrality(IsolateThread thread, ObjectHandle graphHandle, WordPointer res) {
		Graph<Long, Long> g = globalHandles.get(graphHandle);

		VertexScoringAlgorithm<Long, Double> alg = new HarmonicCentrality<>(g);
		Map<Long, Double> result = alg.getScores();

		if (res.isNonNull()) {
			res.write(globalHandles.create(result));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "scoring_exec_custom_harmonic_centrality", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeCustomHarmonicCentrality(IsolateThread thread, ObjectHandle graphHandle, boolean incoming,
			boolean normalize, WordPointer res) {
		Graph<Long, Long> g = globalHandles.get(graphHandle);

		VertexScoringAlgorithm<Long, Double> alg = new HarmonicCentrality<>(g, incoming, normalize);
		Map<Long, Double> result = alg.getScores();

		if (res.isNonNull()) {
			res.write(globalHandles.create(result));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "scoring_exec_pagerank", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executePagerank(IsolateThread thread, ObjectHandle graphHandle, WordPointer res) {
		Graph<Long, Long> g = globalHandles.get(graphHandle);

		VertexScoringAlgorithm<Long, Double> alg = new PageRank<>(g);
		Map<Long, Double> result = alg.getScores();

		if (res.isNonNull()) {
			res.write(globalHandles.create(result));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "scoring_exec_custom_pagerank", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeCustomPagerank(IsolateThread thread, ObjectHandle graphHandle, double dampingFactor,
			int maxIterations, double tolerance, WordPointer res) {
		Graph<Long, Long> g = globalHandles.get(graphHandle);

		VertexScoringAlgorithm<Long, Double> alg = new PageRank<>(g, dampingFactor, maxIterations, tolerance);
		Map<Long, Double> result = alg.getScores();

		if (res.isNonNull()) {
			res.write(globalHandles.create(result));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	// TODO: Add clustering coefficient
	// TODO: Add coreness

}
