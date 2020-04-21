package org.jgrapht.capi.error;

public class VoidExceptionHandler {

	public static void handle(Throwable e) {
		Errors.setError(e);
	}

}
