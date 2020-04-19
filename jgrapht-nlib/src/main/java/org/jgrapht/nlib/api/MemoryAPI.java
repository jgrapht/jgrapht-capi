package org.jgrapht.nlib.api;

import org.graalvm.nativeimage.IsolateThread;
import org.graalvm.nativeimage.ObjectHandle;
import org.graalvm.nativeimage.ObjectHandles;
import org.graalvm.nativeimage.c.function.CEntryPoint;
import org.jgrapht.nlib.Constants;
import org.jgrapht.nlib.Errors;
import org.jgrapht.nlib.Status;

public class MemoryAPI {

	private static ObjectHandles globalHandles = ObjectHandles.getGlobal();

	/**
	 * Destroy a handle
	 * 
	 * @param thread the thread
	 * @param handle the handle
	 */
	@CEntryPoint(name = Constants.LIB_PREFIX + "destroy")
	public static void destroy(IsolateThread thread, ObjectHandle handle) {
		try {
			globalHandles.destroy(handle);
		} catch (IllegalArgumentException e) {
			Errors.setError(Status.ILLEGAL_ARGUMENT, e.getMessage());
		} catch (Exception e) {
			Errors.setError(Status.ERROR, e.getMessage());
		}
	}

}
