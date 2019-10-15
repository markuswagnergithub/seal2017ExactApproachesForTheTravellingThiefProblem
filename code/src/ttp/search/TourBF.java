package ttp.search;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import pwt.exact.dp.DynamicProgramming;
import ttp.problem.Ttp;
import ttp.problem.Tour;

/*
 * Brute force search
 * 
 * This search goes through all possible tours to search for 
 * the best benefit with Dynamic Programming approach for PWT.
 * 
 */

public class TourBF {
	
	public final Ttp problem;
	
	private BigDecimal elapsedTime;
	private double finalBenefit = - Double.MAX_VALUE;
	private int[] finalTour = null;
	private long finalWeight = 0;
	private long finalProfit = 0;
	private int[] finalPackingPlan = null;
	
	public TourBF(Ttp pb){
		this.problem = pb;
	}

	public static long factorial(long n) {
		long fact = 1; // this  will be the result
        for (long i = 1; i <= n; i++) {
            fact *= i;
        }
        return fact;
    }
	
	public static int[] concat(int[] a, int[] b) {
		int[] c = new int[a.length + b.length];
		System.arraycopy(a, 0, c, 0, a.length);
		System.arraycopy(b, 0, c, a.length, b.length);
		return c;
	}
	
	public static void nextPermutation(int[] num) {
        int i = 0;
        int j = 0;
        //From right to left, find the first one that is not in ascending order.
        for (i = num.length - 2; i >= 0; i--) {
            if (num[i] >= num[i + 1])
                continue;
            //From right to left, find the first one that is larger than num[i]
            for (j = num.length - 1; j > i; j--) {
                if (num[j] > num[i])
                    break;
            }
            break;
        }
        //If we find i, swap the number on position i and j
        if (i >= 0) {
            int tmp = num[i];
            num[i] = num[j];
            num[j] = tmp;
        }
        //Reverse the numbers which are on the right of i
        int start = i + 1;
        int end = num.length - 1;
        while (start < end) {
            int tmp = num[start];
            num[start] = num[end];
            num[end] = tmp;
            start++;
            end--;
        }
    }
	
	private int[] initialStops(){
		return IntStream.rangeClosed(2, problem.n).toArray();
	}
	
	public void search(){
		final long startTime = System.nanoTime();
		
		int[] t = initialStops(); 
		
		for (int i=0;i<factorial(t.length);i++){
			int[]  tt = concat(new int[]{1}, t);
			
			Tour tour = new Tour(problem);
			tour.update(tt);
			
			DynamicProgramming dp = new DynamicProgramming(tour);
			dp.dynamicProgramming();
			
			double benefit = dp.getMaxBenefit();
			if (benefit > finalBenefit){
				finalBenefit = benefit;
				finalTour = tt;
				finalWeight = dp.getTotalWeight();
				finalProfit = dp.getTotalProfit();
				finalPackingPlan = dp.getPackingPlan();
			}
			nextPermutation(t);
		}
		
		elapsedTime = new BigDecimal(TimeUnit.MILLISECONDS.convert(System.nanoTime() - startTime, TimeUnit.NANOSECONDS))
				.divide(new BigDecimal(1000));
	}


	public double getMaxBenefit() {
		return finalBenefit;
	}

	public int[] getTour() {
		return concat(finalTour, new int[]{1});
	}

	public long getTotalWeight() {
		return finalWeight;
	}

	public long getTotalProfit() {
		return finalProfit;
	}

	public int[] getPackingPlan() {
		return this.finalPackingPlan;
	}
	
	public BigDecimal getElapsedTime() {
		return elapsedTime;
	}
}
