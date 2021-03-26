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
import org.graalvm.nativeimage.c.type.CDoublePointer;
import org.jgrapht.Graph;
import org.jgrapht.alg.linkprediction.AdamicAdarIndexLinkPrediction;
import org.jgrapht.alg.linkprediction.CommonNeighborsLinkPrediction;
import org.jgrapht.alg.linkprediction.HubDepressedIndexLinkPrediction;
import org.jgrapht.alg.linkprediction.HubPromotedIndexLinkPrediction;
import org.jgrapht.alg.linkprediction.JaccardCoefficientLinkPrediction;
import org.jgrapht.alg.linkprediction.LeichtHolmeNewmanIndexLinkPrediction;
import org.jgrapht.alg.linkprediction.PreferentialAttachmentLinkPrediction;
import org.jgrapht.alg.linkprediction.ResourceAllocationIndexLinkPrediction;
import org.jgrapht.alg.linkprediction.SaltonIndexLinkPrediction;
import org.jgrapht.alg.linkprediction.SørensenIndexLinkPrediction;
import org.jgrapht.capi.Constants;
import org.jgrapht.capi.JGraphTContext.Status;
import org.jgrapht.capi.error.StatusReturnExceptionHandler;

public class LinkPredictionApi {

	private static ObjectHandles globalHandles = ObjectHandles.getGlobal();

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.INTANY
			+ "link_prediction_exec_adamic_adar_index", exceptionHandler = StatusReturnExceptionHandler.class)
	public static <E> int executeAdamicAdarIndex(IsolateThread thread, ObjectHandle graphHandle, int u, int v,
			CDoublePointer res) {
		Graph<Integer, E> g = globalHandles.get(graphHandle);

		double score = new AdamicAdarIndexLinkPrediction<>(g).predict(u, v);
		if (res.isNonNull()) {
			res.write(score);
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.LONGANY
			+ "link_prediction_exec_adamic_adar_index", exceptionHandler = StatusReturnExceptionHandler.class)
	public static <E> int executeAdamicAdarIndex(IsolateThread thread, ObjectHandle graphHandle, long u, long v,
			CDoublePointer res) {
		Graph<Long, E> g = globalHandles.get(graphHandle);

		double score = new AdamicAdarIndexLinkPrediction<>(g).predict(u, v);
		if (res.isNonNull()) {
			res.write(score);
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.INTANY
			+ "link_prediction_exec_common_neighbors", exceptionHandler = StatusReturnExceptionHandler.class)
	public static <E> int executeCommonNeighbors(IsolateThread thread, ObjectHandle graphHandle, int u, int v,
			CDoublePointer res) {
		Graph<Integer, E> g = globalHandles.get(graphHandle);

		double score = new CommonNeighborsLinkPrediction<>(g).predict(u, v);
		if (res.isNonNull()) {
			res.write(score);
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.LONGANY
			+ "link_prediction_exec_common_neighbors", exceptionHandler = StatusReturnExceptionHandler.class)
	public static <E> int executeCommonNeighbors(IsolateThread thread, ObjectHandle graphHandle, long u, long v,
			CDoublePointer res) {
		Graph<Long, E> g = globalHandles.get(graphHandle);

		double score = new CommonNeighborsLinkPrediction<>(g).predict(u, v);
		if (res.isNonNull()) {
			res.write(score);
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.INTANY
			+ "link_prediction_exec_hub_depressed_index", exceptionHandler = StatusReturnExceptionHandler.class)
	public static <E> int executeHubDepressedIndexLinkPrediction(IsolateThread thread, ObjectHandle graphHandle, int u,
			int v, CDoublePointer res) {
		Graph<Integer, E> g = globalHandles.get(graphHandle);

		double score = new HubDepressedIndexLinkPrediction<>(g).predict(u, v);
		if (res.isNonNull()) {
			res.write(score);
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.LONGANY
			+ "link_prediction_exec_hub_depressed_index", exceptionHandler = StatusReturnExceptionHandler.class)
	public static <E> int executeexecuteHubDepressedIndexLinkPrediction(IsolateThread thread, ObjectHandle graphHandle,
			long u, long v, CDoublePointer res) {
		Graph<Long, E> g = globalHandles.get(graphHandle);

		double score = new HubDepressedIndexLinkPrediction<>(g).predict(u, v);
		if (res.isNonNull()) {
			res.write(score);
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.INTANY
			+ "link_prediction_exec_hub_promoted_index", exceptionHandler = StatusReturnExceptionHandler.class)
	public static <E> int executeHubPromotedIndexLinkPrediction(IsolateThread thread, ObjectHandle graphHandle, int u,
			int v, CDoublePointer res) {
		Graph<Integer, E> g = globalHandles.get(graphHandle);

		double score = new HubPromotedIndexLinkPrediction<>(g).predict(u, v);
		if (res.isNonNull()) {
			res.write(score);
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.LONGANY
			+ "link_prediction_exec_hub_promoted_index", exceptionHandler = StatusReturnExceptionHandler.class)
	public static <E> int executeexecuteHubPromotedIndexLinkPrediction(IsolateThread thread, ObjectHandle graphHandle,
			long u, long v, CDoublePointer res) {
		Graph<Long, E> g = globalHandles.get(graphHandle);

		double score = new HubPromotedIndexLinkPrediction<>(g).predict(u, v);
		if (res.isNonNull()) {
			res.write(score);
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.INTANY
			+ "link_prediction_exec_jaccard_coefficient", exceptionHandler = StatusReturnExceptionHandler.class)
	public static <E> int executeJaccardCoefficientLinkPrediction(IsolateThread thread, ObjectHandle graphHandle, int u,
			int v, CDoublePointer res) {
		Graph<Integer, E> g = globalHandles.get(graphHandle);

		double score = new JaccardCoefficientLinkPrediction<>(g).predict(u, v);
		if (res.isNonNull()) {
			res.write(score);
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.LONGANY
			+ "link_prediction_exec_jaccard_coefficient", exceptionHandler = StatusReturnExceptionHandler.class)
	public static <E> int executeJaccardCoefficientLinkPrediction(IsolateThread thread, ObjectHandle graphHandle,
			long u, long v, CDoublePointer res) {
		Graph<Long, E> g = globalHandles.get(graphHandle);

		double score = new JaccardCoefficientLinkPrediction<>(g).predict(u, v);
		if (res.isNonNull()) {
			res.write(score);
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.INTANY
			+ "link_prediction_exec_leicht_holme_newman_index", exceptionHandler = StatusReturnExceptionHandler.class)
	public static <E> int executeLeichtHolmeNewmanIndexLinkPrediction(IsolateThread thread, ObjectHandle graphHandle,
			int u, int v, CDoublePointer res) {
		Graph<Integer, E> g = globalHandles.get(graphHandle);

		double score = new LeichtHolmeNewmanIndexLinkPrediction<>(g).predict(u, v);
		if (res.isNonNull()) {
			res.write(score);
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.LONGANY
			+ "link_prediction_exec_leicht_holme_newman_index", exceptionHandler = StatusReturnExceptionHandler.class)
	public static <E> int executeLeichtHolmeNewmanIndexLinkPrediction(IsolateThread thread, ObjectHandle graphHandle,
			long u, long v, CDoublePointer res) {
		Graph<Long, E> g = globalHandles.get(graphHandle);

		double score = new LeichtHolmeNewmanIndexLinkPrediction<>(g).predict(u, v);
		if (res.isNonNull()) {
			res.write(score);
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.INTANY
			+ "link_prediction_exec_preferential_attachment", exceptionHandler = StatusReturnExceptionHandler.class)
	public static <E> int executePreferentialAttachmentLinkPrediction(IsolateThread thread, ObjectHandle graphHandle,
			int u, int v, CDoublePointer res) {
		Graph<Integer, E> g = globalHandles.get(graphHandle);

		double score = new PreferentialAttachmentLinkPrediction<>(g).predict(u, v);
		if (res.isNonNull()) {
			res.write(score);
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.LONGANY
			+ "link_prediction_exec_preferential_attachment", exceptionHandler = StatusReturnExceptionHandler.class)
	public static <E> int executePreferentialAttachmentLinkPrediction(IsolateThread thread, ObjectHandle graphHandle,
			long u, long v, CDoublePointer res) {
		Graph<Long, E> g = globalHandles.get(graphHandle);

		double score = new PreferentialAttachmentLinkPrediction<>(g).predict(u, v);
		if (res.isNonNull()) {
			res.write(score);
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.INTANY
			+ "link_prediction_exec_resource_allocation_index", exceptionHandler = StatusReturnExceptionHandler.class)
	public static <E> int executeResourceAllocationIndexLinkPrediction(IsolateThread thread, ObjectHandle graphHandle,
			int u, int v, CDoublePointer res) {
		Graph<Integer, E> g = globalHandles.get(graphHandle);

		double score = new ResourceAllocationIndexLinkPrediction<>(g).predict(u, v);
		if (res.isNonNull()) {
			res.write(score);
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.LONGANY
			+ "link_prediction_exec_resource_allocation_index", exceptionHandler = StatusReturnExceptionHandler.class)
	public static <E> int executeResourceAllocationIndexLinkPrediction(IsolateThread thread, ObjectHandle graphHandle,
			long u, long v, CDoublePointer res) {
		Graph<Long, E> g = globalHandles.get(graphHandle);

		double score = new ResourceAllocationIndexLinkPrediction<>(g).predict(u, v);
		if (res.isNonNull()) {
			res.write(score);
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.INTANY
			+ "link_prediction_exec_salton_index", exceptionHandler = StatusReturnExceptionHandler.class)
	public static <E> int executeSaltonIndexLinkPrediction(IsolateThread thread, ObjectHandle graphHandle, int u, int v,
			CDoublePointer res) {
		Graph<Integer, E> g = globalHandles.get(graphHandle);

		double score = new SaltonIndexLinkPrediction<>(g).predict(u, v);
		if (res.isNonNull()) {
			res.write(score);
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.LONGANY
			+ "link_prediction_exec_salton_index", exceptionHandler = StatusReturnExceptionHandler.class)
	public static <E> int executeSaltonIndexLinkPrediction(IsolateThread thread, ObjectHandle graphHandle, long u,
			long v, CDoublePointer res) {
		Graph<Long, E> g = globalHandles.get(graphHandle);

		double score = new SaltonIndexLinkPrediction<>(g).predict(u, v);
		if (res.isNonNull()) {
			res.write(score);
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.INTANY
			+ "link_prediction_exec_sorensen_index", exceptionHandler = StatusReturnExceptionHandler.class)
	public static <E> int executeSørensenIndexLinkPrediction(IsolateThread thread, ObjectHandle graphHandle, int u,
			int v, CDoublePointer res) {
		Graph<Integer, E> g = globalHandles.get(graphHandle);

		double score = new SørensenIndexLinkPrediction<>(g).predict(u, v);
		if (res.isNonNull()) {
			res.write(score);
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.LONGANY
			+ "link_prediction_exec_sorensen_index", exceptionHandler = StatusReturnExceptionHandler.class)
	public static <E> int executeSørensenIndexLinkPrediction(IsolateThread thread, ObjectHandle graphHandle, long u,
			long v, CDoublePointer res) {
		Graph<Long, E> g = globalHandles.get(graphHandle);

		double score = new SørensenIndexLinkPrediction<>(g).predict(u, v);
		if (res.isNonNull()) {
			res.write(score);
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

}
