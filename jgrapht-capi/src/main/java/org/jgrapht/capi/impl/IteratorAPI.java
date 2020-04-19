package org.jgrapht.capi.impl;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.graalvm.nativeimage.IsolateThread;
import org.graalvm.nativeimage.ObjectHandle;
import org.graalvm.nativeimage.ObjectHandles;
import org.graalvm.nativeimage.c.function.CEntryPoint;
import org.jgrapht.capi.Constants;
import org.jgrapht.capi.Errors;
import org.jgrapht.capi.Status;

public class IteratorAPI {

	private static ObjectHandles globalHandles = ObjectHandles.getGlobal();

	@CEntryPoint(name = Constants.LIB_PREFIX + "it_next")
	public static long iteratorNext(IsolateThread thread, ObjectHandle itHandle) {
		try {
			Iterator<Long> it = globalHandles.get(itHandle);
			return it.next();
		} catch (IllegalArgumentException e) {
			Errors.setError(Status.ILLEGAL_ARGUMENT, e.getMessage());
		} catch (NoSuchElementException e) {
			Errors.setError(Status.NO_SUCH_ELEMENT, e.getMessage());
		} catch (Exception e) {
			Errors.setError(Status.ERROR, e.getMessage());
		}
		return Constants.LONG_NO_RESULT;
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "it_hasnext")
	public static boolean iteratorHasNext(IsolateThread thread, ObjectHandle itHandle) {
		try {
			Iterator<Long> it = globalHandles.get(itHandle);
			return it.hasNext();
		} catch (IllegalArgumentException e) {
			Errors.setError(Status.ILLEGAL_ARGUMENT, e.getMessage());
		} catch (Exception e) {
			Errors.setError(Status.ERROR, e.getMessage());
		}
		return false;
	}

}
