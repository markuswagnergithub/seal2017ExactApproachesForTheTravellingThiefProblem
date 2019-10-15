package ttp.tour;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import ttp.problem.Ttp;

public class InverOver {
	
	public static Integer selectElementInSequenceRandomly(List<Integer> sequence) {
		return sequence.get(ThreadLocalRandom.current().nextInt(sequence.size()));
	}
	
	
	public static Integer getNextElement(List<Integer> nArray, Integer element) {
		int idx = nArray.indexOf(element);
		if (idx < 0)
			throw new RuntimeException("Can't find the element in array.");

		if (idx < nArray.size() - 1) {
			return nArray.get(idx + 1);
		} else {
			return nArray.get(0);
		}
	}
	
	public static TspSolution inverse(TspSolution tour, Integer c, Integer cp){
		int s = tour.getPermutation().indexOf(c);
		int e = tour.getPermutation().indexOf(cp);
		if (s < e){
			s++;
			return new TspSolution(inverse(tour.getPermutation(), s, e), tour.getProblem());
		}else if ( s > e){
			s--;
			return new TspSolution(inverse(tour.getPermutation(), e, s), tour.getProblem());
		}else{
			return tour;
		} 
	}
	
	public static List<Integer> inverse(List<Integer> tour, int start, int end) {
		List<Integer> result = new ArrayList<>(tour);		
		for (int i = start, j = end; i < j; i++, j--) {
			final Integer tmp = result.get(i);
			result.set(i, result.get(j));
			result.set(j, tmp);
		}
		return result;
	}
	
	public static List<Integer> generateRandomSequence(final int length) {
		List<Integer> res = new ArrayList<>(length);
		
		for (int i = 0; i < length - 1; i++) {
			res.add(i + 2);
		}

		Collections.shuffle(res, ThreadLocalRandom.current());
		res.add(0, 1); //add the start city, which is always 1
		return res;
	}

	
	public static List<TspSolution> initialisePopulation(int maxPopulation, Ttp problem){
		List<TspSolution> individuals = new ArrayList<>();
		
    	
		for (int i = 0; i < maxPopulation; i++) {
			TspSolution ind = new TspSolution(
			    generateRandomSequence(problem.n), problem);
		    individuals.add(ind);
		}
		
		return individuals;
	}
	
	public static TspSolution run(Ttp problem) {
		int populationSize = 50;
		long maxGenerations = 100000;
		int maxNotChanged = 10000;
		return run(populationSize, maxGenerations, maxNotChanged, problem);
	}
	
	public static TspSolution run(int populationSize, long maxGenerations, int maxNotChanged,
			Ttp problem) {

		// Random initialization of the population P.
		List<TspSolution> lstPopulation = initialisePopulation(populationSize, problem);

		TspSolution bestInd = null;
		int bestFitness = Integer.MAX_VALUE;
		int notChanged = 0;

		// A low probability p.
		final double p = 0.02;

		// Main algorithm
		for (long i = 0; i < maxGenerations && notChanged < maxNotChanged; i++) {

			for (int j = 0; j < lstPopulation.size(); j++) {
				TspSolution indvS_i = lstPopulation.get(j);

				// Create copy of current individual; S'.
				TspSolution indvS_prime = new TspSolution(indvS_i);

				List<Integer> remainingCities = new ArrayList<>(indvS_prime.getPermutation());

				// Select a random city c from S'.
				Integer c = selectElementInSequenceRandomly(indvS_prime.getPermutation());
				
				remainingCities.remove(c);

				while (true) {
					// City c'.
					Integer c_prime = c;

					if (ThreadLocalRandom.current().nextDouble() <= p) {
						// Select the city c' from the remaining cities in S'
						c_prime = selectElementInSequenceRandomly(remainingCities);
						remainingCities.remove(c_prime);

					} else {
						// Select random individual from P
						TspSolution selectedInd = lstPopulation.get(ThreadLocalRandom.current().nextInt(lstPopulation.size()));

						// Assign to c' the next city c in the selected
						// individual.
						c_prime = getNextElement(selectedInd.getPermutation(), c);
					}
					// Neighboring city c in S' is c'. Exit from repeat loop.
					if (indvS_prime.areNeighbouring(c, c_prime))
						break;

					// Inverse the section from the next city of city c to the
					// city c' in S'
					
//					System.out.print("c:" + c + "\t");
//					System.out.print("c':" + c_prime + "\t");
//					System.out.print(indvS_prime.getPermutation());
					indvS_prime = inverse(indvS_prime, c, c_prime);
//					System.out.println(indvS_prime.getPermutation());
					c = c_prime;
				}

				// System.out.println(indvS_i);
				// System.out.println(indvS_prime);
				// System.out.println("-----------");

				// Evaluate and decide whether we want the new individual.
				if (indvS_prime.getFitness() <= indvS_i.getFitness()) {
					lstPopulation.set(j, indvS_prime);
				}
			}

			TspSolution bestGeneraInd = null;
			int bestGeneraFitness = Integer.MAX_VALUE;
			for (TspSolution in : lstPopulation) {
				if (in.getFitness() < bestGeneraFitness) {
					bestGeneraInd = in;
					bestGeneraFitness = in.getFitness();
				}
			}


			if (bestGeneraFitness < bestFitness) {
				bestInd = bestGeneraInd;
				bestFitness = bestGeneraFitness;
				notChanged = 0;
			}else{
				notChanged++;
			}
//			System.out.println(notChanged);
		}

//		String result = "";
//		result += "Best Overall Fitness : " + bestFitness + "\n";
//		result += "Best Overall TspSolution :\n";
//		result += bestInd.toString() + "\n"; 
//		System.out.println(result);

		return bestInd;
	}

}
