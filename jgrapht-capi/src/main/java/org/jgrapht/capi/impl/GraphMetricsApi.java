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
import org.graalvm.nativeimage.c.type.CDoublePointer;
import org.graalvm.nativeimage.c.type.CLongPointer;
import org.jgrapht.Graph;
import org.jgrapht.GraphMetrics;
import org.jgrapht.capi.Constants;
import org.jgrapht.capi.Status;
import org.jgrapht.capi.error.StatusReturnExceptionHandler;

public class GraphMetricsApi {

	private static ObjectHandles globalHandles = ObjectHandles.getGlobal();

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "graph_metrics_diameter", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int diameter(IsolateThread thread, ObjectHandle graphHandle, CDoublePointer res) {
		Graph<Long, Long> g = globalHandles.get(graphHandle);
		double result = GraphMetrics.getDiameter(g);
		if (res.isNonNull()) {
			res.write(result);
		}
		return Status.SUCCESS.toCEnum();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "graph_metrics_radius", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int radius(IsolateThread thread, ObjectHandle graphHandle, CDoublePointer res) {
		Graph<Long, Long> g = globalHandles.get(graphHandle);
		double result = GraphMetrics.getRadius(g);
		if (res.isNonNull()) {
			res.write(result);
		}
		return Status.SUCCESS.toCEnum();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "graph_metrics_girth", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int girth(IsolateThread thread, ObjectHandle graphHandle, CLongPointer res) {
		Graph<Long, Long> g = globalHandles.get(graphHandle);
		long result = GraphMetrics.getGirth(g);
		if (res.isNonNull()) {
			res.write(result);
		}
		return Status.SUCCESS.toCEnum();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "graph_metrics_triangles", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int triangles(IsolateThread thread, ObjectHandle graphHandle, CLongPointer res) {
		Graph<Long, Long> g = globalHandles.get(graphHandle);
		long result = GraphMetrics.getNumberOfTriangles(g);
		if (res.isNonNull()) {
			res.write(result);
		}
		return Status.SUCCESS.toCEnum();
	}

}