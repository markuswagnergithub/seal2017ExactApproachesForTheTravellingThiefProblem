package ttp.packing;


import java.math.BigInteger;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import common.tools.Util;
import pwt.exact.dp.LookupTable;
import ttp.problem.TourLoader;
import ttp.problem.Ttp;
import ttp.problem.TtpLoader;
import ttp.tour.TourType;

public class ArrangementTest {
	
	private static final double epsilon = 1e-10;

	@Test
	public void testArrangeRandomly() {
		
//		String problemFile = "experiments/test/test1_n8_m14.ttp";
		String problemFile = "experiments/eil76/eil76_n75_uncorr_01.ttp";
		String tourFile = "experiments/eil76/eil76.tsp.1.con.tour";
		
		
		Ttp ttp = TtpLoader.load(problemFile);
		
		LookupTable lookup = new LookupTable(ttp);
		
		Arrangement ag = new Arrangement(lookup);
		
//		List<Integer> tour = Util.randomPermutation(ttp.n);
		
		List<Integer> tour = Util.asList(TourLoader.load(tourFile, TourType.con));
		List<Long> weights = new ArrayList<>(tour.size());
		BitSet packingPlan = ag.arrangeRandomly(tour, weights);
		
		System.out.println("tour:" + tour);
		System.out.println("weights:" + weights);
		System.out.println("PackingPlan:" + packingPlan);
	}
	
	
	@Test
	public void testArrangeHeuristically() {
		
//		String problemFile = "experiments/test/test1_n8_m14.ttp";
		String problemFile = "experiments/eil76/eil76_n750_uncorr_10.ttp";
		String tourFile = "experiments/eil76/eil76.tsp.1.con.tour";
		
		
		Ttp ttp = TtpLoader.load(problemFile);
		
		LookupTable lookup = new LookupTable(ttp);
		
		Arrangement ag = new Arrangement(lookup);
		
//		List<Integer> tour = Util.randomPermutation(ttp.n);
		
		List<Integer> tour = Util.asList(TourLoader.load(tourFile, TourType.con));
		List<Long> weights = new ArrayList<>(tour.size());
		System.out.println("tour:" + tour);
		
		BitSet packingPlan = new BitSet(ttp.m); 
		double benefit = ag.arrangeHeuristically(tour, weights, packingPlan);
		
		System.out.println("benefit:" + benefit);
		System.out.println("weights:" + weights);
		System.out.println("PackingPlan:" + packingPlan);
		
		Assert.assertEquals(benefit, ttp.benefit(tour, packingPlan), epsilon);
		Assert.assertEquals(weights.get(weights.size() - 1).longValue(), ttp.totalWeight(packingPlan));
	}
	
	@Test
	public void testBigIntegerToBitSet(){
		int[] items = new int[]{77, 86, 95};
		BigInteger bInt = BigInteger.ZERO;
		for (int i: items){
			bInt = bInt.setBit(i);
		}
		
		System.out.println(bInt.toString(2));
		BitSet bSet = BitSet.valueOf(bInt.toByteArray());
		
		System.out.println(bSet);
		
	}

}
