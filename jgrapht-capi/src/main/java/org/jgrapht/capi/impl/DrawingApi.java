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

import java.util.Comparator;
import java.util.Random;
import java.util.Set;

import org.graalvm.nativeimage.IsolateThread;
import org.graalvm.nativeimage.ObjectHandle;
import org.graalvm.nativeimage.ObjectHandles;
import org.graalvm.nativeimage.c.function.CEntryPoint;
import org.graalvm.nativeimage.c.type.CDoublePointer;
import org.graalvm.nativeimage.c.type.CIntPointer;
import org.graalvm.nativeimage.c.type.WordPointer;
import org.jgrapht.Graph;
import org.jgrapht.alg.drawing.BarycenterGreedyTwoLayeredBipartiteLayout2D;
import org.jgrapht.alg.drawing.CircularLayoutAlgorithm2D;
import org.jgrapht.alg.drawing.FRLayoutAlgorithm2D;
import org.jgrapht.alg.drawing.MedianGreedyTwoLayeredBipartiteLayout2D;
import org.jgrapht.alg.drawing.RandomLayoutAlgorithm2D;
import org.jgrapht.alg.drawing.RescaleLayoutAlgorithm2D;
import org.jgrapht.alg.drawing.TwoLayeredBipartiteLayout2D;
import org.jgrapht.alg.drawing.model.Box2D;
import org.jgrapht.alg.drawing.model.LayoutModel2D;
import org.jgrapht.alg.drawing.model.MapLayoutModel2D;
import org.jgrapht.alg.drawing.model.Point2D;
import org.jgrapht.capi.Constants;
import org.jgrapht.capi.JGraphTContext.IIToIFunctionPointer;
import org.jgrapht.capi.JGraphTContext.LLToIFunctionPointer;
import org.jgrapht.capi.JGraphTContext.Status;
import org.jgrapht.capi.custom.drawing.IndexedFRLayoutAlgorithm2D;
import org.jgrapht.capi.error.StatusReturnExceptionHandler;

public class DrawingApi {

	private static ObjectHandles globalHandles = ObjectHandles.getGlobal();

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.ANYANY
			+ "drawing_layout_model_2d_create", exceptionHandler = StatusReturnExceptionHandler.class)
	public static <V> int layoutModel2DCreate(IsolateThread thread, double minX, double minY, double width,
			double height, WordPointer res) {
		MapLayoutModel2D<V> model = new MapLayoutModel2D<>(Box2D.of(minX, minY, width, height));
		if (res.isNonNull()) {
			res.write(globalHandles.create(model));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.ANYANY
			+ "drawing_layout_model_2d_get_drawable_area", exceptionHandler = StatusReturnExceptionHandler.class)
	public static <V> int layoutModel2dGetDrawableArea(IsolateThread thread, ObjectHandle model, CDoublePointer minX,
			CDoublePointer minY, CDoublePointer width, CDoublePointer height) {
		LayoutModel2D<V> m = globalHandles.get(model);
		Box2D area = m.getDrawableArea();
		if (area != null) {
			if (minX.isNonNull()) {
				minX.write(area.getMinX());
			}
			if (minY.isNonNull()) {
				minY.write(area.getMinY());
			}
			if (width.isNonNull()) {
				width.write(area.getWidth());
			}
			if (height.isNonNull()) {
				height.write(area.getHeight());
			}
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.INTINT
			+ "drawing_layout_model_2d_get_vertex", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int layoutModel2dGetVertex(IsolateThread thread, ObjectHandle model, int vertex, CDoublePointer x,
			CDoublePointer y) {
		LayoutModel2D<Integer> m = globalHandles.get(model);
		Point2D p = m.get(vertex);
		if (p != null) {
			if (x.isNonNull()) {
				x.write(p.getX());
			}
			if (y.isNonNull()) {
				y.write(p.getY());
			}
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.LONGLONG
			+ "drawing_layout_model_2d_get_vertex", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int layoutModel2dGetVertex(IsolateThread thread, ObjectHandle model, long vertex, CDoublePointer x,
			CDoublePointer y) {
		LayoutModel2D<Long> m = globalHandles.get(model);
		Point2D p = m.get(vertex);
		if (p != null) {
			if (x.isNonNull()) {
				x.write(p.getX());
			}
			if (y.isNonNull()) {
				y.write(p.getY());
			}
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.INTINT
			+ "drawing_layout_model_2d_put_vertex", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int layoutModel2dPutVertex(IsolateThread thread, ObjectHandle model, int vertex, double x, double y) {
		LayoutModel2D<Integer> m = globalHandles.get(model);
		m.put(vertex, Point2D.of(x, y));
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.LONGLONG
			+ "drawing_layout_model_2d_put_vertex", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int layoutModel2dPutVertex(IsolateThread thread, ObjectHandle model, long vertex, double x,
			double y) {
		LayoutModel2D<Long> m = globalHandles.get(model);
		m.put(vertex, Point2D.of(x, y));
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.INTINT
			+ "drawing_layout_model_2d_get_fixed", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int layoutModel2dGetFixed(IsolateThread thread, ObjectHandle model, int vertex, CIntPointer res) {
		LayoutModel2D<Integer> m = globalHandles.get(model);
		if (res.isNonNull()) {
			res.write(m.isFixed(vertex) ? 1 : 0);
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.LONGLONG
			+ "drawing_layout_model_2d_get_fixed", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int layoutModel2dGetFixed(IsolateThread thread, ObjectHandle model, long vertex, CIntPointer res) {
		LayoutModel2D<Long> m = globalHandles.get(model);
		if (res.isNonNull()) {
			res.write(m.isFixed(vertex) ? 1 : 0);
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.INTINT
			+ "drawing_layout_model_2d_set_fixed", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int layoutModel2dSetFixed(IsolateThread thread, ObjectHandle model, int vertex, boolean fixed) {
		LayoutModel2D<Integer> m = globalHandles.get(model);
		m.setFixed(vertex, fixed);
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.LONGLONG
			+ "drawing_layout_model_2d_set_fixed", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int layoutModel2dSetFixed(IsolateThread thread, ObjectHandle model, long vertex, boolean fixed) {
		LayoutModel2D<Long> m = globalHandles.get(model);
		m.setFixed(vertex, fixed);
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.ANYANY
			+ "drawing_exec_random_layout_2d", exceptionHandler = StatusReturnExceptionHandler.class)
	public static <V, E> int executeRandomLayout(IsolateThread thread, ObjectHandle graphHandle, ObjectHandle model,
			long seed) {
		Graph<V, E> g = globalHandles.get(graphHandle);
		LayoutModel2D<V> m = globalHandles.get(model);

		new RandomLayoutAlgorithm2D<V, E>(seed).layout(g, m);
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.INTINT
			+ "drawing_exec_circular_layout_2d", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeCircularLayout(IsolateThread thread, ObjectHandle graphHandle, ObjectHandle model,
			double radius, IIToIFunctionPointer vertexComparator) {
		Graph<Integer, Integer> g = globalHandles.get(graphHandle);
		LayoutModel2D<Integer> m = globalHandles.get(model);

		Comparator<Integer> comparator = null;
		if (vertexComparator.isNonNull()) {
			comparator = (a, b) -> vertexComparator.invoke(a, b);
		}

		new CircularLayoutAlgorithm2D<Integer, Integer>(radius, comparator).layout(g, m);
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.LONGLONG
			+ "drawing_exec_circular_layout_2d", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeCircularLayout(IsolateThread thread, ObjectHandle graphHandle, ObjectHandle model,
			double radius, LLToIFunctionPointer vertexComparator) {
		Graph<Long, Long> g = globalHandles.get(graphHandle);
		LayoutModel2D<Long> m = globalHandles.get(model);

		Comparator<Long> comparator = null;
		if (vertexComparator.isNonNull()) {
			comparator = (a, b) -> vertexComparator.invoke(a, b);
		}

		new CircularLayoutAlgorithm2D<Long, Long>(radius, comparator).layout(g, m);
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.ANYANY
			+ "drawing_exec_fr_layout_2d", exceptionHandler = StatusReturnExceptionHandler.class)
	public static <V, E> int executeFRLayout(IsolateThread thread, ObjectHandle graphHandle, ObjectHandle model,
			int iterations, double normalizationFactor, long seed) {
		Graph<V, E> g = globalHandles.get(graphHandle);
		LayoutModel2D<V> m = globalHandles.get(model);
		new FRLayoutAlgorithm2D<V, E>(iterations, normalizationFactor, new Random(seed)).layout(g, m);
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.ANYANY
			+ "drawing_exec_indexed_fr_layout_2d", exceptionHandler = StatusReturnExceptionHandler.class)
	public static <V, E> int executeIndexedFRLayout(IsolateThread thread, ObjectHandle graphHandle, ObjectHandle model,
			int iterations, double normalizationFactor, long seed, double theta, double tolerance) {
		Graph<V, E> g = globalHandles.get(graphHandle);
		LayoutModel2D<V> m = globalHandles.get(model);
		new IndexedFRLayoutAlgorithm2D<V, E>(iterations, theta, normalizationFactor, new Random(seed), tolerance)
				.layout(g, m);
		return Status.STATUS_SUCCESS.getCValue();
	}

	/**
	 * A layout algorithm which re-scales vertex positions to
	 * (center-scale,center+scale) in all dimensions.
	 * 
	 * @param thread      the isolate thread
	 * @param graphHandle the graph
	 * @param model       the {@link LayoutModel2D} model
	 * @param scale       the scale factor
	 * @return the return status
	 */
	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.ANYANY
			+ "drawing_exec_rescale_layout_2d", exceptionHandler = StatusReturnExceptionHandler.class)
	public static <V, E> int executeRescaleLayout(IsolateThread thread, ObjectHandle graphHandle, ObjectHandle model,
			double scale) {
		Graph<V, E> g = globalHandles.get(graphHandle);
		LayoutModel2D<V> m = globalHandles.get(model);
		new RescaleLayoutAlgorithm2D<V, E>(scale).layout(g, m);
		return Status.STATUS_SUCCESS.getCValue();
	}

	/**
	 * A bipartite layout algorithm.
	 * 
	 * @param thread                      the isolate thread
	 * @param graphHandle                 the graph
	 * @param modelHandle                 the {@link LayoutModel2D} model
	 * @param partitionHandle             a possible null left partition of the
	 *                                    bipartite graph
	 * @param vertexComparatorFunctionPtr a possible null vertex comparator
	 * @param vertical                    whether to draw vertically or not
	 * @return the return status
	 */
	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.INTINT
			+ "drawing_exec_two_layered_bipartite_layout_2d", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeTwoLayeredBipartite(IsolateThread thread, ObjectHandle graphHandle,
			ObjectHandle modelHandle, ObjectHandle partitionHandle, IIToIFunctionPointer vertexComparatorFunctionPtr,
			boolean vertical) {
		Graph<Integer, Integer> graph = globalHandles.get(graphHandle);
		LayoutModel2D<Integer> model = globalHandles.get(modelHandle);
		Set<Integer> partition = globalHandles.get(partitionHandle);
		Comparator<Integer> vertexComparator = null;
		if (vertexComparatorFunctionPtr.isNonNull()) {
			vertexComparator = (a, b) -> vertexComparatorFunctionPtr.invoke(a, b);
		}
		new TwoLayeredBipartiteLayout2D<Integer, Integer>(partition, vertexComparator, vertical).layout(graph, model);
		return Status.STATUS_SUCCESS.getCValue();
	}

	/**
	 * A bipartite layout algorithm.
	 * 
	 * @param thread                      the isolate thread
	 * @param graphHandle                 the graph
	 * @param modelHandle                 the {@link LayoutModel2D} model
	 * @param partitionHandle             a possible null left partition of the
	 *                                    bipartite graph
	 * @param vertexComparatorFunctionPtr a possible null vertex comparator
	 * @param vertical                    whether to draw vertically or not
	 * @return the return status
	 */
	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.LONGLONG
			+ "drawing_exec_two_layered_bipartite_layout_2d", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeTwoLayeredBipartite(IsolateThread thread, ObjectHandle graphHandle,
			ObjectHandle modelHandle, ObjectHandle partitionHandle, LLToIFunctionPointer vertexComparatorFunctionPtr,
			boolean vertical) {
		Graph<Long, Long> graph = globalHandles.get(graphHandle);
		LayoutModel2D<Long> model = globalHandles.get(modelHandle);
		Set<Long> partition = globalHandles.get(partitionHandle);
		Comparator<Long> vertexComparator = null;
		if (vertexComparatorFunctionPtr.isNonNull()) {
			vertexComparator = (a, b) -> vertexComparatorFunctionPtr.invoke(a, b);
		}
		new TwoLayeredBipartiteLayout2D<Long, Long>(partition, vertexComparator, vertical).layout(graph, model);
		return Status.STATUS_SUCCESS.getCValue();
	}

	/**
	 * A bipartite layout algorithm.
	 * 
	 * @param thread                      the isolate thread
	 * @param graphHandle                 the graph
	 * @param modelHandle                 the {@link LayoutModel2D} model
	 * @param partitionHandle             a possible null left partition of the
	 *                                    bipartite graph
	 * @param vertexComparatorFunctionPtr a possible null vertex comparator
	 * @param vertical                    whether to draw vertically or not
	 * @return the return status
	 */
	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.INTINT
			+ "drawing_exec_barycenter_greedy_two_layered_bipartite_layout_2d", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeBarycenterGreedyTwoLayeredBipartite(IsolateThread thread, ObjectHandle graphHandle,
			ObjectHandle modelHandle, ObjectHandle partitionHandle, IIToIFunctionPointer vertexComparatorFunctionPtr,
			boolean vertical) {
		Graph<Integer, Integer> graph = globalHandles.get(graphHandle);
		LayoutModel2D<Integer> model = globalHandles.get(modelHandle);
		Set<Integer> partition = globalHandles.get(partitionHandle);
		Comparator<Integer> vertexComparator = null;
		if (vertexComparatorFunctionPtr.isNonNull()) {
			vertexComparator = (a, b) -> vertexComparatorFunctionPtr.invoke(a, b);
		}
		new BarycenterGreedyTwoLayeredBipartiteLayout2D<Integer, Integer>(partition, vertexComparator, vertical)
				.layout(graph, model);
		return Status.STATUS_SUCCESS.getCValue();
	}

	/**
	 * A bipartite layout algorithm.
	 * 
	 * @param thread                      the isolate thread
	 * @param graphHandle                 the graph
	 * @param modelHandle                 the {@link LayoutModel2D} model
	 * @param partitionHandle             a possible null left partition of the
	 *                                    bipartite graph
	 * @param vertexComparatorFunctionPtr a possible null vertex comparator
	 * @param vertical                    whether to draw vertically or not
	 * @return the return status
	 */
	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.LONGLONG
			+ "drawing_exec_barycenter_greedy_two_layered_bipartite_layout_2d", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeBarycenterGreedyTwoLayeredBipartite(IsolateThread thread, ObjectHandle graphHandle,
			ObjectHandle modelHandle, ObjectHandle partitionHandle, LLToIFunctionPointer vertexComparatorFunctionPtr,
			boolean vertical) {
		Graph<Long, Long> graph = globalHandles.get(graphHandle);
		LayoutModel2D<Long> model = globalHandles.get(modelHandle);
		Set<Long> partition = globalHandles.get(partitionHandle);
		Comparator<Long> vertexComparator = null;
		if (vertexComparatorFunctionPtr.isNonNull()) {
			vertexComparator = (a, b) -> vertexComparatorFunctionPtr.invoke(a, b);
		}
		new BarycenterGreedyTwoLayeredBipartiteLayout2D<Long, Long>(partition, vertexComparator, vertical).layout(graph,
				model);
		return Status.STATUS_SUCCESS.getCValue();
	}

	/**
	 * A bipartite layout algorithm.
	 * 
	 * @param thread                      the isolate thread
	 * @param graphHandle                 the graph
	 * @param modelHandle                 the {@link LayoutModel2D} model
	 * @param partitionHandle             a possible null left partition of the
	 *                                    bipartite graph
	 * @param vertexComparatorFunctionPtr a possible null vertex comparator
	 * @param vertical                    whether to draw vertically or not
	 * @return the return status
	 */
	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.INTINT
			+ "drawing_exec_median_greedy_two_layered_bipartite_layout_2d", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeMedianGreedyTwoLayeredBipartite(IsolateThread thread, ObjectHandle graphHandle,
			ObjectHandle modelHandle, ObjectHandle partitionHandle, IIToIFunctionPointer vertexComparatorFunctionPtr,
			boolean vertical) {
		Graph<Integer, Integer> graph = globalHandles.get(graphHandle);
		LayoutModel2D<Integer> model = globalHandles.get(modelHandle);
		Set<Integer> partition = globalHandles.get(partitionHandle);
		Comparator<Integer> vertexComparator = null;
		if (vertexComparatorFunctionPtr.isNonNull()) {
			vertexComparator = (a, b) -> vertexComparatorFunctionPtr.invoke(a, b);
		}
		new MedianGreedyTwoLayeredBipartiteLayout2D<Integer, Integer>(partition, vertexComparator, vertical)
				.layout(graph, model);
		return Status.STATUS_SUCCESS.getCValue();
	}

	/**
	 * A bipartite layout algorithm.
	 * 
	 * @param thread                      the isolate thread
	 * @param graphHandle                 the graph
	 * @param modelHandle                 the {@link LayoutModel2D} model
	 * @param partitionHandle             a possible null left partition of the
	 *                                    bipartite graph
	 * @param vertexComparatorFunctionPtr a possible null vertex comparator
	 * @param vertical                    whether to draw vertically or not
	 * @return the return status
	 */
	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.LONGLONG
			+ "drawing_exec_median_greedy_two_layered_bipartite_layout_2d", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeMedianGreedyTwoLayeredBipartite(IsolateThread thread, ObjectHandle graphHandle,
			ObjectHandle modelHandle, ObjectHandle partitionHandle, LLToIFunctionPointer vertexComparatorFunctionPtr,
			boolean vertical) {
		Graph<Long, Long> graph = globalHandles.get(graphHandle);
		LayoutModel2D<Long> model = globalHandles.get(modelHandle);
		Set<Long> partition = globalHandles.get(partitionHandle);
		Comparator<Long> vertexComparator = null;
		if (vertexComparatorFunctionPtr.isNonNull()) {
			vertexComparator = (a, b) -> vertexComparatorFunctionPtr.invoke(a, b);
		}
		new MedianGreedyTwoLayeredBipartiteLayout2D<Long, Long>(partition, vertexComparator, vertical).layout(graph,
				model);
		return Status.STATUS_SUCCESS.getCValue();
	}

}
