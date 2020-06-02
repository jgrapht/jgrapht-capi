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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.graalvm.nativeimage.IsolateThread;
import org.graalvm.nativeimage.ObjectHandle;
import org.graalvm.nativeimage.ObjectHandles;
import org.graalvm.nativeimage.c.function.CEntryPoint;
import org.graalvm.nativeimage.c.type.CCharPointer;
import org.graalvm.nativeimage.c.type.CTypeConversion.CCharPointerHolder;
import org.graalvm.nativeimage.c.type.WordPointer;
import org.jgrapht.Graph;
import org.jgrapht.capi.Constants;
import org.jgrapht.capi.JGraphTContext.ExporterDIMACSFormat;
import org.jgrapht.capi.JGraphTContext.ImporterExporterCSVFormat;
import org.jgrapht.capi.JGraphTContext.Status;
import org.jgrapht.capi.StringUtils;
import org.jgrapht.capi.attributes.AttributesStore;
import org.jgrapht.capi.attributes.RegisteredAttribute;
import org.jgrapht.capi.error.StatusReturnExceptionHandler;
import org.jgrapht.nio.AttributeType;
import org.jgrapht.nio.BaseExporter;
import org.jgrapht.nio.ExportException;
import org.jgrapht.nio.GraphExporter;
import org.jgrapht.nio.csv.CSVExporter;
import org.jgrapht.nio.csv.CSVFormat;
import org.jgrapht.nio.dimacs.DIMACSExporter;
import org.jgrapht.nio.dimacs.DIMACSFormat;
import org.jgrapht.nio.dot.DOTExporter;
import org.jgrapht.nio.gexf.GEXFAttributeType;
import org.jgrapht.nio.gexf.GEXFExporter;
import org.jgrapht.nio.gexf.GEXFExporter.AttributeCategory;
import org.jgrapht.nio.gml.GmlExporter;
import org.jgrapht.nio.graph6.Graph6Sparse6Exporter;
import org.jgrapht.nio.graphml.GraphMLExporter;
import org.jgrapht.nio.json.JSONExporter;
import org.jgrapht.nio.lemon.LemonExporter;

public class ExporterApi {

	private static ObjectHandles globalHandles = ObjectHandles.getGlobal();

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "export_file_dimacs", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int exportDIMACSToFile(IsolateThread thread, ObjectHandle graphHandle, CCharPointer filename,
			ExporterDIMACSFormat format, boolean exportEdgeWeights, ObjectHandle vertexIdStore) {
		Graph<Integer, Integer> g = globalHandles.get(graphHandle);

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
		DIMACSExporter<Integer, Integer> exporter = new DIMACSExporter<>(createIdProviderDimacs(vertexIdStore),
				actualFormat);
		exporter.setParameter(DIMACSExporter.Parameter.EXPORT_EDGE_WEIGHTS, exportEdgeWeights);
		exportToFile(g, exporter, filename);
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "export_string_dimacs", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int exportDIMACSToString(IsolateThread thread, ObjectHandle graphHandle, ExporterDIMACSFormat format,
			boolean exportEdgeWeights, ObjectHandle vertexIdStore, WordPointer res) {
		Graph<Integer, Integer> g = globalHandles.get(graphHandle);

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

		DIMACSExporter<Integer, Integer> exporter = new DIMACSExporter<>(createIdProviderDimacs(vertexIdStore),
				actualFormat);
		exporter.setParameter(DIMACSExporter.Parameter.EXPORT_EDGE_WEIGHTS, exportEdgeWeights);

		CCharPointerHolder cString = exportToCString(g, exporter);
		if (res.isNonNull()) {
			res.write(globalHandles.create(cString));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "export_file_gml", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int exportGmlFile(IsolateThread thread, ObjectHandle graphHandle, CCharPointer filename,
			boolean exportEdgeWeights, boolean exportVertexLabels, boolean exportEdgeLabels,
			ObjectHandle vertexLabelsStore, ObjectHandle edgeLabelsStore, ObjectHandle vertexIdStore) {
		Graph<Integer, Integer> g = globalHandles.get(graphHandle);

		GmlExporter<Integer, Integer> exporter = new GmlExporter<>(createIdProvider(vertexIdStore));
		
		exporter.setParameter(GmlExporter.Parameter.EXPORT_EDGE_WEIGHTS, exportEdgeWeights);
		exporter.setParameter(GmlExporter.Parameter.EXPORT_CUSTOM_VERTEX_ATTRIBUTES, true);
		exporter.setParameter(GmlExporter.Parameter.EXPORT_CUSTOM_EDGE_ATTRIBUTES, true);
		exporter.setParameter(GmlExporter.Parameter.EXPORT_VERTEX_LABELS, exportVertexLabels);
		exporter.setParameter(GmlExporter.Parameter.EXPORT_EDGE_LABELS, exportEdgeLabels);

		setupAttributeStores(exporter, vertexLabelsStore, edgeLabelsStore);

		exportToFile(g, exporter, filename);
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "export_string_gml", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int exportGmlString(IsolateThread thread, ObjectHandle graphHandle, boolean exportEdgeWeights,
			boolean exportVertexLabels, boolean exportEdgeLabels, ObjectHandle vertexLabelsStore,
			ObjectHandle edgeLabelsStore, ObjectHandle vertexIdStore, WordPointer res) {
		Graph<Integer, Integer> g = globalHandles.get(graphHandle);

		GmlExporter<Integer, Integer> exporter = new GmlExporter<>(createIdProvider(vertexIdStore));
		
		exporter.setParameter(GmlExporter.Parameter.EXPORT_EDGE_WEIGHTS, exportEdgeWeights);
		exporter.setParameter(GmlExporter.Parameter.EXPORT_CUSTOM_VERTEX_ATTRIBUTES, true);
		exporter.setParameter(GmlExporter.Parameter.EXPORT_CUSTOM_EDGE_ATTRIBUTES, true);
		exporter.setParameter(GmlExporter.Parameter.EXPORT_VERTEX_LABELS, exportVertexLabels);
		exporter.setParameter(GmlExporter.Parameter.EXPORT_EDGE_LABELS, exportEdgeLabels);

		setupAttributeStores(exporter, vertexLabelsStore, edgeLabelsStore);

		CCharPointerHolder cString = exportToCString(g, exporter);
		if (res.isNonNull()) {
			res.write(globalHandles.create(cString));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "export_file_json", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int exportJsonFile(IsolateThread thread, ObjectHandle graphHandle, CCharPointer filename,
			ObjectHandle vertexLabelsStore, ObjectHandle edgeLabelsStore, ObjectHandle vertexIdStore) {
		Graph<Integer, Integer> g = globalHandles.get(graphHandle);

		JSONExporter<Integer, Integer> exporter = new JSONExporter<>(createIdProvider(vertexIdStore));

		setupAttributeStores(exporter, vertexLabelsStore, edgeLabelsStore);

		exportToFile(g, exporter, filename);
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "export_string_json", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int exportJsonString(IsolateThread thread, ObjectHandle graphHandle, ObjectHandle vertexLabelsStore,
			ObjectHandle edgeLabelsStore, ObjectHandle vertexIdStore, WordPointer res) {
		Graph<Integer, Integer> g = globalHandles.get(graphHandle);

		JSONExporter<Integer, Integer> exporter = new JSONExporter<>(createIdProvider(vertexIdStore));

		setupAttributeStores(exporter, vertexLabelsStore, edgeLabelsStore);

		CCharPointerHolder cString = exportToCString(g, exporter);
		if (res.isNonNull()) {
			res.write(globalHandles.create(cString));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "export_file_lemon", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int exportLemonToFile(IsolateThread thread, ObjectHandle graphHandle, CCharPointer filename,
			boolean exportEdgeWeights, boolean escapeStringsAsJava, ObjectHandle vertexIdStore) {
		Graph<Integer, Integer> g = globalHandles.get(graphHandle);

		LemonExporter<Integer, Integer> exporter = new LemonExporter<>(createIdProvider(vertexIdStore));
		exporter.setParameter(LemonExporter.Parameter.EXPORT_EDGE_WEIGHTS, exportEdgeWeights);
		exporter.setParameter(LemonExporter.Parameter.ESCAPE_STRINGS_AS_JAVA, escapeStringsAsJava);

		exportToFile(g, exporter, filename);
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "export_string_lemon", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int exportLemonToString(IsolateThread thread, ObjectHandle graphHandle, boolean exportEdgeWeights,
			boolean escapeStringsAsJava, ObjectHandle vertexIdStore, WordPointer res) {
		Graph<Integer, Integer> g = globalHandles.get(graphHandle);

		LemonExporter<Integer, Integer> exporter = new LemonExporter<>(createIdProvider(vertexIdStore));
		exporter.setParameter(LemonExporter.Parameter.EXPORT_EDGE_WEIGHTS, exportEdgeWeights);
		exporter.setParameter(LemonExporter.Parameter.ESCAPE_STRINGS_AS_JAVA, escapeStringsAsJava);

		CCharPointerHolder cString = exportToCString(g, exporter);
		if (res.isNonNull()) {
			res.write(globalHandles.create(cString));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "export_file_csv", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int exportCSVToFile(IsolateThread thread, ObjectHandle graphHandle, CCharPointer filename,
			ImporterExporterCSVFormat format, boolean exportEdgeWeights, boolean matrix_format_nodeid,
			boolean matrix_format_zero_when_no_edge, ObjectHandle vertexIdStore) {
		Graph<Integer, Integer> g = globalHandles.get(graphHandle);

		CSVFormat actualFormat = null;
		switch (format) {
		case CSV_FORMAT_MATRIX:
			actualFormat = CSVFormat.MATRIX;
			break;
		default:
			actualFormat = CSVFormat.ADJACENCY_LIST;
			break;
		}

		CSVExporter<Integer, Integer> exporter = new CSVExporter<>(createIdProvider(vertexIdStore), actualFormat, ',');
		exporter.setParameter(CSVFormat.Parameter.EDGE_WEIGHTS, exportEdgeWeights);
		exporter.setParameter(CSVFormat.Parameter.MATRIX_FORMAT_NODEID, matrix_format_nodeid);
		exporter.setParameter(CSVFormat.Parameter.MATRIX_FORMAT_ZERO_WHEN_NO_EDGE, matrix_format_zero_when_no_edge);

		exportToFile(g, exporter, filename);
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "export_string_csv", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int exportCSVToString(IsolateThread thread, ObjectHandle graphHandle,
			ImporterExporterCSVFormat format, boolean exportEdgeWeights, boolean matrix_format_nodeid,
			boolean matrix_format_zero_when_no_edge, ObjectHandle vertexIdStore, WordPointer res) {
		Graph<Integer, Integer> g = globalHandles.get(graphHandle);

		CSVFormat actualFormat = null;
		switch (format) {
		case CSV_FORMAT_MATRIX:
			actualFormat = CSVFormat.MATRIX;
			break;
		default:
			actualFormat = CSVFormat.ADJACENCY_LIST;
			break;
		}

		CSVExporter<Integer, Integer> exporter = new CSVExporter<>(createIdProvider(vertexIdStore), actualFormat, ',');
		exporter.setParameter(CSVFormat.Parameter.EDGE_WEIGHTS, exportEdgeWeights);
		exporter.setParameter(CSVFormat.Parameter.MATRIX_FORMAT_NODEID, matrix_format_nodeid);
		exporter.setParameter(CSVFormat.Parameter.MATRIX_FORMAT_ZERO_WHEN_NO_EDGE, matrix_format_zero_when_no_edge);

		CCharPointerHolder cString = exportToCString(g, exporter);
		if (res.isNonNull()) {
			res.write(globalHandles.create(cString));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	/**
	 * Export a GEXF file
	 * 
	 * @param thread                the thread
	 * @param graphHandle           handle for the graph
	 * @param filename              filename to export to
	 * @param attributesRegistry    handle for reading which attributes to export
	 * @param vertexAttributesStore handle for acquiring the actual vertex attribute
	 *                              values
	 * @param edgeAttributesStore   handle for acquiring the actual edge attribute
	 *                              values
	 * @param exportEdgeWeights     whether to export edge weights
	 * @param exportEdgeLabels      whether to export edge labels
	 * @param exportEdgeTypes       whether to export edge types
	 * @param exportMeta            whether to export meta information
	 * @return status code
	 */
	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "export_file_gexf", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int exportGexfFile(IsolateThread thread, ObjectHandle graphHandle, CCharPointer filename,
			ObjectHandle attributesRegistry, ObjectHandle vertexAttributesStore, ObjectHandle edgeAttributesStore,
			ObjectHandle vertexIdStore, ObjectHandle edgeIdStore, boolean exportEdgeWeights, boolean exportEdgeLabels,
			boolean exportEdgeTypes, boolean exportMeta) {
		Graph<Integer, Integer> g = globalHandles.get(graphHandle);

		GEXFExporter<Integer, Integer> exporter = new GEXFExporter<>(createIdProvider(vertexIdStore),
				createIdProvider(edgeIdStore));

		exporter.setParameter(GEXFExporter.Parameter.EXPORT_EDGE_WEIGHTS, exportEdgeWeights);
		exporter.setParameter(GEXFExporter.Parameter.EXPORT_EDGE_LABELS, exportEdgeLabels);
		exporter.setParameter(GEXFExporter.Parameter.EXPORT_EDGE_TYPES, exportEdgeTypes);
		exporter.setParameter(GEXFExporter.Parameter.EXPORT_META, exportMeta);

		setupAttributeStores(exporter, vertexAttributesStore, edgeAttributesStore);

		List<RegisteredAttribute> aRegistry = globalHandles.get(attributesRegistry);
		if (aRegistry != null) {
			for (RegisteredAttribute ra : aRegistry) {
				AttributeCategory aCategory = AttributeCategory.valueOf(ra.getCategory().toUpperCase());
				GEXFAttributeType aType = ra.getType() == null ? GEXFAttributeType.STRING
						: GEXFAttributeType.valueOf(ra.getType().toUpperCase());
				exporter.registerAttribute(ra.getName(), aCategory, aType, ra.getDefaultValue());
			}
		}

		exportToFile(g, exporter, filename);
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "export_string_gexf", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int exportGexfString(IsolateThread thread, ObjectHandle graphHandle, ObjectHandle attributesRegistry,
			ObjectHandle vertexAttributesStore, ObjectHandle edgeAttributesStore, ObjectHandle vertexIdStore,
			ObjectHandle edgeIdStore, boolean exportEdgeWeights, boolean exportEdgeLabels, boolean exportEdgeTypes,
			boolean exportMeta, WordPointer res) {
		Graph<Integer, Integer> g = globalHandles.get(graphHandle);

		GEXFExporter<Integer, Integer> exporter = new GEXFExporter<>(createIdProvider(vertexIdStore),
				createIdProvider(edgeIdStore));

		exporter.setParameter(GEXFExporter.Parameter.EXPORT_EDGE_WEIGHTS, exportEdgeWeights);
		exporter.setParameter(GEXFExporter.Parameter.EXPORT_EDGE_LABELS, exportEdgeLabels);
		exporter.setParameter(GEXFExporter.Parameter.EXPORT_EDGE_TYPES, exportEdgeTypes);
		exporter.setParameter(GEXFExporter.Parameter.EXPORT_META, exportMeta);

		setupAttributeStores(exporter, vertexAttributesStore, edgeAttributesStore);

		List<RegisteredAttribute> aRegistry = globalHandles.get(attributesRegistry);
		if (aRegistry != null) {
			for (RegisteredAttribute ra : aRegistry) {
				AttributeCategory aCategory = AttributeCategory.valueOf(ra.getCategory().toUpperCase());
				GEXFAttributeType aType = ra.getType() == null ? GEXFAttributeType.STRING
						: GEXFAttributeType.valueOf(ra.getType().toUpperCase());
				exporter.registerAttribute(ra.getName(), aCategory, aType, ra.getDefaultValue());
			}
		}

		CCharPointerHolder cString = exportToCString(g, exporter);
		if (res.isNonNull()) {
			res.write(globalHandles.create(cString));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "export_file_dot", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int exportDotFile(IsolateThread thread, ObjectHandle graphHandle, CCharPointer filename,
			ObjectHandle vertexAttributesStore, ObjectHandle edgeAttributesStore, ObjectHandle vertexIdStore) {
		Graph<Integer, Integer> g = globalHandles.get(graphHandle);

		DOTExporter<Integer, Integer> exporter = new DOTExporter<>(createIdProvider(vertexIdStore));

		setupAttributeStores(exporter, vertexAttributesStore, edgeAttributesStore);

		exportToFile(g, exporter, filename);
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "export_string_dot", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int exportDotString(IsolateThread thread, ObjectHandle graphHandle,
			ObjectHandle vertexAttributesStore, ObjectHandle edgeAttributesStore, ObjectHandle vertexIdStore,
			WordPointer res) {
		Graph<Integer, Integer> g = globalHandles.get(graphHandle);

		DOTExporter<Integer, Integer> exporter = new DOTExporter<>(createIdProvider(vertexIdStore));

		setupAttributeStores(exporter, vertexAttributesStore, edgeAttributesStore);

		CCharPointerHolder cString = exportToCString(g, exporter);
		if (res.isNonNull()) {
			res.write(globalHandles.create(cString));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "export_file_graph6", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int exportGraph6File(IsolateThread thread, ObjectHandle graphHandle, CCharPointer filename) {
		Graph<Integer, Integer> g = globalHandles.get(graphHandle);

		Graph6Sparse6Exporter<Integer, Integer> exporter = new Graph6Sparse6Exporter<>(
				Graph6Sparse6Exporter.Format.GRAPH6);

		exportToFile(g, exporter, filename);
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "export_string_graph6", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int exportGraph6String(IsolateThread thread, ObjectHandle graphHandle, WordPointer res) {
		Graph<Integer, Integer> g = globalHandles.get(graphHandle);

		Graph6Sparse6Exporter<Integer, Integer> exporter = new Graph6Sparse6Exporter<>(
				Graph6Sparse6Exporter.Format.GRAPH6);

		CCharPointerHolder cString = exportToCString(g, exporter);
		if (res.isNonNull()) {
			res.write(globalHandles.create(cString));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "export_file_sparse6", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int exportSparse6File(IsolateThread thread, ObjectHandle graphHandle, CCharPointer filename) {
		Graph<Integer, Integer> g = globalHandles.get(graphHandle);

		Graph6Sparse6Exporter<Integer, Integer> exporter = new Graph6Sparse6Exporter<>(
				Graph6Sparse6Exporter.Format.SPARSE6);

		exportToFile(g, exporter, filename);
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "export_string_sparse6", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int exportSparse6String(IsolateThread thread, ObjectHandle graphHandle, WordPointer res) {
		Graph<Integer, Integer> g = globalHandles.get(graphHandle);

		Graph6Sparse6Exporter<Integer, Integer> exporter = new Graph6Sparse6Exporter<>(
				Graph6Sparse6Exporter.Format.SPARSE6);

		CCharPointerHolder cString = exportToCString(g, exporter);
		if (res.isNonNull()) {
			res.write(globalHandles.create(cString));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "export_file_graphml", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int exportGraphMLFile(IsolateThread thread, ObjectHandle graphHandle, CCharPointer filename,
			ObjectHandle attributesRegistry, ObjectHandle vertexAttributesStore, ObjectHandle edgeAttributesStore,
			ObjectHandle vertexIdStore, boolean exportEdgeWeights, boolean exportVertexLabels,
			boolean exportEdgeLabels) {
		Graph<Integer, Integer> g = globalHandles.get(graphHandle);

		GraphMLExporter<Integer, Integer> exporter = new GraphMLExporter<>(createIdProvider(vertexIdStore));

		exporter.setExportEdgeWeights(exportEdgeWeights);
		exporter.setExportVertexLabels(exportVertexLabels);
		exporter.setExportEdgeLabels(exportEdgeLabels);

		setupAttributeStores(exporter, vertexAttributesStore, edgeAttributesStore);

		List<RegisteredAttribute> aRegistry = globalHandles.get(attributesRegistry);
		if (aRegistry != null) {
			for (RegisteredAttribute ra : aRegistry) {
				GraphMLExporter.AttributeCategory aCategory = GraphMLExporter.AttributeCategory
						.valueOf(ra.getCategory().toUpperCase());
				AttributeType aType = ra.getType() == null ? null : AttributeType.valueOf(ra.getType().toUpperCase());
				exporter.registerAttribute(ra.getName(), aCategory, aType, ra.getDefaultValue());
			}
		}

		exportToFile(g, exporter, filename);
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "export_string_graphml", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int exportGraphMLString(IsolateThread thread, ObjectHandle graphHandle,
			ObjectHandle attributesRegistry, ObjectHandle vertexAttributesStore, ObjectHandle edgeAttributesStore,
			ObjectHandle vertexIdStore, boolean exportEdgeWeights, boolean exportVertexLabels, boolean exportEdgeLabels,
			WordPointer res) {
		Graph<Integer, Integer> g = globalHandles.get(graphHandle);

		GraphMLExporter<Integer, Integer> exporter = new GraphMLExporter<>(createIdProvider(vertexIdStore));

		exporter.setExportEdgeWeights(exportEdgeWeights);
		exporter.setExportVertexLabels(exportVertexLabels);
		exporter.setExportEdgeLabels(exportEdgeLabels);

		setupAttributeStores(exporter, vertexAttributesStore, edgeAttributesStore);

		List<RegisteredAttribute> aRegistry = globalHandles.get(attributesRegistry);
		if (aRegistry != null) {
			for (RegisteredAttribute ra : aRegistry) {
				GraphMLExporter.AttributeCategory aCategory = GraphMLExporter.AttributeCategory
						.valueOf(ra.getCategory().toUpperCase());
				AttributeType aType = ra.getType() == null ? null : AttributeType.valueOf(ra.getType().toUpperCase());
				exporter.registerAttribute(ra.getName(), aCategory, aType, ra.getDefaultValue());
			}
		}

		CCharPointerHolder cString = exportToCString(g, exporter);
		if (res.isNonNull()) {
			res.write(globalHandles.create(cString));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	private static Function<Integer, String> createIdProvider(ObjectHandle idStore) {
		Map<Integer, String> vIdStore = globalHandles.get(idStore);
		if (vIdStore != null) {
			return x -> {
				String id = vIdStore.get(x);
				if (id == null) {
					return String.valueOf(x);
				}
				return id;
			};
		}
		return x -> String.valueOf(x);
	}

	private static Function<Integer, String> createIdProviderDimacs(ObjectHandle idStore) {
		Map<Integer, String> vIdStore = globalHandles.get(idStore);
		if (vIdStore != null) {
			return x -> {
				String id = vIdStore.get(x);
				if (id == null) {
					return String.valueOf(x + 1);
				}
				return id;
			};
		}
		return x -> String.valueOf(x + 1);
	}

	private static void setupAttributeStores(BaseExporter<Integer, Integer> exporter,
			ObjectHandle vertexAttributesStore, ObjectHandle edgeAttributesStore) {
		AttributesStore vStore = globalHandles.get(vertexAttributesStore);
		if (vStore != null) {
			exporter.setVertexAttributeProvider(v -> {
				return vStore.getAttributes(v);
			});
		}

		AttributesStore eStore = globalHandles.get(edgeAttributesStore);
		if (eStore != null) {
			exporter.setEdgeAttributeProvider(e -> {
				return eStore.getAttributes(e);
			});
		}
	}

	private static void exportToFile(Graph<Integer, Integer> g, GraphExporter<Integer, Integer> exporter,
			CCharPointer filename) {
		File file = new File(StringUtils.toJavaStringFromUtf8(filename));
		try (FileOutputStream outStream = new FileOutputStream(file);
				Writer writer = new OutputStreamWriter(outStream, StandardCharsets.UTF_8)) {
			exporter.exportGraph(g, outStream);
		} catch (Exception e) {
			throw new ExportException(e);
		}
	}

	private static CCharPointerHolder exportToCString(Graph<Integer, Integer> g,
			GraphExporter<Integer, Integer> exporter) {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try (OutputStreamWriter writer = new OutputStreamWriter(os, StandardCharsets.UTF_8)) {
			exporter.exportGraph(g, writer);
		} catch (IOException e) {
			throw new ExportException(e);
		}
		return StringUtils.toCString(os.toByteArray());
	}

}
