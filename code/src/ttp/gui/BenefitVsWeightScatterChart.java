package ttp.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Tooltip;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import ttp.gui.Displayer.Pair;

public class BenefitVsWeightScatterChart extends ScatterChart<Number, Number> {

	
	public BenefitVsWeightScatterChart(String title) {
		this(title, "Weight", "Benefit");
	}
	
	public BenefitVsWeightScatterChart(String title, String xAxisLabel, String yAxisLabel) {
		super(new NumberAxis(), new NumberAxis());
		this.setTitle(title);
		this.getXAxis().setLabel(xAxisLabel);
		this.getYAxis().setLabel(yAxisLabel);
		((NumberAxis)this.getXAxis()).setForceZeroInRange(false);
		((NumberAxis)this.getYAxis()).setForceZeroInRange(false);
		this.setLegendVisible(false);
		this.setAnimated(false);
	}
	
	public void setData(Map<String, Pair<Number, Number>> endpoints) {
		
		List<Data<Number, Number>> data = new ArrayList<>();
		for (Map.Entry<String, Pair<Number, Number>> e : endpoints.entrySet()) {
			Data<Number, Number> d = new Data<>(e.getValue().k, e.getValue().v);
			Circle t = new Circle(5, Color.RED);
			Tooltip tooltip = new Tooltip(e.getKey());
			Tooltip.install(t, tooltip);
			d.setNode(t);
			data.add(d);
		}
		
		
		Platform.runLater(() -> {
			this.getData().clear();
			Series<Number, Number> series = new XYChart.Series<>(FXCollections.observableList(data));
			this.getData().add(series);
			
		});
	}

}
