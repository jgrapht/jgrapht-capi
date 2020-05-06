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
import org.graalvm.nativeimage.c.function.CEntryPoint;
import org.graalvm.nativeimage.c.type.CCharPointer;
import org.jgrapht.capi.Constants;
import org.jgrapht.capi.error.Errors;

/**
 * Error handling
 */
public class ErrorApi {

	/**
	 * Clear the global error status
	 *
	 * @param thread the thread isolate
	 */
	@CEntryPoint(name = Constants.LIB_PREFIX + "error_clear_errno")
	public static void clearError(IsolateThread thread) {
		Errors.clearError();
	}

	/**
	 * Get the global error status
	 *
	 * @param thread the thread isolate
	 */
	@CEntryPoint(name = Constants.LIB_PREFIX + "error_get_errno")
	public static int getError(IsolateThread thread) {
		return Errors.getErrorStatus().getCValue();
	}

	/**
	 * Get the global error status text. Note that the memory for the message is
	 * only kept until the next error status is written.
	 *
	 * @param thread the thread isolate
	 */
	@CEntryPoint(name = Constants.LIB_PREFIX + "error_get_errno_msg")
	public static CCharPointer getErrorMessage(IsolateThread thread) {
		return Errors.getMessageCCharPointer();
	}
	
	@CEntryPoint(name = Constants.LIB_PREFIX + "error_print_stack_trace")
	public static void printLastStacktrace(IsolateThread thread) {
		Errors.getErrorThrowable().ifPresent(e->{
			e.printStackTrace();
		});
	}

}
