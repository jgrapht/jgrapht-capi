package org.jgrapht.capi.impl;

import org.graalvm.nativeimage.IsolateThread;
import org.graalvm.nativeimage.ObjectHandle;
import org.graalvm.nativeimage.ObjectHandles;
import org.graalvm.nativeimage.c.function.CEntryPoint;
import org.jgrapht.capi.Constants;
import org.jgrapht.capi.Status;
import org.jgrapht.capi.error.StatusReturnExceptionHandler;

public class MemoryAPI {

	private static ObjectHandles globalHandles = ObjectHandles.getGlobal();

	/**
	 * Destroy a handle
	 * 
	 * @param thread the thread
	 * @param handle the handle
	 */
	@CEntryPoint(name = Constants.LIB_PREFIX + "destroy", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int destroy(IsolateThread thread, ObjectHandle handle) {
		globalHandles.destroy(handle);
		return Status.SUCCESS.toCEnum();
	}

}
