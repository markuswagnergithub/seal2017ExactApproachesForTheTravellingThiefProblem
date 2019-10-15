package ttp.moea;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

import common.tools.Util;
import pwt.exact.dp.DynamicProgramming;
import pwt.exact.dp.PackingPlan;
import ttp.gui.Displayer;
import ttp.problem.Tour;
import ttp.problem.Ttp;
import ttp.problem.TtpLoader;
import ttp.tour.InverOver;
import ttp.tour.TspSolution;

public class Heuristic {

	public enum MutationType {
		JUMP, TWOOPT, SWAP, JUMPSWAP, JUMP2OPT
	};

	public enum Indicator {
		HV, LHV, SC, LSC, TR
	};

	public enum Selection {
		RANDOM_HALF, BETTER_HALF, WORSE_HALF, TWO_ENDS_HALF, UNIFORMLY_AT_RANDOM_HALF, INDICATOR_PROPORTIONATE_HALF, EXPONENTIAL_HALF, INVERSE_QUADRATIC_HALF, HARMONIC_HALF, TOURNAMENT_HALF
	}

	public enum ReduceType {
		AT_ONCE, ONE_BY_ONE
	}

	public enum PopulationSizeType {
		VARIED, FIXED
	}

	public final Ttp ttp;
	public final long maxIteration = 20000;
	public final long maxUnchanged = 20000;
	public final int populationSize = 50;
	public final double hypervolumeRefPoint = 0;

	public final List<Individual> population;

	private static void printUsage() {
		System.out.println("Usage: ttp.moea.heuristic i s pi si st rt");
		System.out.println("");
		System.out.println("\ti:\tthe ttp instance.");
		System.out.println("\ts:\tthe selection of parents, which shall be one of the followed:");
		System.out.println("RANDOM_HALF, BETTER_HALF, WORSE_HALF, TWO_ENDS_HALF, UNIFORMLY_AT_RANDOM_HALF, ");
		System.out.println(
				"INDICATOR_PROPORTIONATE_HALF, EXPONENTIAL_HALF, INVERSE_QUADRATIC_HALF, HARMONIC_HALF, TOURNAMENT_HALF");
		System.out.println("\tpi:\tthe ranking indicator of parent selection, shall be one of HV, LHV, SC, LSC, TR.");
		System.out.println("\tsi:\tthe ranking indicator of survivor selection, shall be one of HV, LHV, SC, LSC, TR.");
		System.out.println("\tst:\tthe size type of population, shall be one of VARIED, FIXED.");
		System.out.println("\trt:\tthe type of reduction, which shall be one of AT_ONCE, ONE_BY_ONE.");
		System.out.println("");
		System.out.println(
				"example: ttp.moea.heuristic experiments/eil76/eil76_n75_uncorr_01.ttp UNIFORMLY_AT_RANDOM_HALF LHV LHV FIXED ONE_BY_ONE");
	}

	public static void main(String[] args) throws IOException {

		if (args.length < 6) {
			printUsage();
			return;
		}
		int i = 0;
		String instance = args[i++];
		Selection parentSelection = Selection.valueOf(args[i++]);
		Indicator parentIndicator = Indicator.valueOf(args[i++]);
		Indicator survivorIndicator = Indicator.valueOf(args[i++]);
		PopulationSizeType sizeType = PopulationSizeType.valueOf(args[i++]);
		ReduceType reduceType = ReduceType.valueOf(args[i++]);

		Heuristic ls = new Heuristic(instance);
		ls.search(parentSelection, parentIndicator, MutationType.JUMP2OPT, survivorIndicator, sizeType, reduceType);

		// String ttpFile =
		// "../../multiobj/experiments/eil76/eil76_n75_bounded-strongly-corr_01.ttp";
		// String ttpFile =
		// "../../multiobj/experiments/eil76/eil76_n75_uncorr_01.ttp";
		// eil76_n75_uncorr-similar-weights_01.ttp

		// String inputFolder = "../../multiobj/experiments/eil51";
		// String inputFolder = "experiments/eil76";
		//
		// Displayer.display = false;
		//
		// for (Path ttpFile : Files.newDirectoryStream(Paths.get(inputFolder),
		// path -> path.toString().matches(".*eil76_n75_uncorr_01\\.ttp$"))) {
		// IntStream.rangeClosed(1, 10).forEach((i) -> {
		// System.out.println(ttpFile + "\tRun:" + i);
		// Heuristic ls = new Heuristic(ttpFile.toString());

		// ls.search(Selection.UNIFORMLY_AT_RANDOM_HALF, Indicator.SC,
		// MutationType.JUMP2OPT, Indicator.SC, true, ReduceType.AT_ONCE);
		// ls.search(Selection.UNIFORMLY_AT_RANDOM_HALF, Indicator.LSC,
		// MutationType.JUMP2OPT, Indicator.LSC, PopulationSizeType.FIXED,
		// ReduceType.ONE_BY_ONE);
		// ls.search(Selection.INDICATOR_PROPORTIONATE_HALF, Indicator.LSC,
		// MutationType.JUMP2OPT, Indicator.LSC, PopulationSizeType.FIXED,
		// ReduceType.ONE_BY_ONE);
		// ls.search(Selection.EXPONENTIAL_HALF, Indicator.LSC,
		// MutationType.JUMP2OPT, Indicator.LSC, PopulationSizeType.FIXED,
		// ReduceType.ONE_BY_ONE);
		// ls.search(Selection.INVERSE_QUADRATIC_HALF, Indicator.LSC,
		// MutationType.JUMP2OPT, Indicator.LSC, PopulationSizeType.FIXED,
		// ReduceType.ONE_BY_ONE);
		// ls.search(Selection.HARMONIC_HALF, Indicator.LSC,
		// MutationType.JUMP2OPT, Indicator.LSC, PopulationSizeType.FIXED,
		// ReduceType.ONE_BY_ONE);
		// ls.search(Selection.TOURNAMENT_HALF, Indicator.LSC,
		// MutationType.JUMP2OPT, Indicator.LSC, PopulationSizeType.FIXED,
		// ReduceType.ONE_BY_ONE);

		// ls.search(Selection.UNIFORMLY_AT_RANDOM_HALF, Indicator.LHV,
		// MutationType.JUMP2OPT, Indicator.LHV, true, ReduceType.ONE_BY_ONE);
		// ls.search(Selection.INDICATOR_PROPORTIONATE_HALF, Indicator.LHV,
		// MutationType.JUMP2OPT, Indicator.LHV, true, ReduceType.ONE_BY_ONE);
		// ls.search(Selection.EXPONENTIAL_HALF, Indicator.LHV,
		// MutationType.JUMP2OPT, Indicator.LHV, true, ReduceType.ONE_BY_ONE);
		// ls.search(Selection.INVERSE_QUADRATIC_HALF, Indicator.LHV,
		// MutationType.JUMP2OPT, Indicator.LHV, true, ReduceType.ONE_BY_ONE);
		// ls.search(Selection.HARMONIC_HALF, Indicator.LHV,
		// MutationType.JUMP2OPT, Indicator.LHV, true, ReduceType.ONE_BY_ONE);
		// ls.search(Selection.TOURNAMENT_HALF, Indicator.LHV,
		// MutationType.JUMP2OPT, Indicator.LHV, true, ReduceType.ONE_BY_ONE);

		// });
		// }

	}

	public Heuristic(String ttpFile) {
		ttp = TtpLoader.load(ttpFile);
		population = new ArrayList<>();
	}

	public void initialise() {
		Displayer.reset();
		population.clear();

		injectIndividuals();
		// injectRandomIndividuals();
	}

	public void injectRandomIndividuals() {

		int[] seedArray = new int[ttp.n];
		for (int i = 1; i <= ttp.n; i++) {
			seedArray[i - 1] = i;
		}

		Map<Tour.Essence, NavigableMap<Long, PackingPlan>> map = new ConcurrentHashMap<>();
		IntStream.range(0, populationSize).parallel().forEach((i) -> {
			Tour tour = new Tour(ttp);
			int[] permu = seedArray.clone();
			Util.shuffle(permu);
			tour.update(permu);
			DynamicProgramming dp = new DynamicProgramming(tour);
			dp.dynamicProgramming();
			map.put(tour.essence, dp.getSortedFinalColumn());
		});
		map.entrySet().forEach((e) -> {
			population.add(new Individual(e.getKey(), e.getValue()));
		});

	}

	public void injectIndividuals() {
		System.out.println("Injecting");
		int size = this.populationSize / 2 + 2;

		Set<Tour.Essence> tspSolutions = Collections.newSetFromMap(new ConcurrentHashMap<Tour.Essence, Boolean>());

		IntStream.range(0, size).parallel().forEach((i) -> {
			TspSolution solution = InverOver.run(ttp);
			// System.out.println(solution.getPermutation());
			tspSolutions.add(new Tour.Essence(Util.toArray(solution.getPermutation())));

			solution = solution.reverse();
			// System.out.println(solution.getPermutation());
			tspSolutions.add(new Tour.Essence(Util.toArray(solution.getPermutation())));
		});

		Iterator<Tour.Essence> it = tspSolutions.iterator();
		while (it.hasNext() && tspSolutions.size() > this.populationSize) {
			it.next();
			it.remove();
		}

		Map<Tour.Essence, NavigableMap<Long, PackingPlan>> map = new ConcurrentHashMap<>();

		tspSolutions.parallelStream().forEach((solutionPermu) -> {
			Tour tour = new Tour(ttp);
			tour.update(solutionPermu.getStops());
			DynamicProgramming dp = new DynamicProgramming(tour);
			dp.dynamicProgramming();
			map.put(solutionPermu, dp.getSortedFinalColumn());
		});

		map.entrySet().forEach((e) -> {
			population.add(new Individual(e.getKey(), e.getValue()));
		});
	}

	public void search(final Selection selection, final Indicator selectionIndicator, final MutationType type,
			final Indicator reducerIndicator, final PopulationSizeType sizeType, final ReduceType reduceType) {
		long start = System.currentTimeMillis();
		String msg = ttp.path + "\nSelection: " + selection + "\tSelectionIndicator: " + selectionIndicator
				+ "\tMutation: " + type + "\tReducerIndicator: " + reducerIndicator + "\tPopulationSizeType: "
				+ sizeType + "\tReductionType: " + reduceType;
		System.out.println(msg);

		initialise();

		final Report rp = new Report(ttp, type, hypervolumeRefPoint);
		final Ranker ranker = new Ranker(ttp.bigW, hypervolumeRefPoint);

		NavigableMap<Long, Double> surface = Statistics.surface(population);
		Map.Entry<Long, Double> endpoint = surface.lastEntry();
		double hv = Statistics.hypervolume(surface, ttp.bigW, 0);
		msg = "Iteration:0\tpopulationSize:" + population.size() + "\tMax Benefit:" + endpoint.getValue() + "\tWeight:"
				+ endpoint.getKey() + "\tHypervolume:" + hv;
		System.out.println(msg);

		long unchangeCount = 0;
		double maxBenefit = -Double.MAX_VALUE;
		int i;
		for (i = 1; i < maxIteration && unchangeCount <= maxUnchanged; i++) {

			List<Individual> matingPool = matingPool(selection, selectionIndicator, ranker);
			operate(type, matingPool);

			updatePopulation(ranker, reduceType, reducerIndicator, sizeType);

			double b = Statistics.maxBenefitInPopulation(population);
			if (b > maxBenefit) {
				maxBenefit = b;
				unchangeCount = 0;
			} else {
				unchangeCount++;
			}

			if (i % 100 == 0) {
				rp.statusReport(i, population);
			}
		}

		rp.statusReport(i, population);
		rp.printFinalReport(selection, selectionIndicator, population, (System.currentTimeMillis() - start) / 1000);
	}

	public void operate(final MutationType type, final List<Individual> matingPool) {
		List<Tour.Essence> offspring = crossover(matingPool);

		final Random rd = ThreadLocalRandom.current();
		while (offspring.size() < matingPool.size()) {
			offspring.add(matingPool.get(rd.nextInt(matingPool.size())).tour);
		}

		addToursIntoPopulation(mutate(offspring, type));

	}

	private List<Individual> matingPool(final Selection selection, final Indicator indicator, final Ranker ranker) {

		switch (selection) {
		case RANDOM_HALF:
			return ParentSelection.randomHalf(population);
		case BETTER_HALF:
			return ParentSelection.betterHalf(population, ranker.sortedRankAscendingly(indicator, population));
		case WORSE_HALF:
			return ParentSelection.worseHalf(population, ranker.sortedRankAscendingly(indicator, population));
		case TWO_ENDS_HALF:
			return ParentSelection.mixedHalf(population, ranker.sortedRankAscendingly(indicator, population));
		case UNIFORMLY_AT_RANDOM_HALF:
			return ParentSelection.randomHalf(population, ranker.sortedRankAscendingly(indicator, population));
		case INDICATOR_PROPORTIONATE_HALF:
			return ParentSelection.indicatorProportionateHalf(population,
					ranker.sortedRankAscendingly(indicator, population));
		case EXPONENTIAL_HALF:
			return ParentSelection.exponentialHalf(population, ranker.sortedRankAscendingly(indicator, population));
		case INVERSE_QUADRATIC_HALF:
			return ParentSelection.invQuadrHalf(population, ranker.sortedRankAscendingly(indicator, population));
		case HARMONIC_HALF:
			return ParentSelection.harmonicHalf(population, ranker.sortedRankAscendingly(indicator, population));
		case TOURNAMENT_HALF:
			return ParentSelection.tournamentHalf(population, ranker.sortedRankAscendingly(indicator, population));
		default:
			throw new RuntimeException("Selection " + selection + " is not supported.");
		}
	}

	private void updatePopulation(final Ranker ranker, final ReduceType reduceType, final Indicator reducerIndicator,
			final PopulationSizeType sizeType) {
		if (population.size() <= populationSize)
			return;
		switch (sizeType) {
		case FIXED:
			Reducer.removeLowerRankedTours(population, reducerIndicator, populationSize, reduceType, ranker);
			break;
		case VARIED:
			Reducer.removeZeroRankedTours(population, ranker.rank(reducerIndicator, population));
			break;
		default:
			throw new RuntimeException("PopulationSizeType " + sizeType + " is not supported.");
		}

	}

	private List<Tour.Essence> mutate(List<Tour.Essence> matingPool, final MutationType type) {
		List<Tour.Essence> offspring = new ArrayList<>();

		matingPool.stream().forEach((i) -> {
			Tour.Essence mutatedTour = Operators.mutate(i, type);
			offspring.add(mutatedTour);
		});

		return offspring;

	}

	public void addToursIntoPopulation(List<Tour.Essence> offspring) {
		Map<Integer, NavigableMap<Long, PackingPlan>> fronts = new ConcurrentHashMap<>();
		IntStream.range(0, offspring.size()).parallel().forEach((i) -> {
			DynamicProgramming dp = new DynamicProgramming(ttp, offspring.get(i));
			dp.dynamicProgramming();
			fronts.put(i, dp.getSortedFinalColumn());
		});

		for (int i = 0; i < offspring.size(); i++) {
			population.add(new Individual(offspring.get(i), fronts.get(i)));
		}
	}

	private List<Tour.Essence> crossover(List<Individual> matingPool) {
		List<Tour.Essence> offspring = new ArrayList<>();
		IntStream.range(0, 2).forEach((k) -> {
			Individual[] indis = matingPool.toArray(new Individual[matingPool.size()]);
			Util.shuffle(indis);

			final Random rd = ThreadLocalRandom.current();
			if (rd.nextDouble() < 0.8) {
				IntStream.range(0, indis.length / 2).forEach((i) -> {
					Tour.Essence crossed = Operators.mpx(indis[i * 2].tour, indis[i * 2 + 1].tour);
					offspring.add(crossed);

				});
			}
		});

		return offspring;
	}

}
