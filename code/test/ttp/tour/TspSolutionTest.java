package ttp.tour;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import common.tools.Util;
import ttp.problem.Ttp;
import ttp.problem.TtpLoader;

public class TspSolutionTest {

	@Test
	public void testReverse() {
		String ttpFile = "../../multiobj/experiments/eil76/eil76_n75_bounded-strongly-corr_01.ttp";
		Ttp ttp = TtpLoader.load(ttpFile);
		
		TspSolution tsp = InverOver.run(ttp);
		
		System.out.println(tsp);
		System.out.println(tsp.reverse());
		
	}
	
	@Test
	public void testAlign() {
		
		ArrayList<Integer> al = new ArrayList<>(Arrays.asList(new Integer[]{2,3,4,1,6, 8,9,5,7}));
		ArrayList<Integer> t = TspSolution.align(al);
		System.out.println(t);
		Assert.assertArrayEquals(new Integer[]{1, 6, 8, 9, 5, 7, 2, 3, 4}, t.toArray(new Integer[t.size()]));
	
		al = new ArrayList<>(Arrays.asList(new Integer[]{1, 2,3,4,6, 8,9,5,7}));
		t = TspSolution.align(al);
		System.out.println(t);
		Assert.assertArrayEquals(new Integer[]{1, 2,3,4,6, 8,9,5,7}, t.toArray(new Integer[t.size()]));
	
		al = new ArrayList<>(Arrays.asList(new Integer[]{2,3,4,6, 8,9,5,7,1}));
		t = TspSolution.align(al);
		System.out.println(t);
		Assert.assertArrayEquals(new Integer[]{1,2,3,4,6, 8,9,5,7}, t.toArray(new Integer[t.size()]));
		
		
	}
	
	@Test
	public void testAlignArray() {
		
		int[] al = new int[]{2,3,4,1,6, 8,9,5,7};
		int[] t = Util.align(al);
		System.out.println(Arrays.toString(t));
		Assert.assertArrayEquals(new int[]{1, 6, 8, 9, 5, 7, 2, 3, 4}, t);
	
		al = new int[]{1, 2,3,4,6, 8,9,5,7};
		t = Util.align(al);
		System.out.println(Arrays.toString(t));
		Assert.assertArrayEquals(new int[]{1, 2,3,4,6, 8,9,5,7}, t);
	
		al = new int[]{2,3,4,6, 8,9,5,7,1};
		t = Util.align(al);
		System.out.println(Arrays.toString(t));
		Assert.assertArrayEquals(new int[]{1,2,3,4,6, 8,9,5,7}, t);
		
		
	}

}
