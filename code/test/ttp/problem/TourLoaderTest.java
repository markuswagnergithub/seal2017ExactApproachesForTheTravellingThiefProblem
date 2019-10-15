package ttp.problem;

import java.util.TreeSet;

import org.junit.Test;

import pwt.exact.dp.DynamicProgramming;
import ttp.tour.TourType;

public class TourLoaderTest {

	@Test
	public void testLoad() {
		Ttp problem = TtpLoader.load("experiments/a280/a280_n279_uncorr_01.ttp");

		Tour tour = TourLoader.load("experiments/a280/a280.tsp.test.aco.tour", problem, TourType.aco);

	}

	@Test
	public void testLoadLkhTour() {
		Ttp problem = TtpLoader.load("experiments/a280/a280_n279_uncorr_01.ttp");

		Tour tour = TourLoader.loadLkhTour("experiments/a280/a280.tsp.test.lkh.tour", problem);

		// System.out.println(tour);

		DynamicProgramming dp = new DynamicProgramming(tour);

		dp.dynamicProgramming();

		TreeSet<Long> keySet = new TreeSet<>(dp.getFinalColumn().keySet());

		for (Long key : keySet) {
			System.out.println(key + "\t" + dp.getFinalColumn().get(key).beta);
		}
	}

	@Test
	public void testLoadConcordeTour() {
		Ttp problem = TtpLoader.load("experiments/a280/a280_n279_uncorr_01.ttp");

		Tour tour = TourLoader.load("experiments/a280/a280.tsp.test.con.tour", problem, TourType.con);

		// System.out.println(tour);

		DynamicProgramming dp = new DynamicProgramming(tour);

		dp.dynamicProgramming();

		TreeSet<Long> keySet = new TreeSet<>(dp.getFinalColumn().keySet());

		for (Long key : keySet) {
			System.out.println(key + "\t" + dp.getFinalColumn().get(key).beta);
		}
	}

	@Test
	public void testLoadLkh2Tour() {
		Ttp problem = TtpLoader.load("experiments/a280/a280_n279_uncorr_01.ttp");

		Tour tour = TourLoader.load("experiments/a280/a280.tsp.test.lkh2.tour", problem, TourType.lkh2);

		// System.out.println(tour);

		DynamicProgramming dp = new DynamicProgramming(tour);

		dp.dynamicProgramming();

		TreeSet<Long> keySet = new TreeSet<>(dp.getFinalColumn().keySet());

		for (Long key : keySet) {
			System.out.println(key + "\t" + dp.getFinalColumn().get(key).beta);
		}
	}

	@Test
	public void testLoadAcoTour() {
		Ttp problem = TtpLoader.load("experiments/a280/a280_n279_uncorr_01.ttp");

		Tour tour = TourLoader.load("experiments/a280/a280.tsp.test.aco.tour", problem, TourType.aco);

		// System.out.println(tour);

		DynamicProgramming dp = new DynamicProgramming(tour);

		dp.dynamicProgramming();

		TreeSet<Long> keySet = new TreeSet<>(dp.getFinalColumn().keySet());

		for (Long key : keySet) {
			System.out.println(key + "\t" + dp.getFinalColumn().get(key).beta);
		}
	}

	@Test
	public void testLoadInvTour() {
		Ttp problem = TtpLoader.load("experiments/a280/a280_n279_uncorr_01.ttp");

		Tour tour = TourLoader.load("experiments/a280/a280.tsp.test.inv.tour", problem, TourType.inv);

		// System.out.println(tour);

		DynamicProgramming dp = new DynamicProgramming(tour);

		dp.dynamicProgramming();

		TreeSet<Long> keySet = new TreeSet<>(dp.getFinalColumn().keySet());

		for (Long key : keySet) {
			System.out.println(key + "\t" + dp.getFinalColumn().get(key).beta);
		}
	}

}
