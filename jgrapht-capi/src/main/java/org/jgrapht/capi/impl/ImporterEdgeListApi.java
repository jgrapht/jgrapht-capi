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
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.text.StringEscapeUtils;
import org.apache.commons.text.translate.CharSequenceTranslator;
import org.graalvm.nativeimage.IsolateThread;
import org.graalvm.nativeimage.ObjectHandles;
import org.graalvm.nativeimage.c.function.CEntryPoint;
import org.graalvm.nativeimage.c.type.CCharPointer;
import org.graalvm.nativeimage.c.type.CTypeConversion.CCharPointerHolder;
import org.graalvm.nativeimage.c.type.WordPointer;
import org.jgrapht.Graph;
import org.jgrapht.alg.util.Pair;
import org.jgrapht.alg.util.Triple;
import org.jgrapht.capi.Constants;
import org.jgrapht.capi.JGraphTContext.ImporterExporterCSVFormat;
import org.jgrapht.capi.JGraphTContext.IntegerIdNotifyAttributeFunctionPointer;
import org.jgrapht.capi.JGraphTContext.Status;
import org.jgrapht.capi.JGraphTContext.StringIdNotifyAttributeFunctionPointer;
import org.jgrapht.capi.StringUtils;
import org.jgrapht.capi.error.StatusReturnExceptionHandler;
import org.jgrapht.nio.BaseEventDrivenImporter;
import org.jgrapht.nio.csv.CSVEventDrivenImporter;
import org.jgrapht.nio.csv.CSVFormat;
import org.jgrapht.nio.dimacs.DIMACSEventDrivenImporter;
import org.jgrapht.nio.dot.DOTEventDrivenImporter;
import org.jgrapht.nio.gexf.SimpleGEXFEventDrivenImporter;
import org.jgrapht.nio.gml.GmlEventDrivenImporter;
import org.jgrapht.nio.graph6.Graph6Sparse6EventDrivenImporter;
import org.jgrapht.nio.graphml.GraphMLEventDrivenImporter;
import org.jgrapht.nio.graphml.SimpleGraphMLEventDrivenImporter;
import org.jgrapht.nio.json.JSONEventDrivenImporter;

/**
 * API for importing to an edge list.
 * 
 * We have two versions for each, one without any attributes and one with
 * attributes. The reason is that without attributes we can simply return an
 * edge list. In the case we also want attributes, we have to report the
 * attributes to the user given some edge identifier, which forces us to perform
 * extra bookkeeping (thus being slower and more memory hungry). The reported
 * edge identifier is the index of the returned edge in the edge list.
 * 
 * Note that this is required, as the JGraphT importers are not guaranteed to
 * return attributes in some particular order.
 */
public class ImporterEdgeListApi {

	private static ObjectHandles globalHandles = ObjectHandles.getGlobal();

	// ------------------------- DIMACS ------------------------------

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "import_edgelist_noattrs_file_dimacs", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int importEdgelistWithNoAttrsFromDimacsFile(IsolateThread thread, CCharPointer filename,
			WordPointer res) {
		List<Triple<CCharPointerHolder, CCharPointerHolder, Double>> edgelist = new ArrayList<>();

		DIMACSEventDrivenImporter importer = new DIMACSEventDrivenImporter().renumberVertices(false)
				.zeroBasedNumbering(true);

		setupImporterWithEdgeList(importer, edgelist);

		importer.importInput(new File(StringUtils.toJavaStringFromUtf8(filename)));

		if (res.isNonNull()) {
			res.write(globalHandles.create(edgelist));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "import_edgelist_noattrs_string_dimacs", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int importEdgelistWithNoAttrsFromDimacsString(IsolateThread thread, CCharPointer input,
			WordPointer res) {
		List<Triple<CCharPointerHolder, CCharPointerHolder, Double>> edgelist = new ArrayList<>();

		DIMACSEventDrivenImporter importer = new DIMACSEventDrivenImporter().renumberVertices(false)
				.zeroBasedNumbering(true);

		setupImporterWithEdgeList(importer, edgelist);

		String inputAsJava = StringUtils.toJavaStringFromUtf8(input);
		try (StringReader reader = new StringReader(inputAsJava)) {
			importer.importInput(reader);
		}

		if (res.isNonNull()) {
			res.write(globalHandles.create(edgelist));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "import_edgelist_attrs_file_dimacs", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int importEdgelistWithAttrsFromDimacsFile(IsolateThread thread, CCharPointer filename,
			StringIdNotifyAttributeFunctionPointer vertexAttributeFunction,
			IntegerIdNotifyAttributeFunctionPointer edgeAttributeFunction, WordPointer res) {
		List<Triple<CCharPointerHolder, CCharPointerHolder, Double>> edgelist = new ArrayList<>();

		DIMACSEventDrivenImporter importer = new DIMACSEventDrivenImporter().renumberVertices(false)
				.zeroBasedNumbering(true);

		setupImporterWithEdgeListWithIds(importer, edgelist, vertexAttributeFunction, edgeAttributeFunction, null);

		importer.importInput(new File(StringUtils.toJavaStringFromUtf8(filename)));
		if (res.isNonNull()) {
			res.write(globalHandles.create(edgelist));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "import_edgelist_attrs_string_dimacs", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int importEdgelistWithAttrsFromDimacsString(IsolateThread thread, CCharPointer input,
			StringIdNotifyAttributeFunctionPointer vertexAttributeFunction,
			IntegerIdNotifyAttributeFunctionPointer edgeAttributeFunction, WordPointer res) {
		List<Triple<CCharPointerHolder, CCharPointerHolder, Double>> edgelist = new ArrayList<>();

		DIMACSEventDrivenImporter importer = new DIMACSEventDrivenImporter().renumberVertices(false)
				.zeroBasedNumbering(true);

		setupImporterWithEdgeListWithIds(importer, edgelist, vertexAttributeFunction, edgeAttributeFunction, null);

		String inputAsJava = StringUtils.toJavaStringFromUtf8(input);
		try (StringReader reader = new StringReader(inputAsJava)) {
			importer.importInput(reader);
		}
		if (res.isNonNull()) {
			res.write(globalHandles.create(edgelist));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	// ------------------------- GML ---------------------------------

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "import_edgelist_noattrs_file_gml", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int importEdgelistWithNoAttrsFromGmlFile(IsolateThread thread, CCharPointer filename,
			WordPointer res) {
		List<Triple<CCharPointerHolder, CCharPointerHolder, Double>> edgelist = new ArrayList<>();

		GmlEventDrivenImporter importer = new GmlEventDrivenImporter();

		setupImporterWithEdgeList(importer, edgelist);

		importer.importInput(new File(StringUtils.toJavaStringFromUtf8(filename)));

		if (res.isNonNull()) {
			res.write(globalHandles.create(edgelist));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "import_edgelist_noattrs_string_gml", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int importEdgelistWithNoAttrsFromGmlString(IsolateThread thread, CCharPointer input,
			WordPointer res) {
		List<Triple<CCharPointerHolder, CCharPointerHolder, Double>> edgelist = new ArrayList<>();

		GmlEventDrivenImporter importer = new GmlEventDrivenImporter();

		setupImporterWithEdgeList(importer, edgelist);

		String inputAsJava = StringUtils.toJavaStringFromUtf8(input);
		try (StringReader reader = new StringReader(inputAsJava)) {
			importer.importInput(reader);
		}

		if (res.isNonNull()) {
			res.write(globalHandles.create(edgelist));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "import_edgelist_attrs_file_gml", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int importEdgelistWithAttrsFromGmlFile(IsolateThread thread, CCharPointer filename,
			StringIdNotifyAttributeFunctionPointer vertexAttributeFunction,
			IntegerIdNotifyAttributeFunctionPointer edgeAttributeFunction, WordPointer res) {
		List<Triple<CCharPointerHolder, CCharPointerHolder, Double>> edgelist = new ArrayList<>();

		GmlEventDrivenImporter importer = new GmlEventDrivenImporter();

		setupImporterWithEdgeListWithIds(importer, edgelist, vertexAttributeFunction, edgeAttributeFunction, null);

		importer.importInput(new File(StringUtils.toJavaStringFromUtf8(filename)));
		if (res.isNonNull()) {
			res.write(globalHandles.create(edgelist));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "import_edgelist_attrs_string_gml", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int importEdgelistWithAttrsFromGmlString(IsolateThread thread, CCharPointer input,
			StringIdNotifyAttributeFunctionPointer vertexAttributeFunction,
			IntegerIdNotifyAttributeFunctionPointer edgeAttributeFunction, WordPointer res) {
		List<Triple<CCharPointerHolder, CCharPointerHolder, Double>> edgelist = new ArrayList<>();

		GmlEventDrivenImporter importer = new GmlEventDrivenImporter();

		setupImporterWithEdgeListWithIds(importer, edgelist, vertexAttributeFunction, edgeAttributeFunction, null);

		String inputAsJava = StringUtils.toJavaStringFromUtf8(input);
		try (StringReader reader = new StringReader(inputAsJava)) {
			importer.importInput(reader);
		}
		if (res.isNonNull()) {
			res.write(globalHandles.create(edgelist));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	// ------------------------- JSON ---------------------------------

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "import_edgelist_noattrs_file_json", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int importEdgelistWithNoAttrsFromJsonFile(IsolateThread thread, CCharPointer filename,
			WordPointer res) {
		List<Triple<CCharPointerHolder, CCharPointerHolder, Double>> edgelist = new ArrayList<>();
		JSONEventDrivenImporter importer = new JSONEventDrivenImporter();

		setupImporterWithEdgeList(importer, edgelist);

		importer.importInput(new File(StringUtils.toJavaStringFromUtf8(filename)));

		if (res.isNonNull()) {
			res.write(globalHandles.create(edgelist));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "import_edgelist_noattrs_string_json", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int importEdgelistWithNoAttrsFromJsonString(IsolateThread thread, CCharPointer input,
			WordPointer res) {
		List<Triple<CCharPointerHolder, CCharPointerHolder, Double>> edgelist = new ArrayList<>();
		JSONEventDrivenImporter importer = new JSONEventDrivenImporter();
		setupImporterWithEdgeList(importer, edgelist);

		String inputAsJava = StringUtils.toJavaStringFromUtf8(input);
		try (StringReader reader = new StringReader(inputAsJava)) {
			importer.importInput(reader);
		}

		if (res.isNonNull()) {
			res.write(globalHandles.create(edgelist));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "import_edgelist_attrs_file_json", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int importEdgelistWithAttrsFromJsonFile(IsolateThread thread, CCharPointer filename,
			StringIdNotifyAttributeFunctionPointer vertexAttributeFunction,
			IntegerIdNotifyAttributeFunctionPointer edgeAttributeFunction, WordPointer res) {
		List<Triple<CCharPointerHolder, CCharPointerHolder, Double>> edgelist = new ArrayList<>();
		JSONEventDrivenImporter importer = new JSONEventDrivenImporter();

		setupImporterWithEdgeListWithIds(importer, edgelist, vertexAttributeFunction, edgeAttributeFunction,
				StringEscapeUtils.UNESCAPE_JSON);

		importer.importInput(new File(StringUtils.toJavaStringFromUtf8(filename)));
		if (res.isNonNull()) {
			res.write(globalHandles.create(edgelist));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "import_edgelist_attrs_string_json", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int importEdgelistWithAttrsFromJsonString(IsolateThread thread, CCharPointer input,
			StringIdNotifyAttributeFunctionPointer vertexAttributeFunction,
			IntegerIdNotifyAttributeFunctionPointer edgeAttributeFunction, WordPointer res) {
		List<Triple<CCharPointerHolder, CCharPointerHolder, Double>> edgelist = new ArrayList<>();
		JSONEventDrivenImporter importer = new JSONEventDrivenImporter();
		setupImporterWithEdgeListWithIds(importer, edgelist, vertexAttributeFunction, edgeAttributeFunction,
				StringEscapeUtils.UNESCAPE_JSON);

		String inputAsJava = StringUtils.toJavaStringFromUtf8(input);
		try (StringReader reader = new StringReader(inputAsJava)) {
			importer.importInput(reader);
		}
		if (res.isNonNull()) {
			res.write(globalHandles.create(edgelist));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	// ------------------------- CSV ---------------------------------

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "import_edgelist_noattrs_file_csv", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int importEdgelistWithNoAttrsFromCsvFile(IsolateThread thread, CCharPointer filename,
			ImporterExporterCSVFormat format, boolean import_edge_weights, boolean matrix_format_nodeid,
			boolean matrix_format_zero_when_no_edge, WordPointer res) {
		List<Triple<CCharPointerHolder, CCharPointerHolder, Double>> edgelist = new ArrayList<>();

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

		CSVEventDrivenImporter importer = new CSVEventDrivenImporter(actualFormat);

		importer.setParameter(CSVFormat.Parameter.EDGE_WEIGHTS, import_edge_weights);
		importer.setParameter(CSVFormat.Parameter.MATRIX_FORMAT_NODEID, matrix_format_nodeid);
		importer.setParameter(CSVFormat.Parameter.MATRIX_FORMAT_ZERO_WHEN_NO_EDGE, matrix_format_zero_when_no_edge);

		setupImporterWithEdgeList(importer, edgelist);

		importer.importInput(new File(StringUtils.toJavaStringFromUtf8(filename)));

		if (res.isNonNull()) {
			res.write(globalHandles.create(edgelist));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "import_edgelist_noattrs_string_csv", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int importEdgelistWithNoAttrsFromCsvString(IsolateThread thread, CCharPointer input,
			ImporterExporterCSVFormat format, boolean import_edge_weights, boolean matrix_format_nodeid,
			boolean matrix_format_zero_when_no_edge, WordPointer res) {
		List<Triple<CCharPointerHolder, CCharPointerHolder, Double>> edgelist = new ArrayList<>();

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

		CSVEventDrivenImporter importer = new CSVEventDrivenImporter(actualFormat);

		importer.setParameter(CSVFormat.Parameter.EDGE_WEIGHTS, import_edge_weights);
		importer.setParameter(CSVFormat.Parameter.MATRIX_FORMAT_NODEID, matrix_format_nodeid);
		importer.setParameter(CSVFormat.Parameter.MATRIX_FORMAT_ZERO_WHEN_NO_EDGE, matrix_format_zero_when_no_edge);

		setupImporterWithEdgeList(importer, edgelist);

		String inputAsJava = StringUtils.toJavaStringFromUtf8(input);
		try (StringReader reader = new StringReader(inputAsJava)) {
			importer.importInput(reader);
		}

		if (res.isNonNull()) {
			res.write(globalHandles.create(edgelist));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "import_edgelist_attrs_file_csv", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int importEdgelistWithAttrsFromCsvFile(IsolateThread thread, CCharPointer filename,
			StringIdNotifyAttributeFunctionPointer vertexAttributeFunction,
			IntegerIdNotifyAttributeFunctionPointer edgeAttributeFunction, ImporterExporterCSVFormat format,
			boolean import_edge_weights, boolean matrix_format_nodeid, boolean matrix_format_zero_when_no_edge,
			WordPointer res) {
		List<Triple<CCharPointerHolder, CCharPointerHolder, Double>> edgelist = new ArrayList<>();

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

		CSVEventDrivenImporter importer = new CSVEventDrivenImporter(actualFormat);

		importer.setParameter(CSVFormat.Parameter.EDGE_WEIGHTS, import_edge_weights);
		importer.setParameter(CSVFormat.Parameter.MATRIX_FORMAT_NODEID, matrix_format_nodeid);
		importer.setParameter(CSVFormat.Parameter.MATRIX_FORMAT_ZERO_WHEN_NO_EDGE, matrix_format_zero_when_no_edge);

		setupImporterWithEdgeListWithIds(importer, edgelist, vertexAttributeFunction, edgeAttributeFunction, null);

		importer.importInput(new File(StringUtils.toJavaStringFromUtf8(filename)));
		if (res.isNonNull()) {
			res.write(globalHandles.create(edgelist));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "import_edgelist_attrs_string_csv", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int importEdgelistWithAttrsFromCsvString(IsolateThread thread, CCharPointer input,
			StringIdNotifyAttributeFunctionPointer vertexAttributeFunction,
			IntegerIdNotifyAttributeFunctionPointer edgeAttributeFunction, ImporterExporterCSVFormat format,
			boolean import_edge_weights, boolean matrix_format_nodeid, boolean matrix_format_zero_when_no_edge,
			WordPointer res) {
		List<Triple<CCharPointerHolder, CCharPointerHolder, Double>> edgelist = new ArrayList<>();

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

		CSVEventDrivenImporter importer = new CSVEventDrivenImporter(actualFormat);

		importer.setParameter(CSVFormat.Parameter.EDGE_WEIGHTS, import_edge_weights);
		importer.setParameter(CSVFormat.Parameter.MATRIX_FORMAT_NODEID, matrix_format_nodeid);
		importer.setParameter(CSVFormat.Parameter.MATRIX_FORMAT_ZERO_WHEN_NO_EDGE, matrix_format_zero_when_no_edge);

		setupImporterWithEdgeListWithIds(importer, edgelist, vertexAttributeFunction, edgeAttributeFunction, null);

		String inputAsJava = StringUtils.toJavaStringFromUtf8(input);
		try (StringReader reader = new StringReader(inputAsJava)) {
			importer.importInput(reader);
		}
		if (res.isNonNull()) {
			res.write(globalHandles.create(edgelist));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	// ------------------------- GEFX ---------------------------------

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "import_edgelist_noattrs_file_gexf", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int importEdgelistWithNoAttrsFromGexfFile(IsolateThread thread, CCharPointer filename,
			boolean validate_schema, WordPointer res) {
		List<Triple<CCharPointerHolder, CCharPointerHolder, Double>> edgelist = new ArrayList<>();

		SimpleGEXFEventDrivenImporter importer = new SimpleGEXFEventDrivenImporter();
		importer.setSchemaValidation(validate_schema);

		setupImporterWithEdgeList(importer, edgelist);

		importer.importInput(new File(StringUtils.toJavaStringFromUtf8(filename)));

		if (res.isNonNull()) {
			res.write(globalHandles.create(edgelist));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "import_edgelist_noattrs_string_gexf", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int importEdgelistWithNoAttrsFromGexfString(IsolateThread thread, CCharPointer input,
			boolean validate_schema, WordPointer res) {
		List<Triple<CCharPointerHolder, CCharPointerHolder, Double>> edgelist = new ArrayList<>();

		SimpleGEXFEventDrivenImporter importer = new SimpleGEXFEventDrivenImporter();
		importer.setSchemaValidation(validate_schema);

		setupImporterWithEdgeList(importer, edgelist);

		String inputAsJava = StringUtils.toJavaStringFromUtf8(input);
		try (StringReader reader = new StringReader(inputAsJava)) {
			importer.importInput(reader);
		}

		if (res.isNonNull()) {
			res.write(globalHandles.create(edgelist));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "import_edgelist_attrs_file_gexf", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int importEdgelistWithAttrsFromGexfFile(IsolateThread thread, CCharPointer filename,
			boolean validate_schema, StringIdNotifyAttributeFunctionPointer vertexAttributeFunction,
			IntegerIdNotifyAttributeFunctionPointer edgeAttributeFunction, WordPointer res) {
		List<Triple<CCharPointerHolder, CCharPointerHolder, Double>> edgelist = new ArrayList<>();

		SimpleGEXFEventDrivenImporter importer = new SimpleGEXFEventDrivenImporter();
		importer.setSchemaValidation(validate_schema);

		setupImporterWithEdgeListWithIds(importer, edgelist, vertexAttributeFunction, edgeAttributeFunction, null);

		importer.importInput(new File(StringUtils.toJavaStringFromUtf8(filename)));
		if (res.isNonNull()) {
			res.write(globalHandles.create(edgelist));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "import_edgelist_attrs_string_gexf", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int importEdgelistWithAttrsFromGexfString(IsolateThread thread, CCharPointer input,
			boolean validate_schema, StringIdNotifyAttributeFunctionPointer vertexAttributeFunction,
			IntegerIdNotifyAttributeFunctionPointer edgeAttributeFunction, WordPointer res) {
		List<Triple<CCharPointerHolder, CCharPointerHolder, Double>> edgelist = new ArrayList<>();

		SimpleGEXFEventDrivenImporter importer = new SimpleGEXFEventDrivenImporter();
		importer.setSchemaValidation(validate_schema);

		setupImporterWithEdgeListWithIds(importer, edgelist, vertexAttributeFunction, edgeAttributeFunction, null);

		String inputAsJava = StringUtils.toJavaStringFromUtf8(input);
		try (StringReader reader = new StringReader(inputAsJava)) {
			importer.importInput(reader);
		}
		if (res.isNonNull()) {
			res.write(globalHandles.create(edgelist));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	// ------------------------ GraphML simple -----------------------

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "import_edgelist_noattrs_file_graphml_simple", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int importEdgelistWithNoAttrsFromGraphmlSimpleFile(IsolateThread thread, CCharPointer filename,
			boolean validate_schema, WordPointer res) {
		List<Triple<CCharPointerHolder, CCharPointerHolder, Double>> edgelist = new ArrayList<>();

		SimpleGraphMLEventDrivenImporter importer = new SimpleGraphMLEventDrivenImporter();
		importer.setSchemaValidation(validate_schema);

		setupImporterWithEdgeList(importer, edgelist);

		importer.importInput(new File(StringUtils.toJavaStringFromUtf8(filename)));

		if (res.isNonNull()) {
			res.write(globalHandles.create(edgelist));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "import_edgelist_noattrs_string_graphml_simple", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int importEdgelistWithNoAttrsFromGraphmlSimpleString(IsolateThread thread, CCharPointer input,
			boolean validate_schema, WordPointer res) {
		List<Triple<CCharPointerHolder, CCharPointerHolder, Double>> edgelist = new ArrayList<>();

		SimpleGraphMLEventDrivenImporter importer = new SimpleGraphMLEventDrivenImporter();
		importer.setSchemaValidation(validate_schema);

		setupImporterWithEdgeList(importer, edgelist);

		String inputAsJava = StringUtils.toJavaStringFromUtf8(input);
		try (StringReader reader = new StringReader(inputAsJava)) {
			importer.importInput(reader);
		}

		if (res.isNonNull()) {
			res.write(globalHandles.create(edgelist));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "import_edgelist_attrs_file_graphml_simple", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int importEdgelistWithAttrsFromGraphmlSimpleFile(IsolateThread thread, CCharPointer filename,
			boolean validate_schema, StringIdNotifyAttributeFunctionPointer vertexAttributeFunction,
			IntegerIdNotifyAttributeFunctionPointer edgeAttributeFunction, WordPointer res) {
		List<Triple<CCharPointerHolder, CCharPointerHolder, Double>> edgelist = new ArrayList<>();

		SimpleGraphMLEventDrivenImporter importer = new SimpleGraphMLEventDrivenImporter();
		importer.setSchemaValidation(validate_schema);

		setupImporterWithEdgeListWithIds(importer, edgelist, vertexAttributeFunction, edgeAttributeFunction, null);

		importer.importInput(new File(StringUtils.toJavaStringFromUtf8(filename)));
		if (res.isNonNull()) {
			res.write(globalHandles.create(edgelist));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "import_edgelist_attrs_string_graphml_simple", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int importEdgelistWithAttrsFromGraphmlSimpleString(IsolateThread thread, CCharPointer input,
			boolean validate_schema, StringIdNotifyAttributeFunctionPointer vertexAttributeFunction,
			IntegerIdNotifyAttributeFunctionPointer edgeAttributeFunction, WordPointer res) {
		List<Triple<CCharPointerHolder, CCharPointerHolder, Double>> edgelist = new ArrayList<>();

		SimpleGraphMLEventDrivenImporter importer = new SimpleGraphMLEventDrivenImporter();
		importer.setSchemaValidation(validate_schema);

		setupImporterWithEdgeListWithIds(importer, edgelist, vertexAttributeFunction, edgeAttributeFunction, null);

		String inputAsJava = StringUtils.toJavaStringFromUtf8(input);
		try (StringReader reader = new StringReader(inputAsJava)) {
			importer.importInput(reader);
		}
		if (res.isNonNull()) {
			res.write(globalHandles.create(edgelist));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	// ------------------------ GraphML -----------------------------

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "import_edgelist_noattrs_file_graphml", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int importEdgelistWithNoAttrsFromGraphmlFile(IsolateThread thread, CCharPointer filename,
			boolean validate_schema, WordPointer res) {
		List<Triple<CCharPointerHolder, CCharPointerHolder, Double>> edgelist = new ArrayList<>();

		GraphMLEventDrivenImporter importer = new GraphMLEventDrivenImporter();
		importer.setSchemaValidation(validate_schema);

		setupImporterWithEdgeList(importer, edgelist);

		importer.importInput(new File(StringUtils.toJavaStringFromUtf8(filename)));

		if (res.isNonNull()) {
			res.write(globalHandles.create(edgelist));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "import_edgelist_noattrs_string_graphml", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int importEdgelistWithNoAttrsFromGraphmlString(IsolateThread thread, CCharPointer input,
			boolean validate_schema, WordPointer res) {
		List<Triple<CCharPointerHolder, CCharPointerHolder, Double>> edgelist = new ArrayList<>();

		GraphMLEventDrivenImporter importer = new GraphMLEventDrivenImporter();
		importer.setSchemaValidation(validate_schema);

		setupImporterWithEdgeList(importer, edgelist);

		String inputAsJava = StringUtils.toJavaStringFromUtf8(input);
		try (StringReader reader = new StringReader(inputAsJava)) {
			importer.importInput(reader);
		}

		if (res.isNonNull()) {
			res.write(globalHandles.create(edgelist));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "import_edgelist_attrs_file_graphml", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int importEdgelistWithAttrsFromGraphmlFile(IsolateThread thread, CCharPointer filename,
			boolean validate_schema, StringIdNotifyAttributeFunctionPointer vertexAttributeFunction,
			IntegerIdNotifyAttributeFunctionPointer edgeAttributeFunction, WordPointer res) {
		List<Triple<CCharPointerHolder, CCharPointerHolder, Double>> edgelist = new ArrayList<>();

		GraphMLEventDrivenImporter importer = new GraphMLEventDrivenImporter();
		importer.setSchemaValidation(validate_schema);

		setupImporterWithEdgeListWithIds(importer, edgelist, vertexAttributeFunction, edgeAttributeFunction, null);

		importer.importInput(new File(StringUtils.toJavaStringFromUtf8(filename)));
		if (res.isNonNull()) {
			res.write(globalHandles.create(edgelist));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "import_edgelist_attrs_string_graphml", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int importEdgelistWithAttrsFromGraphmlString(IsolateThread thread, CCharPointer input,
			boolean validate_schema, StringIdNotifyAttributeFunctionPointer vertexAttributeFunction,
			IntegerIdNotifyAttributeFunctionPointer edgeAttributeFunction, WordPointer res) {
		List<Triple<CCharPointerHolder, CCharPointerHolder, Double>> edgelist = new ArrayList<>();

		GraphMLEventDrivenImporter importer = new GraphMLEventDrivenImporter();
		importer.setSchemaValidation(validate_schema);

		setupImporterWithEdgeListWithIds(importer, edgelist, vertexAttributeFunction, edgeAttributeFunction, null);

		String inputAsJava = StringUtils.toJavaStringFromUtf8(input);
		try (StringReader reader = new StringReader(inputAsJava)) {
			importer.importInput(reader);
		}
		if (res.isNonNull()) {
			res.write(globalHandles.create(edgelist));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	// ------------------------- Dot ---------------------------------

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "import_edgelist_noattrs_file_dot", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int importEdgelistWithNoAttrsFromDotFile(IsolateThread thread, CCharPointer filename,
			WordPointer res) {
		List<Triple<CCharPointerHolder, CCharPointerHolder, Double>> edgelist = new ArrayList<>();

		DOTEventDrivenImporter importer = new DOTEventDrivenImporter();

		setupImporterWithPairEdgeList(importer, edgelist);

		importer.importInput(new File(StringUtils.toJavaStringFromUtf8(filename)));

		if (res.isNonNull()) {
			res.write(globalHandles.create(edgelist));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "import_edgelist_noattrs_string_dot", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int importEdgelistWithNoAttrsFromDotString(IsolateThread thread, CCharPointer input,
			WordPointer res) {
		List<Triple<CCharPointerHolder, CCharPointerHolder, Double>> edgelist = new ArrayList<>();

		DOTEventDrivenImporter importer = new DOTEventDrivenImporter();

		setupImporterWithPairEdgeList(importer, edgelist);

		String inputAsJava = StringUtils.toJavaStringFromUtf8(input);
		try (StringReader reader = new StringReader(inputAsJava)) {
			importer.importInput(reader);
		}

		if (res.isNonNull()) {
			res.write(globalHandles.create(edgelist));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "import_edgelist_attrs_file_dot", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int importEdgelistWithAttrsFromDotFile(IsolateThread thread, CCharPointer filename,
			StringIdNotifyAttributeFunctionPointer vertexAttributeFunction,
			IntegerIdNotifyAttributeFunctionPointer edgeAttributeFunction, WordPointer res) {
		List<Triple<CCharPointerHolder, CCharPointerHolder, Double>> edgelist = new ArrayList<>();

		DOTEventDrivenImporter importer = new DOTEventDrivenImporter();

		setupImporterWithPairEdgeListWithIds(importer, edgelist, vertexAttributeFunction, edgeAttributeFunction, null);

		importer.importInput(new File(StringUtils.toJavaStringFromUtf8(filename)));
		if (res.isNonNull()) {
			res.write(globalHandles.create(edgelist));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "import_edgelist_attrs_string_dot", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int importEdgelistWithAttrsFromDotString(IsolateThread thread, CCharPointer input,
			StringIdNotifyAttributeFunctionPointer vertexAttributeFunction,
			IntegerIdNotifyAttributeFunctionPointer edgeAttributeFunction, WordPointer res) {
		List<Triple<CCharPointerHolder, CCharPointerHolder, Double>> edgelist = new ArrayList<>();

		DOTEventDrivenImporter importer = new DOTEventDrivenImporter();

		setupImporterWithPairEdgeListWithIds(importer, edgelist, vertexAttributeFunction, edgeAttributeFunction, null);

		String inputAsJava = StringUtils.toJavaStringFromUtf8(input);
		try (StringReader reader = new StringReader(inputAsJava)) {
			importer.importInput(reader);
		}
		if (res.isNonNull()) {
			res.write(globalHandles.create(edgelist));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	// ------------------------- graph6sparse6 ---------------------------------

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "import_edgelist_noattrs_file_graph6sparse6", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int importEdgelistWithNoAttrsFromGraph6Sparse6File(IsolateThread thread, CCharPointer filename,
			WordPointer res) {
		List<Triple<CCharPointerHolder, CCharPointerHolder, Double>> edgelist = new ArrayList<>();

		Graph6Sparse6EventDrivenImporter importer = new Graph6Sparse6EventDrivenImporter();

		setupImporterWithPairEdgeList(importer, edgelist);

		importer.importInput(new File(StringUtils.toJavaStringFromUtf8(filename)));

		if (res.isNonNull()) {
			res.write(globalHandles.create(edgelist));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "import_edgelist_noattrs_string_graph6sparse6", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int importEdgelistWithNoAttrsFromGraph6Sparse6String(IsolateThread thread, CCharPointer input,
			WordPointer res) {
		List<Triple<CCharPointerHolder, CCharPointerHolder, Double>> edgelist = new ArrayList<>();

		Graph6Sparse6EventDrivenImporter importer = new Graph6Sparse6EventDrivenImporter();

		setupImporterWithPairEdgeList(importer, edgelist);

		String inputAsJava = StringUtils.toJavaStringFromUtf8(input);
		try (StringReader reader = new StringReader(inputAsJava)) {
			importer.importInput(reader);
		}

		if (res.isNonNull()) {
			res.write(globalHandles.create(edgelist));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "import_edgelist_attrs_file_graph6sparse6", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int importEdgelistWithAttrsFromGraph6Sparse6File(IsolateThread thread, CCharPointer filename,
			StringIdNotifyAttributeFunctionPointer vertexAttributeFunction,
			IntegerIdNotifyAttributeFunctionPointer edgeAttributeFunction, WordPointer res) {
		List<Triple<CCharPointerHolder, CCharPointerHolder, Double>> edgelist = new ArrayList<>();

		Graph6Sparse6EventDrivenImporter importer = new Graph6Sparse6EventDrivenImporter();

		setupImporterWithPairEdgeListWithIds(importer, edgelist, vertexAttributeFunction, edgeAttributeFunction, null);

		importer.importInput(new File(StringUtils.toJavaStringFromUtf8(filename)));
		if (res.isNonNull()) {
			res.write(globalHandles.create(edgelist));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "import_edgelist_attrs_string_graph6sparse6", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int importEdgelistWithAttrsFromGraph6Sparse6String(IsolateThread thread, CCharPointer input,
			StringIdNotifyAttributeFunctionPointer vertexAttributeFunction,
			IntegerIdNotifyAttributeFunctionPointer edgeAttributeFunction, WordPointer res) {
		List<Triple<CCharPointerHolder, CCharPointerHolder, Double>> edgelist = new ArrayList<>();

		Graph6Sparse6EventDrivenImporter importer = new Graph6Sparse6EventDrivenImporter();

		setupImporterWithPairEdgeListWithIds(importer, edgelist, vertexAttributeFunction, edgeAttributeFunction, null);

		String inputAsJava = StringUtils.toJavaStringFromUtf8(input);
		try (StringReader reader = new StringReader(inputAsJava)) {
			importer.importInput(reader);
		}
		if (res.isNonNull()) {
			res.write(globalHandles.create(edgelist));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	// ---------------------- utils -----------------------------------

	private static <V> void setupImporterWithEdgeList(BaseEventDrivenImporter<V, Triple<V, V, Double>> importer,
			List<Triple<CCharPointerHolder, CCharPointerHolder, Double>> edgelist) {
		importer.addEdgeConsumer(e -> {
			CCharPointerHolder sourceId = StringUtils.toCStringInUtf8(String.valueOf(e.getFirst()));
			CCharPointerHolder targetId = StringUtils.toCStringInUtf8(String.valueOf(e.getSecond()));
			Double weight = e.getThird();
			if (weight == null) {
				weight = Graph.DEFAULT_EDGE_WEIGHT;
			}
			edgelist.add(Triple.of(sourceId, targetId, weight));
		});
	}

	private static <V> void setupImporterWithPairEdgeList(BaseEventDrivenImporter<V, Pair<V, V>> importer,
			List<Triple<CCharPointerHolder, CCharPointerHolder, Double>> edgelist) {
		importer.addEdgeConsumer(e -> {
			CCharPointerHolder sourceId = StringUtils.toCStringInUtf8(String.valueOf(e.getFirst()));
			CCharPointerHolder targetId = StringUtils.toCStringInUtf8(String.valueOf(e.getSecond()));
			edgelist.add(Triple.of(sourceId, targetId, Graph.DEFAULT_EDGE_WEIGHT));
		});
	}

	private static <V> void setupImporterWithEdgeListWithIds(BaseEventDrivenImporter<V, Triple<V, V, Double>> importer,
			List<Triple<CCharPointerHolder, CCharPointerHolder, Double>> edgelist,
			StringIdNotifyAttributeFunctionPointer vertexAttributeFunction,
			IntegerIdNotifyAttributeFunctionPointer edgeAttributeFunction, CharSequenceTranslator unescapeTranslator) {
		int[] count = new int[1];

		Map<Triple<V, V, Double>, Integer> edgelistWithIds = new IdentityHashMap<>();

		importer.addEdgeConsumer(e -> {
			CCharPointerHolder sourceId = StringUtils.toCStringInUtf8(String.valueOf(e.getFirst()));
			CCharPointerHolder targetId = StringUtils.toCStringInUtf8(String.valueOf(e.getSecond()));
			Double weight = e.getThird();
			if (weight == null) {
				weight = Graph.DEFAULT_EDGE_WEIGHT;
			}
			int id = count[0]++;
			edgelist.add(Triple.of(sourceId, targetId, weight));
			edgelistWithIds.put(e, id);
		});

		if (vertexAttributeFunction.isNonNull()) {
			importer.addVertexAttributeConsumer((p, attr) -> {
				CCharPointerHolder vertexHolder = StringUtils.toCStringInUtf8(String.valueOf(p.getFirst()));
				CCharPointerHolder keyHolder = StringUtils.toCStringInUtf8(p.getSecond());
				String value = attr.getValue();
				if (unescapeTranslator != null) {
					value = unescapeTranslator.translate(value);
				}
				CCharPointerHolder valueHolder = StringUtils.toCStringInUtf8(value);
				vertexAttributeFunction.invoke(vertexHolder.get(), keyHolder.get(), valueHolder.get());
			});
		}

		if (edgeAttributeFunction.isNonNull()) {
			importer.addEdgeAttributeConsumer((p, attr) -> {
				Triple<V, V, Double> edgeTriple = p.getFirst();

				// lookup edge id (just the order that it was added in the list)
				int edgeIndex = edgelistWithIds.get(edgeTriple);

				CCharPointerHolder keyHolder = StringUtils.toCStringInUtf8(p.getSecond());
				String value = attr.getValue();
				if (unescapeTranslator != null) {
					value = unescapeTranslator.translate(value);
				}
				CCharPointerHolder valueHolder = StringUtils.toCStringInUtf8(value);
				edgeAttributeFunction.invoke(edgeIndex, keyHolder.get(), valueHolder.get());
			});
		}
	}

	private static <V> void setupImporterWithPairEdgeListWithIds(BaseEventDrivenImporter<V, Pair<V, V>> importer,
			List<Triple<CCharPointerHolder, CCharPointerHolder, Double>> edgelist,
			StringIdNotifyAttributeFunctionPointer vertexAttributeFunction,
			IntegerIdNotifyAttributeFunctionPointer edgeAttributeFunction, CharSequenceTranslator unescapeTranslator) {
		int[] count = new int[1];

		Map<Pair<V, V>, Integer> edgelistWithIds = new IdentityHashMap<>();

		importer.addEdgeConsumer(e -> {
			CCharPointerHolder sourceId = StringUtils.toCStringInUtf8(String.valueOf(e.getFirst()));
			CCharPointerHolder targetId = StringUtils.toCStringInUtf8(String.valueOf(e.getSecond()));
			int id = count[0]++;
			edgelist.add(Triple.of(sourceId, targetId, Graph.DEFAULT_EDGE_WEIGHT));
			edgelistWithIds.put(e, id);
		});

		if (vertexAttributeFunction.isNonNull()) {
			importer.addVertexAttributeConsumer((p, attr) -> {
				CCharPointerHolder vertexHolder = StringUtils.toCStringInUtf8(String.valueOf(p.getFirst()));
				CCharPointerHolder keyHolder = StringUtils.toCStringInUtf8(p.getSecond());
				String value = attr.getValue();
				if (unescapeTranslator != null) {
					value = unescapeTranslator.translate(value);
				}
				CCharPointerHolder valueHolder = StringUtils.toCStringInUtf8(value);
				vertexAttributeFunction.invoke(vertexHolder.get(), keyHolder.get(), valueHolder.get());
			});
		}

		if (edgeAttributeFunction.isNonNull()) {
			importer.addEdgeAttributeConsumer((p, attr) -> {
				Pair<V, V> edgeTriple = p.getFirst();

				// lookup edge id (just the order that it was added in the list)
				int edgeIndex = edgelistWithIds.get(edgeTriple);

				CCharPointerHolder keyHolder = StringUtils.toCStringInUtf8(p.getSecond());
				String value = attr.getValue();
				if (unescapeTranslator != null) {
					value = unescapeTranslator.translate(value);
				}
				CCharPointerHolder valueHolder = StringUtils.toCStringInUtf8(value);
				edgeAttributeFunction.invoke(edgeIndex, keyHolder.get(), valueHolder.get());
			});
		}
	}

}
