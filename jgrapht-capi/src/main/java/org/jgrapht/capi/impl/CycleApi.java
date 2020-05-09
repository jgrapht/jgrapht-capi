package org.jgrapht.capi.impl;

import java.util.Set;

import org.graalvm.nativeimage.IsolateThread;
import org.graalvm.nativeimage.ObjectHandle;
import org.graalvm.nativeimage.ObjectHandles;
import org.graalvm.nativeimage.c.function.CEntryPoint;
import org.graalvm.nativeimage.c.type.CDoublePointer;
import org.graalvm.nativeimage.c.type.CIntPointer;
import org.graalvm.nativeimage.c.type.WordPointer;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.cycle.ChinesePostman;
import org.jgrapht.alg.cycle.DirectedSimpleCycles;
import org.jgrapht.alg.cycle.HawickJamesSimpleCycles;
import org.jgrapht.alg.cycle.HierholzerEulerianCycle;
import org.jgrapht.alg.cycle.JohnsonSimpleCycles;
import org.jgrapht.alg.cycle.PatonCycleBase;
import org.jgrapht.alg.cycle.QueueBFSFundamentalCycleBasis;
import org.jgrapht.alg.cycle.StackBFSFundamentalCycleBasis;
import org.jgrapht.alg.cycle.SzwarcfiterLauerSimpleCycles;
import org.jgrapht.alg.cycle.TarjanSimpleCycles;
import org.jgrapht.alg.cycle.TiernanSimpleCycles;
import org.jgrapht.alg.interfaces.CycleBasisAlgorithm;
import org.jgrapht.alg.interfaces.CycleBasisAlgorithm.CycleBasis;
import org.jgrapht.capi.Constants;
import org.jgrapht.capi.JGraphTContext.Status;
import org.jgrapht.capi.error.StatusReturnExceptionHandler;

public class CycleApi {

	private static ObjectHandles globalHandles = ObjectHandles.getGlobal();

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "cycles_eulerian_exec_hierholzer", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeHierholzer(IsolateThread thread, ObjectHandle graphHandle, CIntPointer isEulerianRes,
			WordPointer eulerianCycleRes) {
		Graph<Long, Long> g = globalHandles.get(graphHandle);

		HierholzerEulerianCycle<Long, Long> alg = new HierholzerEulerianCycle<>();

		GraphPath<Long, Long> eulerianCycle = null;
		try {
			eulerianCycle = alg.getEulerianCycle(g);
		} catch (IllegalArgumentException e) {
			// nothing
		}

		if (eulerianCycle != null) {
			if (isEulerianRes.isNonNull()) {
				isEulerianRes.write(1);
			}
			if (eulerianCycleRes.isNonNull()) {
				eulerianCycleRes.write(globalHandles.create(eulerianCycle));
			}
		} else {
			if (isEulerianRes.isNonNull()) {
				isEulerianRes.write(0);
			}
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "cycles_chinese_postman_exec_edmonds_johnson", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeChinesePostman(IsolateThread thread, ObjectHandle graphHandle, WordPointer res) {
		Graph<Long, Long> g = globalHandles.get(graphHandle);

		ChinesePostman<Long, Long> alg = new ChinesePostman<>();
		GraphPath<Long, Long> path = alg.getCPPSolution(g);

		if (res.isNonNull()) {
			res.write(globalHandles.create(path));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "cycles_simple_enumeration_exec_tarjan", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeTarjan(IsolateThread thread, ObjectHandle graphHandle, WordPointer res) {
		Graph<Long, Long> g = globalHandles.get(graphHandle);
		DirectedSimpleCycles<Long, Long> alg = new TarjanSimpleCycles<>(g);
		if (res.isNonNull()) {
			res.write(globalHandles.create(alg.findSimpleCycles().iterator()));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "cycles_simple_enumeration_exec_tiernan", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeTierman(IsolateThread thread, ObjectHandle graphHandle, WordPointer res) {
		Graph<Long, Long> g = globalHandles.get(graphHandle);
		DirectedSimpleCycles<Long, Long> alg = new TiernanSimpleCycles<>(g);
		if (res.isNonNull()) {
			res.write(globalHandles.create(alg.findSimpleCycles().iterator()));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "cycles_simple_enumeration_exec_szwarcfiter_lauer", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeSzwarcfiterLauer(IsolateThread thread, ObjectHandle graphHandle, WordPointer res) {
		Graph<Long, Long> g = globalHandles.get(graphHandle);
		DirectedSimpleCycles<Long, Long> alg = new SzwarcfiterLauerSimpleCycles<>(g);
		if (res.isNonNull()) {
			res.write(globalHandles.create(alg.findSimpleCycles().iterator()));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "cycles_simple_enumeration_exec_johnson", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeJohnson(IsolateThread thread, ObjectHandle graphHandle, WordPointer res) {
		Graph<Long, Long> g = globalHandles.get(graphHandle);
		DirectedSimpleCycles<Long, Long> alg = new JohnsonSimpleCycles<>(g);
		if (res.isNonNull()) {
			res.write(globalHandles.create(alg.findSimpleCycles().iterator()));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "cycles_simple_enumeration_exec_hawick_james", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeHawickJames(IsolateThread thread, ObjectHandle graphHandle, WordPointer res) {
		Graph<Long, Long> g = globalHandles.get(graphHandle);
		DirectedSimpleCycles<Long, Long> alg = new HawickJamesSimpleCycles<>(g);
		if (res.isNonNull()) {
			res.write(globalHandles.create(alg.findSimpleCycles().iterator()));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "cycles_fundamental_basis_exec_queue_bfs", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeQueueBFSFundamental(IsolateThread thread, ObjectHandle graphHandle,
			CDoublePointer weightRes, WordPointer res) {
		Graph<Long, Long> g = globalHandles.get(graphHandle);

		CycleBasisAlgorithm<Long, Long> alg = new QueueBFSFundamentalCycleBasis<>(g);
		CycleBasis<Long, Long> cycleBasis = alg.getCycleBasis();
		double weight = cycleBasis.getWeight();
		Set<GraphPath<Long, Long>> cycles = cycleBasis.getCyclesAsGraphPaths();

		if (weightRes.isNonNull()) {
			weightRes.write(weight);
		}
		if (res.isNonNull()) {
			res.write(globalHandles.create(cycles.iterator()));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "cycles_fundamental_basis_exec_stack_bfs", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeStackBFSFundamental(IsolateThread thread, ObjectHandle graphHandle,
			CDoublePointer weightRes, WordPointer res) {
		Graph<Long, Long> g = globalHandles.get(graphHandle);

		CycleBasisAlgorithm<Long, Long> alg = new StackBFSFundamentalCycleBasis<>(g);
		CycleBasis<Long, Long> cycleBasis = alg.getCycleBasis();
		double weight = cycleBasis.getWeight();
		Set<GraphPath<Long, Long>> cycles = cycleBasis.getCyclesAsGraphPaths();

		if (weightRes.isNonNull()) {
			weightRes.write(weight);
		}
		if (res.isNonNull()) {
			res.write(globalHandles.create(cycles.iterator()));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}
	
	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "cycles_fundamental_basis_exec_paton", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executePatonFundamental(IsolateThread thread, ObjectHandle graphHandle,
			CDoublePointer weightRes, WordPointer res) {
		Graph<Long, Long> g = globalHandles.get(graphHandle);

		CycleBasisAlgorithm<Long, Long> alg = new PatonCycleBase<>(g);
		CycleBasis<Long, Long> cycleBasis = alg.getCycleBasis();
		double weight = cycleBasis.getWeight();
		Set<GraphPath<Long, Long>> cycles = cycleBasis.getCyclesAsGraphPaths();

		if (weightRes.isNonNull()) {
			weightRes.write(weight);
		}
		if (res.isNonNull()) {
			res.write(globalHandles.create(cycles.iterator()));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}


}
