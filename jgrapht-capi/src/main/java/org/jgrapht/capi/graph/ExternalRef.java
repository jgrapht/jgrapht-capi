package org.jgrapht.capi.graph;

import org.jgrapht.capi.JGraphTContext.LLToIFunctionPointer;
import org.jgrapht.capi.JGraphTContext.LongToIntegerFunctionPointer;

/**
 * A reference to an external object.
 */
public class ExternalRef {

	private final long id;
	private final LLToIFunctionPointer equalsPtr;
	private final LongToIntegerFunctionPointer hashPtr;

	public ExternalRef(long id, LLToIFunctionPointer equalsPtr, LongToIntegerFunctionPointer hashPtr) {
		this.id = id;
		this.equalsPtr = equalsPtr;
		this.hashPtr = hashPtr;
	}

	public long getId() {
		return id;
	}

	@Override
	public int hashCode() {
		if (hashPtr.isNonNull()) {
			return hashPtr.invoke(this.id);
		}
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
			return equalsPtr.invoke(id, other.id) != 0;
		}
		if (id != other.id)
			return false;
		return true;
	}

}
