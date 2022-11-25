package com.morka.serial.read.povs4;

import javafx.scene.chart.XYChart;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;
import java.util.regex.Pattern;

public record EventListener(SerialPort serialPort, Consumer<Data> dataConsumer) implements SerialPortEventListener {
    private static final StringBuffer BUFFER = new StringBuffer();
    private static final Pattern MESSAGE_PATTERN =
            Pattern.compile("[-+]?[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?\\|[-+]?[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?");
    private static final String MESSAGE_SEPARATOR = "\n";
    private static final String FIELDS_SEPARATOR = "\\|";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("ss.ms")
            .withZone(ZoneId.systemDefault());

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
            var time = FORMATTER.format(Instant.now());

            System.out.println();
            System.out.println(light);
            System.out.println(temperature);
            dataConsumer().accept(new Data(
                    new XYChart.Data<>(time, light),
                    new XYChart.Data<>(time, temperature)
            ));
        } catch (SerialPortException e) {
            e.printStackTrace();
        } finally {
            BUFFER.append(data);
        }
    }

    public record Data(XYChart.Data<String, Number> light, XYChart.Data<String, Number> temperature) {
    }
}
