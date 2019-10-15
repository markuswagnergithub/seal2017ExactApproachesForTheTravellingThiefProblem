package ttp.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import ttp.gui.Displayer.Pair;


public class EndPointsHistChart extends BarChart<String, Number> {

	public EndPointsHistChart(String title) {
		this(title, "Tours", "");
	}
	
	public EndPointsHistChart(String title, String xAxisLabel, String yAxisLabel) {
		super(new CategoryAxis(), new NumberAxis());
		this.setTitle(title);
		this.getXAxis().setLabel(xAxisLabel);
		this.getYAxis().setLabel(yAxisLabel);
		this.setLegendVisible(true);
		this.setAnimated(false);
	}

	public void setData(Map<Integer, Pair<Number, Number>> endpoints) {
		List<Data<String, Number>> weights = new ArrayList<>();
		List<Data<String, Number>> benefits = new ArrayList<>();
		
		endpoints.keySet().stream().sorted().forEach((e)->{
			weights.add(new Data<String, Number>(e.toString(), endpoints.get(e).k));
			benefits.add(new Data<String, Number>(e.toString(), endpoints.get(e).v));
		});
		
		Series<String, Number> seriesw = new XYChart.Series<>("Weight", FXCollections.observableList(weights));
		Series<String, Number> seriesb = new XYChart.Series<>("Benefit", FXCollections.observableList(benefits));

		Platform.runLater(() -> {
			this.getData().clear();
			this.getData().add(seriesw);
			this.getData().add(seriesb);
		});
	}
}
