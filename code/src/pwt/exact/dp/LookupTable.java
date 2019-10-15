package pwt.exact.dp;

import java.util.BitSet;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Objects;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import common.tools.Util;
import ttp.problem.Tour;
import ttp.problem.Ttp;

public class LookupTable {
	
	public class PackingKey{
		public final long preloadedWeight;
		public final int curStop;
		public final long distanceToNextStop;
		private final int hashcode;
		
		public PackingKey(int curStop, long preloadedWeight, long distanceToNextStop){
			this.preloadedWeight = preloadedWeight;
			this.curStop = curStop;
			this.distanceToNextStop = distanceToNextStop;
			this.hashcode = Objects.hash(preloadedWeight, curStop, distanceToNextStop);
		}

		@Override
		public int hashCode() {
			return hashcode;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			PackingKey other = (PackingKey) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (curStop != other.curStop)
				return false;
			if (distanceToNextStop != other.distanceToNextStop)
				return false;
			if (preloadedWeight != other.preloadedWeight)
				return false;
			return true;
		}

		private LookupTable getOuterType() {
			return LookupTable.this;
		}

		@Override
		public String toString() {
			return "PackingKey [weight=" + preloadedWeight + ", curStop=" + curStop + ", distanceToNextStop=" + distanceToNextStop + "]";
		}
		
	}
	
	
	
	public final Ttp ttp;
	
	private final Map<PackingKey, NavigableMap<Long, PackingPlanExtra>> table = new ConcurrentHashMap<>();
	public final Map<Integer, NavigableMap<Long, PackingPlanExtra>> baseTable;
	
	public LookupTable(Ttp ttp){
		this(ttp, null);
	}
	
	public LookupTable(Ttp ttp, int[] stops){
		this.ttp = ttp;
		if (stops == null) {
			this.baseTable = Collections.unmodifiableMap(initialiseBaseTable());
		}else {
			this.baseTable = Collections.unmodifiableMap(initialiseBaseTable(stops));
		}
	}

	public Map<Integer, NavigableMap<Long, PackingPlanExtra>> initialiseBaseTable(int[] stops){
		Map<Integer, NavigableMap<Long, PackingPlanExtra>> baseTable = new HashMap<>();
		long[] adjacentDistances = new long[ttp.n];
		Tour.updateAdjacentDistances(adjacentDistances, ttp, stops);
		final long weightZero = 0;
		
		for (int i=0;i<stops.length;i++) {
			baseTable.put(stops[i], new TreeMap<>(packingDP(stops[i], weightZero, adjacentDistances[i])));
		}
		return baseTable;
	}
	
	
	public Map<Integer, NavigableMap<Long, PackingPlanExtra>> initialiseBaseTable() {
		Map<Integer, NavigableMap<Long, PackingPlanExtra>> baseTable = new HashMap<>();

		final long weightZero = 0;
		for (int curStop : ttp.cities.keySet()) {
			baseTable.put(curStop, new TreeMap<>(packingDP(curStop, weightZero, ttp.distanceToNearestCity(curStop))));
		}
		
		return baseTable;
	}

	
	public NavigableMap<Long, PackingPlanExtra> lookup(int curStop, long preloadedWeight, long distanceToNextStop) {
		PackingKey key = new PackingKey(curStop, preloadedWeight, distanceToNextStop);
		return table.computeIfAbsent(key, k -> buildEntry(curStop, preloadedWeight, distanceToNextStop));
	}
	
	
	public NavigableMap<Long, PackingPlanExtra> buildEntry(int currentStop, long preloadedWeight, long distance) {
		NavigableMap<Long, PackingPlanExtra> mapzero = baseTable.get(currentStop);
		
		Map<Long, PackingPlanExtra> map = new HashMap<>();
		for (PackingPlanExtra p: mapzero.values()) {
			double b = ttp.benefit(p.profit, preloadedWeight + p.weight, distance);
			map.put(preloadedWeight + p.weight, new PackingPlanExtra(b, p.solution(), p.profit, p.weight));
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
		
		return new TreeMap<>(map).subMap(treeSet.first(), true, ttp.bigW, true);
	}
	
	
	public void print(){
		System.out.println("size:" + table.size());
		Util.printMap(table);
	}
	
	
	public Map<Long, PackingPlanExtra> packingDP(int curStop, long preloadedWeight, long distanceToNextStop) {

		Map<Long, PackingPlanExtra> preCol = new HashMap<>();

		double b = - distanceToNextStop * ttp.rentingRate / (ttp.maxSpeed - ttp.nu * preloadedWeight);
		preCol.put(preloadedWeight, new PackingPlanExtra(b, new BitSet(ttp.m), 0L, 0L));
		

		Map<Long, PackingPlanExtra> curCol = null;
		for (int k = 0; k < ttp.getCity(curStop).noOfItemsInCity(); k++) {
			curCol = new HashMap<>(preCol);

			final int itemIdx = ttp.getCity(curStop).getItem(k);
			final long itemWeight = ttp.getItem(itemIdx).weight;
			final long profit = ttp.getItem(itemIdx).profit;

			for (long weight : preCol.keySet()) {
				long w = weight + itemWeight;
				if (w > ttp.bigW)
					continue;
				b = profit - (distanceToNextStop * (ttp.rentingRate / (ttp.maxSpeed - (ttp.nu * w))))
						+ (distanceToNextStop * (ttp.rentingRate / (ttp.maxSpeed - (ttp.nu * weight))));

				if (b < 0) { // in this case
					continue; // as beta(k, w) is dominated by beta(k, weight),
				} // skip the rest.

				b += preCol.get(weight).beta;

				if (!curCol.containsKey(w) || b > curCol.get(w).beta) {
					PackingPlanExtra pre = preCol.get(weight);
					BitSet bs = pre.solution();
					bs.set(itemIdx);
					curCol.put(w, new PackingPlanExtra(b, bs, pre.profit + profit, pre.weight + itemWeight));
				}
			}

			TreeSet<Long> treeSet = new TreeSet<Long>(curCol.keySet());
			b = curCol.get(treeSet.first()).beta;
			for (long key : treeSet) {
				if (curCol.get(key).beta < b) {
					curCol.remove(key);
				} else {
					b = curCol.get(key).beta;
				}
			}

			preCol = curCol;
		}
		
		return preCol;
	}
	
}
