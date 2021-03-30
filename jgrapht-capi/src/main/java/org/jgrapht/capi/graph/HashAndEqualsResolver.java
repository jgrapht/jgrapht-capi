package org.jgrapht.capi.graph;

import org.graalvm.word.PointerBase;

public interface HashAndEqualsResolver {

	ExternalRef toExternalRef(PointerBase ptr);

}