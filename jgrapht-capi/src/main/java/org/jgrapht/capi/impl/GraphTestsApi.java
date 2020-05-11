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
import org.graalvm.nativeimage.c.type.CIntPointer;
import org.jgrapht.Graph;
import org.jgrapht.GraphTests;
import org.jgrapht.capi.Constants;
import org.jgrapht.capi.JGraphTContext.Status;
import org.jgrapht.capi.error.StatusReturnExceptionHandler;

public class GraphTestsApi {

	private static ObjectHandles globalHandles = ObjectHandles.getGlobal();

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "graph_test_is_empty", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int isEmpty(IsolateThread thread, ObjectHandle graphHandle, CIntPointer res) {
		Graph<Integer, Integer> g = globalHandles.get(graphHandle);
		if (res.isNonNull()) {
			res.write(GraphTests.isEmpty(g) ? 1 : 0);
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "graph_test_is_simple", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int isSimple(IsolateThread thread, ObjectHandle graphHandle, CIntPointer res) {
		Graph<Integer, Integer> g = globalHandles.get(graphHandle);
		if (res.isNonNull()) {
			res.write(GraphTests.isSimple(g) ? 1 : 0);
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "graph_test_has_selfloops", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int hasSelfLoops(IsolateThread thread, ObjectHandle graphHandle, CIntPointer res) {
		Graph<Integer, Integer> g = globalHandles.get(graphHandle);
		if (res.isNonNull()) {
			res.write(GraphTests.hasSelfLoops(g) ? 1 : 0);
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "graph_test_has_multipleedges", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int hasMultipleEdges(IsolateThread thread, ObjectHandle graphHandle, CIntPointer res) {
		Graph<Integer, Integer> g = globalHandles.get(graphHandle);
		if (res.isNonNull()) {
			res.write(GraphTests.hasMultipleEdges(g) ? 1 : 0);
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "graph_test_is_complete", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int isComplete(IsolateThread thread, ObjectHandle graphHandle, CIntPointer res) {
		Graph<Integer, Integer> g = globalHandles.get(graphHandle);
		if (res.isNonNull()) {
			res.write(GraphTests.isComplete(g) ? 1 : 0);
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "graph_test_is_weakly_connected", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int isWeaklyConnected(IsolateThread thread, ObjectHandle graphHandle, CIntPointer res) {
		Graph<Integer, Integer> g = globalHandles.get(graphHandle);
		if (res.isNonNull()) {
			res.write(GraphTests.isWeaklyConnected(g) ? 1 : 0);
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "graph_test_is_strongly_connected", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int isStronglyConnected(IsolateThread thread, ObjectHandle graphHandle, CIntPointer res) {
		Graph<Integer, Integer> g = globalHandles.get(graphHandle);
		if (res.isNonNull()) {
			res.write(GraphTests.isStronglyConnected(g) ? 1 : 0);
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "graph_test_is_tree", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int isTree(IsolateThread thread, ObjectHandle graphHandle, CIntPointer res) {
		Graph<Integer, Integer> g = globalHandles.get(graphHandle);
		if (res.isNonNull()) {
			res.write(GraphTests.isTree(g) ? 1 : 0);
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "graph_test_is_forest", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int isForest(IsolateThread thread, ObjectHandle graphHandle, CIntPointer res) {
		Graph<Integer, Integer> g = globalHandles.get(graphHandle);
		if (res.isNonNull()) {
			res.write(GraphTests.isForest(g) ? 1 : 0);
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "graph_test_is_overfull", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int isOverfull(IsolateThread thread, ObjectHandle graphHandle, CIntPointer res) {
		Graph<Integer, Integer> g = globalHandles.get(graphHandle);
		if (res.isNonNull()) {
			res.write(GraphTests.isOverfull(g) ? 1 : 0);
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "graph_test_is_split", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int isSplit(IsolateThread thread, ObjectHandle graphHandle, CIntPointer res) {
		Graph<Integer, Integer> g = globalHandles.get(graphHandle);
		if (res.isNonNull()) {
			res.write(GraphTests.isSplit(g) ? 1 : 0);
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "graph_test_is_bipartite", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int isBipartite(IsolateThread thread, ObjectHandle graphHandle, CIntPointer res) {
		Graph<Integer, Integer> g = globalHandles.get(graphHandle);
		if (res.isNonNull()) {
			res.write(GraphTests.isBipartite(g) ? 1 : 0);
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "graph_test_is_cubic", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int isCubic(IsolateThread thread, ObjectHandle graphHandle, CIntPointer res) {
		Graph<Integer, Integer> g = globalHandles.get(graphHandle);
		if (res.isNonNull()) {
			res.write(GraphTests.isCubic(g) ? 1 : 0);
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "graph_test_is_eulerian", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int isEulerian(IsolateThread thread, ObjectHandle graphHandle, CIntPointer res) {
		Graph<Integer, Integer> g = globalHandles.get(graphHandle);
		if (res.isNonNull()) {
			res.write(GraphTests.isEulerian(g) ? 1 : 0);
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "graph_test_is_chordal", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int isChordal(IsolateThread thread, ObjectHandle graphHandle, CIntPointer res) {
		Graph<Integer, Integer> g = globalHandles.get(graphHandle);
		if (res.isNonNull()) {
			res.write(GraphTests.isChordal(g) ? 1 : 0);
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "graph_test_is_weakly_chordal", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int isWeaklyChordal(IsolateThread thread, ObjectHandle graphHandle, CIntPointer res) {
		Graph<Integer, Integer> g = globalHandles.get(graphHandle);
		if (res.isNonNull()) {
			res.write(GraphTests.isWeaklyChordal(g) ? 1 : 0);
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "graph_test_has_ore", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int hasOreProperty(IsolateThread thread, ObjectHandle graphHandle, CIntPointer res) {
		Graph<Integer, Integer> g = globalHandles.get(graphHandle);
		if (res.isNonNull()) {
			res.write(GraphTests.hasOreProperty(g) ? 1 : 0);
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "graph_test_is_trianglefree", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int isTriangleFree(IsolateThread thread, ObjectHandle graphHandle, CIntPointer res) {
		Graph<Integer, Integer> g = globalHandles.get(graphHandle);
		if (res.isNonNull()) {
			res.write(GraphTests.isTriangleFree(g) ? 1 : 0);
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "graph_test_is_perfect", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int isPerfect(IsolateThread thread, ObjectHandle graphHandle, CIntPointer res) {
		Graph<Integer, Integer> g = globalHandles.get(graphHandle);
		if (res.isNonNull()) {
			res.write(GraphTests.isPerfect(g) ? 1 : 0);
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "graph_test_is_planar", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int isPlanar(IsolateThread thread, ObjectHandle graphHandle, CIntPointer res) {
		Graph<Integer, Integer> g = globalHandles.get(graphHandle);
		if (res.isNonNull()) {
			res.write(GraphTests.isPlanar(g) ? 1 : 0);
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "graph_test_is_kuratowski_subdivision", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int isKuratowskiSubdivision(IsolateThread thread, ObjectHandle graphHandle, CIntPointer res) {
		Graph<Integer, Integer> g = globalHandles.get(graphHandle);
		if (res.isNonNull()) {
			res.write(GraphTests.isKuratowskiSubdivision(g) ? 1 : 0);
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "graph_test_is_k33_subdivision", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int isK33Subdivision(IsolateThread thread, ObjectHandle graphHandle, CIntPointer res) {
		Graph<Integer, Integer> g = globalHandles.get(graphHandle);
		if (res.isNonNull()) {
			res.write(GraphTests.isK33Subdivision(g) ? 1 : 0);
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "graph_test_is_k5_subdivision", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int isK5Subdivision(IsolateThread thread, ObjectHandle graphHandle, CIntPointer res) {
		Graph<Integer, Integer> g = globalHandles.get(graphHandle);
		if (res.isNonNull()) {
			res.write(GraphTests.isK5Subdivision(g) ? 1 : 0);
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

}
