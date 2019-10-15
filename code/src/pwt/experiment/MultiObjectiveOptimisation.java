package pwt.experiment;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Map.Entry;

import pwt.exact.dp.DynamicProgramming;
import pwt.exact.dp.PackingPlan;
import ttp.problem.TtpLoader;
import ttp.problem.Tour;

public class MultiObjectiveOptimisation {
	private static final String folder = "experiments/eil101/";
	
	private static final String tourfile = "eil101.linkern.tour";

	private static final String[] testcases = new String[]{
		"eil101_n100_uncorr_01.ttp",
		"eil101_n100_uncorr_06.ttp",
		"eil101_n100_uncorr_10.ttp",
		"eil101_n100_uncorr-similar-weights_01.ttp",
		"eil101_n100_uncorr-similar-weights_06.ttp",
		"eil101_n100_uncorr-similar-weights_10.ttp",
		"eil101_n100_bounded-strongly-corr_01.ttp",
		"eil101_n100_bounded-strongly-corr_06.ttp",
		"eil101_n100_bounded-strongly-corr_10.ttp",
		"eil101_n500_uncorr_01.ttp",
		"eil101_n500_uncorr_06.ttp",
		"eil101_n500_uncorr_10.ttp",
		"eil101_n500_uncorr-similar-weights_01.ttp",
		"eil101_n500_uncorr-similar-weights_06.ttp",
		"eil101_n500_uncorr-similar-weights_10.ttp",
		"eil101_n500_bounded-strongly-corr_01.ttp",
		"eil101_n500_bounded-strongly-corr_06.ttp",
		"eil101_n500_bounded-strongly-corr_10.ttp",
		"eil101_n1000_uncorr_01.ttp",
		"eil101_n1000_uncorr_06.ttp",
		"eil101_n1000_uncorr_10.ttp",
		"eil101_n1000_uncorr-similar-weights_01.ttp",
		"eil101_n1000_uncorr-similar-weights_06.ttp",
		"eil101_n1000_uncorr-similar-weights_10.ttp",
		"eil101_n1000_bounded-strongly-corr_01.ttp",
		"eil101_n1000_bounded-strongly-corr_06.ttp",
		"eil101_n1000_bounded-strongly-corr_10.ttp"
	};
	
	
	public static void front(String instance) throws IOException{
		System.out.println(instance);
		
		Tour tour = TtpLoader.load(folder + instance,
				folder + tourfile);

		DynamicProgramming dp = new DynamicProgramming(tour);
		dp.dynamicProgramming();


		StringBuilder sb = new StringBuilder();
		sb.append(instance + "\n");
		sb.append("Weight\tBenefit\n");
		
		Map<Long, PackingPlan> col = dp.getFinalColumn();
		
		for (Entry<Long, PackingPlan> entry : col.entrySet()) {
			sb.append(entry.getKey() + "\t" + entry.getValue()+"\n");
		}
		
		Files.write(Paths.get(folder + instance+ ".front"), sb.toString().getBytes());
		
	}
	
	
	public static void main(String[] args) throws IOException {
		for (String instance: testcases){
			front(instance);
		}
	}

}
