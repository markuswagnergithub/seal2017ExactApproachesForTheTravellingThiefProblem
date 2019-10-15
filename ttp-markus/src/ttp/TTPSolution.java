package ttp;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author wagner
 */
public class TTPSolution implements Serializable {
    
    public int[] tspTour;
    public int[] packingPlan;
    
/* "finalProfit" final profit gained form the picked items,
   "finalTravelTime" the time takes to finish the tour (including changes of the speed),
   "objectiveScore" objective value (the number that is actually of interest),
   "finalWeight" weight of the knapsack at the end of the tour   
*/
    public double finalProfit   = Double.NEGATIVE_INFINITY;
    public double finalTravelTime   = Double.POSITIVE_INFINITY;
    public long finalDistance   = Long.MAX_VALUE;
    public double objectiveScore   = Double.NEGATIVE_INFINITY;
    public double finalCapacityFree = Double.POSITIVE_INFINITY;
    public double finalWeight = Double.POSITIVE_INFINITY;
    public long computationTime = Long.MAX_VALUE;
    
    public TTPSolution(int[] tspTour, int[] packingPlan) {
        this.tspTour = tspTour;
        this.packingPlan = packingPlan;
    }
    
    public double getObjective() {
        return this.objectiveScore;
    }
    
    public void reset() {
        finalProfit = Double.NEGATIVE_INFINITY;
        finalTravelTime = Double.POSITIVE_INFINITY;
        finalDistance = Long.MAX_VALUE;
        objectiveScore = Double.NEGATIVE_INFINITY;
        finalCapacityFree = Double.POSITIVE_INFINITY;
        finalWeight = Double.POSITIVE_INFINITY;
        computationTime = Long.MAX_VALUE;
    }
    
    /* answer() and writeResult(String file) are necessary for the participation in the competition.
     * 
     * Text from http://cs.adelaide.edu.au/~optlog/CEC2014Comp/ follows:
     * - Output: the file with the result for a single iteration should follow the following naming convention: 
     *           <ttpfile>.<algorithmname>.<systemtime>, e.g.,
     *           a280_n279_bounded-strongly-corr_02.ttp.thisIsMyAlgorithm.12345678912
     * - The file needs to contain the following information as comma-separated values in square brackets: 
     *           the permutation of the cities in the first line (note: the first city is "1", do not print the "1" at the end), and 
     *           the list of packed items in the second line (the numbering of the items starts with "1"). 
     *           For example:
     *                  [1,5,4,2,3]
     *                  [20,113]
     *           which means that only the items with the numbers 20 and 113 are picked, and the sequence of visited cities is <1,5,4,2,3,1>. 
     *           Note that this format can easily be achieved in Java with the function Arrays.toString(...). 
     */
    public String answer() {
    	int[] tourOut = new int[tspTour.length - 1];
    	for (int i = 0; i < tspTour.length - 1; i++){
    		tourOut[i] = tspTour[i] + 1;
    	}
    	
    	int itemsPerCity = packingPlan.length / (tspTour.length  -2);
    	
    	List<Integer> packingPlanList = new ArrayList<Integer>();
    	int packingPlanIndex = 0;
    	for (int i = 1; i < tspTour.length - 1; i++){
    		int city = tspTour[i];
    		for (int j = 0; j < itemsPerCity; j++){
    			if (packingPlan[packingPlanIndex] == 1){
    				int itemIndex = j * (tspTour.length  -2) + city - 1;
    				int itemIndexFrom1 = itemIndex + 1;
    				packingPlanList.add(itemIndexFrom1 );
    			}
    			packingPlanIndex++;
    		}	    	
    	}   	
    	Collections.sort(packingPlanList);
    	
    	int[] packingOut = new int[packingPlanList.size()];
    	for (int i = 0; i < packingPlanList.size(); i++){
    		packingOut[i] = packingPlanList.get(i);
    	}
        return Arrays.toString(tourOut) + "\r\n" + Arrays.toString(packingOut) +  "\r\n";
    }
    
    public void writeResult(String title){
            BufferedWriter writer = null;
            try {
                    writer = new BufferedWriter(new FileWriter(title, false));
                    writer.write(this.answer());
                    writer.flush();
                    writer.close();
            } catch (IOException e) {
                    e.printStackTrace();

            }
    }
    
    
    // the following print functions print varying levels of detail
    public void print() {
    	System.out.print(finalCapacityFree+" "+finalWeight+" "+finalProfit+" "+finalDistance+" "+finalTravelTime+" "+objectiveScore +" "+computationTime);
    }
    public void println() {
        this.print();
        System.out.println();
    }
    public void altPrint(){
    	System.out.println();
    	System.out.println(String.format("%-30s= %.2f" , "Leftover Knapsack Capacity", finalCapacityFree));
    	System.out.println(String.format("%-30s= %.2f" , "Total weight of items", finalWeight));
    	System.out.println(String.format("%-30s= %.2f" , "Total profit", finalProfit));
    	System.out.println(String.format("%-30s= %d" , "Total time (raw)", finalDistance));
    	System.out.println(String.format("%-30s= %.2f" , "Total time", finalTravelTime));
    	System.out.println(String.format("%-30s= %.2f" , "ob", objectiveScore));
    	System.out.println(String.format("%-30s= %d" , "Computation time (ms)", computationTime));
    }
    public void printFull() {
        this.println();
        System.out.println("tspTour "+Arrays.toString(tspTour));
        System.out.println("packingPlan "+Arrays.toString(packingPlan));
    }
    public void printFullForCCode() {
        System.out.print("[");
        for (int i=0; i<tspTour.length-2; i++) {
            System.out.print((tspTour[i]+1)+",");
        }
        System.out.println((tspTour[tspTour.length-2]+1)+"]");
        
        System.out.print("[");
        for (int i=0; i<packingPlan.length-1; i++) {
            if (packingPlan[i]==1) {
                System.out.print((i+1)+",");
            }
        }
        if (packingPlan[packingPlan.length-1]==1) {
                System.out.println(packingPlan.length+"]");
        } else {
            System.out.println("]");
        }
    }

}
