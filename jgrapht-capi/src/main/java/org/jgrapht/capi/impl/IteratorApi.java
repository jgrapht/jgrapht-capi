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

import org.graalvm.nativeimage.IsolateThread;
import org.graalvm.nativeimage.ObjectHandle;
import org.graalvm.nativeimage.ObjectHandles;
import org.graalvm.nativeimage.c.function.CEntryPoint;
import org.graalvm.nativeimage.c.type.CCharPointerPointer;
import org.graalvm.nativeimage.c.type.CDoublePointer;
import org.graalvm.nativeimage.c.type.CIntPointer;
import org.graalvm.nativeimage.c.type.CTypeConversion.CCharPointerHolder;
import org.graalvm.nativeimage.c.type.WordPointer;
import org.jgrapht.Graph;
import org.jgrapht.alg.util.Triple;
import org.jgrapht.capi.Constants;
import org.jgrapht.capi.JGraphTContext.Status;
import org.jgrapht.capi.error.StatusReturnExceptionHandler;

public class IteratorApi {

	private static ObjectHandles globalHandles = ObjectHandles.getGlobal();

	@CEntryPoint(name = Constants.LIB_PREFIX + "it_next_int", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int iteratorNextInt(IsolateThread thread, ObjectHandle itHandle, CIntPointer res) {
		Iterator<Integer> it = globalHandles.get(itHandle);
		if (res.isNonNull()) {
			res.write(it.next());
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "it_next_double", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int iteratorNextDouble(IsolateThread thread, ObjectHandle itHandle, CDoublePointer res) {
		Iterator<Double> it = globalHandles.get(itHandle);
		if (res.isNonNull()) {
			res.write(it.next());
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "it_next_edge_triple", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int iteratorNextEdgeTriple(IsolateThread thread, ObjectHandle itHandle, CIntPointer source,
			CIntPointer target, CDoublePointer weight) {
		Iterator<Triple<Integer, Integer, Double>> it = globalHandles.get(itHandle);
		Triple<Integer, Integer, Double> triple = it.next();
		if (source.isNonNull()) {
			source.write(triple.getFirst());
		}
		if (target.isNonNull()) {
			target.write(triple.getSecond());
		}
		if (weight.isNonNull()) {
			Double edgeWeight = triple.getThird();
			if (edgeWeight == null) { 
				edgeWeight = Graph.DEFAULT_EDGE_WEIGHT;
			}
			weight.write(edgeWeight);
		}
		return Status.STATUS_SUCCESS.getCValue();
	}
	
	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "it_next_str_edge_triple", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int iteratorNextStrEdgeTriple(IsolateThread thread, ObjectHandle itHandle, CCharPointerPointer source,
			CCharPointerPointer target, CDoublePointer weight) {
		Iterator<Triple<CCharPointerHolder, CCharPointerHolder, Double>> it = globalHandles.get(itHandle);
		Triple<CCharPointerHolder, CCharPointerHolder, Double> triple = it.next();
		if (source.isNonNull()) {
			source.write(triple.getFirst().get());
		}
		if (target.isNonNull()) {
			target.write(triple.getSecond().get());
		}
		if (weight.isNonNull()) {
			Double edgeWeight = triple.getThird();
			if (edgeWeight == null) { 
				edgeWeight = Graph.DEFAULT_EDGE_WEIGHT;
			}
			weight.write(edgeWeight);
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "it_next_object", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int iteratorNextObject(IsolateThread thread, ObjectHandle itHandle, WordPointer res) {
		Iterator<?> it = globalHandles.get(itHandle);
		Object o = it.next();
		if (res.isNonNull()) {
			res.write(globalHandles.create(o));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "it_hasnext", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int iteratorHasNext(IsolateThread thread, ObjectHandle itHandle, CIntPointer res) {
		Iterator<?> it = globalHandles.get(itHandle);
		if (res.isNonNull()) {
			res.write(it.hasNext() ? 1 : 0);
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

}
