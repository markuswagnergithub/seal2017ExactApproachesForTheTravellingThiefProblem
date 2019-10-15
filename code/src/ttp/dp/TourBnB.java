package ttp.dp;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import common.tools.Util;
import pwt.exact.dp.DynamicProgramming;
import ttp.problem.City;
import ttp.problem.Ttp;
import ttp.problem.TtpSaver;
import ttp.problem.Tour;
import ttp.search.TourBF;
import ttp.tour.TspSolver;

public class TourBnB {

	// item index starts from 1
	private final int itemIdxStart = 1;
	private final Ttp pb;
	private final int[] initialTour;
	private final Map<Integer, Integer> shortestDistances;
	private final int tspOptimum;

	private BigDecimal elapsedTime;
	private TtpSolution value;

	public TourBnB(Ttp problem, int[] initialTour) {
		this.pb = problem;

		if (initialTour == null) {
			this.initialTour = initialStops(pb.n);
		} else {
			this.initialTour = initialTour;
		}
		this.shortestDistances = Collections.unmodifiableMap(shortestDistances(pb));
		String uid = Util.uniqueId();
		this.tspOptimum = shortestTSPPath(pb, "/tmp/"+ uid +".ttp", "/tmp/"+ uid +".tour");
		
	}

	
	private static int shortestTSPPath(Ttp problem, final String instance, final String tour) {
		TtpSaver.write(problem, Paths.get(instance));
		
		TspSolver tspSolver = new TspSolver();
		
		return tspSolver.solve(instance, tour, TspSolver.SolverName.CONCORDE);
		
	}

	/*
	 * shortest distances between city 1 to city {2, ..., n}
	 */
	public static Map<Integer, Integer> shortestDistances(Ttp pb) {
		Map<Integer, Integer> dist = new HashMap<>();
		for (int i = 2; i <= pb.n; i++) {
			dist.put(i, pb.distance(1, i));
		}
		return dist;
	}

	private static int[] initialStops(int n) {
		return IntStream.rangeClosed(1, n).toArray();
	}

	private Map<Long, TtpSolution> packingDP(Map<Long, TtpSolution> preTour, double distance, int preStop, int curStop) {
		if (preTour == null || preTour.size() <= 0)
			return null;

		Map<Long, TtpSolution> preCol = new HashMap<>();

		for (long weight : preTour.keySet()) {
			double b = -distance * pb.rentingRate / (pb.maxSpeed - pb.nu * weight) + preTour.get(weight).beta;
			preCol.put(weight, composeValueWithNewStop(b, preTour.get(weight), curStop));
		}

		Map<Long, TtpSolution> curCol = null;
		for (int k = 0; k < pb.getCity(preStop).noOfItemsInCity(); k++) {
			curCol = new HashMap<>(preCol);

			final int itemIdx = pb.getCity(preStop).getItem(k);
			final long itemWeight = pb.getItem(itemIdx).weight;
			final long profit = pb.getItem(itemIdx).profit;

			for (long weight : preCol.keySet()) {
				long w = weight + itemWeight;
				if (w > pb.bigW)
					continue;
				double b = profit - (distance * (pb.rentingRate / (pb.maxSpeed - (pb.nu * w))))
						+ (distance * (pb.rentingRate / (pb.maxSpeed - (pb.nu * weight))));

				if (b < 0) { // in this case
					continue; // as beta(k, w) is dominated by beta(k, weight),
				} // skip the rest.

				b += preCol.get(weight).beta;

				if (!curCol.containsKey(w) || b > curCol.get(w).beta) {
					curCol.put(w, composeValueWithNewItem(b, preCol.get(weight), itemIdx));
				}
			}

			TreeSet<Long> treeSet = new TreeSet<Long>(curCol.keySet());
			double b = curCol.get(treeSet.first()).beta;
			for (long key : treeSet) {
				if (curCol.get(key).beta < b) {
					curCol.remove(key);
				} else {
					b = curCol.get(key).beta;
				}
			}

			preCol = curCol;
		}

		return preCol;
	}

	private void swap(int[] tour, int i, int j) {
		int t = tour[i];
		tour[i] = tour[j];
		tour[j] = t;
	}

	public TtpSolution best(Map<Long, TtpSolution> ret) {
		if (ret == null || ret.size() == 0)
			return null;
		TtpSolution v = null;
		double best = -Double.MAX_VALUE;
		for (long k : ret.keySet()) {
			if (ret.get(k).beta > best) {
				v = ret.get(k);
				best = v.beta;
			}
		}
		return v;
	}

	public TtpSolution search(int[] tour, int l, Map<Long, TtpSolution> valueSoFar, TtpSolution bestSoFar) {
		// System.out.println(Arrays.toString(tour) + "\t" + l + "\t" +
		// valueSoFar);
		if (l == tour.length - 1) {
			Map<Long, TtpSolution> ret = packingDP(valueSoFar, pb.distance(tour[tour.length - 1], 1), tour[tour.length - 1],
					1);
			TtpSolution d = best(ret);

			if (d.beta > bestSoFar.beta) {
//				System.out.println(d);
				return d;
			} else {
				return bestSoFar;
			}
		} else {
			TtpSolution max = bestSoFar;
			for (int i = l + 1; i < tour.length; i++) {
				swap(tour, l + 1, i);
				Map<Long, TtpSolution> ret = packingDP(valueSoFar, pb.distance(tour[l], tour[l + 1]), tour[l], tour[l + 1]);

				if (possible(tour, l + 1, ret, max)) {
					TtpSolution v = search(tour, l + 1, ret, max);
					if (v.beta > max.beta) {
						max = v;
					}
				}
				swap(tour, l + 1, i);
			}
			return max;
		}
	}

	private boolean possible(int[] tour, int l, Map<Long, TtpSolution> ret, TtpSolution bestSoFar) {
		// System.out.println(Arrays.toString(tour) + "\t" + l);
		// System.out.println(ret);
		// System.out.println(bestSoFar);
		long tp = 0;
		for (int i = l; i < tour.length; i++) {
			City city = pb.getCity(tour[i]);
			for (int j = 0; j < city.noOfItemsInCity(); j++) {
				tp += pb.getItem(city.getItem(j)).profit;
			}
		}

		int d = 0;
		for (int i = 1; i <= l; i++) {
			d += pb.distance(tour[i - 1], tour[i]);
		}

		d = Math.max(this.shortestDistances.get(tour[l]), this.tspOptimum - d);
//		int d = this.shortestDistances.get(tour[l]);
		
		double tb = best(ret).beta + tp
				- pb.rentingRate *  d / pb.maxSpeed;

		// System.out.println(tb);

		if (tb > bestSoFar.beta) {
			return true;
		} else {
			return false;
		}
	}

	public TtpSolution initialValue() {
		Tour tour = new Tour(pb);
		tour.update(initialTour);
		DynamicProgramming dp = new DynamicProgramming(tour);
		dp.dynamicProgramming();

		return new TtpSolution(dp.Beta(), dp.getSolutionBits(), TourBF.concat(initialTour, new int[] { 1 }));
	}

	public void search() {
		final long startTime = System.nanoTime();

		int[] tour = initialStops(pb.n);
		Map<Long, TtpSolution> col = new HashMap<>();
		col.put(0l, new TtpSolution(0, BigInteger.ZERO, new int[] { 1 }));
		value = search(tour, 0, col, initialValue());

		// System.out.println(value);

		elapsedTime = new BigDecimal(TimeUnit.MILLISECONDS.convert(System.nanoTime() - startTime, TimeUnit.NANOSECONDS))
				.divide(new BigDecimal(1000));
	}

	private TtpSolution composeValueWithNewStop(double beta, TtpSolution old, int lastStop) {
		int[] stops = new int[old.getTourLength() + 1];
		for (int i = 0; i < old.getTourLength(); i++) {
			stops[i] = old.getTour(i);
		}
		stops[stops.length - 1] = lastStop;
		return new TtpSolution(beta, old.packingPlan, stops);
	}

	private TtpSolution composeValueWithNewItem(double beta, TtpSolution old, int pickedItemIdx) {
		BigInteger packingPlan = old.packingPlan.setBit(pickedItemIdx - this.itemIdxStart);
		return new TtpSolution(beta, packingPlan, old.getTour());
	}

	public double getMaxBenefit() {
		return value.beta;
	}

	public long getTotalWeight() {
		long total = 0;
		for (int i = 0; i < pb.getNoOfItems(); i++) {
			if (value.packingPlan.testBit(i)) {
				total += pb.getItem(i + this.itemIdxStart).weight;
			}
		}
		return total;
	}

	public long getTotalProfit() {
		long total = 0;
		for (int i = 0; i < pb.getNoOfItems(); i++) {
			if (value.packingPlan.testBit(i)) {
				total += pb.getItem(i + this.itemIdxStart).profit;
			}
		}
		return total;
	}

	public BigDecimal getElapsedTime() {
		return elapsedTime;
	}

	public BigInteger getPackingPlanBits() {
		return value.packingPlan;
	}

	public int[] getPackingPlan() {
		TreeSet<Integer> items = new TreeSet<>();
		for (int i = 0; i < pb.getNoOfItems(); i++) {
			if (value.packingPlan.testBit(i)) {
				items.add(i + this.itemIdxStart);
			}
		}
		int[] ret = new int[items.size()];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = items.pollFirst();
		}
		return ret;
	}

	public int[] getTour() {
		if (this.value == null)
			return null;
		return value.getTour();
	}
}
