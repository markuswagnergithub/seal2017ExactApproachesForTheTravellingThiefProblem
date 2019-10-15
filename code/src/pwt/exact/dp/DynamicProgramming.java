package pwt.exact.dp;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

import ttp.problem.Tour;
import ttp.problem.Ttp;
import ttp.problem.TtpLoader;

public class DynamicProgramming {

	private BigDecimal elapsedTime;
	private Long totalWeight = null;
	private HashMap<Long, PackingPlan> curCol = null;
	
	private BitSet solution = null;
	private double beta = 0;
	
	private final double cost0;
	private final long totalDistance;
	private final double emptyset;
	private final double r;
	private final double vm;
	private final double nu;
	private final Tour tour;
	private final Ttp pb;
	

	public static void main(String[] args) throws IOException,
			CloneNotSupportedException {

		String instance = "eil51_n50_bounded-strongly-corr_01.ttp";
		
		Tour tour = TtpLoader.load("experiments/eil51/" + instance,
				"experiments/eil51/eil51.linkern.tour");

//		Problem problem = ProblemLoader.load(
//				"experiments/case001/test1_n2_m4.nkp",
//				"experiments/case001/test1_n2_m4.tour");

		DynamicProgramming dp = new DynamicProgramming(tour);
		dp.dynamicProgramming();

		// Log.recordToTxtFile(problem.toString());
		System.out
				.println("ProblemName\tObject\ttotalWeight\ttotalProfit\tRunningTime(s)\tSolution");
		// Utils.recordToFile((Problem) ea.getIndividual());

		System.out.println(tour.getProblem().name + "\t" + dp.getMaxBenefit());
//				+ "\t" + dp.getTotalWeight() + "\t" + dp.getTotalProfit()
//				+ "\t" + dp.getElapsedTime() + "\t" + dp.getSolution());

		dp.getSolution();
		dp.getMaxBenefit();

		// for (Entry<Long, Cell> entry : dp.curCol.entrySet()) {
		// System.out.println(entry.getKey() + "\t" + entry.getValue().beta() +
		// "\t" + entry.getValue().solution().toString());
		// }

	}
	
	public DynamicProgramming(Ttp ttp, Tour.Essence tour) {
		this(new Tour(ttp, tour.getStops()));
	}

	public DynamicProgramming(Tour tour) {
		this.tour = tour;
		this.pb = tour.getProblem();
		
		cost0 = pb.rentingRate /pb.maxSpeed;

		// System.out.println("bigW:" + bigW);

		totalDistance = tour.totalDistance();
		emptyset = -cost0 * totalDistance;

		r = pb.rentingRate;
		vm = pb.maxSpeed;
		nu = pb.nu;

	}
	


	public void dynamicProgramming() {
		final long start = System.nanoTime();

		HashMap<Long, PackingPlan> preCol = new HashMap<>();

		// address the first item (meaning the first column)
		final long zeroWeight = 0L;
		preCol.put(zeroWeight, new PackingPlan(emptyset, new BitSet(pb.m))); // beta(emptyset)

		final int firstItem = 0;
		if (pb.getItem(tour.itemsByTour[firstItem]).weight <= pb.bigW) {
			double benefit = pb.getItem(tour.itemsByTour[firstItem]).profit
					- (tour.traveledDistances[tour.stopsOfItems[firstItem]] * cost0)
					- ((totalDistance - tour.traveledDistances[tour.stopsOfItems[firstItem]]) * 
							(r / (vm - (nu * pb.getItem(tour.itemsByTour[firstItem]).weight))));
			if (benefit > emptyset) {
				preCol.put(pb.getItem(tour.itemsByTour[firstItem]).weight, new PackingPlan(benefit,
						composeSolution(firstItem)));
			}
		}

		// calculation starts from the second item
		for (int m = firstItem + 1; m < pb.getNoOfItems(); m++) {
			curCol = new HashMap<>(preCol);

			final long remainingDistance = totalDistance
					- tour.traveledDistances[tour.stopsOfItems[m]];
			final long weight = pb.getItem(tour.itemsByTour[m]).weight;
			final long profit = pb.getItem(tour.itemsByTour[m]).profit;
			
			for (long key : preCol.keySet()) {
				long k = key + weight;
				if (k <= pb.bigW) {
					double b = profit
							- (remainingDistance * (r / (vm - (nu * k))))
							+ (remainingDistance * (r / (vm - (nu * (k - weight)))));
					if (b < 0) { // in this case
						continue; // as beta(m, k) is dominated by beta(m, key),
									// skip the rest.
					}
					b += preCol.get(key).beta;

					if (!curCol.containsKey(k) || b > curCol.get(k).beta) {
						curCol.put(
								k,
								new PackingPlan(b, composeSolution(m, preCol.get(key)
										.solution())));
					}
				}
			}

			TreeSet<Long> treeSet = new TreeSet<Long>(curCol.keySet());
			double b = emptyset;
			for (long key : treeSet) {
				if (curCol.get(key).beta < b) {
					curCol.remove(key);
				} else {
					b = curCol.get(key).beta;
				}
			}

			preCol = curCol;
		}

		elapsedTime = new BigDecimal(TimeUnit.MILLISECONDS.convert(
				System.nanoTime() - start, TimeUnit.NANOSECONDS))
				.divide(new BigDecimal(1000));

	}

	public String getSolution() {
		if (totalWeight == null) {
			getTotalWeight();
		}
		return solutionToString();
	}
	
	public int[] getPackingPlan(){
		if (totalWeight == null) {
			getTotalWeight();
		}
		
		return solution.stream().toArray();
	}

	public long getTotalProfit() {
		if (totalWeight == null) {
			getTotalWeight();
		}
		return totalProfitInSolution();
	}

	public long getTotalWeight() {
		if (totalWeight != null) {
			return totalWeight;
		}

		double maxBenefit = - Double.MAX_VALUE;
		long totalW = -1;
		for (Entry<Long, PackingPlan> entry : curCol.entrySet()) {
			if (entry.getValue().beta > maxBenefit) {
				maxBenefit = entry.getValue().beta;
				totalW = entry.getKey();
			}
		}
		totalWeight = totalW;
		solution = setSolution(curCol.get(totalWeight).solution());
		beta = maxBenefit;
		return totalWeight;
	}

	public double getMaxBenefit() {
		if (totalWeight == null) {
			getTotalWeight();
		}

		return pb.benefit(tour.essence.getStops(), solution);
	}

	public BigDecimal getElapsedTime() {
		return elapsedTime;
	}

	public int getNoOfEntries() {
		return curCol.size();
	}

	private BitSet composeSolution(int m) {
		BitSet bs = new BitSet(pb.m);
		bs.set(m);
		return bs;
	}

	private BitSet composeSolution(int m, BitSet previous) {
		previous.set(m);
		return previous;
	}

	private long totalProfitInSolution() {
		return pb.totalProfit(solution);
	}
	
	private BitSet setSolution(BitSet solutionBits){
		BitSet solution = new BitSet(pb.m);
		for (int i=0;i<tour.itemsByTour.length;i++) {
			solution.set(tour.itemsByTour[i], solutionBits.get(i));
		}
		return solution;
	}
	
	
	public BigInteger getSolutionBits(){
		final int itemIdxStart = 1; //index of items starts from 1
		
		if (totalWeight == null) {
			getTotalWeight();
		}
		BigInteger solutionBits = BigInteger.ZERO;
		for (int k : solution.stream().toArray()) {
			solutionBits = solutionBits.setBit(k - itemIdxStart);
		}
		
		return solutionBits;
	}

	private String solutionToString() {
		StringBuilder sb = new StringBuilder();
		for (int i = 1; i <= pb.m; i++) {
			if (solution.get(i)) {
				sb.append('1');
			} else {
				sb.append('0');
			}
		}
		return sb.toString();
	}

	
	public double Beta(){
		if (totalWeight == null) {
			getTotalWeight();
		}
		return beta;
	}

	public Map<Long, PackingPlan> getFinalColumn() {
		return this.curCol;
	}
	
	public TreeMap<Long, PackingPlan> getSortedFinalColumn() {
		return new TreeMap<>(this.curCol);
	}
	
}
