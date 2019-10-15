package pwt.exact.dp;

import java.util.BitSet;

public class PackingPlanExtra extends pwt.exact.dp.PackingPlan{
	public final long profit;
	public final long weight;
	
	public PackingPlanExtra(PackingPlanExtra old) {
		this(old.beta, old.solution(), old.profit, old.weight);
	}
	
	public PackingPlanExtra(double benefit, BitSet packingPlan, long profit, long weight) {
		super(benefit, packingPlan);
		this.profit = profit;
		this.weight = weight;
	}

	@Override
	public String toString() {
		return "PackingPlan [profit=" + profit + ", weight=" + weight + ", benefit=" + beta + ", packingPlan="
				+ solution() + "]";
	}
}
