package org.jgrapht.capi;

import org.graalvm.nativeimage.c.constant.CEnum;
import org.graalvm.nativeimage.c.constant.CEnumConstant;
import org.graalvm.nativeimage.c.constant.CEnumLookup;
import org.graalvm.nativeimage.c.constant.CEnumValue;

/**
 * Error number status
 */
@CEnum
public enum Status {

	// @formatter:off
	@CEnumConstant(value = "0")
	SUCCESS, 
	@CEnumConstant(value = "1")
	ERROR,
	@CEnumConstant(value = "2")
	ILLEGAL_ARGUMENT,
	@CEnumConstant(value = "3")
	UNSUPPORTED_OPERATION,
	@CEnumConstant(value = "4")
	INDEX_OUT_OF_BOUNDS,
	@CEnumConstant(value = "5")
	NO_SUCH_ELEMENT,
	@CEnumConstant(value = "6")
	NULL_POINTER,
	;
	// @formatter:on

	@CEnumValue
	public native int toCEnum();

	@CEnumLookup
	public static native Status toJavaEnum(int value);

}
