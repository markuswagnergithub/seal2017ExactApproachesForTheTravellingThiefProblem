package ttp.dp;


import java.io.FileNotFoundException;
import java.io.IOException;
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

public class TourBnBTest {

	private static final double delta = 1e-10;

	@Test
	public void testOSname() {
		System.out.println(System.getProperty("os.name").split("\\s")[0]);
		System.out.println(System.getProperty("os.arch"));
		System.out.println(System.getProperty("os.version"));
		
		
	}
	
	@Test
	public void testDistance() throws FileNotFoundException{
		String instance = "test1_n4_m3.ttp";

		Ttp problem = TtpLoader.load("experiments/small/" + instance);
		
		Assert.assertEquals(problem.distance(1, 2), 8);
		Assert.assertEquals(problem.distance(1, 3), 17);
		Assert.assertEquals(problem.distance(1, 4), 5);
		Assert.assertEquals(problem.distance(2, 3), 20);
		Assert.assertEquals(problem.distance(2, 4), 11);
		Assert.assertEquals(problem.distance(3, 4), 12);
		
	}
	
	@Test
	public void testShortestTSPPath() throws IOException{
		final String tempinstance = "/tmp/" + Util.uniqueId() + ".ttp";
		
		String instance = "test1_n4_m3.ttp";

		Ttp problem = TtpLoader.load("experiments/test/" + instance);
		TtpSaver.write(problem, Paths.get(tempinstance));
		TspSolver tspSolver = new TspSolver();
		Assert.assertEquals(tspSolver.solve(tempinstance, null, TspSolver.SolverName.CONCORDE), 45);
		
		
		instance = "eil51_n50_uncorr_01.ttp";

		problem = TtpLoader.load("experiments/eil51/" + instance);
		TtpSaver.write(problem, Paths.get(tempinstance));
		Assert.assertEquals(tspSolver.solve(tempinstance, null, TspSolver.SolverName.CONCORDE), 459);
		
	}
	
	@Test
	public void testNegateBit() throws FileNotFoundException {
		BigInteger b = new BigInteger("111001",2);
//		System.out.println(b.toString(2));
		BigInteger nb = b.not();
//		System.out.println(nb.toString(2));
		
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
		
		Assert.assertEquals(sb.toString(),"10011100000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000");
		Assert.assertEquals(snb.toString(),"01100011111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111");
		
	}
	
	@Test
	public void testSmall_n4_m3() throws FileNotFoundException {
		String instance = "test1_n4_m3.ttp";

		Ttp problem = TtpLoader.load("experiments/small/" + instance);

		TourBnB bb = new TourBnB(problem, null);
		bb.search();

		String record = instance + "\t" + Arrays.toString(bb.getTour()) + "\tbenefit: " + bb.getMaxBenefit()
				+ "\tweight: " + bb.getTotalWeight() + "\tprofit: " + bb.getTotalProfit() + "\tpackingplan: "
				+ Arrays.toString(bb.getPackingPlan()) + "\ttime:" + bb.getElapsedTime();
		System.out.println(record);

		Assert.assertEquals(-43.0, bb.getMaxBenefit(), delta);
		Assert.assertArrayEquals(new int[] { 1, 2, 3, 4, 1 }, bb.getTour());

		Assert.assertArrayEquals(new int[] { 3 }, bb.getPackingPlan());
		Assert.assertEquals(5, bb.getTotalWeight());
		Assert.assertEquals(7, bb.getTotalProfit());

	}

	@Test
	public void testRandom_n4_m3() throws FileNotFoundException {
		String instance = "random_n4_m3.ttp";

		Ttp problem = TtpLoader.load("experiments/small/" + instance);

		TourBnB bb = new TourBnB(problem, null);
		bb.search();

		String record = instance + "\t" + Arrays.toString(bb.getTour()) + "\tbenefit: " + bb.getMaxBenefit()
				+ "\tweight: " + bb.getTotalWeight() + "\tprofit: " + bb.getTotalProfit() + "\tpackingplan: "
				+ Arrays.toString(bb.getPackingPlan()) + "\ttime:" + bb.getElapsedTime();
		System.out.println(record);

		Assert.assertEquals(-1333.254641067756, bb.getMaxBenefit(), delta);
		Assert.assertArrayEquals(new int[] { 1, 4, 3, 2, 1 }, bb.getTour());

		Assert.assertArrayEquals(new int[] { 1, 2, 3 }, bb.getPackingPlan());
		Assert.assertEquals(1066, bb.getTotalWeight());
		Assert.assertEquals(1260, bb.getTotalProfit());

	}
	

	@Test
	public void testSmall_n4_m6() throws FileNotFoundException {
		String instance = "test1_n4_m6.ttp";

		Ttp problem = TtpLoader.load("experiments/small/" + instance);

		TourBnB bb = new TourBnB(problem, null);
		bb.search();

		String record = instance + "\t" + Arrays.toString(bb.getTour()) + "\tbenefit: " + bb.getMaxBenefit()
				+ "\tweight: " + bb.getTotalWeight() + "\tprofit: " + bb.getTotalProfit() + "\tpackingplan: "
				+ Arrays.toString(bb.getPackingPlan()) + "\ttime:" + bb.getElapsedTime();
		System.out.println(record);

		Assert.assertEquals(-42.684210526315795, bb.getMaxBenefit(), delta);
		Assert.assertArrayEquals(new int[] { 1, 4, 3, 2, 1 }, bb.getTour());
		Assert.assertEquals(6, bb.getTotalWeight());
		Assert.assertEquals(8, bb.getTotalProfit());
		Assert.assertArrayEquals(new int[] { 1, 2 }, bb.getPackingPlan());

	}



	@Test
	public void testSmall_n5_m8() throws FileNotFoundException {
		String instance = "test1_n5_m8.ttp";

		Ttp problem = TtpLoader.load("experiments/small/" + instance);

		TourBnB bb = new TourBnB(problem, null);
		bb.search();

		String record = instance + "\t" + Arrays.toString(bb.getTour()) + "\tbenefit: " + bb.getMaxBenefit()
				+ "\tweight: " + bb.getTotalWeight() + "\tprofit: " + bb.getTotalProfit() + "\tpackingplan: "
				+ Arrays.toString(bb.getPackingPlan()) + "\ttime:" + bb.getElapsedTime();
		System.out.println(record);

		Assert.assertEquals(-40.14995491046507, bb.getMaxBenefit(), delta);
		Assert.assertArrayEquals(new int[] { 1, 2, 5, 3, 4, 1 }, bb.getTour());
		Assert.assertEquals(14, bb.getTotalWeight());
		Assert.assertEquals(26, bb.getTotalProfit());
		Assert.assertArrayEquals(new int[] { 3, 5, 6, 7 }, bb.getPackingPlan());

	}

	
	@Test
	public void testRandom_n5_m12() throws FileNotFoundException {
		String instance = "random_n5_m12.ttp";

		Ttp problem = TtpLoader.load("experiments/small/" + instance);

		TourBnB bb = new TourBnB(problem, null);
		bb.search();

		String record = instance + "\t" + Arrays.toString(bb.getTour()) + "\tbenefit: " + bb.getMaxBenefit()
				+ "\tweight: " + bb.getTotalWeight() + "\tprofit: " + bb.getTotalProfit() + "\tpackingplan: "
				+ Arrays.toString(bb.getPackingPlan()) + "\ttime:" + bb.getElapsedTime();
		System.out.println(record);

		Assert.assertEquals(5724.412017350422, bb.getMaxBenefit(), delta);
		Assert.assertArrayEquals(new int[] { 1, 5, 4, 3, 2, 1 }, bb.getTour());
		Assert.assertEquals(3288, bb.getTotalWeight());
		Assert.assertEquals(7882, bb.getTotalProfit());
		Assert.assertArrayEquals(new int[] { 1, 2, 3, 4, 5, 6, 8, 9, 10, 11, 12 }, bb.getPackingPlan());

	}
	

	@Test
	public void testSmall_n8_m14() throws FileNotFoundException {
		String instance = "test1_n8_m14.ttp";

		Ttp problem = TtpLoader.load("experiments/small/" + instance);

		TourBnB bb = new TourBnB(problem, null);
		bb.search();

		String record = instance + "\t" + Arrays.toString(bb.getTour()) + "\tbenefit: " + bb.getMaxBenefit()
				+ "\tweight: " + bb.getTotalWeight() + "\tprofit: " + bb.getTotalProfit() + "\tpackingplan: "
				+ Arrays.toString(bb.getPackingPlan()) + "\ttime:" + bb.getElapsedTime();
		System.out.println(record);

		Assert.assertEquals(-17.5451051200495, bb.getMaxBenefit(), delta);
		Assert.assertArrayEquals(new int[] { 1, 6, 7, 8, 3, 2, 5, 4, 1 }, bb.getTour());
		Assert.assertEquals(198, bb.getTotalWeight());
		Assert.assertEquals(431, bb.getTotalProfit());
		Assert.assertArrayEquals(new int[] { 1, 3, 5, 7, 8, 11 }, bb.getPackingPlan());

	}



	@Test
	public void testSmall_n4_m26() throws FileNotFoundException {
		String instance = "random_n4_m26.ttp";

		Ttp problem = TtpLoader.load("experiments/small/" + instance);

		TourBnB bb = new TourBnB(problem, null);
		bb.search();

		String record = instance + "\t" + Arrays.toString(bb.getTour()) + "\tbenefit: " + bb.getMaxBenefit()
				+ "\tweight: " + bb.getTotalWeight() + "\tprofit: " + bb.getTotalProfit() + "\tpackingplan: "
				+ Arrays.toString(bb.getPackingPlan()) + "\ttime:" + bb.getElapsedTime();
		System.out.println(record);


		Assert.assertArrayEquals(new int[] { 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26 }, bb.getPackingPlan());
		Assert.assertEquals(11883.802669299388, bb.getMaxBenefit(), delta);
		Assert.assertArrayEquals(new int[] { 1, 3, 2, 4, 1 }, bb.getTour());
		Assert.assertEquals(13584, bb.getTotalWeight());
		Assert.assertEquals(12155, bb.getTotalProfit());

	}
	
	


	@Test
	public void testSmall_random_n4_m6() throws FileNotFoundException {
		String instance = "random_n4_m6.ttp";

		Ttp problem = TtpLoader.load("experiments/small/" + instance);

		TourBnB bb = new TourBnB(problem, null);
		bb.search();

		String record = instance + "\t" + Arrays.toString(bb.getTour()) + "\tbenefit: " + bb.getMaxBenefit()
				+ "\tweight: " + bb.getTotalWeight() + "\tprofit: " + bb.getTotalProfit() + "\tpackingplan: "
				+ Arrays.toString(bb.getPackingPlan()) + "\ttime:" + bb.getElapsedTime();
		System.out.println(record);

		Assert.assertEquals(2110.504478562878, bb.getMaxBenefit(), delta);
		Assert.assertArrayEquals(new int[] { 1, 4, 2, 3, 1 }, bb.getTour());
		Assert.assertEquals(2492, bb.getTotalWeight());
		Assert.assertEquals(2411, bb.getTotalProfit());
		Assert.assertArrayEquals(new int[] { 1, 2, 3, 4, 5, 6 }, bb.getPackingPlan());

	}

//	@Test
	public void testEil_n51_m50() throws FileNotFoundException {
		String instance = "eil51_n50_uncorr_01.ttp";

		Ttp problem = TtpLoader.load("experiments/eil51/" + instance);

		TourBnB bb = new TourBnB(problem, null);
		bb.search();

		String record = instance + "\t" + Arrays.toString(bb.getTour()) + "\tbenefit: " + bb.getMaxBenefit()
				+ "\tweight: " + bb.getTotalWeight() + "\tprofit: " + bb.getTotalProfit() + "\tpackingplan: "
				+ Arrays.toString(bb.getPackingPlan()) + "\ttime:" + bb.getElapsedTime();
		System.out.println(record);

		Assert.assertEquals(2110.504478562878, bb.getMaxBenefit(), delta);
		Assert.assertArrayEquals(new int[] { 1, 4, 2, 3, 1 }, bb.getTour());
		Assert.assertEquals(2492, bb.getTotalWeight());
		Assert.assertEquals(2411, bb.getTotalProfit());
		Assert.assertArrayEquals(new int[] { 1, 2, 3, 4, 5, 6 }, bb.getPackingPlan());

	}
	

}
