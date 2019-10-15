package ttp.dp;

import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.nio.file.Paths;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import common.tools.Util;
import ttp.problem.Ttp;
import ttp.problem.TtpLoader;
import ttp.problem.TtpSaver;
import ttp.tour.TspSolver;

public class TourDpTest {
	private static final double delta = 1e-10;

	@Test
	public void testInitiallyBest() throws FileNotFoundException{
		String instanceFile = "random_n5_m12.ttp";

		Ttp problem = TtpLoader.load("experiments/small/" + instanceFile);
		
		
		final String uid = Util.uniqueId();
		final String instance = "/tmp/" + uid + ".ttp";
		final String tourfile = "/tmp/" + uid + ".tour";
		
		
		TtpSaver.write(problem, Paths.get(instance));
		TspSolver tspSolver = new TspSolver();
		tspSolver.solve(instance, tourfile, TspSolver.SolverName.CONCORDE);
		
		TourDP.initiallyBest(problem, tourfile);
		
	}
	
	
	@Test
	public void testAllSet() {
		Assert.assertEquals(new BigInteger("111", 2), CitySet.allSet(3));
		Assert.assertEquals(new BigInteger("1111", 2), CitySet.allSet(4));
		Assert.assertEquals(new BigInteger("111111", 2), CitySet.allSet(6));
		Assert.assertEquals(new BigInteger("11111111", 2), CitySet.allSet(8));

	}

	@Test
	public void testNext() {
		CitySet tourdp = new CitySet(0, 0, 0);

		Assert.assertEquals(new BigInteger("10", 2).longValue(), tourdp.nextComb(new BigInteger("1", 2).longValue()));
		Assert.assertEquals(new BigInteger("1000", 2).longValue(),
				tourdp.nextComb(new BigInteger("100", 2).longValue()));
		Assert.assertEquals(new BigInteger("110", 2).longValue(),
				tourdp.nextComb(new BigInteger("101", 2).longValue()));
		Assert.assertEquals(new BigInteger("1100", 2).longValue(),
				tourdp.nextComb(new BigInteger("1010", 2).longValue()));
		Assert.assertEquals(new BigInteger("1011", 2).longValue(),
				tourdp.nextComb(new BigInteger("111", 2).longValue()));
		Assert.assertEquals(new BigInteger("110111111", 2).longValue(),
				tourdp.nextComb(new BigInteger("101111111", 2).longValue()));
	}

	@Test
	public void testBinomial() {

		Assert.assertEquals(6, CitySet.binomial(4, 2));
		Assert.assertEquals(28, CitySet.binomial(8, 2));
		Assert.assertEquals(45, CitySet.binomial(10, 2));
		Assert.assertEquals(286, CitySet.binomial(13, 3));
		Assert.assertEquals(314457495, CitySet.binomial(47, 8));
		Assert.assertEquals(2598960, CitySet.binomial(52, 5));
	}


	@Test
	public void testSmall_n4_m3() throws FileNotFoundException {
		String instance = "test1_n4_m3.ttp";

		Ttp problem = TtpLoader.load("experiments/small/" + instance);

		TourDP dp = new TourDP(problem);
		dp.dynamicProgramming();

		String record = instance + "\t" + Arrays.toString(dp.getTour()) + "\tbenefit: " + dp.getMaxBenefit()
				+ "\tweight: " + dp.getTotalWeight() + "\tprofit: " + dp.getTotalProfit() + "\tpackingplan: "
				+ Arrays.toString(dp.getPackingPlan()) + "\ttime:" + dp.getElapsedTime();
		System.out.println(record);

		Assert.assertEquals(-43.0, dp.getMaxBenefit(), delta);
		Assert.assertArrayEquals(new int[] { 1, 2, 3, 4, 1 }, dp.getTour());

		Assert.assertArrayEquals(new int[] { 3 }, dp.getPackingPlan());
		Assert.assertEquals(5, dp.getTotalWeight());
		Assert.assertEquals(7, dp.getTotalProfit());

	}


	@Test
	public void testSmall_n4_m6() throws FileNotFoundException {
		String instance = "test1_n4_m6.ttp";

		Ttp problem = TtpLoader.load("experiments/small/" + instance);

		TourDP dp = new TourDP(problem);
		dp.dynamicProgramming();

		String record = instance + "\t" + Arrays.toString(dp.getTour()) + "\tbenefit: " + dp.getMaxBenefit()
				+ "\tweight: " + dp.getTotalWeight() + "\tprofit: " + dp.getTotalProfit() + "\tpackingplan: "
				+ Arrays.toString(dp.getPackingPlan()) + "\ttime:" + dp.getElapsedTime();
		System.out.println(record);

		Assert.assertEquals(-42.684210526315795, dp.getMaxBenefit(), delta);
		Assert.assertArrayEquals(new int[] { 1, 4, 3, 2, 1 }, dp.getTour());
		Assert.assertEquals(6, dp.getTotalWeight());
		Assert.assertEquals(8, dp.getTotalProfit());
		Assert.assertArrayEquals(new int[] { 1, 2 }, dp.getPackingPlan());

	}



	@Test
	public void testSmall_n5_m8() throws FileNotFoundException {
		String instance = "test1_n5_m8.ttp";

		Ttp problem = TtpLoader.load("experiments/small/" + instance);

		TourDP dp = new TourDP(problem);
		dp.dynamicProgramming();

		String record = instance + "\t" + Arrays.toString(dp.getTour()) + "\tbenefit: " + dp.getMaxBenefit()
				+ "\tweight: " + dp.getTotalWeight() + "\tprofit: " + dp.getTotalProfit() + "\tpackingplan: "
				+ Arrays.toString(dp.getPackingPlan()) + "\ttime:" + dp.getElapsedTime();
		System.out.println(record);

		Assert.assertEquals(-40.14995491046507, dp.getMaxBenefit(), delta);
		Assert.assertArrayEquals(new int[] { 1, 2, 5, 3, 4, 1 }, dp.getTour());
		Assert.assertEquals(14, dp.getTotalWeight());
		Assert.assertEquals(26, dp.getTotalProfit());
		Assert.assertArrayEquals(new int[] { 3, 5, 6, 7 }, dp.getPackingPlan());

	}


	@Test
	public void testSmall_n8_m14() throws FileNotFoundException {
		String instance = "test1_n8_m14.ttp";

		Ttp problem = TtpLoader.load("experiments/small/" + instance);

		TourDP dp = new TourDP(problem);
		dp.dynamicProgramming();

		String record = instance + "\t" + Arrays.toString(dp.getTour()) + "\tbenefit: " + dp.getMaxBenefit()
				+ "\tweight: " + dp.getTotalWeight() + "\tprofit: " + dp.getTotalProfit() + "\tpackingplan: "
				+ Arrays.toString(dp.getPackingPlan()) + "\ttime:" + dp.getElapsedTime();
		System.out.println(record);

		Assert.assertEquals(-17.5451051200495, dp.getMaxBenefit(), delta);
		Assert.assertArrayEquals(new int[] { 1, 6, 7, 8, 3, 2, 5, 4, 1 }, dp.getTour());
		Assert.assertEquals(198, dp.getTotalWeight());
		Assert.assertEquals(431, dp.getTotalProfit());
		Assert.assertArrayEquals(new int[] { 1, 3, 5, 7, 8, 11 }, dp.getPackingPlan());

	}



	@Test
	public void testSmall_n4_m26() throws FileNotFoundException {
		String instance = "random_n4_m26.ttp";

		Ttp problem = TtpLoader.load("experiments/small/" + instance);

		TourDP dp = new TourDP(problem);
		dp.dynamicProgramming();

		String record = instance + "\t" + Arrays.toString(dp.getTour()) + "\tbenefit: " + dp.getMaxBenefit()
				+ "\tweight: " + dp.getTotalWeight() + "\tprofit: " + dp.getTotalProfit() + "\tpackingplan: "
				+ Arrays.toString(dp.getPackingPlan()) + "\ttime:" + dp.getElapsedTime();
		System.out.println(record);


		Assert.assertArrayEquals(new int[] { 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26 }, dp.getPackingPlan());
		Assert.assertEquals(11883.802669299388, dp.getMaxBenefit(), delta);
		Assert.assertArrayEquals(new int[] { 1, 3, 2, 4, 1 }, dp.getTour());
		Assert.assertEquals(13584, dp.getTotalWeight());
		Assert.assertEquals(12155, dp.getTotalProfit());

	}
	
	


	@Test
	public void testSmall_random_n4_m6() throws FileNotFoundException {
		String instance = "random_n4_m6.ttp";

		Ttp problem = TtpLoader.load("experiments/small/" + instance);

		TourDP dp = new TourDP(problem);
		dp.dynamicProgramming();

		String record = instance + "\t" + Arrays.toString(dp.getTour()) + "\tbenefit: " + dp.getMaxBenefit()
				+ "\tweight: " + dp.getTotalWeight() + "\tprofit: " + dp.getTotalProfit() + "\tpackingplan: "
				+ Arrays.toString(dp.getPackingPlan()) + "\ttime:" + dp.getElapsedTime();
		System.out.println(record);

		Assert.assertEquals(2110.504478562878, dp.getMaxBenefit(), delta);
		Assert.assertArrayEquals(new int[] { 1, 4, 2, 3, 1 }, dp.getTour());
		Assert.assertEquals(2492, dp.getTotalWeight());
		Assert.assertEquals(2411, dp.getTotalProfit());
		Assert.assertArrayEquals(new int[] { 1, 2, 3, 4, 5, 6 }, dp.getPackingPlan());

	}

}
