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
import org.graalvm.nativeimage.c.type.CDoublePointer;
import org.graalvm.nativeimage.c.type.CIntPointer;
import org.graalvm.nativeimage.c.type.WordPointer;
import org.jgrapht.Graph;
import org.jgrapht.alg.interfaces.VertexScoringAlgorithm;
import org.jgrapht.alg.scoring.AlphaCentrality;
import org.jgrapht.alg.scoring.BetweennessCentrality;
import org.jgrapht.alg.scoring.ClosenessCentrality;
import org.jgrapht.alg.scoring.ClusteringCoefficient;
import org.jgrapht.alg.scoring.Coreness;
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
		Graph<Integer, Integer> g = globalHandles.get(graphHandle);

		VertexScoringAlgorithm<Integer, Double> alg = new AlphaCentrality<>(g);
		Map<Integer, Double> result = alg.getScores();

		if (res.isNonNull()) {
			res.write(globalHandles.create(result));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "scoring_exec_custom_alpha_centrality", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeCustomAlphaCentrality(IsolateThread thread, ObjectHandle graphHandle, double dampingFactor,
			double exogenousFactor, int maxIterations, double tolerance, WordPointer res) {
		Graph<Integer, Integer> g = globalHandles.get(graphHandle);

		VertexScoringAlgorithm<Integer, Double> alg = new AlphaCentrality<>(g, dampingFactor, exogenousFactor,
				maxIterations, tolerance);
		Map<Integer, Double> result = alg.getScores();

		if (res.isNonNull()) {
			res.write(globalHandles.create(result));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "scoring_exec_betweenness_centrality", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeBetweennessCentrality(IsolateThread thread, ObjectHandle graphHandle, WordPointer res) {
		Graph<Integer, Integer> g = globalHandles.get(graphHandle);

		VertexScoringAlgorithm<Integer, Double> alg = new BetweennessCentrality<>(g);
		Map<Integer, Double> result = alg.getScores();

		if (res.isNonNull()) {
			res.write(globalHandles.create(result));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "scoring_exec_custom_betweenness_centrality", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeCustomBetweennessCentrality(IsolateThread thread, ObjectHandle graphHandle,
			boolean normalize, WordPointer res) {
		Graph<Integer, Integer> g = globalHandles.get(graphHandle);

		VertexScoringAlgorithm<Integer, Double> alg = new BetweennessCentrality<>(g, normalize);
		Map<Integer, Double> result = alg.getScores();

		if (res.isNonNull()) {
			res.write(globalHandles.create(result));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "scoring_exec_closeness_centrality", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeClosenessCentrality(IsolateThread thread, ObjectHandle graphHandle, WordPointer res) {
		Graph<Integer, Integer> g = globalHandles.get(graphHandle);

		VertexScoringAlgorithm<Integer, Double> alg = new ClosenessCentrality<>(g);
		Map<Integer, Double> result = alg.getScores();

		if (res.isNonNull()) {
			res.write(globalHandles.create(result));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "scoring_exec_custom_closeness_centrality", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeCustomClosenessCentrality(IsolateThread thread, ObjectHandle graphHandle, boolean incoming,
			boolean normalize, WordPointer res) {
		Graph<Integer, Integer> g = globalHandles.get(graphHandle);

		VertexScoringAlgorithm<Integer, Double> alg = new ClosenessCentrality<>(g, incoming, normalize);
		Map<Integer, Double> result = alg.getScores();

		if (res.isNonNull()) {
			res.write(globalHandles.create(result));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "scoring_exec_harmonic_centrality", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeHarmonicCentrality(IsolateThread thread, ObjectHandle graphHandle, WordPointer res) {
		Graph<Integer, Integer> g = globalHandles.get(graphHandle);

		VertexScoringAlgorithm<Integer, Double> alg = new HarmonicCentrality<>(g);
		Map<Integer, Double> result = alg.getScores();

		if (res.isNonNull()) {
			res.write(globalHandles.create(result));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "scoring_exec_custom_harmonic_centrality", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeCustomHarmonicCentrality(IsolateThread thread, ObjectHandle graphHandle, boolean incoming,
			boolean normalize, WordPointer res) {
		Graph<Integer, Integer> g = globalHandles.get(graphHandle);

		VertexScoringAlgorithm<Integer, Double> alg = new HarmonicCentrality<>(g, incoming, normalize);
		Map<Integer, Double> result = alg.getScores();

		if (res.isNonNull()) {
			res.write(globalHandles.create(result));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "scoring_exec_pagerank", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executePagerank(IsolateThread thread, ObjectHandle graphHandle, WordPointer res) {
		Graph<Integer, Integer> g = globalHandles.get(graphHandle);

		VertexScoringAlgorithm<Integer, Double> alg = new PageRank<>(g);
		Map<Integer, Double> result = alg.getScores();

		if (res.isNonNull()) {
			res.write(globalHandles.create(result));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "scoring_exec_custom_pagerank", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeCustomPagerank(IsolateThread thread, ObjectHandle graphHandle, double dampingFactor,
			int maxIterations, double tolerance, WordPointer res) {
		Graph<Integer, Integer> g = globalHandles.get(graphHandle);

		VertexScoringAlgorithm<Integer, Double> alg = new PageRank<>(g, dampingFactor, maxIterations, tolerance);
		Map<Integer, Double> result = alg.getScores();

		if (res.isNonNull()) {
			res.write(globalHandles.create(result));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "scoring_exec_coreness", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeCoreness(IsolateThread thread, ObjectHandle graphHandle, CIntPointer degeneracyRes,
			WordPointer res) {
		Graph<Integer, Integer> g = globalHandles.get(graphHandle);

		Coreness<Integer, Integer> alg = new Coreness<>(g);

		int degeneracy = alg.getDegeneracy();
		Map<Integer, Integer> result = alg.getScores();

		if (degeneracyRes.isNonNull()) {
			degeneracyRes.write(degeneracy);
		}
		if (res.isNonNull()) {
			res.write(globalHandles.create(result));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "scoring_exec_clustering_coefficient", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeClusteringCoefficient(IsolateThread thread, ObjectHandle graphHandle,
			CDoublePointer globalRes, CDoublePointer avgRes, WordPointer res) {
		Graph<Integer, Integer> g = globalHandles.get(graphHandle);

		ClusteringCoefficient<Integer, Integer> alg = new ClusteringCoefficient<>(g);

		Map<Integer, Double> result = alg.getScores();
		double avg = alg.getAverageClusteringCoefficient();
		double global = alg.getGlobalClusteringCoefficient();

		if (avgRes.isNonNull()) {
			avgRes.write(avg);
		}
		if (globalRes.isNonNull()) {
			globalRes.write(global);
		}
		if (res.isNonNull()) {
			res.write(globalHandles.create(result));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

}
