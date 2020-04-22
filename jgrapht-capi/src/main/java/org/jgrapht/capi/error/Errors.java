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
package org.jgrapht.capi.error;

import java.util.NoSuchElementException;

import org.graalvm.nativeimage.c.type.CCharPointer;
import org.jgrapht.capi.Status;

/**
 * Error handling
 */
public class Errors {

	/**
	 * The actual error, one per thread.
	 */
	private static CustomErrorThreadLocal error = new CustomErrorThreadLocal();

	/**
	 * A custom thread local for proper initialization.
	 */
	private static class CustomErrorThreadLocal extends ThreadLocal<Error> {

		public CustomErrorThreadLocal() {
			super();
		}

		@Override
		protected Error initialValue() {
			return Error.SUCCESS;
		}

	}

	public static Status getErrorStatus() {
		return error.get().getStatus();
	}

	public static String getErrorMessage() {
		return error.get().getMessage();
	}

	public static CCharPointer getMessageCCharPointer() {
		return error.get().getMessagePin().get();
	}

	public static void clearError() {
		error.set(Error.SUCCESS);
	}

	public static void setError(Throwable e) {
		Status status = throwableToStatus(e);
		String message = e.getMessage();
		if (message == null) {
			StringBuilder sb = new StringBuilder();
			sb.append("Error");
			String exceptionClassName = e.getClass().getSimpleName();
			if (exceptionClassName != null) {
				sb.append(" (");
				sb.append(exceptionClassName);
				sb.append(")");
			}
			message = sb.toString();
		}
		error.set(Error.of(status, message));

	}

	public static Status throwableToStatus(Throwable e) {
		Status status;
		if (e instanceof IllegalArgumentException) {
			status = Status.ILLEGAL_ARGUMENT;
		} else if (e instanceof UnsupportedOperationException) {
			status = Status.UNSUPPORTED_OPERATION;
		} else if (e instanceof IndexOutOfBoundsException) {
			status = Status.INDEX_OUT_OF_BOUNDS;
		} else if (e instanceof NoSuchElementException) {
			status = Status.NO_SUCH_ELEMENT;
		} else if (e instanceof NullPointerException) {
			status = Status.NULL_POINTER;
		} else if (e instanceof ClassCastException) {
			status = Status.CLASS_CAST;
		} else {
			status = Status.ERROR;
		}
		return status;
	}

}
