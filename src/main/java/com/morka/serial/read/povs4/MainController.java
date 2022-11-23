package com.morka.serial.read.povs4;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

import java.util.regex.Pattern;

public class MainController {
    private static final String STM_PORT = "COM4";
    @FXML
    private Label label;

    @FXML
    void initialize() {
        serialPort = new SerialPort(STM_PORT);
        try {
            serialPort.openPort();
            serialPort.setParams(SerialPort.BAUDRATE_9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE); /*Задаем основные параметры протокола UART*/
            serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_IN);
            serialPort.addEventListener(new EventListener(label), SerialPort.MASK_RXCHAR);
        } catch (SerialPortException e) {
            e.printStackTrace();
        }
    }

    public void onStop() {
        try {
            serialPort.closePort();
        } catch (SerialPortException e) {
            throw new RuntimeException(e);
        }
    }

    private static SerialPort serialPort;

    private record EventListener(Label label) implements SerialPortEventListener {
        private static final StringBuffer BUFFER = new StringBuffer();
        private static final Pattern MESSAGE_PATTERN =
                Pattern.compile("[-+]?[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?\\|[-+]?[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?");
        private static final String MESSAGE_SEPARATOR = "\n";
        private static final String FIELDS_SEPARATOR = "\\|";

        public void serialEvent(SerialPortEvent event) {
            if (!event.isRXCHAR() || event.getEventValue() <= 0)
                return;

            var data = "";
            try {
                data = serialPort.readString(event.getEventValue());
                if (!MESSAGE_SEPARATOR.equals(data))
                    return;

                var lastNewLineIndex = BUFFER.lastIndexOf(MESSAGE_SEPARATOR);
                if (lastNewLineIndex == -1)
                    return;

                var message = BUFFER.substring(lastNewLineIndex + 1);
                if (!MESSAGE_PATTERN.matcher(message).matches())
                    return;

                var fields = message.split(FIELDS_SEPARATOR);
                var light = Double.parseDouble(fields[0].trim());
                var temperature = Double.parseDouble(fields[1].trim());

            } catch (SerialPortException e) {
                e.printStackTrace();
            } finally {
                BUFFER.append(data);
            }
        }
    }
}
