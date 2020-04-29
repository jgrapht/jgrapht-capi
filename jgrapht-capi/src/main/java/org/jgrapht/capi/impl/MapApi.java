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

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.graalvm.nativeimage.IsolateThread;
import org.graalvm.nativeimage.ObjectHandle;
import org.graalvm.nativeimage.ObjectHandles;
import org.graalvm.nativeimage.c.function.CEntryPoint;
import org.graalvm.nativeimage.c.type.CDoublePointer;
import org.graalvm.nativeimage.c.type.CIntPointer;
import org.graalvm.nativeimage.c.type.CLongPointer;
import org.graalvm.nativeimage.c.type.WordPointer;
import org.jgrapht.capi.Constants;
import org.jgrapht.capi.JGraphTContext.Status;
import org.jgrapht.capi.error.StatusReturnExceptionHandler;

public class MapApi {

	private static ObjectHandles globalHandles = ObjectHandles.getGlobal();

	@CEntryPoint(name = Constants.LIB_PREFIX + "map_create", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int createMap(IsolateThread thread, WordPointer res) {
		if (res.isNonNull()) {
			res.write(globalHandles.create(new HashMap<>()));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "map_linked_create", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int createLinkedMap(IsolateThread thread, WordPointer res) {
		if (res.isNonNull()) {
			res.write(globalHandles.create(new LinkedHashMap<>()));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "map_keys_it_create", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int createMapKeysIterator(IsolateThread thread, ObjectHandle mapHandle, WordPointer res) {
		Map<?, ?> map = globalHandles.get(mapHandle);
		if (res.isNonNull()) {
			res.write(globalHandles.create(map.keySet().iterator()));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "map_size", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int mapSize(IsolateThread thread, ObjectHandle mapHandle, CLongPointer res) {
		Map<?, ?> map = globalHandles.get(mapHandle);
		if (res.isNonNull()) {
			res.write(map.size());
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "map_values_it_create", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int createMapValuesIterator(IsolateThread thread, ObjectHandle mapHandle, WordPointer res) {
		Map<?, ?> map = globalHandles.get(mapHandle);
		if (res.isNonNull()) {
			res.write(globalHandles.create(map.values().iterator()));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "map_long_double_put", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int mapLongDoublePut(IsolateThread thread, ObjectHandle mapHandle, long key, double value) {
		Map<Long, Double> map = globalHandles.get(mapHandle);
		map.put(key, value);
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "map_long_long_put", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int mapLongLongPut(IsolateThread thread, ObjectHandle mapHandle, long key, long value) {
		Map<Long, Long> map = globalHandles.get(mapHandle);
		map.put(key, value);
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "map_long_double_get", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int mapLongDoubleGet(IsolateThread thread, ObjectHandle mapHandle, long key, CDoublePointer res) {
		Map<Long, Double> map = globalHandles.get(mapHandle);
		Double value = map.get(key);
		if (value == null) {
			throw new IllegalArgumentException("Key " + key + " not in map");
		}
		if (res.isNonNull()) {
			res.write(value);
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "map_long_long_get", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int mapLongLongGet(IsolateThread thread, ObjectHandle mapHandle, long key, CLongPointer res) {
		Map<Long, Long> map = globalHandles.get(mapHandle);
		Long value = map.get(key);
		if (value == null) {
			throw new IllegalArgumentException("Key " + key + " not in map");
		}
		if (res.isNonNull()) {
			res.write(value);
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "map_long_contains_key", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int mapLongContains(IsolateThread thread, ObjectHandle mapHandle, long key, CIntPointer res) {
		Map<Long, ?> map = globalHandles.get(mapHandle);
		if (res.isNonNull()) {
			res.write(map.containsKey(key) ? 1 : 0);
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "map_long_double_remove", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int mapLongDoubleRemove(IsolateThread thread, ObjectHandle mapHandle, long key, CDoublePointer res) {
		Map<Long, Double> map = globalHandles.get(mapHandle);
		Double value = map.remove(key);
		if (value == null) {
			throw new IllegalArgumentException("Key " + key + " not in map");
		}
		if (res.isNonNull()) {
			res.write(value);
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "map_long_long_remove", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int mapLongLongRemove(IsolateThread thread, ObjectHandle mapHandle, long key, CLongPointer res) {
		Map<Long, Long> map = globalHandles.get(mapHandle);
		Long value = map.remove(key);
		if (value == null) {
			throw new IllegalArgumentException("Key " + key + " not in map");
		}
		if (res.isNonNull()) {
			res.write(value);
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "map_clear", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int clearMap(IsolateThread thread, ObjectHandle mapHandle) {
		Map<?, ?> map = globalHandles.get(mapHandle);
		map.clear();
		return Status.STATUS_SUCCESS.getCValue();
	}

}
