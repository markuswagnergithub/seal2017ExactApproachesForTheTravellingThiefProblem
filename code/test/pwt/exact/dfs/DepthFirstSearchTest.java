package pwt.exact.dfs;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;

import org.junit.Assert;
import org.junit.Test;

import common.tools.Util;
import pwt.exact.dfs.DepthFirstSearch.Node;
import pwt.exact.dp.LookupTable;
import pwt.exact.dp.PackingPlanExtra;
import ttp.problem.Tour;
import ttp.problem.TourLoader;
import ttp.problem.Ttp;
import ttp.problem.TtpLoader;
import ttp.tour.TourType;

public class DepthFirstSearchTest {
	private static final double delta = 1e-10;
	
	@Test
	public void testUpperBounds() {
		String problemFile = "experiments/eil51/eil51_n50_uncorr_01.ttp";
//		String problemFile = "experiments/test/test1_n5_m8.ttp";
		
		String tourFile = "experiments/eil51/eil51.linkern.tour";
		
		Ttp ttp = TtpLoader.load(problemFile);
		int[] stops = TourLoader.load(tourFile, TourType.lkh);
		
		LookupTable table = new LookupTable(ttp, stops);
		
		DepthFirstSearch dfs = new DepthFirstSearch(ttp, stops);
		
		for (int i: stops) {
			System.out.println("city:" + i);
			NavigableMap<Long, PackingPlanExtra> m = table.baseTable.get(i);
			System.out.println(m.lastEntry().getValue());
		}
		
		System.out.println(Arrays.toString(dfs.upperBounds(table.baseTable)));
		
		
	}
	
	
	@Test
	public void testSearch() {
		String problemFile = "experiments/eil51/eil51_n500_uncorr_01.ttp";
//		String problemFile = "experiments/test/test1_n5_m8.ttp";
		
		String tourFile = "experiments/eil51/eil51.linkern.tour";
		
		Ttp ttp = TtpLoader.load(problemFile);
		int[] stops = TourLoader.load(tourFile, TourType.lkh);
//		System.out.println(Arrays.toString(stops));
//		int[] stops = new int[] {1, 2, 3, 4, 5};
		
		DepthFirstSearch dfs = new DepthFirstSearch(ttp, stops);
		
		Node best = dfs.search();
		System.out.println(best);
		
		Assert.assertEquals(1988, best.weight);
		Assert.assertEquals(6329, best.profit);
		Assert.assertEquals(1840.2105818409516, best.beta, delta);
		Assert.assertEquals(1840.2105818409516, ttp.benefit(stops, best.solution()), delta);
		
	}
	
	
	@Test
	public void testTest1n4m6() {
//		String problemFile = "experiments/eil51/eil51_n50_uncorr_01.ttp";
		String problemFile = "experiments/test/test1_n4_m6.ttp";
		
//		String tourFile = "experiments/eil51/eil51.linkern.tour";
		
		Ttp ttp = TtpLoader.load(problemFile);
//		int[] stops = TourLoader.load(tourFile, TourType.lkh);
//		System.out.println(Arrays.toString(stops));
		int[] stops = new int[] {1, 2, 3, 4};
		
		DepthFirstSearch dfs = new DepthFirstSearch(ttp, stops);
		
		Node best = dfs.search();
		System.out.println(best);
		
		Assert.assertEquals(1988, best.weight);
		Assert.assertEquals(6329, best.profit);
		Assert.assertEquals(1840.2105818409516, best.beta, delta);
		Assert.assertEquals(1840.2105818409516, ttp.benefit(stops, best.solution()), delta);
		
	}

	@Test
	public void testGetChildren() {
		
		String problemFile = "experiments/eil76/eil76_n750_uncorr_10.ttp";
		String tourFile = "experiments/eil76/eil76.linkern.tour";
		
		Ttp ttp = TtpLoader.load(problemFile);
		int[] stops = TourLoader.load(tourFile, TourType.lkh);
		
		DepthFirstSearch dfs = new DepthFirstSearch(ttp, stops);
		
		LookupTable table = new LookupTable(ttp);
		
		long[] adjacentDistances = new long[ttp.n];
		Tour.updateAdjacentDistances(adjacentDistances, ttp, stops);
		
		Collection<PackingPlanExtra> root = table.baseTable.get(stops[0]).values();
		
		List<Node> nodes = dfs.getChildren(dfs.new Node(stops[0], root.iterator().next()), table, adjacentDistances);
		
		for (Node n: nodes) {
			System.out.println(n);
		}
		
		
	}
	
}
