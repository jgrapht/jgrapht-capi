package org.jgrapht.capi.graph;

import org.graalvm.word.PointerBase;
import org.graalvm.word.WordFactory;
import org.jgrapht.capi.JGraphTContext.PPToIFunctionPointer;
import org.jgrapht.capi.JGraphTContext.PToLFunctionPointer;
import org.jgrapht.capi.JGraphTContext.PToSFunctionPointer;
import org.jgrapht.capi.JGraphTContext.PtrToEqualsFunctionPointer;
import org.jgrapht.capi.JGraphTContext.PtrToHashFunctionPointer;
import org.jgrapht.capi.JGraphTContext.PtrToStringFunctionPointer;

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

	/**
	 * Method to lookup the toString function in case the graph contains external
	 * references. Otherwise null.
	 */
	private PtrToStringFunctionPointer stringLookup;

	public DefaultHashAndEqualsResolver(PtrToHashFunctionPointer hashLookup, PtrToEqualsFunctionPointer equalsLookup,
			PtrToStringFunctionPointer stringLookup) {
		this.hashLookup = hashLookup;
		this.equalsLookup = equalsLookup;
		this.stringLookup = stringLookup;
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

	public PtrToStringFunctionPointer getStringLookup() {
		return stringLookup;
	}

	public void setStringLookup(PtrToStringFunctionPointer stringLookup) {
		this.stringLookup = stringLookup;
	}

	protected PToLFunctionPointer resolveHashFunction(PointerBase ptr) {
		if (hashLookup.isNull()) {
			return WordFactory.nullPointer();
		}
		return hashLookup.invoke(ptr);
	}

	protected PPToIFunctionPointer resolveEqualsFunction(PointerBase ptr) {
		if (equalsLookup.isNull()) {
			return WordFactory.nullPointer();
		}
		return equalsLookup.invoke(ptr);
	}

	protected PToSFunctionPointer resolveStringFunction(PointerBase ptr) {
		if (stringLookup.isNull()) {
			return WordFactory.nullPointer();
		}
		return stringLookup.invoke(ptr);
	}

	@Override
	public ExternalRef toExternalRef(PointerBase ptr) {
		PToLFunctionPointer hashPtr = resolveHashFunction(ptr);
		PPToIFunctionPointer equalsPtr = resolveEqualsFunction(ptr);
		PToSFunctionPointer strPtr = resolveStringFunction(ptr);
		ExternalRef ref = new ExternalRef(ptr, equalsPtr, hashPtr, strPtr);
		return ref;
	}

}
