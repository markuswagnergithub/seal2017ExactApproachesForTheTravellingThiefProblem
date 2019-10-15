package ttp.heuristic;

import java.io.FileNotFoundException;

import org.junit.Test;

import ttp.problem.TtpLoader;
import ttp.problem.Tour;

public class LocalSearchTests {
	
	@Test
	public void testeiln51m150twoOpt() throws FileNotFoundException {
//		Tour tour = ProblemLoader.load(
//				"experiments/eil51/eil51_n150_uncorr_01.ttp");
		
		Tour tour = TtpLoader.load("experiments/eil51/eil51_n150_bounded-strongly-corr_07.ttp",
				"experiments/eil51/eil51.linkern.tour");
		
		LocalSearch localSearch = new LocalSearch(tour);
		localSearch.runLocalSearch(LocalSearch.Heuristic.TWO_OPT);
	}
	
	@Test
	public void testeiln51m150exchange() throws FileNotFoundException {
//		Tour tour = ProblemLoader.load(
//				"experiments/eil51/eil51_n150_uncorr_01.ttp");
		
		Tour tour = TtpLoader.load("experiments/eil51/eil51_n150_uncorr_01.ttp",
				"experiments/eil51/eil51.linkern.tour");
		
		LocalSearch localSearch = new LocalSearch(tour);
		localSearch.runLocalSearch(LocalSearch.Heuristic.EXCHANGE);
	}
	
	@Test
	public void testeiln51m150jump() throws FileNotFoundException {
//		Tour tour = ProblemLoader.load(
//				"experiments/eil51/eil51_n150_uncorr_01.ttp");
		
		Tour tour = TtpLoader.load("experiments/eil51/eil51_n150_bounded-strongly-corr_01.ttp",
				"experiments/eil51/eil51.linkern.tour");
		
		LocalSearch localSearch = new LocalSearch(tour);
		localSearch.runLocalSearch(LocalSearch.Heuristic.JUMP);
	}
	
	@Test
	public void testeiln51m50twoOpt() throws FileNotFoundException {
		Tour tour = TtpLoader.load(
				"experiments/eil51/eil51_n50_bounded-strongly-corr_10.ttp",
				"experiments/eil51/eil51.linkern.tour");
		
//		Tour tour = ProblemLoader.load("experiments/multiobj/eil51_n150_bounded-strongly-corr_01/eil51_n150_bounded-strongly-corr_01.ttp",
//				"experiments/multiobj/eil51_n150_bounded-strongly-corr_01/eil51_n150_bounded-strongly-corr_01.ttp-72.tour");
		//"experiments/eil51/eil51.linkern.tour"
		
		LocalSearch localSearch = new LocalSearch(tour);
		localSearch.runLocalSearch(LocalSearch.Heuristic.TWO_OPT);
	}
	
	@Test
	public void testeiln76m75twoOpt() throws FileNotFoundException {
		Tour tour = TtpLoader.load(
				"experiments/eil76/eil76_n75_bounded-strongly-corr_01.ttp",
				"experiments/eil76/eil76.linkern.tour");
		
		
		LocalSearch localSearch = new LocalSearch(tour);
		localSearch.runLocalSearch(LocalSearch.Heuristic.TWO_OPT);
	}
	
	
	@Test
	public void testkroAn100m99twoOpt() throws FileNotFoundException {
		Tour tour = TtpLoader.load(
				"experiments/kroA100/kroA100_n99_bounded-strongly-corr_01.ttp",
				"experiments/kroA100/kroA100.linkern.tour");
		
		
		LocalSearch localSearch = new LocalSearch(tour);
		localSearch.runLocalSearch(LocalSearch.Heuristic.JUMP);
	}
	
//	@Test
//	public void testkroAn100m99twoOpt() throws FileNotFoundException {
//		Tour tour = ProblemLoader.load(
//				"experiments/kroA100/kroA100_n99_bounded-strongly-corr_01.ttp",
//				"experiments/kroA100/kroA100.linkern.tour");
//		
//		
//		LocalSearch localSearch = new LocalSearch(tour);
//		localSearch.runLocalSearch(Heuristic.TWO_OPT);
//	}
	
	
	@Test
	public void testeiln51m50exchange() throws FileNotFoundException {
		Tour tour = TtpLoader.load(
				"experiments/eil51/eil51_n50_bounded-strongly-corr_07.ttp",
				"experiments/eil51/eil51.linkern.tour");
		
//		Tour tour = ProblemLoader.load("experiments/multiobj/eil51_n150_bounded-strongly-corr_01/eil51_n150_bounded-strongly-corr_01.ttp",
//				"experiments/multiobj/eil51_n150_bounded-strongly-corr_01/eil51_n150_bounded-strongly-corr_01.ttp-72.tour");
		//"experiments/eil51/eil51.linkern.tour"
		
		LocalSearch localSearch = new LocalSearch(tour);
		localSearch.runLocalSearch(LocalSearch.Heuristic.EXCHANGE);
	}
	
	@Test
	public void testeiln51m50jump() throws FileNotFoundException {
		Tour tour = TtpLoader.load(
				"experiments/eil51/eil51_n50_uncorr_01.ttp",
				"experiments/eil51/eil51.linkern.tour");
		
//		Tour tour = ProblemLoader.load("experiments/multiobj/eil51_n150_bounded-strongly-corr_01/eil51_n150_bounded-strongly-corr_01.ttp",
//				"experiments/multiobj/eil51_n150_bounded-strongly-corr_01/eil51_n150_bounded-strongly-corr_01.ttp-72.tour");
		//"experiments/eil51/eil51.linkern.tour"
		
		LocalSearch localSearch = new LocalSearch(tour);
		localSearch.runLocalSearch(LocalSearch.Heuristic.JUMP);
	}
	
}
