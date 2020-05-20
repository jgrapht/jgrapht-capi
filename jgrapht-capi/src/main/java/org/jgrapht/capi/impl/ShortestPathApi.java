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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import org.graalvm.nativeimage.IsolateThread;
import org.graalvm.nativeimage.ObjectHandle;
import org.graalvm.nativeimage.ObjectHandles;
import org.graalvm.nativeimage.c.function.CEntryPoint;
import org.graalvm.nativeimage.c.type.CDoublePointer;
import org.graalvm.nativeimage.c.type.WordPointer;
import org.graalvm.word.WordFactory;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.interfaces.MultiObjectiveShortestPathAlgorithm;
import org.jgrapht.alg.interfaces.MultiObjectiveShortestPathAlgorithm.MultiObjectiveSingleSourcePaths;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm.SingleSourcePaths;
import org.jgrapht.alg.shortestpath.ALTAdmissibleHeuristic;
import org.jgrapht.alg.shortestpath.AStarShortestPath;
import org.jgrapht.alg.shortestpath.BFSShortestPath;
import org.jgrapht.alg.shortestpath.BellmanFordShortestPath;
import org.jgrapht.alg.shortestpath.BidirectionalAStarShortestPath;
import org.jgrapht.alg.shortestpath.BidirectionalDijkstraShortestPath;
import org.jgrapht.alg.shortestpath.DeltaSteppingShortestPath;
import org.jgrapht.alg.shortestpath.EppsteinKShortestPath;
import org.jgrapht.alg.shortestpath.FloydWarshallShortestPaths;
import org.jgrapht.alg.shortestpath.IntVertexDijkstraShortestPath;
import org.jgrapht.alg.shortestpath.JohnsonShortestPaths;
import org.jgrapht.alg.shortestpath.MartinShortestPath;
import org.jgrapht.alg.shortestpath.YenKShortestPath;
import org.jgrapht.capi.Constants;
import org.jgrapht.capi.JGraphTContext.AStarHeuristicFunctionPointer;
import org.jgrapht.capi.JGraphTContext.IntegerToCDoublePointerFunctionPointer;
import org.jgrapht.capi.JGraphTContext.Status;
import org.jgrapht.capi.error.StatusReturnExceptionHandler;

public class ShortestPathApi {

	private static ObjectHandles globalHandles = ObjectHandles.getGlobal();

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "sp_exec_dijkstra_get_path_between_vertices", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeDijkstraBetween(IsolateThread thread, ObjectHandle graphHandle, int source, int target,
			WordPointer pathRes) {
		Graph<Integer, Integer> g = globalHandles.get(graphHandle);

		ShortestPathAlgorithm<Integer, Integer> alg = new IntVertexDijkstraShortestPath<>(g);
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

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "sp_exec_bidirectional_dijkstra_get_path_between_vertices", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeBiDirectionalDijkstraBetween(IsolateThread thread, ObjectHandle graphHandle, int source,
			int target, WordPointer pathRes) {
		Graph<Integer, Integer> g = globalHandles.get(graphHandle);

		ShortestPathAlgorithm<Integer, Integer> alg = new BidirectionalDijkstraShortestPath<>(g);
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

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "sp_exec_dijkstra_get_singlesource_from_vertex", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeDijkstraFrom(IsolateThread thread, ObjectHandle graphHandle, int source,
			WordPointer pathsRes) {
		Graph<Integer, Integer> g = globalHandles.get(graphHandle);

		ShortestPathAlgorithm<Integer, Integer> alg = new IntVertexDijkstraShortestPath<>(g);
		SingleSourcePaths<Integer, Integer> paths = alg.getPaths(source);
		if (pathsRes.isNonNull()) {
			pathsRes.write(globalHandles.create(paths));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "sp_exec_bellmanford_get_singlesource_from_vertex", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeBellmanFordFrom(IsolateThread thread, ObjectHandle graphHandle, int source,
			WordPointer pathsRes) {
		Graph<Integer, Integer> g = globalHandles.get(graphHandle);

		ShortestPathAlgorithm<Integer, Integer> alg = new BellmanFordShortestPath<>(g);
		SingleSourcePaths<Integer, Integer> paths = alg.getPaths(source);
		if (pathsRes.isNonNull()) {
			pathsRes.write(globalHandles.create(paths));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "sp_exec_bfs_get_singlesource_from_vertex", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeBfsFrom(IsolateThread thread, ObjectHandle graphHandle, int source, WordPointer pathsRes) {
		Graph<Integer, Integer> g = globalHandles.get(graphHandle);

		ShortestPathAlgorithm<Integer, Integer> alg = new BFSShortestPath<>(g);
		SingleSourcePaths<Integer, Integer> paths = alg.getPaths(source);
		if (pathsRes.isNonNull()) {
			pathsRes.write(globalHandles.create(paths));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "sp_exec_johnson_get_allpairs", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeJohnson(IsolateThread thread, ObjectHandle graphHandle, WordPointer res) {
		Graph<Integer, Integer> g = globalHandles.get(graphHandle);
		ShortestPathAlgorithm<Integer, Integer> alg = new JohnsonShortestPaths<>(g);
		if (res.isNonNull()) {
			res.write(globalHandles.create(alg));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "sp_exec_floydwarshall_get_allpairs", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeFloydWarshall(IsolateThread thread, ObjectHandle graphHandle, WordPointer res) {
		Graph<Integer, Integer> g = globalHandles.get(graphHandle);
		ShortestPathAlgorithm<Integer, Integer> alg = new FloydWarshallShortestPaths<>(g);
		if (res.isNonNull()) {
			res.write(globalHandles.create(alg));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "sp_singlesource_get_path_to_vertex", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int singleSourceGetPathToVertex(IsolateThread thread, ObjectHandle pathsHandle, int target,
			WordPointer pathRes) {
		SingleSourcePaths<Integer, Integer> paths = globalHandles.get(pathsHandle);
		GraphPath<Integer, Integer> path = paths.getPath(target);
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
	public static int allPairsGetPathBetweenVerticesFields(IsolateThread thread, ObjectHandle handle, int source,
			int target, WordPointer res) {
		ShortestPathAlgorithm<Integer, Integer> alg = globalHandles.get(handle);
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

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "sp_allpairs_get_singlesource_from_vertex", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int allPairsGetSingleSourceFromVertex(IsolateThread thread, ObjectHandle handle, int source,
			WordPointer res) {
		ShortestPathAlgorithm<Integer, Integer> alg = globalHandles.get(handle);
		SingleSourcePaths<Integer, Integer> paths = alg.getPaths(source);
		if (res.isNonNull()) {
			res.write(globalHandles.create(paths));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "sp_exec_astar_get_path_between_vertices", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeAStarBetween(IsolateThread thread, ObjectHandle graphHandle, int source, int target,
			AStarHeuristicFunctionPointer admissibleHeuristicFunctionPointer, WordPointer pathRes) {
		Graph<Integer, Integer> g = globalHandles.get(graphHandle);

		AStarShortestPath<Integer, Integer> alg = new AStarShortestPath<>(g, (a, b) -> {
			return admissibleHeuristicFunctionPointer.invoke(a, b);
		});

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

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "sp_exec_bidirectional_astar_get_path_between_vertices", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeBidirectionalAStarBetween(IsolateThread thread, ObjectHandle graphHandle, int source,
			int target, AStarHeuristicFunctionPointer admissibleHeuristicFunctionPointer, WordPointer pathRes) {
		Graph<Integer, Integer> g = globalHandles.get(graphHandle);

		BidirectionalAStarShortestPath<Integer, Integer> alg = new BidirectionalAStarShortestPath<>(g, (a, b) -> {
			return admissibleHeuristicFunctionPointer.invoke(a, b);
		});

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

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "sp_exec_astar_alt_heuristic_get_path_between_vertices", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeAStarWithAltHeuristicBetween(IsolateThread thread, ObjectHandle graphHandle, int source,
			int target, ObjectHandle landmarksSet, WordPointer pathRes) {
		Graph<Integer, Integer> g = globalHandles.get(graphHandle);
		Set<Integer> landmarks = globalHandles.get(landmarksSet);

		AStarShortestPath<Integer, Integer> alg = new AStarShortestPath<>(g,
				new ALTAdmissibleHeuristic<>(g, landmarks));

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

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "sp_exec_bidirectional_astar_alt_heuristic_get_path_between_vertices", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeBidirectionalAStarWithAltHeuristicBetween(IsolateThread thread, ObjectHandle graphHandle,
			int source, int target, ObjectHandle landmarksSet, WordPointer pathRes) {
		Graph<Integer, Integer> g = globalHandles.get(graphHandle);
		Set<Integer> landmarks = globalHandles.get(landmarksSet);

		BidirectionalAStarShortestPath<Integer, Integer> alg = new BidirectionalAStarShortestPath<>(g,
				new ALTAdmissibleHeuristic<>(g, landmarks));

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

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "sp_exec_yen_get_k_loopless_paths_between_vertices", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeYenBetween(IsolateThread thread, ObjectHandle graphHandle, int source, int target, int k,
			WordPointer pathIteratorRes) {
		Graph<Integer, Integer> g = globalHandles.get(graphHandle);

		YenKShortestPath<Integer, Integer> alg = new YenKShortestPath<>(g);
		List<GraphPath<Integer, Integer>> paths = alg.getPaths(source, target, k);

		if (pathIteratorRes.isNonNull()) {
			pathIteratorRes.write(globalHandles.create(paths.iterator()));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "sp_exec_eppstein_get_k_paths_between_vertices", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeEppsteinBetween(IsolateThread thread, ObjectHandle graphHandle, int source, int target,
			int k, WordPointer pathIteratorRes) {
		Graph<Integer, Integer> g = globalHandles.get(graphHandle);

		EppsteinKShortestPath<Integer, Integer> alg = new EppsteinKShortestPath<>(g);
		List<GraphPath<Integer, Integer>> paths = alg.getPaths(source, target, k);

		if (pathIteratorRes.isNonNull()) {
			pathIteratorRes.write(globalHandles.create(paths.iterator()));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "sp_exec_delta_stepping_get_path_between_vertices", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeDeltaSteppingBetween(IsolateThread thread, ObjectHandle graphHandle, int source,
			int target, double delta, int parallelism, WordPointer pathRes) {
		Graph<Integer, Integer> g = globalHandles.get(graphHandle);

		ShortestPathAlgorithm<Integer, Integer> alg = new DeltaSteppingShortestPath<>(g, delta, parallelism);
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

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "sp_exec_delta_stepping_get_singlesource_from_vertex", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeDeltaSteppingFrom(IsolateThread thread, ObjectHandle graphHandle, int source, double delta,
			int parallelism, WordPointer pathsRes) {
		Graph<Integer, Integer> g = globalHandles.get(graphHandle);

		ShortestPathAlgorithm<Integer, Integer> alg = new DeltaSteppingShortestPath<>(g, delta, parallelism);
		SingleSourcePaths<Integer, Integer> paths = alg.getPaths(source);
		if (pathsRes.isNonNull()) {
			pathsRes.write(globalHandles.create(paths));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "multisp_exec_martin_get_multiobjectivesinglesource_from_vertex", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeMartin(IsolateThread thread, ObjectHandle graphHandle, int source,
			IntegerToCDoublePointerFunctionPointer edgeWeightFunctionPointer, int dim, WordPointer pathsRes) {
		Graph<Integer, Integer> g = globalHandles.get(graphHandle);

		Function<Integer, double[]> edgeWeightFunction = cacheEdgeWeightFunction(g, edgeWeightFunctionPointer, dim);

		MultiObjectiveShortestPathAlgorithm<Integer, Integer> alg = new MartinShortestPath<>(g, edgeWeightFunction);
		MultiObjectiveSingleSourcePaths<Integer, Integer> paths = alg.getPaths(source);

		if (pathsRes.isNonNull()) {
			pathsRes.write(globalHandles.create(paths));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "multisp_exec_martin_get_paths_between_vertices", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeMartin(IsolateThread thread, ObjectHandle graphHandle, int source, int target,
			IntegerToCDoublePointerFunctionPointer edgeWeightFunctionPointer, int dim, WordPointer pathsRes) {
		Graph<Integer, Integer> g = globalHandles.get(graphHandle);

		if (dim <= 0) {
			throw new IllegalArgumentException("Weight function dimension must be positive");
		}
		if (edgeWeightFunctionPointer.isNull()) {
			throw new IllegalArgumentException("Weight function cannot be null");
		}

		Function<Integer, double[]> edgeWeightFunction = cacheEdgeWeightFunction(g, edgeWeightFunctionPointer, dim);

		MultiObjectiveShortestPathAlgorithm<Integer, Integer> alg = new MartinShortestPath<>(g, edgeWeightFunction);
		List<GraphPath<Integer, Integer>> paths = alg.getPaths(source, target);

		if (pathsRes.isNonNull()) {
			pathsRes.write(globalHandles.create(paths.iterator()));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "multisp_multiobjectivesinglesource_get_paths_to_vertex", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int multiObjectiveSingleSourceGetPathToVertex(IsolateThread thread, ObjectHandle sourceHandle,
			int target, WordPointer pathsRes) {
		MultiObjectiveSingleSourcePaths<Integer, Integer> source = globalHandles.get(sourceHandle);
		List<GraphPath<Integer, Integer>> paths = source.getPaths(target);
		if (pathsRes.isNonNull()) {
			pathsRes.write(globalHandles.create(paths.iterator()));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	private static Function<Integer, double[]> cacheEdgeWeightFunction(Graph<Integer, Integer> g,
			IntegerToCDoublePointerFunctionPointer edgeWeightFunctionPointer, int dim) {
		// cache all function values
		Map<Integer, double[]> edgeWeightMap = new HashMap<>();
		for (Integer e : g.edgeSet()) {
			CDoublePointer base = edgeWeightFunctionPointer.invoke(e);
			double[] v = new double[dim];
			for (int i = 0; i < dim; i++) {
				v[i] = base.read(i);
			}
			edgeWeightMap.put(e, v);
		}
		return e -> edgeWeightMap.get(e);
	}

}
