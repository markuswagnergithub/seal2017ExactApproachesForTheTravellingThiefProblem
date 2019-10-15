package ttp.dp;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

import common.tools.Util;
import pwt.exact.dp.DynamicProgramming;
import ttp.problem.City;
import ttp.problem.Item;
import ttp.problem.Ttp;
import ttp.problem.TtpSaver;
import ttp.problem.Tour;
import ttp.tour.TspSolver;

public class TourDpV2 {
	private BigDecimal elapsedTime;

	private final Ttp pb;

	private final double cost0;

	// item index starts from 1
	private final int itemIdxStart = 1;
	// city index starts from 1, i.e. {1, 2, ..., n}
	private final int cityIdxStart = 1;
	// the index of the first city.
	private final int thefirstCity = 1;

	private final Map<Integer, Integer> shortestDistances;
	private final int tspOptimum;

	private final TtpSolution bestInitially;

	private TtpSolution value;

	public final class DpKey {
		public final int cityIdx;
		public final long carriedWeight;
		public final int distance;
		
		public final int hashcode;

		public DpKey(int cityIdx, long carriedWeight, int distance) {
			this.cityIdx = cityIdx;
			this.carriedWeight = carriedWeight;
			this.distance = distance;
			this.hashcode = Objects.hash(cityIdx, distance, carriedWeight);
		}

		@Override
		public int hashCode() {
			return hashcode;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			DpKey other = (DpKey) obj;
			if (cityIdx != other.cityIdx)
				return false;
			if (distance != other.distance)
				return false;
			if (carriedWeight != other.carriedWeight)
				return false;
			return true;
		}

		@Override
		public String toString() {
			return "DpKey [cityIdx=" + cityIdx + ", carriedWeight=" + carriedWeight
					+ ", distance=" + distance + "]";
		}
	}

	public final class DpValue {
		public final double benefit;
		public final BigInteger packingPlan;

		public DpValue(double benefit, BigInteger packingPlan) {
			this.benefit = benefit;
			this.packingPlan = packingPlan;
		}

		@Override
		public String toString() {
			return "DpValue [benefit=" + benefit + ", packingPlan=" + packingPlan.toString(2) + "]";
		}
	}

	public TourDpV2(Ttp problem) {
		this.pb = problem;

		this.cost0 = pb.rentingRate / pb.maxSpeed;

		this.shortestDistances = Collections.unmodifiableMap(TourBnB.shortestDistances(pb));
		final String uid = Util.uniqueId();
		final String instance = "/tmp/" + uid + ".ttp";
		final String tourfile = "/tmp/" + uid + ".tour";
		TtpSaver.write(problem, Paths.get(instance));
		TspSolver tspSolver = new TspSolver();
		this.tspOptimum = tspSolver.solve(instance, tourfile, TspSolver.SolverName.CONCORDE);
		this.bestInitially = initiallyBest(pb, tourfile);
//		System.out.println(bestInitially);
	}

	public static TtpSolution initiallyBest(Ttp pb, String tourfile) {

		int[] stops = readStops(pb.n, tourfile);

		Tour tour = new Tour(pb);
		tour.update(stops);

		DynamicProgramming dp = new DynamicProgramming(tour);
		dp.dynamicProgramming();

		return new TtpSolution(dp.Beta(), dp.getSolutionBits(), stops);
	}

	private static int[] readStops(final int n, final String tourfile) {
		final int cityIndexStart = 1;
		int[] stops = new int[n];

		try {
			List<String> tourstr = Files.readAllLines(Paths.get(tourfile));
			// System.out.println(tourstr);
			int c = 0;
			for (int i = 1; i < tourstr.size(); i++) {
				for (String s : tourstr.get(i).split("\\s")) {
					stops[c] = Integer.parseInt(s) + cityIndexStart;
					c++;
				}
				;
			}
			;

		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("IOException", e);
		}
		// System.out.println(Arrays.toString(stops));

		return stops;
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

	private Map<Long, DpValue> packItemsInCity(DpKey key) {
		Map<Long, DpValue> preCol = new HashMap<>();

		double b = -key.distance * pb.rentingRate / (pb.maxSpeed - pb.nu * key.carriedWeight);
		preCol.put(key.carriedWeight, new DpValue(b, BigInteger.ZERO));

		Map<Long, DpValue> curCol = null;

		for (int k = 0; k < pb.getCity(key.cityIdx).noOfItemsInCity(); k++) {
			curCol = new HashMap<>(preCol);

			final int itemIdx = pb.getCity(key.cityIdx).getItem(k);
			final Item item = pb.getItem(itemIdx);
			final long itemWeight = item.weight;
			final long profit = item.profit;

			for (long weight : preCol.keySet()) {
				long w = weight + itemWeight;
				if (w > pb.bigW)
					continue;
				b = profit - (key.distance * (pb.rentingRate / (pb.maxSpeed - (pb.nu * w))))
						+ (key.distance * (pb.rentingRate / (pb.maxSpeed - (pb.nu * weight))));

				if (b < 0) { // in this case
					continue; // as beta(k, w) is dominated by beta(k, weight),
				} // skip the rest.

				b += preCol.get(weight).benefit;

				if (!curCol.containsKey(w) || b > curCol.get(w).benefit) {
					curCol.put(w, composeValueWithNewItem(b, preCol.get(weight), item));
				}
			}

			TreeSet<Long> treeSet = new TreeSet<Long>(curCol.keySet());
			b = - Double.MAX_VALUE;
			for (long kw : treeSet) {
				if (curCol.get(kw).benefit <= b) {
					curCol.remove(kw);
				} else {
					b = curCol.get(kw).benefit;
				}
			}

			preCol = curCol;
		}

		return preCol;
	}


	private void packingDP(Map<Long, TtpSolution> preTour, final int distance, int preStop, int curStop,
			Map<Long, TtpSolution> curTour, Map<DpKey, Map<Long,DpValue>> results) {
		if (preTour == null || preTour.size() == 0){
			return;
		}
		
		
//		System.out.println("preTour:" + preTour);
		
		for (long carriedWeight : preTour.keySet()) {
			TtpSolution preValue = preTour.get(carriedWeight);
			DpKey dpKey = new DpKey(preStop, carriedWeight, distance);
			
//			System.out.println(dpKey);
			
//			Map<Long, DpValue> preCol = packItemsInCity(dpKey);
			
			Map<Long, DpValue> preCol = results.get(dpKey);
			
			if (preCol == null){
				preCol = packItemsInCity(dpKey);
				results.put(dpKey, preCol);
			}
//			else{
//				System.out.println("hit!");
//			}
			
//			System.out.println(preCol);
			
			for (Long weight : preCol.keySet()) {
				if (!curTour.containsKey(weight) || preValue.beta + preCol.get(weight).benefit > curTour.get(weight).beta) {
					DpValue dpv = preCol.get(weight);
					curTour.put(weight, composeValueWithNewStop(preValue, dpv, curStop));
				}
			}
		}
		
		TreeSet<Long> treeSet = new TreeSet<Long>(curTour.keySet());
		double b = - Double.MAX_VALUE;
		for (long kw : treeSet) {
			if (curTour.get(kw).beta <= b) {
				curTour.remove(kw);
			} else {
				b = curTour.get(kw).beta;
			}
		}
	}

	private boolean possible(Key key, Map<Key, Map<Long, TtpSolution>> tour, int curStop) {
		Map<Long, TtpSolution> ret = tour.get(key);
		if (ret == null || ret.size() == 0)
			return false;
		// System.out.println(key.stops.toString(2));
		// System.out.println(ret);

		BigInteger unvisited = key.stops.not().setBit(curStop - cityIdxStart);

		long tp = 0;
		for (int i = 1; i <= pb.n; i++) {
			if (unvisited.testBit(i - cityIdxStart)) {
				City city = pb.getCity(i);
				for (int j = 0; j < city.noOfItemsInCity(); j++) {
					tp += pb.getItem(city.getItem(j)).profit;
				}
			}
		}

		TtpSolution best = best(ret);
		// System.out.println(best);
		// System.out.println(tp);
		// System.out.println(curStop);

		double p = best.beta + tp - this.shortestDistances.get(curStop) * pb.rentingRate / pb.maxSpeed;

		// System.out.println(p);
		if (p > this.bestInitially.beta) {
			return true;
		} else {
			return false;
		}
	}

	public Set<Integer> getCitySet(BigInteger citiesBit, int noOfCities, int cityIdxStart) {
		Set<Integer> ret = new HashSet<>();
		for (int i = 0; i < noOfCities; i++) { // S ⊆ {2, ..., n}
			if (citiesBit.testBit(i)) {
				ret.add(i + cityIdxStart); // add city
			}
		}
		return ret;
	}

	public void dynamicProgramming() {
		final long startTime = System.nanoTime();

		// (Destination,Stops) -> (Weight-> (Benefit, PackingPlan, Tour))
		Map<Key, Map<Long, TtpSolution>> tour = new HashMap<>();

		final long zeroWeight = 0;
		for (int k = 2; k <= pb.n; k++) {
			Map<Long, TtpSolution> v = new HashMap<>();
			v.put(zeroWeight, new TtpSolution(-pb.distance(1, k) * cost0, BigInteger.ZERO, new int[] { 1, k }));
			tour.put(new Key(k, BigInteger.ONE.setBit(k - cityIdxStart)), v);
		}

		// System.out.println(tour);
		 Map<DpKey, Map<Long,DpValue>> results = new HashMap<>(10000);

		for (int s = 3; s <= pb.n; s++) {
			Map<Key, Map<Long, TtpSolution>> curtour = new HashMap<>();

			for (Key key : tour.keySet()) {
				Set<Integer> sSet = getCitySet(key.stops.not(), pb.getNoOfNodes(), cityIdxStart);
				for (int j : sSet) {
					Set<Integer> throughSet = getCitySet(key.stops.clearBit(thefirstCity - cityIdxStart),
							pb.getNoOfNodes(), cityIdxStart);
					Key curKey = new Key(j, key.stops.setBit(j - cityIdxStart));
					if (!curtour.containsKey(curKey)) {
						curtour.put(curKey, new HashMap<>());
					}
					Map<Long, TtpSolution> itemsmap = curtour.get(curKey);
					for (int i : throughSet) {
						Key prekey = new Key(i, key.stops);
						if (possible(prekey, tour, i)) {
							packingDP(tour.get(prekey), pb.distance(i, j), i, j, itemsmap, results);
						}
					}
				}
			}
			// System.out.println(curtour);
			tour = curtour;
		}

		BigInteger all = CitySet.allSet(pb.n);
		Map<Long, TtpSolution> curTour = new HashMap<>();
		for (int i = 2; i <= pb.n; i++) {
			Key key = new Key(i, all);
			packingDP(tour.get(key), pb.distance(i, thefirstCity), i, thefirstCity, curTour, results);
		}

		double max = -Double.MAX_VALUE;
		TtpSolution v = null;

		for (Long w : curTour.keySet()) {
			TtpSolution tv = curTour.get(w);
			double d = tv.beta;
			if (d > max) {
				max = d;
				v = tv;
			}
		}

		this.value = v;

		elapsedTime = new BigDecimal(TimeUnit.MILLISECONDS.convert(System.nanoTime() - startTime, TimeUnit.NANOSECONDS))
				.divide(new BigDecimal(1000));

	}

	private TtpSolution composeValueWithNewStop(TtpSolution old, DpValue dpv, int lastStop) {

		int[] stops = new int[old.getTourLength() + 1];
		old.copyTourTo(stops);
		stops[stops.length - 1] = lastStop;
		return new TtpSolution(old.beta + dpv.benefit, old.packingPlan.or(dpv.packingPlan) , stops);
	}

	private DpValue composeValueWithNewItem(double beta, DpValue old, Item pickedItem) {
		BigInteger packingPlan = old.packingPlan.setBit(pickedItem.index - this.itemIdxStart);
		return new DpValue(beta, packingPlan);
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
		return this.value.getTour();
	}

}
