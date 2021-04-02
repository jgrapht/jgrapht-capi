/*
 * (C) Copyright 2020-2021, by Dimitrios Michail.
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

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.ANY_ANY
			+ "planarity_exec_boyer_myrvold", exceptionHandler = StatusReturnExceptionHandler.class)
	public static <V, E> int executeBoryerMyrvold(IsolateThread thread, ObjectHandle graphHandle, CIntPointer res,
			WordPointer embeddingRes, WordPointer kuratowskiSubdivisionRes) {
		Graph<V, E> g = globalHandles.get(graphHandle);

		BoyerMyrvoldPlanarityInspector<V, E> alg = new BoyerMyrvoldPlanarityInspector<>(g);

		boolean result = alg.isPlanar();
		if (res.isNonNull()) {
			res.write(result ? 1 : 0);
		}
		if (result) {
			Embedding<V, E> embedding = alg.getEmbedding();
			if (embeddingRes.isNonNull()) {
				embeddingRes.write(globalHandles.create(embedding));
			}
		} else {
			Graph<V, E> kuratowskiSubdivision = alg.getKuratowskiSubdivision();
			if (kuratowskiSubdivisionRes.isNonNull()) {
				kuratowskiSubdivisionRes.write(globalHandles.create(kuratowskiSubdivision));
			}
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.INT_ANY
			+ "planarity_embedding_edges_around_vertex", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int edgesAround(IsolateThread thread, ObjectHandle embeddingHandle, int vertex, WordPointer res) {
		Embedding<Integer, ?> embedding = globalHandles.get(embeddingHandle);
		List<?> list = embedding.getEdgesAround(vertex);
		if (list != null && res.isNonNull()) {
			res.write(globalHandles.create(list.iterator()));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.LONG_ANY
			+ "planarity_embedding_edges_around_vertex", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int edgesAround(IsolateThread thread, ObjectHandle embeddingHandle, long vertex, WordPointer res) {
		Embedding<Long, ?> embedding = globalHandles.get(embeddingHandle);
		List<?> list = embedding.getEdgesAround(vertex);
		if (list != null && res.isNonNull()) {
			res.write(globalHandles.create(list.iterator()));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

}
