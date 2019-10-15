package pwt.experiment.multiobjective;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.TreeSet;

import common.tools.Util;
import pwt.exact.dp.DynamicProgramming;
import ttp.problem.Ttp;
import ttp.problem.TtpLoader;
import ttp.tour.TourType;
import ttp.problem.Tour;
import ttp.problem.TourLoader;

public class FrontExp {

	public static void main(String[] args) throws IOException {
		String inputFolder = "../../multiobj/experiments/eil76";

		for (Path ttpFile : Files.newDirectoryStream(Paths.get(inputFolder),
				path -> path.toString().endsWith(".ttp"))) {
			System.out.println(ttpFile);

			Ttp problem = TtpLoader.load(ttpFile.toString());

//			for (TourType tType : TourType.values()) {
//				buildFronts(inputFolder, ttpFile, problem, TourType.con);
				buildFronts(inputFolder, ttpFile, problem, TourType.aco);
//			}
		}

	}

	private static void buildFronts(String inputFolder, Path ttpFile, Ttp problem, TourType tType) throws IOException {
		Path output = Paths.get(ttpFile.toString() + "." + tType + ".txt");
		System.out.print(output);
		Files.deleteIfExists(output);
		int i=0;
		for (Path tourFile : Files.newDirectoryStream(Paths.get(inputFolder),
				path -> path.toString().endsWith("." + tType + ".tour"))) {
			Tour tour = TourLoader.load(tourFile.toString(), problem, tType);
			Files.write(output, dp(tourFile.toString(), tour).getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);

			int[] stops = tour.essence.getStops();
			Util.reverse(stops, 1, stops.length - 1);
			tour.update(stops);
			Files.write(output, dp(tourFile.toString() + " reversed", tour).getBytes(),
					StandardOpenOption.APPEND);
			i++;
		}
		System.out.println(" " + (i * 2) + " records in total");
	}

	public static String dp(final String file, final Tour tour) {
		DynamicProgramming dp = new DynamicProgramming(tour);

		dp.dynamicProgramming();

		TreeSet<Long> keySet = new TreeSet<>(dp.getFinalColumn().keySet());
		StringBuilder sb = new StringBuilder();
		sb.append(file + "\n");
		sb.append("weight\tbenefit\n");

		for (Long key : keySet) {
			sb.append(key + "\t" + dp.getFinalColumn().get(key).beta + "\n");
		}

		return sb.toString();
	}

}
