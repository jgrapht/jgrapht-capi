package org.jgrapht.capi;

import org.graalvm.nativeimage.c.CContext;
import org.graalvm.nativeimage.c.constant.CEnum;
import org.graalvm.nativeimage.c.constant.CEnumLookup;
import org.graalvm.nativeimage.c.constant.CEnumValue;
import org.graalvm.nativeimage.c.function.CFunctionPointer;
import org.graalvm.nativeimage.c.function.InvokeCFunctionPointer;
import org.graalvm.nativeimage.c.type.CCharPointer;

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
		STATUS_EXPORT_ERROR, 
		STATUS_IMPORT_ERROR,
		STATUS_NEGATIVE_CYCLE_DETECTED,
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

	@CEnum("csv_format_t")
	public enum ImporterExporterCSVFormat {

		// @formatter:off
	    CSV_FORMAT_EDGE_LIST,
	    CSV_FORMAT_ADJACENCY_LIST,
	    CSV_FORMAT_MATRIX,
		;
		// @formatter:on

		@CEnumValue
		public native int toCEnum();

		@CEnumLookup
		public static native ImporterExporterCSVFormat toJavaEnum(int value);

	}

	/* Import of a C function pointer type. */
	public interface NotifyAttributeFunctionPointer extends CFunctionPointer {

		/*
		 * Invocation of the function pointer. A call to the function is replaced with
		 * an indirect call of the function pointer.
		 */
		@InvokeCFunctionPointer
		void invoke(int element, CCharPointer key, CCharPointer value);
	}

	/*
	 * Function pointer for importers which give the user control on how to convert
	 * the input identifier of a vertex or edge into a long integer.
	 */
	public interface ImportIdFunctionPointer extends CFunctionPointer {

		@InvokeCFunctionPointer
		int invoke(CCharPointer id);

	}

	/*
	 * Function pointer for A* heuristic functions.
	 */
	public interface AStarHeuristicFunctionPointer extends CFunctionPointer {

		@InvokeCFunctionPointer
		double invoke(int source, int target);

	}

	public interface IntegerToIntegerFunctionPointer extends CFunctionPointer {

		@InvokeCFunctionPointer
		int invoke(int key);

	}

}
