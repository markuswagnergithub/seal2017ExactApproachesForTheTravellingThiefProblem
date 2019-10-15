package ttp.dp;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;

public class CitySet {
	private final long total;
	private final long noOfSet;
	private final int start;
	private BigInteger set;
	private long no;
	
	public static long binomial(final int n, final int k) {
		double ret = 1;
		for (int i = 1; i <= k; i++) {
			ret = ret * ((n - k + i) / (double) i);
		}
		return (long) ret;
	}
	
	public static BigInteger allSet(int n){
		return BigInteger.valueOf((long)Math.pow(2, n) - 1);
	}
	
	public CitySet(int total, int chosen, int start){
		this.total = total;
		this.set = allSet(chosen);
		this.noOfSet = binomial(total, chosen);
		this.start = start;
		this.no = 1; //count starts from 1
	}
	
	
	public Set<Integer> getCitySet(){
		Set<Integer> ret = new HashSet<>();
		for (int i=0;i < total;i++){ //S is a subset of {2, ..., n}
			if (set.testBit(i)){
				ret.add(i + start); //add city
			}
		}
		return ret;
	}
	
	public boolean hasNext(){
		if (no <=  noOfSet){
			return true;
		}else{
			return false;
		}
	}
	
	public BigInteger substract(int cityIdx){
		return set.clearBit(cityIdx - start);
	}
	
	public BigInteger next(){
		if (no > 1){
			set = BigInteger.valueOf(nextComb(set.longValue()));
		}
		no++;
		return set;
	}

	
	
/*
 * Compute the lexicographically next bit permutation
 * http://graphics.stanford.edu/~seander/bithacks.html#NextBitPermutation
 * 
 * 
 * Suppose we have a pattern of N bits set to 1 in an integer and we want 
 * the next permutation of N 1 bits in a lexicographical sense. For example, 
 * if N is 3 and the bit pattern is 00010011, the next patterns would be 
 * 00010101, 00010110, 00011001,00011010, 00011100, 00100011, and so forth. 
 * The following is a fast way to compute the next permutation.
 * 
 * unsigned int v; // current permutation of bits
 * unsigned int w; // next permutation of bits
 * 
 * unsigned int t = (v | (v - 1)) + 1;  
 * w = t | ((((t & -t) / (v & -v)) >> 1) - 1);  
 * Thanks to Dario Sneidermanis of Argentina, who provided this on November 28, 2009.
 * 
 */
	public long nextComb(long v){
		long t = (v | (v - 1)) + 1;  
		return t | ((((t & -t) / (v & -v)) >> 1) - 1);
	}
		
		
	
}
