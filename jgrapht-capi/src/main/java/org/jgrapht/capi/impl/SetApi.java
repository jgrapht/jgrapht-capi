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

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import org.graalvm.nativeimage.IsolateThread;
import org.graalvm.nativeimage.ObjectHandle;
import org.graalvm.nativeimage.ObjectHandles;
import org.graalvm.nativeimage.c.function.CEntryPoint;
import org.graalvm.nativeimage.c.type.CIntPointer;
import org.graalvm.nativeimage.c.type.WordPointer;
import org.graalvm.word.PointerBase;
import org.jgrapht.capi.Constants;
import org.jgrapht.capi.JGraphTContext.Status;
import org.jgrapht.capi.error.StatusReturnExceptionHandler;
import org.jgrapht.capi.graph.ExternalRef;
import org.jgrapht.capi.graph.HashAndEqualsResolver;

public class SetApi {

	private static ObjectHandles globalHandles = ObjectHandles.getGlobal();

	@CEntryPoint(name = Constants.LIB_PREFIX + "set_create", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int createSet(IsolateThread thread, WordPointer res) {
		if (res.isNonNull()) {
			res.write(globalHandles.create(new HashSet<>()));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "set_linked_create", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int createLinkedSet(IsolateThread thread, WordPointer res) {
		if (res.isNonNull()) {
			res.write(globalHandles.create(new LinkedHashSet<>()));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "set_it_create", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int createMapKeysIterator(IsolateThread thread, ObjectHandle handle, WordPointer res) {
		Set<?> set = globalHandles.get(handle);
		if (res.isNonNull()) {
			res.write(globalHandles.create(set.iterator()));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "set_size", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int setSize(IsolateThread thread, ObjectHandle handle, CIntPointer res) {
		Set<?> set = globalHandles.get(handle);
		if (res.isNonNull()) {
			res.write(set.size());
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "set_int_add", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int setIntAdd(IsolateThread thread, ObjectHandle handle, int value, CIntPointer res) {
		Set<Integer> set = globalHandles.get(handle);
		boolean result = set.add(value);
		if (res.isNonNull()) {
			res.write(result ? 1 : 0);
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "set_long_add", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int setLongAdd(IsolateThread thread, ObjectHandle handle, long value, CIntPointer res) {
		Set<Long> set = globalHandles.get(handle);
		boolean result = set.add(value);
		if (res.isNonNull()) {
			res.write(result ? 1 : 0);
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "set_double_add", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int setDoubleAdd(IsolateThread thread, ObjectHandle handle, double value, CIntPointer res) {
		Set<Double> set = globalHandles.get(handle);
		boolean result = set.add(value);
		if (res.isNonNull()) {
			res.write(result ? 1 : 0);
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "set_ref_add", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int setRefAdd(IsolateThread thread, ObjectHandle handle, ObjectHandle refHandle, CIntPointer res) {
		Set<ExternalRef> set = globalHandles.get(handle);
		ExternalRef ref = globalHandles.get(refHandle);
		boolean result = set.add(ref);
		if (res.isNonNull()) {
			res.write(result ? 1 : 0);
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "set_ref_add_direct", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int setRefAdd(IsolateThread thread, ObjectHandle handle, PointerBase refPtr,
			ObjectHandle hashEqualsResolverHandle, CIntPointer res) {
		Set<ExternalRef> set = globalHandles.get(handle);
		HashAndEqualsResolver resolver = globalHandles.get(hashEqualsResolverHandle);
		ExternalRef ref = resolver.toExternalRef(refPtr);
		boolean result = set.add(ref);
		if (res.isNonNull()) {
			res.write(result ? 1 : 0);
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "set_int_remove", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int setIntRemove(IsolateThread thread, ObjectHandle handle, int value) {
		Set<Integer> set = globalHandles.get(handle);
		set.remove(value);
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "set_long_remove", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int setLongRemove(IsolateThread thread, ObjectHandle handle, long value) {
		Set<Long> set = globalHandles.get(handle);
		set.remove(value);
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "set_double_remove", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int setDoubleRemove(IsolateThread thread, ObjectHandle handle, double value) {
		Set<Double> set = globalHandles.get(handle);
		set.remove(value);
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "set_ref_remove", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int setRefRemove(IsolateThread thread, ObjectHandle handle, ObjectHandle refHandle) {
		Set<ExternalRef> set = globalHandles.get(handle);
		ExternalRef ref = globalHandles.get(refHandle);
		set.remove(ref);
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "set_ref_remove_direct", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int setRefRemove(IsolateThread thread, ObjectHandle handle, PointerBase refPtr,
			ObjectHandle hashEqualsResolverHandle) {
		Set<ExternalRef> set = globalHandles.get(handle);
		HashAndEqualsResolver resolver = globalHandles.get(hashEqualsResolverHandle);
		ExternalRef ref = resolver.toExternalRef(refPtr);
		set.remove(ref);
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "set_int_contains", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int setIntContains(IsolateThread thread, ObjectHandle handle, int value, CIntPointer res) {
		Set<Integer> set = globalHandles.get(handle);
		boolean result = set.contains(value);
		if (res.isNonNull()) {
			res.write(result ? 1 : 0);
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "set_long_contains", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int setLongContains(IsolateThread thread, ObjectHandle handle, long value, CIntPointer res) {
		Set<Long> set = globalHandles.get(handle);
		boolean result = set.contains(value);
		if (res.isNonNull()) {
			res.write(result ? 1 : 0);
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "set_double_contains", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int setDoubleContains(IsolateThread thread, ObjectHandle handle, double value, CIntPointer res) {
		Set<Double> set = globalHandles.get(handle);
		boolean result = set.contains(value);
		if (res.isNonNull()) {
			res.write(result ? 1 : 0);
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "set_ref_contains", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int setRefContains(IsolateThread thread, ObjectHandle handle, ObjectHandle refHandle,
			CIntPointer res) {
		Set<ExternalRef> set = globalHandles.get(handle);
		ExternalRef ref = globalHandles.get(refHandle);
		boolean result = set.contains(ref);
		if (res.isNonNull()) {
			res.write(result ? 1 : 0);
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "set_ref_contains_direct", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int setRefContainsDirect(IsolateThread thread, ObjectHandle handle, PointerBase refPtr,
			ObjectHandle hashEqualsResolverHandle, CIntPointer res) {
		Set<ExternalRef> set = globalHandles.get(handle);
		HashAndEqualsResolver resolver = globalHandles.get(hashEqualsResolverHandle);
		ExternalRef ref = resolver.toExternalRef(refPtr);
		boolean result = set.contains(ref);
		if (res.isNonNull()) {
			res.write(result ? 1 : 0);
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "set_clear", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int clearSet(IsolateThread thread, ObjectHandle handle) {
		Set<?> set = globalHandles.get(handle);
		set.clear();
		return Status.STATUS_SUCCESS.getCValue();
	}

}
