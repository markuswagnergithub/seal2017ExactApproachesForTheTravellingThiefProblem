package pwt.exact.dp;

import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.junit.Test;

import common.tools.Util;
import pwt.exact.dp.LookupTable;
import pwt.exact.dp.PackingPlanExtra;
import ttp.problem.Ttp;
import ttp.problem.Ttp.BenefitAndWeight;
import ttp.problem.TtpLoader;

public class LookupTest {

	@Test
	public void testPackingDP() {
//		String problemFileName = "experiments/eil76/eil76_n75_uncorr_01.ttp";
		String problemFileName = "experiments/test/test1_n8_m14.ttp";
		
		Ttp ttp = TtpLoader.load(problemFileName);
		
		LookupTable lookup = new LookupTable(ttp);
		
		long w = 0;
		int i = 2, j= 1;
		long distance = ttp.distance(i, j);
		System.out.println("distance:" + distance);
		System.out.println(lookup.lookup(i, w, distance));
		lookup.print();
		
	}
	
	@Test
	public void testLookup() {
//		String problemFile = "experiments/eil76/eil76_n75_uncorr_01.ttp";
		String problemFile = "experiments/eil76/eil76_n750_uncorr_10.ttp";
//		String problemFile = "experiments/test/test1_n8_m14.ttp";
		
		Ttp ttp = TtpLoader.load(problemFile);
		
		LookupTable lookup = new LookupTable(ttp);
		int i = 3, j = 16;
		long zeroWeight = 0;
		long distance = ttp.distance(i, j);
		System.out.println("distance:" + distance);
		NavigableMap<Long, PackingPlanExtra> mapzero = lookup.buildEntry(i, zeroWeight, distance);
//		long[] mapzerokeys = mapzero.navigableKeySet().stream().mapToLong(Long::valueOf).toArray();
		Set<BitSet> zeroPackingPlanExtraSet = new HashSet<>(mapzero.size());
		for (PackingPlanExtra pp: mapzero.values()) {
			zeroPackingPlanExtraSet.add(pp.solution());
		}
		
		int currentSize = mapzero.size();
		System.out.println("current size:" + currentSize);
		Util.printMap(mapzero);
		

		for (Long w = zeroWeight + 1;w <= ttp.capacity;w++) {
//			System.out.println("weight:" + w);
			
			NavigableMap<Long, PackingPlanExtra> map = lookup.buildEntry(i, w, distance);
//			long[] mapkeys = map.navigableKeySet().stream().mapToLong(Long::valueOf).toArray();
			
			if (currentSize != map.size()) {
				currentSize = map.size();
				System.out.println("current size:" + currentSize);
				System.out.println("weight:" + w);
				Util.printMap(map);
			}
			
			map.values().parallelStream().forEach((pp)->{
				if (!zeroPackingPlanExtraSet.contains(pp.solution())) {
					System.out.println("#########################");
					System.out.println(pp.solution());
					System.out.println("#########################");
				}
			});

			
//			Assert.assertEquals(mapzero.size(), map.size());
//			for (int k=0;k<mapzerokeys.length;k++) {
//				Assert.assertEquals(mapzerokeys[i] + w - zeroWeight, mapkeys[i]);
//				Assert.assertEquals(mapzero.get(mapzerokeys[i]).solution(), map.get(mapkeys[i]).solution());
//			}

			// lookup.print();
		};
	}
	
	public NavigableMap<Long, PackingPlanExtra> buildTable(Ttp ttp, long weight, int currentStop, int nextStop, Set<BitSet> packingPlans) {
		Map<Long, PackingPlanExtra> map = new HashMap<>();

		int d = ttp.distance(currentStop, nextStop);
		for (BitSet p: packingPlans) {
			BenefitAndWeight bnw = ttp.benefitAtStop(currentStop, weight, d, p);
			map.put(bnw.weight, new PackingPlanExtra(bnw.benefit, p, 0, 0));
		}
		
		TreeSet<Long> treeSet = new TreeSet<Long>(map.keySet());
		double b = map.get(treeSet.first()).beta;
		for (long key : treeSet) {
			if (map.get(key).beta < b) {
				map.remove(key);
			} else {
				b = map.get(key).beta;
			}
		}
		
		return new TreeMap<>(map).subMap(treeSet.first(), true, ttp.capacity, true);
	}
	
	
	@Test
	public void testLookup1() {
//		String problemFile = "experiments/eil76/eil76_n75_uncorr_01.ttp";
		String problemFile = "experiments/eil76/eil76_n750_uncorr_10.ttp";
//		String problemFile = "experiments/test/test1_n8_m14.ttp";
		
		Ttp ttp = TtpLoader.load(problemFile);
		
		LookupTable lookup = new LookupTable(ttp);
		int i = 3, j = 16;
		long zeroWeight = 0;
		long distance = ttp.distance(i, j);
		System.out.println("distance:" + distance);
		
		TreeMap<Long, PackingPlanExtra> mapzero = new TreeMap<>(lookup.packingDP(i, zeroWeight, distance));

		Set<BitSet> zeroPackingPlanExtraSet = new HashSet<>(mapzero.size());
		for (PackingPlanExtra pp: mapzero.values()) {
			zeroPackingPlanExtraSet.add(pp.solution());
		}
		
		int currentSize = mapzero.size();
		System.out.println("current size:" + currentSize);
		Util.printMap(mapzero);
		
		long[] weights = new long[] {247792,255919,297905,302332,304414,305341,309545,322430,322714,323357,323572,323835,327140,328307,331051,333967,334021,334025,334879,334883,335453,335457,335716,335793,335797,336056,336060,336062,336066,336128,336132,336134};
		
		for (Long w :weights) {
//			System.out.println("weight:" + w);
			
			NavigableMap<Long, PackingPlanExtra> map = buildTable(ttp, w, i, j, zeroPackingPlanExtraSet);
			
			System.out.println("current size:" + map.size());
			System.out.println("weight:" + w);
			Util.printMap(map);
			
			
			map.values().parallelStream().forEach((pp)->{
				if (!zeroPackingPlanExtraSet.contains(pp.solution())) {
					System.out.println("#########################");
					System.out.println(pp.solution());
					System.out.println("#########################");
				}
			});

			

		};
	}

	@Test
	public void testBuildEntry() {
		String problemFile = "experiments/eil76/eil76_n750_uncorr_10.ttp";
//		String problemFile = "experiments/test/test1_n8_m14.ttp";
		
		Ttp ttp = TtpLoader.load(problemFile);
		
		LookupTable lookup = new LookupTable(ttp);
		
		int i = 3, j = 16;
		long zeroWeight = 0;
		long distance = ttp.distance(i, j);
		System.out.println("distance:" + distance);
		
		NavigableMap<Long, PackingPlanExtra> mapzero = lookup.buildEntry(i, zeroWeight, distance);
		System.out.println("current size:" + mapzero.size());
		Util.printMap(mapzero);
		
		zeroWeight = 247792;
		mapzero = lookup.buildEntry(i, zeroWeight, distance);
		
		System.out.println("current size:" + mapzero.size());
		Util.printMap(mapzero);
		
	}
	
	
}
