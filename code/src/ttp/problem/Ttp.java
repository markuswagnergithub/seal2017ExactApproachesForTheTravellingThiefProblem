package ttp.problem;

import java.util.BitSet;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import common.tools.Util;


public final class Ttp {
	public enum KnapsackType {Uncorrelated, UncorrelatedWithSimilarWeights, BoundedStronglyCorrelated};
	public enum WeightType {CEIL_2D, EUC_2D};
	
	public static class BenefitAndWeight{
		public final double benefit;
		public final long weight;
		
		public BenefitAndWeight(double benefit, long weight) {
			this.benefit = benefit;
			this.weight = weight;
		}

		@Override
		public String toString() {
			return "BenefitAndWeight [benefit=" + benefit + ", weight=" + weight + "]";
		}
		
		
	}
	
	public final String name;
	public final String path;
	public final WeightType edgeWeightType;
	public final int n; //No. of Nodes
	public final int m; //No. of Items
	public final long capacity;
	public final long bigW;
	
	public final double maxSpeed;
	public final double minSpeed;
	public final double rentingRate;
	public final double nu;
	
	public final Map<Integer, City> cities;
	public final Map<Integer, Item> items;
	
	private final int hashcode;
	
	public int getNoOfNodes(){
		return n;
	}
	
	public int getNoOfItems() {
		return m;
	}

	
	public City getCity(int cityIdx){
		return cities.get(cityIdx);
	}
	
	public Item getItem(int index){
		return items.get(index);
	}
	
	
	@Override
	public String toString() {
		return toJsonString();
	}

	public String toJsonString() {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		return gson.toJson(this);
	}
	
	public static long totalWeight(Map<Integer, Item> items) {
		long total = 0;
		for (Item item:items.values()){
			total += item.weight;
		}
		return total;
	}
	
	public long totalWeight(){
		return totalWeight(this.items);
	}
	
	public double getSpeedCoef(){
		return nu;
	}
	
	public int distance(int cityIdxA, int cityIdxB){
		return distance(this.cities.get(cityIdxA), this.cities.get(cityIdxB));
	}
	
	public int distance(City a, City b){
		double distance = Math.sqrt(Math.pow(a.x - b.x, 2) + Math.pow(a.y - b.y, 2));
		if (this.edgeWeightType == WeightType.CEIL_2D){
			return (int)Math.ceil(distance);
		}else if (this.edgeWeightType == WeightType.EUC_2D){
			return (int)Math.rint(distance);
		}else{
			throw new RuntimeException("Unsupported type of edge weight.");
		}
	}
	
	

	
	public Ttp(String problemName, String path, int noOfNodes, int noOfItems, double rentingRate, 
			double minSpeed, double maxSpeed, long capacity, WeightType weightType, 
			Map<Integer, City> cities, Map<Integer, Item> items){		
		this(problemName, path, noOfNodes, noOfItems, rentingRate, minSpeed, maxSpeed, capacity, bigW(capacity, items),
				(maxSpeed - minSpeed)/capacity, weightType, 
				cities == null? null : Collections.unmodifiableMap(new HashMap<Integer, City>(cities)), 
				items == null? null : Collections.unmodifiableMap(new HashMap<Integer, Item>(items)));
	}

	
	
	private Ttp(String problemName, String path, int noOfNodes, int noOfItems, double rentingRate, 
			double minSpeed, double maxSpeed, long capacity, long bigW, double nu, WeightType weightType, 
			Map<Integer, City> cities, Map<Integer, Item> items){
		this.name = problemName;
		this.path = path;
		this.n = noOfNodes;
		this.m = noOfItems;
		this.rentingRate = rentingRate;
		this.minSpeed = minSpeed;
		this.maxSpeed = maxSpeed;
		this.capacity = capacity;
		this.bigW = bigW;
		this.edgeWeightType = weightType;
		this.nu = nu;
		this.cities = cities;
		this.items = items;
		

		hashcode = Objects.hash(this.name, this.path, this.n, this.m, this.rentingRate, this.minSpeed, this.maxSpeed,
				this.capacity, this.edgeWeightType, this.nu, cities, items);
	}

	public static long bigW(long capacity, Map<Integer, Item> items) {
		if (items != null) {
			return Math.min(capacity, totalWeight(items));
		}else {
			return capacity;
		}
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
		Ttp other = (Ttp) obj;
		if (capacity != other.capacity)
			return false;
		if (cities == null) {
			if (other.cities != null)
				return false;
		} else if (!cities.equals(other.cities))
			return false;
		if (edgeWeightType != other.edgeWeightType)
			return false;
		if (items == null) {
			if (other.items != null)
				return false;
		} else if (!items.equals(other.items))
			return false;
		if (m != other.m)
			return false;
		if (Double.doubleToLongBits(maxSpeed) != Double.doubleToLongBits(other.maxSpeed))
			return false;
		if (Double.doubleToLongBits(minSpeed) != Double.doubleToLongBits(other.minSpeed))
			return false;
		if (n != other.n)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (Double.doubleToLongBits(nu) != Double.doubleToLongBits(other.nu))
			return false;
		if (path == null) {
			if (other.path != null)
				return false;
		} else if (!path.equals(other.path))
			return false;
		if (Double.doubleToLongBits(rentingRate) != Double.doubleToLongBits(other.rentingRate))
			return false;
		return true;
	}
	
	public double benefit(List<Integer> stops, BitSet packingPlan) {
		return benefit(Util.toArray(stops), packingPlan);
	}
	
	public double benefit(int[] stops, BitSet packingPlan) {
		final long[] adjacentDistances = new long[this.n];
		for (int i = 1; i < stops.length; i++) {
			adjacentDistances[i - 1] = this.distance(stops[i - 1], stops[i]);
		}
		adjacentDistances[stops.length - 1] = this.distance(stops[stops.length - 1], stops[0]);

		double benefit = 0;
		long preloadedWeight = 0;

		for (int i = 0; i < stops.length; i++) {
			BenefitAndWeight bnw = benefitAtStop(stops[i], preloadedWeight, adjacentDistances[i], packingPlan);
			benefit += bnw.benefit;
			preloadedWeight = bnw.weight;
		}

		return benefit;
	}
	
	public BenefitAndWeight benefitAtStop(final int currentStop, final long preloadedWeight, final long distanceToNextStop, final BitSet packingPlan) {
		long currentProfit = 0;
		long currentWeight = preloadedWeight;
		for (int i = 0; i < this.getCity(currentStop).noOfItemsInCity(); i++) {
			int itemIdx = this.getCity(currentStop).getItem(i);
			if (packingPlan.get(itemIdx)) {
				currentProfit += this.getItem(itemIdx).profit;
				currentWeight += this.getItem(itemIdx).weight;
			}
		}
		// Formula 1 in the paper
		return new BenefitAndWeight(benefit(currentProfit, currentWeight, distanceToNextStop), currentWeight);
	}
	
	public double benefit(long profit, long weight, long distance) {
		return profit - this.rentingRate * distance / (this.maxSpeed - nu * weight);
	}
	
	public long totalProfit(BitSet packingPlan) {
		long total = 0;
		for (int idx:packingPlan.stream().toArray()) {
			total += this.getItem(idx).profit;
		}
		return total;
	}

	public long totalWeight(BitSet packingPlan) {
		long total = 0;
		for (int idx:packingPlan.stream().toArray()) {
			total += this.getItem(idx).weight;
		}
		return total;
	}
	
	public long distanceToNearestCity(int cityIdx) {
		long shortest = Long.MAX_VALUE;
		City curCity = cities.get(cityIdx);
		for (City neighbor : cities.values()) {
			if (curCity.equals(neighbor)) {
				continue;
			}
			long d = this.distance(curCity, neighbor);
//			System.out.println("distance:" + d + " city:" + neighbor);
			if (d < shortest) {
				shortest = d;
			}
		}
		return shortest;
	}
	
}
