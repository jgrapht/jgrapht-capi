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

	/**
	 * Method to lookup the equals function in case the graph contains external
	 * references. Otherwise null.
	 */
	private PtrToEqualsFunctionPointer equalsLookup;

	public DefaultHashAndEqualsResolver() { 
		this(null, null);
	}
	
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
		if (hashLookup == null || hashLookup.isNull()) {
			return WordFactory.nullPointer();
		}
		return hashLookup.invoke(ptr);
	}

	protected PPToIFunctionPointer resolveEqualsFunction(PointerBase ptr) {
		if (equalsLookup == null || equalsLookup.isNull()) {
			return WordFactory.nullPointer();
		}
		return equalsLookup.invoke(ptr);
	}

	@Override
	public ExternalRef toExternalRef(PointerBase ptr) {
		PToLFunctionPointer hashPtr = resolveHashFunction(ptr);
		PPToIFunctionPointer equalsPtr = resolveEqualsFunction(ptr);
		ExternalRef ref = new ExternalRef(ptr, equalsPtr, hashPtr);
		return ref;
	}

}
