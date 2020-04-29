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

import org.graalvm.nativeimage.c.type.CTypeConversion;
import org.graalvm.nativeimage.c.type.CTypeConversion.CCharPointerHolder;
import org.jgrapht.capi.enums.Status;

public class Error {

	public static final Error SUCCESS = Error.of(Status.SUCCESS);

	private static final String NO_MESSAGE = "";
	private static final CCharPointerHolder NO_MESSAGE_PIN = CTypeConversion.toCString(NO_MESSAGE);

	private Status status;
	private String message;
	private CCharPointerHolder messagePin;

	private Error(Status status, String message) {
		this(status, message, CTypeConversion.toCString(message));
	}

	private Error(Status status, String message, CCharPointerHolder messagePin) {
		this.status = status;
		this.message = message;
		this.messagePin = messagePin;
	}

	public static Error of(Status status) {
		return new Error(status, NO_MESSAGE, NO_MESSAGE_PIN);
	}

	public static Error of(Status status, String message) {
		return new Error(status, Objects.requireNonNull(message));
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

}
