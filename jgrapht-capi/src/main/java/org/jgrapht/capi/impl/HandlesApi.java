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
import org.graalvm.nativeimage.c.type.CCharPointerPointer;
import org.graalvm.nativeimage.c.type.CTypeConversion.CCharPointerHolder;
import org.jgrapht.capi.Constants;
import org.jgrapht.capi.JGraphTContext.Status;
import org.jgrapht.capi.error.StatusReturnExceptionHandler;

public class HandlesApi {

	private static ObjectHandles globalHandles = ObjectHandles.getGlobal();

	/**
	 * Destroy a handle
	 * 
	 * @param thread the thread
	 * @param handle the handle
	 */
	@CEntryPoint(name = Constants.LIB_PREFIX + "handles_destroy", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int destroy(IsolateThread thread, ObjectHandle handle) {
		globalHandles.destroy(handle);
		return Status.STATUS_SUCCESS.getCValue();
	}

	/**
	 * Access a CCharPointerHolder which has been previously kept in the global
	 * handles.
	 * 
	 * @param thread
	 * @param handle
	 * @param res
	 * @return
	 */
	@CEntryPoint(name = Constants.LIB_PREFIX + "handles_get_ccharpointer", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int getHandleAsString(IsolateThread thread, ObjectHandle handle, CCharPointerPointer res) {
		CCharPointerHolder cstr = globalHandles.get(handle);
		if (cstr != null && res.isNonNull()) {
			res.write(cstr.get());
		}
		return Status.STATUS_SUCCESS.getCValue();
	}
	
}
