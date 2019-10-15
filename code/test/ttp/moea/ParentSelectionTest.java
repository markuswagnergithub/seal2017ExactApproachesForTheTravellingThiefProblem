package ttp.moea;


import java.util.Arrays;

import org.junit.Test;

public class ParentSelectionTest {

	@Test
	public void testHarmonicProbability() {
		double[] w = ParentSelection.harmonicProbability(50);
		
		System.out.println(Arrays.toString(w));
		
	}
	
	@Test
	public void testExponentialProbability() {
		double[] w = ParentSelection.exponentialProbability(50);
		
		System.out.println(Arrays.toString(w));
		
	}
	
	@Test
	public void testInverseQuadraticProbability() {
		double[] w = ParentSelection.inverseQuadraticProbability(50);
		
		System.out.println(Arrays.toString(w));
		
	}

}
