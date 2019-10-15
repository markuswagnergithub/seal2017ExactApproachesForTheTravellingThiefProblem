package ttp.problem;

import java.math.BigDecimal;

import org.junit.Test;

public class ProblemGeneratorTest {

	@Test
	public void testGenerate() {
		
		int noOfCities = 10;
		IntRange xRange = new IntRange(1, 100);
		IntRange yRange = new IntRange(1, 100);
		IntRange noOfItemsRange = new IntRange(10, 100);
		IntRange weightRange = new IntRange(1, 1000);
		IntRange profitRange = new IntRange(1, 1000);
		
		BigDecimal rentingRate = new BigDecimal("1");
		BigDecimal minSpeed = new BigDecimal("0.1");
		BigDecimal maxSpeed = new BigDecimal("1");
		
		TtpGenerator pg = new TtpGenerator(noOfCities, xRange, yRange, noOfItemsRange, weightRange, profitRange,
				rentingRate, minSpeed, maxSpeed);
		
		for (int i=0;i<1;i++){
			System.out.println(TtpSaver.convert2TTPString(pg.generate()));
		}
		
	}
	
	@Test
	public void testGenerateUniformlyDistributed() {
		
		int noOfCities = 10;
		IntRange xRange = new IntRange(1, 100);
		IntRange yRange = new IntRange(1, 100);
		IntRange weightRange = new IntRange(1, 1000);
		IntRange profitRange = new IntRange(1, 1000);
		
		BigDecimal rentingRate = new BigDecimal("1");
		BigDecimal minSpeed = new BigDecimal("0.1");
		BigDecimal maxSpeed = new BigDecimal("1");
		
		final int[] noOfItemsFactor = new int[] { 1, 3, 5, 10 };
		
		TtpGenerator pg = new TtpGenerator(noOfCities, xRange, yRange, noOfItemsFactor, weightRange, profitRange,
				rentingRate, minSpeed, maxSpeed);
		
		for (int i=0;i<10;i++){
			System.out.println(TtpSaver.convert2TTPString(pg.generate()));
		}
		
	}
	
	@Test
	public void testGenerateAllTypes() {
		
		int noOfCities = 10;
		IntRange xRange = new IntRange(1, 100);
		IntRange yRange = new IntRange(1, 100);
		IntRange weightRange = new IntRange(1, 1000);
		IntRange profitRange = new IntRange(1, 1000);
		
		BigDecimal rentingRate = new BigDecimal("1");
		BigDecimal minSpeed = new BigDecimal("0.1");
		BigDecimal maxSpeed = new BigDecimal("1");
		
		final int[] noOfItemsFactor = new int[] { 5 };
		final int[] capacityCategory = new int[] { 1, 5, 10 };
		
		TtpGenerator pg = new TtpGenerator(noOfCities, xRange, yRange, noOfItemsFactor, weightRange, profitRange,
				rentingRate, minSpeed, maxSpeed);
		
		for (Ttp p:pg.generateAllType("test", capacityCategory)){
			System.out.println(TtpSaver.convert2TTPString(p));
		}
		
		
	}

}
