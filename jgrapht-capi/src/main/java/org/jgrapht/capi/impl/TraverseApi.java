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

import java.util.Iterator;
import java.util.Random;

import org.graalvm.nativeimage.IsolateThread;
import org.graalvm.nativeimage.ObjectHandle;
import org.graalvm.nativeimage.ObjectHandles;
import org.graalvm.nativeimage.c.function.CEntryPoint;
import org.graalvm.nativeimage.c.type.WordPointer;
import org.jgrapht.Graph;
import org.jgrapht.capi.Constants;
import org.jgrapht.capi.JGraphTContext.Status;
import org.jgrapht.capi.error.StatusReturnExceptionHandler;
import org.jgrapht.traverse.BreadthFirstIterator;
import org.jgrapht.traverse.ClosestFirstIterator;
import org.jgrapht.traverse.DegeneracyOrderingIterator;
import org.jgrapht.traverse.DepthFirstIterator;
import org.jgrapht.traverse.LexBreadthFirstIterator;
import org.jgrapht.traverse.MaximumCardinalityIterator;
import org.jgrapht.traverse.RandomWalkIterator;
import org.jgrapht.traverse.TopologicalOrderIterator;

public class TraverseApi {

	private static ObjectHandles globalHandles = ObjectHandles.getGlobal();

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "traverse_create_bfs_from_all_vertices_vit", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int bfsAll(IsolateThread thread, ObjectHandle graphHandle, WordPointer res) {
		Graph<Integer, Integer> g = globalHandles.get(graphHandle);
		Iterator<Integer> it = new BreadthFirstIterator<>(g);
		if (res.isNonNull()) {
			res.write(globalHandles.create(it));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "traverse_create_bfs_from_vertex_vit", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int bfsFromVertex(IsolateThread thread, ObjectHandle graphHandle, int vertex, WordPointer res) {
		Graph<Integer, Integer> g = globalHandles.get(graphHandle);
		Iterator<Integer> it = new BreadthFirstIterator<>(g, vertex);
		if (res.isNonNull()) {
			res.write(globalHandles.create(it));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "traverse_create_lex_bfs_vit", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int lexBFSIterator(IsolateThread thread, ObjectHandle graphHandle, WordPointer res) {
		Graph<Integer, Integer> g = globalHandles.get(graphHandle);
		Iterator<Integer> it = new LexBreadthFirstIterator<>(g);
		if (res.isNonNull()) {
			res.write(globalHandles.create(it));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "traverse_create_dfs_from_all_vertices_vit", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int dfsAll(IsolateThread thread, ObjectHandle graphHandle, WordPointer res) {
		Graph<Integer, Integer> g = globalHandles.get(graphHandle);
		Iterator<Integer> it = new DepthFirstIterator<>(g);
		if (res.isNonNull()) {
			res.write(globalHandles.create(it));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "traverse_create_dfs_from_vertex_vit", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int dfsFromVertex(IsolateThread thread, ObjectHandle graphHandle, int vertex, WordPointer res) {
		Graph<Integer, Integer> g = globalHandles.get(graphHandle);
		Iterator<Integer> it = new DepthFirstIterator<>(g, vertex);
		if (res.isNonNull()) {
			res.write(globalHandles.create(it));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "traverse_create_topological_order_vit", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int topo(IsolateThread thread, ObjectHandle graphHandle, WordPointer res) {
		Graph<Integer, Integer> g = globalHandles.get(graphHandle);
		Iterator<Integer> it = new TopologicalOrderIterator<>(g);
		if (res.isNonNull()) {
			res.write(globalHandles.create(it));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "traverse_create_random_walk_from_vertex_vit", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int randomWalkFromVertex(IsolateThread thread, ObjectHandle graphHandle, int vertex,
			WordPointer res) {
		Graph<Integer, Integer> g = globalHandles.get(graphHandle);
		Iterator<Integer> it = new RandomWalkIterator<>(g, vertex, false);
		if (res.isNonNull()) {
			res.write(globalHandles.create(it));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "traverse_create_custom_random_walk_from_vertex_vit", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int customRandomWalkFromVertex(IsolateThread thread, ObjectHandle graphHandle, int vertex,
			boolean weighted, long maxSteps, long seed, WordPointer res) {
		Graph<Integer, Integer> g = globalHandles.get(graphHandle);
		Iterator<Integer> it = new RandomWalkIterator<>(g, vertex, weighted, maxSteps, new Random(seed));
		if (res.isNonNull()) {
			res.write(globalHandles.create(it));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "traverse_create_max_cardinality_vit", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int maxCardItVertex(IsolateThread thread, ObjectHandle graphHandle, WordPointer res) {
		Graph<Integer, Integer> g = globalHandles.get(graphHandle);
		Iterator<Integer> it = new MaximumCardinalityIterator<>(g);
		if (res.isNonNull()) {
			res.write(globalHandles.create(it));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "traverse_create_degeneracy_ordering_vit", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int degeneracyOrderingIt(IsolateThread thread, ObjectHandle graphHandle, WordPointer res) {
		Graph<Integer, Integer> g = globalHandles.get(graphHandle);
		Iterator<Integer> it = new DegeneracyOrderingIterator<>(g);
		if (res.isNonNull()) {
			res.write(globalHandles.create(it));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "traverse_create_closest_first_from_vertex_vit", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int closestFirst(IsolateThread thread, ObjectHandle graphHandle, int vertex, WordPointer res) {
		Graph<Integer, Integer> g = globalHandles.get(graphHandle);
		Iterator<Integer> it = new ClosestFirstIterator<>(g, vertex);
		if (res.isNonNull()) {
			res.write(globalHandles.create(it));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "traverse_create_custom_closest_first_from_vertex_vit", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int customClosestFirst(IsolateThread thread, ObjectHandle graphHandle, int vertex, double radius,
			WordPointer res) {
		Graph<Integer, Integer> g = globalHandles.get(graphHandle);
		Iterator<Integer> it = new ClosestFirstIterator<>(g, vertex, radius);
		if (res.isNonNull()) {
			res.write(globalHandles.create(it));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

}
