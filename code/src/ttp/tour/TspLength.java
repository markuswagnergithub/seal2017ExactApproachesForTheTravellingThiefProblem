package ttp.tour;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import ttp.problem.Tour;
import ttp.problem.TourLoader;
import ttp.problem.Ttp;
import ttp.problem.TtpLoader;

public class TspLength {

	public static void main(String[] args) throws IOException {
		
		String inputFolder = "../../multiobj/experiments/eil76";		
		
		Ttp ttp = TtpLoader.load(inputFolder + "/eil76_n75_uncorr_01.ttp");
		


		for (Path ttpFile : Files.newDirectoryStream(Paths.get(inputFolder),
				path -> path.toString().endsWith(".tour"))) {
			
			String typestr = ttpFile.getFileName().toString().split("\\.")[3];
			
			Tour tour = TourLoader.load(ttpFile.toString(), ttp, TourType.valueOf(typestr));
			
			System.out.println(ttpFile);
			long totalLength = tour.totalDistance();
			System.out.println(totalLength);
			
			Files.write(Paths.get(ttpFile.toString() + ".length"), Long.toString(totalLength).getBytes());
			
		}
		
		
	

	}

}
