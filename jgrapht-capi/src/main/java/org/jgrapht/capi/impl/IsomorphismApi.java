package org.jgrapht.capi.impl;

import org.graalvm.nativeimage.IsolateThread;
import org.graalvm.nativeimage.ObjectHandle;
import org.graalvm.nativeimage.ObjectHandles;
import org.graalvm.nativeimage.c.function.CEntryPoint;
import org.graalvm.nativeimage.c.type.CIntPointer;
import org.graalvm.nativeimage.c.type.WordPointer;
import org.jgrapht.Graph;
import org.jgrapht.GraphMapping;
import org.jgrapht.alg.isomorphism.VF2GraphIsomorphismInspector;
import org.jgrapht.alg.isomorphism.VF2SubgraphIsomorphismInspector;
import org.jgrapht.capi.Constants;
import org.jgrapht.capi.JGraphTContext.Status;
import org.jgrapht.capi.error.StatusReturnExceptionHandler;

public class IsomorphismApi {

	private static ObjectHandles globalHandles = ObjectHandles.getGlobal();

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "isomorphism_exec_vf2", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeVF2(IsolateThread thread, ObjectHandle graph1Handle, ObjectHandle graph2Handle,
			CIntPointer existsRes, WordPointer graphMappingIteratorRes) {
		Graph<Integer, Integer> g1 = globalHandles.get(graph1Handle);
		Graph<Integer, Integer> g2 = globalHandles.get(graph2Handle);

		VF2GraphIsomorphismInspector<Integer, Integer> alg = new VF2GraphIsomorphismInspector<>(g1, g2);
		boolean exists = alg.isomorphismExists();
		if (existsRes.isNonNull()) {
			existsRes.write(exists ? 1 : 0);
		}
		if (exists && graphMappingIteratorRes.isNonNull()) {
			graphMappingIteratorRes.write(globalHandles.create(alg.getMappings()));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	/**
	 * Note that this inspector only finds isomorphisms between a smaller graph and
	 * all
	 * <a href="http://mathworld.wolfram.com/Vertex-InducedSubgraph.html">induced
	 * subgraphs</a> of a larger graph. It does not find isomorphisms between the
	 * smaller graph and arbitrary subgraphs of the larger graph.
	 */
	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "isomorphism_exec_vf2_subgraph", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeVF2Subgraph(IsolateThread thread, ObjectHandle graph1Handle, ObjectHandle graph2Handle,
			CIntPointer existsRes, WordPointer graphMappingIteratorRes) {
		Graph<Integer, Integer> g1 = globalHandles.get(graph1Handle);
		Graph<Integer, Integer> g2 = globalHandles.get(graph2Handle);

		VF2SubgraphIsomorphismInspector<Integer, Integer> alg = new VF2SubgraphIsomorphismInspector<>(g1, g2);
		boolean exists = alg.isomorphismExists();
		if (existsRes.isNonNull()) {
			existsRes.write(exists ? 1 : 0);
		}
		if (exists && graphMappingIteratorRes.isNonNull()) {
			graphMappingIteratorRes.write(globalHandles.create(alg.getMappings()));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "isomorphism_graph_mapping_edge_correspondence", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int graphMappingEdge(IsolateThread thread, ObjectHandle mappingHandle, int edge, boolean forward,
			CIntPointer existsEdgeRes, CIntPointer edgeRes) {
		GraphMapping<Integer, Integer> graphMapping = globalHandles.get(mappingHandle);

		Integer otherEdge = graphMapping.getEdgeCorrespondence(edge, forward);
		if (existsEdgeRes.isNonNull()) {
			if (otherEdge != null) {
				existsEdgeRes.write(1);
				if (edgeRes.isNonNull()) {
					edgeRes.write(otherEdge);
				}
			} else {
				existsEdgeRes.write(0);
			}
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "isomorphism_graph_mapping_vertex_correspondence", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int graphMappingVertex(IsolateThread thread, ObjectHandle mappingHandle, int vertex, boolean forward,
			CIntPointer existsVertexRes, CIntPointer vertexRes) {
		GraphMapping<Integer, Integer> graphMapping = globalHandles.get(mappingHandle);

		Integer otherVertex = graphMapping.getVertexCorrespondence(vertex, forward);
		if (existsVertexRes.isNonNull()) {
			if (otherVertex != null) {
				existsVertexRes.write(1);
				if (vertexRes.isNonNull()) {
					vertexRes.write(otherVertex);
				}
			} else {
				existsVertexRes.write(0);
			}
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

}
