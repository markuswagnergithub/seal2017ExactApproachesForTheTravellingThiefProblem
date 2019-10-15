package pwt.experiment;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import pwt.exact.dp.DpEnhanced;
import pwt.exact.dp.DynamicProgramming;
import ttp.dp.TourBnB;
import ttp.dp.TourDP;
import ttp.problem.IntRange;
import ttp.problem.Tour;
import ttp.problem.TourLoader;
import ttp.problem.Ttp;
import ttp.problem.TtpGenerator;
import ttp.problem.TtpLoader;
import ttp.problem.TtpSaver;
import ttp.search.BeamSearch;
import ttp.search.TourBF;
import ttp.tour.TourType;

public class DpExp {

	private static final double delta = 1e-10;

	public static void main(String[] args) throws IOException {
		if (args.length < 1) {
			expOnExisted();
			return;
		}
		
		if (args.length == 1) {
			String instancefile = args[0];
			Ttp problem = TtpLoader.load(instancefile);
			experimentDp(problem, instancefile);
			return;
		}
		
	}
	
	public static void expOnExisted() throws IOException {
		String inputFolder = "experiments/eil76";
		String tourfile = "experiments/eil76/eil76.linkern.tour";
		int [] stops = TourLoader.load(tourfile, TourType.lkh);

		for (Path file : Files.newDirectoryStream(Paths.get(inputFolder),
				path -> path.toString().matches(".*eil76_n225_.*_05\\.ttp$"))) {
			
			
			Ttp problem = TtpLoader.load(file.toString());
			try {

				experimentDpEnhanced(problem, stops);
			} catch (Exception e) {
				e.printStackTrace();
			} catch (OutOfMemoryError e) {
				System.out.println("DP on\t" + file.toString() + "\tis out of memory.");
				System.gc();
			}
		}
	}
	

	public static void expOnGenerated() {
		// int noOfCities = 5;
		IntRange xRange = new IntRange(1, 1000);
		IntRange yRange = new IntRange(1, 1000);
		// IntRange noOfItemsRange = new IntRange(noOfCities, noOfCities*10);
		IntRange weightRange = new IntRange(1, 1000);
		IntRange profitRange = new IntRange(1, 1000);

		BigDecimal rentingRate = new BigDecimal("1");
		BigDecimal minSpeed = new BigDecimal("0.1");
		BigDecimal maxSpeed = new BigDecimal("1");

		final int[] factors = new int[] { 1, 3, 5, 10 };
		final int[] capacityCategory = new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
		
		Ttp problem = null;
		try {
			for (int n = 5; n <= 20; n++) {
					TtpGenerator pg = new TtpGenerator(n, xRange, yRange, factors, weightRange, profitRange,
							rentingRate, minSpeed, maxSpeed);
					System.out.println("n=" + n);
					for (int i = 1; i <= 1; i++) {
//						System.out.println("no." + i);
						for (Ttp p : pg.generateAllType("RandomCities", capacityCategory)) {
							problem = p;
							System.out.println(TtpSaver.convert2TTPString(problem));
//							ProblemSaver.write(problem, Paths.get("./instances/" + problem.name + ".ttp"));
//							experimentDp(problem);
						}
					}
			}
		} catch (Exception e) {
			System.out.println(TtpSaver.convert2TTPString(problem));
			e.printStackTrace();
		}
	}

	public static boolean equals(double a, double b) {
		return Math.abs(a - b) < delta;
	}

	public static void experimentBS(Ttp problem){
		TourBF bf = new TourBF(problem);
		bf.search();

		String record = "BF\t" + problem.name + "\t" + Arrays.toString(bf.getTour()) + "\tbenefit: "
				+ bf.getMaxBenefit() + "\tweight: " + bf.getTotalWeight() + "\tprofit: " + bf.getTotalProfit()
				+ "\tpackingplan: " + Arrays.toString(bf.getPackingPlan()) + "\ttime:" + bf.getElapsedTime();
		System.out.println(record);

		BeamSearch bs = new BeamSearch(problem, 1000);
		bs.search();

		record = "BS\t" + problem.name + "\t" + Arrays.toString(bs.getTour()) + "\tbenefit: " + bs.getMaxBenefit()
				+ "\tweight: " + bs.getTotalWeight() + "\tprofit: " + bs.getTotalProfit() + "\tpackingplan: "
				+ Arrays.toString(bs.getPackingPlan()) + "\ttime:" + bs.getElapsedTime();
		System.out.println(record);

		if (!equals(bs.getMaxBenefit(), bf.getMaxBenefit()) || !Arrays.equals(bs.getTour(), bf.getTour())
				|| bs.getTotalProfit() != bf.getTotalProfit() || bs.getTotalWeight() != bf.getTotalWeight()
				|| !Arrays.equals(bs.getPackingPlan(), bf.getPackingPlan())) {

			throw new RuntimeException("results disagree");
		}
		
	}
	
	public static void experimentDp(Ttp problem, String fileName){
		if (fileName == null){
			fileName = problem.name;
		}
		
		System.out.print(fileName + "\t");
		
		TourDP dp = new TourDP(problem);
		dp.dynamicProgramming();
		

		String record = "DP" + "\ttour: " + Arrays.toString(dp.getTour()) + "\tbenefit: " + dp.getMaxBenefit()
				+ "\tweight: " + dp.getTotalWeight() + "\tprofit: " + dp.getTotalProfit() + "\tpackingplan: "
				+ Arrays.toString(dp.getPackingPlan()) + "\ttime:" + dp.getElapsedTime();
		System.out.println(record);
	}
	
	
	public static void experimentBnB(Ttp problem, String fileName) {

		System.out.print(fileName + "\t");
		
		TourDP dp = new TourDP(problem);
		dp.dynamicProgramming();

		String record = "DP\t" + problem.name + "\t" + Arrays.toString(dp.getTour()) + "\tbenefit: "
				+ dp.getMaxBenefit() + "\tweight: " + dp.getTotalWeight() + "\tprofit: " + dp.getTotalProfit()
				+ "\tpackingplan: " + Arrays.toString(dp.getPackingPlan()) + "\ttime:" + dp.getElapsedTime();
		System.out.println(record);

		
		System.out.print(fileName + "\t");
		
		TourBnB bb = new TourBnB(problem, null);
		bb.search();

		record = "BnB\t" + problem.name + "\t" + Arrays.toString(bb.getTour()) + "\tbenefit: " + bb.getMaxBenefit()
				+ "\tweight: " + bb.getTotalWeight() + "\tprofit: " + bb.getTotalProfit() + "\tpackingplan: "
				+ Arrays.toString(bb.getPackingPlan()) + "\ttime:" + bb.getElapsedTime();
		System.out.println(record);

		if (!equals(dp.getMaxBenefit(), bb.getMaxBenefit()) || !Arrays.equals(dp.getTour(), bb.getTour())
				|| dp.getTotalProfit() != bb.getTotalProfit() || dp.getTotalWeight() != bb.getTotalWeight()
				|| !Arrays.equals(dp.getPackingPlan(), bb.getPackingPlan())) {

			throw new RuntimeException("results disagree");
		}
	}
	
	public static void experimentDpEnhanced(Ttp problem, int[] stops) {

		Tour tour = new Tour(problem);
		tour.update(stops);
		DynamicProgramming dp = new DynamicProgramming(tour);
		dp.dynamicProgramming();

		String record = "DP\t" + problem.path + "\t" + "\tbenefit: "
				+ dp.getMaxBenefit() + "\tweight: " + dp.getTotalWeight() + "\tprofit: " + dp.getTotalProfit()
				+ "\tpackingplan: " + Arrays.toString(dp.getPackingPlan()) + "\ttime:" + dp.getElapsedTime();
		System.out.println(record);

		DpEnhanced bb = new DpEnhanced(problem, stops);
		bb.dynamicProgramming();

		record = "DpEx\t" + problem.path + "\t" + "\tbenefit: " + bb.getMaxBenefit()
				+ "\tweight: " + bb.getTotalWeight() + "\tprofit: " + bb.getTotalProfit() + "\tpackingplan: "
				+ Arrays.toString(bb.getPackingPlan()) + "\ttime:" + bb.getElapsedTime();
		System.out.println(record);

		if (!equals(dp.getMaxBenefit(), bb.getMaxBenefit())
				|| dp.getTotalProfit() != bb.getTotalProfit() || dp.getTotalWeight() != bb.getTotalWeight()
				|| !Arrays.equals(dp.getPackingPlan(), bb.getPackingPlan())) {

			throw new RuntimeException("results disagree");
		}
	}
	
	public static void experiment(Ttp problem) {

		TourBF bf = new TourBF(problem);
		bf.search();

		String record = "BF\t" + problem.name + "\t" + Arrays.toString(bf.getTour()) + "\tbenefit: "
				+ bf.getMaxBenefit() + "\tweight: " + bf.getTotalWeight() + "\tprofit: " + bf.getTotalProfit()
				+ "\tpackingplan: " + Arrays.toString(bf.getPackingPlan()) + "\ttime:" + bf.getElapsedTime();
		System.out.println(record);

		TourDP dp = new TourDP(problem);
		dp.dynamicProgramming();

		record = "DP\t" + problem.name + "\t" + Arrays.toString(dp.getTour()) + "\tbenefit: " + dp.getMaxBenefit()
				+ "\tweight: " + dp.getTotalWeight() + "\tprofit: " + dp.getTotalProfit() + "\tpackingplan: "
				+ Arrays.toString(dp.getPackingPlan()) + "\ttime:" + dp.getElapsedTime();
		System.out.println(record);

		if (!equals(dp.getMaxBenefit(), bf.getMaxBenefit()) || !Arrays.equals(dp.getTour(), bf.getTour())
				|| dp.getTotalProfit() != bf.getTotalProfit() || dp.getTotalWeight() != bf.getTotalWeight()
				|| !Arrays.equals(dp.getPackingPlan(), bf.getPackingPlan())) {

			throw new RuntimeException("results disagree");
		}
	}

}
