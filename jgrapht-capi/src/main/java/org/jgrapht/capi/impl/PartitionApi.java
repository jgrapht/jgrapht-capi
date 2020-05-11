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
import org.graalvm.nativeimage.c.type.CIntPointer;
import org.graalvm.nativeimage.c.type.WordPointer;
import org.jgrapht.Graph;
import org.jgrapht.alg.interfaces.PartitioningAlgorithm.Partitioning;
import org.jgrapht.alg.partition.BipartitePartitioning;
import org.jgrapht.capi.Constants;
import org.jgrapht.capi.JGraphTContext.Status;
import org.jgrapht.capi.error.StatusReturnExceptionHandler;

public class PartitionApi {

	private static ObjectHandles globalHandles = ObjectHandles.getGlobal();

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "partition_exec_bipartite", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeBipartitePartitioner(IsolateThread thread, ObjectHandle graphHandle, CIntPointer res,
			WordPointer part1, WordPointer part2) {
		Graph<Integer, Integer> g = globalHandles.get(graphHandle);

		BipartitePartitioning<Integer, Integer> p = new BipartitePartitioning<>(g);
		boolean result = p.isBipartite();
		if (res.isNonNull()) {
			res.write(result ? 1 : 0);
		}
		if (result) {
			Partitioning<Integer> partitioning = p.getPartitioning();
			if (part1.isNonNull()) {
				part1.write(globalHandles.create(partitioning.getPartition(0)));
			}
			if (part2.isNonNull()) {
				part2.write(globalHandles.create(partitioning.getPartition(1)));
			}
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

}
