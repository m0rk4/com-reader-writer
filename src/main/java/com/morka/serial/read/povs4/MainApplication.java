package com.morka.serial.read.povs4;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import jssc.SerialPort;
import jssc.SerialPortException;

import java.io.IOException;

public class MainApplication extends Application {
    private static final MainController CONTROLLER = new MainController();
    private static final String STM_PORT = "COM4";
    private static SerialPort serialPort;

    @Override
    public void init() {
//        serialPort = new SerialPort(STM_PORT);
//        try {
//            serialPort.openPort();
//            serialPort.setParams(SerialPort.BAUDRATE_9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
//            serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_IN);
//            serialPort.addEventListener(new EventListener(serialPort, CONTROLLER::onDataUpdate), SerialPort.MASK_RXCHAR);
//        } catch (SerialPortException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public void start(Stage stage) throws IOException {
        var fxmlLoader = new FXMLLoader(MainApplication.class.getResource("main-view.fxml"));
        fxmlLoader.setControllerFactory(c -> CONTROLLER);
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
