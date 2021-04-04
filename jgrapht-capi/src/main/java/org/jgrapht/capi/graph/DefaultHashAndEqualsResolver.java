package org.jgrapht.capi.graph;

import org.graalvm.word.PointerBase;
import org.graalvm.word.WordFactory;
import org.jgrapht.capi.JGraphTContext.PPToIFunctionPointer;
import org.jgrapht.capi.JGraphTContext.PToLFunctionPointer;
import org.jgrapht.capi.JGraphTContext.PtrToEqualsFunctionPointer;
import org.jgrapht.capi.JGraphTContext.PtrToHashFunctionPointer;

public class DefaultHashAndEqualsResolver implements HashAndEqualsResolver {

	/**
	 * Method to lookup the hash function in case the graph contains external
	 * references. Otherwise null.
	 */
	private PtrToHashFunctionPointer hashLookup;
	private PToLFunctionPointer cachedHashLookup;

	/**
	 * Method to lookup the equals function in case the graph contains external
	 * references. Otherwise null.
	 */
	private PtrToEqualsFunctionPointer equalsLookup;
	private PPToIFunctionPointer cachedEqualsLookup;

	public DefaultHashAndEqualsResolver(PtrToHashFunctionPointer hashLookup, PtrToEqualsFunctionPointer equalsLookup) {
		this.hashLookup = hashLookup;
		this.equalsLookup = equalsLookup;
	}

	public PtrToHashFunctionPointer getHashLookup() {
		return hashLookup;
	}

	public void setHashLookup(PtrToHashFunctionPointer hashLookup) {
		this.hashLookup = hashLookup;
	}

	public PtrToEqualsFunctionPointer getEqualsLookup() {
		return equalsLookup;
	}

	public void setEqualsLookup(PtrToEqualsFunctionPointer equalsLookup) {
		this.equalsLookup = equalsLookup;
	}

	protected PToLFunctionPointer resolveHashFunction(PointerBase ptr) {
		if (cachedHashLookup == null) {
			if (hashLookup.isNull()) {
				cachedHashLookup = WordFactory.nullPointer();
			} else {
				cachedHashLookup = hashLookup.invoke(ptr);
			}
		}
		return cachedHashLookup;
	}

	protected PPToIFunctionPointer resolveEqualsFunction(PointerBase ptr) {
		if (cachedEqualsLookup == null) {
			if (equalsLookup.isNull()) {
				cachedEqualsLookup = WordFactory.nullPointer();
			} else {
				cachedEqualsLookup = equalsLookup.invoke(ptr);
			}
		}
		return cachedEqualsLookup;
	}

	@Override
	public ExternalRef toExternalRef(PointerBase ptr) {
		PToLFunctionPointer hashPtr = resolveHashFunction(ptr);
		PPToIFunctionPointer equalsPtr = resolveEqualsFunction(ptr);
		ExternalRef ref = new ExternalRef(ptr, equalsPtr, hashPtr);
		return ref;
	}

}
