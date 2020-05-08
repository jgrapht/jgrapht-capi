package org.jgrapht.capi.impl;

import java.util.Set;

import org.graalvm.nativeimage.IsolateThread;
import org.graalvm.nativeimage.ObjectHandle;
import org.graalvm.nativeimage.ObjectHandles;
import org.graalvm.nativeimage.c.function.CEntryPoint;
import org.graalvm.nativeimage.c.type.CDoublePointer;
import org.graalvm.nativeimage.c.type.WordPointer;
import org.jgrapht.Graph;
import org.jgrapht.alg.StoerWagnerMinimumCut;
import org.jgrapht.capi.Constants;
import org.jgrapht.capi.JGraphTContext.Status;
import org.jgrapht.capi.error.StatusReturnExceptionHandler;

public class CutApi {

	private static ObjectHandles globalHandles = ObjectHandles.getGlobal();

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "cut_exec_stoer_wagner", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeStoerWagner(IsolateThread thread, ObjectHandle graphHandle, CDoublePointer valueRes,
			WordPointer cutSourcePartitionRes) {
		Graph<Long, Long> g = globalHandles.get(graphHandle);

		StoerWagnerMinimumCut<Long, Long> alg = new StoerWagnerMinimumCut<>(g);
		double weight = alg.minCutWeight();
		Set<Long> cutSourcePartition = alg.minCut();

		if (valueRes.isNonNull()) {
			valueRes.write(weight);
		}
		if (cutSourcePartitionRes.isNonNull()) {
			cutSourcePartitionRes.write(globalHandles.create(cutSourcePartition));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

}
