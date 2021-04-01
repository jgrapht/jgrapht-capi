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
import java.util.Set;

import org.graalvm.nativeimage.IsolateThread;
import org.graalvm.nativeimage.ObjectHandle;
import org.graalvm.nativeimage.ObjectHandles;
import org.graalvm.nativeimage.c.function.CEntryPoint;
import org.graalvm.nativeimage.c.type.CIntPointer;
import org.graalvm.nativeimage.c.type.WordPointer;
import org.jgrapht.Graph;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.alg.connectivity.GabowStrongConnectivityInspector;
import org.jgrapht.alg.connectivity.KosarajuStrongConnectivityInspector;
import org.jgrapht.capi.Constants;
import org.jgrapht.capi.JGraphTContext.Status;
import org.jgrapht.capi.error.StatusReturnExceptionHandler;

public class ConnectivityApi {

	private static ObjectHandles globalHandles = ObjectHandles.getGlobal();

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.ANY_ANY
			+ "connectivity_strong_exec_kosaraju", exceptionHandler = StatusReturnExceptionHandler.class)
	public static <V,E> int executeKosaraju(IsolateThread thread, ObjectHandle graphHandle, CIntPointer valueRes,
			WordPointer res) {
		Graph<V, E> g = globalHandles.get(graphHandle);

		KosarajuStrongConnectivityInspector<V, E> alg = new KosarajuStrongConnectivityInspector<>(g);

		boolean result = alg.isStronglyConnected();
		List<Set<V>> connectedSets = alg.stronglyConnectedSets();

		if (valueRes.isNonNull()) {
			valueRes.write(result ? 1 : 0);
		}
		if (res.isNonNull()) {
			res.write(globalHandles.create(connectedSets.iterator()));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.ANY_ANY
			+ "connectivity_strong_exec_gabow", exceptionHandler = StatusReturnExceptionHandler.class)
	public static <V,E> int executeGabow(IsolateThread thread, ObjectHandle graphHandle, CIntPointer valueRes,
			WordPointer res) {
		Graph<V, E> g = globalHandles.get(graphHandle);

		GabowStrongConnectivityInspector<V, E> alg = new GabowStrongConnectivityInspector<>(g);

		boolean result = alg.isStronglyConnected();
		List<Set<V>> connectedSets = alg.stronglyConnectedSets();

		if (valueRes.isNonNull()) {
			valueRes.write(result ? 1 : 0);
		}
		if (res.isNonNull()) {
			res.write(globalHandles.create(connectedSets.iterator()));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.ANY_ANY
			+ "connectivity_weak_exec_bfs", exceptionHandler = StatusReturnExceptionHandler.class)
	public static <V,E> int executeWeakBfs(IsolateThread thread, ObjectHandle graphHandle, CIntPointer valueRes,
			WordPointer res) {
		Graph<V, E> g = globalHandles.get(graphHandle);

		ConnectivityInspector<V, E> alg = new ConnectivityInspector<>(g);

		boolean result = alg.isConnected();
		List<Set<V>> connectedSets = alg.connectedSets();

		if (valueRes.isNonNull()) {
			valueRes.write(result ? 1 : 0);
		}
		if (res.isNonNull()) {
			res.write(globalHandles.create(connectedSets.iterator()));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

}
