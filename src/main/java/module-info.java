module com.morka.serial.read.povs {
    requires javafx.controls;
    requires javafx.fxml;
    requires RXTXcomm;

    opens com.morka.serial.read.povs4 to javafx.fxml;
    exports com.morka.serial.read.povs4;
}
