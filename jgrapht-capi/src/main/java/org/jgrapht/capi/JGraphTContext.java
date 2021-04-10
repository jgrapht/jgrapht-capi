package org.jgrapht.capi;

import org.graalvm.nativeimage.c.CContext;
import org.graalvm.nativeimage.c.constant.CEnum;
import org.graalvm.nativeimage.c.constant.CEnumLookup;
import org.graalvm.nativeimage.c.constant.CEnumValue;
import org.graalvm.nativeimage.c.function.CFunctionPointer;
import org.graalvm.nativeimage.c.function.InvokeCFunctionPointer;
import org.graalvm.nativeimage.c.type.CCharPointer;
import org.graalvm.nativeimage.c.type.CDoublePointer;
import org.graalvm.word.PointerBase;

@CContext(JGraphTDirectives.class)
public class JGraphTContext {

	@CEnum("status_t")
	public enum Status {

		// @formatter:off
		STATUS_SUCCESS, STATUS_ERROR, STATUS_ILLEGAL_ARGUMENT, STATUS_UNSUPPORTED_OPERATION, STATUS_INDEX_OUT_OF_BOUNDS,
		STATUS_NO_SUCH_ELEMENT, STATUS_NULL_POINTER, STATUS_CLASS_CAST, STATUS_IO_ERROR, STATUS_EXPORT_ERROR,
		STATUS_IMPORT_ERROR, STATUS_NEGATIVE_CYCLE_DETECTED, STATUS_NUMBER_FORMAT_EXCEPTION;
		// @formatter:on

		@CEnumValue
		public native int getCValue();

		@CEnumLookup
		public static native Status fromCValue(int value);

	}

	@CEnum("dimacs_format_t")
	public enum ExporterDIMACSFormat {

		// @formatter:off
		DIMACS_FORMAT_SHORTEST_PATH, DIMACS_FORMAT_MAX_CLIQUE, DIMACS_FORMAT_COLORING,;
		// @formatter:on

		@CEnumValue
		public native int toCEnum();

		@CEnumLookup
		public static native ExporterDIMACSFormat toJavaEnum(int value);

	}

	@CEnum("csv_format_t")
	public enum ImporterExporterCSVFormat {

		// @formatter:off
		CSV_FORMAT_EDGE_LIST, CSV_FORMAT_ADJACENCY_LIST, CSV_FORMAT_MATRIX,;
		// @formatter:on

		@CEnumValue
		public native int toCEnum();

		@CEnumLookup
		public static native ImporterExporterCSVFormat toJavaEnum(int value);

	}

	@CEnum("graph_event_t")
	public enum GraphEvent {

		// @formatter:off
		GRAPH_EVENT_BEFORE_VERTEX_ADDED, GRAPH_EVENT_BEFORE_VERTEX_REMOVED, GRAPH_EVENT_VERTEX_ADDED,
		GRAPH_EVENT_VERTEX_REMOVED, GRAPH_EVENT_BEFORE_EDGE_ADDED, GRAPH_EVENT_BEFORE_EDGE_REMOVED,
		GRAPH_EVENT_EDGE_ADDED, GRAPH_EVENT_EDGE_REMOVED, GRAPH_EVENT_EDGE_WEIGHT_UPDATED,;
		// @formatter:on

		@CEnumValue
		public native int toCEnum();

		@CEnumLookup
		public static native GraphEvent toJavaEnum(int value);

	}

	@CEnum("attribute_type_t")
	public enum AttributeType {

		// @formatter:off
		ATTRIBUTE_TYPE_NULL, ATTRIBUTE_TYPE_BOOLEAN, ATTRIBUTE_TYPE_INT, ATTRIBUTE_TYPE_LONG, ATTRIBUTE_TYPE_FLOAT,
		ATTRIBUTE_TYPE_DOUBLE, ATTRIBUTE_TYPE_STRING, ATTRIBUTE_TYPE_HTML, ATTRIBUTE_TYPE_UNKNOWN,
		ATTRIBUTE_TYPE_IDENTIFIER;
		// @formatter:on

		@CEnumValue
		public native int toCEnum();

		@CEnumLookup
		public static native AttributeType toJavaEnum(int value);

	}

	@CEnum("incoming_edges_support_t")
	public enum IncomingEdgesSupport {

		// @formatter:off
		INCOMING_EDGES_SUPPORT_NO_INCOMING_EDGES, INCOMING_EDGES_SUPPORT_LAZY_INCOMING_EDGES,
		INCOMING_EDGES_SUPPORT_FULL_INCOMING_EDGES;
		// @formatter:on

		@CEnumValue
		public native int toCEnum();

		@CEnumLookup
		public static native IncomingEdgesSupport toJavaEnum(int value);

	}

	public interface VoidToIntegerFunctionPointer extends CFunctionPointer {

		@InvokeCFunctionPointer
		int invoke();

	}

	public interface VoidToLongFunctionPointer extends CFunctionPointer {

		@InvokeCFunctionPointer
		long invoke();

	}

	/* Import of a C function pointer type. */
	public interface IntegerIdNotifyAttributeFunctionPointer extends CFunctionPointer {

		/*
		 * Invocation of the function pointer. A call to the function is replaced with
		 * an indirect call of the function pointer.
		 */
		@InvokeCFunctionPointer
		void invoke(int element, CCharPointer key, CCharPointer value);
	}

	public interface LongIdNotifyAttributeFunctionPointer extends CFunctionPointer {

		/*
		 * Invocation of the function pointer. A call to the function is replaced with
		 * an indirect call of the function pointer.
		 */
		@InvokeCFunctionPointer
		void invoke(long element, CCharPointer key, CCharPointer value);
	}

	public interface StringIdNotifyAttributeFunctionPointer extends CFunctionPointer {

		/*
		 * Invocation of the function pointer. A call to the function is replaced with
		 * an indirect call of the function pointer.
		 */
		@InvokeCFunctionPointer
		void invoke(CCharPointer element, CCharPointer key, CCharPointer value);
	}

	public interface PIdNotifyAttributeFunctionPointer extends CFunctionPointer {

		/*
		 * Invocation of the function pointer. A call to the function is replaced with
		 * an indirect call of the function pointer.
		 */
		@InvokeCFunctionPointer
		void invoke(PointerBase element, CCharPointer key, CCharPointer value);
	}

	/*
	 * Function pointer for importers which give the user control on how to convert
	 * the input identifier of a vertex or edge into an integer.
	 */
	public interface CCharPointerToIntegerFunctionPointer extends CFunctionPointer {

		@InvokeCFunctionPointer
		int invoke(CCharPointer id);

	}

	/*
	 * Function pointer for importers which give the user control on how to convert
	 * the input identifier of a vertex or edge into a long integer.
	 */
	public interface CCharPointerToLongFunctionPointer extends CFunctionPointer {

		@InvokeCFunctionPointer
		long invoke(CCharPointer id);

	}

	public interface CCharPointerToPFunctionPointer extends CFunctionPointer {

		@InvokeCFunctionPointer
		PointerBase invoke(CCharPointer id);

	}

	public interface CCharPointerToCCharPointerFunctionPointer extends CFunctionPointer {

		@InvokeCFunctionPointer
		CCharPointer invoke(CCharPointer key);

	}

	public interface IToIFunctionPointer extends CFunctionPointer {

		@InvokeCFunctionPointer
		int invoke(int key);

	}

	public interface IToDFunctionPointer extends CFunctionPointer {

		@InvokeCFunctionPointer
		double invoke(int key);

	}

	public interface IntegerToBooleanFunctionPointer extends CFunctionPointer {

		@InvokeCFunctionPointer
		boolean invoke(int key);

	}

	public interface PToBFunctionPointer extends CFunctionPointer {

		@InvokeCFunctionPointer
		boolean invoke(PointerBase key);

	}

	public interface IntegerToCDoublePointerFunctionPointer extends CFunctionPointer {

		@InvokeCFunctionPointer
		CDoublePointer invoke(int key);

	}

	public interface LongToCDoublePointerFunctionPointer extends CFunctionPointer {

		@InvokeCFunctionPointer
		CDoublePointer invoke(long key);

	}

	public interface PToCDoublePointerFunctionPointer extends CFunctionPointer {

		@InvokeCFunctionPointer
		CDoublePointer invoke(PointerBase key);

	}

	public interface LToIFunctionPointer extends CFunctionPointer {

		@InvokeCFunctionPointer
		int invoke(long key);

	}

	public interface LToLFunctionPointer extends CFunctionPointer {

		@InvokeCFunctionPointer
		long invoke(long key);

	}

	public interface LToDFunctionPointer extends CFunctionPointer {

		@InvokeCFunctionPointer
		double invoke(long key);

	}

	public interface LToPFunctionPointer extends CFunctionPointer {

		@InvokeCFunctionPointer
		PointerBase invoke(long key);

	}

	public interface LongToBooleanFunctionPointer extends CFunctionPointer {

		@InvokeCFunctionPointer
		boolean invoke(long key);

	}

	public interface IFunctionPointer extends CFunctionPointer {

		@InvokeCFunctionPointer
		void invoke(int key);

	}

	public interface LFunctionPointer extends CFunctionPointer {

		@InvokeCFunctionPointer
		void invoke(long key);

	}

	public interface PFunctionPointer extends CFunctionPointer {

		@InvokeCFunctionPointer
		void invoke(PointerBase key);

	}

	public interface IIFunctionPointer extends CFunctionPointer {

		@InvokeCFunctionPointer
		void invoke(int key, int value);

	}

	public interface LIFunctionPointer extends CFunctionPointer {

		@InvokeCFunctionPointer
		void invoke(long key, int value);

	}

	public interface PIFunctionPointer extends CFunctionPointer {

		@InvokeCFunctionPointer
		void invoke(PointerBase key, int value);

	}

	public interface IIToIFunctionPointer extends CFunctionPointer {

		@InvokeCFunctionPointer
		int invoke(int d1, int d2);

	}

	public interface IIToDFunctionPointer extends CFunctionPointer {

		@InvokeCFunctionPointer
		double invoke(int d1, int d2);

	}

	public interface LLToIFunctionPointer extends CFunctionPointer {

		@InvokeCFunctionPointer
		int invoke(long d1, long d2);

	}

	public interface LLToDFunctionPointer extends CFunctionPointer {

		@InvokeCFunctionPointer
		double invoke(long d1, long d2);

	}

	public interface DDToDFunctionPointer extends CFunctionPointer {

		@InvokeCFunctionPointer
		double invoke(double d1, double d2);

	}

	public interface PToIFunctionPointer extends CFunctionPointer {

		@InvokeCFunctionPointer
		int invoke(PointerBase p1);

	}

	public interface PToLFunctionPointer extends CFunctionPointer {

		@InvokeCFunctionPointer
		long invoke(PointerBase p1);

	}

	public interface PToDFunctionPointer extends CFunctionPointer {

		@InvokeCFunctionPointer
		double invoke(PointerBase p1);

	}

	public interface PToPFunctionPointer extends CFunctionPointer {

		@InvokeCFunctionPointer
		PointerBase invoke(PointerBase p1);

	}

	public interface VToPFunctionPointer extends CFunctionPointer {

		@InvokeCFunctionPointer
		PointerBase invoke();

	}

	public interface PPToIFunctionPointer extends CFunctionPointer {

		@InvokeCFunctionPointer
		int invoke(PointerBase p1, PointerBase p2);

	}

	public interface PPToDFunctionPointer extends CFunctionPointer {

		@InvokeCFunctionPointer
		double invoke(PointerBase p1, PointerBase p2);

	}

	public interface PtrToHashFunctionPointer extends CFunctionPointer {

		@InvokeCFunctionPointer
		PToLFunctionPointer invoke(PointerBase p1);

	}

	public interface PtrToEqualsFunctionPointer extends CFunctionPointer {

		@InvokeCFunctionPointer
		PPToIFunctionPointer invoke(PointerBase p1);

	}

}
