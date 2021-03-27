package org.jgrapht.capi.graph;

import org.graalvm.word.PointerBase;
import org.jgrapht.capi.JGraphTContext.PPToIFunctionPointer;
import org.jgrapht.capi.JGraphTContext.PToIFunctionPointer;

/**
 * A reference to an external object.
 */
public class ExternalRef {

	private final PointerBase ptr;
	private final PPToIFunctionPointer equalsPtr;
	private final PToIFunctionPointer hashPtr;

	public ExternalRef(PointerBase ptr, PPToIFunctionPointer equalsPtr, PToIFunctionPointer hashPtr) {
		this.ptr = ptr;
		this.equalsPtr = equalsPtr;
		this.hashPtr = hashPtr;
	}

	@Override
	public int hashCode() {
		if (hashPtr.isNonNull()) {
			return hashPtr.invoke(ptr);
		}
		long id = ptr.rawValue();
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		return result;
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
			return equalsPtr.invoke(ptr, other.ptr) != 0;
		}
		if (ptr.rawValue() != other.ptr.rawValue())
			return false;
		return true;
	}

}
