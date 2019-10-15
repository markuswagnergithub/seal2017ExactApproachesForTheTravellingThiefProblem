package ttp.moea;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;

import pwt.exact.dp.PackingPlan;
import ttp.gui.Displayer.Pair;

public class Statistics {
	public static final double epsilon = 1e-9;
	
	
	public static NavigableMap<Long, Double> surface(List<Individual> population) {
		
		Map<Long, Double> all = new HashMap<>();
		
		for (Individual individual : population) {
			Map<Long, PackingPlan> front = individual.front;
			for (Long w: front.keySet()) {
				PackingPlan p = front.get(w);
				if (!all.containsKey(w) || p.beta > all.get(w)) {
					all.put(w, p.beta);
				}
			}
		}
		
		TreeSet<Long> treeSet = new TreeSet<Long>(all.keySet());
		double b = - Double.MAX_VALUE;
		for (long key : treeSet) {
			if (all.get(key) <= b) {
				all.remove(key);
			} else {
				b = all.get(key);
			}
		}
		
		return new TreeMap<>(all);
	}

	
	public static List<Set<Integer>> fastNonDominatedSort (int size, Comparator<Integer> dominated){
		
		List<Set<Integer>> dominatingSet = new ArrayList<>(size);
		int[] counter = new int[size];
		
		for (int i=0;i<size;i++) {
			dominatingSet.add(new HashSet<>());
			counter[i] = 0;
		}
		
		int[] rank = new int[size];
		List<Set<Integer>> fronts = new ArrayList<>();
		fronts.add(new HashSet<>());
		
		IntStream.range(0, size).parallel().forEach((p)->{
			for (int q=0;q<size;q++) {
				if (dominated.compare(p, q) == 1) {
					dominatingSet.get(p).add(q);
				}else if (dominated.compare(q, p) == 1) {
					counter[p] += 1;
				}
			}
			if (counter[p] == 0) {
				rank[p] = 1;
				fronts.get(0).add(p);
			}
		});
		
		int i = 0;
		while (fronts.get(i).size() != 0) {
			Set<Integer> front = new HashSet<>();
			for (int p :fronts.get(i)) {
				for (int q:dominatingSet.get(p)) {
					counter[q] -= 1;
					if (counter[q] == 0) {
						rank[q] = i + 1;
						front.add(q);
					}
				}
			}
			i++;
			fronts.add(front);
		}
		
		return fronts;
		
	}
	
	
	public static int dominated(long w1, double b1, Long w2, double b2) {
		
		if (Math.abs(b1 - b2) < epsilon && w1 == w2) {
			return 0;
		}

		if ((b1 >= b2 && w1 < w2) || (b1 > b2 && w1 <= w2)) {
			return 1;
		}

		return -1;

	}
	
	public static int compareWeight(long w1, double b1, Long w2, double b2) {
		int c = Long.compare(w1, w2);
		if (c == 0) {
			return - Double.compare(b1, b2);
		}else {
			return c;
		}
	}
	
	public static int compareBenefit(long w1, double b1, Long w2, double b2) {
		int c = Double.compare(b1, b2);
		if (c == 0) {
			return - Long.compare(w1, w2);
		}else {
			return c;
		}
	}
	
	public static Map<Integer, Double> hypervolumeOfIndividuals(
			final List<Individual> population, final long bigW,
			final double referencePoint) {
		Map<Integer, Double> hypervolumes = new ConcurrentHashMap<>();
		
		IntStream.range(0, population.size()).parallel().forEach((e)->{
			hypervolumes.put(e, hypervolumeOfFront(population.get(e).front, bigW, referencePoint));
		});

		return hypervolumes;
	}
	
	public static Map<Integer, Double> marginalHypervolumeOfIndividuals(
			final List<Individual> population, final long bigW,
			final double referencePoint) {
		NavigableMap<Long, Double> surface = surface(population);
		double overallHv = hypervolume(surface, bigW, referencePoint);
		
		Map<Integer, Double> hypervolumes = new ConcurrentHashMap<>();
		
		IntStream.range(0, population.size()).parallel().forEach((e)->{
			hypervolumes.put(e, overallHv - marginalHypervolumeOfFront(population, e, bigW, referencePoint));
		});

		return hypervolumes;
	}
	
	public static Double marginalHypervolumeOfFront(
			final List<Individual> population,
			final int excludedIndividual, final long bigW, final double referencePoint) {
		
		List<Individual> list = new ArrayList<>(population);
		list.remove(excludedIndividual);
		
		NavigableMap<Long, Double> surface = surface(list);
		
		return hypervolume(surface, bigW, referencePoint);
	}


	public static double hypervolumeOfFront(final NavigableMap<Long, PackingPlan> front, long bigW, double referencePoint) {
		Iterator<Map.Entry<Long, PackingPlan>> it = front.entrySet().iterator();
		Map.Entry<Long, PackingPlan> p = it.next();
		final double nadir = referencePoint;
		double hv = 0;
		while(it.hasNext()) {
			Map.Entry<Long, PackingPlan> c = it.next();
			if (p.getValue().beta > nadir) {
				hv += (c.getKey() - p.getKey())*(p.getValue().beta - nadir);
			}
			p = c;
		}
		
		hv += (bigW - p.getKey())*p.getValue().beta;
		
		return hv;
	}
	
	
	
	public static double hypervolume(final NavigableMap<Long, Double> surface, long bigW, double referencePoint) {
		Iterator<Map.Entry<Long, Double>> it = surface.entrySet().iterator();
		Map.Entry<Long, Double> p = it.next();
		final double nadir = referencePoint;
		double hv = 0;
		while(it.hasNext()) {
			Map.Entry<Long, Double> c = it.next();
			if (p.getValue() > nadir) {
				hv += (c.getKey() - p.getKey())*(p.getValue() - nadir);
			}
			p = c;
		}
		
		hv += (bigW - p.getKey())*p.getValue();
		
		return hv;
	}
	
	public static double maxBenefitInPopulation(List<Individual> population){
		double best = - Double.MAX_VALUE;
		for (Individual m:population){
			double b = m.front.lastEntry().getValue().beta;
			if (b > best){
				best = b;
			}
		}
		return best;
	}
	
	public static Map<Integer, Double> marginalSurfaceContribution(final NavigableMap<Long, Double> surface, 
			List<Individual> population) {
		Map<Integer, Double> contrib = new ConcurrentHashMap<>();
		
		IntStream.range(0, population.size()).parallel().forEach((k)->{
			contrib.put(k, 1 - marginalSurfaceContributionOfFront(population, k, surface));
		});
		
		return contrib;
	}
	
	public static Double marginalSurfaceContributionOfFront(
			final List<Individual> population,
			final int excludedIndividual, NavigableMap<Long, Double> overallSurface) {
		
		List<Individual> list = new ArrayList<>(population);
		list.remove(excludedIndividual);
		
		NavigableMap<Long, Double> surface = surface(list);
		
		return surfaceContibutionOfFront(surface, overallSurface);
	}
	
	public static Double surfaceContibutionOfFront(NavigableMap<Long, Double> front, NavigableMap<Long, Double> surface) {
		int counter = 0;
		for (Map.Entry<Long, Double> e : surface.entrySet()) {
			Double p = front.get(e.getKey());  
			if (p != null && (p - e.getValue())  > - epsilon){
				counter++;
			}
		}
		
		return (double)counter/surface.size();
	}
	
	
	public static Map<Integer, Double> surfaceContribution(final NavigableMap<Long, Double> surface, 
			final List<Individual> population) {
		Map<Integer, Double> contrib = new ConcurrentHashMap<>();
		
		IntStream.range(0, population.size()).parallel().forEach((k)->{
			contrib.put(k, surfaceContibution(population.get(k).front, surface));
		});
		
		return contrib;
	}

	public static Double surfaceContibution(NavigableMap<Long, PackingPlan> front, NavigableMap<Long, Double> surface) {
		int counter = 0;
		for (Map.Entry<Long, Double> e : surface.entrySet()) {
			PackingPlan p = front.get(e.getKey());  
			if (p != null && (p.beta - e.getValue())  > - epsilon){
				counter++;
			}
		}
		
		return (double)counter/surface.size();
	}
	
	
	public static List<List<Pair<Number, Number>>> fronts(List<Individual> population) {
		List<List<Pair<Number, Number>>> list = new ArrayList<>();

		for (Individual ind : population) {

			Map<Long, PackingPlan> front = ind.front;
			List<Pair<Number, Number>> data = new ArrayList<>();
			for (Long weight : front.keySet()) {
				data.add(new Pair<Number, Number>(weight, front.get(weight).beta));
			}
			list.add(data);

		}
		return list;
	}
	

	
}
