package ttp.problem;

import java.io.IOException;
import java.util.TreeSet;

import org.junit.Test;

import pwt.exact.dp.DynamicProgramming;
import ttp.tour.TourType;

public class ProblemLoaderTest {

	@Test
	public void testloadConcordeTour() throws IOException {
		
		Ttp problem = TtpLoader.load("experiments/eil76/eil76_n75_uncorr_01.ttp");
		
		Tour tour = TourLoader.load("experiments/eil76/eil76.tsp.1.con.tour", problem, TourType.con);
		
//		System.out.println(tour);
		
		DynamicProgramming dp = new DynamicProgramming(tour);
		
		dp.dynamicProgramming();
		
		
		TreeSet<Long> keySet = new TreeSet<>(dp.getFinalColumn().keySet());
		
		for (Long key :keySet){
			System.out.println(key + "\t" + dp.getFinalColumn().get(key).beta);
		}
		
		
	}

}
