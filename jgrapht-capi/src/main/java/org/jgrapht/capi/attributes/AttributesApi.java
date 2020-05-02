package org.jgrapht.capi.attributes;

import org.graalvm.nativeimage.IsolateThread;
import org.graalvm.nativeimage.ObjectHandle;
import org.graalvm.nativeimage.ObjectHandles;
import org.graalvm.nativeimage.c.function.CEntryPoint;
import org.graalvm.nativeimage.c.type.CCharPointer;
import org.graalvm.nativeimage.c.type.CTypeConversion;
import org.graalvm.nativeimage.c.type.WordPointer;
import org.jgrapht.capi.Constants;
import org.jgrapht.capi.JGraphTContext.Status;
import org.jgrapht.capi.error.StatusReturnExceptionHandler;
import org.jgrapht.nio.DefaultAttribute;

public class AttributesApi {

	private static ObjectHandles globalHandles = ObjectHandles.getGlobal();

	/**
	 * Create a new attributes store.
	 * 
	 * @param thread the thread isolate
	 * @param res    Pointer to store the result
	 * @return return code
	 */
	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "attributes_store_create", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int createStore(IsolateThread thread, WordPointer res) {
		AttributesStore store = new AttributesStore();
		if (res.isNonNull()) {
			res.write(globalHandles.create(store));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "attributes_store_put_boolean_attribute", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int putBooleanAttribute(IsolateThread thread, ObjectHandle storeHandle, long element,
			CCharPointer namePtr, boolean value) {
		AttributesStore store = globalHandles.get(storeHandle);
		String name = CTypeConversion.toJavaString(namePtr);
		store.putAttribute(element, name, DefaultAttribute.createAttribute(value));
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "attributes_store_put_long_attribute", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int putLongAttribute(IsolateThread thread, ObjectHandle storeHandle, long element,
			CCharPointer namePtr, long value) {
		AttributesStore store = globalHandles.get(storeHandle);
		String name = CTypeConversion.toJavaString(namePtr);
		store.putAttribute(element, name, DefaultAttribute.createAttribute(value));
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "attributes_store_put_double_attribute", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int putDoubleAttribute(IsolateThread thread, ObjectHandle storeHandle, long element,
			CCharPointer namePtr, double value) {
		AttributesStore store = globalHandles.get(storeHandle);
		String name = CTypeConversion.toJavaString(namePtr);
		store.putAttribute(element, name, DefaultAttribute.createAttribute(value));
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "attributes_store_put_string_attribute", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int putStringAttribute(IsolateThread thread, ObjectHandle storeHandle, long element,
			CCharPointer namePtr, CCharPointer valuePtr) {
		AttributesStore store = globalHandles.get(storeHandle);
		String name = CTypeConversion.toJavaString(namePtr);
		String value = CTypeConversion.toJavaString(valuePtr);
		store.putAttribute(element, name, DefaultAttribute.createAttribute(value));
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "attributes_store_remove_attribute", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int removeAttribute(IsolateThread thread, ObjectHandle storeHandle, long element,
			CCharPointer namePtr) {
		AttributesStore store = globalHandles.get(storeHandle);
		String name = CTypeConversion.toJavaString(namePtr);
		store.removeAttribute(element, name);
		return Status.STATUS_SUCCESS.getCValue();
	}

}
