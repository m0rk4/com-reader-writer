package com.morka.serial.read.povs4.model;

import javafx.scene.chart.XYChart;

public record MetricsResponse(XYChart.Data<String, Number> potential,
                              XYChart.Data<String, Number> light,
                              XYChart.Data<String, Number> temperature) {
}
