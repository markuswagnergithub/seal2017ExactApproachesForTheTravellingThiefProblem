package pwt.experiment;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import ttp.heuristic.LocalSearch;
import ttp.problem.TtpLoader;
import ttp.problem.Tour;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import common.tools.Config;
import common.tools.ConfigBuilder;

public class TtpExp {
	final int THREAD_COUNT = 48;
	
	private static void printUsage() {
		System.out.println("Usage:");
		System.out.println("nkp configFile");
		System.out.println("example: nkp conf/rd100.conf");
	}

	public static void main(String[] args) throws JsonSyntaxException,
			JsonIOException, FileNotFoundException, IOException, ExecutionException, InterruptedException {
		if (args.length < 1) {
			printUsage();
			return;
		}
		
		if (args.length == 1) {
			ConfigBuilder.newInstance(args[0]);
			TtpExp ttpExp = new TtpExp();
			ttpExp.runExperiment();
			return;
		}
		
		if (args.length == 2) {
			String instancefile = args[0];
			String tourfile = args[1];
			Tour tour = TtpLoader.load(instancefile, tourfile);
			TtpExp ttpexp = new TtpExp();
			String result = ttpexp.runSingleProblem(tourfile, instancefile, tour);
			String header = "Tour,Instance,Initial Benefit,2-Opt Results,2-Opt Improvement (%),2-Opt Runtime (s),Reversed 2-Opt Results,Reversed 2-Opt Improvement (%),Reversed 2-Opt Runtime (s),Exchange Results,Exchange Improvement (%),Exchange Runtime (s),Reversed Exchange Results,Reversed Exchange Improvement (%),Reversed Exchange Runtime (s),Jump Results,Jump Improvement (%),Jump Runtime (s),Reversed Jump Results,Reversed Jump Improvement (%),Reversed Jump Runtime (s),Combined Results,Combined Improvement (%),Combined Runtime (s)";
			System.out.println(header);
			System.out.println(result);
			return;
		}
	}
	
	public void runExperiment() throws FileNotFoundException, IOException, ExecutionException, InterruptedException {

		System.out.println("#################################################################");
		
		String filePath = Paths.get(System.getProperty("user.dir"), "results.csv").toString();
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, false))) {

			System.out.println("Writing results to " + filePath);
			String header = "Tour,Instance,Initial Benefit,2-Opt Results,2-Opt Improvement (%),2-Opt Runtime (s),Reversed 2-Opt Results,Reversed 2-Opt Improvement (%),Reversed 2-Opt Runtime (s),Exchange Results,Exchange Improvement (%),Exchange Runtime (s),Reversed Exchange Results,Reversed Exchange Improvement (%),Reversed Exchange Runtime (s),Jump Results,Jump Improvement (%),Jump Runtime (s),Reversed Jump Results,Reversed Jump Improvement (%),Reversed Jump Runtime (s),Combined Results,Combined Improvement (%),Combined Runtime (s)";
			System.out.println(header);
			bw.write(header);
			bw.newLine();
			
			ExecutorService es = Executors.newFixedThreadPool(THREAD_COUNT);
			ArrayList<Future<String>> records = new ArrayList<Future<String>>();

			PrintStream realOut = System.out;
			PrintStream dummyOut = new PrintStream(new OutputStream(){
			    public void write(int b) {
			    }
			});
			System.setOut(dummyOut);
			
			for (Config.Case kase : ConfigBuilder.getInstance().cases) {
				for (String tourfile : kase.tourFile) {
					for (String instance : kase.instanceFiles) {
						if (!kase.active) {
							continue;
						}
						Tour tour = TtpLoader.load(kase.folder + instance, kase.folder + tourfile);
						Future<String> record = es.submit(() -> runSingleProblem(tourfile, instance, tour));
						records.add(record);
					}
				}
			}

			es.shutdown();
			while (records.size() > 0) {
				Thread.sleep(60000);
				for (int i = 0; i < records.size(); i++) {
					Future<String> record = records.get(i);
					if (record.isDone()) {
						records.remove(record);
						realOut.println(record.get());
						bw.write(record.get());
						bw.newLine();
						bw.flush();
					}
				}
			}
		}
	}

	public String runSingleProblem(String tourfile, String instanceName, Tour tour) {
		System.out.println("tour:\t" + tourfile);
		System.out.println("instance:\t" + instanceName);
		String record = tourfile + "," + instanceName + ",";
		
		LocalSearch ls = new LocalSearch(tour);
		double initial = ls.getInitial();
		record += initial;
		double result = 0;
		double improve = 0;
		double runtime = 0;
		
		for (LocalSearch.Heuristic heuristic : LocalSearch.Heuristic.values()) {
			System.out.println("@@@@@run " + heuristic);
			ls = new LocalSearch(tour);
			final long start = System.nanoTime();
			result = ls.runLocalSearch(heuristic);
			System.out.println("operator:\t" + heuristic.toString());
			record += "," + result;
			System.out.println("result:\t" + result);
			
			improve = (result - initial) / initial * 100;
			record += "," + improve + "%";
			System.out.println("improved:\t" + improve + "%");
			
			runtime = new BigDecimal(System.nanoTime() - start).divide(new BigDecimal(1000000000)).doubleValue();
			record += "," + runtime;
			System.out.println("runtime:\t" + runtime);

		}
		
		ls = new LocalSearch(tour);
		final long start = System.nanoTime();
		
		System.out.println("@@@@@run combinated localsearch");
		for (LocalSearch.Heuristic heuristic : LocalSearch.Heuristic.values()) {
			result = ls.runLocalSearch(heuristic);
		}
		record += "," + result;
		record += "," + (result - initial) / initial * 100 + "%";
		record += "," + new BigDecimal(System.nanoTime() - start).divide(new BigDecimal(1000000000));
		
		return record;
	}
}
