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

import org.graalvm.nativeimage.IsolateThread;
import org.graalvm.nativeimage.ObjectHandle;
import org.graalvm.nativeimage.ObjectHandles;
import org.graalvm.nativeimage.c.function.CEntryPoint;
import org.jgrapht.Graph;
import org.jgrapht.capi.Constants;
import org.jgrapht.capi.Status;
import org.jgrapht.capi.error.StatusReturnExceptionHandler;
import org.jgrapht.generate.BarabasiAlbertForestGenerator;
import org.jgrapht.generate.BarabasiAlbertGraphGenerator;
import org.jgrapht.generate.CompleteBipartiteGraphGenerator;
import org.jgrapht.generate.CompleteGraphGenerator;
import org.jgrapht.generate.EmptyGraphGenerator;
import org.jgrapht.generate.GraphGenerator;

public class GenerateApi {

	private static ObjectHandles globalHandles = ObjectHandles.getGlobal();

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "generate_barabasi_albert", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int barabasiAlbertGraphGenerator(IsolateThread thread, ObjectHandle graph, int m0, int m, int n,
			long seed) {
		Graph<Long, Long> g = globalHandles.get(graph);
		GraphGenerator<Long, Long, Long> gen = new BarabasiAlbertGraphGenerator<>(m0, m, n, seed);
		gen.generateGraph(g);
		return Status.SUCCESS.toCEnum();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "generate_barabasi_albert_forest", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int barabasiAlbertForestGenerator(IsolateThread thread, ObjectHandle graph, int t, int n, long seed) {
		Graph<Long, Long> g = globalHandles.get(graph);
		GraphGenerator<Long, Long, Long> gen = new BarabasiAlbertForestGenerator<>(t, n, seed);
		gen.generateGraph(g);
		return Status.SUCCESS.toCEnum();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "generate_complete", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int completeGraphGenerator(IsolateThread thread, ObjectHandle graph, int n) {
		Graph<Long, Long> g = globalHandles.get(graph);
		GraphGenerator<Long, Long, Long> gen = new CompleteGraphGenerator<>(n);
		gen.generateGraph(g);
		return Status.SUCCESS.toCEnum();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "generate_bipartite_complete", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int completeBipartiteGraphGenerator(IsolateThread thread, ObjectHandle graph, int a, int b) {
		Graph<Long, Long> g = globalHandles.get(graph);
		GraphGenerator<Long, Long, Long> gen = new CompleteBipartiteGraphGenerator<>(a, b);
		gen.generateGraph(g);
		return Status.SUCCESS.toCEnum();
	}
	
	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "generate_empty", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int emptyGraphGenerator(IsolateThread thread, ObjectHandle graph, int n) {
		Graph<Long, Long> g = globalHandles.get(graph);
		GraphGenerator<Long, Long, Long> gen = new EmptyGraphGenerator<>(n);
		gen.generateGraph(g);
		return Status.SUCCESS.toCEnum();
	}

}
