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
import org.jgrapht.Graph;
import org.jgrapht.capi.Constants;
import org.jgrapht.capi.JGraphTContext.Status;
import org.jgrapht.capi.error.StatusReturnExceptionHandler;
import org.jgrapht.generate.BarabasiAlbertForestGenerator;
import org.jgrapht.generate.BarabasiAlbertGraphGenerator;
import org.jgrapht.generate.ComplementGraphGenerator;
import org.jgrapht.generate.CompleteBipartiteGraphGenerator;
import org.jgrapht.generate.CompleteGraphGenerator;
import org.jgrapht.generate.EmptyGraphGenerator;
import org.jgrapht.generate.GeneralizedPetersenGraphGenerator;
import org.jgrapht.generate.GnmRandomGraphGenerator;
import org.jgrapht.generate.GnpRandomGraphGenerator;
import org.jgrapht.generate.GraphGenerator;
import org.jgrapht.generate.GridGraphGenerator;
import org.jgrapht.generate.HyperCubeGraphGenerator;
import org.jgrapht.generate.KleinbergSmallWorldGraphGenerator;
import org.jgrapht.generate.LinearGraphGenerator;
import org.jgrapht.generate.LinearizedChordDiagramGraphGenerator;
import org.jgrapht.generate.RandomRegularGraphGenerator;
import org.jgrapht.generate.RingGraphGenerator;
import org.jgrapht.generate.ScaleFreeGraphGenerator;
import org.jgrapht.generate.StarGraphGenerator;
import org.jgrapht.generate.WattsStrogatzGraphGenerator;
import org.jgrapht.generate.WheelGraphGenerator;
import org.jgrapht.generate.WindmillGraphsGenerator;
import org.jgrapht.generate.WindmillGraphsGenerator.Mode;

public class GenerateApi {

	private static ObjectHandles globalHandles = ObjectHandles.getGlobal();

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.ANY_ANY
			+ "generate_barabasi_albert", exceptionHandler = StatusReturnExceptionHandler.class)
	public static <V, E> int barabasiAlbertGraphGenerator(IsolateThread thread, ObjectHandle graph, int m0, int m,
			int n, long seed) {
		Graph<V, E> g = globalHandles.get(graph);
		GraphGenerator<V, E, ?> gen = new BarabasiAlbertGraphGenerator<>(m0, m, n, seed);
		gen.generateGraph(g);
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.ANY_ANY
			+ "generate_barabasi_albert_forest", exceptionHandler = StatusReturnExceptionHandler.class)
	public static <V, E> int barabasiAlbertForestGenerator(IsolateThread thread, ObjectHandle graph, int t, int n,
			long seed) {
		Graph<V, E> g = globalHandles.get(graph);
		GraphGenerator<V, E, ?> gen = new BarabasiAlbertForestGenerator<>(t, n, seed);
		gen.generateGraph(g);
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.ANY_ANY
			+ "generate_complete", exceptionHandler = StatusReturnExceptionHandler.class)
	public static <V, E> int completeGraphGenerator(IsolateThread thread, ObjectHandle graph, int n) {
		Graph<V, E> g = globalHandles.get(graph);
		GraphGenerator<V, E, ?> gen = new CompleteGraphGenerator<>(n);
		gen.generateGraph(g);
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.ANY_ANY
			+ "generate_bipartite_complete", exceptionHandler = StatusReturnExceptionHandler.class)
	public static <V, E> int completeBipartiteGraphGenerator(IsolateThread thread, ObjectHandle graph, int a, int b) {
		Graph<V, E> g = globalHandles.get(graph);
		GraphGenerator<V, E, ?> gen = new CompleteBipartiteGraphGenerator<>(a, b);
		gen.generateGraph(g);
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.ANY_ANY
			+ "generate_empty", exceptionHandler = StatusReturnExceptionHandler.class)
	public static <V, E> int emptyGraphGenerator(IsolateThread thread, ObjectHandle graph, int n) {
		Graph<V, E> g = globalHandles.get(graph);
		GraphGenerator<V, E, ?> gen = new EmptyGraphGenerator<>(n);
		gen.generateGraph(g);
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.ANY_ANY
			+ "generate_gnm_random", exceptionHandler = StatusReturnExceptionHandler.class)
	public static <V, E> int customRandomGnmGenerator(IsolateThread thread, ObjectHandle graph, int n, int m,
			boolean loops, boolean multipleEdges, long seed) {
		Graph<V, E> g = globalHandles.get(graph);
		GraphGenerator<V, E, ?> gen = new GnmRandomGraphGenerator<>(n, m, seed, loops, multipleEdges);
		gen.generateGraph(g);
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.ANY_ANY
			+ "generate_gnp_random", exceptionHandler = StatusReturnExceptionHandler.class)
	public static <V, E> int customRandomGnpGenerator(IsolateThread thread, ObjectHandle graph, int n, double p,
			boolean createLoops, long seed) {
		Graph<V, E> g = globalHandles.get(graph);
		GraphGenerator<V, E, ?> gen = new GnpRandomGraphGenerator<>(n, p, seed, createLoops);
		gen.generateGraph(g);
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.ANY_ANY
			+ "generate_ring", exceptionHandler = StatusReturnExceptionHandler.class)
	public static <V, E> int ringGenerator(IsolateThread thread, ObjectHandle graph, int n) {
		Graph<V, E> g = globalHandles.get(graph);
		GraphGenerator<V, E, ?> gen = new RingGraphGenerator<>(n);
		gen.generateGraph(g);
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.ANY_ANY
			+ "generate_scalefree", exceptionHandler = StatusReturnExceptionHandler.class)
	public static <V, E> int scaleFreeGenerator(IsolateThread thread, ObjectHandle graph, int n, long seed) {
		Graph<V, E> g = globalHandles.get(graph);
		GraphGenerator<V, E, ?> gen = new ScaleFreeGraphGenerator<>(n, seed);
		gen.generateGraph(g);
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.ANY_ANY
			+ "generate_watts_strogatz", exceptionHandler = StatusReturnExceptionHandler.class)
	public static <V, E> int wattsStrogatzGenerator(IsolateThread thread, ObjectHandle graph, int n, int k, double p,
			boolean addInsteadOfRewire, long seed) {
		Graph<V, E> g = globalHandles.get(graph);
		GraphGenerator<V, E, ?> gen = new WattsStrogatzGraphGenerator<>(n, k, p, addInsteadOfRewire, new Random(seed));
		gen.generateGraph(g);
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.ANY_ANY
			+ "generate_kleinberg_smallworld", exceptionHandler = StatusReturnExceptionHandler.class)
	public static <V, E> int kleinbergSmallWorldGenerator(IsolateThread thread, ObjectHandle graph, int n, int p, int q,
			int r, long seed) {
		Graph<V, E> g = globalHandles.get(graph);
		GraphGenerator<V, E, ?> gen = new KleinbergSmallWorldGraphGenerator<>(n, p, q, r, seed);
		gen.generateGraph(g);
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.ANY_ANY
			+ "generate_complement", exceptionHandler = StatusReturnExceptionHandler.class)
	public static <V, E> int generateComplement(IsolateThread thread, ObjectHandle graphTarget,
			ObjectHandle graphSource, boolean generateSelfLoops) {
		Graph<V, E> gTarget = globalHandles.get(graphTarget);
		Graph<V, E> gSource = globalHandles.get(graphSource);
		GraphGenerator<V, E, ?> gen = new ComplementGraphGenerator<>(gSource, generateSelfLoops);
		gen.generateGraph(gTarget);
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.ANY_ANY
			+ "generate_generalized_petersen", exceptionHandler = StatusReturnExceptionHandler.class)
	public static <V, E> int generateGeneralizedPetersen(IsolateThread thread, ObjectHandle graph, int n, int k) {
		Graph<V, E> g = globalHandles.get(graph);
		GraphGenerator<V, E, ?> gen = new GeneralizedPetersenGraphGenerator<>(n, k);
		gen.generateGraph(g);
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.ANY_ANY
			+ "generate_grid", exceptionHandler = StatusReturnExceptionHandler.class)
	public static <V, E> int generateGrid(IsolateThread thread, ObjectHandle graph, int rows, int cols) {
		Graph<V, E> g = globalHandles.get(graph);
		GraphGenerator<V, E, ?> gen = new GridGraphGenerator<>(rows, cols);
		gen.generateGraph(g);
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.ANY_ANY
			+ "generate_hypercube", exceptionHandler = StatusReturnExceptionHandler.class)
	public static <V, E> int generateHyperCube(IsolateThread thread, ObjectHandle graph, int dim) {
		Graph<V, E> g = globalHandles.get(graph);
		GraphGenerator<V, E, ?> gen = new HyperCubeGraphGenerator<>(dim);
		gen.generateGraph(g);
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.ANY_ANY
			+ "generate_linear", exceptionHandler = StatusReturnExceptionHandler.class)
	public static <V, E> int generateLinear(IsolateThread thread, ObjectHandle graph, int n) {
		Graph<V, E> g = globalHandles.get(graph);
		GraphGenerator<V, E, ?> gen = new LinearGraphGenerator<>(n);
		gen.generateGraph(g);
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.ANY_ANY
			+ "generate_random_regular", exceptionHandler = StatusReturnExceptionHandler.class)
	public static <V, E> int generateRandomRegularGraph(IsolateThread thread, ObjectHandle graph, int n, int d,
			long seed) {
		Graph<V, E> g = globalHandles.get(graph);
		GraphGenerator<V, E, ?> gen = new RandomRegularGraphGenerator<>(n, d, seed);
		gen.generateGraph(g);
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.ANY_ANY
			+ "generate_star", exceptionHandler = StatusReturnExceptionHandler.class)
	public static <V, E> int generateStarGraph(IsolateThread thread, ObjectHandle graph, int n) {
		Graph<V, E> g = globalHandles.get(graph);
		GraphGenerator<V, E, ?> gen = new StarGraphGenerator<>(n);
		gen.generateGraph(g);
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.ANY_ANY
			+ "generate_wheel", exceptionHandler = StatusReturnExceptionHandler.class)
	public static <V, E> int generateWheel(IsolateThread thread, ObjectHandle graph, int size, boolean inwardSpokes) {
		Graph<V, E> g = globalHandles.get(graph);
		GraphGenerator<V, E, ?> gen = new WheelGraphGenerator<>(size, inwardSpokes);
		gen.generateGraph(g);
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.ANY_ANY
			+ "generate_windmill", exceptionHandler = StatusReturnExceptionHandler.class)
	public static <V, E> int generateWindmill(IsolateThread thread, ObjectHandle graph, int m, int n, boolean dutch) {
		Graph<V, E> g = globalHandles.get(graph);
		GraphGenerator<V, E, ?> gen = new WindmillGraphsGenerator<>(dutch ? Mode.DUTCHWINDMILL : Mode.WINDMILL, m, n);
		gen.generateGraph(g);
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.ANY_ANY
			+ "generate_linearized_chord_diagram", exceptionHandler = StatusReturnExceptionHandler.class)
	public static <V, E> int generateLinearizedChordDiagram(IsolateThread thread, ObjectHandle graph, int n, int m,
			long seed) {
		Graph<V, E> g = globalHandles.get(graph);
		GraphGenerator<V, E, ?> gen = new LinearizedChordDiagramGraphGenerator<>(n, m, seed);
		gen.generateGraph(g);
		return Status.STATUS_SUCCESS.getCValue();
	}

}
