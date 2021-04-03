package org.jgrapht.capi;

public class CapiUtils {

	@SuppressWarnings("unchecked")
	public static <V, U> V unsafeCast(U elem) {
		return (V) elem;
	}

}
