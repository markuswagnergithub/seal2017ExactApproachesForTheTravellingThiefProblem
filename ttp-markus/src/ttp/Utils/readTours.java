package ttp.Utils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.io.FilenameFilter;

import ttp.TTPInstance;

public class readTours {
  public static ArrayList<int[]> read(TTPInstance instance, int maxTours) {
	  boolean debug = !true;
	  
	  ArrayList<int[]> tours = new ArrayList<int[]>();
	  
      File theDir = new File("tours/"+instance.file.getName().substring(0,instance.file.getName().indexOf('_')));

      // if the directory does not exist, create it
      if (!theDir.exists()) {
    	  if (debug){
    		  System.out.println("creating directory: " + theDir.getAbsolutePath());
    	  }	


    	  try{
    		  theDir.mkdir();
    	  } catch(SecurityException se){
    		  System.out.println("Exception creating tours directory");
    	  }        
      }else{
    	  String[] fileList = theDir.list(new FilenameFilter() {
    		    @Override
    		    public boolean accept(File theDir, String name) {
    		        return name.toLowerCase().endsWith(".tour");
    		    }
    		});
    	  
    	  for(int i = 0; i<fileList.length; i++){
    		  if(i==maxTours){
    			  break;
    		  }
    		  try {
	    		  File tourFile = new File(theDir.getAbsolutePath()+"/"+fileList[i]);
	    		  if (debug){
	    			  System.out.println(tourFile.getAbsolutePath());
	    		  }
	    		  
	    		  FileInputStream fis = null;
	    		  
	    		  fis = new FileInputStream(tourFile);
	    		  BufferedReader br = new BufferedReader(new InputStreamReader(fis));
	    		  String line = br.readLine();
	    		  line = line.replace(",", "");
	    		  line = line.replace("[", "");
	    		  line = line.replace("]", "");
	    		  Scanner scanner = new Scanner(line);
	    		  List<Integer> list = new ArrayList<Integer>();
	    		  while (scanner.hasNextInt()){
	    			  list.add(scanner.nextInt());
	    		  }
	    		  if (debug){
	    			  System.out.println(line);
	    			  System.out.println(list.size());
	    		  }
	    		  
	    		  int[] tour = new int[list.size()];
	    		  for (int j = 0; j<list.size(); j++){
	    			  tour[j]=list.get(j);
	    			  if (debug)
	    				  System.out.print(j+" ");
	    		  }
	    		  tours.add(tour);
	    		  if (debug){
	    			  System.out.println();
	    		  }
	    		  
	    		  br.close();
	    		  fis.close();
	    		  scanner.close();
    		  } catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
    		  }
    	  }
      } 
	  return tours;
  }
  
  public static void write(TTPInstance instance, String tour, int count) {
	  boolean debug = !true;
	  File theDir = new File("tours/"+instance.file.getName().substring(0,instance.file.getName().indexOf('_')));
	  
	  PrintWriter writer=null;
	  try {
		  if (debug)
			  System.out.println("Writing to file: "+theDir.getAbsolutePath()+"/"+String.format("%05d", count)+".tour");
		  
		  writer = new PrintWriter(theDir.getAbsolutePath()+"/"+String.format("%05d", count)+".tour", "UTF-8");
	  } catch (Exception e) {
		  e.printStackTrace();
	  }
	  
	  writer.println(tour);
	  writer.close();
  
  }
}
