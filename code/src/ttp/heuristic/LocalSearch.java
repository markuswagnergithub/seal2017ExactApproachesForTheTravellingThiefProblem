package ttp.heuristic;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

import common.tools.Util;
import pwt.exact.dp.DynamicProgramming;
import ttp.problem.Tour;

public class LocalSearch {
	public enum Heuristic {TWO_OPT, TWO_OPT_FROM_END, EXCHANGE, EXCHANGE_FROM_END, JUMP, JUMP_FROM_END }
	
	private Tour tour;
	
	public LocalSearch(Tour tour) {
		this.tour = tour;
	}

	public void printPermutation() {
		StringBuilder sb = new StringBuilder();
		StringBuilder distance = new StringBuilder();
		StringBuilder traveled = new StringBuilder();
		
		sb.append("cities:  [");
		distance.append("distance:[");
		traveled.append("traveled:[");
		for (int i=0;i<tour.length;i++) {
			sb.append(tour.essence.getStop(i));
			sb.append('\t');
			distance.append(tour.adjacentDistances[i]);
			distance.append('\t');
			traveled.append(tour.traveledDistances[i]);
			traveled.append('\t');
		}
		sb.setCharAt(sb.length() - 1, ']');
		System.out.println(sb);
		
		distance.setCharAt(distance.length() - 1, ']');
		System.out.println(distance);
		
		traveled.setCharAt(traveled.length() - 1, ']');
		System.out.println(traveled);
	}
	
	public void printItems() {
		StringBuilder sb = new StringBuilder();
		sb.append("items:[");
		for (int itemIdx : tour.itemsByTour) {
			sb.append(itemIdx);
			sb.append(',');
		}
		sb.setCharAt(sb.length() - 1, ']');
		System.out.println(sb);
	}
	


	
	public double runLocalSearch(Heuristic heuristic){
		final long start = System.nanoTime();
		
		double initial = runIteration(heuristic);
		double current = runIteration(heuristic);
		
		while(current > initial){
			initial = current;
			current = runIteration(heuristic);
		}
		
		System.out.println("running time (s):"+new BigDecimal(TimeUnit.MILLISECONDS.convert(
				System.nanoTime() - start, TimeUnit.NANOSECONDS))
				.divide(new BigDecimal(1000)));
		
		return initial;
	}

	private double runIteration(Heuristic heuristic) {
		if (heuristic.toString().endsWith("END")){
			return runIterationFromEnd(heuristic);
		}else{
			return runIterationFromBeginning(heuristic);
		}
	}
	
	private void operate(Heuristic heuristic, int[] nodes, int i, int k) {
		switch (heuristic) {
		case TWO_OPT:
			Util.reverse(nodes, i, k);
			break;
		case EXCHANGE:
			Util.swap(nodes, i, k);
			break;
		case JUMP:
			Util.jump(nodes, i, k);
			break;
		case TWO_OPT_FROM_END:
			Util.reverse(nodes, i, k);
			break;
		case EXCHANGE_FROM_END:
			Util.swap(nodes, i, k);
			break;
		case JUMP_FROM_END:
			Util.jump(nodes, i, k);
			break;
		default:
			throw new IllegalArgumentException("Invalid heuristic");
		}
	}
	
	
	
	public double runIterationFromBeginning(Heuristic heuristic){
		double best = getInitial();
//		long totalDistances = tour.totalDistance();
		for (int i = 1; i < tour.essence.length - 1; i++) {
			for (int k = i + 1; k < tour.essence.length; k++) {
				int[] newTour = tour.essence.getStops();
				operate(heuristic, newTour, i, k);
				Tour tt = new Tour(tour);
				tt.update(newTour);
//				if (tt.totalDistance() < (0.9*totalDistances)){
//					continue;
//				}
				DynamicProgramming dp = new DynamicProgramming(tt);
				dp.dynamicProgramming();
				double current = dp.getMaxBenefit();
				if (current > best) {
					tour = tt;
					System.out.println("############################");
					System.out.println("i=" + i + " k=" + k +" current:" + current);
					printPermutation();
					System.out.println("total distance:" + tour.totalDistance());
					return current;
				}
//				totalDistances = tour.totalDistance();
			}
		}
		return best;
	}
	
	public double runIterationFromEnd(Heuristic heuristic){
		double best = getInitial();
//		long totalDistances = tour.totalDistance();
		for (int i = tour.length - 1; i > 0; i--) {
			for (int k = i - 1; k > i; k--) {
				int[] newTour = tour.essence.getStops();
				operate(heuristic, newTour, i, k);
				Tour tt = new Tour(tour);
				tt.update(newTour);

//				if (tp.totalDistance() > (1.1*totalDistances)){
//					continue;
//				}
				DynamicProgramming dp = new DynamicProgramming(tt);
				dp.dynamicProgramming();
				double current = dp.getMaxBenefit();
				if (current > best) {
					tour = tt;
					System.out.println("############################");
					System.out.println("i=" + i + " k=" + k +" current:" + current);
					printPermutation();
					System.out.println("total distance:" + tour.totalDistance());
					return current;
				}
			}
		}
		return best;
	}
	
	public double getInitial() {
		DynamicProgramming idp = new DynamicProgramming(tour);
		idp.dynamicProgramming();
		double best = idp.getMaxBenefit();
		System.out.println("initial:" + best);
		printPermutation();
		printItems();
		System.out.println("total distance:" + tour.totalDistance());
		return best;
	}
	
}
