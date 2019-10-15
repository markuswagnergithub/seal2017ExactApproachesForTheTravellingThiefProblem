package ttp.moea;

import java.util.Arrays;
import java.util.stream.IntStream;

import org.junit.Test;

public class HeuristicTest {

	@Test
	public void testMpx() {
		int[] donor = new int[] { 1, 3, 4, 5, 8, 9, 6, 2, 7 };
		int[] receiver = new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 };

		System.out.println(Arrays.toString(donor));
		System.out.println(Arrays.toString(receiver));

		int[] offspring = Operators.mpx(donor, receiver);
		System.out.println(Arrays.toString(offspring));
	}

	@Test
	public void testCrossoverSurface() {

		IntStream.iterate(0, i -> i + 2).limit(10 / 2).parallel().forEach((i) -> {
			System.out.println(i);

		});
	}


}
