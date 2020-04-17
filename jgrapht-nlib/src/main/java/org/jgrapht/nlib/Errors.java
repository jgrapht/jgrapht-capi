package org.jgrapht.nlib;

/**
 * Error handling
 */
public class Errors {

	private static Status error = Status.SUCCESS;

	public static Status getError() {
		return error;
	}

	public static void setError(Status newError) {
		error = newError;
	}

	public static void clearError() {
		error = Status.SUCCESS;
	}

}
