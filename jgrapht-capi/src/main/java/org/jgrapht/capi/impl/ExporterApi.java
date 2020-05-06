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
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.graalvm.nativeimage.IsolateThread;
import org.graalvm.nativeimage.ObjectHandle;
import org.graalvm.nativeimage.ObjectHandles;
import org.graalvm.nativeimage.c.function.CEntryPoint;
import org.graalvm.nativeimage.c.type.CCharPointer;
import org.graalvm.nativeimage.c.type.CTypeConversion;
import org.graalvm.nativeimage.c.type.CTypeConversion.CCharPointerHolder;
import org.graalvm.nativeimage.c.type.WordPointer;
import org.jgrapht.Graph;
import org.jgrapht.capi.Constants;
import org.jgrapht.capi.JGraphTContext.ExporterDIMACSFormat;
import org.jgrapht.capi.JGraphTContext.ImporterExporterCSVFormat;
import org.jgrapht.capi.JGraphTContext.Status;
import org.jgrapht.capi.attributes.AttributesStore;
import org.jgrapht.capi.attributes.RegisteredAttribute;
import org.jgrapht.capi.error.StatusReturnExceptionHandler;
import org.jgrapht.nio.Attribute;
import org.jgrapht.nio.AttributeType;
import org.jgrapht.nio.BaseExporter;
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
			ExporterDIMACSFormat format, boolean exportEdgeWeights) {
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

		DIMACSExporter<Long, Long> exporter = new DIMACSExporter<>(x -> String.valueOf(x + 1), actualFormat);
		exporter.setParameter(DIMACSExporter.Parameter.EXPORT_EDGE_WEIGHTS, exportEdgeWeights);
		exporter.exportGraph(g, new File(CTypeConversion.toJavaString(filename)));
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "export_string_dimacs", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int exportDIMACSToString(IsolateThread thread, ObjectHandle graphHandle, ExporterDIMACSFormat format,
			boolean exportEdgeWeights, WordPointer res) {
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

		DIMACSExporter<Long, Long> exporter = new DIMACSExporter<>(x -> String.valueOf(x + 1), actualFormat);
		exporter.setParameter(DIMACSExporter.Parameter.EXPORT_EDGE_WEIGHTS, exportEdgeWeights);

		ByteArrayOutputStream os = new ByteArrayOutputStream();
		exporter.exportGraph(g, os);
		try {
			String outputAsAString = new String(os.toByteArray(), "UTF-8");
			CCharPointerHolder cString = CTypeConversion.toCString(outputAsAString);
			if (res.isNonNull()) {
				res.write(globalHandles.create(cString));
			}
		} catch (UnsupportedEncodingException e) {
			// should not happen
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "export_file_gml", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int exportGmlFile(IsolateThread thread, ObjectHandle graphHandle, CCharPointer filename,
			boolean exportEdgeWeights, ObjectHandle vertexLabelsStore, ObjectHandle edgeLabelsStore) {
		Graph<Long, Long> g = globalHandles.get(graphHandle);

		GmlExporter<Long, Long> exporter = new GmlExporter<>(x -> String.valueOf(x));
		exporter.setParameter(GmlExporter.Parameter.EXPORT_EDGE_WEIGHTS, exportEdgeWeights);

		AttributesStore vStore = globalHandles.get(vertexLabelsStore);
		if (vStore != null) {
			exporter.setParameter(GmlExporter.Parameter.EXPORT_VERTEX_LABELS, true);
			exporter.setVertexAttributeProvider(v -> {
				Map<String, Attribute> h = new HashMap<>();
				h.put("label", vStore.getAttribute(v, "label"));
				return h;
			});
		}

		AttributesStore eStore = globalHandles.get(edgeLabelsStore);
		if (eStore != null) {
			exporter.setParameter(GmlExporter.Parameter.EXPORT_EDGE_LABELS, true);
			exporter.setEdgeAttributeProvider(e -> {
				Map<String, Attribute> h = new HashMap<>();
				h.put("label", eStore.getAttribute(e, "label"));
				return h;
			});
		}

		exporter.exportGraph(g, new File(CTypeConversion.toJavaString(filename)));
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "export_file_json", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int exportJsonFile(IsolateThread thread, ObjectHandle graphHandle, CCharPointer filename,
			ObjectHandle vertexLabelsStore, ObjectHandle edgeLabelsStore) {
		Graph<Long, Long> g = globalHandles.get(graphHandle);

		JSONExporter<Long, Long> exporter = new JSONExporter<>(x -> String.valueOf(x));

		AttributesStore vStore = globalHandles.get(vertexLabelsStore);
		if (vStore != null) {
			exporter.setVertexAttributeProvider(v -> vStore.getAttributes(v));
		}

		AttributesStore eStore = globalHandles.get(edgeLabelsStore);
		if (eStore != null) {
			exporter.setEdgeAttributeProvider(e -> eStore.getAttributes(e));
		}

		exporter.exportGraph(g, new File(CTypeConversion.toJavaString(filename)));
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "export_file_lemon", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int exportLemonToFile(IsolateThread thread, ObjectHandle graphHandle, CCharPointer filename,
			boolean exportEdgeWeights, boolean escapeStringsAsJava) {
		Graph<Long, Long> g = globalHandles.get(graphHandle);

		LemonExporter<Long, Long> exporter = new LemonExporter<>(x -> String.valueOf(x));
		exporter.setParameter(LemonExporter.Parameter.EXPORT_EDGE_WEIGHTS, exportEdgeWeights);
		exporter.setParameter(LemonExporter.Parameter.ESCAPE_STRINGS_AS_JAVA, escapeStringsAsJava);
		exporter.exportGraph(g, new File(CTypeConversion.toJavaString(filename)));
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "export_file_csv", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int exportCSVToFile(IsolateThread thread, ObjectHandle graphHandle, CCharPointer filename,
			ImporterExporterCSVFormat format, boolean exportEdgeWeights, boolean matrix_format_nodeid,
			boolean matrix_format_zero_when_no_edge) {
		Graph<Long, Long> g = globalHandles.get(graphHandle);

		CSVFormat actualFormat = null;
		switch (format) {
		case CSV_FORMAT_MATRIX:
			actualFormat = CSVFormat.MATRIX;
			break;
		default:
			actualFormat = CSVFormat.ADJACENCY_LIST;
			break;
		}

		CSVExporter<Long, Long> exporter = new CSVExporter<>(x -> String.valueOf(x), actualFormat, ',');
		exporter.setParameter(CSVFormat.Parameter.EDGE_WEIGHTS, exportEdgeWeights);
		exporter.setParameter(CSVFormat.Parameter.MATRIX_FORMAT_NODEID, matrix_format_nodeid);
		exporter.setParameter(CSVFormat.Parameter.MATRIX_FORMAT_ZERO_WHEN_NO_EDGE, matrix_format_zero_when_no_edge);
		exporter.exportGraph(g, new File(CTypeConversion.toJavaString(filename)));
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
			boolean exportEdgeWeights, boolean exportEdgeLabels, boolean exportEdgeTypes, boolean exportMeta) {
		Graph<Long, Long> g = globalHandles.get(graphHandle);

		GEXFExporter<Long, Long> exporter = new GEXFExporter<>(x -> String.valueOf(x), x -> String.valueOf(x));

		exporter.setParameter(GEXFExporter.Parameter.EXPORT_EDGE_WEIGHTS, exportEdgeWeights);
		exporter.setParameter(GEXFExporter.Parameter.EXPORT_EDGE_LABELS, exportEdgeLabels);
		exporter.setParameter(GEXFExporter.Parameter.EXPORT_EDGE_TYPES, exportEdgeTypes);
		exporter.setParameter(GEXFExporter.Parameter.EXPORT_META, exportMeta);

		setupAttributeStores(exporter, vertexAttributesStore, edgeAttributesStore);

		List<RegisteredAttribute> aRegistry = globalHandles.get(attributesRegistry);
		if (aRegistry != null) {
			for (RegisteredAttribute ra : aRegistry) {
				AttributeCategory aCategory = AttributeCategory.valueOf(ra.getCategory().toUpperCase());
				GEXFAttributeType aType = ra.getType() == null ? null
						: GEXFAttributeType.valueOf(ra.getType().toUpperCase());
				exporter.registerAttribute(ra.getName(), aCategory, aType, ra.getDefaultValue());
			}
		}

		exporter.exportGraph(g, new File(CTypeConversion.toJavaString(filename)));
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "export_file_dot", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int exportDotFile(IsolateThread thread, ObjectHandle graphHandle, CCharPointer filename,
			ObjectHandle vertexAttributesStore, ObjectHandle edgeAttributesStore) {
		Graph<Long, Long> g = globalHandles.get(graphHandle);

		DOTExporter<Long, Long> exporter = new DOTExporter<>(x -> String.valueOf(x));

		setupAttributeStores(exporter, vertexAttributesStore, edgeAttributesStore);

		exporter.exportGraph(g, new File(CTypeConversion.toJavaString(filename)));
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "export_file_graph6", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int exportGraph6File(IsolateThread thread, ObjectHandle graphHandle, CCharPointer filename) {
		Graph<Long, Long> g = globalHandles.get(graphHandle);

		Graph6Sparse6Exporter<Long, Long> exporter = new Graph6Sparse6Exporter<>(Graph6Sparse6Exporter.Format.GRAPH6);

		exporter.exportGraph(g, new File(CTypeConversion.toJavaString(filename)));
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "export_file_sparse6", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int exportSparse6File(IsolateThread thread, ObjectHandle graphHandle, CCharPointer filename) {
		Graph<Long, Long> g = globalHandles.get(graphHandle);

		Graph6Sparse6Exporter<Long, Long> exporter = new Graph6Sparse6Exporter<>(Graph6Sparse6Exporter.Format.SPARSE6);

		exporter.exportGraph(g, new File(CTypeConversion.toJavaString(filename)));
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "export_file_graphml", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int exportGraphMLFile(IsolateThread thread, ObjectHandle graphHandle, CCharPointer filename,
			ObjectHandle attributesRegistry, ObjectHandle vertexAttributesStore, ObjectHandle edgeAttributesStore,
			boolean exportEdgeWeights, boolean exportVertexLabels, boolean exportEdgeLabels) {
		Graph<Long, Long> g = globalHandles.get(graphHandle);

		GraphMLExporter<Long, Long> exporter = new GraphMLExporter<>(x -> String.valueOf(x));

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

		exporter.exportGraph(g, new File(CTypeConversion.toJavaString(filename)));
		return Status.STATUS_SUCCESS.getCValue();
	}

	private static void setupAttributeStores(BaseExporter<Long, Long> exporter, ObjectHandle vertexAttributesStore,
			ObjectHandle edgeAttributesStore) {
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

}
