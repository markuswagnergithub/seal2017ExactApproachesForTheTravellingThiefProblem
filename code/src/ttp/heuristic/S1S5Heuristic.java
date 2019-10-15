package ttp.heuristic;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;

import common.tools.Util;
import pwt.exact.dp.DynamicProgramming;
import ttp.problem.Tour;
import ttp.problem.TourLoader;
import ttp.problem.Ttp;
import ttp.problem.TtpSaver;
import ttp.tour.TspSolver;

public class S1S5Heuristic {
	public final static long TEN_MINUTES = TimeUnit.MINUTES.toMillis(10);
	
	public class Solution{
		public final double benefit;
		public final long elapsedTime;
		private final int[] tour;
		private final int[] packingPlan;
		
		public Solution(double benefit, int[] tour, int[] packingPlan, long elapsedTime){
			this.benefit = benefit;
			this.tour = tour;
			this.packingPlan = packingPlan;
			this.elapsedTime = elapsedTime;
		}
		
		public int[] getTour(){
			return tour.clone();
		}
		
		public int[] getPackingPlan(){
			return packingPlan.clone();
		}

		@Override
		public String toString() {
			return "Solution [benefit=" + benefit + ", elapsedTime=" + elapsedTime + ", tour=" + Arrays.toString(tour)
					+ ", packingPlan=" + Arrays.toString(packingPlan) + "]";
		}
	}
	
	public final Ttp problem;
	public final TspSolver.SolverName solverName;
	public final long maxRuntime;
	
	public S1S5Heuristic(Ttp problem, TspSolver.SolverName solverName, long maxRuntime){
		this.problem = problem;
		this.solverName = solverName;
		this.maxRuntime = maxRuntime;
	}
	

	
	public Tour solveDifferently(HashSet<Integer> existingSolutions, long maxRuntime) {
		long startTime = System.currentTimeMillis();
		long elapsedTime = 0;

		TspSolver tspSolver = new TspSolver();

		final String uid = Util.uniqueId();
		final String tempTtpInstance = "/tmp/" + uid + ".ttp";
		final String tempTtpTour = "/tmp/" + uid + ".tour";

		TtpSaver.write(problem, Paths.get(tempTtpInstance));
		Tour tour = null;
		do {
			tspSolver.solve(tempTtpInstance, tempTtpTour, solverName);

			tour = TourLoader.load(tempTtpTour, problem, solverName.type());

			elapsedTime = System.currentTimeMillis() - startTime;
		} while (elapsedTime < maxRuntime && existingSolutions != null
				&& existingSolutions.contains(tour.essence.hashcode));

		return tour;
	}
	
	public void reverse(final int[] tour) {
		int from = 1;
		int to = tour.length - 1;
		Util.reverse(tour, from, to);
	}
	
	public Solution S1Heuristic(){
		long startTime = System.currentTimeMillis();
		long elapsedTime = 0;
		
		Tour tour = solveDifferently(null, maxRuntime);
		
		DynamicProgramming dp = new DynamicProgramming(tour);
		dp.dynamicProgramming();
		elapsedTime = System.currentTimeMillis() - startTime;
		
		return new Solution(dp.getMaxBenefit(), tour.essence.getStops(), dp.getPackingPlan(), elapsedTime);
	}
	
	public Solution findBetter(Tour tour, Solution current, long startTime){
		DynamicProgramming dp = new DynamicProgramming(tour);
		dp.dynamicProgramming();
		
		long elapsedTime = System.currentTimeMillis() - startTime;
		
		if (current == null || dp.getMaxBenefit() > current.benefit){
			return new Solution(dp.getMaxBenefit(), tour.essence.getStops(), dp.getPackingPlan(), elapsedTime);
		}else{
			return current;
		}
		
	}
	
	
	public Solution S5Heuristic4ExistingLknTours() throws IOException{
		long startTime = System.currentTimeMillis();
		
		Solution solution = null;
		
		Path ttpPath = Paths.get(problem.path);
		
		String fileNameFragments[] = ttpPath.getFileName().toString().split("_");
		String tourPrefix = fileNameFragments[0] + "_" + fileNameFragments[1];
		
		
		for (Path filePath: Files.newDirectoryStream(ttpPath.getParent(), 
				path -> path.getFileName().toString().matches(tourPrefix + "\\.tsp\\.\\d+\\.lkn\\.tour"))){
			Tour tour = TourLoader.loadLkhTour(filePath.toString(), problem);
			
			solution = findBetter(tour, solution, startTime);
			int[] stops = tour.essence.getStops();
			reverse(stops);
			tour.update(stops);
			
			solution = findBetter(tour, solution, startTime);
		}
		
		return solution;
	}
	
	
	
	public Solution S5Heuristic(){
		long startTime = System.currentTimeMillis();
		long elapsedTime = 0;
		
		Solution solution = null;
		HashSet<Integer> existingSolution = new HashSet<>();
		do {
			Tour tour = solveDifferently(existingSolution, maxRuntime);
			
			existingSolution.add(tour.essence.hashcode);
			
			solution = findBetter(tour, solution, startTime);
			
			//reverse the tour and run the DP again
			int[] stops = tour.essence.getStops();
			reverse(stops);
			tour.update(stops);
			
			existingSolution.add(tour.essence.hashcode);
			
			solution = findBetter(tour, solution, startTime);
			
			elapsedTime = System.currentTimeMillis() - startTime;
			
		} while (elapsedTime < maxRuntime);
		
		return solution;
	}
	
}
