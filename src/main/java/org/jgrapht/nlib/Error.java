package org.jgrapht.nlib;

import org.graalvm.nativeimage.IsolateThread;
import org.graalvm.nativeimage.c.function.CEntryPoint;

/**
 * Error handling
 */
public class Error {

	private static Status error = Status.SUCCESS;

	public static void clearError() {
		error = Status.SUCCESS;
	}

	public static void setError(Status newError) {
		error = newError;
	}

	/**
	 * Clear the global error status
	 *
	 * @param thread the thread isolate
	 */
	@CEntryPoint(name = Constants.LIB_PREFIX + "clear_errno")
	public static void clearError(IsolateThread thread) {
		clearError();
	}

	/**
	 * Get the global error status
	 *
	 * @param thread the thread isolate
	 */
	@CEntryPoint(name = Constants.LIB_PREFIX + "get_errno")
	public static Status getError(IsolateThread thread) {
		return error;
	}

}
