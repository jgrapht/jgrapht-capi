package org.jgrapht.capi.impl;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.graalvm.nativeimage.IsolateThread;
import org.graalvm.nativeimage.ObjectHandle;
import org.graalvm.nativeimage.ObjectHandles;
import org.graalvm.nativeimage.c.function.CEntryPoint;
import org.jgrapht.capi.Constants;
import org.jgrapht.capi.error.BooleanExceptionHandler;
import org.jgrapht.capi.error.DoubleExceptionHandler;
import org.jgrapht.capi.error.LongExceptionHandler;
import org.jgrapht.capi.error.ObjectHandleExceptionHandler;
import org.jgrapht.capi.error.VoidExceptionHandler;

public class MapAPI {

	private static ObjectHandles globalHandles = ObjectHandles.getGlobal();

	@CEntryPoint(name = Constants.LIB_PREFIX + "map_create", exceptionHandler = ObjectHandleExceptionHandler.class)
	public static ObjectHandle createMap(IsolateThread thread) {
		return globalHandles.create(new HashMap<>());
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "map_linked_create", exceptionHandler = ObjectHandleExceptionHandler.class)
	public static ObjectHandle createLinkedMap(IsolateThread thread) {
		return globalHandles.create(new LinkedHashMap<>());
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "map_keys_it_create", exceptionHandler = ObjectHandleExceptionHandler.class)
	public static ObjectHandle createMapKeysIterator(IsolateThread thread, ObjectHandle mapHandle) {
		Map<?, ?> map = globalHandles.get(mapHandle);
		return globalHandles.create(map.keySet().iterator());
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "map_size", exceptionHandler = LongExceptionHandler.class)
	public static long mapSize(IsolateThread thread, ObjectHandle mapHandle) {
		Map<?, ?> map = globalHandles.get(mapHandle);
		return map.size();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "map_values_it_create", exceptionHandler = ObjectHandleExceptionHandler.class)
	public static ObjectHandle createMapValuesIterator(IsolateThread thread, ObjectHandle mapHandle) {
		Map<?, ?> map = globalHandles.get(mapHandle);
		return globalHandles.create(map.values().iterator());
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "map_long_double_put", exceptionHandler = VoidExceptionHandler.class)
	public static void mapLongDoublePut(IsolateThread thread, ObjectHandle mapHandle, long key, double value) {
		Map<Long, Double> map = globalHandles.get(mapHandle);
		map.put(key, value);
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "map_long_long_put", exceptionHandler = VoidExceptionHandler.class)
	public static void mapLongLongPut(IsolateThread thread, ObjectHandle mapHandle, long key, long value) {
		Map<Long, Long> map = globalHandles.get(mapHandle);
		map.put(key, value);
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "map_long_double_get", exceptionHandler = DoubleExceptionHandler.class)
	public static double mapLongDoubleGet(IsolateThread thread, ObjectHandle mapHandle, long key) {
		Map<Long, Double> map = globalHandles.get(mapHandle);
		Double value = map.get(key);
		if (value == null) {
			throw new IllegalArgumentException("Key " + key + " not in map");
		}
		return value;
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "map_long_long_get", exceptionHandler = LongExceptionHandler.class)
	public static long mapLongLongGet(IsolateThread thread, ObjectHandle mapHandle, long key) {
		Map<Long, Long> map = globalHandles.get(mapHandle);
		Long value = map.get(key);
		if (value == null) {
			throw new IllegalArgumentException("Key " + key + " not in map");
		}
		return value;
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "map_long_contains_key", exceptionHandler = BooleanExceptionHandler.class)
	public static boolean mapLongContains(IsolateThread thread, ObjectHandle mapHandle, long key) {
		Map<Long, ?> map = globalHandles.get(mapHandle);
		return map.containsKey(key);
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "map_clear", exceptionHandler = VoidExceptionHandler.class)
	public static void clearMap(IsolateThread thread, ObjectHandle mapHandle) {
		Map<?, ?> map = globalHandles.get(mapHandle);
		map.clear();
	}

}
