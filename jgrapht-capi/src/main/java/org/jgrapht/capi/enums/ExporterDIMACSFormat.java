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
package org.jgrapht.capi.enums;

import org.graalvm.nativeimage.c.CContext;
import org.graalvm.nativeimage.c.constant.CEnum;
import org.graalvm.nativeimage.c.constant.CEnumLookup;
import org.graalvm.nativeimage.c.constant.CEnumValue;
import org.jgrapht.capi.JGraphTDirectives;

@CContext(JGraphTDirectives.class)
@CEnum("dimacs_format_t")
public enum ExporterDIMACSFormat {

	// @formatter:off
	DIMACS_FORMAT_SHORTEST_PATH, 
	DIMACS_FORMAT_MAX_CLIQUE,
	DIMACS_FORMAT_COLORING,
	;
	// @formatter:on

	@CEnumValue
	public native int toCEnum();

	@CEnumLookup
	public static native ExporterDIMACSFormat toJavaEnum(int value);

}
