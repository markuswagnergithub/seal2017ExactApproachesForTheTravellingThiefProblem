package ttp.packing;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.NavigableMap;
import java.util.concurrent.ThreadLocalRandom;

import common.tools.Util;
import pwt.exact.dp.LookupTable;
import pwt.exact.dp.PackingPlanExtra;

public class Arrangement {

	private final LookupTable table;

	public Arrangement(LookupTable lookupTable) {
		this.table = lookupTable;
	}
	
	public double binarySearch() {
		return 0;
	}

	public double arrangeHeuristically(final List<Integer> stops, final List<Long> finalWeights, final BitSet packingPlan) {

		List<Long> weights = new ArrayList<>(stops.size()); //return values
		double benefit = arrangeInnerly(stops, weights, packingPlan);

		for (int i = 0; i < stops.size() - 1; i++) {
			
			
		}
		
		finalWeights.addAll(weights);
		

		return benefit;
	}
	
	
	/*******************************************
	 * 
	 * parameters:
	 * 	stops
	 * 	upperbounds
	 * 	lowerbounds
	 * 
	 * returns:
	 * 	weights
	 * 	packingPlan
	 * 	benefit
	 * 
	 ********************************************/
	private double arrangeInnerly(final List<Integer> stops, List<Long> weights, BitSet packingPlan) {
		double benefit = 0;
		long weight = 0;
		for (int i = 0; i < stops.size() - 1; i++) {
			System.out.println("------------------");
			System.out.println("Current stop:" + stops.get(i) + "\tNext Stop:" + stops.get(i + 1));
			long distance = table.ttp.distance(stops.get(i), stops.get(i + 1));
			PackingPlanExtra res = chooseWeightHeuristically(table.lookup(stops.get(i), weight, distance), weights);
			BitSet packing = res.solution();
			benefit += res.beta;
			packingPlan.or(packing);
			System.out.println(packingPlan);
			weight = weights.get(weights.size() - 1);
		}
		System.out.println("------------------");
		System.out.println("Current stop:" + stops.get(stops.size() - 1) + "\tNext Stop:" + stops.get(0));
		long distance = table.ttp.distance(stops.get(stops.size() - 1), stops.get(0));
		PackingPlanExtra res = chooseWeightHeuristically(table.lookup(stops.get(stops.size() - 1), weight, distance), weights);
		packingPlan.or(res.solution());
		benefit += res.beta;
		
		return benefit;
	}

	
	private PackingPlanExtra chooseWeightHeuristically(NavigableMap<Long, PackingPlanExtra> packings, List<Long> weights) {
		Util.printMap(packings);
		long weight = packings.floorKey((packings.firstKey() + packings.lastKey())/2);

		weights.add(weight);


		System.out.println("Chosen weight:" + weight);

		return packings.get(weight);
	}

	public BitSet arrangeRandomly(final List<Integer> stops, final List<Long> weights) {

		BitSet packingPlan = new BitSet(table.ttp.m);
		long weight = 0;
		for (int i = 0; i < stops.size() - 1; i++) {
			packingPlan.or(chooseWeightRandomly(stops.get(i), stops.get(i + 1), weights, weight).solution());
			weight = weights.get(weights.size() - 1);
		}
		packingPlan.or(chooseWeightRandomly(stops.get(stops.size() - 1), stops.get(0), weights, weight).solution());
		weight = weights.get(weights.size() - 1);

		return packingPlan;
	}

	private PackingPlanExtra chooseWeightRandomly(int curStop, int nextStop, List<Long> weights, long weight) {
		long distance = table.ttp.distance(curStop, nextStop);
		NavigableMap<Long, PackingPlanExtra> packings = table.lookup(curStop, weight, distance);
		if (packings.size() > 1) {
			long idx = ThreadLocalRandom.current().nextLong(packings.firstKey(), packings.lastKey());
			weight = packings.ceilingKey(idx);
		} else {
			weight = packings.firstKey();
		}
		weights.add(weight);

		System.out.println("------------------");
		System.out.println("Current stop:" + curStop + "\tNext Stop:" + nextStop);
		Util.printMap(packings);
		System.out.println("Chosen weight:" + weight);

		return packings.get(weight);
	}

}
