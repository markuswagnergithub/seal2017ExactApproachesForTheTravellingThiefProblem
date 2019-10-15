package ttp.newrep;

import java.util.List;

public class Item {
	public int weight;
	public int profit;
	public boolean isSelected;
	public int itemId;
	public double[] profitPerWc;
	
	public Item(int profit, int weight, int itemId){
		this.profit = profit;
		this.weight = weight;
		this.itemId = itemId;
		this.isSelected = false;
	}
}
