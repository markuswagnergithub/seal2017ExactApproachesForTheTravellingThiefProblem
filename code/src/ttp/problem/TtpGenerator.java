package ttp.problem;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import common.tools.Util;

public class TtpGenerator {

	private final Ttp.WeightType weightType = Ttp.WeightType.CEIL_2D;
	private final BigDecimal rentingRate;
	private final BigDecimal minSpeed;
	private final BigDecimal maxSpeed;

	private final int noOfCities;
	private final IntRange xRange;
	private final IntRange yRange;
	private final IntRange noOfItemsRange;
	private final int[] noOfItemsFactor;
	private final IntRange weightRange;
	private final IntRange profitRange;
	private final boolean uniformlyDistributed;

	public TtpGenerator(int noOfCities, IntRange xRange, IntRange yRange, int[] noOfItemsFactor, IntRange weightRange,
			IntRange profitRange, BigDecimal rentingRate, BigDecimal minSpeed, BigDecimal maxSpeed) {
		this(noOfCities, xRange, yRange, null, noOfItemsFactor, weightRange, profitRange, rentingRate, minSpeed,
				maxSpeed, true);
	}

	public TtpGenerator(int noOfCities, IntRange xRange, IntRange yRange, IntRange noOfItemsRange, IntRange weightRange,
			IntRange profitRange, BigDecimal rentingRate, BigDecimal minSpeed, BigDecimal maxSpeed) {
		this(noOfCities, xRange, yRange, noOfItemsRange, null, weightRange, profitRange, rentingRate, minSpeed,
				maxSpeed, false);
	}

	public TtpGenerator(int noOfCities, IntRange xRange, IntRange yRange, IntRange noOfItemsRange,
			int[] noOfItemsFactor, IntRange weightRange, IntRange profitRange, BigDecimal rentingRate,
			BigDecimal minSpeed, BigDecimal maxSpeed, boolean uniformlyDistributed) {
		this.rentingRate = rentingRate;
		this.minSpeed = minSpeed;
		this.maxSpeed = maxSpeed;

		this.noOfCities = noOfCities;
		this.xRange = xRange;
		this.yRange = yRange;
		this.noOfItemsRange = noOfItemsRange;
		this.noOfItemsFactor = noOfItemsFactor;
		this.weightRange = weightRange;
		this.profitRange = profitRange;
		this.uniformlyDistributed = uniformlyDistributed;
	}

	private int randomInt(IntRange range) {
		return ThreadLocalRandom.current().nextInt(range.min, range.max + 1);
	}

	private int randomInt(int origin, int bound) {
		return ThreadLocalRandom.current().nextInt(origin, bound + 1);
	}

	private long randomLong(long origin, long bound) {
		return ThreadLocalRandom.current().nextLong(origin, bound + 1);
	}

	public Ttp generateHead() {
		int noOfItems = (this.noOfCities - 1) * this.noOfItemsFactor[0];
		if (!this.uniformlyDistributed) {
			noOfItems = randomInt(noOfItemsRange);
		}

		String problemName = "random_n" + noOfCities + "_m" + noOfItems;
		long capacity = randomLong(noOfItems * weightRange.max / 5, noOfItems * weightRange.max * 3 / 5);

		return new Ttp(problemName, "/tmp/" + problemName + ".ttp", noOfCities, noOfItems, rentingRate.doubleValue(), minSpeed.doubleValue(),
				maxSpeed.doubleValue(), capacity, weightType, null, null);
	}

	public Map<Integer, City> generateCities() {
		Map<Integer, City> cities = new HashMap<>();

		for (int i = 1; i <= this.noOfCities; i++) {
			cities.put(i, new City(i, randomInt(xRange), randomInt(yRange)));
		}

		return cities;
	}

	public Map<Integer, Item> generateItems(int noOfCities, int noOfItems, Ttp.KnapsackType kpType) {
		Map<Integer, Item> items = new HashMap<>();

		int cityIdx = 0;
		Item item = null;
		for (int i = 1; i <= noOfItems; i++) {
			if (this.uniformlyDistributed) {
				cityIdx = (int) ((i - 1) * (noOfCities - 1) / noOfItems) + 2;
			} else {
				cityIdx = this.randomInt(2, noOfCities);
			}

			switch (kpType) {
			case Uncorrelated:
				item = new Item(i, this.randomInt(profitRange), this.randomInt(weightRange), cityIdx);
				break;
			case BoundedStronglyCorrelated:
				int w = this.randomInt(weightRange);
				item = new Item(i, w + 100, w, cityIdx);
				break;
			case UncorrelatedWithSimilarWeights:
				item = new Item(i, this.randomInt(profitRange), this.randomInt(1000, 1010), cityIdx);
				break;
			default:
				break;
			}

			items.put(item.index, item);
		}

		return items;

	}

	public void linkItemsToCity(Map<Integer, City> cities, Map<Integer, Item> items) {

		HashMap<Integer, LinkedList<Integer>> itemsInCity = new HashMap<>();

		for (Item item : items.values()) {
			if (!itemsInCity.containsKey(item.cityIdx)) {
				itemsInCity.put(item.cityIdx, new LinkedList<>());
			}
			itemsInCity.get(item.cityIdx).add(item.index);
		}

		for (int cityIdx : cities.keySet()) {
			City city = new City(cities.get(cityIdx), Util.toArray(itemsInCity.get(cityIdx)));
			cities.put(cityIdx, city);
		}

	}

	public long totalWeight(Collection<Item> items) {
		long totalW = 0;
		for (Item item : items) {
			totalW += item.weight;
		}
		return totalW;
	}

	public List<Ttp> generateAllType(String ttpName, final int[] capacityCategory) {

		Map<Integer, City> citiesOrigin = generateCities();

		List<Ttp> ret = new ArrayList<>();

		for (int factor : this.noOfItemsFactor) {
			int noOfitems = (this.noOfCities - 1) * factor;
			for (Ttp.KnapsackType kpType : Ttp.KnapsackType.values()) {
				Map<Integer, City> cities = new HashMap<>(citiesOrigin);
				Map<Integer, Item> items = generateItems(this.noOfCities, noOfitems, kpType);
				linkItemsToCity(cities, items);
				long totalW = totalWeight(items.values());

				for (int c : capacityCategory) {
					long capacity = c * totalW / 11;
					String problemName = ttpName + "_" + kpType.toString() + "_n" + this.noOfCities + "_m" + noOfitems
							+ "_c" + c;
					ret.add(new Ttp(problemName, "/tmp/" + problemName + ".ttp", this.noOfCities, noOfitems,
							rentingRate.doubleValue(), minSpeed.doubleValue(), maxSpeed.doubleValue(), capacity, weightType, cities, items));
				}
			}
		}

		return ret;
	}

	public Ttp generate() {

		Ttp pb = generateHead();

		Map<Integer, City> cities = generateCities();

		Map<Integer, Item> items = generateItems(pb.n, pb.m, Ttp.KnapsackType.Uncorrelated);

		linkItemsToCity(cities, items);

		return new Ttp(pb.name, pb.path, pb.n, pb.m, pb.rentingRate, pb.minSpeed, pb.maxSpeed, pb.capacity,
				pb.edgeWeightType, cities, items);
	}

}
