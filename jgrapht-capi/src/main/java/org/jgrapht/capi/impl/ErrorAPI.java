package org.jgrapht.capi.impl;

import org.graalvm.nativeimage.IsolateThread;
import org.graalvm.nativeimage.c.function.CEntryPoint;
import org.graalvm.nativeimage.c.type.CCharPointer;
import org.jgrapht.capi.Constants;
import org.jgrapht.capi.Status;
import org.jgrapht.capi.error.Errors;

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

	/**
	 * Get the global error status text. Note that the memory for the message is
	 * only kept until the next error status is written.
	 *
	 * @param thread the thread isolate
	 */
	@CEntryPoint(name = Constants.LIB_PREFIX + "get_errno_msg")
	public static CCharPointer getErrorMessage(IsolateThread thread) {
		return Errors.getMessageCCharPointer();
	}

}
