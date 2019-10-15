package ttp.moea;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import ttp.moea.Heuristic.Indicator;

public class Ranker {
	
	
	public final long bigW;
	public final double hypervolumeRefPoint;
	
	public Ranker(final long bigW, final double hypervolumeRefPoint){
		this.bigW = bigW;
		this.hypervolumeRefPoint = hypervolumeRefPoint;
	}
	
	public Map<Integer, Double> rank(final Indicator indicator, List<Individual> population) {

		switch (indicator) {
		case HV:
			return Ranker.rankByHypervolume(population, bigW, hypervolumeRefPoint);
		case LHV:
			return Ranker.rankByMarginalHypervolume(population, bigW, hypervolumeRefPoint);
		case SC:
			return Ranker.rankBySurfaceContribution(population);
		case LSC:
			return Ranker.rankByMarginalSurfaceContribution(population);
		case TR:
			return Ranker.rankByTotalReward(population);
		default:
			throw new RuntimeException("Indicator " + indicator + " is not supported.");
		}

	}
	
	public List<Entry<Integer, Double>> sortedRankAscendingly(final Indicator indicator, List<Individual> population){
		Map<Integer, Double> rank = rank(indicator, population);
		return rank.entrySet().stream().sorted(Map.Entry.comparingByValue()).collect(Collectors.toList());
	}
	

	public static Map<Integer, Double> rankBySurfaceContribution(final List<Individual> population) {		
		NavigableMap<Long, Double> surface = Statistics.surface(population);
		return Statistics.surfaceContribution(surface, population);
	}
	
	public static Map<Integer, Double> rankByMarginalSurfaceContribution(final List<Individual> population){
		NavigableMap<Long, Double> surface = Statistics.surface(population);
		return Statistics.marginalSurfaceContribution(surface, population);
	}
	
	public static Map<Integer, Double> rankByMarginalHypervolume(final List<Individual> population, final long bigW, final double referencePoint){
		return Statistics.marginalHypervolumeOfIndividuals(population, bigW, referencePoint);
	}
	
	
	public static Map<Integer, Double> rankByHypervolume(final List<Individual> population, final long bigW, final double referencePoint){
		return Statistics.hypervolumeOfIndividuals(population, bigW, referencePoint);
	}
	
	public static Map<Integer, Double> rankByTotalReward(final List<Individual> population) {
		Map<Integer, Double> rewards = new HashMap<>();
		for (int i=0;i<population.size();i++){
			rewards.put(i, population.get(i).front.lastEntry().getValue().beta);
		}
		return rewards;
	}
	
}
