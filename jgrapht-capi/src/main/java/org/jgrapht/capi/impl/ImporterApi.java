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
import org.graalvm.nativeimage.c.type.CTypeConversion.CCharPointerHolder;
import org.jgrapht.Graph;
import org.jgrapht.capi.Constants;
import org.jgrapht.capi.JGraphTContext.IFunctionPointer;
import org.jgrapht.capi.JGraphTContext.ImportIdFunctionPointer;
import org.jgrapht.capi.JGraphTContext.ImporterExporterCSVFormat;
import org.jgrapht.capi.JGraphTContext.IntegerToIntegerFunctionPointer;
import org.jgrapht.capi.JGraphTContext.IntegerIdNotifyAttributeFunctionPointer;
import org.jgrapht.capi.JGraphTContext.Status;
import org.jgrapht.capi.StringUtils;
import org.jgrapht.capi.error.StatusReturnExceptionHandler;
import org.jgrapht.capi.io.CustomDIMACSImporter;
import org.jgrapht.nio.BaseEventDrivenImporter;
import org.jgrapht.nio.csv.CSVFormat;
import org.jgrapht.nio.csv.CSVImporter;
import org.jgrapht.nio.dot.DOTImporter;
import org.jgrapht.nio.gexf.SimpleGEXFImporter;
import org.jgrapht.nio.gml.GmlImporter;
import org.jgrapht.nio.graph6.Graph6Sparse6Importer;
import org.jgrapht.nio.graphml.GraphMLImporter;
import org.jgrapht.nio.graphml.SimpleGraphMLImporter;
import org.jgrapht.nio.json.JSONImporter;

public class ImporterApi {

	private static ObjectHandles globalHandles = ObjectHandles.getGlobal();

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "import_file_dimacs", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int importDIMACSFromFile(IsolateThread thread, ObjectHandle graphHandle, CCharPointer filename,
			IntegerToIntegerFunctionPointer importIdFunctionPointer, IFunctionPointer notifyVertexFunctionPointer,
			IFunctionPointer notifyEdgeFunctionPointer) {
		Graph<Integer, Integer> g = globalHandles.get(graphHandle);

		CustomDIMACSImporter<Integer, Integer> importer = new CustomDIMACSImporter<>();
		if (importIdFunctionPointer.isNonNull()) {
			importer.setVertexFactory(x -> importIdFunctionPointer.invoke(x));
		}
		setupNotifyVertexEdge(importer, notifyVertexFunctionPointer, notifyEdgeFunctionPointer);
		
		importer.importGraph(g, new File(StringUtils.toJavaStringFromUtf8(filename)));

		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "import_string_dimacs", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int importDIMACSFromString(IsolateThread thread, ObjectHandle graphHandle, CCharPointer input,
			IntegerToIntegerFunctionPointer importIdFunctionPointer, IFunctionPointer notifyVertexFunctionPointer,
			IFunctionPointer notifyEdgeFunctionPointer) {
		Graph<Integer, Integer> g = globalHandles.get(graphHandle);

		CustomDIMACSImporter<Integer, Integer> importer = new CustomDIMACSImporter<>();
		if (importIdFunctionPointer.isNonNull()) {
			importer.setVertexFactory(x -> importIdFunctionPointer.invoke(x));
		}
		setupNotifyVertexEdge(importer, notifyVertexFunctionPointer, notifyEdgeFunctionPointer);
		
		String inputAsJava = StringUtils.toJavaStringFromUtf8(input);
		try (StringReader reader = new StringReader(inputAsJava)) {
			importer.importGraph(g, reader);
		}

		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "import_file_gml", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int importGmlFromFile(IsolateThread thread, ObjectHandle graphHandle, CCharPointer filename,
			IntegerToIntegerFunctionPointer importIdFunctionPointer,
			IntegerIdNotifyAttributeFunctionPointer vertexAttributeFunction,
			IntegerIdNotifyAttributeFunctionPointer edgeAttributeFunction, IFunctionPointer notifyVertexFunctionPointer,
			IFunctionPointer notifyEdgeFunctionPointer) {
		Graph<Integer, Integer> g = globalHandles.get(graphHandle);

		GmlImporter<Integer, Integer> importer = new GmlImporter<Integer, Integer>();
		if (importIdFunctionPointer.isNonNull()) {
			importer.setVertexFactory(x -> importIdFunctionPointer.invoke(x));
		}
		setupNotifyVertexEdge(importer, notifyVertexFunctionPointer, notifyEdgeFunctionPointer);

		setupImportAttributes(importer, vertexAttributeFunction, edgeAttributeFunction, null);

		importer.importGraph(g, new File(StringUtils.toJavaStringFromUtf8(filename)));

		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "import_string_gml", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int importGmlFromString(IsolateThread thread, ObjectHandle graphHandle, CCharPointer input,
			IntegerToIntegerFunctionPointer importIdFunctionPointer,
			IntegerIdNotifyAttributeFunctionPointer vertexAttributeFunction,
			IntegerIdNotifyAttributeFunctionPointer edgeAttributeFunction, IFunctionPointer notifyVertexFunctionPointer,
			IFunctionPointer notifyEdgeFunctionPointer) {
		Graph<Integer, Integer> g = globalHandles.get(graphHandle);

		GmlImporter<Integer, Integer> importer = new GmlImporter<Integer, Integer>();
		if (importIdFunctionPointer.isNonNull()) {
			importer.setVertexFactory(x -> importIdFunctionPointer.invoke(x));
		}

		setupNotifyVertexEdge(importer, notifyVertexFunctionPointer, notifyEdgeFunctionPointer);
		setupImportAttributes(importer, vertexAttributeFunction, edgeAttributeFunction, null);

		String inputAsJava = StringUtils.toJavaStringFromUtf8(input);
		try (StringReader reader = new StringReader(inputAsJava)) {
			importer.importGraph(g, reader);
		}

		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "import_file_json", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int importJsonFromFile(IsolateThread thread, ObjectHandle graphHandle, CCharPointer filename,
			ImportIdFunctionPointer importIdFunctionPointer, IntegerIdNotifyAttributeFunctionPointer vertexAttributeFunction,
			IntegerIdNotifyAttributeFunctionPointer edgeAttributeFunction, IFunctionPointer notifyVertexFunctionPointer,
			IFunctionPointer notifyEdgeFunctionPointer) {
		Graph<Integer, Integer> g = globalHandles.get(graphHandle);

		JSONImporter<Integer, Integer> importer = new JSONImporter<Integer, Integer>();

		if (importIdFunctionPointer.isNonNull()) {
			importer.setVertexFactory(x -> {
				CCharPointerHolder holder = StringUtils.toCStringInUtf8(x);
				return importIdFunctionPointer.invoke(holder.get());
			});
		}

		setupNotifyVertexEdge(importer, notifyVertexFunctionPointer, notifyEdgeFunctionPointer);
		setupImportAttributes(importer, vertexAttributeFunction, edgeAttributeFunction,
				StringEscapeUtils.UNESCAPE_JSON);

		importer.importGraph(g, new File(StringUtils.toJavaStringFromUtf8(filename)));

		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "import_string_json", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int importJsonFromString(IsolateThread thread, ObjectHandle graphHandle, CCharPointer input,
			ImportIdFunctionPointer importIdFunctionPointer, IntegerIdNotifyAttributeFunctionPointer vertexAttributeFunction,
			IntegerIdNotifyAttributeFunctionPointer edgeAttributeFunction, IFunctionPointer notifyVertexFunctionPointer,
			IFunctionPointer notifyEdgeFunctionPointer) {
		Graph<Integer, Integer> g = globalHandles.get(graphHandle);

		JSONImporter<Integer, Integer> importer = new JSONImporter<Integer, Integer>();

		if (importIdFunctionPointer.isNonNull()) {
			importer.setVertexFactory(x -> {
				CCharPointerHolder holder = StringUtils.toCStringInUtf8(x);
				return importIdFunctionPointer.invoke(holder.get());
			});
		}
		setupNotifyVertexEdge(importer, notifyVertexFunctionPointer, notifyEdgeFunctionPointer);
		setupImportAttributes(importer, vertexAttributeFunction, edgeAttributeFunction,
				StringEscapeUtils.UNESCAPE_JSON);

		String inputAsJava = StringUtils.toJavaStringFromUtf8(input);
		try (StringReader reader = new StringReader(inputAsJava)) {
			importer.importGraph(g, reader);
		}

		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "import_file_csv", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int importCSVFromFile(IsolateThread thread, ObjectHandle graphHandle, CCharPointer filename,
			ImportIdFunctionPointer importIdFunctionPointer, IFunctionPointer notifyVertexFunctionPointer,
			IFunctionPointer notifyEdgeFunctionPointer, ImporterExporterCSVFormat format,
			boolean import_edge_weights, boolean matrix_format_nodeid, boolean matrix_format_zero_when_no_edge) {
		Graph<Integer, Integer> g = globalHandles.get(graphHandle);

		CSVFormat actualFormat = null;
		switch (format) {
		case CSV_FORMAT_ADJACENCY_LIST:
			actualFormat = CSVFormat.ADJACENCY_LIST;
			break;
		case CSV_FORMAT_EDGE_LIST:
			actualFormat = CSVFormat.EDGE_LIST;
			break;
		default:
			actualFormat = CSVFormat.MATRIX;
			break;
		}

		CSVImporter<Integer, Integer> importer = new CSVImporter<>(actualFormat);

		if (importIdFunctionPointer.isNonNull()) {
			importer.setVertexFactory(x -> {
				CCharPointerHolder holder = StringUtils.toCStringInUtf8(x);
				return importIdFunctionPointer.invoke(holder.get());
			});
		}
		setupNotifyVertexEdge(importer, notifyVertexFunctionPointer, notifyEdgeFunctionPointer);

		importer.setParameter(CSVFormat.Parameter.EDGE_WEIGHTS, import_edge_weights);
		importer.setParameter(CSVFormat.Parameter.MATRIX_FORMAT_NODEID, matrix_format_nodeid);
		importer.setParameter(CSVFormat.Parameter.MATRIX_FORMAT_ZERO_WHEN_NO_EDGE, matrix_format_zero_when_no_edge);
		importer.importGraph(g, new File(StringUtils.toJavaStringFromUtf8(filename)));

		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "import_string_csv", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int importCSVFromString(IsolateThread thread, ObjectHandle graphHandle, CCharPointer input,
			ImportIdFunctionPointer importIdFunctionPointer, IFunctionPointer notifyVertexFunctionPointer,
			IFunctionPointer notifyEdgeFunctionPointer, ImporterExporterCSVFormat format,
			boolean import_edge_weights, boolean matrix_format_nodeid, boolean matrix_format_zero_when_no_edge) {
		Graph<Integer, Integer> g = globalHandles.get(graphHandle);

		CSVFormat actualFormat = null;
		switch (format) {
		case CSV_FORMAT_ADJACENCY_LIST:
			actualFormat = CSVFormat.ADJACENCY_LIST;
			break;
		case CSV_FORMAT_EDGE_LIST:
			actualFormat = CSVFormat.EDGE_LIST;
			break;
		default:
			actualFormat = CSVFormat.MATRIX;
			break;
		}

		CSVImporter<Integer, Integer> importer = new CSVImporter<>(actualFormat);

		if (importIdFunctionPointer.isNonNull()) {
			importer.setVertexFactory(x -> {
				CCharPointerHolder holder = StringUtils.toCStringInUtf8(x);
				return importIdFunctionPointer.invoke(holder.get());
			});
		}
		
		setupNotifyVertexEdge(importer, notifyVertexFunctionPointer, notifyEdgeFunctionPointer);

		importer.setParameter(CSVFormat.Parameter.EDGE_WEIGHTS, import_edge_weights);
		importer.setParameter(CSVFormat.Parameter.MATRIX_FORMAT_NODEID, matrix_format_nodeid);
		importer.setParameter(CSVFormat.Parameter.MATRIX_FORMAT_ZERO_WHEN_NO_EDGE, matrix_format_zero_when_no_edge);

		String inputAsJava = StringUtils.toJavaStringFromUtf8(input);
		try (StringReader reader = new StringReader(inputAsJava)) {
			importer.importGraph(g, reader);
		}

		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "import_file_gexf", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int importGEXFFromFile(IsolateThread thread, ObjectHandle graphHandle, CCharPointer filename,
			ImportIdFunctionPointer importIdFunctionPointer, boolean validate_schema,
			IntegerIdNotifyAttributeFunctionPointer vertexAttributeFunction,
			IntegerIdNotifyAttributeFunctionPointer edgeAttributeFunction, IFunctionPointer notifyVertexFunctionPointer,
			IFunctionPointer notifyEdgeFunctionPointer) {
		Graph<Integer, Integer> g = globalHandles.get(graphHandle);

		SimpleGEXFImporter<Integer, Integer> importer = new SimpleGEXFImporter<>();

		if (importIdFunctionPointer.isNonNull()) {
			importer.setVertexFactory(x -> {
				CCharPointerHolder holder = StringUtils.toCStringInUtf8(x);
				return importIdFunctionPointer.invoke(holder.get());
			});
		}

		setupNotifyVertexEdge(importer, notifyVertexFunctionPointer, notifyEdgeFunctionPointer);
		setupImportAttributes(importer, vertexAttributeFunction, edgeAttributeFunction, null);

		importer.setSchemaValidation(validate_schema);

		importer.importGraph(g, new File(StringUtils.toJavaStringFromUtf8(filename)));

		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "import_string_gexf", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int importGEXFFromString(IsolateThread thread, ObjectHandle graphHandle, CCharPointer input,
			ImportIdFunctionPointer importIdFunctionPointer, boolean validate_schema,
			IntegerIdNotifyAttributeFunctionPointer vertexAttributeFunction,
			IntegerIdNotifyAttributeFunctionPointer edgeAttributeFunction, IFunctionPointer notifyVertexFunctionPointer,
			IFunctionPointer notifyEdgeFunctionPointer) {
		Graph<Integer, Integer> g = globalHandles.get(graphHandle);

		SimpleGEXFImporter<Integer, Integer> importer = new SimpleGEXFImporter<>();

		if (importIdFunctionPointer.isNonNull()) {
			importer.setVertexFactory(x -> {
				CCharPointerHolder holder = StringUtils.toCStringInUtf8(x);
				return importIdFunctionPointer.invoke(holder.get());
			});
		}

		setupNotifyVertexEdge(importer, notifyVertexFunctionPointer, notifyEdgeFunctionPointer);
		setupImportAttributes(importer, vertexAttributeFunction, edgeAttributeFunction, null);

		importer.setSchemaValidation(validate_schema);

		String inputAsJava = StringUtils.toJavaStringFromUtf8(input);
		try (StringReader reader = new StringReader(inputAsJava)) {
			importer.importGraph(g, reader);
		}

		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "import_file_graphml_simple", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int importGraphMLSimpleFromFile(IsolateThread thread, ObjectHandle graphHandle, CCharPointer filename,
			ImportIdFunctionPointer importIdFunctionPointer, boolean validate_schema,
			IntegerIdNotifyAttributeFunctionPointer vertexAttributeFunction,
			IntegerIdNotifyAttributeFunctionPointer edgeAttributeFunction, IFunctionPointer notifyVertexFunctionPointer,
			IFunctionPointer notifyEdgeFunctionPointer) {
		Graph<Integer, Integer> g = globalHandles.get(graphHandle);

		SimpleGraphMLImporter<Integer, Integer> importer = new SimpleGraphMLImporter<>();

		if (importIdFunctionPointer.isNonNull()) {
			importer.setVertexFactory(x -> {
				CCharPointerHolder holder = StringUtils.toCStringInUtf8(x);
				return importIdFunctionPointer.invoke(holder.get());
			});
		}

		setupNotifyVertexEdge(importer, notifyVertexFunctionPointer, notifyEdgeFunctionPointer);
		setupImportAttributes(importer, vertexAttributeFunction, edgeAttributeFunction, null);

		importer.setSchemaValidation(validate_schema);

		importer.importGraph(g, new File(StringUtils.toJavaStringFromUtf8(filename)));

		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "import_string_graphml_simple", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int importGraphMLSimpleFromString(IsolateThread thread, ObjectHandle graphHandle, CCharPointer input,
			ImportIdFunctionPointer importIdFunctionPointer, boolean validate_schema,
			IntegerIdNotifyAttributeFunctionPointer vertexAttributeFunction,
			IntegerIdNotifyAttributeFunctionPointer edgeAttributeFunction, IFunctionPointer notifyVertexFunctionPointer,
			IFunctionPointer notifyEdgeFunctionPointer) {
		Graph<Integer, Integer> g = globalHandles.get(graphHandle);

		SimpleGraphMLImporter<Integer, Integer> importer = new SimpleGraphMLImporter<>();

		if (importIdFunctionPointer.isNonNull()) {
			importer.setVertexFactory(x -> {
				CCharPointerHolder holder = StringUtils.toCStringInUtf8(x);
				return importIdFunctionPointer.invoke(holder.get());
			});
		}

		setupNotifyVertexEdge(importer, notifyVertexFunctionPointer, notifyEdgeFunctionPointer);
		setupImportAttributes(importer, vertexAttributeFunction, edgeAttributeFunction, null);

		importer.setSchemaValidation(validate_schema);

		String inputAsJava = StringUtils.toJavaStringFromUtf8(input);
		try (StringReader reader = new StringReader(inputAsJava)) {
			importer.importGraph(g, reader);
		}

		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "import_file_graphml", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int importGraphMLFromFile(IsolateThread thread, ObjectHandle graphHandle, CCharPointer filename,
			ImportIdFunctionPointer importIdFunctionPointer, boolean validate_schema,
			IntegerIdNotifyAttributeFunctionPointer vertexAttributeFunction,
			IntegerIdNotifyAttributeFunctionPointer edgeAttributeFunction, IFunctionPointer notifyVertexFunctionPointer,
			IFunctionPointer notifyEdgeFunctionPointer) {
		Graph<Integer, Integer> g = globalHandles.get(graphHandle);

		GraphMLImporter<Integer, Integer> importer = new GraphMLImporter<>();

		if (importIdFunctionPointer.isNonNull()) {
			importer.setVertexFactory(x -> {
				CCharPointerHolder holder = StringUtils.toCStringInUtf8(x);
				return importIdFunctionPointer.invoke(holder.get());
			});
		}

		setupNotifyVertexEdge(importer, notifyVertexFunctionPointer, notifyEdgeFunctionPointer);
		setupImportAttributes(importer, vertexAttributeFunction, edgeAttributeFunction, null);

		importer.setSchemaValidation(validate_schema);

		importer.importGraph(g, new File(StringUtils.toJavaStringFromUtf8(filename)));

		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "import_string_graphml", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int importGraphMLFromString(IsolateThread thread, ObjectHandle graphHandle, CCharPointer input,
			ImportIdFunctionPointer importIdFunctionPointer, boolean validate_schema,
			IntegerIdNotifyAttributeFunctionPointer vertexAttributeFunction,
			IntegerIdNotifyAttributeFunctionPointer edgeAttributeFunction, IFunctionPointer notifyVertexFunctionPointer,
			IFunctionPointer notifyEdgeFunctionPointer) {
		Graph<Integer, Integer> g = globalHandles.get(graphHandle);

		GraphMLImporter<Integer, Integer> importer = new GraphMLImporter<>();

		if (importIdFunctionPointer.isNonNull()) {
			importer.setVertexFactory(x -> {
				CCharPointerHolder holder = StringUtils.toCStringInUtf8(x);
				return importIdFunctionPointer.invoke(holder.get());
			});
		}

		setupNotifyVertexEdge(importer, notifyVertexFunctionPointer, notifyEdgeFunctionPointer);
		setupImportAttributes(importer, vertexAttributeFunction, edgeAttributeFunction, null);

		importer.setSchemaValidation(validate_schema);

		String inputAsJava = StringUtils.toJavaStringFromUtf8(input);
		try (StringReader reader = new StringReader(inputAsJava)) {
			importer.importGraph(g, reader);
		}

		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "import_file_dot", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int importDOTFromFile(IsolateThread thread, ObjectHandle graphHandle, CCharPointer filename,
			ImportIdFunctionPointer importIdFunctionPointer, IntegerIdNotifyAttributeFunctionPointer vertexAttributeFunction,
			IntegerIdNotifyAttributeFunctionPointer edgeAttributeFunction, IFunctionPointer notifyVertexFunctionPointer,
			IFunctionPointer notifyEdgeFunctionPointer) {
		Graph<Integer, Integer> g = globalHandles.get(graphHandle);

		DOTImporter<Integer, Integer> importer = new DOTImporter<>();

		if (importIdFunctionPointer.isNonNull()) {
			importer.setVertexFactory(x -> {
				CCharPointerHolder holder = StringUtils.toCStringInUtf8(x);
				return importIdFunctionPointer.invoke(holder.get());
			});
		}

		setupNotifyVertexEdge(importer, notifyVertexFunctionPointer, notifyEdgeFunctionPointer);
		setupImportAttributes(importer, vertexAttributeFunction, edgeAttributeFunction, null);

		importer.importGraph(g, new File(StringUtils.toJavaStringFromUtf8(filename)));

		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "import_string_dot", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int importDOTFromString(IsolateThread thread, ObjectHandle graphHandle, CCharPointer input,
			ImportIdFunctionPointer importIdFunctionPointer, IntegerIdNotifyAttributeFunctionPointer vertexAttributeFunction,
			IntegerIdNotifyAttributeFunctionPointer edgeAttributeFunction, IFunctionPointer notifyVertexFunctionPointer,
			IFunctionPointer notifyEdgeFunctionPointer) {
		Graph<Integer, Integer> g = globalHandles.get(graphHandle);

		DOTImporter<Integer, Integer> importer = new DOTImporter<>();

		if (importIdFunctionPointer.isNonNull()) {
			importer.setVertexFactory(x -> {
				CCharPointerHolder holder = StringUtils.toCStringInUtf8(x);
				return importIdFunctionPointer.invoke(holder.get());
			});
		}

		setupNotifyVertexEdge(importer, notifyVertexFunctionPointer, notifyEdgeFunctionPointer);
		setupImportAttributes(importer, vertexAttributeFunction, edgeAttributeFunction, null);

		String inputAsJava = StringUtils.toJavaStringFromUtf8(input);
		try (StringReader reader = new StringReader(inputAsJava)) {
			importer.importGraph(g, reader);
		}

		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "import_file_graph6sparse6", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int importGraph6FromFile(IsolateThread thread, ObjectHandle graphHandle, CCharPointer filename,
			ImportIdFunctionPointer importIdFunctionPointer, IntegerIdNotifyAttributeFunctionPointer vertexAttributeFunction,
			IntegerIdNotifyAttributeFunctionPointer edgeAttributeFunction, IFunctionPointer notifyVertexFunctionPointer,
			IFunctionPointer notifyEdgeFunctionPointer) {
		Graph<Integer, Integer> g = globalHandles.get(graphHandle);

		Graph6Sparse6Importer<Integer, Integer> importer = new Graph6Sparse6Importer<>();

		if (importIdFunctionPointer.isNonNull()) {
			importer.setVertexFactory(x -> {
				String xAsAString = String.valueOf(x);
				CCharPointerHolder holder = StringUtils.toCStringInUtf8(xAsAString);
				return importIdFunctionPointer.invoke(holder.get());
			});
		}

		setupNotifyVertexEdge(importer, notifyVertexFunctionPointer, notifyEdgeFunctionPointer);
		setupImportAttributes(importer, vertexAttributeFunction, edgeAttributeFunction, null);

		importer.importGraph(g, new File(StringUtils.toJavaStringFromUtf8(filename)));

		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "import_string_graph6sparse6", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int importGraph6FromString(IsolateThread thread, ObjectHandle graphHandle, CCharPointer input,
			ImportIdFunctionPointer importIdFunctionPointer, IntegerIdNotifyAttributeFunctionPointer vertexAttributeFunction,
			IntegerIdNotifyAttributeFunctionPointer edgeAttributeFunction, IFunctionPointer notifyVertexFunctionPointer,
			IFunctionPointer notifyEdgeFunctionPointer) {
		Graph<Integer, Integer> g = globalHandles.get(graphHandle);

		Graph6Sparse6Importer<Integer, Integer> importer = new Graph6Sparse6Importer<>();

		if (importIdFunctionPointer.isNonNull()) {
			importer.setVertexFactory(x -> {
				String xAsAString = String.valueOf(x);
				CCharPointerHolder holder = StringUtils.toCStringInUtf8(xAsAString);
				return importIdFunctionPointer.invoke(holder.get());
			});
		}

		setupNotifyVertexEdge(importer, notifyVertexFunctionPointer, notifyEdgeFunctionPointer);
		setupImportAttributes(importer, vertexAttributeFunction, edgeAttributeFunction, null);

		String inputAsJava = StringUtils.toJavaStringFromUtf8(input);
		try (StringReader reader = new StringReader(inputAsJava)) {
			importer.importGraph(g, reader);
		}

		return Status.STATUS_SUCCESS.getCValue();
	}

	private static void setupImportAttributes(BaseEventDrivenImporter<Integer, Integer> importer,
			IntegerIdNotifyAttributeFunctionPointer vertexAttributeFunction,
			IntegerIdNotifyAttributeFunctionPointer edgeAttributeFunction, CharSequenceTranslator unescapeTranslator) {
		if (vertexAttributeFunction.isNonNull()) {
			importer.addVertexAttributeConsumer((p, attr) -> {
				int vertex = p.getFirst();
				String key = p.getSecond();
				CCharPointerHolder keyHolder = StringUtils.toCStringInUtf8(key);
				String value = attr.getValue();
				if (unescapeTranslator != null) {
					value = unescapeTranslator.translate(value);
				}
				CCharPointerHolder valueHolder = StringUtils.toCStringInUtf8(value);
				vertexAttributeFunction.invoke(vertex, keyHolder.get(), valueHolder.get());
			});
		}

		if (edgeAttributeFunction.isNonNull()) {
			importer.addEdgeAttributeConsumer((p, attr) -> {
				int edge = p.getFirst();
				String key = p.getSecond();
				CCharPointerHolder keyHolder = StringUtils.toCStringInUtf8(key);
				String value = attr.getValue();
				if (unescapeTranslator != null) {
					value = unescapeTranslator.translate(value);
				}
				CCharPointerHolder valueHolder = StringUtils.toCStringInUtf8(value);
				edgeAttributeFunction.invoke(edge, keyHolder.get(), valueHolder.get());
			});
		}
	}

	private static void setupNotifyVertexEdge(BaseEventDrivenImporter<Integer, Integer> importer,
			IFunctionPointer notifyVertexFunctionPointer, IFunctionPointer notifyEdgeFunctionPointer) {
		if (notifyVertexFunctionPointer.isNonNull()) {
			importer.addVertexConsumer(v -> {
				notifyVertexFunctionPointer.invoke(v);
			});
		}

		if (notifyEdgeFunctionPointer.isNonNull()) {
			importer.addEdgeConsumer(e -> {
				notifyEdgeFunctionPointer.invoke(e);
			});
		}
	}

}
