package pwt.exact.dp;

import org.junit.Assert;

import java.io.FileNotFoundException;
import java.util.Arrays;

import org.junit.Test;

import pwt.exact.dp.DynamicProgramming;
import ttp.problem.TtpLoader;
import ttp.problem.Tour;
import ttp.problem.Ttp;

public class NkpDpTest {
	
	private static final double delta = 1e-10;

	
	@Test
	public void testDpTest1n8m14() throws FileNotFoundException{

		String problemFile = "experiments/test/test1_n8_m14.ttp";
		
		Ttp ttp = TtpLoader.load(problemFile);
		
		Tour tour = new Tour(ttp);
		
		int[] stops = new int[] {1, 2, 3, 4, 5, 6, 7, 8};
		tour.update(stops);
		
		DynamicProgramming dp = new DynamicProgramming(tour);
		dp.dynamicProgramming();

		String record = problemFile + "\t" + dp.getMaxBenefit() + "\t"
				+ dp.getTotalWeight() + "\t" + dp.getTotalProfit()
				+ "\t" + dp.getElapsedTime() + "\t" + Arrays.toString(dp.getPackingPlan());
		System.out.println(record);
		
//		Assert.assertEquals("10000010000100000100001000000010100100000000000100", dp.getSolution());
		
		Assert.assertEquals(1988, dp.getTotalWeight());
		Assert.assertEquals(6329, dp.getTotalProfit());
		Assert.assertEquals(1840.2105818409516, dp.getMaxBenefit(), delta);
		Assert.assertEquals(1840.2105818409516, dp.Beta(), delta);
		
		
	}

	
	@Test
	public void testDpEil51N50Uncorr01() throws FileNotFoundException{
		String instance = "eil51_n50_uncorr_01.ttp";
		
		Tour tour = TtpLoader.load("experiments/eil51/" + instance,
				"experiments/eil51/eil51.linkern.tour");
		
		DynamicProgramming dp = new DynamicProgramming(tour);
		dp.dynamicProgramming();

		String record = instance + "\t" + dp.getMaxBenefit() + "\t"
				+ dp.getTotalWeight() + "\t" + dp.getTotalProfit()
				+ "\t" + dp.getElapsedTime() + "\t" + dp.getSolution();
		System.out.println(record);
		
		Assert.assertEquals("10000010000100000100001000000010100100000000000100", dp.getSolution());
		
		Assert.assertEquals(1988, dp.getTotalWeight());
		Assert.assertEquals(6329, dp.getTotalProfit());
		Assert.assertEquals(1840.2105818409516, dp.getMaxBenefit(), delta);
		Assert.assertEquals(1840.2105818409516, dp.Beta(), delta);
		
		
	}
	
	@Test
	public void testDpEil51N50Uncorr06() throws FileNotFoundException{
		String instance = "eil51_n50_uncorr_06.ttp";
		
		Tour tour = TtpLoader.load("experiments/eil51/" + instance,
				"experiments/eil51/eil51.linkern.tour");
		
		DynamicProgramming dp = new DynamicProgramming(tour);
		dp.dynamicProgramming();

		String record = instance + "\t" + dp.getMaxBenefit() + "\t"
				+ dp.getTotalWeight() + "\t" + dp.getTotalProfit()
				+ "\t" + dp.getElapsedTime() + "\t" + dp.getSolution();
		System.out.println(record);
		

		Assert.assertEquals(9552, dp.getTotalWeight());
		
		Assert.assertEquals(16959, dp.getTotalProfit());
		Assert.assertEquals("10100011011100111100101100100110100111011100100100", dp.getSolution());
		Assert.assertEquals(3316.671813514843, dp.getMaxBenefit(), delta);
		Assert.assertEquals(3316.671813514843, dp.Beta(), delta);
		

	}
	
	@Test
	public void testDpEil51N50Uncorr10() throws FileNotFoundException{
		String instance = "eil51_n50_uncorr_10.ttp";
		
		Tour tour = TtpLoader.load("experiments/eil51/" + instance,
				"experiments/eil51/eil51.linkern.tour");
		
		DynamicProgramming dp = new DynamicProgramming(tour);
		dp.dynamicProgramming();

		String record = instance + "\t" + dp.getMaxBenefit() + "\t"
				+ dp.getTotalWeight() + "\t" + dp.getTotalProfit()
				+ "\t" + dp.getElapsedTime() + "\t" + dp.getSolution();
		System.out.println(record);
		

		Assert.assertEquals(14468, dp.getTotalWeight());
		Assert.assertEquals(19957, dp.getTotalProfit());
		Assert.assertEquals("10100011111101111100101110101110110111111111101100", dp.getSolution());
		Assert.assertEquals(6129.6920328707465, dp.getMaxBenefit(), delta);
		Assert.assertEquals(6129.6920328707465, dp.Beta(), delta);
		
	}
	
	@Test
	public void testDpEil51N50UncorrSw01() throws FileNotFoundException{
		String instance = "eil51_n50_uncorr-similar-weights_01.ttp";
		
		Tour tour = TtpLoader.load("experiments/eil51/" + instance,
				"experiments/eil51/eil51.linkern.tour");
		
		DynamicProgramming dp = new DynamicProgramming(tour);
		dp.dynamicProgramming();

		String record = instance + "\t" + dp.getMaxBenefit() + "\t"
				+ dp.getTotalWeight() + "\t" + dp.getTotalProfit()
				+ "\t" + dp.getElapsedTime() + "\t" + dp.getSolution();
		System.out.println(record);
		
		
		Assert.assertEquals(4019, dp.getTotalWeight());
		Assert.assertEquals(3455, dp.getTotalProfit());
		Assert.assertEquals("00000000100010000000000000000010000001000000000000", dp.getSolution());
		Assert.assertEquals(1238.3329830255648, dp.getMaxBenefit(), delta);
		Assert.assertEquals(1238.3329830255648, dp.Beta(), delta);
	}
	
	@Test
	public void testDpEil51N50UncorrSw06() throws FileNotFoundException{
		String instance = "eil51_n50_uncorr-similar-weights_06.ttp";
		
		Tour tour = TtpLoader.load("experiments/eil51/" + instance,
				"experiments/eil51/eil51.linkern.tour");
		
		DynamicProgramming dp = new DynamicProgramming(tour);
		dp.dynamicProgramming();

		String record = instance + "\t" + dp.getMaxBenefit() + "\t"
				+ dp.getTotalWeight() + "\t" + dp.getTotalProfit()
				+ "\t" + dp.getElapsedTime() + "\t" + dp.getSolution();
		System.out.println(record);
		
		
		Assert.assertEquals(22088, dp.getTotalWeight());
		Assert.assertEquals(16454, dp.getTotalProfit());
		Assert.assertEquals("01010100110011000000000111010011001011010010011101", dp.getSolution());
		Assert.assertEquals(1933.0840010742143, dp.getMaxBenefit(), delta);
		Assert.assertEquals(1933.0840010742143, dp.Beta(), delta);
	}
	
	@Test
	public void testDpEil51N50UncorrSw10() throws FileNotFoundException{
		String instance = "eil51_n50_uncorr-similar-weights_10.ttp";
		
		Tour tour = TtpLoader.load("experiments/eil51/" + instance,
				"experiments/eil51/eil51.linkern.tour");
		
		DynamicProgramming dp = new DynamicProgramming(tour);
		dp.dynamicProgramming();

		String record = instance + "\t" + dp.getMaxBenefit() + "\t"
				+ dp.getTotalWeight() + "\t" + dp.getTotalProfit()
				+ "\t" + dp.getElapsedTime() + "\t" + dp.getSolution();
		System.out.println(record);
		
		
		Assert.assertEquals(33146, dp.getTotalWeight());
		Assert.assertEquals(21315, dp.getTotalProfit());
		Assert.assertEquals("01111100111111010010000111110011001011111011111101", dp.getSolution());
		Assert.assertEquals(5416.604558726433, dp.getMaxBenefit(), delta);
		Assert.assertEquals(5416.604558726433, dp.Beta(), delta);
		
	}
	
	@Test
	public void testDpEil51N50Corr01() throws FileNotFoundException{
		String instance = "eil51_n50_bounded-strongly-corr_01.ttp";
		
		Tour tour = TtpLoader.load("experiments/eil51/" + instance,
				"experiments/eil51/eil51.linkern.tour");
		
		DynamicProgramming dp = new DynamicProgramming(tour);
		dp.dynamicProgramming();

		String record = instance + "\t" + dp.getMaxBenefit() + "\t"
				+ dp.getTotalWeight() + "\t" + dp.getTotalProfit()
				+ "\t" + dp.getElapsedTime() + "\t" + dp.getSolution();
		System.out.println(record);
		

		Assert.assertEquals(4019, dp.getTotalWeight());
		Assert.assertEquals(6419, dp.getTotalProfit());
		Assert.assertEquals("11110000110000000110000000000010000000000010000011", dp.getSolution());
		Assert.assertEquals(3844.234524152193, dp.getMaxBenefit(), delta);
		Assert.assertEquals(3844.234524152193, dp.Beta(), delta);
		
	}
	
	@Test
	public void testDpEil51N50Corr06() throws FileNotFoundException{
		String instance = "eil51_n50_bounded-strongly-corr_06.ttp";
		
		Tour tour = TtpLoader.load("experiments/eil51/" + instance,
				"experiments/eil51/eil51.linkern.tour");
		
		DynamicProgramming dp = new DynamicProgramming(tour);
		dp.dynamicProgramming();

		String record = instance + "\t" + dp.getMaxBenefit() + "\t"
				+ dp.getTotalWeight() + "\t" + dp.getTotalProfit()
				+ "\t" + dp.getElapsedTime() + "\t" + dp.getSolution();
		System.out.println(record);
		

		Assert.assertEquals(21446, dp.getTotalWeight());
		Assert.assertEquals(27346, dp.getTotalProfit());
		Assert.assertEquals("11110000110001010111100100000011000111101011111111", dp.getSolution());
		Assert.assertEquals(5879.3806946447185, dp.getMaxBenefit(), delta);
		Assert.assertEquals(5879.3806946447185, dp.Beta(), delta);
		
	}
	
	@Test
	public void testDpEil51N50Corr10() throws FileNotFoundException{
		String instance = "eil51_n50_bounded-strongly-corr_10.ttp";
		
		Tour tour = TtpLoader.load("experiments/eil51/" + instance,
				"experiments/eil51/eil51.linkern.tour");
		
		DynamicProgramming dp = new DynamicProgramming(tour);
		dp.dynamicProgramming();
		
		String record = instance + "\t" + dp.getMaxBenefit() + "\t"
				+ dp.getTotalWeight() + "\t" + dp.getTotalProfit()
				+ "\t" + dp.getElapsedTime() + "\t" + dp.getSolution();
		System.out.println(record);


		Assert.assertEquals(33332, dp.getTotalWeight());
		Assert.assertEquals(41332, dp.getTotalProfit());
		Assert.assertEquals("11111000111111011111111101100011111111111111111111", dp.getSolution());
		Assert.assertEquals(9586.07178123069, dp.getMaxBenefit(), delta);
		Assert.assertEquals(9586.07178123069, dp.Beta(), delta);
		
	}

	
	@Test
	public void testDpEil51N250UncorrSw01() throws FileNotFoundException{
		String instance = "eil51_n250_uncorr-similar-weights_01.ttp";
		
		Tour tour = TtpLoader.load("experiments/eil51/" + instance,
				"experiments/eil51/eil51.linkern.tour");
		
		DynamicProgramming dp = new DynamicProgramming(tour);
		dp.dynamicProgramming();

		String record = instance + "\t" + dp.getMaxBenefit() + "\t"
				+ dp.getTotalWeight() + "\t" + dp.getTotalProfit()
				+ "\t" + dp.getElapsedTime() + "\t" + dp.getSolution();
		System.out.println(record);
		

		Assert.assertEquals(22111, dp.getTotalWeight());
		Assert.assertEquals(18869, dp.getTotalProfit());
		Assert.assertEquals("0001000010001000000000000000001000000100000000000000000000010101010100000000000011000000100000000100000000000001000000000000000000100000000000000000000001000010000000000000000000000000001100000000000000000000000000000000000000000010000000000000000100", dp.getSolution());
		Assert.assertEquals(5450.89157675745, dp.getMaxBenefit(), delta);
		Assert.assertEquals(5450.89157675745, dp.Beta(), delta);
		
	}
	
	@Test
	public void testDpEil51N250UncorrSw06() throws FileNotFoundException{
		String instance = "eil51_n250_uncorr-similar-weights_06.ttp";
		
		Tour tour = TtpLoader.load("experiments/eil51/" + instance,
				"experiments/eil51/eil51.linkern.tour");
		
		DynamicProgramming dp = new DynamicProgramming(tour);
		dp.dynamicProgramming();
		
		String record = instance + "\t" + dp.getMaxBenefit() + "\t"
				+ dp.getTotalWeight() + "\t" + dp.getTotalProfit()
				+ "\t" + dp.getElapsedTime() + "\t" + dp.getSolution();
		System.out.println(record);


		Assert.assertEquals(112513, dp.getTotalWeight());
		Assert.assertEquals(82185, dp.getTotalProfit());
		Assert.assertEquals("0101010011001100000000011101001100101101001001110100011001111101011111000000000111000110111111100110000000100101000101100110000001101100101010000111000001101010011000100001010000001001001111101101000100110010011111010000010011001110001111100111100100", dp.getSolution());
		Assert.assertEquals(15632.631808893982, dp.getMaxBenefit(), delta);
		Assert.assertEquals(15632.631808893982, dp.Beta(), delta);
	}
	
	@Test
	public void testDpEil51N250UncorrSw10() throws FileNotFoundException{
		String instance = "eil51_n250_uncorr-similar-weights_10.ttp";
		
		Tour tour = TtpLoader.load("experiments/eil51/" + instance,
				"experiments/eil51/eil51.linkern.tour");
		
		DynamicProgramming dp = new DynamicProgramming(tour);
		dp.dynamicProgramming();
		
		String record = instance + "\t" + dp.getMaxBenefit() + "\t"
				+ dp.getTotalWeight() + "\t" + dp.getTotalProfit()
				+ "\t" + dp.getElapsedTime() + "\t" + dp.getSolution();
		System.out.println(record);

		
		Assert.assertEquals(166747, dp.getTotalWeight());
		Assert.assertEquals(108165, dp.getTotalProfit());
		Assert.assertEquals("0111110011101101001000011111001100101111101111110101011101111101011111110001001111010110111111110110101000101101100101111110000111111111111011010111000001111011011001111101010010111111011111101101110111110010011111011000110011001111101111111111100100", dp.getSolution());
		Assert.assertEquals(26536.640417284227, dp.getMaxBenefit(), delta);
		Assert.assertEquals(26536.640417284227, dp.Beta(), delta);
	}
	
	
	@Test
	public void testDpEil76N75Uncorr01() throws FileNotFoundException{
		String instance = "eil76_n75_uncorr_01.ttp";
		
		Tour tour = TtpLoader.load("experiments/eil76/" + instance,
				"experiments/eil76/eil76.linkern.tour");
		
		DynamicProgramming dp = new DynamicProgramming(tour);
		dp.dynamicProgramming();
		
		String record = instance + "\t" + dp.getMaxBenefit() + "\t"
				+ dp.getTotalWeight() + "\t" + dp.getTotalProfit()
				+ "\t" + dp.getElapsedTime() + "\t" + dp.getSolution();
		System.out.println(record);

		
		Assert.assertEquals(3320, dp.getTotalWeight());
		Assert.assertEquals(10430, dp.getTotalProfit());
		Assert.assertEquals("101000110001001001000010000000001001000000000000001000000000011000000000011", dp.getSolution());
		Assert.assertEquals(3727.390754703563, dp.getMaxBenefit(), delta);
		Assert.assertEquals(3727.390754703563, dp.Beta(), delta);
	}
	
	@Test
	public void testDpEil76N75Uncorr06() throws FileNotFoundException{
		String instance = "eil76_n75_uncorr_06.ttp";
		
		Tour tour = TtpLoader.load("experiments/eil76/" + instance,
				"experiments/eil76/eil76.linkern.tour");
		
		DynamicProgramming dp = new DynamicProgramming(tour);
		dp.dynamicProgramming();
		
		String record = instance + "\t" + dp.getMaxBenefit() + "\t"
				+ dp.getTotalWeight() + "\t" + dp.getTotalProfit()
				+ "\t" + dp.getElapsedTime() + "\t" + dp.getSolution();
		System.out.println(record);

		
		Assert.assertEquals(16295, dp.getTotalWeight());
		Assert.assertEquals(26542, dp.getTotalProfit());
		Assert.assertEquals("101000110111001111001010101111111001010011101011001011001010011011000010111", dp.getSolution());
		Assert.assertEquals(6174.836888772153, dp.getMaxBenefit(), delta);
		Assert.assertEquals(6174.836888772153, dp.Beta(), delta);
	}
	
	@Test
	public void testDpEil76N75Uncorr10() throws FileNotFoundException{
		String instance = "eil76_n75_uncorr_10.ttp";
		
		Tour tour = TtpLoader.load("experiments/eil76/" + instance,
				"experiments/eil76/eil76.linkern.tour");
		
		DynamicProgramming dp = new DynamicProgramming(tour);
		dp.dynamicProgramming();

		String record = instance + "\t" + dp.getMaxBenefit() + "\t"
				+ dp.getTotalWeight() + "\t" + dp.getTotalProfit()
				+ "\t" + dp.getElapsedTime() + "\t" + dp.getSolution();
		System.out.println(record);
		

		Assert.assertEquals(25310, dp.getTotalWeight());
		Assert.assertEquals(32851, dp.getTotalProfit());
		Assert.assertEquals("111010110111001111111011101111111101011011101011011011001011111011101111111", dp.getSolution());
		Assert.assertEquals(8825.005876110024, dp.getMaxBenefit(), delta);
		Assert.assertEquals(8825.005876110024, dp.Beta(), delta);
		
	}
	
	@Test
	public void testDpEil101N1000UncorrSw10() throws FileNotFoundException{
		String instance = "eil101_n1000_uncorr-similar-weights_10.ttp";
		
		Tour tour = TtpLoader.load("experiments/eil101/" + instance,
				"experiments/eil101/eil101.linkern.tour");
		
		DynamicProgramming dp = new DynamicProgramming(tour);
		dp.dynamicProgramming();

		String record = instance + "\t" + dp.getMaxBenefit() + "\t"
				+ dp.getTotalWeight() + "\t" + dp.getTotalProfit()
				+ "\t" + dp.getElapsedTime() + "\t" + dp.getSolution();
		System.out.println(record);
		

		Assert.assertEquals(641927, dp.getTotalWeight());
		Assert.assertEquals(427233, dp.getTotalProfit());
		Assert.assertEquals("0101110011101101101000011111111100101101101011110101001101111101011111110001001111111110111111111110100001101101101101111110001111101111001011011111001001111110011010111101010010100011001111101101100111111010011111111000110011001110101111100111101100100001011100111111100101000100111011111011111101101001011111011100100110010001111110111011111011111011010000111101010010010010101011010011111111000000010111101001001111111001110111111011011101101111001011110101110010111110101100011111101101001001011000001110110101000101000010111111111101110101110111100101011111011011100101000001110110011001001110111111111011100001110111011100111011111101001010011110111011111110010110101000111110111111110111011000001111101110010001110111110111110110001000111111011000101110111110111110100000111100101010101101111011001011111111011111111011001110011001010101111000110011110011111011111010000110110101011110101101110011110111111111111101100101111111110011011111111001100001100111011111100111010011010110100101100101", dp.getSolution());
		Assert.assertEquals(103572.407430488, dp.getMaxBenefit(), delta);
		Assert.assertEquals(103572.407430488, dp.Beta(), delta);
	}
	
	
	@Test
	public void testDpEil101N500UncorrSw10() throws FileNotFoundException{
		String instance = "eil101_n500_uncorr-similar-weights_10.ttp";
		
		Tour tour = TtpLoader.load("experiments/eil101/" + instance,
				"experiments/eil101/eil101.linkern.tour");
		
		DynamicProgramming dp = new DynamicProgramming(tour);
		dp.dynamicProgramming();

		String record = instance + "\t" + dp.getMaxBenefit() + "\t"
				+ dp.getTotalWeight() + "\t" + dp.getTotalProfit()
				+ "\t" + dp.getElapsedTime() + "\t" + dp.getSolution();
		System.out.println(record);
		
		Assert.assertEquals(50836.65881680791, dp.getMaxBenefit(), delta);
		Assert.assertEquals(50836.65881680791, dp.Beta(), delta);
		
		Assert.assertEquals(320484, dp.getTotalWeight());
		Assert.assertEquals(211591, dp.getTotalProfit());
		Assert.assertEquals("01011100111011011010000111111111001011011010111101010011011111010111111100010011111111101111111111101000011011011011011111100011111011110010110111110010011111100110101111010100101000110011111011011001111110100111111110001100110011101011111001111011001000010111001111111001010001001110111110111111011010010111110111001011100100011111101110111110111110110100001111010100100100101010110100111111110000000101111010010011111110011101111110110111011011110010111101011100101111101011000111111011010010010110", dp.getSolution());

	}
	
	
	@Test
	public void testDpEil101N100Uncorr01() throws FileNotFoundException{
		String instance = "eil101_n100_uncorr_01.ttp";
		
		Tour tour = TtpLoader.load("experiments/eil101/" + instance,
				"experiments/eil101/eil101.linkern.tour");
		
		DynamicProgramming dp = new DynamicProgramming(tour);
		dp.dynamicProgramming();

		String record = instance + "\t" + dp.getMaxBenefit() + "\t"
				+ dp.getTotalWeight() + "\t" + dp.getTotalProfit()
				+ "\t" + dp.getElapsedTime() + "\t" + dp.getSolution();
		System.out.println(record);
		
		Assert.assertEquals(1651.696980924922, dp.getMaxBenefit(), delta);
		Assert.assertEquals(1651.696980924922, dp.Beta(), delta);
		
		Assert.assertEquals(4221, dp.getTotalWeight());
		Assert.assertEquals(13296, dp.getTotalProfit());
		Assert.assertEquals("1000001000010010110000100000011010010000000000010010000000000010000010000110100000000011000000000000", dp.getSolution());

	}
	
	@Test
	public void testDpEil101N100Uncorr06() throws FileNotFoundException{
		String instance = "eil101_n100_uncorr_06.ttp";
		
		Tour tour = TtpLoader.load("experiments/eil101/" + instance,
				"experiments/eil101/eil101.linkern.tour");
		
		DynamicProgramming dp = new DynamicProgramming(tour);
		dp.dynamicProgramming();

		String record = instance + "\t" + dp.getMaxBenefit() + "\t"
				+ dp.getTotalWeight() + "\t" + dp.getTotalProfit()
				+ "\t" + dp.getElapsedTime() + "\t" + dp.getSolution();
		System.out.println(record);
		
		Assert.assertEquals(10155.494217792017, dp.getMaxBenefit(), delta);
		Assert.assertEquals(10155.494217792017, dp.Beta(), delta);
		
		Assert.assertEquals(22154, dp.getTotalWeight());
		Assert.assertEquals(34544, dp.getTotalProfit());
		Assert.assertEquals("1010011101010011110010111010111010010000110010110010110000111110100110111110100001100111100001001011", dp.getSolution());

	}
	
	@Test
	public void testDpEil101N100Uncorr10() throws FileNotFoundException{
		String instance = "eil101_n100_uncorr_10.ttp";
		
		Tour tour = TtpLoader.load("experiments/eil101/" + instance,
				"experiments/eil101/eil101.linkern.tour");
		
		DynamicProgramming dp = new DynamicProgramming(tour);
		dp.dynamicProgramming();

		String record = instance + "\t" + dp.getMaxBenefit() + "\t"
				+ dp.getTotalWeight() + "\t" + dp.getTotalProfit()
				+ "\t" + dp.getElapsedTime() + "\t" + dp.getSolution();
		System.out.println(record);
		
		Assert.assertEquals(10297.713425303893, dp.getMaxBenefit(), delta);
		Assert.assertEquals(10297.713425303893, dp.Beta(), delta);
		
		Assert.assertEquals(31265, dp.getTotalWeight());
		Assert.assertEquals(41374, dp.getTotalProfit());
		Assert.assertEquals("1010111111110011110010111011111011011001110110110010110010111110110110111111100101100111101011011011", dp.getSolution());

	}
	
}
