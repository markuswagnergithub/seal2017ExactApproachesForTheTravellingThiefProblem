package ttp.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.NavigableMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import common.tools.Util;
import javafx.scene.chart.XYChart.Data;



public class Displayer{
	
	public static boolean display = false;
	
	public static class Pair<K, V>{
		public final K k;
		public final V v;
		public Pair(K k,V v) {
			this.k = k;
			this.v = v;
		}
		@Override
		public String toString() {
			return "Pair [k=" + k + ", v=" + v + "]";
		}
	}
	
	public static void main(String[] args){
		Random r = new Random();
		
		double[] xValues = new double[]{1, 2, 3, 4, 5, 6, 7};
		double[] yValues = new double[]{71, 61, 51, 41, 31, 21, 11};
		
		for (int i=1;i<100;i++){
		
			for (int j=0;j<xValues.length;j++){
				xValues[j] = xValues[j] + r.nextInt(100);
			}
			
			List<Pair<Number, Number>> data = new ArrayList<>();
			for (int k=0;k<xValues.length;k++){
				data.add(new Pair<Number, Number>(xValues[k], yValues[k]));
				
			}
			
			List<List<Pair<Number, Number>>> overall = new ArrayList<>();
			overall.add(data);
//			Displayer.show(overall, data, data, data, ""+i, "Msg");
			
			try {
				TimeUnit.MILLISECONDS.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	public static void reset(){
		if (!display) return;
		ChartWindow.getInstance().reset();
	}
	
	public static Data<Number, Number> convert(Pair<Number, Number> pair){
		return new Data<Number, Number>(pair.k, pair.v);
	}
	
	public static Data<Number, Number> convert(Map.Entry<Long, Double> entry){
		return new Data<Number, Number>(entry.getKey(), entry.getValue());
	}
	
	public static Data<String, Number> convertCategory(Pair<String, Number> pair){
		return new Data<String, Number>(pair.k, pair.v);
	}
	
	public static List<Data<Number, Number>> convertSeries(List<Pair<Number, Number>> series){
		return Util.convertList(series, Displayer::convert);
	}
	
	public static List<Data<Number, Number>> convertSeries(NavigableMap<Long, Double> series){
		return series.entrySet().parallelStream().map(Displayer::convert).collect(Collectors.toList());
	}
	
	public static List<Data<String, Number>> convertCategorySeries(List<Pair<String, Number>> series){
		return Util.convertList(series, Displayer::convertCategory);
	}
	
	public static List<List<Data<Number, Number>>> convertFronts(List<List<Pair<Number, Number>>> fronts){
		List<List<Data<Number, Number>>> data = new ArrayList<>();
		for (List<Pair<Number, Number>> l: fronts) {
			data.add(convertSeries(l));
		}
		return data;
	}
	
	
	public static void show(List<List<Pair<Number, Number>>> fronts, NavigableMap<Long, Double> surface, 
			Map<Integer, Pair<Number, Number>> endpoints, List<Pair<String, Number>> surfaceContribution, String title, String msg){
		if (!display) return;
		ChartWindow chartWindow = ChartWindow.getInstance();
		chartWindow.addFronts(convertFronts(fronts), convertSeries(surface));
		chartWindow.addEndPoints(endpoints);
		chartWindow.addSurfaceContribution(convertCategorySeries(surfaceContribution));
		chartWindow.setChartTitle(title);
		chartWindow.setMsg(msg);
	}



	public static void save(String file) {
		if (!display) return;
		ChartWindow.getInstance().savePng(file);
	}




}
