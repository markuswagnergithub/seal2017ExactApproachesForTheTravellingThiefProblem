package common.tools;

import java.io.FileNotFoundException;
import java.io.FileReader;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import common.tools.Config;

public class ConfigBuilder {
private static Config config = null;
	
	public static Config newInstance(String filename) throws JsonSyntaxException, JsonIOException, FileNotFoundException{
		Gson gson = new Gson();
		config = gson.fromJson(new FileReader(filename), Config.class);
		return config;
	}
	
	public static Config getInstance(){
		if (config == null){
			config = new Config();
		}
		return config;
	}
}
