package ttp.problem;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;

import org.junit.Assert;
import org.junit.Test;

import pwt.exact.dp.DynamicProgramming;
import ttp.problem.Ttp;
import ttp.problem.TtpLoader;
import ttp.problem.Tour;

public class TourTest {
	private static final double delta = 1e-10;
	
	@Test
	public void testUpdateIntArray() {
		fail("Not yet implemented");
	}

	@Test
	public void testUpdateIntArrayLongArrayLongArray() {
		fail("Not yet implemented");
	}

	@Test
	public void testTotalDistance() {
		fail("Not yet implemented");
	}

	@Test
	public void testUpdateAdjacentDistances() {
		fail("Not yet implemented");
	}

	@Test
	public void testUpdateAdjacentDistancesIntArray() {
		fail("Not yet implemented");
	}

	@Test
	public void testUpdateItemsByTour() {
		fail("Not yet implemented");
	}

	@Test
	public void testUpdateTraveledDistances() {
		fail("Not yet implemented");
	}
	

	@Test
	public void tourTest() throws FileNotFoundException {

		String instance = "eil51_n50_uncorr_01.ttp";
		Ttp problem = TtpLoader.loadProblem("experiments/eil51/" + instance);
		
		Tour tour = new Tour(problem);
		
		int[] stops = {1, 22, 2, 16, 50, 9, 30, 34, 21, 29, 20, 35, 36, 3, 28, 31, 8, 26, 7, 43, 24, 23, 48, 6, 27, 51, 46, 12, 47, 4, 18, 14, 25, 13, 41, 19, 40, 42, 44, 17, 37, 15, 45, 33, 39, 10, 49, 5, 38, 11, 32};
		tour.update(stops);

		DynamicProgramming dp = new DynamicProgramming(tour);
		dp.dynamicProgramming();
		System.out.println(dp.getSolution());
		System.out.println(dp.getTotalWeight());
		System.out.println(dp.getTotalProfit());
		System.out.println(dp.getMaxBenefit());
		String record = "blah" + "\t" + dp.getMaxBenefit() + "\t"
				+ dp.getTotalWeight() + "\t" + dp.getTotalProfit()
				+ "\t" + dp.getElapsedTime() + "\t" + dp.getSolution();
		System.out.println(record);
		
		Assert.assertEquals("10000010000100000100001000000010100100000000000100", dp.getSolution());
		Assert.assertEquals(1988, dp.getTotalWeight());
		Assert.assertEquals(6329, dp.getTotalProfit());
		Assert.assertEquals(1834.4591639793941, dp.getMaxBenefit(), delta);
	}
	

}
