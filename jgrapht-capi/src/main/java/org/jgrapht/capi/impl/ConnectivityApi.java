package org.jgrapht.capi.impl;

import java.util.List;
import java.util.Set;

import org.graalvm.nativeimage.IsolateThread;
import org.graalvm.nativeimage.ObjectHandle;
import org.graalvm.nativeimage.ObjectHandles;
import org.graalvm.nativeimage.c.function.CEntryPoint;
import org.graalvm.nativeimage.c.type.CIntPointer;
import org.graalvm.nativeimage.c.type.WordPointer;
import org.jgrapht.Graph;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.alg.connectivity.GabowStrongConnectivityInspector;
import org.jgrapht.alg.connectivity.KosarajuStrongConnectivityInspector;
import org.jgrapht.capi.Constants;
import org.jgrapht.capi.JGraphTContext.Status;
import org.jgrapht.capi.error.StatusReturnExceptionHandler;

public class ConnectivityApi {

	private static ObjectHandles globalHandles = ObjectHandles.getGlobal();

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "connectivity_strong_exec_kosaraju", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeKosaraju(IsolateThread thread, ObjectHandle graphHandle, CIntPointer valueRes,
			WordPointer res) {
		Graph<Integer, Integer> g = globalHandles.get(graphHandle);

		KosarajuStrongConnectivityInspector<Integer, Integer> alg = new KosarajuStrongConnectivityInspector<>(g);

		boolean result = alg.isStronglyConnected();
		List<Set<Integer>> connectedSets = alg.stronglyConnectedSets();

		if (valueRes.isNonNull()) {
			valueRes.write(result ? 1 : 0);
		}
		if (res.isNonNull()) {
			res.write(globalHandles.create(connectedSets.iterator()));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "connectivity_strong_exec_gabow", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeGabow(IsolateThread thread, ObjectHandle graphHandle, CIntPointer valueRes,
			WordPointer res) {
		Graph<Integer, Integer> g = globalHandles.get(graphHandle);

		GabowStrongConnectivityInspector<Integer, Integer> alg = new GabowStrongConnectivityInspector<>(g);

		boolean result = alg.isStronglyConnected();
		List<Set<Integer>> connectedSets = alg.stronglyConnectedSets();

		if (valueRes.isNonNull()) {
			valueRes.write(result ? 1 : 0);
		}
		if (res.isNonNull()) {
			res.write(globalHandles.create(connectedSets.iterator()));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "connectivity_weak_exec_bfs", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeWeakBfs(IsolateThread thread, ObjectHandle graphHandle, CIntPointer valueRes,
			WordPointer res) {
		Graph<Integer, Integer> g = globalHandles.get(graphHandle);

		ConnectivityInspector<Integer, Integer> alg = new ConnectivityInspector<>(g);

		boolean result = alg.isConnected();
		List<Set<Integer>> connectedSets = alg.connectedSets();

		if (valueRes.isNonNull()) {
			valueRes.write(result ? 1 : 0);
		}
		if (res.isNonNull()) {
			res.write(globalHandles.create(connectedSets.iterator()));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

}
