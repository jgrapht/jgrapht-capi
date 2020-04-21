package org.jgrapht.capi.error;

public class LongExceptionHandler {

	public static long handle(Throwable e) { 
		Errors.setError(e);
		return 0L;
	}
	
}
