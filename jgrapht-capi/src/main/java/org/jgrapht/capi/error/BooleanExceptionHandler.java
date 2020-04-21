package org.jgrapht.capi.error;

public class BooleanExceptionHandler {

	public static boolean handle(Throwable e) { 
		Errors.setError(e);
		return false;
	}
	
}
