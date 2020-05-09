package org.jgrapht.capi.impl;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import org.graalvm.nativeimage.IsolateThread;
import org.graalvm.nativeimage.ObjectHandle;
import org.graalvm.nativeimage.ObjectHandles;
import org.graalvm.nativeimage.c.function.CEntryPoint;
import org.graalvm.nativeimage.c.type.CDoublePointer;
import org.graalvm.nativeimage.c.type.WordPointer;
import org.jgrapht.Graph;
import org.jgrapht.alg.flow.DinicMFImpl;
import org.jgrapht.alg.flow.EdmondsKarpMFImpl;
import org.jgrapht.alg.flow.MaximumFlowAlgorithmBase;
import org.jgrapht.alg.flow.PushRelabelMFImpl;
import org.jgrapht.alg.flow.mincost.CapacityScalingMinimumCostFlow;
import org.jgrapht.alg.flow.mincost.MinimumCostFlowProblem.MinimumCostFlowProblemImpl;
import org.jgrapht.alg.interfaces.MaximumFlowAlgorithm.MaximumFlow;
import org.jgrapht.alg.interfaces.MinimumCostFlowAlgorithm.MinimumCostFlow;
import org.jgrapht.capi.Constants;
import org.jgrapht.capi.JGraphTContext.LongToIntegerFunctionPointer;
import org.jgrapht.capi.JGraphTContext.Status;
import org.jgrapht.capi.error.StatusReturnExceptionHandler;

public class FlowApi {

	private static ObjectHandles globalHandles = ObjectHandles.getGlobal();

	private static int doRunMaxFlow(IsolateThread thread, ObjectHandle graphHandle,
			Function<Graph<Long, Long>, MaximumFlowAlgorithmBase<Long, Long>> algProvider, long source, long sink,
			CDoublePointer valueRes, WordPointer flowRes, WordPointer cutSourcePartitionRes) {
		Graph<Long, Long> g = globalHandles.get(graphHandle);
		MaximumFlowAlgorithmBase<Long, Long> alg = algProvider.apply(g);
		MaximumFlow<Long> maximumFlow = alg.getMaximumFlow(source, sink);
		Map<Long, Double> flowMap = maximumFlow.getFlowMap();
		Set<Long> cutSourcePartition = alg.getSourcePartition();
		double flowValue = maximumFlow.getValue();
		if (valueRes.isNonNull()) {
			valueRes.write(flowValue);
		}
		if (flowRes.isNonNull()) {
			flowRes.write(globalHandles.create(flowMap));
		}
		if (cutSourcePartitionRes.isNonNull()) {
			cutSourcePartitionRes.write(globalHandles.create(cutSourcePartition));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "maxflow_exec_push_relabel", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executePushRelabel(IsolateThread thread, ObjectHandle graphHandle, long source, long sink,
			CDoublePointer valueRes, WordPointer flowRes, WordPointer cutSourcePartitionRes) {
		return doRunMaxFlow(thread, graphHandle, PushRelabelMFImpl::new, source, sink, valueRes, flowRes,
				cutSourcePartitionRes);
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "maxflow_exec_dinic", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeDinic(IsolateThread thread, ObjectHandle graphHandle, long source, long sink,
			CDoublePointer valueRes, WordPointer flowRes, WordPointer cutSourcePartitionRes) {
		return doRunMaxFlow(thread, graphHandle, DinicMFImpl::new, source, sink, valueRes, flowRes,
				cutSourcePartitionRes);
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "maxflow_exec_edmonds_karp", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeEdmondsKarp(IsolateThread thread, ObjectHandle graphHandle, long source, long sink,
			CDoublePointer valueRes, WordPointer flowRes, WordPointer cutSourcePartitionRes) {
		return doRunMaxFlow(thread, graphHandle, EdmondsKarpMFImpl::new, source, sink, valueRes, flowRes,
				cutSourcePartitionRes);
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "mincostflow_exec_capacity_scaling", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeCapacityScaling(IsolateThread thread, ObjectHandle graphHandle,
			LongToIntegerFunctionPointer nodeSupplyFunction,
			LongToIntegerFunctionPointer arcCapacityLowerBoundsFunction,
			LongToIntegerFunctionPointer arcCapacityUpperBoundsFunction, int scalingFactor, CDoublePointer valueRes,
			WordPointer flowRes, WordPointer dualSolutionRes) {

		Graph<Long, Long> g = globalHandles.get(graphHandle);

		Function<Long, Integer> nodeSupplies = v -> nodeSupplyFunction.invoke(v);
		Function<Long, Integer> arcCapacityUpperBounds = e -> arcCapacityUpperBoundsFunction.invoke(e);
		Function<Long, Integer> arcCapacityLowerBounds;
		if (arcCapacityLowerBoundsFunction.isNonNull()) {
			arcCapacityLowerBounds = e -> arcCapacityLowerBoundsFunction.invoke(e);
		} else {
			arcCapacityLowerBounds = e -> 0;
		}

		MinimumCostFlowProblemImpl<Long, Long> problem = new MinimumCostFlowProblemImpl<>(g, nodeSupplies,
				arcCapacityUpperBounds, arcCapacityLowerBounds);
		CapacityScalingMinimumCostFlow<Long, Long> alg = new CapacityScalingMinimumCostFlow<>(scalingFactor);

		MinimumCostFlow<Long> flow = alg.getMinimumCostFlow(problem);
		double flowCost = flow.getCost();
		Map<Long, Double> flowMap = flow.getFlowMap();
		Map<Long, Double> dualMap = alg.getDualSolution();

		if (valueRes.isNonNull()) {
			valueRes.write(flowCost);
		}
		if (flowRes.isNonNull()) {
			flowRes.write(globalHandles.create(flowMap));
		}
		if (dualSolutionRes.isNonNull()) {
			dualSolutionRes.write(globalHandles.create(dualMap));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

}
