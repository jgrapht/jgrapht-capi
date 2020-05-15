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

import java.util.Objects;

import org.graalvm.nativeimage.c.type.CTypeConversion.CCharPointerHolder;
import org.jgrapht.capi.JGraphTContext.Status;
import org.jgrapht.capi.StringUtils;

public class Error {

	private Status status;
	private String message;
	private CCharPointerHolder messagePin;
	private Throwable throwable;

	public Error(Status status, String message, Throwable throwable) {
		this.status = Objects.requireNonNull(status, "Status cannot be null");
		this.message = Objects.requireNonNull(message, "Message cannot be null");
		this.messagePin = StringUtils.toCStringInUtf8(message);
		this.throwable = throwable;
	}

	public Status getStatus() {
		return status;
	}

	public String getMessage() {
		return message;
	}

	public CCharPointerHolder getMessagePin() {
		return messagePin;
	}

	public Throwable getThrowable() {
		return throwable;
	}

	@Override
	public String toString() {
		return "Error [status=" + status + ", message=" + message + ", messagePin=" + messagePin + ", throwable="
				+ throwable + "]";
	}

}
