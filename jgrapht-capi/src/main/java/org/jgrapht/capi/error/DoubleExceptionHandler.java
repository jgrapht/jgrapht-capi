package org.jgrapht.capi.error;

public class DoubleExceptionHandler {

	public static double handle(Throwable e) {
		Errors.setError(e);
		return 0d;
	}

}
