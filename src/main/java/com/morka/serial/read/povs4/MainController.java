package com.morka.serial.read.povs4;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

public class MainController {
    @FXML
    private LineChart<String, Number> lightChart;
    private final XYChart.Series<String, Number> lightSeries = new XYChart.Series<>();
    @FXML
    private LineChart<String, Number> temperatureChart;
    private final XYChart.Series<String, Number> temperatureSeries = new XYChart.Series<>();

    @FXML
    void initialize() {
        lightSeries.setName("Light %");
        lightChart.getData().add(lightSeries);
        temperatureSeries.setName("Temp in C");
        temperatureChart.getData().add(temperatureSeries);
    }

    public void onDataUpdate(EventListener.Data data) {
        Platform.runLater(() -> {
            lightSeries.getData().add(data.light());
            temperatureSeries.getData().add(data.temperature());
        });
    }
}
