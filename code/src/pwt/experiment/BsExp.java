package pwt.experiment;

import java.io.FileNotFoundException;
import java.util.Arrays;

import ttp.problem.Ttp;
import ttp.problem.TtpLoader;
import ttp.search.BeamSearch;

public class BsExp {

	public static void main(String[] args) throws FileNotFoundException {
		
		System.out.println("beamWidth=" + args[0]);
		
		String instance = "eil51_n50_uncorr_01.ttp";

		Ttp problem = TtpLoader.load("experiments/eil51/" + instance);

		BeamSearch bs = new BeamSearch(problem, Integer.parseInt(args[0]));
		bs.search();

		String record = instance + "\t" + Arrays.toString(bs.getTour()) + "\tbenefit: " + bs.getMaxBenefit()
				+ "\tweight: " + bs.getTotalWeight() + "\tprofit: " + bs.getTotalProfit() + "\tpackingplan: "
				+ Arrays.toString(bs.getPackingPlan()) + "\ttime:" + bs.getElapsedTime();
		System.out.println(record);

	}

}
