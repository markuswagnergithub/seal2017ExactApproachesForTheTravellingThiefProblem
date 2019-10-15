package ttp.problem;

public final class Item {
    public final int index;
    public final long profit;
    public final long weight;
	public final int cityIdx;
    
	
	@Override
	public String toString(){
    	StringBuilder sb = new StringBuilder();
    	sb.append("item:" + index);
    	sb.append("\tprofit:" + profit);
    	sb.append("\tweight:" + weight);
    	
    	return sb.toString();
	}
	
	public Item(int index, long profit, long weight, int cityIdx){
		this.index = index;
		this.profit = profit;
		this.weight = weight;
		this.cityIdx = cityIdx;
	}
	
	public Item(Item old){
		this.index = old.index;
		this.profit = old.profit;
		this.weight = old.weight;
		this.cityIdx = old.cityIdx;
	}
}
