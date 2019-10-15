package pwt.experiment;

import java.io.FileNotFoundException;

import ttp.problem.Ttp;
import ttp.problem.TtpLoader;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import common.tools.Config;
import common.tools.ConfigBuilder;

public class VersionOfInstances {

	public static void main(String[] args) throws JsonSyntaxException, JsonIOException, FileNotFoundException {
		ConfigBuilder.newInstance(args[0]);
		VersionOfInstances voi = new VersionOfInstances();
		voi.run();
	}

	public void run() throws FileNotFoundException {
		
		for (Config.Case kase : ConfigBuilder.getInstance().cases) {

			for (String instance : kase.instanceFiles) {
				if (!kase.active)
					continue;

				Ttp problem = TtpLoader.load(kase.folder + instance);

				if (problem.capacity >= problem.totalWeight()){
					System.out.println(instance + " " + problem.capacity + " " + problem.totalWeight() + " U");
				}else{
					System.out.println(instance + " " + problem.capacity + " " + problem.totalWeight() + " C");
				}
			}
		}

	}
	
}
