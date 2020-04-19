package org.jgrapht.nlib;

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
	GENERIC_ERROR,
	@CEnumConstant(value = "2")
	UNSUPPORTED_OPERATION,
	@CEnumConstant(value = "3")
	ILLEGAL_ARGUMENT,
	@CEnumConstant(value = "20")
	INVALID_REFERENCE,
	@CEnumConstant(value = "21")
	INVALID_GRAPH,
	@CEnumConstant(value = "22")
	INVALID_VERTEX,
	@CEnumConstant(value = "23")
	INVALID_EDGE,
	@CEnumConstant(value = "50")
	GRAPH_CREATION_ERROR,
	@CEnumConstant(value = "51")
	GRAPH_IS_UNWEIGHTED,
	@CEnumConstant(value = "52")
	GRAPH_NOT_UNDIRECTED,
	@CEnumConstant(value = "100")
	ITERATOR_NO_SUCH_ELEMENT,
	@CEnumConstant(value = "200")
	MAP_NO_SUCH_KEY
	;
	// @formatter:on

	@CEnumValue
	public native int toCEnum();

	@CEnumLookup
	public static native Status toJavaEnum(int value);
	
}
