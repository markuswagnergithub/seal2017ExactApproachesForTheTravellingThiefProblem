package ttp.moea;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

public class ParentSelection {

	public static List<Individual> randomHalf(
			List<Individual> population) {
		final Random rd = ThreadLocalRandom.current();
		List<Individual> matingPool = new ArrayList<>();

		IntStream.iterate(0, i -> i + 2).limit((population.size() - 1) / 2).forEach((i) -> {

			if (rd.nextBoolean()) {
				matingPool.add(population.get(i));
			} else {
				matingPool.add(population.get(i + 1));
			}

		});
		return matingPool;
	}

	public static List<Individual> betterHalf(
			final List<Individual> population,  final List<Entry<Integer, Double>> sortedRank) {
		
		return composeMatingPool(population, sortedRank.subList(sortedRank.size() / 2, sortedRank.size()));
	}

	public static List<Individual> worseHalf(
			final List<Individual> population, final List<Entry<Integer, Double>> sortedRank) {
		
		return composeMatingPool(population, sortedRank.subList(0, sortedRank.size()/2));
	}

	public static List<Individual> mixedHalf(
			final List<Individual> population, final List<Entry<Integer, Double>> sortedRank) {
		
		
		List<Entry<Integer, Double>> selected = new ArrayList<>(); 
		selected.addAll(sortedRank.subList(0, sortedRank.size()/4));
		selected.addAll(sortedRank.subList(sortedRank.size()*3/4, sortedRank.size()));
		
		return composeMatingPool(population, selected);
	}
	
	private static List<Individual> composeMatingPool(
			List<Individual> population, List<Entry<Integer, Double>> selected) {
		List<Individual> matingPool = new ArrayList<>();
		
		selected.forEach((e) -> {
			matingPool.add(population.get(e.getKey()));
		});
		return matingPool;
	}
	
	
	public static List<Individual> randomHalf(
			final List<Individual> population, final List<Entry<Integer, Double>> sortedRank){
		double[] weights = new double[sortedRank.size()];
		for (int i=0;i<weights.length;i++){
			weights[i] = 1.0/weights.length;
		}
		
		return rouletteSelect(population, sortedRank, weights);
	}
	
	
	public static List<Individual> rouletteSelect(
			List<Individual> population, List<Entry<Integer, Double>> sortedRank, double[] weights) {
		final Random rd = ThreadLocalRandom.current();
		
		List<Entry<Integer, Double>> selected = new ArrayList<>();
		
		while(selected.size() < population.size()/2){
			selected.add(sortedRank.get(rouletteSelect(weights, rd)));
		}
		
		return composeMatingPool(population, selected);
	}


	// the sum of weights must be one;
	public static int rouletteSelect(double[] weight, final Random rd) {

		double value = rd.nextDouble();	
		// locate the random value based on the weights
		for(int i=0; i<weight.length; i++) {		
			value -= weight[i];		
			if(value <= 0) return i;
		}
		// when rounding errors occur, we return the last item's index 
		return weight.length - 1;
	}

	public static List<Individual> indicatorProportionateHalf(
			List<Individual> population, List<Entry<Integer, Double>> sortedRank) {
		double sum = 0;
		for (Entry<Integer, Double> e:sortedRank){
			sum += e.getValue();
		}
		
		double[] weights = new double[sortedRank.size()];
		for (int i=0;i<weights.length;i++){
			weights[i] = sortedRank.get(i).getValue()/sum;
		}
		
		return rouletteSelect(population, sortedRank, weights);
	}

	public static List<Individual> exponentialHalf(
			List<Individual> population, List<Entry<Integer, Double>> sortedRank) {
		double[] weights = exponentialProbability(sortedRank.size());
		
		return rouletteSelect(population, sortedRank, weights);
	}

	public static double[] exponentialProbability(int size) {
		double sum = 0;
		for (int i=0;i<size;i++){
			sum += Math.pow(2, -(i+1));
		}
		
		double[] weights = new double[size];
		for (int i=0;i<weights.length;i++){
			weights[i] = Math.pow(2, -(size-i))/sum;
		}
		return weights;
	}

	public static List<Individual> invQuadrHalf(
			List<Individual> population, List<Entry<Integer, Double>> sortedRank) {
		double[] weights = inverseQuadraticProbability(sortedRank.size());
		
		return rouletteSelect(population, sortedRank, weights);
	}

	public static double[] inverseQuadraticProbability(int size) {
		double sum = 0;
		for (int i=0;i<size;i++){
			sum += 1/Math.pow((i+1), 2);
		}
		
		double[] weights = new double[size];
		for (int i=0;i<weights.length;i++){
			weights[i] = 1/(Math.pow((size-i), 2)*sum);
		}
		return weights;
	}

	
	
	public static List<Individual> harmonicHalf(
			List<Individual> population, List<Entry<Integer, Double>> sortedRank) {
		double[] weights = harmonicProbability(sortedRank.size());
		
		return rouletteSelect(population, sortedRank, weights);
	}

	public static double[] harmonicProbability(final int size) {
		double sum = 0;
		for (int i=0;i<size;i++){
			sum += 1.0/(i+1);
		}
		
		double[] weights = new double[size];
		for (int i=0;i<weights.length;i++){
			weights[i] = 1/((size-i)*sum);
		}
		return weights;
	}

	public static List<Individual> tournamentHalf(
			List<Individual> population, List<Entry<Integer, Double>> sortedRank) {
		
		final Random rd = ThreadLocalRandom.current();
		
		List<Entry<Integer, Double>> selected = new ArrayList<>();
		
		while(selected.size() < population.size()/2){
			
			Entry<Integer, Double> e1 = sortedRank.get(rd.nextInt(sortedRank.size()));
			Entry<Integer, Double> e2 = sortedRank.get(rd.nextInt(sortedRank.size()));
			if (e1.getValue() > e2.getValue()){
				selected.add(e1);
			}else{
				selected.add(e2);
			}

		}
		
		return composeMatingPool(population, selected);
	}

}
