package ttp.tour;


import org.junit.Test;

import ttp.problem.Ttp;
import ttp.problem.TtpLoader;

public class InverOverTest {

	@Test
	public void testRun() {
		String ttpFile = "../../multiobj/experiments/eil76/eil76_n375_bounded-strongly-corr_01.ttp";
//		String ttpFile = "./experiments/eil51_sub/eil51_n10_m9_uncorr_01.ttp";
		
		Ttp ttp = TtpLoader.load(ttpFile);
		
		long start = System.currentTimeMillis();
		InverOver.run(ttp);
		
		long end = System.currentTimeMillis();
		System.out.println(end - start);
	}

}
