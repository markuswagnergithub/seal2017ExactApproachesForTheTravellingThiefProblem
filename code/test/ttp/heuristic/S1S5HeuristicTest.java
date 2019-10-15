package ttp.heuristic;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import common.tools.Util;
import ttp.problem.Ttp;
import ttp.problem.TtpLoader;
import ttp.problem.Tour;
import ttp.tour.TspSolver;

public class S1S5HeuristicTest {
	
	private static final double delta = 1e-10;

	@Test
	public void testS1Heuristic() {
		
		Ttp problem = TtpLoader.load("experiments/eil76/eil76_n75_uncorr_01.ttp");
		
		S1S5Heuristic heuristic = new S1S5Heuristic(problem, TspSolver.SolverName.CONCORDE, S1S5Heuristic.TEN_MINUTES);
		
		
		S1S5Heuristic.Solution solution = heuristic.S1Heuristic();
		
		System.out.println(solution);
		
		Assert.assertEquals(3727.390754703563, solution.benefit, delta);
		
		Assert.assertArrayEquals(new int[]{1, 73, 62, 22, 64, 42, 43, 41, 56, 23, 49, 24, 18, 50, 25, 55, 31, 10, 38, 65, 11, 66, 59, 14, 53, 7, 35, 8, 19, 54, 13, 57, 15, 5, 37, 20, 70, 60, 71, 69, 36, 47, 21, 61, 28, 74, 2, 30, 48, 29, 45, 27, 52, 46, 34, 67, 26, 76, 75, 4, 68, 6, 51, 17, 40, 12, 58, 72, 39, 9, 32, 44, 3, 16, 63, 33}, solution.getTour());
		
		Assert.assertArrayEquals(new int[]{1, 3, 7, 8, 12, 15, 18, 23, 33, 36, 51, 62, 63, 74, 75}, solution.getPackingPlan());
		
	}
	
	public double calculateBenefit(Tour tour, int[] packingPlan) {
		
		Ttp ttp = tour.problem;
		
		System.out.println(tour);
		
		return ttp.benefit(tour.essence.getStops(), Util.bitSet(packingPlan));
		
	}

	@Test
	public void verifyTest1n4m3Result(){
		Ttp problem = TtpLoader.load("experiments/test/test1_n4_m3.ttp");
		
		Tour tour = new Tour(problem);
		
		tour.update(new int[]{1,2,3,4});
		
		int[] packingPlan = new int[]{2,3};
		
		Assert.assertEquals(-86, calculateBenefit(tour, packingPlan), delta);

	}
	
	
	
	@Test
	public void verifyEil76n75uncorr01Result(){
		Ttp problem = TtpLoader.load("experiments/eil76/eil76_n75_uncorr_01.ttp");
		
		Tour tour = new Tour(problem);
		
		tour.update(new int[]{1, 33, 63, 16, 3, 44, 32, 9, 39, 72, 58, 12, 40, 17, 51, 6, 68, 4, 75, 76, 67, 26, 7, 35, 53, 14, 59, 66, 11, 65, 38, 10, 31, 55, 25, 50, 18, 24, 49, 23, 56, 41, 43, 42, 64, 22, 61, 21, 47, 36, 69, 71, 60, 70, 20, 37, 5, 15, 57, 13, 54, 19, 8, 46, 34, 52, 27, 45, 29, 48, 30, 2, 74, 28, 62, 73});
		
		int[] packingPlan = new int[]{1, 7, 12, 17, 18, 21, 23, 27, 33, 36, 51, 53, 63, 72, 73, 74, 75};
		
		Assert.assertEquals(4716.7866310745585, calculateBenefit(tour, packingPlan), delta);

	}
	
	@Test
	public void verifyEil51subn05m20ms10Result(){
		Ttp problem = TtpLoader.load("experiments/eil51_sub/eil51_n05_m20_multiple-strongly-corr_10.ttp");
		
		Tour tour = new Tour(problem);
		
		tour.update(new int[]{1,3,4,5,2});
		
		int[] packingPlan = new int[]{3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20};
		
		Assert.assertEquals(6009.149579639547, calculateBenefit(tour, packingPlan), delta);

	}
	
	@Test
	public void verifyEil51subn05m20un06Result(){
		Ttp problem = TtpLoader.load("experiments/eil51_sub/eil51_n05_m20_uncorr_06.ttp");
		
		Tour tour = new Tour(problem);
		
		tour.update(new int[]{1,3,4,5,2});
		
		int[] packingPlan = new int[]{1,2,3,6,7,9,11,12,13,16,17,18,19};
		
		Assert.assertEquals(8069.852301143108, calculateBenefit(tour, packingPlan), delta);

	}
	
	
	@Test
	public void testReverse() {
		S1S5Heuristic heuristic = new S1S5Heuristic(null, TspSolver.SolverName.LKH, S1S5Heuristic.TEN_MINUTES);
		int[] t = new int[]{1, 4, 5, 2, 3, 6, 7};
		heuristic.reverse(t);
		
		Assert.assertArrayEquals(new int[]{1, 7, 6, 3, 2, 5, 4}, t);
	}
	
	@Test
	public void testS5Heuristic() {
		
		Ttp problem = TtpLoader.load("experiments/eil76/eil76_n75_uncorr_01.ttp");
		
		S1S5Heuristic heuristic = new S1S5Heuristic(problem, TspSolver.SolverName.LKH, S1S5Heuristic.TEN_MINUTES);
		
		
		S1S5Heuristic.Solution solution = heuristic.S5Heuristic();
		
		System.out.println(solution);
		
		Assert.assertEquals(3727.390754703563, solution.benefit, delta);
		
		Assert.assertArrayEquals(new int[]{1, 73, 62, 22, 64, 42, 43, 41, 56, 23, 49, 24, 18, 50, 25, 55, 31, 10, 38, 65, 11, 66, 59, 14, 53, 7, 35, 8, 19, 54, 13, 57, 15, 5, 37, 20, 70, 60, 71, 69, 36, 47, 21, 61, 28, 74, 2, 30, 48, 29, 45, 27, 52, 46, 34, 67, 26, 76, 75, 4, 68, 6, 51, 17, 40, 12, 58, 72, 39, 9, 32, 44, 3, 16, 63, 33}, solution.getTour());
		
		Assert.assertArrayEquals(new int[]{1, 3, 7, 8, 12, 15, 18, 23, 33, 36, 51, 62, 63, 74, 75}, solution.getPackingPlan());
		
	}
	
	
	@Test
	public void testS5HeuristicOnEil51Sub() {
		
		Ttp problem = TtpLoader.load("experiments/eil51_sub/eil51_n05_m20_uncorr_01.ttp");
		
		S1S5Heuristic heuristic = new S1S5Heuristic(problem, TspSolver.SolverName.LKH, S1S5Heuristic.TEN_MINUTES);
		
		
		S1S5Heuristic.Solution solution = heuristic.S5Heuristic();
		
		System.out.println(solution);
		
		Assert.assertEquals(3727.390754703563, solution.benefit, delta);
		
		Assert.assertArrayEquals(new int[]{1, 73, 62, 22, 64, 42, 43, 41, 56, 23, 49, 24, 18, 50, 25, 55, 31, 10, 38, 65, 11, 66, 59, 14, 53, 7, 35, 8, 19, 54, 13, 57, 15, 5, 37, 20, 70, 60, 71, 69, 36, 47, 21, 61, 28, 74, 2, 30, 48, 29, 45, 27, 52, 46, 34, 67, 26, 76, 75, 4, 68, 6, 51, 17, 40, 12, 58, 72, 39, 9, 32, 44, 3, 16, 63, 33}, solution.getTour());
		
		Assert.assertArrayEquals(new int[]{1, 3, 7, 8, 12, 15, 18, 23, 33, 36, 51, 62, 63, 74, 75}, solution.getPackingPlan());
		
	}
	
	@Test
	public void testS5Heuristic4ExistingLknTours() throws IOException {
		
		Ttp problem = TtpLoader.load("experiments/eil51_sub/eil51_n05_m20_uncorr_01.ttp");
		
		S1S5Heuristic heuristic = new S1S5Heuristic(problem, TspSolver.SolverName.LKH, S1S5Heuristic.TEN_MINUTES);
		
		
		S1S5Heuristic.Solution solution = heuristic.S5Heuristic4ExistingLknTours();
		
		System.out.println(solution);
		
		Assert.assertEquals(2002.7111706663904, solution.benefit, delta);
		
		Assert.assertArrayEquals(new int[]{1, 73, 62, 22, 64, 42, 43, 41, 56, 23, 49, 24, 18, 50, 25, 55, 31, 10, 38, 65, 11, 66, 59, 14, 53, 7, 35, 8, 19, 54, 13, 57, 15, 5, 37, 20, 70, 60, 71, 69, 36, 47, 21, 61, 28, 74, 2, 30, 48, 29, 45, 27, 52, 46, 34, 67, 26, 76, 75, 4, 68, 6, 51, 17, 40, 12, 58, 72, 39, 9, 32, 44, 3, 16, 63, 33}, solution.getTour());
		
		Assert.assertArrayEquals(new int[]{1, 3, 7, 8, 12, 15, 18, 23, 33, 36, 51, 62, 63, 74, 75}, solution.getPackingPlan());
		
	}
	
}
