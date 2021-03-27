package org.jgrapht.capi.graph;

import java.util.function.Supplier;

import org.graalvm.word.PointerBase;
import org.jgrapht.capi.JGraphTContext.VToPFunctionPointer;

public class ExternalRefSupplier implements Supplier<ExternalRef> {

	private VToPFunctionPointer supplier;
	private DefaultCapiGraph<ExternalRef, ExternalRef> graph;

	public ExternalRefSupplier(VToPFunctionPointer supplier, DefaultCapiGraph<ExternalRef, ExternalRef> graph) {
		this.supplier = supplier;
		this.graph = graph;
	}

	public DefaultCapiGraph<ExternalRef, ExternalRef> getGraph() {
		return graph;
	}

	public void setGraph(DefaultCapiGraph<ExternalRef, ExternalRef> graph) {
		this.graph = graph;
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
		return graph.toExternalRef(ptr);
	}

}
