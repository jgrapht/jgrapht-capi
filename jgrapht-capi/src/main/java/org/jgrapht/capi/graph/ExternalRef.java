package org.jgrapht.capi.graph;

import org.graalvm.word.PointerBase;
import org.jgrapht.capi.JGraphTContext.PPToIFunctionPointer;
import org.jgrapht.capi.JGraphTContext.PToLFunctionPointer;

/**
 * A reference to an external object.
 */
public class ExternalRef {

	private final PointerBase ptr;
	private final PPToIFunctionPointer equalsPtr;
	private final PToLFunctionPointer hashPtr;

	public ExternalRef(PointerBase ptr, PPToIFunctionPointer equalsPtr, PToLFunctionPointer hashPtr) {
		this.ptr = ptr;
		this.equalsPtr = equalsPtr;
		this.hashPtr = hashPtr;
	}

	public PointerBase getPtr() {
		return ptr;
	}

	public PPToIFunctionPointer getEqualsPtr() {
		return equalsPtr;
	}

	public PToLFunctionPointer getHashPtr() {
		return hashPtr;
	}

	@Override
	public int hashCode() {
		if (hashPtr.isNonNull()) {
			return Long.hashCode(hashPtr.invoke(ptr));
		}
		return Long.hashCode(ptr.rawValue());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ExternalRef other = (ExternalRef) obj;
		if (equalsPtr.isNonNull()) {
			int comp = equalsPtr.invoke(ptr, other.ptr);
			if (comp == -1) {
				throw new IllegalArgumentException("Error occured when calling external comparison function.");
			}
			return comp != 0;
		}
		if (ptr.rawValue() != other.ptr.rawValue())
			return false;
		return true;
	}

}
