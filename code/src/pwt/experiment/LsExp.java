package pwt.experiment;

import java.io.IOException;
import java.util.Arrays;

import ttp.heuristic.S1S5Heuristic;
import ttp.problem.Ttp;
import ttp.problem.TtpLoader;
import ttp.tour.TspSolver;

public class LsExp {

	public static void main(String[] args) throws IOException {
		if (args.length < 2) {
			System.out.println("LsExp instance algorithm");
			System.out.println("e.g. LsExp experiments/eil51/eil51_n150_uncorr_01.ttp S5");
			return;
		}
		
		if (args.length == 2) {
			String instancefile = args[0];
			String algo = args[1];
			
			System.out.print(instancefile + "\t" + algo + "\t");
			
			Ttp problem = TtpLoader.load(instancefile);
			S1S5Heuristic heuristic = new S1S5Heuristic(problem, TspSolver.SolverName.LKH, S1S5Heuristic.TEN_MINUTES);
			
			
			S1S5Heuristic.Solution solution = null;
			
			if ("S1".equals(algo)){
				solution = heuristic.S1Heuristic();
			}else{
				solution = heuristic.S5Heuristic();
			}
			
			String record = "tour: " + Arrays.toString(solution.getTour()) + "\tbenefit: " + solution.benefit
					+ "\tpackingplan: "+ Arrays.toString(solution.getPackingPlan()) + "\ttime:" + solution.elapsedTime;
			System.out.println(record);
			
			return;
		}
		

		
	}

}
