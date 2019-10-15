package ttp.problem;

import java.util.Arrays;

public final class Tour {
	public static class Essence {
		private final int[] stops;
		public final int length;
		public final int hashcode;
		
		public Essence(int[] stops) {
			this.stops = stops;
			this.length = stops.length;
			this.hashcode = Arrays.hashCode(stops);
		}
		
		public int[] getStops() {
			return stops.clone();
		}
		
		public int getStop(int i) {
			return stops[i];
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
			Essence other = (Essence) obj;
			if (hashcode != other.hashcode)
				return false;
			if (!Arrays.equals(stops, other.stops))
				return false;
			return true;
		}

		@Override
		public String toString() {
			return "Essence [" + hashcode + "]";
		}
		
		
	}
	
	
	public final Ttp problem;
	public final int length;
	
	public Essence essence;
	public final long[] adjacentDistances;
	public final long[] traveledDistances;
	public final int[] stopsOfItems;
	public final int[] itemsByTour;
	
	public Tour(Ttp problem){
		this(problem, problem.getNoOfNodes());
	}
	
	public Tour(Ttp problem, int length){
		this.problem = problem;
		if (length > problem.getNoOfNodes()){
			this.length = problem.getNoOfNodes();
		}else{
			this.length = length;
		}
		essence = null;
		adjacentDistances = new long[this.length];
		traveledDistances = new long[this.length];
		stopsOfItems = new int[problem.getNoOfItems()];
		itemsByTour = new int[problem.getNoOfItems()];
		
		
	}
	
	public Tour(Tour old){
		this.problem = old.problem;
		this.length = old.length;
		this.essence = old.essence;
		this.adjacentDistances = old.adjacentDistances.clone();
		this.traveledDistances = old.traveledDistances.clone();
		this.stopsOfItems = old.stopsOfItems.clone();
		this.itemsByTour = old.itemsByTour.clone();
	}
	
	public Tour(Ttp ttp, int[] stops) {
		this(ttp);
		this.update(stops);
	}

	public Ttp getProblem(){
		return problem;
	}
	
	public void update(int[] stops){
		if (stops.length != problem.getNoOfNodes()){
			throw new RuntimeException(
					"Error: inconsistent between no. of cities and stops");
		}
		
		this.essence = new Essence(stops.clone());
		updateAdjacentDistances();
		updateTraveledDistances();
		updateItemsByTour();
	}
	
	
	public void update(int[] stops, long[] adjacentDistances, long[] traveledDistances){
		this.essence = new Essence(stops.clone());
		System.arraycopy(adjacentDistances, 0, this.adjacentDistances, 0, adjacentDistances.length);
		System.arraycopy(traveledDistances, 0, this.traveledDistances, 0, traveledDistances.length);
		updateItemsByTour(this.itemsByTour, this.stopsOfItems, this.problem, stops);
	}
	
	public long totalDistance(){
		long total = 0;
		for (int i=0;i<this.adjacentDistances.length;i++){
			total += adjacentDistances[i];
		}
		return total;
	}
	
	
	public void updateAdjacentDistances(){
		updateAdjacentDistances(this.adjacentDistances, this.problem, this.essence.getStops());
	}
	
	
	public static void updateAdjacentDistances(final long[] adjacentDistances, final Ttp ttp, final int[] stops){
		for (int i=1;i < stops.length;i++){
			adjacentDistances[i-1] = ttp.distance(stops[i-1], stops[i]);
		}
		adjacentDistances[stops.length-1] = ttp.distance(stops[stops.length-1], stops[0]);
	}
	
	public void updateItemsByTour(){
		updateItemsByTour(this.itemsByTour, this.stopsOfItems, this.problem, this.essence.getStops());
	}
	
	public static void updateItemsByTour(final int[] itemsByTour, final int[] stopsOfItems, final Ttp ttp, final int[] stops){
		
		int mptr = 0;
		for (int c=0;c< stops.length;c++){
			//sort items according to the tour
			for (int i = 0; i < ttp.getCity(stops[c]).noOfItemsInCity(); i++) {
				itemsByTour[mptr] = ttp.getCity(stops[c]).getItem(i);
				stopsOfItems[mptr] = c;
				mptr++;
			}
		}
		
	}
	
	
	public void updateTraveledDistances(){
		updateTraveledDistances(this.traveledDistances, this.adjacentDistances);
	}
	
	public static void updateTraveledDistances(final long[] traveledDistances, final long[] adjacentDistances){
		
		long traveledDistance = 0;
		for (int c=0;c< adjacentDistances.length;c++){
			traveledDistances[c] = traveledDistance;
			traveledDistance += adjacentDistances[c];
		}
		
	}

	@Override
	public String toString() {
		return "Tour [stops=" + Arrays.toString(this.essence.getStops()) + "\nadjacentDistances=" + Arrays.toString(adjacentDistances)
				+ "\ntraveledDistances=" + Arrays.toString(traveledDistances) + "]";
	}

	
}
