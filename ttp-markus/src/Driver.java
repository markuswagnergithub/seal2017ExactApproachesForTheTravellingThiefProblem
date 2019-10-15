/* Code to generate the results used in the GECCO 2015 article
    "Approximate Approaches to the Traveling Thief Problem" by
    Hayden Faulkner, Sergey Polyakovskiy, Tom Schultz, Markus Wagner
    http://dl.acm.org/citation.cfm?doid=2739480.2754716
For more general information, check http://cs.adelaide.edu.au/~optlog/research/ttp.php
For all instance files, check http://cs.adelaide.edu.au/~optlog/CEC2014COMP_InstancesNew/
In case of problems, email wagner@acrocon.com

Note: 
- if nothing is printed, then it is likely that the instance files are missing
- if you are interested in the final solution in a file in the root directory, 
  then use the following command once the solution was generated: 
    newSolution.writeResult(resultTitle);
*/

import java.io.File;
import java.io.PrintStream;
import java.util.*;
import ttp.Optimisation.Optimisation;
import ttp.TTPInstance;
import ttp.TTPSolution;
import ttp.Utils.Utils;
import ttp.Utils.drawTSP;

        
/**
 *
 * @author wagner
 */
public class Driver {
    
    /* The current sequence of parameters is
     * args[0]  folder with TTP files
     * args[1]  pattern to identify the TTP problems that should be solved
     * args[2]  optimisation approach chosen
     * args[3]  stopping criterion: number of evaluations without improvement
     * args[4]  stopping criterion: time in milliseconds (e.g., 60000 equals 1 minute)
     */
    public static void main(String[] args) {
       
        if (args.length==0) 
//        	args = new String[]{"instances", "a280_n1395_bounded-strongly-corr_01",
//        	args = new String[]{"instances", "d657_n6560_bounded-strongly-corr_01.ttp", // PSSD
//        	args = new String[]{"instances", "d657_n3280_bounded-strongly-corr_01.ttp", // PSSD
//        	args = new String[]{"instances", "a280_n837_bounded-strongly-corr_01.ttp", // PSSD
//        	args = new String[]{"instances", "eil51_n500_bounded-strongly-corr_01.ttp", // PSSD
//             	args = new String[]{"instances", "eil51_n500_bounded-strongly-corr_10.ttp",
//        	args = new String[]{"instances", "eil51_n50_bounded-strongly-corr_01.ttp", // PSSD
//        	args = new String[]{"instances", "simple4_n6",                             // PSSD
//                args = new String[]{"instances", 
//                    "rat195_n1940_bounded-strongly-corr_03.ttp",
//                    "a280_n1395_uncorr-similar-weights_05",
//                    "u724_n2169_bounded-strongly-corr_07.ttp",
//                    "a280_n279_bounded-strongly-corr_01.ttp",
//                    "a280_n2790_bounded-strongly-corr_10",
        	//args = new String[]{"instances", "a280_n1395_uncorr-similar-weights_05",
//        	args = new String[]{"instances", "a280_n2790_uncorr_10",
//        	args = new String[]{"instances", "rl5934_n59330_uncorr-similar-weights_07",//---
//        	args = new String[]{"instances", "fnl4461_n44600_bounded-strongly-corr_02.ttp", //---
//        	args = new String[]{"instances", "rl11849_n35544_bounded-strongly-corr_02.ttp", //---
        	//args = new String[]{"instances", "pla33810_n33809_bounded-strongly-corr_01",
        	//args = new String[]{"instances", "pla33810_n169045_uncorr-similar-weights_05",
//        	args = new String[]{"instances", "pla33810_n338090_uncorr_10",
            //args = new String[]{"instances", "kroA100_n990_bounded-strongly-corr_01",
        	//args = new String[]{"instances", "pla85900_n858990_bounded-strongly-corr_01",
        	//args = new String[]{"instances70", "",
            //args=new String[]{"instances/eil51_sub","eil51_n05_m4_multiple-strongly-corr_06.ttp",
            args=new String[]{"../code/experiments/eil51_sub",
//                "eil51_n05_m4_multiple-strongly-corr_06.ttp",
                "eil51_n20_m190_uncorr-similar-weights_01.ttp",
            "30", "10000", (1*1000)+""};//10mins, algorithm number 24 is algorithm S5 (best performing) from the GECCO 2015 article          last number: seed
        	//"8", "5", "60000"};//1min
        doBatch(args);
    }
    
    // internal note: the following is used to print: System.out.print(wend+" "+wendUsed+" "+fp+" "+ftraw+" "+ft+" "+ob +" "+computationTime);

    // note: doBatch can process several files sequentially
    public static void doBatch(String args[])
    {
        // if seed provided then use it, otherwise pick a random number
        int seed = -1;
        if (args.length==6) {
            seed = Integer.parseInt(args[5]);
        } else {
            seed = (int)(Math.random()*Integer.MAX_VALUE);
        }
        Utils.setRandomNumberSeed(seed);
        Optimisation.setRandomNumberSeed(seed);
        
        // get file list based on the provided file name pattern (note: often, this will result in only one file)
        File files[] = Utils.getFileList(args);
        
        if (files.length==0) {
            System.out.println("ERROR: no files found to match the pattern "+args[1]+" in directory "+args[0]);
            System.exit(0);
        }
        
        int algorithm = Integer.parseInt(args[2]);
        int durationWithoutImprovement = Integer.parseInt(args[3]); // ignored later
        int maxRuntime = Integer.parseInt(args[4]);
        for (File f:files) {
            // read the TSP instance
            TTPInstance instance = new TTPInstance(f);   
            
            // generate a Linkern tour (or read it if it already exists)
            //int[] tour = Optimisation.linkernTour(instance);
            int[] tour= null;
            long startTime = System.currentTimeMillis();
            String resultTitle = "results/";
            System.out.print(f.getName()+": ");

            
//            TTPSolution newSolution = null;
            TTPSolution newSolution = new TTPSolution(Optimisation.linkernTour(instance.file.getPath(), instance.numberOfNodes+1), new int[instance.numberOfItems]);
            instance.evaluate(newSolution, false);
        
            
            switch(algorithm)
            {
            case -1: //for debugging
                System.out.println();
                newSolution.printFullForCCode();
                newSolution.packingPlan[0]=1;
                newSolution.packingPlan[1]=1;

                newSolution.printFullForCCode();
                instance.evaluate(newSolution, true);
                System.out.println(newSolution.finalProfit);

                break;
            case 0: //pack nothing
                    resultTitle += instance.file.getName() + ".packNothing." + startTime;
                    break;
            case 1: //Hill-Climber (Provided)
                    newSolution=Optimisation.hillClimber(instance, newSolution.tspTour, new int[instance.numberOfItems], 2, durationWithoutImprovement, maxRuntime);
                    resultTitle += instance.file.getName() + ".hillClimber." + startTime;
                    break;
            case 2: //Simple Heuristic (From Research)
                    newSolution=Optimisation.simpleHeuristic(instance, tour, maxRuntime);
                    resultTitle += instance.file.getName() + ".simpleHeuristic." + startTime;
                    break;
            case 3: //Exercise 2 : Algorithm 1 : Greedy Heuristic Packing Plan Change            		
                    newSolution=Optimisation.ppGreedyRegardTour(instance, tour);            		
                    resultTitle += instance.file.getName() + ".ppGreedyRegardTour." + startTime;
                    break;
            case 4: //Exercise 2 : Algorithm 1 : Greedy Heuristic Packing Plan Change with bitflip            		
                    TTPSolution temp=Optimisation.ppGreedyRegardTour(instance, tour);
                    newSolution=Optimisation.bitFlip(instance, temp, (int)((long)maxRuntime - ((System.currentTimeMillis() - startTime) + 10L)), 0);
                    resultTitle += instance.file.getName() + "ppGreedyRegardTour.bitFlip." + startTime;
                    break;
            case 5: //Exercise 3 : Algorithm 1 : Greedy Heuristic Packing Plan with Tour Flip Potential 
                    newSolution=Optimisation.flipTourCheck(instance,tour);//check whether should flip and apply original PPlan
                    resultTitle += instance.file.getName() + ".ppGreedyRegardTour_flip." + startTime;
                    break;
            case 6: //Exercise 3 : Algorithm 2 : Continuous tour building, flipping, and PP assignment
                    newSolution=Optimisation.randomLinkernTours(instance, maxRuntime);
                    resultTitle += instance.file.getName() + ".randomLinkernTours_ppGreedyRegardTour_flip." +newSolution.objectiveScore+"."+ startTime;
                    break;
            case 7: //Exercise 3 : Algorithm 3 : A2 + insertion
                    TTPSolution tmpSolution=Optimisation.randomLinkernTours(instance, maxRuntime-(int)(instance.numberOfItems*.001));
                    newSolution = Optimisation.insertionReverse(instance, tmpSolution.tspTour, tmpSolution.packingPlan, (int)(instance.numberOfItems*.001),0);
                    resultTitle += instance.file.getName() + ".randomLinkernTours_ppGreedyRegardTour_flip_insert." + startTime;
                    break; 
            case 8: //UP-TO-DATE METHOD :: New heuristic + bitFilp convergence
                    //tmpSolution = Optimisation.HT(instance, 10000);
                    newSolution = Optimisation.HT(instance, 60000L, false);
                    newSolution.altPrint();
                    newSolution.printFull();
                    boolean improved = true;
                    double lastOb = newSolution.objectiveScore;
                    int iterations = 0;
                    while(improved) 
                    {
                        iterations++;
                        System.out.println("\n\n***iteration "+iterations+" starting with ob="+lastOb);
                        newSolution = Optimisation.bitFlip(instance, newSolution, (int)((long)maxRuntime - ((System.currentTimeMillis() - startTime) + 10L)), 0);
                        newSolution.altPrint();
                        newSolution.printFull();
                        System.out.println("HillClimber");
                        newSolution = Optimisation.hillClimber(instance, newSolution.tspTour, newSolution.packingPlan, 2, 0x989680, 10000);
                        newSolution.altPrint();
                        newSolution.printFull();
                        newSolution = Optimisation.insertionReverse(instance, newSolution.tspTour, newSolution.packingPlan, 0x989680, 1);
                        newSolution.altPrint();
                        newSolution.printFull();
                        if(newSolution.objectiveScore > lastOb)
                            lastOb = newSolution.objectiveScore;
                        else
                            improved = false;
                    }
                    System.out.println("***finished after iteration "+iterations+" with ob="+lastOb);
                    break;
                
            /* In the following we refer to the names as they are used in the GECCO 2015 article
                "Approximate Approaches to the Traveling Thief Problem" by
                Hayden Faulkner, Sergey Polyakovskiy, Tom Schultz, Markus Wagner
                http://dl.acm.org/citation.cfm?doid=2739480.2754716
            */
                
            // Heuristic S1: run CLK, then PackIterative
            case 20:
            {
                resultTitle = (instance.file.getName() + ".rt3once." + startTime);
                newSolution = Optimisation.HT(instance, maxRuntime, true);
                break;
            }

            // Heuristic S2: run CLK, then PackIterative, then repeat Bitflip until converged
            case 21: // '\025'
            {
                resultTitle = (instance.file.getName() + ".rt3onceBitflip." + startTime);
                newSolution = Optimisation.HT(instance, maxRuntime, true);
                long timePassed = System.currentTimeMillis() - startTime;
                int maxRuntimeLeft = (int)((long)maxRuntime - (timePassed + 10L));
//                System.out.println("XXX"+maxRuntimeLeft/1000+"XXX");
                newSolution = Optimisation.bitFlip(instance, newSolution, maxRuntimeLeft, 0);
                break;
            }

            // Heuristic S3: run CLK, then PackIterative, then (1+1)-EA
            case 22:
            {
                resultTitle = (instance.file.getName() + ".rt3onceEA." + startTime);
                newSolution = Optimisation.HT(instance, maxRuntime, true);
                long timePassed = System.currentTimeMillis() - startTime;
                int maxRuntimeLeft = (int)((long)maxRuntime - (timePassed + 10L));
                newSolution = Optimisation.hillClimber(instance, newSolution.tspTour, newSolution.packingPlan, 2, 0x7fffffff, maxRuntimeLeft);
                break;
            }

            // Heuristic S4: run CLK, then PackIterative, then repeat Insertion until converged
            case 23:
            {
                resultTitle = (instance.file.getName() + ".rt3onceINSERTION." + startTime);
                newSolution = Optimisation.HT(instance, maxRuntime, true);
//                if (true) System.out.println("23: HT done");
                long timePassed = System.currentTimeMillis() - startTime;
                int maxRuntimeLeft = (int)((long)maxRuntime - (timePassed + 10L));
                newSolution = Optimisation.insertionReverse(instance, newSolution.tspTour, newSolution.packingPlan, maxRuntimeLeft, 0);
                break;
            }

            // Heuristic S5: repeat S1 until time is up
            case 24: 
            {
                resultTitle = (instance.file.getName() + ".rt3timelimit." + startTime);
                long starttime = System.currentTimeMillis();
                long currenttime = starttime;
                for(long lastDuration = 0L; currenttime < (startTime + maxRuntime) - lastDuration * 2D; lastDuration = currenttime - starttime)
//                for(long lastDuration = 0L; (double)currenttime < (double)(startTime + (long)maxRuntime) - (double)lastDuration * 2D; lastDuration = currenttime - starttime)
                {
                    starttime = System.currentTimeMillis();
                    resultTitle = (instance.file.getName() + ".rt3." + startTime);
                    TTPSolution tempSolution = Optimisation.HT(instance, maxRuntime, true);
                    if(newSolution == null || tempSolution.objectiveScore > newSolution.objectiveScore)
                        newSolution = tempSolution;
                    currenttime = System.currentTimeMillis();
                }

                break;
            }
            
                
            
            case 25: // Heuristic C1: run CLK, then PackIterative, then repeat “one Bitflip pass, one Insertion pass” until converged                                                               (internal description: first "complex" algorithm: one HT, then REPEAT "one BF pass, one IR pass"        )
            case 26: // Heuristic C2: run CLK, then PackIterative, then repeat “one Bitflip pass, one (1+1)-EA pass, one Insertion pass”                                                            (internal description:                            one HT, then REPEAT "one BF pass, one EA, one IR pass")
            case 27: // Heuristic C3: run CLK until 10% of the time is used, then apply PackIterative, select the best of those, then “one Bitflip pass, one Insertion pass”                        (internal description: pick in HT the best of several,    then REPEAT "one BF pass, one IR pass"        )
            case 28: // Heuristic C4: run CLK until 10% of the time is used, then apply PackIterative, select the best of those, then “one Bitflip pass, one (1+1)-EA pass, one Insertion pass”     (internal description: pick in HT the best of several,    then REPEAT "one BF pass, one EA, one IR pass")
            case 29: // Heuristic C5: repeat C1 until time is up                                                                                                                                    (internal description: case25 several times)
            case 30: // Heuristic C6: repeat C2 until time is up                                                                                                                                    (internal description: case26 several times)
            {
                resultTitle = (instance.file.getName() + ".rt3algorithm"+algorithm+"." + startTime);
                boolean debug = false;
                
                
            int maxRuntimeLeft = 0;
            do{
                TTPSolution newSolutionInner = null;
                if (algorithm==25 || algorithm==26 || algorithm==29 || algorithm==30) {
                    // one tour
                    newSolutionInner = Optimisation.HT(instance, maxRuntime, true);
                } else 
                if (algorithm==27 || algorithm==28) {
                    // several times, for 10% of the maxRuntime
                    newSolutionInner = Optimisation.HT(instance, maxRuntime/10, false);
//                    System.out.println("27/28: Optimisation.HT done");
                }
                if (newSolution==null) newSolution=newSolutionInner;
//System.out.println("SSS"+newSolutionInner.ob+"SSS");
                
                if (debug) System.out.println("after init ob="+newSolutionInner.objectiveScore + " knapsackFree="+newSolutionInner.finalCapacityFree);
                
                long timePassed = System.currentTimeMillis() - startTime;
                maxRuntimeLeft = (int)(maxRuntime - (timePassed + 10));
                
                int improvementLimit = 1;
                int improvementCounter = 0;
                boolean improvmentCounterReseted = false;
                
                for(int i=0; improvementCounter<improvementLimit ;i++ ) {
                    double bestSoFar = newSolutionInner.objectiveScore;
                    
                // one single pass of a bitflip
                    newSolutionInner = Optimisation.bitFlip(instance, newSolutionInner, maxRuntimeLeft, 1);
                    
                    if (newSolutionInner.objectiveScore > bestSoFar) {
                        if (debug) System.out.println("BF improvementCounter reset");
                        improvementCounter=0;
                        improvmentCounterReseted = true;
                        bestSoFar = newSolutionInner.objectiveScore;
                    }
                    if (debug) System.out.println("i="+i+" ob="+newSolutionInner.objectiveScore + " knapsackFree="+newSolutionInner.finalCapacityFree+" maxRuntimeLeft="+maxRuntimeLeft);
                    timePassed = System.currentTimeMillis() - startTime + 10;
                    maxRuntimeLeft = (int)(maxRuntime - (timePassed + 10));
                    if (timePassed>=maxRuntime) break;
                    
                    // one single pass of the 1+1 EA
                    if (algorithm==26 || algorithm==28 || algorithm==30) {
                        int maxEvalsEA = 10000;
                        newSolutionInner = Optimisation.hillClimber(instance, newSolutionInner.tspTour, newSolutionInner.packingPlan, 2, maxEvalsEA, maxRuntimeLeft);
                        
                        if (newSolutionInner.objectiveScore > bestSoFar) {
                            if (debug) System.out.println("HC improvementCounter reset");
                            improvementCounter=0;
                            improvmentCounterReseted = true;
                            bestSoFar = newSolutionInner.objectiveScore;
                        }
                        if (debug) System.out.println("i="+i+" ob="+newSolutionInner.objectiveScore + " knapsackFree="+newSolutionInner.finalCapacityFree+" maxRuntimeLeft="+maxRuntimeLeft);
                        timePassed = System.currentTimeMillis() - startTime + 10;
                        maxRuntimeLeft = (int)(maxRuntime - (timePassed + 10));
                        if (timePassed>=maxRuntime) break;
                    }
                    
                    
                // one single pass of insertion
                    if (debug) System.out.println("  before IR ob="+newSolutionInner.objectiveScore + " knapsackFree="+newSolutionInner.finalCapacityFree+" maxRuntimeLeft="+maxRuntimeLeft);
                    newSolutionInner = Optimisation.insertionReverse(instance, newSolutionInner.tspTour, newSolutionInner.packingPlan, maxRuntimeLeft, 1);
                    if (debug) System.out.println("  after IR ob="+newSolutionInner.objectiveScore + " knapsackFree="+newSolutionInner.finalCapacityFree+" maxRuntimeLeft="+maxRuntimeLeft);
                    
                    if (newSolutionInner.objectiveScore > bestSoFar) {
                        if (debug) System.out.println("IR improvementCounter reset");
                        improvementCounter=0;
                        improvmentCounterReseted = true;
                        bestSoFar = newSolutionInner.objectiveScore;
                    }
                    if (debug) System.out.println("i="+i+" ob="+newSolutionInner.objectiveScore + " knapsackFree="+newSolutionInner.finalCapacityFree+" maxRuntimeLeft="+maxRuntimeLeft);
                    
                // timer condition
                    timePassed = System.currentTimeMillis() - startTime + 10;
                    maxRuntimeLeft = (int)(maxRuntime - (timePassed + 10));
                    if (timePassed>=maxRuntime) break;
                    
                    if (improvmentCounterReseted) {
                        improvmentCounterReseted=false;
                    } else {
                        improvementCounter++;
                    }
                    if (debug) System.out.println("improvementCounter"+improvementCounter);
                } // END OF MAIN LOOP
                
                if (newSolution.objectiveScore<newSolutionInner.objectiveScore) newSolution=newSolutionInner;
//System.out.println("XXX"+newSolution.ob+","+maxRuntimeLeft+"XXX");
            }while(maxRuntimeLeft>0 && (algorithm==29 || algorithm==30));
                
                break;
            }
            default:
            {
                System.out.println("invalid algorithm option"); System.exit(0);
            }
            
            }
            if(algorithm != 8)
            {
                newSolution.computationTime = System.currentTimeMillis() - startTime;
//                newSolution.writeResult(resultTitle);             // UNCOMMENTED 170417
                newSolution.println();
            }
            
            
//            newSolution.printFull();
//            newSolution.printFullForCCode();              // UNCOMMENTED 170417
            
//            System.out.println(newSolution.answer());
//            
//            // 151124: Freiburg study 
//            System.out.println("151124: Freiburg Study");
//            instance.evaluate(newSolution, true);
//            newSolution.altPrint();
        }
    }

    public static void investigateDifferentLinkernTours(String[] args, int times){
        boolean debugPrint = true;
        boolean draw = true;
        File[] files = ttp.Utils.Utils.getFileList(args);
        TTPInstance instance = new TTPInstance(files[0]);//look only at the first one

        TreeSet tours = new TreeSet();

        System.out.println("investigateDifferentLinkernTours: "+args[1]);

        for (int i = 0; i<times; i++) {
            // Generate a new linkern tour
            int[] linTour = Optimisation.linkernTour(instance.file.getPath(), instance.numberOfNodes+1);
            
            //Draw the tours to .png image
            File f = new File("graphs/"+instance.file.getName().substring(0,instance.file.getName().indexOf('_')));
            f.mkdir();
            
            String linTourConverted = Arrays.toString(linTour);
            if(draw && !tours.contains(linTourConverted)){//draw them
            	ttp.Utils.drawTSP.draw(instance,linTour,"graphs/linkerns/"+instance.file.getName().substring(0,instance.file.getName().indexOf('_'))+"/"+String.format("%05d",i+1)+".png");
            }
            tours.add(linTourConverted);
            
            if (debugPrint) System.out.println(i+"th: "+tours.size() + " " + linTourConverted);

            for(int j =0; j < linTour.length/2; j++){
            	int temp = linTour[j];
            	linTour[j] = linTour[linTour.length-1 - j];
            	linTour[linTour.length-1 - j] = temp;
            }

            linTourConverted = Arrays.toString(linTour);
            if(draw && !tours.contains(linTourConverted)){//draw them
            	ttp.Utils.drawTSP.draw(instance,linTour,"graphs/linkerns/"+instance.file.getName().substring(0,instance.file.getName().indexOf('_'))+"/"+String.format("%05d", i+1)+"f.png");
            }
            tours.add(linTourConverted);

            if (debugPrint) System.out.println(i+"th: "+tours.size() + " " + linTourConverted);

            if (!debugPrint) {
                System.out.print(i+" ");
                if (i%20==19)System.out.println();
            }
        }
        System.out.println();

        System.out.println(args[1]+": "+tours.size()+" different tours found (including mirrored ones):");
        for (Object o:tours) {
            System.out.println(o);
        }
        System.out.println();

    }
}
