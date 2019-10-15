package ttp.problem;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;


public class TtpSaver {
	public static void pushHeader(Ttp problem, StringBuilder sb){
		sb.append(TtpLoader.PROBLEM_NAME);
		sb.append(" ");
		sb.append(problem.name);
		sb.append(System.lineSeparator());
		
		sb.append("KNAPSACK DATA TYPE:");
		sb.append(System.lineSeparator());
		
		sb.append(TtpLoader.DIMENSION);
		sb.append(" ");
		sb.append(problem.getNoOfNodes());
		sb.append(System.lineSeparator());
		
		sb.append(TtpLoader.NUMBER_OF_ITEMS);
		sb.append(" ");
		sb.append(problem.getNoOfItems());
		sb.append(System.lineSeparator());
		
		sb.append(TtpLoader.CAPACITY_OF_KNAPSACK);
		sb.append(" ");
		sb.append(problem.capacity);
		sb.append(System.lineSeparator());
		
		sb.append(TtpLoader.MIN_SPEED);
		sb.append(" ");
		sb.append(problem.minSpeed);
		sb.append(System.lineSeparator());
		
		sb.append(TtpLoader.MAX_SPEED);
		sb.append(" ");
		sb.append(problem.maxSpeed);
		sb.append(System.lineSeparator());
		
		sb.append(TtpLoader.RENTING_RATIO);
		sb.append(" ");
		sb.append(problem.rentingRate);
		sb.append(System.lineSeparator());
		
		sb.append("EDGE_WEIGHT_TYPE: CEIL_2D");
		sb.append(System.lineSeparator());
		
	}
	
	public static void pushCities(Ttp problem, StringBuilder sb){
		
		
		sb.append(TtpLoader.NODE_COORD_SECTION);
		sb.append("	(INDEX, X, Y): ");
		sb.append(System.lineSeparator());
		
		for (int i=1;i<=problem.getNoOfNodes();i++){
			City city = problem.getCity(i);
			sb.append(city.index);
			sb.append("\t");
			sb.append(Math.round(city.x));
			sb.append("\t");
			sb.append(Math.round(city.y));
			sb.append(System.lineSeparator());
		}
	}
	
	public static void pushItems(Ttp problem, StringBuilder sb){
		sb.append("ITEMS SECTION	(INDEX, PROFIT, WEIGHT, ASSIGNED NODE NUMBER): ");
		sb.append(System.lineSeparator());
		
		for (int i=1;i<=problem.getNoOfItems();i++){
			Item item = problem.getItem(i);
			sb.append(item.index);
			sb.append("\t");
			sb.append(item.profit);
			sb.append("\t");
			sb.append(item.weight);
			sb.append("\t");
			sb.append(item.cityIdx);
			sb.append(System.lineSeparator());
		}
		
	}
	
	public static String convert2TTPString(Ttp problem){
		StringBuilder sb = new StringBuilder();
		
		pushHeader(problem, sb);
		pushCities(problem, sb);
		pushItems(problem, sb);
		
		return sb.toString();
	}
	
	public static void write(Ttp problem, Path filePath){
		try {
			Files.write(filePath, convert2TTPString(problem).getBytes());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
