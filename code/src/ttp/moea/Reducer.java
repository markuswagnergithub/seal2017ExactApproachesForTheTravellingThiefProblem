package ttp.moea;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import ttp.moea.Heuristic.Indicator;
import ttp.moea.Heuristic.ReduceType;

public class Reducer {

	public static void removeLowerRankedTours(final List<Individual> population,
			final Indicator reducerIndicator, int preferredPopulationSize, final ReduceType reduceType,
			final Ranker ranker) {
		switch (reduceType) {
		case AT_ONCE:
			removeLowerRankedToursAtOnce(population, ranker.rank(reducerIndicator, population),
					preferredPopulationSize);
			break;
		case ONE_BY_ONE:
			removeLowerRankedToursAtOneByOne(population, reducerIndicator, preferredPopulationSize, ranker);
			break;
		default:
			throw new RuntimeException("ReduceType " + reduceType + "is not supported.");
		}
	}

	private static void removeLowerRankedToursAtOneByOne(final List<Individual> population,
			final Indicator reducerIndicator, int preferredPopulationSize, final Ranker ranker) {

		while (population.size() > preferredPopulationSize) {

			Map<Integer, Double> rank = ranker.rank(reducerIndicator, population);

			int worstIndi = -1;
			double worst = Double.MAX_VALUE;
			for (Entry<Integer, Double> e : rank.entrySet()) {
				if (e.getValue() < worst) {
					worst = e.getValue();
					worstIndi = e.getKey();
				}
			}

			population.remove(worstIndi);

		}
	}

	private static void removeLowerRankedToursAtOnce(final List<Individual> population,
			final Map<Integer, Double> rank, int preferredPopulationSize) {
		List<Entry<Integer, Double>> sorted = rank.entrySet().stream().sorted(Map.Entry.comparingByValue())
				.collect(Collectors.toList());

		int size = population.size() - preferredPopulationSize;

		sorted.stream().limit(size).forEach((e) -> {
			population.remove(e.getKey().intValue());
		});
	}

	public static void removeZeroRankedTours(final List<Individual> population,
			final Map<Integer, Double> rank) {

		for (Map.Entry<Integer, Double> e : rank.entrySet()) {
			if (e.getValue() <= 0) {
				population.remove(e.getKey().intValue());
			}
		}

	}

}
