package ttp.gui;


import java.util.List;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

public class BenefitVsWeightLineChart extends LineChart<Number, Number> {

	public static final int MAX_SERIES = 100;
	public static final int MIN_SERIES = 0;
	
	private final int maxSeries;
	
	public BenefitVsWeightLineChart(String title) {
		this(title, MAX_SERIES);
	}
	
	public BenefitVsWeightLineChart(String title, int maxSeries){
		this(title, "Weight", "Benefit", maxSeries);
	}

	public BenefitVsWeightLineChart(String title, String xAxisLabel, String yAxisLabel, int maxSeries) {
		super(new NumberAxis(), new NumberAxis());
		this.setTitle(title);
		this.getXAxis().setLabel(xAxisLabel);
		this.getYAxis().setLabel(yAxisLabel);
		this.maxSeries = maxSeries;
		this.setLegendVisible(false);
		this.setCreateSymbols(false);
		this.setAnimated(false);
	}

	public void setData(List<List<Data<Number, Number>>> overall) {
		if (overall.size() > maxSeries) {
			System.out.println("Cant set data to chart as the size exceeds the limit.");
			return;
		}
		
		Platform.runLater(() -> {
			this.getData().clear();
			for (List<Data<Number, Number>> data : overall) {
				this.getData().add(new XYChart.Series<>(FXCollections.observableList(data)));
			}
		});
	}

	public void addSeries(XYChart.Series<Number, Number> series) {
		XYChart.Series<Number, Number> s = new XYChart.Series<Number, Number>();
		s.getData().addAll(series.getData());
		s.setName(series.getName());
		
		Platform.runLater(() -> {
			if (this.getData().size() > maxSeries) {
				this.getData().remove(maxSeries/2);
			}
			this.getData().add(s);
		});
	}
	
}
