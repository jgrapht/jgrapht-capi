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

import org.graalvm.nativeimage.IsolateThread;
import org.graalvm.nativeimage.ObjectHandle;
import org.graalvm.nativeimage.ObjectHandles;
import org.graalvm.nativeimage.c.function.CEntryPoint;
import org.graalvm.nativeimage.c.type.WordPointer;
import org.jgrapht.Graph;
import org.jgrapht.ListenableGraph;
import org.jgrapht.capi.Constants;
import org.jgrapht.capi.JGraphTContext.IIFunctionPointer;
import org.jgrapht.capi.JGraphTContext.Status;
import org.jgrapht.capi.error.StatusReturnExceptionHandler;
import org.jgrapht.event.GraphEdgeChangeEvent;
import org.jgrapht.event.GraphListener;
import org.jgrapht.event.GraphVertexChangeEvent;
import org.jgrapht.graph.DefaultListenableGraph;

/**
 * Listenable graph API.
 */
public class ListenableGraphApi {

	private static ObjectHandles globalHandles = ObjectHandles.getGlobal();

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "listenable_create_listenable_graph", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int createListenable(IsolateThread thread, ObjectHandle graphHandle, WordPointer res) {
		Graph<Integer, Integer> gIn = globalHandles.get(graphHandle);
		Graph<Integer, Integer> gOut = new DefaultListenableGraph<>(gIn);
		if (res.isNonNull()) {
			res.write(globalHandles.create(gOut));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "listenable_create_graph_listener", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int createGraphListener(IsolateThread thread, IIFunctionPointer vertexAddedFunctionPointer,
			IIFunctionPointer vertexRemovedFunctionPointer, IIFunctionPointer edgeAddedFunctionPointer,
			IIFunctionPointer edgeRemovedFunctionPointer, IIFunctionPointer edgeWeightUpdatedFunctionPointer,
			WordPointer res) {

		InvokeGraphListener listener = new InvokeGraphListener(vertexAddedFunctionPointer, vertexRemovedFunctionPointer,
				edgeAddedFunctionPointer, edgeRemovedFunctionPointer, edgeWeightUpdatedFunctionPointer);
		if (res.isNonNull()) {
			res.write(globalHandles.create(listener));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "listenable_add_graph_listener", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int addGraphListener(IsolateThread thread, ObjectHandle graphHandle, ObjectHandle listenerHandle) {
		ListenableGraph<Integer, Integer> gIn = globalHandles.get(graphHandle);
		InvokeGraphListener listener = globalHandles.get(listenerHandle);
		gIn.addGraphListener(listener);
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "listenable_remove_graph_listener", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int removeGraphListener(IsolateThread thread, ObjectHandle graphHandle, ObjectHandle listenerHandle) {
		ListenableGraph<Integer, Integer> gIn = globalHandles.get(graphHandle);
		InvokeGraphListener listener = globalHandles.get(listenerHandle);
		gIn.removeGraphListener(listener);
		return Status.STATUS_SUCCESS.getCValue();
	}

	private static class InvokeGraphListener implements GraphListener<Integer, Integer> {

		private IIFunctionPointer vertexAddedFunctionPointer;
		private IIFunctionPointer vertexRemovedFunctionPointer;
		private IIFunctionPointer edgeAddedFunctionPointer;
		private IIFunctionPointer edgeRemovedFunctionPointer;
		private IIFunctionPointer edgeWeightUpdatedFunctionPointer;

		public InvokeGraphListener(IIFunctionPointer vertexAddedFunctionPointer,
				IIFunctionPointer vertexRemovedFunctionPointer, IIFunctionPointer edgeAddedFunctionPointer,
				IIFunctionPointer edgeRemovedFunctionPointer, IIFunctionPointer edgeWeightUpdatedFunctionPointer) {
			this.vertexAddedFunctionPointer = vertexAddedFunctionPointer;
			this.vertexRemovedFunctionPointer = vertexRemovedFunctionPointer;
			this.edgeAddedFunctionPointer = edgeAddedFunctionPointer;
			this.edgeRemovedFunctionPointer = edgeRemovedFunctionPointer;
			this.edgeWeightUpdatedFunctionPointer = edgeWeightUpdatedFunctionPointer;
		}

		@Override
		public void vertexAdded(GraphVertexChangeEvent<Integer> e) {
			if (vertexAddedFunctionPointer.isNonNull()) {
				vertexAddedFunctionPointer.invoke(e.getVertex(), e.getType());
			}
		}

		@Override
		public void vertexRemoved(GraphVertexChangeEvent<Integer> e) {
			if (vertexRemovedFunctionPointer.isNonNull()) {
				vertexRemovedFunctionPointer.invoke(e.getVertex(), e.getType());
			}
		}

		@Override
		public void edgeAdded(GraphEdgeChangeEvent<Integer, Integer> e) {
			if (edgeAddedFunctionPointer.isNonNull()) {
				edgeAddedFunctionPointer.invoke(e.getEdge(), e.getType());
			}
		}

		@Override
		public void edgeRemoved(GraphEdgeChangeEvent<Integer, Integer> e) {
			if (edgeRemovedFunctionPointer.isNonNull()) {
				edgeRemovedFunctionPointer.invoke(e.getEdge(), e.getType());
			}
		}

		@Override
		public void edgeWeightUpdated(GraphEdgeChangeEvent<Integer, Integer> e) {
			if (edgeWeightUpdatedFunctionPointer.isNonNull()) {
				edgeWeightUpdatedFunctionPointer.invoke(e.getEdge(), e.getType());
			}
		}

	}

}
