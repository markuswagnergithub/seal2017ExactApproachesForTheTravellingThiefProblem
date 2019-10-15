package pwt.exact.dp;

import java.util.BitSet;

public class PackingPlan {
	public final double beta;
	private final BitSet solution;
	
	
	public PackingPlan(double beta, BitSet solution){
		this.beta = beta;
		this.solution = solution;
	}
	
	public BitSet solution(){
		return (BitSet)solution.clone();
	}

	@Override
	public String toString() {
		return "PackingPlan [beta=" + beta + ", solution=" + solution + "]";
	}
	
}
