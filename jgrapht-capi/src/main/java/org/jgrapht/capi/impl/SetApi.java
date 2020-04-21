package org.jgrapht.capi.impl;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import org.graalvm.nativeimage.IsolateThread;
import org.graalvm.nativeimage.ObjectHandle;
import org.graalvm.nativeimage.ObjectHandles;
import org.graalvm.nativeimage.c.function.CEntryPoint;
import org.graalvm.nativeimage.c.type.CIntPointer;
import org.graalvm.nativeimage.c.type.CLongPointer;
import org.graalvm.nativeimage.c.type.WordPointer;
import org.jgrapht.capi.Constants;
import org.jgrapht.capi.Status;
import org.jgrapht.capi.error.StatusReturnExceptionHandler;

public class SetApi {

	private static ObjectHandles globalHandles = ObjectHandles.getGlobal();

	@CEntryPoint(name = Constants.LIB_PREFIX + "set_create", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int createSet(IsolateThread thread, WordPointer res) {
		if (res.isNonNull()) {
			res.write(globalHandles.create(new HashSet<>()));
		}
		return Status.SUCCESS.toCEnum();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "set_linked_create", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int createLinkedSet(IsolateThread thread, WordPointer res) {
		if (res.isNonNull()) {
			res.write(globalHandles.create(new LinkedHashSet<>()));
		}
		return Status.SUCCESS.toCEnum();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "set_it_create", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int createMapKeysIterator(IsolateThread thread, ObjectHandle handle, WordPointer res) {
		Set<?> set = globalHandles.get(handle);
		if (res.isNonNull()) {
			res.write(globalHandles.create(set.iterator()));
		}
		return Status.SUCCESS.toCEnum();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "set_size", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int setSize(IsolateThread thread, ObjectHandle handle, CLongPointer res) {
		Set<?> set = globalHandles.get(handle);
		if (res.isNonNull()) {
			res.write(set.size());
		}
		return Status.SUCCESS.toCEnum();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "set_long_add", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int setLongAdd(IsolateThread thread, ObjectHandle handle, long value) {
		Set<Long> set = globalHandles.get(handle);
		set.add(value);
		return Status.SUCCESS.toCEnum();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "set_double_add", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int setDoubleAdd(IsolateThread thread, ObjectHandle handle, double value) {
		Set<Double> set = globalHandles.get(handle);
		set.add(value);
		return Status.SUCCESS.toCEnum();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "set_long_remove", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int setLongRemove(IsolateThread thread, ObjectHandle handle, long value) {
		Set<Long> set = globalHandles.get(handle);
		set.remove(value);
		return Status.SUCCESS.toCEnum();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "set_double_remove", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int setDoubleRemove(IsolateThread thread, ObjectHandle handle, double value) {
		Set<Double> set = globalHandles.get(handle);
		set.remove(value);
		return Status.SUCCESS.toCEnum();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "set_long_contains", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int setLongContains(IsolateThread thread, ObjectHandle handle, long value, CIntPointer res) {
		Set<Long> set = globalHandles.get(handle);
		boolean result = set.contains(value);
		if (res.isNonNull()) {
			res.write(result ? 1 : 0);
		}
		return Status.SUCCESS.toCEnum();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "set_double_contains", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int setDoubleContains(IsolateThread thread, ObjectHandle handle, double value, CIntPointer res) {
		Set<Double> set = globalHandles.get(handle);
		boolean result = set.contains(value);
		if (res.isNonNull()) {
			res.write(result ? 1 : 0);
		}
		return Status.SUCCESS.toCEnum();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "set_clear", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int clearMap(IsolateThread thread, ObjectHandle handle) {
		Set<?> set = globalHandles.get(handle);
		set.clear();
		return Status.SUCCESS.toCEnum();
	}

}
