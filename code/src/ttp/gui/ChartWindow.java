package ttp.gui;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import ttp.gui.Displayer.Pair;

public class ChartWindow extends Application {


	private static ChartWindow chartsFrame = null;

	private static Stage stage = null;
	private static BenefitVsWeightLineChart totalChart = null;
	private static BenefitVsWeightLineChart currentChart = null;
	private static EndPointsHistChart totalEndPointsChart = null;
	private static SurfaceContributionHistChart contributionChart = null;
	private static Text title = null;
	private static Text message = null;

	public ChartWindow() {
		chartsFrame = this;
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle("Searching");
		totalChart = new BenefitVsWeightLineChart("Population");
		currentChart = new BenefitVsWeightLineChart("Surface");
		totalEndPointsChart = new EndPointsHistChart("Endpoints");
		contributionChart = new SurfaceContributionHistChart("Surface Contribution");
		title = new Text();
		message = new Text();
		
		GridPane rootPane = new GridPane();

		rootPane.add(title, 0, 0, 2, 1);
		rootPane.add(totalChart, 0, 1);
		rootPane.add(currentChart, 1, 1);
		rootPane.add(totalEndPointsChart, 0, 2);
		rootPane.add(contributionChart, 1, 2);
		rootPane.add(message, 0, 3, 2, 1);
		
		Scene scene = new Scene(rootPane, 1100, 600);

		primaryStage.setScene(scene);
		primaryStage.show();
		stage = primaryStage;

		primaryStage.setOnCloseRequest(e -> Platform.exit());
	}

	public static ChartWindow getInstance() {
		if (chartsFrame == null) {
			new Thread(() -> Application.launch(ChartWindow.class)).start();
			while (totalChart == null || currentChart == null || stage == null) {
				try {
					Thread.sleep(20);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		return chartsFrame;
	}

	public void addFronts(List<List<Data<Number, Number>>> overall, List<Data<Number, Number>> current) {

		totalChart.setData(overall);

		currentChart.addSeries(new XYChart.Series<>(FXCollections.observableList(current)));
	}
	
	public void addEndPoints(Map<Integer, Pair<Number, Number>> endpoints) {

		totalEndPointsChart.setData(endpoints);
	}
	
	public void addSurfaceContribution(List<Data<String, Number>> contribution) {
		contributionChart.setData(contribution);
		
	}

	public void setChartTitle(String string) {
		if (string != null) {
			Platform.runLater(() -> title.setText(string));
		}
	}

	public void setMsg(String string) {
		if (string != null) {
			Platform.runLater(() -> message.setText(string));
		}
	}

	public void savePng(String fileName) {
		Platform.runLater(() -> {
			WritableImage image = snapshot(stage.getScene());

			try {
				ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", Paths.get(fileName).toFile());
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}

	public void reset() {
		Platform.runLater(() -> {
			totalChart.getData().clear();
			currentChart.getData().clear();
			message.setText("");
			title.setText("");
		});
	}

	public static WritableImage snapshot(Scene scene) {
		WritableImage writableImage = new WritableImage((int) Math.rint(scene.getWidth()),
				(int) Math.rint(scene.getHeight()));
		return scene.snapshot(writableImage);
	}

}
