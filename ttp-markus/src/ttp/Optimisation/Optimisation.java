package ttp.Optimisation;

import java.io.*;
import java.util.Comparator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import ttp.TTPInstance;
import ttp.TTPSolution;
import ttp.Utils.DeepCopy;
import ttp.Utils.readTours;
import ttp.newrep.City;
import ttp.newrep.Individual;

/**
 *
 * @author wagner, faulkner, schultz
 */
public class Optimisation {
    
    private static Random rnd = null;
    public static void setRandomNumberSeed(int seed) {
        rnd = new Random(seed);
    }
	
	/**
	 * Simple Heuristic implementation from GECCO 2013 paper (similar)
	 *
	 * 
	 * @param instance		TTPInstance a TTP instance
	 * @param tour			int[] a TSP tour
	 * @param maxRuntime	int maximum time to allow method to run in milliseconds
	 * @return            	A TTPSolution
	 */
    public static TTPSolution simpleHeuristic(TTPInstance instance, int[] tour, int maxRuntime) {
    	ttp.Utils.Utils.startTiming();
    	double[] D = new double[instance.numberOfNodes];
    	double dSum = 0;
    	//D[instance.numberOfNodes-1] = 0; 
    	D[0] = 0;
    	//for (int i = instance.numberOfNodes-2; i >= 0; i--) {
    	for (int i = tour.length-2; i >= 0; i--) { // if >= D[0] is set to total distance
    		dSum += instance.distances(tour[i+1], tour[i]);
    		D[tour[i]] = dSum;
    	}
    	double noItemTime = dSum/instance.maxSpeed;
    	double v = (instance.maxSpeed - instance.minSpeed) / instance.capacityOfKnapsack;
    	
    	final double[] score = new double[instance.numberOfItems];
    	double[] threshScore = new double[instance.numberOfItems];
    	for (int i = 0; i < instance.numberOfItems; i++) {
    		int cityIdx = instance.items[i][3];
    		double itemCarryTime = D[cityIdx] / (instance.maxSpeed - v*instance.items[i][2]);
    		double itemCycleTime = noItemTime - D[cityIdx] + itemCarryTime;
    		score[instance.items[i][0]] = instance.items[i][1] - instance.rentingRatio * itemCarryTime;
    		threshScore[instance.items[i][0]] = instance.rentingRatio*noItemTime 
    				+ (instance.items[i][1] - instance.rentingRatio * itemCycleTime);
    	}
    	
    	// Form array of item indexes to sort
    	Integer[] itemIdx = new Integer[instance.numberOfItems];
    	for (int i = 0; i < itemIdx.length; i++) {
    		itemIdx[i] = instance.items[i][0];
    	}
    	
    	
    	// Heuristic sort
    	Arrays.sort(itemIdx, new Comparator<Integer>() {
    		@Override
    		public int compare(Integer o1, Integer o2) {
    			double diff = score[o1] - score[o2];
    			return (diff == 0) ? 0 : ((diff > 0) ? -1 : 1); // swapped order
    		}
    	});
    	
    	// Construct solution
    	int[] packingPlan = new int[instance.numberOfItems];
    	int Wc = 0;
    	
    	TTPSolution s = new TTPSolution(tour, packingPlan);
    	instance.evaluate(s, false);	
    	for (int i = 0; i < instance.numberOfItems; i++) {
    		// If we're not full
    		//if ( ((Wc + instance.items[itemIdx[i]][2]) < instance.capacityOfKnapsack) && threshScore[itemIdx[i]] > 0) {
    		if ((Wc + instance.items[itemIdx[i]][2]) < instance.capacityOfKnapsack) {
    			int arrIndex=-1;
    			int itemsPCity=(int)Math.round((double)instance.numberOfItems/(instance.numberOfNodes-1));
    			int cityIndex=instance.items[itemIdx[i]][3];
    			int itemNumber=(int)Math.floor((double)(instance.items[itemIdx[i]][0])/(instance.numberOfNodes-1));
    			
    			for (int j = 1; j<tour.length; j++){
    				if (tour[j]==cityIndex){
    					arrIndex=j-1;    					
    					break;
    				}	
    			}

    			int ppIndex = (arrIndex*itemsPCity)+itemNumber;
    			
    			// Only use the item if it produces a better solution
    			packingPlan[ppIndex] = 1;
    			TTPSolution newSol = new TTPSolution(tour,packingPlan);
    			instance.evaluate(newSol, false);
    			if (newSol.objectiveScore > s.objectiveScore) {
    				s = newSol;
        			Wc += instance.items[itemIdx[i]][2];
    			} else {
    				packingPlan[ppIndex] = 0;
    			}
    			
    			//System.out.println("i: "+arrIndex+" ppI: "+ppIndex + " CI " + (cityIndex) + " id: " + (instance.items[itemIdx[i]][0])+" IN: "+itemNumber+" WGC: "+ instance.items[itemIdx[i]][2] +" WGT: "+Wc);
    			//System.out.printf("ItemID: %d\n", itemIdx[i]);
    		}
    		if (Wc == instance.capacityOfKnapsack) {
    			break;
    		}
    	}
    	//System.out.println("Our wc: " +Wc);
    	
    	
   
    	//System.out.println(Arrays.toString(packingPlan));
        long duration = ttp.Utils.Utils.stopTiming();
        s.computationTime = duration;
        return s;
    }
        
    public static TTPSolution randomLinkernTours(TTPInstance instance, long maxRunTime) {
    	ttp.Utils.Utils.startTiming();
    	long startTime = System.currentTimeMillis();
    	int numIterations = 0;
    	long elapsedTime = 0;
    	TTPSolution bestSol = null;
    	double bestObj = Double.NEGATIVE_INFINITY;
    	boolean two = false;
    	boolean five = false;
    	boolean ten = false;
        List<Double> objValHist = new ArrayList<Double>();

    	TreeSet tours = new TreeSet();
    	while (elapsedTime <= maxRunTime) {
    		// Generate a new linkern tour
        	int[] linTour = linkernTour(instance.file.getPath(), instance.numberOfNodes+1);
        	String linTourConverted = Arrays.toString(linTour);
            if(tours.contains(linTourConverted)){//
             	continue;
            }
            tours.add(linTourConverted);
        	// Generate a packing plan
        	
        	int itemsPerCity = instance.numberOfItems / (instance.numberOfNodes - 1);
        	TTPSolution sol = flipTourCheck(instance, linTour);
        	instance.evaluate(sol, false);    	    
        	
    		elapsedTime = System.currentTimeMillis() - startTime;
    		if (elapsedTime >= 60000*2 && !two) {
    			System.out.println("Two Min: " + bestObj);
    			two = true;
    		}
    		if (elapsedTime >= 60000*5 && !five) {
    			System.out.println("Five Min: " + bestObj);
    			five = true;
    		}
    		if (elapsedTime >= 60000*10 && !ten) {
    			System.out.println("Ten Min: " + bestObj);
    			ten = true;
    		}
        	
        	if (sol.objectiveScore > bestObj) {
        		bestSol = sol;
        		bestObj = sol.objectiveScore;
        	}
        	
        	objValHist.add(sol.objectiveScore);
    		//System.out.printf("BestObj: %f,\t NewObj: %f,\t Time: %d\n",bestObj, sol.ob,elapsedTime);
    		numIterations++;
    	}
    	bestSol.computationTime = elapsedTime;
    	
    	
    	//CALC AVG AND STD
    	double tot=0;
        for (Double d : objValHist) {
            tot+=d;
        }
        double avg=tot/objValHist.size();
        tot=0;
        for (Double d : objValHist) {
            tot+=Math.pow(d-avg, 2);
        }
        double std=Math.sqrt(tot/objValHist.size());
        System.out.println("AVG: "+avg+"\t STD: "+std+"\t COUNT: "+objValHist.size());
    	return bestSol;
    }

    public static TTPSolution flipTourCheck(TTPInstance instance, int[] tour) {
        int[] tourOrig= new int[tour.length];
        tourOrig=tour.clone();
        TTPSolution newSolution = null;
        
    	for(int i = 1; i<tour.length/2;i++){ //perform the flip of a tour
        	int temp = tour[i];
        	tour[i]=tour[tour.length-1-i];
        	tour[tour.length-1-i]=temp;
    	}
        
    	//apply packing plan and apply result
    	TTPSolution origSolution = Optimisation.ppGreedyRegardTour(instance, tourOrig);
    	instance.evaluate(origSolution, false);
    	TTPSolution flipSolution = Optimisation.ppGreedyRegardTour(instance, tour);
    	instance.evaluate(flipSolution, false);
    	if(origSolution.objectiveScore>flipSolution.objectiveScore){
    		newSolution=origSolution;
    	}else{
    		newSolution=flipSolution;
    	}

        instance.evaluate(newSolution, false);
        return newSolution;
    }
    
   
   
    /**
     * Greedy Heuristic based packing plan builder, with regard to tour
     * 
     * @param instance
     * @param tour
     */
    public static TTPSolution ppGreedyRegardTour(TTPInstance instance, int[] tour) {

        boolean debugPrint = !true;
        
        ttp.Utils.Utils.startTiming();

        // how far to jump
        double jump = instance.numberOfItems/100;
        
        int[] packingPlan = new int[instance.numberOfItems];
        int[] cityIndex = new int[instance.numberOfNodes];
        int[] cityTourIndex = new int[instance.numberOfItems];
        
        double[] profitVSweight = new double[instance.numberOfItems];

        double[] weights = new double[instance.numberOfItems];
        double[] weightsRatio = new double[instance.numberOfItems];
        double totW = 0;
        double[] profits = new double[instance.numberOfItems];
        double[] profitsRatio = new double[instance.numberOfItems];
        double totP = 0;
        double[] values = new double[instance.numberOfItems];
        double[] dSoFar = new double[instance.numberOfNodes];
        double[] dToGo = new double[instance.numberOfNodes];
        
        double MAXWEIGHT = instance.capacityOfKnapsack;
        
        // Calculate information ready for heuristics
        int itemsPerCity = instance.numberOfItems / (instance.numberOfNodes-1);
		dSoFar[0]=0;
		cityTourIndex[0]=0;

		for(int i = 1; i < tour.length-1; i++){
			dSoFar[i] = dSoFar[i-1]+instance.distances(tour[i-1],tour[i]);
			cityIndex[tour[i]]=i;
		}
        
		for(int i = 0; i < instance.numberOfNodes; i++){
			dToGo[i] = dSoFar[instance.numberOfNodes-1]-dSoFar[i];
		}
		
		for(int i = 0; i < instance.numberOfItems; i++){
			cityTourIndex[i]=cityIndex[instance.items[i][3]];
			totW+=instance.items[i][2];
			weights[i]=instance.items[i][2];
			totP+=instance.items[i][2];
			profits[i]=instance.items[i][1];
		}
		
		for(int i = 0; i < instance.numberOfItems; i++){
			weightsRatio[i]=weights[i]/totW;
			profitsRatio[i]=profits[i]/totP;
			profitVSweight[i]=profitsRatio[i]/weightsRatio[i];
		}
		
		// The heuristics
		for(int i = 0; i < instance.numberOfItems; i++){
			if(itemsPerCity==1){
				//values[i]=profitVSweight[i];//(A1) PvWr
				//values[i]=profitVSweight[i]-weightsRatio[i]*instance.rentingRatio*(dToGo[cityTourIndex[i]]);//(A2) PvWr-WrDRr
				//values[i]=profitVSweight[i]+profitsRatio[i]-weightsRatio[i]*instance.rentingRatio*(dToGo[cityTourIndex[i]]);//(A3) PvWr+Pr-WrDRr
				//values[i]=Math.pow(profitVSweight[i],4)-weightsRatio[i]*instance.rentingRatio*(dToGo[cityTourIndex[i]]);//(A4) 
				
				values[i]=Math.pow(profitVSweight[i],4)+profitsRatio[i]-weightsRatio[i]*instance.rentingRatio*(dToGo[cityTourIndex[i]]);//(A5) best for 01s
			}else{
				//values[i]=profitVSweight[i]/(instance.rentingRatio*(dToGo[cityTourIndex[i]]/dToGo[0]));//(B1)
				//values[i]=(profitVSweight[i]*profits[i])/(instance.rentingRatio*(dToGo[cityTourIndex[i]]/dToGo[0]));//(B2)
				values[i]=profitVSweight[i]/(instance.rentingRatio*(dToGo[cityTourIndex[i]]/dToGo[0])*weightsRatio[i]);//(B3)
				//values[i]=(profitVSweight[i]*profits[i])/(instance.rentingRatio*(dToGo[cityTourIndex[i]]/dToGo[0])*weightsRatio[i]);//(B4)
				//values[i]=Math.pow(profitVSweight[i]*profits[i],1)/Math.pow(instance.rentingRatio*(dToGo[cityTourIndex[i]]/dToGo[0]),1);//(B5) best for 05s
				}	
		}
		
		// Sort the items based on heuristic value
		double[][] sortData = new double[instance.numberOfItems][2];
		
		for(int i = 0; i<instance.numberOfItems; i++){
			sortData[i][0]=i;
			sortData[i][1]=values[i];
		}
		
		Comparator<double[]> newComp = new Comparator<double[]>() {
    		@Override
    		public int compare(double[] s1, double[] s2) {
    			return -Double.compare(s1[1], s2[1]);
		    }
    	};
    	
    	if(debugPrint) System.out.println("Sorting "+instance.numberOfItems+" items...");
    	Arrays.sort(sortData,newComp);

    	// Add items to the packing plan
    	if(debugPrint) System.out.println("Filling Packing Plan");
		
		double totalWeight = 0;
		int count=0;

		double lastOB=Double.NEGATIVE_INFINITY;
		int index=0;
		int noImprovement=0;
		int[] packingPlanOld = new int[instance.numberOfItems];
		int indexOld = 0;
		double weightOld = 0;
		boolean jumpSet=false;
		if(jump>1){
			jumpSet=true;
		}else{
			jumpSet=false;
			jump=2;
		}
		
		while(totalWeight<MAXWEIGHT && count<instance.numberOfItems && jump>=2){
			int bestValueIndex=(int)sortData[index][0];
			
			count++;
			//add it as long as it doesn't break capacity			
			if(totalWeight+weights[bestValueIndex]<=instance.capacityOfKnapsack){
				int ppIndex=(cityTourIndex[bestValueIndex]-1)*itemsPerCity + (int)(bestValueIndex/(tour.length-2));
				packingPlan[ppIndex]=1;
				totalWeight+=weights[bestValueIndex];
				if(debugPrint) System.out.println("I: "+bestValueIndex+" .. P: "+profits[bestValueIndex]+" .. W: "+weights[bestValueIndex]+" .. C: "+cityTourIndex[bestValueIndex]+"/"+(tour.length-2)+" ... V: "+values[bestValueIndex]+" PvW: "+profitVSweight[bestValueIndex]);
				
				if(!jumpSet || index%jump==0){
					
					TTPSolution s = new TTPSolution(tour, packingPlan);
			        instance.evaluate(s, false);
			        if(debugPrint) System.out.println(jump+" .. "+s.objectiveScore+" .<?. "+lastOB+" .. "+index+" .. "+noImprovement);
			        if(s.objectiveScore<lastOB){//reset to previous PP if OB isn't improved by additions added during jump
			        	totalWeight-=weights[bestValueIndex];
			        	packingPlan[ppIndex]=0;
			        	noImprovement++;
			        	
			        	if(jumpSet){
				        	packingPlan=packingPlanOld.clone();
				        	index=indexOld;
				        	totalWeight=weightOld;
			        		jump=Math.ceil(jump/2);
			        	}
			        }else{
			        	weightOld=totalWeight;
			        	indexOld=index;
			        	noImprovement=0;
			        	lastOB=s.objectiveScore;
			        	packingPlanOld=packingPlan.clone();
					}	
				}					        
			}
			index++;			
		}
		
        long duration = ttp.Utils.Utils.stopTiming();
        if(debugPrint) System.out.println("TIME TAKEN: "+duration+" .. Total Weight: "+totalWeight);
        TTPSolution newSolution = new TTPSolution(tour, packingPlan);
        newSolution.computationTime = duration;
        instance.evaluate(newSolution, false);
        return newSolution;
    }
    
public static TTPSolution bitFlip(TTPInstance instance, TTPSolution newSolution, int maxRuntime, int iterations) {
    
    	boolean debug = !true;
        
        if (debug) System.out.println();
        if (debug) System.out.println("-------- BEGIN BITFLIP --------");
        
        long totalTime=0;

        int[] ppTmp=newSolution.packingPlan.clone();
        int itemsPerCity = instance.numberOfItems / (newSolution.tspTour.length - 2);
        
        if(iterations<1)
        	iterations=Integer.MAX_VALUE;
        
        long startTime = System.currentTimeMillis();
        for(int c=0; c<iterations; c++){
//        	long startTime = System.currentTimeMillis(); //orig
        	int count=0;
                
                boolean timeOutReachedInside = false;
                
//        	for(int i=0; i<ppTmp.length; i++){      /*front to back*/
        	for(int i=ppTmp.length-1; i>0; i--){    /*back to front*/
//        	for(int i=ppTmp.length-1; i> ppTmp.length-1 - stopAfter; i--){    /*back to front, do not do all*/
        		ppTmp[i]=Math.abs(newSolution.packingPlan[i]-1);
        		TTPSolution tempSolution = new TTPSolution(newSolution.tspTour, ppTmp);
        		instance.evaluate(newSolution, false);
        		instance.evaluate(tempSolution, false);

        		if(tempSolution.objectiveScore>newSolution.objectiveScore && tempSolution.finalCapacityFree>=0){
        			if(debug)
        				System.out.println("Flip index: "+String.format("%6d", i)+
        									"\t Profit: "+instance.items[instance.getItemIndex(i,newSolution.tspTour)][1]+
        									"\t Weight: "+instance.items[instance.getItemIndex(i,newSolution.tspTour)][2]+
        									"\t p^2/w^2: "+Math.pow(instance.items[instance.getItemIndex(i,newSolution.tspTour)][1], 2)/Math.pow(instance.items[instance.getItemIndex(i,newSolution.tspTour)][2], 2)+
        									"\t City: "+instance.items[instance.getItemIndex(i,newSolution.tspTour)][3]+
        									"\t Tour Position: "+(int)Math.ceil(i/itemsPerCity)+"/"+(newSolution.tspTour.length-2)+
        									"\t On/Off: "+ppTmp[i]+
        									"\t old OB: "+newSolution.objectiveScore+"\t new OB: "+tempSolution.objectiveScore);
        	
        			newSolution.packingPlan[i]=Math.abs(newSolution.packingPlan[i]-1);
        		
        			count++;
        		}else{
        			ppTmp[i]=newSolution.packingPlan[i];
        		}
        	
        		if((System.currentTimeMillis()-startTime)>(maxRuntime-1)){
        			if (debug) System.out.println("did: "+i);
                                timeOutReachedInside = true;
        			break;
        		}
        	}

        	if (debug) System.out.println(count+" flipped taking "+(System.currentTimeMillis()-startTime)+" milliseconds (iteration number "+c+")");
        	if (debug) System.out.println("---");
        
        	totalTime+=(System.currentTimeMillis()-startTime);
        	instance.evaluate(newSolution, false);
        	
        	if(count==0 || timeOutReachedInside){
        		if (debug) System.out.println("-------------------------------");
        		if (debug) System.out.println(c+" bitFlip Iterations with total time: "+totalTime);
        		if (debug) System.out.println("-------------------------------");
        		break;
        	}
        }
        newSolution.computationTime=(System.currentTimeMillis()-startTime);
        if (debug) System.out.println("--------- END BITFLIP ---------");
        if (debug) System.out.println();
        return newSolution;
    }
    
public static TTPSolution insertionReverse(TTPInstance instance, int[] tour, int[] packingPlan, int maxRuntime, int iterations){

    boolean debug = !true;
    TTPSolution newSolution=null;

    int itemsPerCity = instance.numberOfItems / (instance.numberOfNodes-1);
	long totalTime=0;
    if (debug) System.out.println();
    if (debug) System.out.println("------- BEGIN INSERTION REVERSE -------");
    
    if(iterations<1)
    	iterations=Integer.MAX_VALUE;
    
//    int[] tourNew = tour.clone();
//    int[] packingNew = packingPlan.clone();

    TTPSolution oldSolution = new TTPSolution(tour,packingPlan);
    instance.evaluate(oldSolution, false);
    
    if (debug) {
        System.out.println("...old instance: ");oldSolution.println();
    }
    
    long startTime = System.currentTimeMillis();
        
    //set testing interval
    int testingInterval = 100;
//    if (tour.length>10000) testingInterval = 50;
    if (tour.length>10000) testingInterval = 10;
    
//    int evalCounter = 0;
    
    for(int c=0; c<iterations; c++){
            	
    	int count=0;
    	long timeForInnerLoop = 0;
        
    	//for(int im = 1; im<tour.length-2;im++){// move this index old
    	for(int im = tour.length-2; im>1; im--){// move this index, with this loop configuration  we start at the back and we try to move that city further to the back
            
            int[] tourNew = tour.clone();
            int[] packingNew = packingPlan.clone();
            
            long startTimeThisIteration = System.currentTimeMillis();
            
            int bestNewInd=im;
            TTPSolution bestSolution=oldSolution;
            int cityToMove=tourNew[im];
            int[] itemsToMove=new int[itemsPerCity];
            for(int ip=0; ip<itemsPerCity; ip++){
                    itemsToMove[ip]=packingPlan[((im-1)*itemsPerCity)+ip];
            }

            boolean timeOutReachedInside = false;

            for(int ic=im+1;ic<tour.length-1;ic++){// check in this index
//                for(int ic=im-1;ic>im-1-1 && ic>0 ;ic--){// /*check only the neighbour*/ // not comprehensively tested
//                for(int ic=im+1;ic<tour.length-1 && ic<im+3;ic++){// check in this index
                    // move the city
                    if (debug) { System.out.println("checking oldTour="+Arrays.toString(tourNew)); }
                    for(int is=im; is<ic; is++){// shuffle the cities, taking one out and pushing others up
                        tourNew[is]=tourNew[is+1];
                    }
                    tourNew[ic]=cityToMove;
                    if (debug) { System.out.println("checking oldTour="+Arrays.toString(tourNew)); }

                    if (debug) {
                        System.out.println("checking cityToMove="+cityToMove+" im="+im+" ip="+ic);
//                            System.out.println("checking new tour: "+Arrays.toString(tourNew));
                    }

                    // fix the packing plan FROM INSERTION
                    for(int is=((im-1)*itemsPerCity);is<((ic-1)*itemsPerCity);is++){
                            packingNew[is]=packingPlan[is+itemsPerCity];        				
                    }
                    for(int ip=0; ip<itemsPerCity; ip++){
                            packingNew[((ic-1)*itemsPerCity)+ip]=itemsToMove[ip];
                    }

                    // create new solution
                    newSolution = new TTPSolution(tourNew,packingNew);
                    instance.evaluate(newSolution, false);    
//                        evalCounter++;

                    if (debug) 
                        if (newSolution.finalCapacityFree<0 || false || rnd.nextDouble()<0) {
                            System.out.println("PROBLEM WITH: +++ im="+im+" ic="+ic+" ob="+bestSolution.objectiveScore);
                            System.out.println("===bestSolution===");
                            bestSolution.printFull();
                            System.out.println("===newSolution===");
                            newSolution.printFull();
                            System.exit(0);
                    }

                    if (debug) {
                                System.out.print("...generated new instance: ");newSolution.println();
                    }

//                        System.out.println("    im="+im+" ic="+ic+" ob="+newSolution.ob);

                    // check to see if new option finds better OB
                    if(newSolution.objectiveScore>bestSolution.objectiveScore){
                            bestNewInd=ic;
                            bestSolution=newSolution;
                            if (debug) {
                                System.out.println("+++ im="+im+" ic="+ic+" ob="+bestSolution.objectiveScore);
                                System.out.print("...new better instance: ");newSolution.println();
                            }
                    }

                    //reset the tour and packing plan
                    tourNew=tour.clone();
                    packingNew=packingPlan.clone();


                    // important: have break here as well for the large instances
                    if (//tour.length>10000 &&
                            (ic%1000==0 ||
                            (tour.length>10000) && (ic%500==0) ||
                            (tour.length>25000) && (ic%200==0) ||
                            (tour.length>50000) && (ic%100==0)
                            )) {
//                            System.out.println("test");
                        if (System.currentTimeMillis()-startTime > maxRuntime - 200) {
//                                System.out.println("killed");
                            timeOutReachedInside = true;
                            break;
                        }
                    } 
                }

                if(bestNewInd!=im){//found at least one better spot
                        count++;
                        if (debug) {
                            System.out.println("Moved city "+tour[im]+" from position "+im+" to position "+bestNewInd+" taking OB from "+oldSolution.objectiveScore+" to "+bestSolution.objectiveScore);
                            bestSolution.printFull();
                            System.out.println();
                        }

                        // update the solutions
                        tour=bestSolution.tspTour;
                        packingPlan=bestSolution.packingPlan;
                        oldSolution=bestSolution;
                }
                
                
                
                // do the check not too often to check how close we are to reaching the time limit:
                if (im%testingInterval==0 || timeOutReachedInside) {
                    // important: have break here as well for the large instances
                    if (System.currentTimeMillis()-startTime > maxRuntime - 200) {
//                                System.out.println("killed2");
                                break;
                    }                    
//                    long timeForThisInnerLoop = System.currentTimeMillis()-startTimeThisIteration;
//                    timeForInnerLoop = timeForThisInnerLoop;
//                    long timeElapsedSoFar = System.currentTimeMillis()-startTime;
////                    System.out.println("im="+im+"      timer inside 23, timeForThisInnerLoop="+timeForThisInnerLoop+" timeElapsedSoFar="+timeElapsedSoFar+" maxRuntime="+maxRuntime);
//                    if ( timeElapsedSoFar > maxRuntime - timeForThisInnerLoop ) {
////                        System.out.println("BREAK timer inside 23, timeForThisInnerLoop="+timeForThisInnerLoop+" timeElapsedSoFar="+timeElapsedSoFar+" maxRuntime="+maxRuntime);
//                        break;
//                    }
                }
            }
    	
            long timeForThisIteration = System.currentTimeMillis()-startTime;
        
            if (debug) System.out.println(count+" insertions taking "+ timeForThisIteration +" milliseconds");
            if (debug) System.out.println("---");

            oldSolution.computationTime=timeForThisIteration;
            totalTime+=timeForThisIteration;

            if(count==0){
                    if (debug) System.out.println("-------------------------------");
                    if (debug) System.out.println(c+" insertion iterations with total time: "+totalTime);
                    if (debug) System.out.println("-------------------------------");
//                    if (debug) System.out.println("break..."+evalCounter);
                    break;
            }
//            System.out.println(c+" insertion iterations with total time: "+totalTime+ " "+ (timeForThisIteration > (maxRuntime-500)) +" "+evalCounter);
            
//            System.out.println("XXX totalTime="+totalTime+" timeForInnerLoop="+timeForInnerLoop+" maxRuntime="+maxRuntime   +"XXX");
            if (timeForThisIteration > (maxRuntime-500) ) break; 
//            if (totalTime > (maxRuntime - timeForThisIteration +1000 )) break;
    }
            
    if (debug) System.out.println("-------- END INSERTION REVERSE --------");
    if (debug) System.out.println();
    return oldSolution;
}


    
    public static TTPSolution hillClimber(TTPInstance instance,int[] tour,int[] packingPlanExisting,int mode,int durationWithoutImprovement, int maxRuntime) {

        ttp.Utils.Utils.startTiming();

        TTPSolution s = new TTPSolution(tour, packingPlanExisting);
        boolean debugPrint = !true;

        int[] packingPlan = new int[instance.numberOfItems];

        if (packingPlanExisting!=null) {
            packingPlan = packingPlanExisting;
        }

//        TTPSolution tempSolution = new TTPSolution(tour, packingPlan);
        instance.evaluate(s, false);


        boolean improvement = true;
        double bestObjective = s.objectiveScore;

        long startingTimeForRuntimeLimit = System.currentTimeMillis()-200;

        int i = 0;
        int counter = 0;
        while(counter<durationWithoutImprovement) {

                if (i%10==0 /*do the time check just every 10 iterations, as it is time consuming*/
                    && (System.currentTimeMillis()-startingTimeForRuntimeLimit)>=maxRuntime)
                break;


            if (debugPrint) {
                System.out.println(" i="+i+"("+counter+") bestObjective="+bestObjective);
            }
            int[] newPackingPlan = (int[])DeepCopy.copy(packingPlan);

            boolean flippedToZero = false;

            boolean notFlippedYet = true;

            switch (mode) {
                case 1:
                    // flip one bit
                    int position = (int)(rnd.nextDouble()*newPackingPlan.length);
                    if (newPackingPlan[position] == 1) {
                                newPackingPlan[position] = 0;
                                // investigation: was at least one item flipped to zero during an improvement?
//                                flippedToZero = true;
                    } else {
                        newPackingPlan[position] = 1;
                    }
                    break;
                case 2:

                    while (notFlippedYet) {
//                    System.out.println(notFlippedYet);
                        // flip with probability 1/n
                        for (int j=0; j<packingPlan.length; j++) {
                            if (rnd.nextDouble()<1d/packingPlan.length) {
                                if (newPackingPlan[j] == 1) {
                                    newPackingPlan[j] = 0;
                                    // investigation: was at least one item flipped to zero during an improvement?
    //                                flippedToZero = true;
                                } else {
                                    newPackingPlan[j] = 1;
                                }
                                notFlippedYet = false;
                            }
                        }
                    }
                    break;
            }

            TTPSolution newSolution = new TTPSolution(tour, newPackingPlan);
            instance.evaluate(newSolution, false);


            /* replacement condition:
             *   objective value has to be at least as good AND
             *   the knapsack cannot be overloaded
             */
            if (newSolution.objectiveScore >= bestObjective && newSolution.finalCapacityFree >=0 ) {

                // for the stopping criterion: check if there was an actual improvement
                if (newSolution.objectiveScore > bestObjective && newSolution.finalCapacityFree >=0) {
                    improvement = true;
                    counter = 0;
                }

                packingPlan = newPackingPlan;
                s = newSolution;
                bestObjective = newSolution.objectiveScore;

            } else {
                improvement = false;
                counter ++;
            }
            i++;
        }
        
        long duration = ttp.Utils.Utils.stopTiming();
        s.computationTime = duration;
        return s;
    }
    
//    // not save for cluster use when many jobs run in parallel
//    public static int[] linkernTour(TTPInstance instance) {
//        int[] result = new int[instance.numberOfNodes+1];
//        
//        boolean debugPrint = !true;
//
//        String temp = instance.file.getPath();
//        int index = temp.indexOf("_");
//        String tspfilename = temp;
//        if (index==-1) index = tspfilename.indexOf(".");
//        String tspresultfilename = System.getProperty("user.dir") + File.separator + temp.substring(0,index)+".linkern.tour";
//        if (debugPrint) System.out.println("LINKERN: "+tspfilename);
//    
//        File tspresultfile = new File(tspresultfilename);
//        
//        
//        try {
//            if (!tspresultfile.exists()) {
//                List<String> command = new ArrayList<String>();
//                command.add("./linkern");
//                command.add("-o");
//                command.add(tspresultfilename);
//                command.add(tspfilename);
////                printListOfStrings(command);
//                
//                ProcessBuilder builder = new ProcessBuilder(command);
//                builder.redirectErrorStream(true);
//                final Process process = builder.start();
//                InputStream is = process.getInputStream();
//                InputStreamReader isr = new InputStreamReader(is);
//                BufferedReader br = new BufferedReader(isr);
//                String line;
//                while ((line = br.readLine()) != null) {
//                    if (debugPrint) System.out.println("<LINKERN> "+line);
//                }
//                
//                if (debugPrint) System.out.println("Program terminated?");    
//                int rc = process.waitFor();
//                if (debugPrint) System.out.println("Program terminated!");
//            }
//            
//            BufferedReader br = new BufferedReader( new FileReader(tspresultfilename));
//            // discard the first line
//            br.readLine();
//            String line = null; 
//            for (int i=0; i<result.length-1; i++) {
//                line = br.readLine();
//                if (debugPrint) System.out.println("<TOUR> "+line);
//                index = line.indexOf(" ");
//                int number = Integer.parseInt(line.split("\\s+")[0]);
//                result[i] = number; 
//            }
//            
//            if (debugPrint) System.out.println(Arrays.toString(result));
//          
//            
//            } catch (Exception ex) {
//            	ex.printStackTrace();
//            }
//        return result;
//    }
    
    public static int[] linkernTour(String tourFileName, int numNodes) {
        int[] result = new int[numNodes];
        
        boolean debugPrint = false;
        File tourFile = new File(tourFileName);
        String temp = tourFile.getPath();
        int index = temp.indexOf("_");
        String tspfilename = temp;
        if (index==-1) index = tspfilename.indexOf(".");
        String tspresultfilename = System.getProperty("user.dir") + File.separator + temp.substring(0,index)+".linkern.tour."+rnd.nextDouble();
//        String tspresultfilename = System.getProperty("user.dir") + "/" + temp.substring(0,index)+".linkern.tour.alt";
        if (debugPrint) {
            System.out.println("LINKERN: "+tspfilename+ " -o "+tspresultfilename);
        }

        try {
//            System.out.println("System.getProperty(\"os.name\")"+System.getProperty("os.name"));
            
        	List<String> command = new ArrayList<String>();
        	if(System.getProperty("os.name").contains("Windows")){//WINDOWS OS
        		command.add("linkern-windows.exe");
        	}else if(System.getProperty("os.name").contains("Mac")){// MAC OS
        		command.add("./linkern-macos");
        	}else if(System.getProperty("os.name").contains("nix") || System.getProperty("os.name").contains("nux")){// Linux
        		command.add("./linkern-linux");
        	}else {
                    System.out.println("@linkernTour: incorrect OS?");
                    System.exit(0);
                }
                command.add("-s");
                command.add(rnd.nextInt()+"");
        	command.add("-o");
        	command.add(tspresultfilename);
        	command.add(tspfilename);
        	
//        	ProcessBuilder builder = new ProcessBuilder(command);
//        	builder.redirectErrorStream(true);
//        	final Process process = builder.start();
//        	InputStream is = process.getInputStream();
//        	InputStreamReader isr = new InputStreamReader(is);
//        	BufferedReader br = new BufferedReader(isr);
//        	String line;
//        	while ((line = br.readLine()) != null) {
//        		if (debugPrint) System.out.println("<LINKERN> "+line);
//        	}
//           
//        	if (debugPrint) System.out.println("Program terminated?");    
//        	int rc = process.waitFor();
//        	if (debugPrint) System.out.println("Program terminated!");
//        	br.close();
                
                if(System.getProperty("os.name").contains("Windows")){//WINDOWS OS
                    // nop: gets skipped since there are currently problems with linkern.exe and Windows 10
                    // with the following line we do not generated a new one but rely on an existing ttp.tour file on the disk
                    tspresultfilename = System.getProperty("user.dir") + File.separator + temp.substring(0,index)+".linkern.tour";                    
        	} else {//LINUX
                    ProcessBuilder builder = new ProcessBuilder(command);
                    builder.redirectErrorStream(true);
                    final Process process = builder.start();
                    InputStream is = process.getInputStream();
                    InputStreamReader isr = new InputStreamReader(is);
                    BufferedReader br = new BufferedReader(isr);
                    String line;
                    while ((line = br.readLine()) != null) {
                            if (debugPrint) System.out.println("<LINKERN> "+line);
                    }

                    if (debugPrint) System.out.println("Program terminated?");    
                    int rc = process.waitFor();
                    if (debugPrint) System.out.println("Program terminated!");
                    br.close();                    
                }
                
                // inserted penalty, which has the effect that one cluster's file system does not cause problems with too many I/O's (UNSUCCESSFUL)
//                if (tspresultfilename.contains("eil")) {
//                    try {
//                        Thread.sleep(1000);                 //1000 milliseconds is one second.
//                    } catch(InterruptedException ex) {
//                        Thread.currentThread().interrupt();
//                    }
//                }
           
        	BufferedReader br = new BufferedReader( new FileReader(tspresultfilename));
        	// discard the first line
        	br.readLine();
        	String line = null; 
                
                //for cluster: check if all lines were read
                int linesCounter = 0;
                
        	for (int i=0; i<result.length-1; i++) {
                    line = br.readLine();
                    if (debugPrint) System.out.println("<TOUR> "+line);
//                    index = line.indexOf(" ");
                    int number = Integer.parseInt(line.split("\\s+")[0]);
                    result[i] = number; 
                    
                    linesCounter++;
        	}
           
        	if (debugPrint) System.out.println(Arrays.toString(result));
        	br.close();
                
                //hack to trigger the restart (might cause lifelocks)
//                if (linesCounter!=result.length-1) throw new Exception();
                
           	} catch (Exception ex) {
                    // a hack to restart the procedure in case the file on the cluster is removed or overwritten or ...
//                    return linkernTour(tourFileName, numNodes);
           		ex.printStackTrace();
           	}
        
        (new File(tspresultfilename)).delete();
        
        return result;
    }
    
    public static void doAllLinkernTours() {
        
        boolean debugPrint = !true;
        
        File f = new File("instances/tsplibCEIL");
        try {
            if (debugPrint) System.out.println(f.getCanonicalPath());
        } catch (IOException ex) {
        }
        
        File[] fa = f.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                boolean result = false;
                if (name.contains(".tsp")) result = true;
                return result;
            }});
        
        if (debugPrint){
            for (File temp:fa) {
                System.out.println(temp.getAbsolutePath());
            }
        }
        
        for(File tsp:fa) {
            String tspfilename = tsp.getAbsolutePath();
            int index = tspfilename.indexOf("_");
            if (index==-1) index = tspfilename.indexOf(".");
            String tspresultfilename = tspfilename.substring(0, index) +".linkern.tour";


            if (debugPrint) System.out.println("LINKERN: "+tspfilename);

            File tspresultfile = new File(tspresultfilename);

            try {
                if (! tspresultfile.exists()) {
                    List<String> command = new ArrayList<String>();
                    command.add("./linkern");
                    command.add("-o");
                    command.add(tspresultfilename);
                    command.add(tspfilename);
//                    printListOfStrings(command);

                    ProcessBuilder builder = new ProcessBuilder(command);
                    builder.redirectErrorStream(true);
                    
                    ttp.Utils.Utils.startTiming();
                    
                    final Process process = builder.start();
                    InputStream is = process.getInputStream();
                    InputStreamReader isr = new InputStreamReader(is);
                    BufferedReader br = new BufferedReader(isr);
                    String line;
                    while ((line = br.readLine()) != null) {
                        if (debugPrint) System.out.println("<LINKERN> "+line);
                    }
                    if (debugPrint) System.out.println("Program terminated?");    
                    int rc = process.waitFor();
                    
                    long duration = ttp.Utils.Utils.stopTiming();
                    
                    System.out.println( new File(tspresultfilename).getName() +" "+duration);
                    
                    if (debugPrint) System.out.println("Program terminated!");
                }
                                
            } catch (Exception ex) {
            }
        }    
    }
    
    public static void printListOfStrings(List<String> list) {
        String result = "";
        for (String s:list)
            result+=s+" ";
        System.out.println(result);
    }
    
    
    
    
    ///////////////////////////////////////////////////////
    // HEURISTIC TESTER
    ///////////////////////////////////////////////////////
    public static TTPSolution HT(TTPInstance instance, long maxRunTime, boolean runOnce) {
    	ttp.Utils.Utils.startTiming();
    	long startTime = System.currentTimeMillis();
    	long elapsedTime = 0;
    	TTPSolution bestSol = null;
    	double bestObj = Double.NEGATIVE_INFINITY;
        
        boolean debugPrint = !true;

        List<Double> objValHist = new ArrayList<Double>();
        List<int[]> tours2 = new ArrayList<int[]>();

        // Get tours
    	TreeSet tours = new TreeSet();
    	if (debugPrint) System.out.println("Generating Tours: ");
        
        boolean runOnceCheck = true;
        
    	while (elapsedTime <= maxRunTime && runOnceCheck) {
            // Generate a new linkern tours
            if (debugPrint) System.out.println("(generate...)");
            int[] linTour = linkernTour(instance.file.getPath(), instance.numberOfNodes+1);
            String linTourConverted = Arrays.toString(linTour);
            elapsedTime = System.currentTimeMillis() - startTime;
            
            if(tours.contains(linTourConverted)){//
                continue;
            }
            tours.add(linTourConverted);
            tours2.add(linTour);
            if (debugPrint) System.out.print(".");

            // set to false in case this should run just once
            if (runOnce) runOnceCheck = false;
    	}
    	if (debugPrint) System.out.println("(tours generated)");
    	// Assign packing plans (no flip check)
    	int itemsPerCity = instance.numberOfItems / (instance.numberOfNodes - 1);
    	//String[] heuristics = {"DBH","A1","A2","A3","A4","A5","A6","A7","A8","A9","A10","B1","B2","B3","B4","B4B","B4C","B4D","B4E","B4F","B4G"};
    	String[] heuristics = {"B4"};
//    	String[] heuristics = {"DBH","A1","A2","A3","A4","A5","A6","A7","A8","A9","A10","B1","B2","B3","B4","B5"};
    	double[] avgs = new double[heuristics.length];
    	double[] stds = new double[heuristics.length];
    	TTPSolution result=null;
    	
    	for(int c = 0; c<heuristics.length; c++){
    		String h=heuristics[c];
    		objValHist.clear();
    		if (debugPrint) System.out.printf("------- Heuristic: %s -------\n",h);
    		startTime=System.currentTimeMillis();
    		bestObj = Double.NEGATIVE_INFINITY;
        	for(int i = 0; i<tours2.size(); i++){// Generate a packing plan
        		//TTPSolution sol = flipTourCheck(instance, linTour);       		
        		//150225 TTPSolution sol = ppGreedyRT2(instance,tours2.get(i),h);
                        TTPSolution sol = ppGreedyRT3(instance, maxRunTime-elapsedTime /*Integer.MAX_VALUE*/, tours2.get(i), 5, 2.5, false);
                        
                        
                        //150201 injected code to measure time of 30 reps
//                        long measureStart = System.currentTimeMillis();
//                        for (int j=0; j<30; j++) {
//                            TTPSolution soltemp = ppGreedyRT3(instance, Integer.MAX_VALUE, tours2.get(i), 5, 2.5, false);
//                        }
//                        long measureStop = System.currentTimeMillis();
//                        System.out.println((measureStop-measureStart)/30d);
                        //150201 injected code END
                        
                        instance.evaluate(sol, false);    	    

                        if (sol.objectiveScore > bestObj) {
                                bestSol = sol;
                                bestObj = sol.objectiveScore;
                        }

                        objValHist.add(sol.objectiveScore);
                        elapsedTime = System.currentTimeMillis() - startTime;
        		if (debugPrint) System.out.printf("BestObj: %f,\t NewObj: %f,\t Time: %d\n",bestObj, sol.objectiveScore,elapsedTime);
        	}
        	 
        	bestSol.computationTime = elapsedTime;
        	//CALC AVG AND STD
        	double tot=0;
            for (Double d : objValHist) {
                tot+=d;
            }
            double avg=tot/objValHist.size();
            tot=0;
            for (Double d : objValHist) {
                tot+=Math.pow(d-avg, 2);
            }
            double std=Math.sqrt(tot/objValHist.size());
            //bestSol.altPrint();
            if (debugPrint) System.out.println("AVG: "+avg+"\t STD: "+std+"\t COUNT: "+objValHist.size());
            if (debugPrint) System.out.println("------------------------------------------------");
            avgs[c]=avg;
            stds[c]=std;
            if(h.compareTo("B4")==0){
            	result=bestSol;
            }
    	}
    	if (debugPrint) System.out.println("------------------------------------------------");
    	for(int c = 0; c<heuristics.length; c++){
    		if (debugPrint) System.out.println("HEUR: "+heuristics[c]+"\t AVG: "+avgs[c]+"\t STD: "+stds[c]);
    	}
        if (debugPrint) System.out.println("chosen: "+result.objectiveScore);
    	return result;
    }
    
    
    
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static TTPSolution ppGreedyRT3(TTPInstance instance, long maxRunTime, int[] tour, double powerStart, double powerSpread, boolean debug) {
  //public static TTPSolution ppGreedyRT2(TTPInstance instance,                  int[] tour, String h) {
        
//        System.out.println("tour="+Arrays.toString(tour));
        
        ttp.Utils.Utils.startTiming();
        long startTime = System.currentTimeMillis();
        
        double epsilon=.1;
        double maxIterations=20;
        
        int[] cityIndex = new int[instance.numberOfNodes];
        int[] cityTourIndex = new int[instance.numberOfItems];

        double[] weights = new double[instance.numberOfItems];
        double[] profits = new double[instance.numberOfItems];

        double[] dSoFar = new double[instance.numberOfNodes];
        double[] dToGo = new double[instance.numberOfNodes];
        
        
        // Calculate information ready for heuristics
        int itemsPerCity = instance.numberOfItems / (instance.numberOfNodes-1);
		dSoFar[0]=0;
		cityTourIndex[0]=0;

		for(int i = 1; i < tour.length-1; i++){
			dSoFar[i] = dSoFar[i-1]+instance.distances(tour[i-1],tour[i]);
			cityIndex[tour[i]]=i;
		}
        
		for(int i = 0; i < instance.numberOfNodes; i++){
			dToGo[i] = dSoFar[instance.numberOfNodes-1]-dSoFar[i];
		}
		
		for(int i = 0; i < instance.numberOfItems; i++){
			cityTourIndex[i]=cityIndex[instance.items[i][3]];
			weights[i]=instance.items[i][2];
			profits[i]=instance.items[i][1];
		}
		
		//initial solutions
		int iteration=1;
		TTPSolution leftSolution = null;
		TTPSolution middleSolution = null;
		TTPSolution rightSolution = null;
		double leftPower = powerStart-powerSpread;
		double middlePower = powerStart;
		double rightPower = powerStart+powerSpread;
		
		leftSolution=ppGreedyRT3inner(instance, maxRunTime-(System.currentTimeMillis()-startTime), tour, leftPower, itemsPerCity, weights, profits, dToGo, cityTourIndex, debug);
		middleSolution=ppGreedyRT3inner(instance, maxRunTime-(System.currentTimeMillis()-startTime), tour, middlePower, itemsPerCity, weights, profits, dToGo, cityTourIndex, debug);
		rightSolution=ppGreedyRT3inner(instance, maxRunTime-(System.currentTimeMillis()-startTime), tour, rightPower, itemsPerCity, weights, profits, dToGo, cityTourIndex, debug);		
		
		
		while(iteration<maxIterations){
			if(debug)System.out.println("Time remaining: "+(maxRunTime-(System.currentTimeMillis()-startTime)));
			
			if(maxRunTime<=(System.currentTimeMillis()-startTime)){
				break;
			}
			if(leftSolution.objectiveScore>middleSolution.objectiveScore && rightSolution.objectiveScore>middleSolution.objectiveScore){//rare local mins near greater max
				if(leftSolution.objectiveScore>rightSolution.objectiveScore){//left bigger, move middle to left, half spread
					middlePower=leftPower;
					
					powerSpread=powerSpread/2;
					leftPower = middlePower-powerSpread;
					rightPower = middlePower+powerSpread;
					
					middleSolution=leftSolution;
					leftSolution=ppGreedyRT3inner(instance, maxRunTime-(System.currentTimeMillis()-startTime), tour, leftPower, itemsPerCity, weights, profits, dToGo, cityTourIndex, debug);
					rightSolution=ppGreedyRT3inner(instance, maxRunTime-(System.currentTimeMillis()-startTime), tour, rightPower, itemsPerCity, weights, profits, dToGo, cityTourIndex, debug);				
					
					if(debug)System.out.println("BIGGER BOTH BUT MOVED LEFT: \t L: "+leftPower+" ("+leftSolution.objectiveScore+")"+" \t M: "+middlePower+" ("+middleSolution.objectiveScore+")"+" \t R: "+rightPower+" ("+rightSolution.objectiveScore+")");
					
				}else{//right bigger, move middle to right, half spread
					middlePower=rightPower;
					
					powerSpread=powerSpread/2;
					leftPower = middlePower-powerSpread;
					rightPower = middlePower+powerSpread;
					
					middleSolution=rightSolution;
					leftSolution=ppGreedyRT3inner(instance, maxRunTime-(System.currentTimeMillis()-startTime), tour, leftPower, itemsPerCity, weights, profits, dToGo, cityTourIndex, debug);
					rightSolution=ppGreedyRT3inner(instance, maxRunTime-(System.currentTimeMillis()-startTime), tour, rightPower, itemsPerCity, weights, profits, dToGo, cityTourIndex, debug);				
					
					if(debug)System.out.println("BIGGER BOTH BUT MOVED RIGHT: \t L: "+leftPower+" ("+leftSolution.objectiveScore+")"+" \t M: "+middlePower+" ("+middleSolution.objectiveScore+")"+" \t R: "+rightPower+" ("+rightSolution.objectiveScore+")");
				}
			}else if(leftSolution.objectiveScore>middleSolution.objectiveScore){//left bigger, move middle to left, half spread
				middlePower=leftPower;
				
				powerSpread=powerSpread/2;
				leftPower = middlePower-powerSpread;
				rightPower = middlePower+powerSpread;
				
				middleSolution=leftSolution;
				leftSolution=ppGreedyRT3inner(instance, maxRunTime-(System.currentTimeMillis()-startTime), tour, leftPower, itemsPerCity, weights, profits, dToGo, cityTourIndex, debug);
				rightSolution=ppGreedyRT3inner(instance, maxRunTime-(System.currentTimeMillis()-startTime), tour, rightPower, itemsPerCity, weights, profits, dToGo, cityTourIndex, debug);				
				
				if(debug)System.out.println("MOVED LEFT: \t L: "+leftPower+" ("+leftSolution.objectiveScore+")"+" \t M: "+middlePower+" ("+middleSolution.objectiveScore+")"+" \t R: "+rightPower+" ("+rightSolution.objectiveScore+")");
				
			}else if(rightSolution.objectiveScore>middleSolution.objectiveScore){//right bigger, move middle to right, half spread
				middlePower=rightPower;
				
				powerSpread=powerSpread/2;
				leftPower = middlePower-powerSpread;
				rightPower = middlePower+powerSpread;
				
				middleSolution=rightSolution;
				leftSolution=ppGreedyRT3inner(instance, maxRunTime-(System.currentTimeMillis()-startTime), tour, leftPower, itemsPerCity, weights, profits, dToGo, cityTourIndex, debug);
				rightSolution=ppGreedyRT3inner(instance, maxRunTime-(System.currentTimeMillis()-startTime), tour, rightPower, itemsPerCity, weights, profits, dToGo, cityTourIndex, debug);				
				
				if(debug)System.out.println("MOVED RIGHT: \t L: "+leftPower+" ("+leftSolution.objectiveScore+")"+" \t M: "+middlePower+" ("+middleSolution.objectiveScore+")"+" \t R: "+rightPower+" ("+rightSolution.objectiveScore+")");
			}else{//both less than, half spread, keep middle
				powerSpread=powerSpread/2;
				leftPower = middlePower-powerSpread;
				rightPower = middlePower+powerSpread;
				
				leftSolution=ppGreedyRT3inner(instance, maxRunTime-(System.currentTimeMillis()-startTime), tour, leftPower, itemsPerCity, weights, profits, dToGo, cityTourIndex, debug);
				rightSolution=ppGreedyRT3inner(instance, maxRunTime-(System.currentTimeMillis()-startTime), tour, rightPower, itemsPerCity, weights, profits, dToGo, cityTourIndex, debug);
				
				if(debug)System.out.println("BOTH LESS: \t L: "+leftPower+" ("+leftSolution.objectiveScore+")"+" \t M: "+middlePower+" ("+middleSolution.objectiveScore+")"+" \t R: "+rightPower+" ("+rightSolution.objectiveScore+")");								
			}
			
			if(Math.abs(rightSolution.objectiveScore-middleSolution.objectiveScore)<=epsilon && Math.abs(leftSolution.objectiveScore-middleSolution.objectiveScore)<=epsilon){
				if(debug)System.out.println("NO CHANGE; BREAK!");
				break;
			}
			iteration++;
		}
		if(debug)System.out.print("FINAL POWER: "+middlePower+", \t");
		return middleSolution;
    }

public static TTPSolution ppGreedyRT3inner(TTPInstance instance, long maxRunTime, int[] tour, double power, int itemsPerCity, double[] weights, double[] profits, double[] dToGo, int[] cityTourIndex, boolean debug) {
        
        ttp.Utils.Utils.startTiming();
        long startTime = System.currentTimeMillis();
        long elapsedTime = 0;
        
        debug=false;
        // how far to jump
        double jump = instance.numberOfItems/100;
        int[] packingPlan = new int[instance.numberOfItems];
        double[] values = new double[instance.numberOfItems];
        double MAXWEIGHT = instance.capacityOfKnapsack;	
		
		// The heuristics
		for(int i = 0; i < instance.numberOfItems; i++){
			values[i]=Math.pow(profits[i],power)/(dToGo[cityTourIndex[i]]*Math.pow(weights[i],power));			
		}
		
		// Sort the items based on heuristic value
		double[][] sortData = new double[instance.numberOfItems][2];
		
		for(int i = 0; i<instance.numberOfItems; i++){
			sortData[i][0]=i;
			sortData[i][1]=values[i];
		}
		
		Comparator<double[]> newComp = new Comparator<double[]>() {
    		@Override
    		public int compare(double[] s1, double[] s2) {
    			return -Double.compare(s1[1], s2[1]);
		    }
    	};
    	
    	if(debug) System.out.println("Sorting "+instance.numberOfItems+" items...");
    	Arrays.sort(sortData,newComp);

    	// Add items to the packing plan
    	if(debug) System.out.println("Filling Packing Plan");
		
		double totalWeight = 0;
		int count=0;

		double lastOB=Double.NEGATIVE_INFINITY;
		int index=0;
		int noImprovement=0;
		int[] packingPlanOld = new int[instance.numberOfItems];
		int indexOld = 0;
		double weightOld = 0;
		boolean jumpSet=false;
		if(jump>1){
			jumpSet=true;
		}else{
			jumpSet=false;
			jump=2;
		}
		
		while(totalWeight<MAXWEIGHT && count<instance.numberOfItems && jump>=2){
			int bestValueIndex=(int)sortData[index][0];
			
			count++;
			//add it as long as it doesn't break capacity			
			if(totalWeight+weights[bestValueIndex]<=instance.capacityOfKnapsack){
				int ppIndex=(cityTourIndex[bestValueIndex]-1)*itemsPerCity + (int)(bestValueIndex/(tour.length-2));
				packingPlan[ppIndex]=1;
				totalWeight+=weights[bestValueIndex];
				if(debug) System.out.println("I: "+bestValueIndex+" .. P: "+profits[bestValueIndex]+" .. W: "+weights[bestValueIndex]+" .. C: "+cityTourIndex[bestValueIndex]+"/"+(tour.length-2)+" ... V: "+values[bestValueIndex]);
				
				if(!jumpSet || index%jump==0){
					
					TTPSolution s = new TTPSolution(tour, packingPlan);
			        instance.evaluate(s, false);
			        if(debug) System.out.println(jump+" .. "+s.objectiveScore+" .<?. "+lastOB+" .. "+index+" .. "+noImprovement);
			        if(s.objectiveScore<lastOB){//reset to previous PP if OB isn't improved by additions added during jump
			        	totalWeight-=weights[bestValueIndex];
			        	packingPlan[ppIndex]=0;
			        	noImprovement++;
			        	
			        	if(jumpSet){
				        	packingPlan=packingPlanOld.clone();
				        	index=indexOld;
				        	totalWeight=weightOld;
			        		jump=Math.ceil(jump/2);
			        	}
			        }else{
			        	weightOld=totalWeight;
			        	indexOld=index;
			        	noImprovement=0;
			        	lastOB=s.objectiveScore;
			        	packingPlanOld=packingPlan.clone();
					}	
				}					        
			}
			index++;			
		}
		
        long duration = ttp.Utils.Utils.stopTiming();
        if(debug) System.out.println("TIME TAKEN: "+duration+" .. Total Weight: "+totalWeight);
        TTPSolution newSolution = new TTPSolution(tour, packingPlan);
        newSolution.computationTime = duration;
        instance.evaluate(newSolution, false);
        return newSolution;
    }
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    
    
}
    
    
    
    
    
    
    
    
// OUDATED AND NO LONGER USED
//    public static TTPSolution ppGreedyRT2(TTPInstance instance, int[] tour, String h) {
//
//        boolean debugPrint = !true;
//        
//        ttp.Utils.Utils.startTiming();
//
//        // how far to jump
//        double jump = instance.numberOfItems/100;
//        
//        int[] packingPlan = new int[instance.numberOfItems];
//        int[] cityIndex = new int[instance.numberOfNodes];
//        int[] cityTourIndex = new int[instance.numberOfItems];
//        
//        double[] profitVSweight = new double[instance.numberOfItems];
//
//        double[] weights = new double[instance.numberOfItems];
//        double[] weightsRatio = new double[instance.numberOfItems];
//        double totW = 0;
//        double[] profits = new double[instance.numberOfItems];
//        double[] profitsRatio = new double[instance.numberOfItems];
//        double totP = 0;
//        double[] values = new double[instance.numberOfItems];
//        double[] dSoFar = new double[instance.numberOfNodes];
//        double[] dToGo = new double[instance.numberOfNodes];
//        
//        double MAXWEIGHT = instance.capacityOfKnapsack;
//        
//        // Calculate information ready for heuristics
//        int itemsPerCity = instance.numberOfItems / (instance.numberOfNodes-1);
//		dSoFar[0]=0;
//		cityTourIndex[0]=0;
//
//		for(int i = 1; i < tour.length-1; i++){
//			dSoFar[i] = dSoFar[i-1]+instance.distances(tour[i-1],tour[i]);
//			cityIndex[tour[i]]=i;
//		}
//        
//		for(int i = 0; i < instance.numberOfNodes; i++){
//			dToGo[i] = dSoFar[instance.numberOfNodes-1]-dSoFar[i];
//		}
//		
//		for(int i = 0; i < instance.numberOfItems; i++){
//			cityTourIndex[i]=cityIndex[instance.items[i][3]];
//			totW+=instance.items[i][2];
//			weights[i]=instance.items[i][2];
//			totP+=instance.items[i][2];
//			profits[i]=instance.items[i][1];
//		}
//		
//		for(int i = 0; i < instance.numberOfItems; i++){
//			weightsRatio[i]=weights[i]/totW;
//			profitsRatio[i]=profits[i]/totP;
//			profitVSweight[i]=profitsRatio[i]/weightsRatio[i];
//		}
//		
//		// The heuristics
//		for(int i = 0; i < instance.numberOfItems; i++){
//			switch(h){
//			case "DBH":
//				values[i]=profits[i]-((instance.rentingRatio*dToGo[cityTourIndex[i]])/(instance.maxSpeed-weights[i]*((instance.maxSpeed-instance.minSpeed)/instance.capacityOfKnapsack)));//Destiny Based H
//				break;
//			case "A1":
//				values[i]=profitVSweight[i];//(A1) PvWr
//				break;
//			case "A2":
//				values[i]=profitVSweight[i]-weightsRatio[i]*instance.rentingRatio*(dToGo[cityTourIndex[i]]);//(A2) PvWr-WrDRr
//				break;
//			case "A3":
//				values[i]=profitVSweight[i]+profitsRatio[i]-weightsRatio[i]*instance.rentingRatio*(dToGo[cityTourIndex[i]]);//(A3) PvWr+Pr-WrDRr
//				break;
//			case "A4":
//				values[i]=Math.pow(profitVSweight[i],4)-weightsRatio[i]*instance.rentingRatio*(dToGo[cityTourIndex[i]]);//(A4)
//				break;
//			case "A5":
//				values[i]=Math.pow(profitVSweight[i],4)+profitsRatio[i]-weightsRatio[i]*instance.rentingRatio*(dToGo[cityTourIndex[i]]);//(A5) best for 01s
//				break;
//			case "A6":
//				values[i]=Math.pow(profitVSweight[i],2)+profitsRatio[i]-weightsRatio[i]*instance.rentingRatio*(dToGo[cityTourIndex[i]]);//(A5) best for 01s
//				break;
//			case "A7":
//				values[i]=Math.pow(profitVSweight[i],3)+profitsRatio[i]-weightsRatio[i]*instance.rentingRatio*(dToGo[cityTourIndex[i]]);//(A5) best for 01s
//				break;
//			case "A8":
//				values[i]=Math.pow(profitVSweight[i],5)+profitsRatio[i]-weightsRatio[i]*instance.rentingRatio*(dToGo[cityTourIndex[i]]);//(A5) best for 01s
//				break;
//			case "A9":
//				values[i]=Math.pow(profitVSweight[i],4.5)+profitsRatio[i]-weightsRatio[i]*instance.rentingRatio*(dToGo[cityTourIndex[i]]);//(A5) best for 01s
//				break;
//			case "A10":
//				values[i]=Math.pow(profitVSweight[i],3.5)+profitsRatio[i]-weightsRatio[i]*instance.rentingRatio*(dToGo[cityTourIndex[i]]);//(A5) best for 01s
//				break;
//			case "B1":
//				values[i]=profitVSweight[i]/(instance.rentingRatio*(dToGo[cityTourIndex[i]]/dToGo[0]));//(B1)
//				break;
//			case "B2":
//				values[i]=(profitVSweight[i]*profits[i])/(instance.rentingRatio*(dToGo[cityTourIndex[i]]/dToGo[0]));//(B2)
//				break;
//			case "B3":
//				values[i]=profitVSweight[i]/(instance.rentingRatio*(dToGo[cityTourIndex[i]]/dToGo[0])*weightsRatio[i]);//(B3)
//				break;
//			case "B4":
//				values[i]=Math.pow(profits[i],2)/(dToGo[cityTourIndex[i]]*Math.pow(weights[i],2));
//				break;													
//			case "B4B":
//				values[i]=Math.pow(profits[i],2)/(dToGo[cityTourIndex[i]]*Math.pow(weights[i],1));
//				break;
//			case "B4C":
//				values[i]=Math.pow(profits[i],1)/(dToGo[cityTourIndex[i]]*Math.pow(weights[i],2));
//				break;
//			case "B4D":
//				values[i]=Math.pow(profits[i],1)/(dToGo[cityTourIndex[i]]*Math.pow(weights[i],1));
//				break;
//			case "B4E":
//				values[i]=Math.pow(profits[i],3)/(dToGo[cityTourIndex[i]]*Math.pow(weights[i],3));
//				break;
//			case "B4F":
//				values[i]=Math.pow(profits[i],2)/(dToGo[cityTourIndex[i]]*Math.pow(weights[i],3));
//				break;
//			case "B4G":
//				values[i]=Math.pow(profits[i],3)/(dToGo[cityTourIndex[i]]*Math.pow(weights[i],2));
//				break;
//			}		
//		}
//		
//		// Sort the items based on heuristic value
//		double[][] sortData = new double[instance.numberOfItems][2];
//		
//		for(int i = 0; i<instance.numberOfItems; i++){
//			sortData[i][0]=i;
//			sortData[i][1]=values[i];
//		}
//		
//		Comparator<double[]> newComp = new Comparator<double[]>() {
//    		@Override
//    		public int compare(double[] s1, double[] s2) {
//    			return -Double.compare(s1[1], s2[1]);
//		    }
//    	};
//    	
//    	if(debugPrint) System.out.println("Sorting "+instance.numberOfItems+" items...");
//    	Arrays.sort(sortData,newComp);
//
//    	// Add items to the packing plan
//    	if(debugPrint) System.out.println("Filling Packing Plan");
//		
//		double totalWeight = 0;
//		int count=0;
//
//		double lastOB=Double.NEGATIVE_INFINITY;
//		int index=0;
//		int noImprovement=0;
//		int[] packingPlanOld = new int[instance.numberOfItems];
//		int indexOld = 0;
//		double weightOld = 0;
//		boolean jumpSet=false;
//		if(jump>1){
//			jumpSet=true;
//		}else{
//			jumpSet=false;
//			jump=2;
//		}
//		
//		while(totalWeight<MAXWEIGHT && count<instance.numberOfItems && jump>=2){
//			int bestValueIndex=(int)sortData[index][0];
//			
//			count++;
//			//add it as long as it doesn't break capacity			
//			if(totalWeight+weights[bestValueIndex]<=instance.capacityOfKnapsack){
//				int ppIndex=(cityTourIndex[bestValueIndex]-1)*itemsPerCity + (int)(bestValueIndex/(tour.length-2));
//				packingPlan[ppIndex]=1;
//				totalWeight+=weights[bestValueIndex];
//				if(debugPrint) System.out.println("I: "+bestValueIndex+" .. P: "+profits[bestValueIndex]+" .. W: "+weights[bestValueIndex]+" .. C: "+cityTourIndex[bestValueIndex]+"/"+(tour.length-2)+" ... V: "+values[bestValueIndex]+" PvW: "+profitVSweight[bestValueIndex]);
//				
//				if(!jumpSet || index%jump==0){
//					
//					TTPSolution s = new TTPSolution(tour, packingPlan);
//			        instance.evaluate(s, false);
//			        if(debugPrint) System.out.println(jump+" .. "+s.objectiveScore+" .<?. "+lastOB+" .. "+index+" .. "+noImprovement);
//			        if(s.objectiveScore<lastOB){//reset to previous PP if OB isn't improved by additions added during jump
//			        	totalWeight-=weights[bestValueIndex];
//			        	packingPlan[ppIndex]=0;
//			        	noImprovement++;
//			        	
//			        	if(jumpSet){
//				        	packingPlan=packingPlanOld.clone();
//				        	index=indexOld;
//				        	totalWeight=weightOld;
//			        		jump=Math.ceil(jump/2);
//			        	}
//			        }else{
//			        	weightOld=totalWeight;
//			        	indexOld=index;
//			        	noImprovement=0;
//			        	lastOB=s.objectiveScore;
//			        	packingPlanOld=packingPlan.clone();
//					}	
//				}					        
//			}
//			index++;			
//		}
//		
//        long duration = ttp.Utils.Utils.stopTiming();
//        if(debugPrint) System.out.println("TIME TAKEN: "+duration+" .. Total Weight: "+totalWeight);
//        TTPSolution newSolution = new TTPSolution(tour, packingPlan);
//        newSolution.computationTime = duration;
//        instance.evaluate(newSolution, false);
//        return newSolution;
//    }


//    public static TTPSolution insertionOld(TTPInstance instance, int[] tour, int[] packingPlan, int maxRuntime, int iterations){
//    	
//    	/*
//    	 * IMPROVEMENTS YET TO BE IMPLEMENTED:
//    	 * - only check and attempt to move cities that don't add too much length to tour (ie moving city b, compare dist a>b>c to a'>b>c' if too large don't check ob_val and dont move)
//    	 * - don't do sorting below just brute force?
//    	 */
//    	boolean debug = !true;
//    	TTPSolution newSolution=null;
//    	long totalTime=0;
//    	System.out.println();
//        System.out.println("------- BEGIN INSERTION -------");
//        
//        if(iterations<1)
//        	iterations=Integer.MAX_VALUE;
//        
//        for(int c=0; c<iterations; c++){
//        	long startTime = System.currentTimeMillis();
//        	int count=0;
//        	
//        	long elapsedTime=0;
//        	Individual individual = instance.createIndividual(tour, packingPlan);
//        
//        	double[] cityProfits = new double[tour.length-2];
//        	double[] tourCityProfits = new double[tour.length-2];
//        	double[] cityWeights = new double[tour.length-2];
//        	double[] tourCityWeights = new double[tour.length-2];
//        	double[] tourCityWeightsCumulative = new double[tour.length-2];
//        	double[][] itemCosts = new double[instance.numberOfItems][3];
//    	
//        	double[] distToCity = new double[tour.length-2];
//
//        	for(int i = 0; i<instance.numberOfItems; i++){
//        		itemCosts[i][2]=i;
//        		itemCosts[i][1]=instance.items[i][3];
//        		itemCosts[i][0]=(double)instance.items[i][1]/instance.items[i][2];
//        	}
//    	
//        	Comparator<double[]> newComp = new Comparator<double[]>() {
//        		@Override
//        		public int compare(double[] s1, double[] s2) {
//        			return -Double.compare(s1[0], s2[0]);
//        		}
//        	};
//    	
//        	//System.out.println("Sorting "+instance.numberOfItems+" items...");
//        	Arrays.sort(itemCosts,newComp);
//    	
//        	// CALCULATE CITY WEIGHTS AND PROFITS AND DISTANCES TRAVELLED TO CITY
//        	for(int i = 0; i<cityWeights.length;i++){
//        		cityProfits[individual.tour[i].cityId-1] = individual.tour[i].getEmptyProfit();
//        		tourCityProfits[i] = individual.tour[i].getProfit();
//        		cityWeights[individual.tour[i].cityId-1] = individual.tour[i].getEmptyWeight();
//        		tourCityWeights[i] = individual.tour[i].getWeight();
//        		if (i>0){
//        			tourCityWeightsCumulative[i] = tourCityWeightsCumulative[i-1]+tourCityWeights[i];
//        			distToCity[i]=distToCity[i-1]+instance.distances(i+1, i);
//        		}else{
//        			tourCityWeightsCumulative[i] = tourCityWeights[i];
//        			distToCity[i]=instance.distances(i+1, i);
//        		}
//        	}
//    	
//        	double overallDist=distToCity[cityWeights.length-1]+instance.distances(cityWeights.length-1, 0);
//    	
//        	// WORK OUT WHAT CITYS ARE BEST TO MOVE BASED ON WEIGHTS*DISTANCE TO TRAVEL
//        	double[][] movePref = new double[cityWeights.length][2];
//        	for(int i = 0; i<cityWeights.length;i++){
//        		movePref[i][1]=i;
//        		movePref[i][0]=tourCityWeights[i]*(overallDist-distToCity[i]);
//        	}
//    	
//        	Arrays.sort(movePref,newComp);
//
//        	// FOR ALL CITIES THAT ARE SUGGESTED TO BE MOVED TRY AND FIT THEM ELSEWHERE IN THE TOUR, CHECK NEW OB OR HUERISTIC VALUE
//        	double moveThresh=1000;
//        	for(int i = 0; i<cityWeights.length;i++){
//        		if(movePref[i][0]<moveThresh){
//        			//break;
//        		}
//        		//j=tourIndex
//        		int j=(int)movePref[i][1];
//        		if(debug)
//        			System.out.println("Attempting to move city "+individual.tour[j].cityId+" from tour position "+j+". Move Ratio: "+movePref[i][0]);
//    		
//        		double distanceWithoutOld=0;
//        		double distanceWithOld = 0;
//        		double removedDistance=0;
//        		if(j==individual.tour.length-1){
//        			continue;
//        		}else if (j==0){
//        			distanceWithoutOld =instance.distances(0,individual.tour[j+1].cityId);
//        			distanceWithOld = instance.distances(individual.tour[j].cityId,0)+instance.distances(individual.tour[j].cityId,individual.tour[j+1].cityId);    		
//        			removedDistance=distanceWithoutOld-distanceWithOld;
//        		}else{
//        			distanceWithoutOld = instance.distances(individual.tour[j-1].cityId,individual.tour[j+1].cityId);
//        			distanceWithOld = instance.distances(individual.tour[j].cityId,individual.tour[j-1].cityId)+instance.distances(individual.tour[j].cityId,individual.tour[j+1].cityId);    		
//        			removedDistance=distanceWithoutOld-distanceWithOld;
//        		}
//        		if(debug)
//        			System.out.println("Distance W: "+distanceWithOld+" ... D WOUT: "+distanceWithoutOld+" ... RM D:"+removedDistance);
//    		
//        		// LOOK FOR PLACE TO REFIT CITY    		
//        		//only want to add later\
//        		double currentValue= Double.POSITIVE_INFINITY;
//        		double bestValue= Double.POSITIVE_INFINITY;
//        		int bestIndex=-1;
//    		
//        		//put at ni and push all forward (so basically put after ni)    		
//        		for(int ni=j+1;ni<cityWeights.length;ni++){
//        			double distanceWithoutNew=0;
//        			double distanceWithNew = 0;
//        			double addedDistance = 0;
//        			if(ni==j){// ni == j can compare with current spot (doesn't move)	
//        				addedDistance=removedDistance;
//        			}else if(ni==individual.tour.length-1){
//        				distanceWithoutNew = instance.distances(individual.tour[ni].cityId,0);
//        				distanceWithNew = instance.distances(individual.tour[j].cityId,individual.tour[ni].cityId)+instance.distances(individual.tour[j].cityId,0);    		
//        				addedDistance=distanceWithNew-distanceWithoutNew;
//        			}else{
//        				distanceWithoutNew = instance.distances(individual.tour[ni].cityId,individual.tour[ni+1].cityId);
//        				distanceWithNew = instance.distances(individual.tour[j].cityId,individual.tour[ni].cityId)+instance.distances(individual.tour[j].cityId,individual.tour[ni+1].cityId);    		
//        				addedDistance=distanceWithNew-distanceWithoutNew;
//        			}
//    			
//        			currentValue=(addedDistance+removedDistance)*(tourCityWeightsCumulative[ni]-tourCityWeights[j])/overallDist;
//    			    			
//        			if(currentValue<=bestValue){
//        				//System.out.println(ni+" .. "+currentValue+" .. "+removedDistance+" .. "+addedDistance);
//        				bestValue=currentValue;
//        				bestIndex=ni;
//        			}
//        		}
//    		
//        		Individual old = instance.createIndividual(instance.getTour(individual), instance.getPackingPlan(individual));
//        		TTPSolution oldSolution = new TTPSolution(instance.getTour(old), instance.getPackingPlan(individual));
//        		instance.evaluate(oldSolution, false);
//        		double oldOB = oldSolution.objectiveScore;
//              		
//        		// INSERT CITY LATER DOWN TOUR
//        		for(int mi=bestIndex;mi>j;mi--){
//        			City temp = individual.tour[mi];
//        			individual.tour[mi]=individual.tour[j];
//        			individual.tour[j]=temp;
//        		}
//    		
//        		newSolution = new TTPSolution(instance.getTour(individual), instance.getPackingPlan(individual));
//        		instance.evaluate(newSolution, false);
//            	double newOB = newSolution.objectiveScore;
//            
//            	if(newOB>oldOB){
//            		System.out.println(j+" ...: "+bestIndex+" .. "+bestValue+" .. "+oldOB+" .. "+newOB);
//                	count++;
//            	}else{
//            		individual=old;
//            	}
//            	elapsedTime = System.currentTimeMillis() - startTime;
//            	if (elapsedTime>maxRuntime){
//            		break;
//            	}    		
//        	}
//        	
//        	System.out.println(count+" insertions taking "+(System.currentTimeMillis()-startTime)+" milliseconds");
//        	System.out.println("---");
//        
//        	totalTime+=(System.currentTimeMillis()-startTime);
//        	instance.evaluate(newSolution, false);
//        	
//        	if(count==0){
//        		System.out.println("-------------------------------");
//        		System.out.println(c+" insertion iterations with total time: "+totalTime);
//        		System.out.println("-------------------------------");
//        		break;
//        	}
//        	// IF NEW PLACEMENT IS BETTER, REARRANGE TOUR (DOUBLE CHECK IT HELPS BEFORE OVERWRITING?)
//        	//System.out.println("TIME TAKEN: "+duration+" .. Total Weight: "+totalWeight);
//        	newSolution = new TTPSolution(instance.getTour(individual), instance.getPackingPlan(individual));
//        	tour=newSolution.tspTour;
//        	packingPlan=newSolution.packingPlan;
//        	newSolution.computationTime = elapsedTime;
//        	totalTime+=elapsedTime;
//        	instance.evaluate(newSolution, false);
//
//        }
//        
//        
//        System.out.println("-------- END INSERTION --------");
//        System.out.println();
//        return newSolution;
//    }
    
// the following function works, but it might not stop in time. Note: this was *NOT* used in the GECCO 2015 article
//public static TTPSolution insertion(TTPInstance instance, int[] tour, int[] packingPlan, int maxRuntime, int iterations){
//
//    	boolean debug = true;
//    	TTPSolution newSolution=null;
//
//        int itemsPerCity = instance.numberOfItems / (instance.numberOfNodes-1);
//    	long totalTime=0;
//    	System.out.println();
//        System.out.println("------- BEGIN INSERTION -------");
//        
//        if(iterations<1)
//        	iterations=Integer.MAX_VALUE;
//        
//        int[] tourNew = tour.clone();
//    	int[] packingNew = packingPlan.clone();
//    	
//    	TTPSolution oldSolution = new TTPSolution(tour,packingPlan);
//    	instance.evaluate(oldSolution, false);
//        
//        if (debug) {
//            System.out.println("...old instance: ");oldSolution.println();
//        }        
//        
//        for(int c=0; c<iterations; c++){
//            if (true) System.out.println("-insertion: iteration "+c);
//        	long startTime = System.currentTimeMillis();
//        	int count=0;
//        	
//        	for(int im = tour.length-2; im>1; im--){// move this index
////        	for(int im = 1; im<tour.length-2;im++){// move this index
//                    
//                    
//                    if (true && im%10==0) System.out.println("-insertion: index "+im);
//                    
//        		int bestNewInd=im;
//        		TTPSolution bestSolution=oldSolution;
//        		int cityToMove=tourNew[im];
//        		int[] itemsToMove=new int[itemsPerCity];
//        		for(int ip=0; ip<itemsPerCity; ip++){
//        			itemsToMove[ip]=packingPlan[((im-1)*itemsPerCity)+ip];
//        		}
//        		
//        		for(int ic=im+1;ic<tour.length-1;ic++){// check in this index
//                            // move the city
//                            for(int is=im; is<ic; is++){// shuffle the cities, taking one out and pushing others up
//                                    tourNew[is]=tourNew[is+1];
//                            }
//                            tourNew[ic]=cityToMove;
//
//                            // fix the packing plan
//                            for(int is=((im-1)*itemsPerCity);is<((ic-1)*itemsPerCity);is++){
//                                    packingNew[is]=packingPlan[is+itemsPerCity];        				
//                            }
//                            for(int ip=0; ip<itemsPerCity; ip++){
//                                    packingNew[((ic-1)*itemsPerCity)+ip]=itemsToMove[ip];
//                            }
//
//                            // create new solution
//                            newSolution = new TTPSolution(tourNew,packingNew);
//                            instance.evaluate(newSolution, false);
//
//
//                            if (debug) {
//                                System.out.println("...generateed new instance: ");newSolution.println();
//                            }
//
//
//                            // check to see if new option finds better OB
//                            if(newSolution.objectiveScore>bestSolution.objectiveScore){
//                                    bestNewInd=ic;
//                                    bestSolution=newSolution;
//                            }
//
//                            //reset the tour and packing plan
//                            tourNew=tour.clone();
//                            packingNew=packingPlan.clone();
//        		}
//        		
//        		if(bestNewInd!=im){//found at least one better spot
//        			count++;
//        			System.out.println("Moved city "+tour[im]+" from position "+im+" to position "+bestNewInd+" taking OB from "+oldSolution.objectiveScore+" to "+bestSolution.objectiveScore);
//        			
//        			// update the solutions
//        			tour=bestSolution.tspTour;
//        			packingPlan=bestSolution.packingPlan;
//        			oldSolution=bestSolution;
//        		}
//        	}
//        	
//        	
//        	System.out.println(count+" insertions taking "+(System.currentTimeMillis()-startTime)+" milliseconds");
//        	System.out.println("---");
//        
//        	oldSolution.computationTime=System.currentTimeMillis()-startTime;
//        	totalTime+=(System.currentTimeMillis()-startTime);
//        	
//        	if(count==0){
//        		System.out.println("-------------------------------");
//        		System.out.println(c+" insertion iterations with total time: "+totalTime);
//        		System.out.println("-------------------------------");
//        		break;
//        	}
//        }
//                
//        System.out.println("-------- END INSERTION --------");
//        System.out.println();
//        return oldSolution;
//    }

//    public static TTPSolution inversion(TTPInstance instance, int[] tour, int[] packingPlan, int maxRuntime){
//    	ttp.Utils.Utils.startTiming();
//   	
//    	Individual individual = instance.createIndividual(tour);
//        individual = instance.createIndividual(tour, packingPlan);
//        Individual individualOld= instance.createIndividual(tour, packingPlan);
//		TTPSolution oldS = new TTPSolution(tour, packingPlan);
//        instance.evaluate(oldS, false);
//        
//        double[] cityWeights = new double[tour.length];
//        double[] fromStart = new double[tour.length];
//        double[] fromEnd = new double[tour.length];
//        
//        for(int i = 1; i<tour.length-1; i++){
//        	cityWeights[i]=cityWeights[i-1]+individual.tour[i-1].getWeight();
//        	fromStart[i]=fromStart[i-1]+instance.distances(tour[i], tour[i-1]);
//        }
//        
//        for(int i = 1; i<tour.length; i++){
//        	fromEnd[i]=fromStart[tour.length-1]-fromStart[i];        
//        }
//        
//        int itemsPerCity = instance.numberOfItems / (instance.numberOfNodes-1);
//        
//        int betterCount=0;
//        int betterCountB=0;
//        
//    	//for(int dist=tour.length-2;dist>1;dist--){
//    	for(int dist=4;dist>1;dist--){
//    		
//    		for(int a = 1; a+dist<tour.length; a++){
//    			int b=a+dist-1;
//    			double valueBefore=0;
//    			for(int c = a; c<=b; c++){
//    				valueBefore+=cityWeights[c]*fromEnd[c];
//    			}
//    			
//    			double valueAfter=0;
//    			double[] tempWeight= new double[dist];
//    			double[] tempFromEnd= new double[dist];
//    			for(int c = a; c<=b; c++){
//    				if(c==a){
//    					tempWeight[dist-(1+c-a)]=tempWeight[dist-(1+c-a)]+(cityWeights[c]-cityWeights[c-1])+cityWeights[a-1];
//    				}else{
//    					tempWeight[dist-(1+c-a)]=tempWeight[dist-(1+c-a)]+(cityWeights[c]-cityWeights[c-1]);
//    				}
//    				tempFromEnd[dist-(1+c-a)]=(fromEnd[b+1]+(fromStart[c]-fromStart[a-1])-instance.distances(tour[a-1], tour[a])+instance.distances(tour[a],tour[b+1]));
//    				valueAfter+=tempWeight[dist-(1+c-a)]*tempFromEnd[dist-(1+c-a)];
//    			}
//    			
//
//    			System.out.println("D: "+dist+" A: "+a+" B: "+b+" BF: "+valueBefore+" AV: "+valueAfter+" BC: "+betterCount+" BCB: "+betterCountB);
//    			//if(valueAfter<valueBefore){
//    			if(true){
//    				
//    				//System.out.println("-----------D: "+dist+" A: "+a+" B: "+b+" BF: "+valueBefore+" AV: "+valueAfter+" BC: "+betterCount+" BCB: "+betterCountB);
//    				betterCount++;
//    				
//    				int swaps = (int) Math.floor(dist/2);//how many swap operations
//    				//System.out.println(indexA+", "+indexB+", "+swaps);//testing
//   		        
//    				for (int c = 0; c < swaps; c++) {
//    					int temp = tour[a+c];//store temp
//    					tour[a+c]=tour[b-c];
//    					tour[b-c]=temp;
//    				}
//   				
//    				for (int c = 0; c < swaps; c++) {
//    					City t = individual.tour[a+c-1];
//    					individual.tour[a+c-1]=individual.tour[b-c-1];
//    					individual.tour[b-c-1]=t;
//    				}	
//    				
//    				TTPSolution tempS = new TTPSolution(instance.getTour(individual), instance.getPackingPlan(individual));
//    		        instance.evaluate(tempS, false);
//    		        //System.out.println(oldS.ob + " ... N: "+ tempS.ob);
//    		        
//    		        if(tempS.objectiveScore>oldS.objectiveScore){
//        				for(int c = a; c<=b; c++){
//        					cityWeights[c]=tempWeight[c-a];
//        					fromEnd[c]=tempFromEnd[c-a];
//        				}
//    		        	betterCountB++;
//    		        	System.out.println(oldS.objectiveScore + " ... YY: "+ tempS.objectiveScore);
//    		        	oldS.objectiveScore=tempS.objectiveScore;
//        		        
//    		        	individualOld=instance.createIndividual(instance.getTour(individual),instance.getPackingPlan(individual));
//    		        }else{
//    		        	individual=instance.createIndividual(instance.getTour(individualOld),instance.getPackingPlan(individualOld));
//    		        }
//    			}
//    		}
//    	}
//    	
//    	// IF NEW PLACEMENT IS BETTER, REARRANGE TOUR (DOUBLE CHECK IT HELPS BEFORE OVERWRITING?)
//        long duration = ttp.Utils.Utils.stopTiming();
//        //System.out.println("TIME TAKEN: "+duration+" .. Total Weight: "+totalWeight);
//        TTPSolution newSolution = new TTPSolution(instance.getTour(individual), instance.getPackingPlan(individual));
//        newSolution.computationTime = duration;
//        instance.evaluate(newSolution, false);
//        return newSolution;
//    }