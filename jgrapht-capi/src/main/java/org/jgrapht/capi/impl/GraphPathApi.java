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
package org.jgrapht.capi.impl;

import org.graalvm.nativeimage.IsolateThread;
import org.graalvm.nativeimage.ObjectHandle;
import org.graalvm.nativeimage.ObjectHandles;
import org.graalvm.nativeimage.c.function.CEntryPoint;
import org.graalvm.nativeimage.c.type.CDoublePointer;
import org.graalvm.nativeimage.c.type.CLongPointer;
import org.graalvm.nativeimage.c.type.WordPointer;
import org.jgrapht.GraphPath;
import org.jgrapht.capi.Constants;
import org.jgrapht.capi.enums.Status;
import org.jgrapht.capi.error.StatusReturnExceptionHandler;

public class GraphPathApi {

	private static ObjectHandles globalHandles = ObjectHandles.getGlobal();

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "graphpath_get_fields", exceptionHandler = StatusReturnExceptionHandler.class)
	public static Status readGraphPath(IsolateThread thread, ObjectHandle handle, CDoublePointer weightRes,
			CLongPointer startVertexRes, CLongPointer endVertexRes, WordPointer edgeItRes) {
		GraphPath<Long, Long> gp = globalHandles.get(handle);
		if (weightRes.isNonNull()) {
			weightRes.write(gp.getWeight());
		}
		if (startVertexRes.isNonNull()) {
			startVertexRes.write(gp.getStartVertex());
		}
		if (endVertexRes.isNonNull()) {
			endVertexRes.write(gp.getEndVertex());
		}
		if (edgeItRes.isNonNull()) {
			edgeItRes.write(globalHandles.create(gp.getEdgeList().iterator()));
		}
		return Status.STATUS_SUCCESS;
	}

}
