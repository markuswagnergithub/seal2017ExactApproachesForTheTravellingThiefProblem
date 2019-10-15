package pwt.experiment.multiobjective;

import org.junit.Test;

import pwt.experiment.multiobjective.FrontExp;
import ttp.problem.Tour;
import ttp.problem.TtpLoader;

public class FrontExpTest {

	@Test
	public void testDp() {
		
		String instance = "eil51_n50_uncorr_01.ttp";
		
		Tour tour = TtpLoader.load("experiments/eil51/" + instance,
				"experiments/eil51/eil51.linkern.tour");
		
		String str = FrontExp.dp(instance, tour);
		
		System.out.println(str);
	}

}
