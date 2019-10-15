package ttp.moea;

import java.util.NavigableMap;
import java.util.TreeMap;

import org.junit.Test;

import common.tools.Util;

public class AttainmentSurfaceTest {


	
	@Test
	public void testHypervolume() {
		
		String ttpFile = "../../multiobj/experiments/eil76/eil76_n75_uncorr_01.ttp";
		Heuristic h = new Heuristic(ttpFile);
		
		h.initialise();
		
		NavigableMap<Long, Double> l = Statistics.surface(h.population);
		
		Util.printMap(l);
		
		double hv = Statistics.hypervolume(l, h.ttp.bigW, 0);
		
		System.out.println(hv);
	}

}
