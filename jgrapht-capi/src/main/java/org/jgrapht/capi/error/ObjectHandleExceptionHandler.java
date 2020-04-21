package org.jgrapht.capi.error;

import org.graalvm.nativeimage.ObjectHandle;
import org.graalvm.word.WordFactory;

public class ObjectHandleExceptionHandler {

	public static ObjectHandle handle(Throwable e) { 
		Errors.setError(e);
		return WordFactory.nullPointer();
	}
	
}
