package pwt.experiment;

import java.io.FileNotFoundException;
import java.util.Arrays;

import ttp.dp.TourBnB;
import ttp.problem.Ttp;
import ttp.problem.TtpLoader;
import ttp.problem.Tour;

public class BbExp {

	private static void printUsage() {
		System.out.println("Usage:");
		System.out.println("BbExp TtpInstance initialTour");
		System.out.println("example: BbExp experiments/eil51_sub/eil51_n05_m4_uncorr_01.ttp");
		System.out.println("example: BbExp experiments/eil51_sub/eil51_n05_m4_uncorr_01.ttp eil51_n05.lkn.tour");
	}
	
	public static void main(String[] args) throws FileNotFoundException {
		if (args.length < 1) {
			printUsage();
			return;
		}
		
		String instance = args[0];
		String tourfile = null;
		
		if (args.length == 2){
			tourfile = args[1];
		}
				
		System.out.print(instance + "\tBnB\t");
		
		Ttp problem = null;
		int[] stops = null;
		if (tourfile == null){
			problem = TtpLoader.load(instance);
		}else{
			Tour tour = TtpLoader.load(instance, tourfile);
			stops = tour.essence.getStops();
			problem = tour.getProblem();
		}
		

		TourBnB bs = new TourBnB(problem, stops);
		bs.search();

		String record = Arrays.toString(bs.getTour()) + "\tbenefit: " + bs.getMaxBenefit()
				+ "\tweight: " + bs.getTotalWeight() + "\tprofit: " + bs.getTotalProfit() + "\tpackingplan: "
				+ Arrays.toString(bs.getPackingPlan()) + "\ttime:" + bs.getElapsedTime();
		System.out.println(record);

	}
	
	
}
