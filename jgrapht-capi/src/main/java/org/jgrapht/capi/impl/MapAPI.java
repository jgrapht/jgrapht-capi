package org.jgrapht.capi.impl;

import java.util.HashMap;
import java.util.Map;

import org.graalvm.nativeimage.IsolateThread;
import org.graalvm.nativeimage.ObjectHandle;
import org.graalvm.nativeimage.ObjectHandles;
import org.graalvm.nativeimage.c.function.CEntryPoint;
import org.graalvm.word.WordFactory;
import org.jgrapht.capi.Constants;
import org.jgrapht.capi.Errors;
import org.jgrapht.capi.Status;

public class MapAPI {

	private static ObjectHandles globalHandles = ObjectHandles.getGlobal();

	/**
	 * Create a map
	 * 
	 * @param thread the thread
	 * @return the handle
	 */
	@CEntryPoint(name = Constants.LIB_PREFIX + "map_create")
	public static ObjectHandle createMap(IsolateThread thread) {
		try {
			return globalHandles.create(new HashMap<>());
		} catch (Exception e) {
			Errors.setError(Status.ERROR, e.getMessage());
		}
		return WordFactory.nullPointer();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "map_keys_it_create")
	public static ObjectHandle createMapKeysIterator(IsolateThread thread, ObjectHandle mapHandle) {
		try {
			Map<?, ?> map = globalHandles.get(mapHandle);
			return globalHandles.create(map.keySet().iterator());
		} catch (Exception e) {
			Errors.setError(Status.ERROR, e.getMessage());
		}
		return WordFactory.nullPointer();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "map_values_it_create")
	public static ObjectHandle createMapValuesIterator(IsolateThread thread, ObjectHandle mapHandle) {
		try {
			Map<?, ?> map = globalHandles.get(mapHandle);
			return globalHandles.create(map.values().iterator());
		} catch (Exception e) {
			Errors.setError(Status.ERROR, e.getMessage());
		}
		return WordFactory.nullPointer();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "map_long_double_put")
	public static void mapLongDoublePut(IsolateThread thread, ObjectHandle mapHandle, long key, double value) {
		try {
			Map<Long, Double> map = globalHandles.get(mapHandle);
			map.put(key, value);
		} catch (IllegalArgumentException e) {
			Errors.setError(Status.ILLEGAL_ARGUMENT, e.getMessage());
		} catch (Exception e) {
			Errors.setError(Status.ERROR, e.getMessage());
		}
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "map_long_double_get")
	public static double mapLongDoubleGet(IsolateThread thread, ObjectHandle mapHandle, long key) {
		try {
			Map<Long, Double> map = globalHandles.get(mapHandle);
			Double value = map.get(key);
			if (value == null) {
				Errors.setError(Status.MAP_NO_SUCH_KEY, "Key " + key + " not found in map");
				return 0d;
			}
			return value;
		} catch (IllegalArgumentException e) {
			Errors.setError(Status.ILLEGAL_ARGUMENT, e.getMessage());
		} catch (Exception e) {
			Errors.setError(Status.ERROR, e.getMessage());
		}
		return 0d;
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "map_long_contains_key")
	public static boolean mapLongContains(IsolateThread thread, ObjectHandle mapHandle, long key) {
		try {
			Map<Long, ?> map = globalHandles.get(mapHandle);
			return map.containsKey(key);
		} catch (IllegalArgumentException e) {
			Errors.setError(Status.ILLEGAL_ARGUMENT, e.getMessage());
		} catch (Exception e) {
			Errors.setError(Status.ERROR, e.getMessage());
		}
		return false;
	}

}
