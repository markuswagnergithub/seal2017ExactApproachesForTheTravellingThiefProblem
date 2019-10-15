package ttp.problem;


import java.util.BitSet;

import org.junit.Assert;
import org.junit.Test;

import common.tools.Util;


public class TtpTest {
	
	private static final double delta = 1e-10;

	@Test
	public void testBenefitIntArrayBitSet() {
		String problemFile = "../../multiobj/experiments/eil76/eil76_n75_uncorr_01.ttp";
		
		Ttp ttp = TtpLoader.load(problemFile);
		
		int[] stops = new int[] {1, 62, 73, 33, 63, 16, 3, 44, 32, 9, 39, 72, 58, 12, 40, 17, 51, 6, 68, 2, 30, 48, 29, 45, 27, 57, 15, 5, 37, 20, 70, 60, 71, 69, 36, 47, 21, 61, 64, 41, 56, 23, 49, 24, 18, 50, 25, 55, 31, 10, 38, 65, 11, 66, 59, 14, 53, 7, 35, 8, 19, 54, 13, 52, 46, 34, 67, 26, 76, 75, 4, 74, 28, 22, 42, 43};
		BitSet packingPlan = Util.bitSet(new int[] {1, 3, 7, 12, 18, 21, 23, 27, 33, 36, 41, 42, 51, 53, 63, 74, 75});

		double benefit = ttp.benefit(stops, packingPlan);
		
		Assert.assertEquals(5508.865173099702, benefit, delta);
		
		System.out.println(benefit);
		
	}
	
	@Test
	public void testBestBenefitIntArrayBitSet() {
		String problemFile = "../../multiobj/experiments/eil76/eil76_n75_uncorr_01.ttp";
		
		Ttp ttp = TtpLoader.load(problemFile);
		
		int[] stops = new int[] {1, 62, 73, 33, 63, 16, 3, 44, 32, 9, 39, 72, 58, 12, 40, 17, 51, 6, 68, 2, 30, 48, 29, 45, 27, 46, 67, 26, 7, 35, 53, 14, 59, 66, 11, 65, 38, 10, 31, 55, 25, 50, 18, 24, 49, 23, 56, 41, 64, 61, 21, 47, 36, 69, 71, 60, 70, 20, 37, 5, 15, 57, 13, 54, 19, 8, 52, 34, 76, 75, 4, 74, 28, 22, 42, 43};
		BitSet packingPlan = Util.bitSet(new int[] {1, 3, 7, 12, 18, 21, 23, 27, 33, 36, 41, 42, 51, 53, 63, 74, 75});

		double benefit = ttp.benefit(stops, packingPlan);
		
		Assert.assertEquals(5514.665860559197, benefit, delta);
		
		System.out.println(benefit);
		
	}
	
	@Test
	public void testDistanceToNearestCity() {
		String problemFile = "../../multiobj/experiments/eil76/eil76_n75_uncorr_01.ttp";
		
		Ttp ttp = TtpLoader.load(problemFile);
		
		Assert.assertEquals(6, ttp.distanceToNearestCity(1));
		System.out.println("-----");
		System.out.println(ttp.distanceToNearestCity(2));
		
	}

}
