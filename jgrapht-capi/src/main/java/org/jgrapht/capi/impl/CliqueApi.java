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

import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.graalvm.nativeimage.IsolateThread;
import org.graalvm.nativeimage.ObjectHandle;
import org.graalvm.nativeimage.ObjectHandles;
import org.graalvm.nativeimage.c.function.CEntryPoint;
import org.graalvm.nativeimage.c.type.WordPointer;
import org.jgrapht.Graph;
import org.jgrapht.alg.clique.BronKerboschCliqueFinder;
import org.jgrapht.alg.clique.ChordalGraphMaxCliqueFinder;
import org.jgrapht.alg.clique.DegeneracyBronKerboschCliqueFinder;
import org.jgrapht.alg.clique.PivotBronKerboschCliqueFinder;
import org.jgrapht.alg.interfaces.MaximalCliqueEnumerationAlgorithm;
import org.jgrapht.capi.Constants;
import org.jgrapht.capi.JGraphTContext.Status;
import org.jgrapht.capi.error.StatusReturnExceptionHandler;

public class CliqueApi {

	private static ObjectHandles globalHandles = ObjectHandles.getGlobal();

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "clique_exec_bron_kerbosch", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeBrownKerbosch(IsolateThread thread, ObjectHandle graphHandle, long timeoutSeconds,
			WordPointer res) {
		Graph<Integer, Integer> g = globalHandles.get(graphHandle);
		MaximalCliqueEnumerationAlgorithm<Integer, Integer> alg = new BronKerboschCliqueFinder<>(g, timeoutSeconds,
				TimeUnit.SECONDS);
		Iterator<Set<Integer>> it = alg.iterator();
		if (res.isNonNull()) {
			res.write(globalHandles.create(it));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "clique_exec_bron_kerbosch_pivot", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeBrownKerboschWithPivot(IsolateThread thread, ObjectHandle graphHandle, long timeoutSeconds,
			WordPointer res) {
		Graph<Integer, Integer> g = globalHandles.get(graphHandle);
		MaximalCliqueEnumerationAlgorithm<Integer, Integer> alg = new PivotBronKerboschCliqueFinder<>(g, timeoutSeconds,
				TimeUnit.SECONDS);
		Iterator<Set<Integer>> it = alg.iterator();
		if (res.isNonNull()) {
			res.write(globalHandles.create(it));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "clique_exec_bron_kerbosch_pivot_degeneracy_ordering", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeBrownKerboschPivotAndDegeneracyOrdering(IsolateThread thread, ObjectHandle graphHandle,
			long timeoutSeconds, WordPointer res) {
		Graph<Integer, Integer> g = globalHandles.get(graphHandle);
		MaximalCliqueEnumerationAlgorithm<Integer, Integer> alg = new DegeneracyBronKerboschCliqueFinder<>(g,
				timeoutSeconds, TimeUnit.SECONDS);
		Iterator<Set<Integer>> it = alg.iterator();
		if (res.isNonNull()) {
			res.write(globalHandles.create(it));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "clique_exec_chordal_max_clique", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeChordalMaxCliqueFinder(IsolateThread thread, ObjectHandle graphHandle, WordPointer res) {
		Graph<Integer, Integer> g = globalHandles.get(graphHandle);
		ChordalGraphMaxCliqueFinder<Integer, Integer> alg = new ChordalGraphMaxCliqueFinder<>(g);
		Set<Integer> clique = alg.getClique();
		if (clique == null) {
			throw new IllegalArgumentException("Graph is not chordal");
		}
		if (res.isNonNull()) {
			res.write(globalHandles.create(clique));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

}
