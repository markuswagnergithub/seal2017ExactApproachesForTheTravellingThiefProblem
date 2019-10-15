package ttp.search;


import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import ttp.problem.Ttp;
import ttp.problem.TtpLoader;

public class BeamSearchTest {
	private static final double delta = 1e-10;

	@Test
	public void testNegateBit() throws FileNotFoundException {
		BigInteger b = new BigInteger("111001",2);
		System.out.println(b.toString(2));
		BigInteger nb = b.not();
		System.out.println(nb.toString(2));
		
		StringBuilder sb = new StringBuilder();
		StringBuilder snb = new StringBuilder();
		for (int i=0;i<128;i++){
			if(b.testBit(i)){
				sb.append('1');
			}else{
				sb.append('0');
			}
			if(nb.testBit(i)){
				snb.append('1');
			}else{
				snb.append('0');
			}
		}
		System.out.println(sb);
		System.out.println(snb);
		
	}
	
	@Test
	public void testSmall_n4_m3() throws FileNotFoundException {
		String instance = "test1_n4_m3.ttp";

		Ttp problem = TtpLoader.load("experiments/small/" + instance);

		BeamSearch bs = new BeamSearch(problem, 10);
		bs.search();

		String record = instance + "\t" + Arrays.toString(bs.getTour()) + "\tbenefit: " + bs.getMaxBenefit()
				+ "\tweight: " + bs.getTotalWeight() + "\tprofit: " + bs.getTotalProfit() + "\tpackingplan: "
				+ Arrays.toString(bs.getPackingPlan()) + "\ttime:" + bs.getElapsedTime();
		System.out.println(record);

		Assert.assertEquals(-43.0, bs.getMaxBenefit(), delta);
		Assert.assertArrayEquals(new int[] { 1, 2, 3, 4, 1 }, bs.getTour());

		Assert.assertArrayEquals(new int[] { 3 }, bs.getPackingPlan());
		Assert.assertEquals(5, bs.getTotalWeight());
		Assert.assertEquals(7, bs.getTotalProfit());

	}


	@Test
	public void testSmall_n4_m6() throws FileNotFoundException {
		String instance = "test1_n4_m6.ttp";

		Ttp problem = TtpLoader.load("experiments/small/" + instance);

		BeamSearch bs = new BeamSearch(problem, 1000);
		bs.search();

		String record = instance + "\t" + Arrays.toString(bs.getTour()) + "\tbenefit: " + bs.getMaxBenefit()
				+ "\tweight: " + bs.getTotalWeight() + "\tprofit: " + bs.getTotalProfit() + "\tpackingplan: "
				+ Arrays.toString(bs.getPackingPlan()) + "\ttime:" + bs.getElapsedTime();
		System.out.println(record);

		Assert.assertEquals(-42.684210526315795, bs.getMaxBenefit(), delta);
		Assert.assertArrayEquals(new int[] { 1, 4, 3, 2, 1 }, bs.getTour());
		Assert.assertEquals(6, bs.getTotalWeight());
		Assert.assertEquals(8, bs.getTotalProfit());
		Assert.assertArrayEquals(new int[] { 1, 2 }, bs.getPackingPlan());

	}



	@Test
	public void testSmall_n5_m8() throws FileNotFoundException {
		String instance = "test1_n5_m8.ttp";

		Ttp problem = TtpLoader.load("experiments/small/" + instance);

		BeamSearch bs = new BeamSearch(problem, 50);
		bs.search();

		String record = instance + "\t" + Arrays.toString(bs.getTour()) + "\tbenefit: " + bs.getMaxBenefit()
				+ "\tweight: " + bs.getTotalWeight() + "\tprofit: " + bs.getTotalProfit() + "\tpackingplan: "
				+ Arrays.toString(bs.getPackingPlan()) + "\ttime:" + bs.getElapsedTime();
		System.out.println(record);

		Assert.assertEquals(-40.14995491046507, bs.getMaxBenefit(), delta);
		Assert.assertArrayEquals(new int[] { 1, 2, 5, 3, 4, 1 }, bs.getTour());
		Assert.assertEquals(14, bs.getTotalWeight());
		Assert.assertEquals(26, bs.getTotalProfit());
		Assert.assertArrayEquals(new int[] { 3, 5, 6, 7 }, bs.getPackingPlan());

	}


	@Test
	public void testSmall_n8_m14() throws FileNotFoundException {
		String instance = "test1_n8_m14.ttp";

		Ttp problem = TtpLoader.load("experiments/small/" + instance);

		BeamSearch bs = new BeamSearch(problem, 2500);
		bs.search();

		String record = instance + "\t" + Arrays.toString(bs.getTour()) + "\tbenefit: " + bs.getMaxBenefit()
				+ "\tweight: " + bs.getTotalWeight() + "\tprofit: " + bs.getTotalProfit() + "\tpackingplan: "
				+ Arrays.toString(bs.getPackingPlan()) + "\ttime:" + bs.getElapsedTime();
		System.out.println(record);

		Assert.assertEquals(-17.5451051200495, bs.getMaxBenefit(), delta);
		Assert.assertArrayEquals(new int[] { 1, 6, 7, 8, 3, 2, 5, 4, 1 }, bs.getTour());
		Assert.assertEquals(198, bs.getTotalWeight());
		Assert.assertEquals(431, bs.getTotalProfit());
		Assert.assertArrayEquals(new int[] { 1, 3, 5, 7, 8, 11 }, bs.getPackingPlan());

	}



	@Test
	public void testSmall_n4_m26() throws FileNotFoundException {
		String instance = "random_n4_m26.ttp";

		Ttp problem = TtpLoader.load("experiments/small/" + instance);

		BeamSearch bs = new BeamSearch(problem, 1000);
		bs.search();

		String record = instance + "\t" + Arrays.toString(bs.getTour()) + "\tbenefit: " + bs.getMaxBenefit()
				+ "\tweight: " + bs.getTotalWeight() + "\tprofit: " + bs.getTotalProfit() + "\tpackingplan: "
				+ Arrays.toString(bs.getPackingPlan()) + "\ttime:" + bs.getElapsedTime();
		System.out.println(record);


		Assert.assertArrayEquals(new int[] { 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26 }, bs.getPackingPlan());
		Assert.assertEquals(11883.802669299388, bs.getMaxBenefit(), delta);
		Assert.assertArrayEquals(new int[] { 1, 3, 2, 4, 1 }, bs.getTour());
		Assert.assertEquals(13584, bs.getTotalWeight());
		Assert.assertEquals(12155, bs.getTotalProfit());

	}
	
	


	@Test
	public void testSmall_random_n4_m6() throws FileNotFoundException {
		String instance = "random_n4_m6.ttp";

		Ttp problem = TtpLoader.load("experiments/small/" + instance);

		BeamSearch bs = new BeamSearch(problem, 1000);
		bs.search();

		String record = instance + "\t" + Arrays.toString(bs.getTour()) + "\tbenefit: " + bs.getMaxBenefit()
				+ "\tweight: " + bs.getTotalWeight() + "\tprofit: " + bs.getTotalProfit() + "\tpackingplan: "
				+ Arrays.toString(bs.getPackingPlan()) + "\ttime:" + bs.getElapsedTime();
		System.out.println(record);

		Assert.assertEquals(2110.504478562878, bs.getMaxBenefit(), delta);
		Assert.assertArrayEquals(new int[] { 1, 4, 2, 3, 1 }, bs.getTour());
		Assert.assertEquals(2492, bs.getTotalWeight());
		Assert.assertEquals(2411, bs.getTotalProfit());
		Assert.assertArrayEquals(new int[] { 1, 2, 3, 4, 5, 6 }, bs.getPackingPlan());

	}

	@Test
	public void testEil_n51_m50() throws FileNotFoundException {
		String instance = "eil51_n50_uncorr_01.ttp";

		Ttp problem = TtpLoader.load("experiments/eil51/" + instance);

		BeamSearch bs = new BeamSearch(problem, 50000);
		bs.search();

		String record = instance + "\t" + Arrays.toString(bs.getTour()) + "\tbenefit: " + bs.getMaxBenefit()
				+ "\tweight: " + bs.getTotalWeight() + "\tprofit: " + bs.getTotalProfit() + "\tpackingplan: "
				+ Arrays.toString(bs.getPackingPlan()) + "\ttime:" + bs.getElapsedTime();
		System.out.println(record);

		Assert.assertEquals(2110.504478562878, bs.getMaxBenefit(), delta);
		Assert.assertArrayEquals(new int[] { 1, 4, 2, 3, 1 }, bs.getTour());
		Assert.assertEquals(2492, bs.getTotalWeight());
		Assert.assertEquals(2411, bs.getTotalProfit());
		Assert.assertArrayEquals(new int[] { 1, 2, 3, 4, 5, 6 }, bs.getPackingPlan());

	}
	
}
