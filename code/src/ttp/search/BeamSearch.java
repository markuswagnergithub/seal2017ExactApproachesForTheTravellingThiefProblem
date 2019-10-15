package ttp.search;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

import common.tools.Util;
import ttp.dp.CitySet;
import ttp.dp.Key;
import ttp.dp.TourBnB;
import ttp.dp.TtpSolution;
import ttp.problem.City;
import ttp.problem.Ttp;

public class BeamSearch {

	private BigDecimal elapsedTime;

	private final Ttp pb;
	private final int beamWidth;

	private final double cost0;

	// item index starts from 1
	private final int itemIdxStart = 1;
	// city index starts from 1, i.e. {1, 2, ..., n}
	private final int cityIdxStart = 1;
	// the index of the first city.
	private final int thefirstCity = 1;
	
	private final Map<Integer, Integer> shortestDistances;

	private TtpSolution value;

	public BeamSearch(Ttp problem, int beamWidth) {
		this.pb = problem;
		this.beamWidth = beamWidth;

		this.cost0 = pb.rentingRate / pb.maxSpeed;
		
		this.shortestDistances = Collections.unmodifiableMap(TourBnB.shortestDistances(pb));

	}

	private void packingDP(Map<Long, TtpSolution> preTour, double distance, int preStop, int curStop,
			Map<Long, TtpSolution> curTour) {
		if (preTour == null || preTour.size() <= 0)
			return;

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

		for (Long weight : preCol.keySet()) {
			if (!curTour.containsKey(weight) || preCol.get(weight).beta > curTour.get(weight).beta) {
				curTour.put(weight, preCol.get(weight));
			}
		}
	}
	
	private double potentialValue(TtpSolution v){
		
		long tp = 0;
		City curCity = pb.getCity(v.getTour(v.getTourLength() - 1));
		for (int i = 0; i < curCity.noOfItemsInCity(); i++) {
			tp += pb.getItem(curCity.getItem(i)).profit;
		}
		
		Set<Integer> visitedCities = new HashSet<Integer>(Util.asList(v.getTour()));
		for (int i = 1; i <= pb.n; i++) {
			if (!visitedCities.contains(i)){
				City c = pb.getCity(i);
				for (int j=0;j<c.noOfItemsInCity();j++){
					tp += pb.getItem(c.getItem(j)).profit;
				}
			}
		}
		
		
		return tp - this.shortestDistances.get(curCity.index)*pb.rentingRate/pb.maxSpeed;
	}

	private Map<Key, Map<Long, TtpSolution>> filter(Map<Key, Map<Long, TtpSolution>> column) {
//		System.out.println(column);
		Map<Double, SearchKey> tempMap = new HashMap<>();
		for (Key key:column.keySet()){
			for (long weight:column.get(key).keySet()){
				TtpSolution v = column.get(key).get(weight);
				tempMap.put(column.get(key).get(weight).beta + potentialValue(v), new SearchKey(key, weight));
			}
		}
		tempMap = new TreeMap<>(tempMap).descendingMap();
		
		
		Map<Key, Map<Long, TtpSolution>> retCol = new HashMap<>();
		
		int count = this.beamWidth;
		for(SearchKey v: tempMap.values()){
			
			if (!retCol.containsKey(v.key)){
				retCol.put(v.key, new HashMap<>());
			}
			
			retCol.get(v.key).put(v.weight, column.get(v.key).get(v.weight));
			
			if (count == 0) break;
			count--;
		}
		
//		System.out.println(retCol);
		return retCol;
	}

	public Set<Integer> getCitySet(BigInteger citiesBit, int noOfCities, int cityIdxStart) {
		Set<Integer> ret = new HashSet<>();
		for (int i = 0; i < noOfCities; i++) { // S is a subset of {2, ..., n}
			if (citiesBit.testBit(i)) {
				ret.add(i + cityIdxStart); // add city
			}
		}
		return ret;
	}

	public void search() {
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
						packingDP(tour.get(prekey), pb.distance(i, j), i, j, itemsmap);
					}
				}
			}

			// System.out.println(curtour);
			tour = filter(curtour);
		}

		BigInteger all = CitySet.allSet(pb.n);
		Map<Long, TtpSolution> curTour = new HashMap<>();
		for (int i = 2; i <= pb.n; i++) {
			Key key = new Key(i, all);
			packingDP(tour.get(key), pb.distance(i, thefirstCity), i, thefirstCity, curTour);
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
		return this.value.getTour();
	}

}
