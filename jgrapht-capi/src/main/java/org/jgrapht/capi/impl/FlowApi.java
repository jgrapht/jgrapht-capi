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
import org.jgrapht.alg.flow.GusfieldEquivalentFlowTree;
import org.jgrapht.alg.flow.MaximumFlowAlgorithmBase;
import org.jgrapht.alg.flow.PushRelabelMFImpl;
import org.jgrapht.alg.flow.mincost.CapacityScalingMinimumCostFlow;
import org.jgrapht.alg.flow.mincost.MinimumCostFlowProblem.MinimumCostFlowProblemImpl;
import org.jgrapht.alg.interfaces.MaximumFlowAlgorithm.MaximumFlow;
import org.jgrapht.alg.interfaces.MinimumCostFlowAlgorithm.MinimumCostFlow;
import org.jgrapht.capi.Constants;
import org.jgrapht.capi.JGraphTContext.IntegerToIntegerFunctionPointer;
import org.jgrapht.capi.JGraphTContext.Status;
import org.jgrapht.capi.error.StatusReturnExceptionHandler;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

public class FlowApi {

	private static ObjectHandles globalHandles = ObjectHandles.getGlobal();

	private static int doRunMaxFlow(IsolateThread thread, ObjectHandle graphHandle,
			Function<Graph<Integer, Integer>, MaximumFlowAlgorithmBase<Integer, Integer>> algProvider, int source,
			int sink, CDoublePointer valueRes, WordPointer flowRes, WordPointer cutSourcePartitionRes) {
		Graph<Integer, Integer> g = globalHandles.get(graphHandle);
		MaximumFlowAlgorithmBase<Integer, Integer> alg = algProvider.apply(g);
		MaximumFlow<Integer> maximumFlow = alg.getMaximumFlow(source, sink);
		Map<Integer, Double> flowMap = maximumFlow.getFlowMap();
		Set<Integer> cutSourcePartition = alg.getSourcePartition();
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
	public static int executePushRelabel(IsolateThread thread, ObjectHandle graphHandle, int source, int sink,
			CDoublePointer valueRes, WordPointer flowRes, WordPointer cutSourcePartitionRes) {
		return doRunMaxFlow(thread, graphHandle, PushRelabelMFImpl::new, source, sink, valueRes, flowRes,
				cutSourcePartitionRes);
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "maxflow_exec_dinic", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeDinic(IsolateThread thread, ObjectHandle graphHandle, int source, int sink,
			CDoublePointer valueRes, WordPointer flowRes, WordPointer cutSourcePartitionRes) {
		return doRunMaxFlow(thread, graphHandle, DinicMFImpl::new, source, sink, valueRes, flowRes,
				cutSourcePartitionRes);
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "maxflow_exec_edmonds_karp", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeEdmondsKarp(IsolateThread thread, ObjectHandle graphHandle, int source, int sink,
			CDoublePointer valueRes, WordPointer flowRes, WordPointer cutSourcePartitionRes) {
		return doRunMaxFlow(thread, graphHandle, EdmondsKarpMFImpl::new, source, sink, valueRes, flowRes,
				cutSourcePartitionRes);
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "mincostflow_exec_capacity_scaling", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeCapacityScaling(IsolateThread thread, ObjectHandle graphHandle,
			IntegerToIntegerFunctionPointer nodeSupplyFunction,
			IntegerToIntegerFunctionPointer arcCapacityLowerBoundsFunction,
			IntegerToIntegerFunctionPointer arcCapacityUpperBoundsFunction, int scalingFactor, CDoublePointer valueRes,
			WordPointer flowRes, WordPointer dualSolutionRes) {

		Graph<Integer, Integer> g = globalHandles.get(graphHandle);

		Function<Integer, Integer> nodeSupplies = v -> nodeSupplyFunction.invoke(v);
		Function<Integer, Integer> arcCapacityUpperBounds = e -> arcCapacityUpperBoundsFunction.invoke(e);
		Function<Integer, Integer> arcCapacityLowerBounds;
		if (arcCapacityLowerBoundsFunction.isNonNull()) {
			arcCapacityLowerBounds = e -> arcCapacityLowerBoundsFunction.invoke(e);
		} else {
			arcCapacityLowerBounds = e -> 0;
		}

		MinimumCostFlowProblemImpl<Integer, Integer> problem = new MinimumCostFlowProblemImpl<>(g, nodeSupplies,
				arcCapacityUpperBounds, arcCapacityLowerBounds);
		CapacityScalingMinimumCostFlow<Integer, Integer> alg = new CapacityScalingMinimumCostFlow<>(scalingFactor);

		MinimumCostFlow<Integer> flow = alg.getMinimumCostFlow(problem);
		double flowCost = flow.getCost();
		Map<Integer, Double> flowMap = flow.getFlowMap();
		Map<Integer, Double> dualMap = alg.getDualSolution();

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



	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "equivalentflowtree_exec_gusfield", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeEFTGusfield(IsolateThread thread, ObjectHandle graphHandle, WordPointer res) {
		Graph<Integer, Integer> g = globalHandles.get(graphHandle);
		GusfieldEquivalentFlowTree<Integer, Integer> alg = new GusfieldEquivalentFlowTree<>(g);
		if (res.isNonNull()) {
			res.write(globalHandles.create(alg));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "equivalentflowtree_max_st_flow", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int eftMaxSTFlow(IsolateThread thread, ObjectHandle eft, int source, int sink,
			CDoublePointer valueRes) {
		GusfieldEquivalentFlowTree<Integer, Integer> alg = globalHandles.get(eft);
		double flowValue = alg.getMaximumFlowValue(source, sink);
		if (valueRes.isNonNull()) {
			valueRes.write(flowValue);
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "equivalentflowtree_tree", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int eftGetTree(IsolateThread thread, ObjectHandle eft, WordPointer treeRes) {
		GusfieldEquivalentFlowTree<Integer, Integer> alg = globalHandles.get(eft);
		SimpleWeightedGraph<Integer, DefaultWeightedEdge> origTree = alg.getEquivalentFlowTree();

		// convert to integer vertices/edges
		Graph<Integer, Integer> tree = GraphApi.createGraph(false, false, false, true);
		for (Integer v : origTree.vertexSet()) {
			tree.addVertex(v);
		}
		for (DefaultWeightedEdge e : origTree.edgeSet()) {
			int s = origTree.getEdgeSource(e);
			int t = origTree.getEdgeTarget(e);
			double w = origTree.getEdgeWeight(e);
			tree.setEdgeWeight(tree.addEdge(s, t), w);
		}

		// write result
		if (treeRes.isNonNull()) {
			treeRes.write(globalHandles.create(tree));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

}
