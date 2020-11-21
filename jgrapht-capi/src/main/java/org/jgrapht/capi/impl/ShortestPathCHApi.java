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
import java.util.Set;
import java.util.function.Supplier;

import org.graalvm.nativeimage.IsolateThread;
import org.graalvm.nativeimage.ObjectHandle;
import org.graalvm.nativeimage.ObjectHandles;
import org.graalvm.nativeimage.c.function.CEntryPoint;
import org.graalvm.nativeimage.c.type.WordPointer;
import org.graalvm.word.WordFactory;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.interfaces.ManyToManyShortestPathsAlgorithm.ManyToManyShortestPaths;
import org.jgrapht.alg.shortestpath.CHManyToManyShortestPaths;
import org.jgrapht.alg.shortestpath.ContractionHierarchyBidirectionalDijkstra;
import org.jgrapht.alg.shortestpath.ContractionHierarchyPrecomputation;
import org.jgrapht.alg.shortestpath.ContractionHierarchyPrecomputation.ContractionHierarchy;
import org.jgrapht.capi.Constants;
import org.jgrapht.capi.JGraphTContext.Status;
import org.jgrapht.capi.error.StatusReturnExceptionHandler;
import org.jheaps.tree.PairingHeap;

/**
 * Contraction Hierarchy related API
 */
public class ShortestPathCHApi {

	private static ObjectHandles globalHandles = ObjectHandles.getGlobal();

	/**
	 * Given a {@link ManyToManyShortestPaths} get a path.
	 * 
	 * @param thread the thread
	 * @param handle the {@link ManyToManyShortestPaths} handle
	 * @param source source vertex
	 * @param target target vertex
	 * @param res    a {@link GraphPath} handle
	 * @return status
	 */
	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "sp_manytomany_get_path_between_vertices", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int manyToManyGetPathBetweenVerticesFields(IsolateThread thread, ObjectHandle handle, int source,
			int target, WordPointer res) {
		ManyToManyShortestPaths<Integer, Integer> alg = globalHandles.get(handle);
		GraphPath<Integer, Integer> path = alg.getPath(source, target);
		if (res.isNonNull()) {
			if (path != null) {
				res.write(globalHandles.create(path));
			} else {
				res.write(WordFactory.nullPointer());
			}
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	/**
	 * Compute a {@link ContractionHierarchy}
	 * 
	 * @param thread      thread
	 * @param graphHandle the graph handle
	 * @param parallelism how many thread to use
	 * @param seed        seed for the random number generator
	 * @param res         the {@link ContractionHierarchy} handle
	 * @return status
	 */
	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "sp_exec_contraction_hierarchy", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeCH(IsolateThread thread, ObjectHandle graphHandle, int parallelism, long seed,
			WordPointer res) {
		Graph<Integer, Integer> g = globalHandles.get(graphHandle);

		if (parallelism < 1) {
			throw new IllegalArgumentException("Parallelism must be positive");
		}

		ContractionHierarchyPrecomputation<Integer, Integer> chp = new ContractionHierarchyPrecomputation<>(g,
				parallelism, new SingleRandomToManySupplier(seed));
		ContractionHierarchy<Integer, Integer> ch = chp.computeContractionHierarchy();

		if (res.isNonNull()) {
			res.write(globalHandles.create(ch));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	/**
	 * Given a contraction hierarchy get a {@link ManyToManyShortestPaths}.
	 * 
	 * @param thread        the thread
	 * @param chHandle      the contraction hierarchy handle
	 * @param sourcesHandle handle to set of source vertices
	 * @param targetsHandle handle to set of target vertices
	 * @param res           handle to a {@link ManyToManyShortestPaths}.
	 * @return status
	 */
	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "sp_exec_contraction_hierarchy_get_manytomany", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeCHManyToMany(IsolateThread thread, ObjectHandle chHandle, ObjectHandle sourcesHandle,
			ObjectHandle targetsHandle, WordPointer res) {
		ContractionHierarchy<Integer, Integer> ch = globalHandles.get(chHandle);
		Set<Integer> sources = globalHandles.get(sourcesHandle);
		Set<Integer> targets = globalHandles.get(targetsHandle);
		CHManyToManyShortestPaths<Integer, Integer> mm = new CHManyToManyShortestPaths<Integer, Integer>(ch);
		ManyToManyShortestPaths<Integer, Integer> mmPaths = mm.getManyToManyPaths(sources, targets);
		if (res.isNonNull()) {
			res.write(globalHandles.create(mmPaths));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	/**
	 * Given a contraction hierarchy get a {@link GraphPath} using bidirectional
	 * dijkstra.
	 * 
	 * @param thread   the thread
	 * @param chHandle the contraction hierarchy handle
	 * @param source   the source vertex
	 * @param target   the target vertex
	 * @param res      handle to a {@link GraphPath}.
	 * @return status
	 */
	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "sp_exec_contraction_hierarchy_bidirectional_dijkstra_get_path_between_vertices", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeCHBiDirectionalDijkstraBetween(IsolateThread thread, ObjectHandle chHandle, int source,
			int target, double radius, WordPointer pathRes) {
		ContractionHierarchy<Integer, Integer> ch = globalHandles.get(chHandle);

		ContractionHierarchyBidirectionalDijkstra<Integer, Integer> alg = new ContractionHierarchyBidirectionalDijkstra<>(
				ch, radius, PairingHeap::new);
		GraphPath<Integer, Integer> path = alg.getPath(source, target);
		if (pathRes.isNonNull()) {
			if (path != null) {
				pathRes.write(globalHandles.create(path));
			} else {
				pathRes.write(WordFactory.nullPointer());
			}
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	/**
	 * Helper to return different random instances from a single random seed.
	 */
	private static class SingleRandomToManySupplier implements Supplier<Random> {

		private Random rng;

		public SingleRandomToManySupplier(long seed) {
			this.rng = new Random(seed);
		}

		@Override
		public Random get() {
			long seed = rng.nextLong();
			return new Random(seed);
		}

	}

}
