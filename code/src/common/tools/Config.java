package common.tools;

import java.util.ArrayList;

public class Config {
	
	public double[] epsilon = {0.1};
	
	public String logFolder = "experiments/log/";
		
	public class Case{
		public String name = null;
		public boolean active = true;
		
		public ArrayList<String> instanceFiles = new ArrayList<String>();
		
	    public ArrayList<String> tourFile = new ArrayList<String>();
	    public String folder = null;

	}
	
	public ArrayList<Case> cases = new ArrayList<Case>();
	
}
