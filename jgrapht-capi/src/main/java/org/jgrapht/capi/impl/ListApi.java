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
import org.graalvm.nativeimage.c.type.CLongPointer;
import org.graalvm.nativeimage.c.type.WordPointer;
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
	public static int listSize(IsolateThread thread, ObjectHandle handle, CLongPointer res) {
		List<?> list = globalHandles.get(handle);
		if (res.isNonNull()) {
			res.write(list.size());
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "list_long_add", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int listLongAdd(IsolateThread thread, ObjectHandle handle, long value, CIntPointer res) {
		List<Long> list = globalHandles.get(handle);
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
			+ "list_long_remove", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int listLongRemove(IsolateThread thread, ObjectHandle handle, long value) {
		List<Long> list = globalHandles.get(handle);
		list.remove(value);
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
			+ "list_long_contains", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int listLongContains(IsolateThread thread, ObjectHandle handle, long value, CIntPointer res) {
		List<Long> list = globalHandles.get(handle);
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
