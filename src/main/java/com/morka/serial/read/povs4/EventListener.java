package com.morka.serial.read.povs4;

import com.morka.serial.read.povs4.model.MetricsResponse;
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

public record EventListener(SerialPort serialPort,
                            Consumer<MetricsResponse> dataConsumer) implements SerialPortEventListener {
    private static final StringBuffer BUFFER = new StringBuffer();
    private static final Pattern METRICS_MESSAGE_PATTERN =
            Pattern.compile(
                    "(null|[-+]?[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?)\\|" +
                            "(null|[-+]?[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?)\\|" +
                            "(null|[-+]?[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?)"
            );

    private static final String NO_DATA = "null";
    private static final String MESSAGE_SEPARATOR = "\n";
    private static final String FIELDS_SEPARATOR = "\\|";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter
            .ofPattern("mm.ss")
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

            if (!METRICS_MESSAGE_PATTERN.matcher(message).matches())
                return;

            var fields = message.split(FIELDS_SEPARATOR);

            var p = fields[0].trim();
            var l = fields[1].trim();
            var t = fields[2].trim();

            var time = DATE_TIME_FORMATTER.format(Instant.now());
            dataConsumer().accept(new MetricsResponse(
                    NO_DATA.equals(p) ? null : new XYChart.Data<>(time, Double.parseDouble(p)),
                    NO_DATA.equals(l) ? null : new XYChart.Data<>(time, Double.parseDouble(l)),
                    NO_DATA.equals(t) ? null : new XYChart.Data<>(time, Double.parseDouble(t))
            ));
        } catch (SerialPortException e) {
            e.printStackTrace();
        } finally {
            BUFFER.append(data);
        }
    }
}
