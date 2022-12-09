package com.morka.serial.read.povs4;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import jssc.SerialPort;
import jssc.SerialPortException;

import java.io.IOException;

public class MainApplication extends Application {
    private static final String STM_PORT = "COM6";
    private static SerialPort serialPort;

    @Override
    public void init() {
        serialPort = new SerialPort(STM_PORT);
    }

    @Override
    public void start(Stage stage) throws IOException {
        var fxmlLoader = new FXMLLoader(MainApplication.class.getResource("main-view.fxml"));
        fxmlLoader.setControllerFactory(c -> new MainController(serialPort));
        var scene = new Scene(fxmlLoader.load());
        stage.setTitle("COM3 - Read");
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() throws SerialPortException {
        if (serialPort != null && serialPort.isOpened())
            serialPort.closePort();
    }
}
