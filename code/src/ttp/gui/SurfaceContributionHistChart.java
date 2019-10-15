package ttp.gui;

import java.util.List;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;


public class SurfaceContributionHistChart extends BarChart<String, Number> {

	public SurfaceContributionHistChart(String title) {
		this(title, "Tours", "Contribution Rate");
	}
	
	public SurfaceContributionHistChart(String title, String xAxisLabel, String yAxisLabel) {
		super(new CategoryAxis(), new NumberAxis());
		this.setTitle(title);
		this.getXAxis().setLabel(xAxisLabel);
		this.getYAxis().setLabel(yAxisLabel);
		this.setLegendVisible(false);
		this.setAnimated(false);
	}

	public void setData(List<Data<String, Number>> overall) {
		Series<String, Number> series = new XYChart.Series<>(FXCollections.observableList(overall));

		Platform.runLater(() -> {
			this.getData().clear();
			this.getData().add(series);
		});
	}
	
}
