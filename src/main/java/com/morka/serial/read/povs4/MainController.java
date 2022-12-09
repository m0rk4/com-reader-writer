package com.morka.serial.read.povs4;

import com.morka.serial.read.povs4.model.MetricsResponse;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ToggleButton;
import jssc.SerialPort;
import jssc.SerialPortException;

public class MainController {

    private final SerialPort serialPort;

    @FXML
    private LineChart<String, Number> lightChart;
    private final XYChart.Series<String, Number> lightSeries = new XYChart.Series<>();


    @FXML
    private LineChart<String, Number> temperatureChart;
    private final XYChart.Series<String, Number> temperatureSeries = new XYChart.Series<>();


    @FXML
    private ToggleButton connectToggle;
    @FXML
    private ToggleButton startToggle;

    @FXML
    private LineChart<String, Number> potentialChart;
    private final XYChart.Series<String, Number> potentialSeries = new XYChart.Series<>();


    @FXML
    private CheckBox lCheckBox;
    @FXML
    private CheckBox pCheckBox;
    @FXML
    private CheckBox tCheckBox;


    public MainController(SerialPort port) {
        this.serialPort = port;
    }

    @FXML
    void initialize() {
        potentialSeries.setName("Potential");
        potentialChart.getData().add(potentialSeries);
        lightSeries.setName("Light %");
        lightChart.getData().add(lightSeries);
        temperatureSeries.setName("Temp in C");
        temperatureChart.getData().add(temperatureSeries);
        connectToggle.selectedProperty().addListener((__, ___, selected) -> onConnectChanged(selected));
        startToggle.selectedProperty().addListener((__, ___, selected) -> onStartChanged(selected));
        pCheckBox.selectedProperty().addListener((__, ___, selected) -> onStartChanged(startToggle.isSelected()));
        lCheckBox.selectedProperty().addListener((__, ___, selected) -> onStartChanged(startToggle.isSelected()));
        tCheckBox.selectedProperty().addListener((__, ___, selected) -> onStartChanged(startToggle.isSelected()));
        startToggle.disableProperty().bind(connectToggle.selectedProperty().not());
        pCheckBox.disableProperty().bind(connectToggle.selectedProperty().not());
        lCheckBox.disableProperty().bind(connectToggle.selectedProperty().not());
        tCheckBox.disableProperty().bind(connectToggle.selectedProperty().not());
    }

    private void onConnectChanged(boolean selected) {
        if (selected) {
            connect();
        } else {
            disconnect();
        }
    }

    private void onStartChanged(boolean selected) {
        if (isNotConnected())
            return;

        try {
            if (selected) {
                serialPort.writeBytes(new byte[]{(byte) 1, toByte(pCheckBox), toByte(lCheckBox), toByte(tCheckBox)});
            } else {
                serialPort.writeBytes(new byte[]{(byte) 0, (byte) 0, (byte) 0, (byte) 0});
            }
        } catch (SerialPortException e) {
            e.printStackTrace();
        }
    }

    private byte toByte(CheckBox checkBox) {
        return checkBox.isSelected() ? (byte) 1 : (byte) 0;
    }

    private boolean isNotConnected() {
        return !serialPort.isOpened();
    }

    private void connect() {
        try {
            serialPort.openPort();
            serialPort.setParams(SerialPort.BAUDRATE_9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
            serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_IN | SerialPort.FLOWCONTROL_RTSCTS_OUT);
            serialPort.addEventListener(new EventListener(serialPort, this::onDataUpdate), SerialPort.MASK_RXCHAR);
        } catch (SerialPortException e) {
            e.printStackTrace();
        }
    }

    private void disconnect() {
        try {
            if (serialPort != null && serialPort.isOpened())
                serialPort.closePort();
        } catch (SerialPortException e) {
            e.printStackTrace();
        }
    }

    private void onDataUpdate(MetricsResponse metrics) {
        Platform.runLater(() -> {
            if (null != metrics.potential())
                potentialSeries.getData().add(metrics.potential());
            if (null != metrics.light())
                lightSeries.getData().add(metrics.light());
            if (null != metrics.temperature())
                temperatureSeries.getData().add(metrics.temperature());
        });
    }
}
