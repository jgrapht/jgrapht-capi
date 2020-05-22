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
import java.util.LinkedHashMap;
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
import org.jgrapht.alg.util.Triple;
import org.jgrapht.capi.Constants;
import org.jgrapht.capi.JGraphTContext.ImportIdFunctionPointer;
import org.jgrapht.capi.JGraphTContext.NotifyAttributeFunctionPointer;
import org.jgrapht.capi.JGraphTContext.Status;
import org.jgrapht.capi.StringUtils;
import org.jgrapht.capi.error.StatusReturnExceptionHandler;
import org.jgrapht.nio.BaseEventDrivenImporter;
import org.jgrapht.nio.gexf.SimpleGEXFEventDrivenImporter;
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

	// ------------------------- JSON ---------------------------------
	
	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "import_edgelist_noattrs_file_json", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int importEdgelistWithNoAttrsFromJsonFile(IsolateThread thread, CCharPointer filename,
			ImportIdFunctionPointer importIdFunctionPointer, WordPointer res) {
		List<Triple<Integer, Integer, Double>> edgelist = new ArrayList<>();
		JSONEventDrivenImporter importer = new JSONEventDrivenImporter();

		setupImporterWithEdgeList(importer, edgelist, importIdFunctionPointer);

		importer.importInput(new File(StringUtils.toJavaStringFromUtf8(filename)));

		if (res.isNonNull()) {
			res.write(globalHandles.create(edgelist));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "import_edgelist_noattrs_string_json", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int importEdgelistWithNoAttrsFromJsonString(IsolateThread thread, CCharPointer input,
			ImportIdFunctionPointer importIdFunctionPointer, WordPointer res) {
		List<Triple<Integer, Integer, Double>> edgelist = new ArrayList<>();
		JSONEventDrivenImporter importer = new JSONEventDrivenImporter();
		setupImporterWithEdgeList(importer, edgelist, importIdFunctionPointer);

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
			ImportIdFunctionPointer importIdFunctionPointer, NotifyAttributeFunctionPointer vertexAttributeFunction,
			NotifyAttributeFunctionPointer edgeAttributeFunction, WordPointer res) {
		Map<Triple<Integer, Integer, Double>, Integer> edgelistWithIds = new LinkedHashMap<>();
		JSONEventDrivenImporter importer = new JSONEventDrivenImporter();

		setupImporterWithEdgeListWithIds(importer, edgelistWithIds, importIdFunctionPointer, vertexAttributeFunction,
				edgeAttributeFunction, StringEscapeUtils.UNESCAPE_JSON);

		importer.importInput(new File(StringUtils.toJavaStringFromUtf8(filename)));
		if (res.isNonNull()) {
			res.write(globalHandles.create(edgelistWithIds.keySet()));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "import_edgelist_attrs_string_json", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int importEdgelistWithAttrsFromJsonString(IsolateThread thread, CCharPointer input,
			ImportIdFunctionPointer importIdFunctionPointer, NotifyAttributeFunctionPointer vertexAttributeFunction,
			NotifyAttributeFunctionPointer edgeAttributeFunction, WordPointer res) {
		Map<Triple<Integer, Integer, Double>, Integer> edgelistWithIds = new LinkedHashMap<>();
		JSONEventDrivenImporter importer = new JSONEventDrivenImporter();
		setupImporterWithEdgeListWithIds(importer, edgelistWithIds, importIdFunctionPointer, vertexAttributeFunction,
				edgeAttributeFunction, StringEscapeUtils.UNESCAPE_JSON);

		String inputAsJava = StringUtils.toJavaStringFromUtf8(input);
		try (StringReader reader = new StringReader(inputAsJava)) {
			importer.importInput(reader);
		}
		if (res.isNonNull()) {
			res.write(globalHandles.create(edgelistWithIds.keySet()));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	// ------------------------- GEFX ---------------------------------

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "import_edgelist_noattrs_file_gexf", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int importEdgelistWithNoAttrsFromGexfFile(IsolateThread thread, CCharPointer filename,
			ImportIdFunctionPointer importIdFunctionPointer, boolean validate_schema, WordPointer res) {
		List<Triple<Integer, Integer, Double>> edgelist = new ArrayList<>();
		SimpleGEXFEventDrivenImporter importer = new SimpleGEXFEventDrivenImporter();
		importer.setSchemaValidation(validate_schema);

		setupImporterWithEdgeList(importer, edgelist, importIdFunctionPointer);

		importer.importInput(new File(StringUtils.toJavaStringFromUtf8(filename)));

		if (res.isNonNull()) {
			res.write(globalHandles.create(edgelist));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}
	
	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "import_edgelist_noattrs_string_gexf", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int importEdgelistWithNoAttrsFromGexfString(IsolateThread thread, CCharPointer input,
			ImportIdFunctionPointer importIdFunctionPointer, boolean validate_schema, WordPointer res) {
		List<Triple<Integer, Integer, Double>> edgelist = new ArrayList<>();
		SimpleGEXFEventDrivenImporter importer = new SimpleGEXFEventDrivenImporter();
		importer.setSchemaValidation(validate_schema);
		
		setupImporterWithEdgeList(importer, edgelist, importIdFunctionPointer);

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
			ImportIdFunctionPointer importIdFunctionPointer, boolean validate_schema, NotifyAttributeFunctionPointer vertexAttributeFunction,
			NotifyAttributeFunctionPointer edgeAttributeFunction, WordPointer res) {
		Map<Triple<Integer, Integer, Double>, Integer> edgelistWithIds = new LinkedHashMap<>();
		SimpleGEXFEventDrivenImporter importer = new SimpleGEXFEventDrivenImporter();
		importer.setSchemaValidation(validate_schema);

		setupImporterWithEdgeListWithIds(importer, edgelistWithIds, importIdFunctionPointer, vertexAttributeFunction,
				edgeAttributeFunction, StringEscapeUtils.UNESCAPE_JSON);

		importer.importInput(new File(StringUtils.toJavaStringFromUtf8(filename)));
		if (res.isNonNull()) {
			res.write(globalHandles.create(edgelistWithIds.keySet()));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "import_edgelist_attrs_string_gexf", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int importEdgelistWithAttrsFromGexfString(IsolateThread thread, CCharPointer input,
			ImportIdFunctionPointer importIdFunctionPointer, boolean validate_schema, NotifyAttributeFunctionPointer vertexAttributeFunction,
			NotifyAttributeFunctionPointer edgeAttributeFunction, WordPointer res) {
		Map<Triple<Integer, Integer, Double>, Integer> edgelistWithIds = new LinkedHashMap<>();
		SimpleGEXFEventDrivenImporter importer = new SimpleGEXFEventDrivenImporter();
		importer.setSchemaValidation(validate_schema);
		
		setupImporterWithEdgeListWithIds(importer, edgelistWithIds, importIdFunctionPointer, vertexAttributeFunction,
				edgeAttributeFunction, StringEscapeUtils.UNESCAPE_JSON);

		String inputAsJava = StringUtils.toJavaStringFromUtf8(input);
		try (StringReader reader = new StringReader(inputAsJava)) {
			importer.importInput(reader);
		}
		if (res.isNonNull()) {
			res.write(globalHandles.create(edgelistWithIds.keySet()));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}
	
	// ---------------------- utils -----------------------------------
	
	private static int vertexToInteger(ImportIdFunctionPointer importIdFunctionPointer, String vertex) {
		CCharPointerHolder sourceHolder = StringUtils.toCStringInUtf8(vertex);
		return importIdFunctionPointer.invoke(sourceHolder.get());
	}

	private static void setupImporterWithEdgeList(
			BaseEventDrivenImporter<String, Triple<String, String, Double>> importer,
			List<Triple<Integer, Integer, Double>> edgelist, ImportIdFunctionPointer importIdFunctionPointer) {
		if (importIdFunctionPointer.isNull()) {
			throw new IllegalArgumentException("Need function to convert strings vertex ids to integers");
		}
		importer.addEdgeConsumer(e -> {
			int sourceId = vertexToInteger(importIdFunctionPointer, e.getFirst());
			int targetId = vertexToInteger(importIdFunctionPointer, e.getSecond());
			Double weight = e.getThird();
			edgelist.add(Triple.of(sourceId, targetId, weight));
		});
	}

	private static void setupImporterWithEdgeListWithIds(
			BaseEventDrivenImporter<String, Triple<String, String, Double>> importer,
			Map<Triple<Integer, Integer, Double>, Integer> edgelistWithIds,
			ImportIdFunctionPointer importIdFunctionPointer, NotifyAttributeFunctionPointer vertexAttributeFunction,
			NotifyAttributeFunctionPointer edgeAttributeFunction, CharSequenceTranslator unescapeTranslator) {
		if (importIdFunctionPointer.isNull()) {
			throw new IllegalArgumentException("Need function to convert strings vertex ids to integers");
		}
		int[] count = new int[1];

		importer.addEdgeConsumer(e -> {
			int sourceId = vertexToInteger(importIdFunctionPointer, e.getFirst());
			int targetId = vertexToInteger(importIdFunctionPointer, e.getSecond());
			Double weight = e.getThird();
			int id = count[0]++;
			edgelistWithIds.put(Triple.of(sourceId, targetId, weight), id);
		});

		if (vertexAttributeFunction.isNonNull()) {
			importer.addVertexAttributeConsumer((p, attr) -> {
				int vertex = vertexToInteger(importIdFunctionPointer, p.getFirst());
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
				Triple<String, String, Double> edgeTriple = p.getFirst();

				// lookup edge id (just the order that it was added in the list)
				int edge = edgelistWithIds.get(edgeTriple);

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

}
