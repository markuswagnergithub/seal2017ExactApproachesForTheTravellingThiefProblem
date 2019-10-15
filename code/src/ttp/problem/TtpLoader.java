package ttp.problem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Scanner;

import com.google.gson.Gson;

import common.tools.Util;

public class TtpLoader {

	public static final String PROBLEM_NAME = "PROBLEM NAME:";
	public static final String DIMENSION = "DIMENSION:";
	public static final String NUMBER_OF_ITEMS = "NUMBER OF ITEMS:";
	public static final String RENTING_RATIO = "RENTING RATIO:";
	public static final String MIN_SPEED = "MIN SPEED:";
	public static final String MAX_SPEED = "MAX SPEED:";
	public static final String CAPACITY_OF_KNAPSACK = "CAPACITY OF KNAPSACK:";
	public static final String DISTANCE_RANGE = "DISTANCE RANGE:";
	public static final String PROFIT_RANGE = "PROFIT RANGE:";
	public static final String WEIGHT_RANGE = "WEIGHT RANGE:";
	public static final String DISTRIBUTION = "DISTRIBUTION:";
	public static final String EDGE_WEIGHT_TYPE = "EDGE_WEIGHT_TYPE:";
	public static final String NODE_COORD_SECTION = "NODE_COORD_SECTION";
	public static final String ITEMS_SECTION = "ITEMS SECTION";

	private static City readNode(String input) {
		String[] ptr = input.split("\\s+");
		if (ptr.length < 3)
			throw new RuntimeException("Error: inconsistent data in the NODE_COORD_SECTION of the file");
		return new City(Integer.parseInt(ptr[0]), (int) Double.parseDouble(ptr[1]), (int) Double.parseDouble(ptr[2]));
	}

	private static Item readItem(String input) {
		String[] ptr = input.split("\\s+");
		if (ptr.length < 3)
			throw new RuntimeException("Error: inconsistent data in the ITEMS SECTION of the file");
		return new Item(Integer.parseInt(ptr[0]), Long.parseLong(ptr[1]), Long.parseLong(ptr[2]),
				Integer.parseInt(ptr[3]));
	}

	public static Ttp readProblemHead(Scanner dataFile, String problemFileName) {
		String problemName = null;
		int noOfNodes = 0;
		int noOfItems = 0;
		BigDecimal rentingRate = null;
		BigDecimal minSpeed = null;
		BigDecimal maxSpeed = null;
		long capacity = 0;
		Ttp.WeightType weightType = null;

		String input = dataFile.nextLine();
		while (!input.contains(NODE_COORD_SECTION)) {

			if (input.contains(PROBLEM_NAME))
				problemName = input.replace(PROBLEM_NAME, "").replace("-TTP", "").trim();
			else if (input.contains(DIMENSION))
				noOfNodes = Integer.parseInt(input.replace(DIMENSION, "").trim());
			else if (input.contains(NUMBER_OF_ITEMS))
				noOfItems = Integer.parseInt(input.replace(NUMBER_OF_ITEMS, "").trim());
			else if (input.contains(RENTING_RATIO)) {
				String[] ratio = input.replace(RENTING_RATIO, "").trim().split("\\.\\.");
				rentingRate = new BigDecimal(ratio[0]);
			} else if (input.contains(MIN_SPEED))
				minSpeed = new BigDecimal(input.replace(MIN_SPEED, "").trim());
			else if (input.contains(MAX_SPEED))
				maxSpeed = new BigDecimal(input.replace(MAX_SPEED, "").trim());
			else if (input.contains(CAPACITY_OF_KNAPSACK))
				capacity = Long.parseLong(input.replace(CAPACITY_OF_KNAPSACK, "").trim());
			else if (input.contains(EDGE_WEIGHT_TYPE))
				weightType = Ttp.WeightType.valueOf(input.replace(EDGE_WEIGHT_TYPE, "").trim());

			input = dataFile.nextLine();
		}

		if (problemName == null || noOfNodes <= 0 || noOfItems <= 0 || rentingRate == null || minSpeed == null
				|| maxSpeed == null || capacity <= 0 || weightType == null)
			throw new RuntimeException("Error: inconsistent data in the Head SECTION of the file {problemName:"
					+ problemName + " noOfNodes:" + noOfNodes + " noOfItems:" + noOfItems + " rentingRate:"
					+ rentingRate + " minSpeed:" + minSpeed + " maxSpeed:" + maxSpeed + " capacity:" + capacity
					+ " weightType:" + weightType + "}");

		return new Ttp(problemName, problemFileName, noOfNodes, noOfItems, rentingRate.doubleValue(), minSpeed.doubleValue(), maxSpeed.doubleValue(), capacity,
				weightType, null, null);

	}

	public static Tour load(String problemFileName, String tourFileName) {
		return loadProblemAndLkhTour(problemFileName, tourFileName);
	}

	public static Ttp load(String problemFileName) {
		if (problemFileName == null)
			return null;
		return loadProblem(problemFileName);

	}

	public static Tour loadProblemAndLkhTour(String problemFileName, String tourFileName) {
		Ttp problem = loadProblem(problemFileName);
		return TourLoader.loadLkhTour(tourFileName, problem);
	}

	/*
	 * Load TTP instance from a file.
	 * 
	 * Please note that the indices of cities and items start from one (1).
	 * 
	 */
	public static Ttp loadProblem(String problemFileName) {

		Ttp pb = null;
		try (Scanner dataFile = new Scanner(new File(problemFileName))) {

			dataFile.useLocale(Locale.ENGLISH);

			pb = readProblemHead(dataFile, problemFileName);

			// reading the NODE_COORD_SECTION part
			String input = dataFile.nextLine();
			HashMap<Integer, City> cities = new HashMap<>();
			while (!input.contains(ITEMS_SECTION)) {
				City city = readNode(input.trim());
				cities.put(city.index, city);
				input = dataFile.nextLine();
			}

			// reading the ITEMS SECTION part
			HashMap<Integer, Item> items = new HashMap<>();
			HashMap<Integer, LinkedList<Integer>> itemsInCity = new HashMap<>();
			while (dataFile.hasNextLine()) {
				input = dataFile.nextLine();
				Item item = readItem(input.trim());
				items.put(item.index, item);

				if (!itemsInCity.containsKey(item.cityIdx)) {
					itemsInCity.put(item.cityIdx, new LinkedList<>());
				}
				itemsInCity.get(item.cityIdx).add(item.index);
			}

			if (pb == null || pb.getNoOfNodes() != cities.size() || pb.getNoOfItems() != items.size()) {
				throw new RuntimeException("Error: inconsistent data in the node or item section of the file");
			}

			for (int cityIdx : cities.keySet()) {
				City city = new City(cities.get(cityIdx), Util.toArray(itemsInCity.get(cityIdx)));
				cities.put(cityIdx, city);
			}

			pb = new Ttp(pb.name, pb.path, pb.n, pb.m, pb.rentingRate, pb.minSpeed, pb.maxSpeed, pb.capacity,
					pb.edgeWeightType, cities, items);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}

		return pb;
	}

	public Ttp loadFromJsonFile(String fileName) throws FileNotFoundException {
		Gson gson = new Gson();
		Reader json = new FileReader(fileName);
		return gson.fromJson(json, Ttp.class);
	}

}
