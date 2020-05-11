package org.jgrapht.capi.impl;

import java.util.List;

import org.graalvm.nativeimage.IsolateThread;
import org.graalvm.nativeimage.ObjectHandle;
import org.graalvm.nativeimage.ObjectHandles;
import org.graalvm.nativeimage.c.function.CEntryPoint;
import org.graalvm.nativeimage.c.type.CIntPointer;
import org.graalvm.nativeimage.c.type.WordPointer;
import org.jgrapht.Graph;
import org.jgrapht.alg.interfaces.PlanarityTestingAlgorithm.Embedding;
import org.jgrapht.alg.planar.BoyerMyrvoldPlanarityInspector;
import org.jgrapht.capi.Constants;
import org.jgrapht.capi.JGraphTContext.Status;
import org.jgrapht.capi.error.StatusReturnExceptionHandler;

public class PlanarityApi {

	private static ObjectHandles globalHandles = ObjectHandles.getGlobal();

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "planarity_exec_boyer_myrvold", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeBoryerMyrvold(IsolateThread thread, ObjectHandle graphHandle, CIntPointer res,
			WordPointer embeddingRes, WordPointer kuratowskiSubdivisionRes) {
		Graph<Integer, Integer> g = globalHandles.get(graphHandle);

		BoyerMyrvoldPlanarityInspector<Integer, Integer> alg = new BoyerMyrvoldPlanarityInspector<>(g);

		boolean result = alg.isPlanar();
		if (res.isNonNull()) {
			res.write(result ? 1 : 0);
		}
		if (result) {
			Embedding<Integer, Integer> embedding = alg.getEmbedding();
			if (embeddingRes.isNonNull()) {
				embeddingRes.write(globalHandles.create(embedding));
			}
		} else {
			Graph<Integer, Integer> kuratowskiSubdivision = alg.getKuratowskiSubdivision();
			if (kuratowskiSubdivisionRes.isNonNull()) {
				kuratowskiSubdivisionRes.write(globalHandles.create(kuratowskiSubdivision));
			}
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "planarity_embedding_edges_around_vertex", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int edgesAround(IsolateThread thread, ObjectHandle embeddingHandle, int vertex, WordPointer res) {
		Embedding<Integer, Integer> embedding = globalHandles.get(embeddingHandle);
		List<Integer> list = embedding.getEdgesAround(vertex);
		if (list != null && res.isNonNull()) {
			res.write(globalHandles.create(list.iterator()));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

}
