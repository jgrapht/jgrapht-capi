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
import org.graalvm.nativeimage.c.type.CDoublePointer;
import org.graalvm.nativeimage.c.type.WordPointer;
import org.jgrapht.Graph;
import org.jgrapht.alg.interfaces.SpannerAlgorithm;
import org.jgrapht.alg.interfaces.SpannerAlgorithm.Spanner;
import org.jgrapht.alg.spanning.GreedyMultiplicativeSpanner;
import org.jgrapht.capi.Constants;
import org.jgrapht.capi.JGraphTContext.Status;
import org.jgrapht.capi.error.StatusReturnExceptionHandler;

public class SpannerApi {

	private static ObjectHandles globalHandles = ObjectHandles.getGlobal();

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "spanner_exec_greedy_multiplicative", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeGreedyMultiplicativeSpanner(IsolateThread thread, ObjectHandle graph, int k,
			CDoublePointer weightRes, WordPointer res) {
		Graph<Integer, Integer> g = globalHandles.get(graph);
		SpannerAlgorithm<Integer> spannerAlg = new GreedyMultiplicativeSpanner<>(g, k);
		Spanner<Integer> spanner = spannerAlg.getSpanner();
		double weight = spanner.getWeight();
		if (weightRes.isNonNull()) {
			weightRes.write(weight);
		}
		if (res.isNonNull()) {
			res.write(globalHandles.create(spanner));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

}
