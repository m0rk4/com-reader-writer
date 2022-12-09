module com.morka.serial.read.povs {
    requires javafx.controls;
    requires javafx.fxml;
    requires jssc;

    opens com.morka.serial.read.povs4 to javafx.fxml;
    exports com.morka.serial.read.povs4;
    exports com.morka.serial.read.povs4.model;
    opens com.morka.serial.read.povs4.model to javafx.fxml;
}
