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

/**
 * Global library constants
 */
public class Constants {

	/**
	 * Library name
	 */
	public static final String LIB_NAME = "jgrapht_capi";

	/**
	 * Library prefix
	 */
	public static final String LIB_PREFIX = LIB_NAME + "_";

	/**
	 * Indicates a method working on a graph with integer vertices and integer
	 * edges.
	 */
	public static final String INTINT = "ii_";

	/**
	 * Indicates a method working on a graph with long vertices and long edges.
	 */
	public static final String LONGLONG = "ll_";

	/**
	 * Indicates a method working on a graph with any type of vertices and edges.
	 */
	public static final String ANYANY = "xx_";

}
