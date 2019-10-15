package ttp.dp;

import java.math.BigInteger;
import java.util.Arrays;

public class TtpSolution {
	public final double beta;
	public final BigInteger packingPlan;
	private final int[] tour;
	
	public TtpSolution(double beta, BigInteger packingPlan, int[] tour){
		this.beta = beta;
		this.packingPlan = packingPlan;
		this.tour = tour.clone();
	}
	
	public int getTour(int pos){
		return tour[pos];
	}
	
	public int[] getTour(){
		return tour.clone();
	}
	
	public int getTourLength(){
		return tour.length;
	}
	
	public void copyTourTo(int[] newTour) {
		if (newTour.length < tour.length) {
			throw new RuntimeException("The length of new tour is less than the one of current tour.");
		}
		System.arraycopy(tour, 0, newTour, 0, tour.length);
	}

	@Override
	public String toString() {
		return "[beta=" + beta + ", packingPlan=" + packingPlan.toString(2) + ", tour=" + Arrays.toString(tour) + "]";
	}
	
	
}
