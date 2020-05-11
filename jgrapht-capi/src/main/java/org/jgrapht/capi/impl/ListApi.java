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

import java.util.ArrayList;
import java.util.List;

import org.graalvm.nativeimage.IsolateThread;
import org.graalvm.nativeimage.ObjectHandle;
import org.graalvm.nativeimage.ObjectHandles;
import org.graalvm.nativeimage.c.function.CEntryPoint;
import org.graalvm.nativeimage.c.type.CIntPointer;
import org.graalvm.nativeimage.c.type.WordPointer;
import org.jgrapht.alg.util.Pair;
import org.jgrapht.alg.util.Triple;
import org.jgrapht.capi.Constants;
import org.jgrapht.capi.JGraphTContext.Status;
import org.jgrapht.capi.error.StatusReturnExceptionHandler;

public class ListApi {

	private static ObjectHandles globalHandles = ObjectHandles.getGlobal();

	@CEntryPoint(name = Constants.LIB_PREFIX + "list_create", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int createList(IsolateThread thread, WordPointer res) {
		if (res.isNonNull()) {
			res.write(globalHandles.create(new ArrayList<>()));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "list_it_create", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int createListIterator(IsolateThread thread, ObjectHandle handle, WordPointer res) {
		List<?> list = globalHandles.get(handle);
		if (res.isNonNull()) {
			res.write(globalHandles.create(list.iterator()));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "list_size", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int listSize(IsolateThread thread, ObjectHandle handle, CIntPointer res) {
		List<?> list = globalHandles.get(handle);
		if (res.isNonNull()) {
			res.write(list.size());
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "list_int_add", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int listIntegerAdd(IsolateThread thread, ObjectHandle handle, int value, CIntPointer res) {
		List<Integer> list = globalHandles.get(handle);
		boolean result = list.add(value);
		if (res.isNonNull()) {
			res.write(result ? 1 : 0);
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "list_double_add", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int listDoubleAdd(IsolateThread thread, ObjectHandle handle, double value, CIntPointer res) {
		List<Double> list = globalHandles.get(handle);
		boolean result = list.add(value);
		if (res.isNonNull()) {
			res.write(result ? 1 : 0);
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "list_edge_pair_add", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int listEdgePairAdd(IsolateThread thread, ObjectHandle handle, int source, int target,
			CIntPointer res) {
		List<Pair<Integer, Integer>> list = globalHandles.get(handle);
		boolean result = list.add(Pair.of(source, target));
		if (res.isNonNull()) {
			res.write(result ? 1 : 0);
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "list_edge_triple_add", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int listEdgeTripleAdd(IsolateThread thread, ObjectHandle handle, int source, int target,
			double weight, CIntPointer res) {
		List<Triple<Integer, Integer, Double>> list = globalHandles.get(handle);
		boolean result = list.add(Triple.of(source, target, weight));
		if (res.isNonNull()) {
			res.write(result ? 1 : 0);
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "list_int_remove", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int listIntegerRemove(IsolateThread thread, ObjectHandle handle, int value) {
		List<Integer> list = globalHandles.get(handle);
		Integer objectToRemove = value;
		list.remove(objectToRemove);
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "list_double_remove", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int listDoubleRemove(IsolateThread thread, ObjectHandle handle, double value) {
		List<Double> list = globalHandles.get(handle);
		list.remove(value);
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "list_int_contains", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int listIntegerContains(IsolateThread thread, ObjectHandle handle, int value, CIntPointer res) {
		List<Integer> list = globalHandles.get(handle);
		boolean result = list.contains(value);
		if (res.isNonNull()) {
			res.write(result ? 1 : 0);
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "list_double_contains", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int listDoubleContains(IsolateThread thread, ObjectHandle handle, double value, CIntPointer res) {
		List<Double> list = globalHandles.get(handle);
		boolean result = list.contains(value);
		if (res.isNonNull()) {
			res.write(result ? 1 : 0);
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "list_clear", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int clearList(IsolateThread thread, ObjectHandle handle) {
		List<?> list = globalHandles.get(handle);
		list.clear();
		return Status.STATUS_SUCCESS.getCValue();
	}

}
