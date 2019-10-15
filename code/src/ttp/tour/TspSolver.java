package ttp.tour;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class TspSolver {
	public final static String TOOLS_FOLDER = "tools/";
	
	public enum SolverName {
		LKH("linkern", TourType.lkh, null, "-o", null, "Best cycle length:"),
		LKH2("LKH", TourType.lkh2, null, null, null, "Optimal Solution:"),
		CONCORDE("concorde", TourType.con, null, "-o", null, "Optimal Solution:"),
		ACO("acotsp", TourType.aco, "-i", "-p", "--quiet", "Best Solution in try 0 is"),
		INVEROVER("inverover", TourType.inv, null, null, null, "Best Overall Fitness:");
		
		private final String command;
		private final TourType type;
		private final String inputArgument;
		private final String outputArgument;
		private final String options;
		private final String optimalTag;
		SolverName(String command, TourType type, String inputArgument, String outputArgument, String options, String optimalTag){
			this.command = command;
			this.type = type;
			this.inputArgument = inputArgument;
			this.outputArgument = outputArgument;
			this.options = options;
			this.optimalTag = optimalTag;
		}
		
		public String cmd(){
			return this.command;
		}
		
		public TourType type(){
			return this.type;
		}
		
		public String in(){
			return this.inputArgument;
		}
		
		public String out(){
			return this.outputArgument;
		}
		
		public String opt(){
			return this.options;
		}
		
		public String tag(){
			return this.optimalTag;
		}
	
	};

	private final String tool_folder;
	
	public TspSolver(){
		String osname = System.getProperty("os.name").split("\\s")[0].toLowerCase();
		tool_folder = TOOLS_FOLDER + osname + "/";
	}
	
	public String[] composeCommand(String tspFileName, String outputFileName, SolverName solver){
		
		final String solverPath = tool_folder + solver.cmd();
		
		ArrayList<String> command = new ArrayList<>();
		command.add(solverPath);
		if (solver.opt() != null){
			command.add(solver.opt());
		}
		
		if (outputFileName != null){
			if (solver.out()!=null){
				command.add(solver.out());
			}
			command.add(outputFileName);
		}
		
		if (solver.in()!=null){
			command.add(solver.in());
		}
		command.add(tspFileName);
		
		return command.toArray(new String[command.size()]);
	}
	
	public int solve(String tspFileName, String outputFileName, SolverName solver){
		String[] command = composeCommand(tspFileName, outputFileName, solver);
		
		double ret = 0;

		try {
			
			ProcessBuilder pb = new ProcessBuilder(command);
			pb.redirectErrorStream(true);
			Process p = pb.start();

			BufferedReader inp = new BufferedReader(new InputStreamReader(p.getInputStream()));

			String thisLine = null;

			while ((thisLine = inp.readLine()) != null) {
//				System.out.println(thisLine);
				if (thisLine.trim().startsWith(solver.tag())) {
					ret = Double.parseDouble(thisLine.replaceAll(solver.tag(), "").trim());
				}
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
			throw e;
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("IOException", e);
		}

		return (int) ret;
		
	}
	
}
