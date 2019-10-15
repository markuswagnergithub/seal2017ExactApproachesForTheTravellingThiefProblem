package ttp.tour;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import common.tools.Util;
import ttp.problem.TourLoader;

public class TspGenerator {

	public static void main(String[] args) throws IOException {

//		String folder = "../../multiobj/experiments/a280/";
		TspGenerator g = new TspGenerator();
		for (Path filePath: Files.newDirectoryStream(Paths.get(args[0]), 
				path -> path.toString().endsWith(".tsp"))){
			String file = filePath.toString();
			
//			g.generate(file, TspSolver.SolverName.CONCORDE);
			
//			g.generate(file, TspSolver.SolverName.LKH);
			
//			g.generate(file, TspSolver.SolverName.LKH2);
//			
			g.generate(file, TspSolver.SolverName.ACO);
//			
//			g.generate(file, TspSolver.SolverName.INVEROVER);
		}
		
		System.out.println("All done");
	}

	public TspGenerator() {
	}

	public void generate(String tspfile, TspSolver.SolverName solverName) {
		System.out.println("Generating tours for " + tspfile + " by " + solverName.name());
		
		TspSolver tspSolver = new TspSolver();

		HashSet<Integer> keys = new HashSet<>();

		for (int i = 1; i <= 100000; i++) {

			String tourfile = tspfile + "." + i + "." + solverName.type() + ".tour";

			try {
				tspSolver.solve(tspfile, tourfile, solverName);

				List<Integer> permutation = TspSolution
						.align(new ArrayList<>(Util.asList(TourLoader.load(tourfile, solverName.type()))));
				Integer key = permutation.hashCode();
				System.out.println(key);

				if (!keys.contains(key)) {
					keys.add(key);
					if (keys.size() >= 100) {
						break;
					}
				} else {
					Files.delete(Paths.get(tourfile));
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		System.out.println(keys.size() + " in total");

	}

}
