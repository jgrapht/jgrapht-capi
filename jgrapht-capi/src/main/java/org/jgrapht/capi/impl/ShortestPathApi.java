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
import org.graalvm.nativeimage.c.type.WordPointer;
import org.graalvm.word.WordFactory;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm.SingleSourcePaths;
import org.jgrapht.alg.shortestpath.AStarShortestPath;
import org.jgrapht.alg.shortestpath.BFSShortestPath;
import org.jgrapht.alg.shortestpath.BellmanFordShortestPath;
import org.jgrapht.alg.shortestpath.BidirectionalAStarShortestPath;
import org.jgrapht.alg.shortestpath.BidirectionalDijkstraShortestPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.alg.shortestpath.FloydWarshallShortestPaths;
import org.jgrapht.alg.shortestpath.JohnsonShortestPaths;
import org.jgrapht.capi.Constants;
import org.jgrapht.capi.JGraphTContext.AStarHeuristicFunctionPointer;
import org.jgrapht.capi.JGraphTContext.Status;
import org.jgrapht.capi.error.StatusReturnExceptionHandler;

public class ShortestPathApi {

	private static ObjectHandles globalHandles = ObjectHandles.getGlobal();

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "sp_exec_dijkstra_get_path_between_vertices", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeDijkstraBetween(IsolateThread thread, ObjectHandle graphHandle, long source, long target,
			WordPointer pathRes) {
		Graph<Long, Long> g = globalHandles.get(graphHandle);

		DijkstraShortestPath<Long, Long> alg = new DijkstraShortestPath<>(g);
		GraphPath<Long, Long> path = alg.getPath(source, target);
		if (pathRes.isNonNull()) {
			if (path != null) {
				pathRes.write(globalHandles.create(path));
			} else {
				pathRes.write(WordFactory.nullPointer());
			}
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "sp_exec_bidirectional_dijkstra_get_path_between_vertices", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeBiDirectionalDijkstraBetween(IsolateThread thread, ObjectHandle graphHandle, long source,
			long target, WordPointer pathRes) {
		Graph<Long, Long> g = globalHandles.get(graphHandle);

		ShortestPathAlgorithm<Long, Long> alg = new BidirectionalDijkstraShortestPath<>(g);
		GraphPath<Long, Long> path = alg.getPath(source, target);
		if (pathRes.isNonNull()) {
			if (path != null) {
				pathRes.write(globalHandles.create(path));
			} else {
				pathRes.write(WordFactory.nullPointer());
			}
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "sp_exec_dijkstra_get_singlesource_from_vertex", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeDijkstraFrom(IsolateThread thread, ObjectHandle graphHandle, long source,
			WordPointer pathsRes) {
		Graph<Long, Long> g = globalHandles.get(graphHandle);

		ShortestPathAlgorithm<Long, Long> alg = new DijkstraShortestPath<>(g);
		SingleSourcePaths<Long, Long> paths = alg.getPaths(source);
		if (pathsRes.isNonNull()) {
			pathsRes.write(globalHandles.create(paths));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "sp_exec_bellmanford_get_singlesource_from_vertex", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeBellmanFordFrom(IsolateThread thread, ObjectHandle graphHandle, long source,
			WordPointer pathsRes) {
		Graph<Long, Long> g = globalHandles.get(graphHandle);

		ShortestPathAlgorithm<Long, Long> alg = new BellmanFordShortestPath<>(g);
		SingleSourcePaths<Long, Long> paths = alg.getPaths(source);
		if (pathsRes.isNonNull()) {
			pathsRes.write(globalHandles.create(paths));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "sp_exec_bfs_get_singlesource_from_vertex", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeBfsFrom(IsolateThread thread, ObjectHandle graphHandle, long source,
			WordPointer pathsRes) {
		Graph<Long, Long> g = globalHandles.get(graphHandle);

		ShortestPathAlgorithm<Long, Long> alg = new BFSShortestPath<>(g);
		SingleSourcePaths<Long, Long> paths = alg.getPaths(source);
		if (pathsRes.isNonNull()) {
			pathsRes.write(globalHandles.create(paths));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "sp_exec_johnson_get_allpairs", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeJohnson(IsolateThread thread, ObjectHandle graphHandle, WordPointer res) {
		Graph<Long, Long> g = globalHandles.get(graphHandle);
		ShortestPathAlgorithm<Long, Long> alg = new JohnsonShortestPaths<>(g);
		if (res.isNonNull()) {
			res.write(globalHandles.create(alg));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "sp_exec_floydwarshall_get_allpairs", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeFloydWarshall(IsolateThread thread, ObjectHandle graphHandle, WordPointer res) {
		Graph<Long, Long> g = globalHandles.get(graphHandle);
		ShortestPathAlgorithm<Long, Long> alg = new FloydWarshallShortestPaths<>(g);
		if (res.isNonNull()) {
			res.write(globalHandles.create(alg));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "sp_singlesource_get_path_to_vertex", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int singleSourceGetPathToVertex(IsolateThread thread, ObjectHandle pathsHandle, long target,
			WordPointer pathRes) {
		SingleSourcePaths<Long, Long> paths = globalHandles.get(pathsHandle);
		GraphPath<Long, Long> path = paths.getPath(target);
		if (pathRes.isNonNull()) {
			if (path != null) {
				pathRes.write(globalHandles.create(path));
			} else {
				pathRes.write(WordFactory.nullPointer());
			}
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "sp_allpairs_get_path_between_vertices", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int allPairsGetPathBetweenVerticesFields(IsolateThread thread, ObjectHandle handle, long source,
			long target, WordPointer res) {
		ShortestPathAlgorithm<Long, Long> alg = globalHandles.get(handle);
		GraphPath<Long, Long> path = alg.getPath(source, target);
		if (res.isNonNull()) {
			if (path != null) {
				res.write(globalHandles.create(path));
			} else {
				res.write(WordFactory.nullPointer());
			}
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "sp_allpairs_get_singlesource_from_vertex", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int allPairsGetSingleSourceFromVertex(IsolateThread thread, ObjectHandle handle, long source,
			WordPointer res) {
		ShortestPathAlgorithm<Long, Long> alg = globalHandles.get(handle);
		SingleSourcePaths<Long, Long> paths = alg.getPaths(source);
		if (res.isNonNull()) {
			res.write(globalHandles.create(paths));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "sp_exec_astar_get_path_between_vertices", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeAStarBetween(IsolateThread thread, ObjectHandle graphHandle, long source, long target,
			AStarHeuristicFunctionPointer admissibleHeuristicFunctionPointer, WordPointer pathRes) {
		Graph<Long, Long> g = globalHandles.get(graphHandle);

		AStarShortestPath<Long, Long> alg = new AStarShortestPath<>(g, (a,b)->{
			return admissibleHeuristicFunctionPointer.invoke(a, b);
		});
		
		GraphPath<Long, Long> path = alg.getPath(source, target);
		if (pathRes.isNonNull()) {
			if (path != null) {
				pathRes.write(globalHandles.create(path));
			} else {
				pathRes.write(WordFactory.nullPointer());
			}
		}
		return Status.STATUS_SUCCESS.getCValue();
	}
	
	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "sp_exec_bidirectional_astar_get_path_between_vertices", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeBidirectionalAStarBetween(IsolateThread thread, ObjectHandle graphHandle, long source, long target,
			AStarHeuristicFunctionPointer admissibleHeuristicFunctionPointer, WordPointer pathRes) {
		Graph<Long, Long> g = globalHandles.get(graphHandle);

		BidirectionalAStarShortestPath<Long, Long> alg = new BidirectionalAStarShortestPath<>(g, (a,b)->{
			return admissibleHeuristicFunctionPointer.invoke(a, b);
		});
		
		GraphPath<Long, Long> path = alg.getPath(source, target);
		if (pathRes.isNonNull()) {
			if (path != null) {
				pathRes.write(globalHandles.create(path));
			} else {
				pathRes.write(WordFactory.nullPointer());
			}
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

}
