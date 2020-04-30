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

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.graalvm.nativeimage.IsolateThread;
import org.graalvm.nativeimage.ObjectHandle;
import org.graalvm.nativeimage.ObjectHandles;
import org.graalvm.nativeimage.c.function.CEntryPoint;
import org.graalvm.nativeimage.c.type.CCharPointer;
import org.graalvm.nativeimage.c.type.CTypeConversion;
import org.jgrapht.Graph;
import org.jgrapht.capi.Constants;
import org.jgrapht.capi.JGraphTContext.ExporterDIMACSFormat;
import org.jgrapht.capi.JGraphTContext.LongToStringFunctionPointer;
import org.jgrapht.capi.JGraphTContext.Status;
import org.jgrapht.capi.error.StatusReturnExceptionHandler;
import org.jgrapht.nio.Attribute;
import org.jgrapht.nio.DefaultAttribute;
import org.jgrapht.nio.dimacs.DIMACSExporter;
import org.jgrapht.nio.dimacs.DIMACSFormat;
import org.jgrapht.nio.gml.GmlExporter;

public class ExporterApi {

	private static ObjectHandles globalHandles = ObjectHandles.getGlobal();

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "export_file_dimacs", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int exportDIMACSToFile(IsolateThread thread, ObjectHandle graphHandle, CCharPointer filename,
			ExporterDIMACSFormat format) {
		Graph<Long, Long> g = globalHandles.get(graphHandle);

		DIMACSFormat actualFormat = null;
		switch (format) {
		case DIMACS_FORMAT_COLORING:
			actualFormat = DIMACSFormat.COLORING;
			break;
		case DIMACS_FORMAT_MAX_CLIQUE:
			actualFormat = DIMACSFormat.MAX_CLIQUE;
			break;
		default:
			actualFormat = DIMACSFormat.SHORTEST_PATH;
			break;
		}

		DIMACSExporter<Long, Long> exporter = new DIMACSExporter<>(x -> String.valueOf(x), actualFormat);
		exporter.exportGraph(g, new File(CTypeConversion.toJavaString(filename)));
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "export_file_gml", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int exportGmlFile(IsolateThread thread, ObjectHandle graphHandle, CCharPointer filename,
			boolean exportEdgeWeights, LongToStringFunctionPointer vertexLabelsFunction,
			LongToStringFunctionPointer edgeLabelsFunction) {
		Graph<Long, Long> g = globalHandles.get(graphHandle);

		GmlExporter<Long, Long> exporter = new GmlExporter<>(x -> String.valueOf(x));
		exporter.setParameter(GmlExporter.Parameter.EXPORT_EDGE_WEIGHTS, exportEdgeWeights);

		if (vertexLabelsFunction.isNonNull()) {
			exporter.setParameter(GmlExporter.Parameter.EXPORT_VERTEX_LABELS, true);
			exporter.setVertexAttributeProvider(v -> {
				Map<String, Attribute> h = new HashMap<>();
				// invoke the provided C function
				CCharPointer cResult = vertexLabelsFunction.invoke(thread, v);
				String result = CTypeConversion.toJavaString(cResult);
				h.put("label", DefaultAttribute.createAttribute(result));
				return h;
			});
		}

		if (edgeLabelsFunction.isNonNull()) {
			exporter.setParameter(GmlExporter.Parameter.EXPORT_EDGE_LABELS, true);
			exporter.setEdgeAttributeProvider(e -> {
				Map<String, Attribute> h = new HashMap<>();
				// invoke the provided C function
				CCharPointer cResult = edgeLabelsFunction.invoke(thread, e);
				String result = CTypeConversion.toJavaString(cResult);
				h.put("label", DefaultAttribute.createAttribute(result));
				return h;
			});
		}

		exporter.exportGraph(g, new File(CTypeConversion.toJavaString(filename)));
		return Status.STATUS_SUCCESS.getCValue();
	}

}
