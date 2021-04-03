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

import org.graalvm.nativeimage.IsolateThread;
import org.graalvm.nativeimage.ObjectHandle;
import org.graalvm.nativeimage.ObjectHandles;
import org.graalvm.nativeimage.c.function.CEntryPoint;
import org.graalvm.nativeimage.c.type.WordPointer;
import org.jgrapht.ListenableGraph;
import org.jgrapht.capi.Constants;
import org.jgrapht.capi.JGraphTContext.IIFunctionPointer;
import org.jgrapht.capi.JGraphTContext.LIFunctionPointer;
import org.jgrapht.capi.JGraphTContext.PIFunctionPointer;
import org.jgrapht.capi.JGraphTContext.Status;
import org.jgrapht.capi.error.StatusReturnExceptionHandler;
import org.jgrapht.capi.graph.CapiGraph;
import org.jgrapht.capi.graph.CapiGraphAsListenableGraph;
import org.jgrapht.capi.graph.ExternalRef;
import org.jgrapht.event.GraphEdgeChangeEvent;
import org.jgrapht.event.GraphListener;
import org.jgrapht.event.GraphVertexChangeEvent;

/**
 * Listenable graph API.
 */
public class ListenableGraphApi {

	private static ObjectHandles globalHandles = ObjectHandles.getGlobal();

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.ANY_ANY
			+ "listenable_as_listenable", exceptionHandler = StatusReturnExceptionHandler.class)
	public static <V, E> int asListenable(IsolateThread thread, ObjectHandle graphHandle, WordPointer res) {
		CapiGraph<V, E> gIn = globalHandles.get(graphHandle);
		CapiGraph<V, E> gOut = new CapiGraphAsListenableGraph<>(gIn);
		if (res.isNonNull()) {
			res.write(globalHandles.create(gOut));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.INT_INT
			+ "listenable_create_graph_listener", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int createGraphListener(IsolateThread thread, IIFunctionPointer eventFunctionPointer,
			WordPointer res) {
		InvokeGraphListener listener = new InvokeGraphListener(eventFunctionPointer);
		if (res.isNonNull()) {
			res.write(globalHandles.create(listener));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.LONG_LONG
			+ "listenable_create_graph_listener", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int createGraphListener(IsolateThread thread, LIFunctionPointer eventFunctionPointer,
			WordPointer res) {
		LongInvokeGraphListener listener = new LongInvokeGraphListener(eventFunctionPointer);
		if (res.isNonNull()) {
			res.write(globalHandles.create(listener));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.DREF_DREF
			+ "listenable_create_graph_listener", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int createGraphListener(IsolateThread thread, PIFunctionPointer eventFunctionPointer,
			WordPointer res) {
		ExternalRefInvokeGraphListener listener = new ExternalRefInvokeGraphListener(eventFunctionPointer);
		if (res.isNonNull()) {
			res.write(globalHandles.create(listener));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.ANY_ANY
			+ "listenable_add_graph_listener", exceptionHandler = StatusReturnExceptionHandler.class)
	public static <V, E> int xxAddGraphListener(IsolateThread thread, ObjectHandle graphHandle,
			ObjectHandle listenerHandle) {
		ListenableGraph<V, E> g = globalHandles.get(graphHandle);
		GraphListener<V, E> listener = globalHandles.get(listenerHandle);
		g.addGraphListener(listener);
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.ANY_ANY
			+ "listenable_remove_graph_listener", exceptionHandler = StatusReturnExceptionHandler.class)
	public static <V, E> int removeGraphListener(IsolateThread thread, ObjectHandle graphHandle,
			ObjectHandle listenerHandle) {
		ListenableGraph<V, E> g = globalHandles.get(graphHandle);
		GraphListener<V, E> listener = globalHandles.get(listenerHandle);
		g.removeGraphListener(listener);
		return Status.STATUS_SUCCESS.getCValue();
	}

	private static class InvokeGraphListener implements GraphListener<Integer, Integer> {

		private IIFunctionPointer eventFunctionPointer;

		public InvokeGraphListener(IIFunctionPointer eventFunctionPointer) {
			this.eventFunctionPointer = eventFunctionPointer;
		}

		@Override
		public void vertexAdded(GraphVertexChangeEvent<Integer> e) {
			if (eventFunctionPointer.isNonNull()) {
				eventFunctionPointer.invoke(e.getVertex(), e.getType());
			}
		}

		@Override
		public void vertexRemoved(GraphVertexChangeEvent<Integer> e) {
			if (eventFunctionPointer.isNonNull()) {
				eventFunctionPointer.invoke(e.getVertex(), e.getType());
			}
		}

		@Override
		public void edgeAdded(GraphEdgeChangeEvent<Integer, Integer> e) {
			if (eventFunctionPointer.isNonNull()) {
				eventFunctionPointer.invoke(e.getEdge(), e.getType());
			}
		}

		@Override
		public void edgeRemoved(GraphEdgeChangeEvent<Integer, Integer> e) {
			if (eventFunctionPointer.isNonNull()) {
				eventFunctionPointer.invoke(e.getEdge(), e.getType());
			}
		}

		@Override
		public void edgeWeightUpdated(GraphEdgeChangeEvent<Integer, Integer> e) {
			if (eventFunctionPointer.isNonNull()) {
				eventFunctionPointer.invoke(e.getEdge(), e.getType());
			}
		}

	}

	private static class LongInvokeGraphListener implements GraphListener<Long, Long> {

		private LIFunctionPointer eventFunctionPointer;

		public LongInvokeGraphListener(LIFunctionPointer eventFunctionPointer) {
			this.eventFunctionPointer = eventFunctionPointer;
		}

		@Override
		public void vertexAdded(GraphVertexChangeEvent<Long> e) {
			if (eventFunctionPointer.isNonNull()) {
				eventFunctionPointer.invoke(e.getVertex(), e.getType());
			}
		}

		@Override
		public void vertexRemoved(GraphVertexChangeEvent<Long> e) {
			if (eventFunctionPointer.isNonNull()) {
				eventFunctionPointer.invoke(e.getVertex(), e.getType());
			}
		}

		@Override
		public void edgeAdded(GraphEdgeChangeEvent<Long, Long> e) {
			if (eventFunctionPointer.isNonNull()) {
				eventFunctionPointer.invoke(e.getEdge(), e.getType());
			}
		}

		@Override
		public void edgeRemoved(GraphEdgeChangeEvent<Long, Long> e) {
			if (eventFunctionPointer.isNonNull()) {
				eventFunctionPointer.invoke(e.getEdge(), e.getType());
			}
		}

		@Override
		public void edgeWeightUpdated(GraphEdgeChangeEvent<Long, Long> e) {
			if (eventFunctionPointer.isNonNull()) {
				eventFunctionPointer.invoke(e.getEdge(), e.getType());
			}
		}

	}

	private static class ExternalRefInvokeGraphListener implements GraphListener<ExternalRef, ExternalRef> {

		private PIFunctionPointer eventFunctionPointer;

		public ExternalRefInvokeGraphListener(PIFunctionPointer eventFunctionPointer) {
			this.eventFunctionPointer = eventFunctionPointer;
		}

		@Override
		public void vertexAdded(GraphVertexChangeEvent<ExternalRef> e) {
			if (eventFunctionPointer.isNonNull()) {
				eventFunctionPointer.invoke(e.getVertex().getPtr(), e.getType());
			}
		}

		@Override
		public void vertexRemoved(GraphVertexChangeEvent<ExternalRef> e) {
			if (eventFunctionPointer.isNonNull()) {
				eventFunctionPointer.invoke(e.getVertex().getPtr(), e.getType());
			}
		}

		@Override
		public void edgeAdded(GraphEdgeChangeEvent<ExternalRef, ExternalRef> e) {
			if (eventFunctionPointer.isNonNull()) {
				eventFunctionPointer.invoke(e.getEdge().getPtr(), e.getType());
			}
		}

		@Override
		public void edgeRemoved(GraphEdgeChangeEvent<ExternalRef, ExternalRef> e) {
			if (eventFunctionPointer.isNonNull()) {
				eventFunctionPointer.invoke(e.getEdge().getPtr(), e.getType());
			}
		}

		@Override
		public void edgeWeightUpdated(GraphEdgeChangeEvent<ExternalRef, ExternalRef> e) {
			if (eventFunctionPointer.isNonNull()) {
				eventFunctionPointer.invoke(e.getEdge().getPtr(), e.getType());
			}
		}

	}
}
