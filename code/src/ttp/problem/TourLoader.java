package ttp.problem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;
import java.util.stream.Collectors;

import common.tools.Util;
import ttp.tour.TourType;

public class TourLoader {
	
	public static int[] load(String tourFileName, TourType type){
		switch (type) {
		case lkh:
			return Util.align(loadLkhTour(tourFileName));
		case lkh2:
			return Util.align(loadLkh2Tour(tourFileName));
		case con:
			return Util.align(loadConcordeTour(tourFileName));
		case aco:
			return Util.align(loadAcoTour(tourFileName));
		case inv:
			return Util.align(loadInvTour(tourFileName));
		default:
			throw new RuntimeException("The type of tour cannot be handled.");
	}
	}
	
	public static Tour load(String tourFileName, Ttp problem, TourType type){
		int[] stops = null;
		switch (type) {
			case lkh:
				return loadLkhTour(tourFileName, problem);
			case lkh2:
				stops = loadLkh2Tour(tourFileName);
				break;
			case con:
				stops = loadConcordeTour(tourFileName);
				break;
			case aco:
				stops = loadAcoTour(tourFileName);
				break;
			case inv:
				stops = loadInvTour(tourFileName);
				break;
			default:
				throw new RuntimeException("The type of tour cannot be handled.");
		}
		
		
	    if (problem.getNoOfNodes() != stops.length){
	    	throw new RuntimeException("The number of stops in tour doesn't agree with the one in problem file");
	    }

		Tour tour = new Tour(problem);
		tour.update(Util.align(stops));
		
		return tour;
	}
	

	/*
	 * Load tour generated by LKH.
	 * 
	 * Please note that in LKH files, the city index starts from zero (0).
	 * 
	 */
	public static Tour loadLkhTour(String tourFileName, Ttp problem) {
		
		int[] stops = new int[problem.getNoOfNodes()];
		long[] adjacentDistances = new long[problem.getNoOfNodes()];
		long[] traveledDistances = new long[problem.getNoOfNodes()];
		
		try (Scanner dataFile = new Scanner(new File(tourFileName))) {
			dataFile.useLocale(Locale.ENGLISH);
			String input = null;
			dataFile.nextLine(); // skip the first line

			int p = 0;
			long traveledDistance = 0;
			while (dataFile.hasNextLine()) {
				
				input = dataFile.nextLine().trim();

				String[] strs = input.split("\\s+");
				int cityIdx = Integer.parseInt(strs[0]) + 1; //Add one in order to match the indices in TTP instance file.
				long distance = Long.parseLong(strs[2]);
				
				stops[p] = cityIdx;
				adjacentDistances[p] = distance;
				traveledDistances[p] = traveledDistance;
				
				traveledDistance += distance;
				p++;
			}
		    if (p != stops.length){
		    	throw new RuntimeException("The number of stops in tour doesn't agree with the one in problem file");
		    }
		}catch (FileNotFoundException e){
			throw new RuntimeException(e);
		}
		
		Tour tour = new Tour(problem);
		tour.update(stops, adjacentDistances, traveledDistances);
		
		return tour;
	}

	/*
	 * Load tour generated by LKH.
	 * 
	 * Please note that in LKH files, the city index starts from zero (0).
	 * 
	 */
	public static int[] loadLkhTour(String tourFileName) {
		
		ArrayList<Integer> stops = new ArrayList<>();
		
		try (Scanner dataFile = new Scanner(new File(tourFileName))) {
			dataFile.useLocale(Locale.ENGLISH);
			String input = null;
			dataFile.nextLine(); // skip the first line

			while (dataFile.hasNextLine()) {
				
				input = dataFile.nextLine().trim();

				String[] strs = input.split("\\s+");
				int cityIdx = Integer.parseInt(strs[0]) + 1; //Add one in order to match the indices in TTP instance file.
				
				stops.add(cityIdx);
			}

		}catch (FileNotFoundException e){
			throw new RuntimeException(e);
		}
	
		return Util.toArray(stops);
	}
	
	
	/*
	 * Load tour generated by Concorde.
	 * 
	 * Please note that in Concorde files, the city index starts from zero (0).
	 * 
	 */
	public static int[] loadConcordeTour(String tourFileName) {
		
	    String[] stopStr = null;
		try {
			stopStr = Files.lines(Paths.get(tourFileName)).collect(
			        Collectors.joining(" ")).split("\\s+");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		int[] stops = new int[stopStr.length - 1];	    
	    for (int i=0;i<stops.length;i++){
	    	stops[i] = Integer.parseInt(stopStr[i+1]) + 1;
	    }
	    
		return stops;
	}
	
	/*
	 * Load tour generated by LKH 2.0.x.
	 * 
	 * Please note that in LKH 2 tour files, the city index starts from one (1).
	 * 
	 */
	public static int[] loadLkh2Tour(final String tourFileName) {
		
		ArrayList<Integer> stops = new ArrayList<>();
		
		try (Scanner dataFile = new Scanner(new File(tourFileName))) {
			dataFile.useLocale(Locale.ENGLISH);
			String input = dataFile.nextLine().trim(); // read the first line
			while (dataFile.hasNextLine() && !input.equals("TOUR_SECTION")) {
				input = dataFile.nextLine().trim();
			}
			
			while (dataFile.hasNextLine()) {
				input = dataFile.nextLine().trim();
				int cityIdx = Integer.parseInt(input);
				if (cityIdx == -1){
					break;
				}
				stops.add(cityIdx);
			}

		}catch (FileNotFoundException e){
			throw new RuntimeException(e);
		}
		
		return Util.toArray(stops);
	}
	
	/*
	 * Load tour generated by ACO.
	 * 
	 * Please note that in ACO tour files, the city index starts from zero (0).
	 * 
	 */
	public static int[] loadAcoTour(String tourFileName) {
		int[] stops = null;
		
		try (Scanner dataFile = new Scanner(new File(tourFileName))) {
			dataFile.useLocale(Locale.ENGLISH);
			dataFile.nextLine(); // skip the first line
			String input = dataFile.nextLine().trim();// read the second line
			String[] stopStr = input.split("\\s+");
			
			stops = new int[stopStr.length];
		    for (int i=0;i<stops.length;i++){
		    	stops[i] = Integer.parseInt(stopStr[i]) + 1;
		    }
		}catch (FileNotFoundException e){
			throw new RuntimeException(e);
		}
	    
		return stops;
	}
	
	/*
	 * Load tour generated by Inver Over.
	 * 
	 * Please note that in Inver Over tour files, the city index starts from one (1).
	 * 
	 */
	
	public static int[] loadInvTour(String tourFileName){
		int[] stops = null;
		try (Scanner dataFile = new Scanner(new File(tourFileName))) {
			dataFile.useLocale(Locale.ENGLISH);
			dataFile.nextLine(); // skip the first line
			String input = dataFile.nextLine().trim();// read the second line
			String[] stopStr = input.split("\\s+");
			
			stops = new int[stopStr.length];
		    for (int i=0;i<stops.length;i++){
		    	stops[i] = Integer.parseInt(stopStr[i]);
		    }
		}catch (FileNotFoundException e){
			throw new RuntimeException(e);
		}
		return stops;
	}
	
}
