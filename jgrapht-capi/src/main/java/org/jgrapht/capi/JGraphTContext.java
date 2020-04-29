package org.jgrapht.capi;

import org.graalvm.nativeimage.c.CContext;
import org.graalvm.nativeimage.c.constant.CEnum;
import org.graalvm.nativeimage.c.constant.CEnumLookup;
import org.graalvm.nativeimage.c.constant.CEnumValue;

@CContext(JGraphTDirectives.class)
public class JGraphTContext {

	@CEnum("status_t")
	public enum Status {

		// @formatter:off
		STATUS_SUCCESS, 
		STATUS_ERROR,
		STATUS_ILLEGAL_ARGUMENT,
		STATUS_UNSUPPORTED_OPERATION,
		STATUS_INDEX_OUT_OF_BOUNDS,
		STATUS_NO_SUCH_ELEMENT,
		STATUS_NULL_POINTER,
		STATUS_CLASS_CAST,
		STATUS_IO_ERROR,
		STATUS_EXPORT_ERROR
		;
		// @formatter:on

		@CEnumValue
		public native int getCValue();

		@CEnumLookup
		public static native Status fromCValue(int value);

	}

	@CEnum("dimacs_format_t")
	public enum ExporterDIMACSFormat {

		// @formatter:off
		DIMACS_FORMAT_SHORTEST_PATH, 
		DIMACS_FORMAT_MAX_CLIQUE,
		DIMACS_FORMAT_COLORING,
		;
		// @formatter:on

		@CEnumValue
		public native int toCEnum();

		@CEnumLookup
		public static native ExporterDIMACSFormat toJavaEnum(int value);

	}

}
