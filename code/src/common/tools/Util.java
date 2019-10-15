package common.tools;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class Util {
	
	public static final int[] toArray(List<Integer> list){
		if (list == null) return null;
		return list.stream().mapToInt(Integer::intValue).toArray();
	}
	
	public static final List<Integer> asList(int[] array){
		if (array == null) return null;
		return IntStream.of(array).boxed().collect(Collectors.toList());
	}
	
	

	public static <T> void reverse(final ArrayList<T> x, final int from, final int to) {
		for (int i = from, j = to; i < j; i++, j--) {
			final T tmp = x.get(i);
			x.set(i, x.get(j));
			x.set(j, tmp);
		}
	}
	
	public static void reverse(final int[] x, final int from, final int to) {
		for (int i = from, j = to; i < j; i++, j--) {
			final int tmp = x[i];
			x[i] = x[j];
			x[j] = tmp;
		}
	}
	
	public static void swap(final int[] x, final int city1, final int city2) {
		final int tmp = x[city2];
		x[city2] = x[city1];
		x[city1] = tmp;
	}
	
	public static <T> void swap(final T[] x, final int city1, final int city2) {
		final T tmp = x[city2];
		x[city2] = x[city1];
		x[city1] = tmp;
	}
	
	public static <T> void shuffle(T[] array) {
		final Random rd = ThreadLocalRandom.current();
		for (int i = array.length; i > 1; i--) {
			swap(array, i - 1, rd.nextInt(i));
		}
	}
	
	public static void shuffle(int[] array) {
		final Random rd = ThreadLocalRandom.current();
		for (int i = array.length; i > 1; i--) {
			swap(array, i - 1, rd.nextInt(i));
		}
	}
	
	public static List<Integer> randomPermutation(int size){
		ArrayList<Integer> res = new ArrayList<>(size);
	
		for (int i = 2; i <= size; i++) {
			res.add(i);
		}
		Collections.shuffle(res, ThreadLocalRandom.current());
		
		res.add(0, 1);
		
		return res;
	}
	
	public static void jump(final int[] x, final int prev, final int dest) {
		final int tmp = x[dest];
		if (prev < dest) {
			for (int i = dest; i > prev + 1; i--) {
				x[i] = x[i - 1];
			}
			x[prev + 1] = tmp;
		} else {
			for (int i = dest; i < prev; i++) {
				x[i] = x[i + 1];
			}
			x[prev] = tmp;
		}
	}
	
	public static String uniqueId(){
		
		return UUID.randomUUID().toString();
	}
	
	//shift the permutation starting from the city 1;
	public static int[] align(int[] permutation){
		if (permutation[0] == 1){
			return permutation;
		}
		
		int idxOfStart = 0;
		for (int i=0;i<permutation.length;i++){
			if (permutation[i] == 1){
				 idxOfStart = i;
				 break;
			}
		}
		
		int[] res = new int[permutation.length];
		System.arraycopy(permutation, idxOfStart, res, 0, permutation.length - idxOfStart);
		System.arraycopy(permutation, 0, res, permutation.length - idxOfStart, idxOfStart);
		return res;	
	}
	
	public static String padZero(String src, int length){
		return String.format("%" + length + "s", src).replace(' ', '0');
	}
	
	public static <K, V> void printMap(Map<K, V> map) {
		StringBuilder sb = new StringBuilder();
		for (K k : map.keySet()) {
			sb.append("<" + k + ":" + map.get(k) + ">" + System.lineSeparator());
		}
		System.out.print(sb);
	}

	public static BitSet bitSet(int[] is) {
		BitSet bitSet = new BitSet();
		
		for (int i:is) {
			bitSet.set(i);
		}
		
		return bitSet;
	}
	
	public static BitSet bitSet(String bitString) {
		BitSet bitSet = new BitSet();
		
		for (int i = 0;i<bitString.length();i++) {
			if (bitString.charAt(i) == '1') {
				bitSet.set(i + 1);
			}
		}
		
		return bitSet;
	}
	
	public static <T, U> List<U> convertList(List<T> from, Function<T, U> func) {
	    return from.parallelStream().map(func).collect(Collectors.toList());
	}
	
}
