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
import java.util.Set;

import org.graalvm.nativeimage.IsolateThread;
import org.graalvm.nativeimage.ObjectHandle;
import org.graalvm.nativeimage.ObjectHandles;
import org.graalvm.nativeimage.c.function.CEntryPoint;
import org.graalvm.nativeimage.c.type.CDoublePointer;
import org.graalvm.nativeimage.c.type.CIntPointer;
import org.graalvm.nativeimage.c.type.CLongPointer;
import org.graalvm.nativeimage.c.type.WordPointer;
import org.jgrapht.Graph;
import org.jgrapht.GraphMetrics;
import org.jgrapht.alg.shortestpath.GraphMeasurer;
import org.jgrapht.capi.Constants;
import org.jgrapht.capi.JGraphTContext.Status;
import org.jgrapht.capi.error.StatusReturnExceptionHandler;

public class GraphMetricsApi {

	private static ObjectHandles globalHandles = ObjectHandles.getGlobal();

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "graph_metrics_diameter", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int diameter(IsolateThread thread, ObjectHandle graphHandle, CDoublePointer res) {
		Graph<Integer, Integer> g = globalHandles.get(graphHandle);
		double result = GraphMetrics.getDiameter(g);
		if (res.isNonNull()) {
			res.write(result);
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "graph_metrics_radius", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int radius(IsolateThread thread, ObjectHandle graphHandle, CDoublePointer res) {
		Graph<Integer, Integer> g = globalHandles.get(graphHandle);
		double result = GraphMetrics.getRadius(g);
		if (res.isNonNull()) {
			res.write(result);
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "graph_metrics_girth", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int girth(IsolateThread thread, ObjectHandle graphHandle, CIntPointer res) {
		Graph<Integer, Integer> g = globalHandles.get(graphHandle);
		int result = GraphMetrics.getGirth(g);
		if (res.isNonNull()) {
			res.write(result);
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "graph_metrics_triangles", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int triangles(IsolateThread thread, ObjectHandle graphHandle, CLongPointer res) {
		Graph<Integer, Integer> g = globalHandles.get(graphHandle);
		long result = GraphMetrics.getNumberOfTriangles(g);
		if (res.isNonNull()) {
			res.write(result);
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "graph_metrics_measure_graph", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int vertexEccentricity(IsolateThread thread, ObjectHandle graphHandle, CDoublePointer diameter,
			CDoublePointer radius, WordPointer center, WordPointer periphery, WordPointer pseudoPeriphery,
			WordPointer vertexEccentricityMap) {
		Graph<Integer, Integer> g = globalHandles.get(graphHandle);

		GraphMeasurer<Integer, Integer> alg = new GraphMeasurer<>(g);
		double graphDiameter = alg.getDiameter();
		if (diameter.isNonNull()) {
			diameter.write(graphDiameter);
		}
		double graphRadius = alg.getRadius();
		if (radius.isNonNull()) {
			radius.write(graphRadius);
		}
		Set<Integer> graphCenter = alg.getGraphCenter();
		if (center.isNonNull()) {
			center.write(globalHandles.create(graphCenter));
		}
		Set<Integer> graphPeriphery = alg.getGraphPeriphery();
		if (periphery.isNonNull()) {
			periphery.write(globalHandles.create(graphPeriphery));
		}
		Set<Integer> graphPseudoPeriphery = alg.getGraphPseudoPeriphery();
		if (pseudoPeriphery.isNonNull()) {
			pseudoPeriphery.write(globalHandles.create(graphPseudoPeriphery));
		}
		Map<Integer, Double> graphVertexEccentricityMap = alg.getVertexEccentricityMap();
		if (vertexEccentricityMap.isNonNull()) {
			vertexEccentricityMap.write(globalHandles.create(graphVertexEccentricityMap));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

}
