/*
 * (C) Copyright 2020, by Dimitrios Michail.
 *
 * JGraphT C-API
 *
 * See the CONTRIBUTORS.md file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the
 * GNU Lesser General Public License v2.1 or later
 * which is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1-standalone.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR LGPL-2.1-or-later
 */
package org.jgrapht.capi;

import org.graalvm.nativeimage.c.constant.CEnum;
import org.graalvm.nativeimage.c.constant.CEnumConstant;
import org.graalvm.nativeimage.c.constant.CEnumLookup;
import org.graalvm.nativeimage.c.constant.CEnumValue;

/**
 * Error number status
 */
@CEnum
public enum Status {

	// @formatter:off
	@CEnumConstant(value = "0")
	SUCCESS, 
	@CEnumConstant(value = "1")
	ERROR,
	@CEnumConstant(value = "2")
	ILLEGAL_ARGUMENT,
	@CEnumConstant(value = "3")
	UNSUPPORTED_OPERATION,
	@CEnumConstant(value = "4")
	INDEX_OUT_OF_BOUNDS,
	@CEnumConstant(value = "5")
	NO_SUCH_ELEMENT,
	@CEnumConstant(value = "6")
	NULL_POINTER,
	;
	// @formatter:on

	@CEnumValue
	public native int toCEnum();

	@CEnumLookup
	public static native Status toJavaEnum(int value);

}
