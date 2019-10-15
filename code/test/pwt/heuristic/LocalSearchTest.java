package pwt.heuristic;


import org.junit.Test;

import ttp.problem.Ttp;
import ttp.problem.TtpLoader;

public class LocalSearchTest {

	@Test
	public void testSearch() {
		String problemFile = "experiments/eil76/eil76_n750_uncorr_10.ttp";
//		String problemFile = "experiments/test/test1_n8_m14.ttp";
		
		Ttp ttp = TtpLoader.load(problemFile);
		
		LocalSearch ls = new LocalSearch(ttp);
		
		ls.search();
		
	
	}

}
