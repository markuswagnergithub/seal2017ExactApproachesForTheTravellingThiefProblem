package ttp.search;


import java.io.FileNotFoundException;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import ttp.problem.Ttp;
import ttp.problem.TtpLoader;

public class TourBFTest {
	private static final double delta = 1e-10;

	@Test
	public void testNextPermutation() throws FileNotFoundException {
		
		int[] permu = new int[]{2, 3, 4};
		
		for (int i =0;i<TourBF.factorial(permu.length);i++){
			
			System.out.println(Arrays.toString(permu));
			
			TourBF.nextPermutation(permu);
			
		}
		
	}
	
	@Test
	public void testSmall_n4_m3_bf() throws FileNotFoundException {
		String instance = "test1_n4_m3.ttp";
		Ttp problem = TtpLoader.load("experiments/small/" + instance);

		TourBF bf = new TourBF(problem);

		bf.search();

		String record = instance + "\t" + Arrays.toString(bf.getTour()) + "\tbenefit: " + bf.getMaxBenefit()
				+ "\tweight: " + bf.getTotalWeight() + "\tprofit: " + bf.getTotalProfit() + "\tpackingplan: "
				+ Arrays.toString(bf.getPackingPlan()) + "\ttime:" + bf.getElapsedTime();
		System.out.println(record);

		Assert.assertEquals(-43.0, bf.getMaxBenefit(), delta);
		Assert.assertArrayEquals(new int[] { 1, 2, 3, 4, 1 }, bf.getTour());

		Assert.assertArrayEquals(new int[] { 3 }, bf.getPackingPlan());
		Assert.assertEquals(5, bf.getTotalWeight());
		Assert.assertEquals(7, bf.getTotalProfit());
	}

	@Test
	public void testSmall_n4_m6_bf() throws FileNotFoundException {
		String instance = "test1_n4_m6.ttp";
		Ttp problem = TtpLoader.load("experiments/small/" + instance);

		TourBF bf = new TourBF(problem);
		bf.search();

		String record = instance + "\t" + Arrays.toString(bf.getTour()) + "\tbenefit: " + bf.getMaxBenefit()
				+ "\tweight: " + bf.getTotalWeight() + "\tprofit: " + bf.getTotalProfit() + "\tpackingplan: "
				+ Arrays.toString(bf.getPackingPlan()) + "\ttime:" + bf.getElapsedTime();
		System.out.println(record);

		Assert.assertEquals(-42.684210526315795, bf.getMaxBenefit(), delta);
		Assert.assertArrayEquals(new int[] { 1, 4, 3, 2, 1 }, bf.getTour());
		Assert.assertEquals(6, bf.getTotalWeight());
		Assert.assertEquals(8, bf.getTotalProfit());
		Assert.assertArrayEquals(new int[] { 1, 2 }, bf.getPackingPlan());
	}
	
	
	@Test
	public void testSmall_n5_m8_bf() throws FileNotFoundException {
		String instance = "test1_n5_m8.ttp";
		Ttp problem = TtpLoader.load("experiments/small/" + instance);

		TourBF bf = new TourBF(problem);
		bf.search();

		String record = instance + "\t" + Arrays.toString(bf.getTour()) + "\tbenefit: " + bf.getMaxBenefit()
				+ "\tweight: " + bf.getTotalWeight() + "\tprofit: " + bf.getTotalProfit() + "\tpackingplan: "
				+ Arrays.toString(bf.getPackingPlan()) + "\ttime:" + bf.getElapsedTime();
		System.out.println(record);

		Assert.assertEquals(-40.14995491046507, bf.getMaxBenefit(), delta);
		Assert.assertArrayEquals(new int[] { 1, 2, 5, 3, 4, 1 }, bf.getTour());
		Assert.assertEquals(14, bf.getTotalWeight());
		Assert.assertEquals(26, bf.getTotalProfit());
		Assert.assertArrayEquals(new int[] { 3, 5, 6, 7 }, bf.getPackingPlan());
	}
	

	@Test
	public void testSmall_n8_m14_bf() throws FileNotFoundException {
		String instance = "test1_n8_m14.ttp";
		Ttp problem = TtpLoader.load("experiments/small/" + instance);

		TourBF bf = new TourBF(problem);
		bf.search();

		String record = instance + "\t" + Arrays.toString(bf.getTour()) + "\tbenefit: " + bf.getMaxBenefit()
				+ "\tweight: " + bf.getTotalWeight() + "\tprofit: " + bf.getTotalProfit() + "\tpackingplan: "
				+ Arrays.toString(bf.getPackingPlan()) + "\ttime:" + bf.getElapsedTime();
		System.out.println(record);

		Assert.assertEquals(-17.5451051200495, bf.getMaxBenefit(), delta);
		Assert.assertArrayEquals(new int[] { 1, 6, 7, 8, 3, 2, 5, 4, 1 }, bf.getTour());
		Assert.assertEquals(198, bf.getTotalWeight());
		Assert.assertEquals(431, bf.getTotalProfit());
		Assert.assertArrayEquals(new int[] { 1, 3, 5, 7, 8, 11 }, bf.getPackingPlan());

	}
	
	@Test
	public void testSmall_n4_m26_bf() throws FileNotFoundException {
		String instance = "random_n4_m26.ttp";
		Ttp problem = TtpLoader.load("experiments/small/" + instance);

		TourBF bf = new TourBF(problem);
		bf.search();

		String record = instance + "\t" + Arrays.toString(bf.getTour()) + "\tbenefit: " + bf.getMaxBenefit()
				+ "\tweight: " + bf.getTotalWeight() + "\tprofit: " + bf.getTotalProfit() + "\tpackingplan: "
				+ Arrays.toString(bf.getPackingPlan()) + "\ttime:" + bf.getElapsedTime();
		System.out.println(record);

		Assert.assertEquals(11883.802669299388, bf.getMaxBenefit(), delta);
		Assert.assertArrayEquals(new int[] { 1, 3, 2, 4, 1 }, bf.getTour());
		Assert.assertEquals(13584, bf.getTotalWeight());
		Assert.assertEquals(12155, bf.getTotalProfit());
		Assert.assertArrayEquals(new int[] { 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26 }, bf.getPackingPlan());

	}
	
	@Test
	public void testSmall_random_n4_m6_bf() throws FileNotFoundException {
		String instance = "random_n4_m6.ttp";
		Ttp problem = TtpLoader.load("experiments/small/" + instance);

		TourBF bf = new TourBF(problem);
		bf.search();

		String record = instance + "\t" + Arrays.toString(bf.getTour()) + "\tbenefit: " + bf.getMaxBenefit()
				+ "\tweight: " + bf.getTotalWeight() + "\tprofit: " + bf.getTotalProfit() + "\tpackingplan: "
				+ Arrays.toString(bf.getPackingPlan()) + "\ttime:" + bf.getElapsedTime();
		System.out.println(record);

		Assert.assertEquals(2110.504478562878, bf.getMaxBenefit(), delta);
		Assert.assertArrayEquals(new int[] { 1, 4, 2, 3, 1 }, bf.getTour());
		Assert.assertEquals(2492, bf.getTotalWeight());
		Assert.assertEquals(2411, bf.getTotalProfit());
		Assert.assertArrayEquals(new int[] { 1, 2, 3, 4, 5, 6 }, bf.getPackingPlan());
	}
	
}
