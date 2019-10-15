package pwt.exact.dp;

import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.TreeSet;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import ttp.problem.Tour;
import ttp.problem.Ttp;

public class DpEnhanced {
	public final Ttp ttp;
	private final int[] stops;
	private final long[] adjacentDistances;
	
	
	private Long totalWeight = null;
	private BitSet solution = null;
	private double beta = 0;
	
	private HashMap<Long, PackingPlan> curCol = null;
	
	private long elapsedTime;
	
	public DpEnhanced(Ttp ttp, int[] stops) {
		this.ttp = ttp;
		this.stops = stops;
		adjacentDistances = new long[ttp.n];
		Tour.updateAdjacentDistances(adjacentDistances, ttp, stops);	
	}
	
	public void dynamicProgramming() {
		final long start = System.nanoTime();
		
		LookupTable table = new LookupTable(ttp, stops);
		
		final int firstStopIdx = 0;
		HashMap<Long, PackingPlan> preCol = new HashMap<>(table.baseTable.get(stops[firstStopIdx]));
		
		for (int i = 1; i < stops.length; i++) {
			curCol = new HashMap<>();
			NavigableMap<Long, PackingPlanExtra> mapzero = table.baseTable.get(stops[i]);			
			for (long w:preCol.keySet()) {
				PackingPlan prep = preCol.get(w);
//				System.out.println("distance:" + adjacentDistances[i]);
				double best = - Double.MAX_VALUE;
				for (PackingPlanExtra p: mapzero.values()) {
					long newW = w + p.weight;
					if (newW > ttp.bigW) break;

					double b = prep.beta + ttp.benefit(p.profit, newW, adjacentDistances[i]);
					if (b < best) {
						continue;
					}else {
						best = b;
					}

					PackingPlan existedP = curCol.get(newW);
					
					if (existedP == null || existedP.beta < b) {
						BitSet bs = p.solution();
						bs.or(prep.solution());
						curCol.put(newW, new PackingPlan(b, bs));
					}
				}
			}
			
			NavigableSet<Long> treeSet = new TreeSet<>(curCol.keySet());
			double b = - Double.MAX_VALUE;
			for (long key : treeSet) {
				if (curCol.get(key).beta < b) {
					curCol.remove(key);
				} 
				else {
					b = curCol.get(key).beta;
				}
			}

			preCol = curCol;
			
//			System.out.println("------------");
//			System.out.println("No." + i + " stop:" + stops[i]);
//			Util.printMap(curCol);

		}
		
		elapsedTime = TimeUnit.MILLISECONDS.convert(
				System.nanoTime() - start, TimeUnit.NANOSECONDS);

	}

	
	public String getSolution() {
		if (totalWeight == null) {
			getTotalWeight();
		}
		return solutionToBitString();
	}
	
	public int[] getPackingPlan(){
		if (totalWeight == null) {
			getTotalWeight();
		}
		
		return solution.stream().toArray();
	}

	public long getTotalProfit() {
		if (totalWeight == null) {
			getTotalWeight();
		}
		return totalProfitInSolution();
	}

	public long getTotalWeight() {
		if (totalWeight != null) {
			return totalWeight;
		}

		double maxBenefit = - Double.MAX_VALUE;
		long totalW = -1;
		for (Entry<Long, PackingPlan> entry : curCol.entrySet()) {
			if (entry.getValue().beta > maxBenefit) {
				maxBenefit = entry.getValue().beta;
				totalW = entry.getKey();
			}
		}
		totalWeight = totalW;
		solution = curCol.get(totalWeight).solution();
		beta = maxBenefit;
		return totalWeight;
	}

	public double getMaxBenefit() {
		if (totalWeight == null) {
			getTotalWeight();
		}

		return ttp.benefit(stops, solution);
	}

	public String getElapsedTime() {
		return String.format("%.2fsec.", elapsedTime/1000.0);
	}

	public int getNoOfEntries() {
		return curCol.size();
	}


	private long totalProfitInSolution() {
		return ttp.totalProfit(solution);
	}
	
	
	public BitSet getSolutionBitSet(){
		return (BitSet)solution.clone();
	}

	private String solutionToBitString() {
		StringBuilder sb = new StringBuilder();
		for (int i = 1; i <= ttp.m; i++) {
			if (solution.get(i)) {
				sb.append('1');
			} else {
				sb.append('0');
			}
		}
		return sb.toString();
	}

	
	public double Beta(){
		if (totalWeight == null) {
			getTotalWeight();
		}
		return beta;
	}

	public Map<Long, PackingPlan> getFinalColumn() {
		return this.curCol;
	}
	
}
