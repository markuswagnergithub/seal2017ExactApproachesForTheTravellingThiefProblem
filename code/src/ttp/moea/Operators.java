package ttp.moea;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import common.tools.Util;
import ttp.moea.Heuristic.MutationType;
import ttp.problem.Tour;
import ttp.problem.Tour.Essence;

public class Operators {
	
	public static Tour.Essence mutate(Essence tour, MutationType type) {
		int[] stops = tour.getStops();
		mutate(type, stops);
		return new Tour.Essence(stops);
	}

	public static void mutate(final MutationType type, final int[] stops) {
		final Random rd = ThreadLocalRandom.current();
		int from = rd.nextInt(stops.length - 1) + 1;
		int to = rd.nextInt(stops.length - 1) + 1;
		switch (type) {
		case TWOOPT:
			Util.reverse(stops, from, to);
			break;
		case JUMP:
			Util.jump(stops, from, to);
			break;
		case SWAP:
			Util.swap(stops, from, to);
			break;
		case JUMPSWAP:
			if (rd.nextDouble()>0.5) {
				int chosen = rd.nextInt(stops.length - 2) + 1;
				Util.swap(stops, chosen, chosen + 1);
			}else {
				Util.jump(stops, from, to);
			}
			break;
		case JUMP2OPT:
			if (rd.nextDouble()>0.5) {
				Util.reverse(stops, from, to);
			}else {
				Util.jump(stops, from, to);
			}
			break;
		default:
			throw new RuntimeException("Mutation " + type + " is not supported.");
		}
	}

	public static Tour.Essence mpx(Tour.Essence donor, Tour.Essence receiver) {
		int[] offspring = mpx(donor.getStops(), receiver.getStops());
		return new Tour.Essence(offspring);
	}

	// Maximal Preservative Crossover
	public static int[] mpx(int[] donor, int[] receiver) {
		final Random rd = ThreadLocalRandom.current();
		int bl = donor.length / 5;
		int bu = donor.length / 2;

		HashMap<Integer, Integer> donorMap = new HashMap<>();
		HashMap<Integer, Integer> receiverMap = new HashMap<>();

		for (int i = 0; i < donor.length - 1; i++) {
			donorMap.put(donor[i], donor[i + 1]);
			receiverMap.put(receiver[i], receiver[i + 1]);
		}
		donorMap.put(donor[donor.length - 1], donor[0]);
		receiverMap.put(receiver[receiver.length - 1], receiver[0]);

		int i = rd.nextInt(donor.length - 1);
		int j = (i + rd.nextInt(bu) + bl);

		// System.out.println("i:" + i + " j:" + j);

		int[] offspring = new int[donor.length];

		HashSet<Integer> xvrString = new HashSet<>();
		int l = 0;
		for (int k = i; k <= j && l < offspring.length; k++) {
			int c = k >= donor.length ? k % donor.length : k;
			offspring[l] = donor[c];
			xvrString.add(offspring[l]);
			l++;
		}

		// System.out.println(Arrays.toString(offspring));
		for (int k = l; k < offspring.length; k++) {
			int p = offspring[k - 1];
			// System.out.println(p);
			if (!xvrString.contains(receiverMap.get(p))) {
				offspring[k] = receiverMap.get(p);
			} else if (!xvrString.contains(donorMap.get(p))) {
				offspring[k] = donorMap.get(p);

			} else {
				for (int q : receiver) {
					if (!xvrString.contains(q)) {
						offspring[k] = q;
						break;
					}
				}
			}
			xvrString.add(offspring[k]);
		}
		return Util.align(offspring);
	}
	
}
