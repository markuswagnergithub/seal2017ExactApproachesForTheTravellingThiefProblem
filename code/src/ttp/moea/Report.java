package ttp.moea;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;

import pwt.exact.dp.DynamicProgramming;
import pwt.exact.dp.PackingPlan;
import ttp.gui.Displayer;
import ttp.gui.Displayer.Pair;
import ttp.moea.Heuristic.Indicator;
import ttp.moea.Heuristic.MutationType;
import ttp.moea.Heuristic.Selection;
import ttp.problem.Tour;
import ttp.problem.Ttp;

public class Report {
	public final Ttp ttp;
	public final MutationType mutationType;
	public final double hypervolumeRefPoint;

	public Report(Ttp ttp, MutationType mutationType, double hypervolumeRefPoint) {
		this.ttp = ttp;
		this.mutationType = mutationType;
		this.hypervolumeRefPoint = hypervolumeRefPoint;
	}

	public static List<Individual> sortByHashcode(List<Individual> population) {
		List<Individual> tours = new ArrayList<>(population);
		
		Collections.sort(tours, (p, q)->{
			return Integer.compare(p.tour.hashcode, q.tour.hashcode);
		});
		
		return tours;
	}
	
	public static List<Tour.Essence> sortByHashcode(Set<Tour.Essence> tours) {
		List<Tour.Essence> ts = new ArrayList<>(tours);
		
		Collections.sort(ts, (p, q)->{
			return Integer.compare(p.hashcode, q.hashcode);
		});
		
		return ts;
	}
	
	public static Map<Integer, Pair<Number, Number>> endPoints(List<Individual> population) {
		Map<Integer, Pair<Number, Number>> endpoints = new HashMap<>();

		for (int i=0;i<population.size();i++) {
			endpoints.put(i, new Pair<>(population.get(i).front.lastKey(), population.get(i).front.lastEntry().getValue().beta));
		}
		return endpoints;
	}
	
	public void statusReport(int iteration, List<Individual> population) {
		double hv;
		String title = ttp.path + " " + mutationType;
		String msg;
		NavigableMap<Long, Double> surface = Statistics.surface(population);
		Map.Entry<Long, Double> endpoint = surface.lastEntry();

		hv = Statistics.hypervolume(surface, ttp.bigW, hypervolumeRefPoint);
		msg = "Iteration:" + iteration + "\tpopulationSize:" + population.size() + "\tMax Benefit:"
				+ endpoint.getValue() + "\tWeight:" + endpoint.getKey() + "\tHypervolume:" + hv;

		System.out.println(msg);
		if (Displayer.display) {
			Map<Integer, Double> contribution = Statistics.surfaceContribution(surface, population);

			List<List<Pair<Number, Number>>> list = Statistics.fronts(population);
			Map<Integer, Pair<Number, Number>> endPoints = endPoints(population);

			Displayer.show(list, surface, endPoints, histdata(contribution), title, msg);
		}

	}

	public List<Pair<String, Number>> histdata(Map<Integer, Double> contrib) {
		List<Pair<String, Number>> c = new ArrayList<>();
		contrib.entrySet().stream().forEach((e)->{
			c.add(new Pair<>(e.getKey().toString(), e.getValue()));
		});

		return c;
	}

	public void printFinalReport(Selection selection, Indicator indicator,
			List<Individual> population, long runtime) {
		double maxBenefit = -Double.MAX_VALUE;
		Tour.Essence bestTour = null;

		for (Individual ind : population) {
			NavigableMap<Long, PackingPlan> front = ind.front;
			PackingPlan bestCell = front.lastEntry().getValue();
			if (bestCell.beta > maxBenefit) {
				maxBenefit = bestCell.beta;
				bestTour = ind.tour;
			}
		}
		DynamicProgramming dp = new DynamicProgramming(ttp, bestTour);
		dp.dynamicProgramming();

		System.out.println("Best solution:");
		System.out.println("Benefit:" + dp.getMaxBenefit() + "\tProfit:" + dp.getTotalProfit() + "\tWeight:"
				+ dp.getTotalWeight());
		System.out.println("Tour:" + Arrays.toString(bestTour.getStops()));
		System.out.println("Packing Plan:" + Arrays.toString(dp.getPackingPlan()));

		double hv = Statistics.hypervolume(Statistics.surface(population), ttp.bigW, hypervolumeRefPoint);
		System.out.println("Selection\tIndicator\tPopulationSize\tReward\tHypervolume");
		System.out.println("$" + selection + "\t"+ indicator + "\t" + population.size()
				+ "\t" + dp.getMaxBenefit() + "\t" + hv);

	}
//
//	public static void printTourList(List<Tour.Essence> list,
//			List<Individual> population) {
//		System.out.println("#####################");
//		for (int i = 0; i < list.size(); i++) {
//			Map.Entry<Long, PackingPlan> e = population.get(list.get(i)).lastEntry();
//			System.out.println(i + " " + e.getKey() + " " + e.getValue().beta);
//		}
//	}

}
