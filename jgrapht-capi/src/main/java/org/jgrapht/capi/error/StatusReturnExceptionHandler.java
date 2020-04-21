package org.jgrapht.capi.error;

/**
 * Convert an exception into an status code.
 */
public class StatusReturnExceptionHandler {

	public static int handle(Throwable e) {
		return Errors.throwableToStatus(e).toCEnum();
	}

}
