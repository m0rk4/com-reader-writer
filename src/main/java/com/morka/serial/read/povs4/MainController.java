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



        // TODO: remove
        DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("ss.ms")
                .withZone(ZoneId.systemDefault());
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                var light = ThreadLocalRandom.current().nextDouble(20, 100);
                var temperature = ThreadLocalRandom.current().nextDouble(15, 20);
                var time = FORMATTER.format(Instant.now());
                Platform.runLater(() -> {
                    lightSeries.getData().add(new XYChart.Data<>(time, light));
                    temperatureSeries.getData().add(new XYChart.Data<>(time, temperature));
                });
            }
        }).start();
    }

    public void onDataUpdate(EventListener.Data data) {
        Platform.runLater(() -> {
            lightSeries.getData().add(data.light());
            temperatureSeries.getData().add(data.temperature());
        });
    }
}
