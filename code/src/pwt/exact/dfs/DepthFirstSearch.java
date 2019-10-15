package pwt.exact.dfs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;

import common.tools.Util;
import pwt.exact.dp.LookupTable;
import pwt.exact.dp.PackingPlanExtra;
import ttp.problem.Tour;
import ttp.problem.Ttp;

public class DepthFirstSearch {
	
	public class Node extends PackingPlanExtra{
		public final int curStopIdx;

		public Node(int curStopIdx, PackingPlanExtra ppe) {
			super(ppe);
			this.curStopIdx = curStopIdx;
		}
		
		public Node(int curStopIdx, double benefit, BitSet packingPlan, long profit, long weight) {
			super(benefit, packingPlan, profit, weight);
			this.curStopIdx = curStopIdx;
		}

		@Override
		public String toString() {
			return "Node [curStopIdx=" + curStopIdx + ", profit=" + profit + ", weight=" + weight + ", beta=" + beta
					+ ", solution=" + solution() + "]";
		}
	}
	
	public final Ttp ttp;
	public final int[] stops;
	
	public DepthFirstSearch(Ttp ttp, int[] stops) {
		this.ttp = ttp;
		this.stops = stops;
	}
	
	
	
	public Node search() {
		LookupTable table = new LookupTable(ttp, stops);
		
		double[] lbs = upperBounds(table.baseTable);
		long[] adjacentDistances = new long[ttp.n];
		Tour.updateAdjacentDistances(adjacentDistances, ttp, stops);
		
		LinkedList<Node> stack = new LinkedList<>();
		Collection<PackingPlanExtra> root = table.baseTable.get(stops[0]).values();
		for (PackingPlanExtra ppe:root) {
			stack.add(new Node(0, ppe));
		}
		
//		System.out.println(stack);
		
		Node bestNode = null;
		double bestBenefit = - Double.MAX_VALUE;
		
		while (!stack.isEmpty()) {
			Node current = stack.removeFirst();
			if (current.curStopIdx == stops.length - 1) {
				//is leaf
				if (current.beta > bestBenefit) {
					bestBenefit = current.beta;
					bestNode = current;
//					System.out.println("best so far:" + bestNode);
				}
			}else {
				List<Node> children = getChildren(current, table, adjacentDistances);
				for (Node node:children) {
					if (node.beta + ub (node.curStopIdx, node.weight, table, adjacentDistances) > bestBenefit) {
						stack.addFirst(node);
					}
				}
			}
//			System.out.println("current:" + current);
//			for (Node node:stack) {
//				System.out.println(node);
//			}
//			System.out.println(stack.size());
		}
		return bestNode;
	}
	
	public double ub (int curStopIdx, long loadedWeight, LookupTable table, long[]  adjacentDistances) {
		double ub = 0;
		for (int i=curStopIdx + 1; i<stops.length;i++) {
			NavigableMap<Long, PackingPlanExtra> m =table.buildEntry(stops[i], loadedWeight, adjacentDistances[i]);
			ub += m.lastEntry().getValue().beta;
		}
		return ub;
	}

	public double[] upperBounds(Map<Integer, NavigableMap<Long, PackingPlanExtra>> baseTable) {
		double[] benefits = new double[stops.length];
		for (int i = 0;i< stops.length;i++) {
			NavigableMap<Long, PackingPlanExtra> m = baseTable.get(stops[i]);
			benefits[i] = m.lastEntry().getValue().beta;
		}
//		System.out.println(Arrays.toString(benefits));
		double [] res = new double[stops.length];
		for (int i = 0; i < res.length; i++) {
			for (int j = i; j < benefits.length; j++) {
				res[i] += benefits[j];
			}
		}
		
		return res;
	}



	public List<Node> getChildren(Node current, LookupTable table, long[] adjacentDistances) {
		final int nextStopIdx = current.curStopIdx + 1;
		NavigableMap<Long, PackingPlanExtra> map = table.buildEntry(stops[nextStopIdx], current.weight,
				adjacentDistances[nextStopIdx]);
//		System.out.println("current city:" + stops[current.curStopIdx]);
//		System.out.println("next city:" + stops[current.curStopIdx + 1]);
//		System.out.println("distance:" + adjacentDistances[nextStopIdx]);

		List<Node> res = new ArrayList<>(map.size());
		BitSet prePackingPlan = current.solution();

		for (PackingPlanExtra entry : map.values()) {
//			System.out.println("entry:"+entry);
			BitSet packingPlan = entry.solution();
			packingPlan.or(prePackingPlan);

			res.add(new Node(nextStopIdx, current.beta + entry.beta, packingPlan,
					current.profit + entry.profit, current.weight + entry.weight));
		}
		
		return res;
	}
	
}
