package org.jgrapht.capi.graph;

import org.graalvm.word.PointerBase;
import org.graalvm.word.WordFactory;

public interface PointerGraph {

	default PointerBase getVertexAddress(ExternalRef v) {
		return WordFactory.nullPointer();
	}

	default PointerBase getEdgeAddress(ExternalRef e) {
		return WordFactory.nullPointer();
	}

}