package com.morka.serial.read.povs4;

import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

public class MainController {
    private static final String STM_PORT = "COM3";
    private final Executor executor;
    @FXML
    private Label label;

    public MainController(ExecutorService threadPool) {
        executor = threadPool;
    }

    @FXML
    void initialize() {
        System.out.println(getAvailableSerialPorts());
        // todo handle close
        var in = getPortInputStream(STM_PORT);
        executor.execute(new SerialReader(label, in));
    }

    private InputStream getPortInputStream(String portName) {
        try {
            var portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
            if (portIdentifier.isCurrentlyOwned())
                throw new RuntimeException("Port, " + portName + ", is in use.");

            var openWaitTime = 2000;
            var commPort = portIdentifier.open(MainController.class.getName(), openWaitTime);
            if (!(commPort instanceof SerialPort serialPort))
                throw new RuntimeException("Only serial ports are supported.");

            serialPort.setSerialPortParams(
                    9600,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE
            );
            return serialPort.getInputStream();
        } catch (NoSuchPortException | UnsupportedCommOperationException | PortInUseException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static Set<String> getAvailableSerialPorts() {
        var ports = new HashSet<String>();
        var thePorts = CommPortIdentifier.getPortIdentifiers();
        while (thePorts.hasMoreElements()) {
            var com = (CommPortIdentifier) thePorts.nextElement();
            if (com.getPortType() != CommPortIdentifier.PORT_SERIAL)
                continue;

            try {
                var thePort = com.open("CommUtil", 50);
                thePort.close();
                ports.add(com.getName());
            } catch (PortInUseException e) {
                System.out.println("Port, " + com.getName() + ", is in use.");
            }
        }
        return ports;
    }

    private record SerialReader(Label label, InputStream in) implements Runnable {
        public void run() {
            var buffer = new byte[1024];
            var len = -1;
            try {
                while ((len = in.read(buffer)) > -1) {
                    var data = new String(buffer, 0, len);
                    System.out.println(data);
                    Platform.runLater(() -> label.setText(data));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
