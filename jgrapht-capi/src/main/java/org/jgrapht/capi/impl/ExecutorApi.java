/*
 * (C) Copyright 2020-2021, by Dimitrios Michail.
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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import org.graalvm.nativeimage.IsolateThread;
import org.graalvm.nativeimage.ObjectHandle;
import org.graalvm.nativeimage.ObjectHandles;
import org.graalvm.nativeimage.c.function.CEntryPoint;
import org.graalvm.nativeimage.c.type.WordPointer;
import org.jgrapht.capi.Constants;
import org.jgrapht.capi.JGraphTContext.Status;
import org.jgrapht.capi.error.StatusReturnExceptionHandler;
import org.jgrapht.util.ConcurrencyUtil;

public class ExecutorApi {

	private static ObjectHandles globalHandles = ObjectHandles.getGlobal();

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "executor_thread_pool_create", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int createThreadPoolExecutor(IsolateThread thread, int threads, WordPointer res) {
		if (threads <= 0) {
			throw new IllegalArgumentException("Threads must be positive");
		}
		if (res.isNonNull()) {
			res.write(globalHandles.create(ConcurrencyUtil.createThreadPoolExecutor(threads)));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "executor_thread_pool_shutdown", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int createThreadPoolExecutor(IsolateThread thread, ObjectHandle executorHandle, long timeoutInMillis) {
		ExecutorService executor = globalHandles.get(executorHandle);
		try {
			ConcurrencyUtil.shutdownExecutionService(executor, timeoutInMillis, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			throw new RuntimeException("Thread interrupted");
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

}
