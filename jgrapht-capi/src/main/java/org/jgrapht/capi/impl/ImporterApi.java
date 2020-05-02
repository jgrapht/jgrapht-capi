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
import java.io.StringReader;

import org.apache.commons.text.StringEscapeUtils;
import org.apache.commons.text.translate.CharSequenceTranslator;
import org.graalvm.nativeimage.IsolateThread;
import org.graalvm.nativeimage.ObjectHandle;
import org.graalvm.nativeimage.ObjectHandles;
import org.graalvm.nativeimage.c.function.CEntryPoint;
import org.graalvm.nativeimage.c.type.CCharPointer;
import org.graalvm.nativeimage.c.type.CTypeConversion;
import org.graalvm.nativeimage.c.type.CTypeConversion.CCharPointerHolder;
import org.jgrapht.Graph;
import org.jgrapht.capi.Constants;
import org.jgrapht.capi.JGraphTContext.NotifyAttributeFunctionPointer;
import org.jgrapht.capi.JGraphTContext.Status;
import org.jgrapht.capi.error.StatusReturnExceptionHandler;
import org.jgrapht.capi.io.CustomDIMACSImporter;
import org.jgrapht.nio.BaseEventDrivenImporter;
import org.jgrapht.nio.gml.GmlImporter;
import org.jgrapht.nio.json.JSONImporter;

public class ImporterApi {

	private static ObjectHandles globalHandles = ObjectHandles.getGlobal();

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "import_file_dimacs", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int importDIMACSFromFile(IsolateThread thread, ObjectHandle graphHandle, CCharPointer filename) {
		Graph<Long, Long> g = globalHandles.get(graphHandle);

		CustomDIMACSImporter<Long, Long> importer = new CustomDIMACSImporter<>();
		importer.setVertexFactory(x -> Long.valueOf(x));
		importer.importGraph(g, new File(CTypeConversion.toJavaString(filename)));

		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "import_string_dimacs", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int importDIMACSFromString(IsolateThread thread, ObjectHandle graphHandle, CCharPointer input) {
		Graph<Long, Long> g = globalHandles.get(graphHandle);

		CustomDIMACSImporter<Long, Long> importer = new CustomDIMACSImporter<>();
		importer.setVertexFactory(x -> Long.valueOf(x));

		String inputAsJava = CTypeConversion.toJavaString(input);
		try (StringReader reader = new StringReader(inputAsJava)) {
			importer.importGraph(g, reader);
		}

		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "import_file_gml", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int importGmlFromFile(IsolateThread thread, ObjectHandle graphHandle, CCharPointer filename,
			NotifyAttributeFunctionPointer vertexAttributeFunction,
			NotifyAttributeFunctionPointer edgeAttributeFunction) {
		Graph<Long, Long> g = globalHandles.get(graphHandle);

		GmlImporter<Long, Long> importer = new GmlImporter<Long, Long>();
		importer.setVertexFactory(x -> Long.valueOf(x));

		setupImportAttributes(importer, vertexAttributeFunction, edgeAttributeFunction, null);

		importer.importGraph(g, new File(CTypeConversion.toJavaString(filename)));

		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "import_string_gml", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int importGmlFromString(IsolateThread thread, ObjectHandle graphHandle, CCharPointer input,
			NotifyAttributeFunctionPointer vertexAttributeFunction,
			NotifyAttributeFunctionPointer edgeAttributeFunction) {
		Graph<Long, Long> g = globalHandles.get(graphHandle);

		GmlImporter<Long, Long> importer = new GmlImporter<Long, Long>();
		importer.setVertexFactory(x -> Long.valueOf(x));

		setupImportAttributes(importer, vertexAttributeFunction, edgeAttributeFunction, null);

		String inputAsJava = CTypeConversion.toJavaString(input);
		try (StringReader reader = new StringReader(inputAsJava)) {
			importer.importGraph(g, reader);
		}

		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "import_file_json", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int importJsonFromFile(IsolateThread thread, ObjectHandle graphHandle, CCharPointer filename,
			NotifyAttributeFunctionPointer vertexAttributeFunction,
			NotifyAttributeFunctionPointer edgeAttributeFunction) {
		Graph<Long, Long> g = globalHandles.get(graphHandle);

		JSONImporter<Long, Long> importer = new JSONImporter<Long, Long>();
		importer.setVertexFactory(x -> Long.valueOf(x));

		setupImportAttributes(importer, vertexAttributeFunction, edgeAttributeFunction,
				StringEscapeUtils.UNESCAPE_JSON);

		importer.importGraph(g, new File(CTypeConversion.toJavaString(filename)));

		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "import_string_json", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int importJsonFromString(IsolateThread thread, ObjectHandle graphHandle, CCharPointer input,
			NotifyAttributeFunctionPointer vertexAttributeFunction,
			NotifyAttributeFunctionPointer edgeAttributeFunction) {
		Graph<Long, Long> g = globalHandles.get(graphHandle);

		JSONImporter<Long, Long> importer = new JSONImporter<Long, Long>();
		importer.setVertexFactory(x -> Long.valueOf(x));

		setupImportAttributes(importer, vertexAttributeFunction, edgeAttributeFunction,
				StringEscapeUtils.UNESCAPE_JSON);

		String inputAsJava = CTypeConversion.toJavaString(input);
		try (StringReader reader = new StringReader(inputAsJava)) {
			importer.importGraph(g, reader);
		}

		return Status.STATUS_SUCCESS.getCValue();
	}

	private static void setupImportAttributes(BaseEventDrivenImporter<Long, Long> importer,
			NotifyAttributeFunctionPointer vertexAttributeFunction,
			NotifyAttributeFunctionPointer edgeAttributeFunction, CharSequenceTranslator unescapeTranslator) {
		if (vertexAttributeFunction.isNonNull()) {
			importer.addVertexAttributeConsumer((p, attr) -> {
				long vertex = p.getFirst();
				String key = p.getSecond();
				CCharPointerHolder keyHolder = CTypeConversion.toCString(key);
				String value = attr.getValue();
				if (unescapeTranslator != null) {
					value = unescapeTranslator.translate(value);
				}
				CCharPointerHolder valueHolder = CTypeConversion.toCString(value);
				vertexAttributeFunction.invoke(vertex, keyHolder.get(), valueHolder.get());
			});
		}

		if (edgeAttributeFunction.isNonNull()) {
			importer.addEdgeAttributeConsumer((p, attr) -> {
				long edge = p.getFirst();
				String key = p.getSecond();
				CCharPointerHolder keyHolder = CTypeConversion.toCString(key);
				String value = attr.getValue();
				if (unescapeTranslator != null) {
					value = unescapeTranslator.translate(value);
				}
				CCharPointerHolder valueHolder = CTypeConversion.toCString(value);
				edgeAttributeFunction.invoke(edge, keyHolder.get(), valueHolder.get());
			});
		}
	}

}
