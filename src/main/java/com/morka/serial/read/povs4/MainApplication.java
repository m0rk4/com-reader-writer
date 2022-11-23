package com.morka.serial.read.povs4;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApplication extends Application {

    private static final MainController CONTROLLER = new MainController();

    @Override
    public void start(Stage stage) throws IOException {
        var fxmlLoader = new FXMLLoader(MainApplication.class.getResource("main-view.fxml"));
        fxmlLoader.setControllerFactory(c -> CONTROLLER);
        var scene = new Scene(fxmlLoader.load());
        stage.setTitle("COM3 - Read");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void stop() {
        CONTROLLER.onStop();
    }
}
