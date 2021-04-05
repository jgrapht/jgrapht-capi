package org.jgrapht.capi.graph;

import java.util.function.Supplier;

import org.graalvm.word.PointerBase;
import org.jgrapht.capi.JGraphTContext.VToPFunctionPointer;

public class ExternalRefSupplier implements Supplier<ExternalRef> {

	private VToPFunctionPointer supplier;
	private HashAndEqualsResolver resolver;

	public ExternalRefSupplier(VToPFunctionPointer supplier, HashAndEqualsResolver resolver) {
		this.supplier = supplier;
		this.resolver = resolver;
	}

	public HashAndEqualsResolver getResolver() {
		return resolver;
	}

	public void setResolver(HashAndEqualsResolver resolver) {
		this.resolver = resolver;
	}

	public VToPFunctionPointer getSupplier() {
		return supplier;
	}

	public void setSupplier(VToPFunctionPointer supplier) {
		this.supplier = supplier;
	}

	@Override
	public ExternalRef get() {
		PointerBase ptr = supplier.invoke();
		if (ptr.isNull()) {
			throw new IllegalArgumentException("Supplier must return valid values");
		}
		return resolver.toExternalRef(ptr);
	}

}
