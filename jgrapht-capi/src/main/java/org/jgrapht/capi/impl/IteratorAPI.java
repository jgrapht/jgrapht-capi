package org.jgrapht.capi.impl;

import java.util.Iterator;

import org.graalvm.nativeimage.IsolateThread;
import org.graalvm.nativeimage.ObjectHandle;
import org.graalvm.nativeimage.ObjectHandles;
import org.graalvm.nativeimage.c.function.CEntryPoint;
import org.jgrapht.capi.Constants;
import org.jgrapht.capi.error.BooleanExceptionHandler;
import org.jgrapht.capi.error.DoubleExceptionHandler;
import org.jgrapht.capi.error.LongExceptionHandler;

public class IteratorAPI {

	private static ObjectHandles globalHandles = ObjectHandles.getGlobal();

	@CEntryPoint(name = Constants.LIB_PREFIX + "it_next_long", exceptionHandler = LongExceptionHandler.class)
	public static long iteratorNextLong(IsolateThread thread, ObjectHandle itHandle) {
		Iterator<Long> it = globalHandles.get(itHandle);
		return it.next();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "it_next_double", exceptionHandler = DoubleExceptionHandler.class)
	public static double iteratorNextDouble(IsolateThread thread, ObjectHandle itHandle) {
		Iterator<Double> it = globalHandles.get(itHandle);
		return it.next();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "it_hasnext", exceptionHandler = BooleanExceptionHandler.class)
	public static boolean iteratorHasNext(IsolateThread thread, ObjectHandle itHandle) {
		Iterator<Long> it = globalHandles.get(itHandle);
		return it.hasNext();
	}

}
