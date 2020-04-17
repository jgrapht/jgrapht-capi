package org.jgrapht.nlib.api;

import org.graalvm.nativeimage.IsolateThread;
import org.graalvm.nativeimage.c.function.CEntryPoint;
import org.jgrapht.nlib.Constants;
import org.jgrapht.nlib.Errors;
import org.jgrapht.nlib.Status;

/**
 * Error handling
 */
public class ErrorAPI {

	/**
	 * Clear the global error status
	 *
	 * @param thread the thread isolate
	 */
	@CEntryPoint(name = Constants.LIB_PREFIX + "clear_errno")
	public static void clearError(IsolateThread thread) {
		Errors.clearError();
	}

	/**
	 * Get the global error status
	 *
	 * @param thread the thread isolate
	 */
	@CEntryPoint(name = Constants.LIB_PREFIX + "get_errno")
	public static Status getError(IsolateThread thread) {
		return Errors.getError();
	}

}
