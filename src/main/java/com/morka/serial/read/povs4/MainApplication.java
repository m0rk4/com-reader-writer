package com.morka.serial.read.povs4;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MainApplication extends Application {

    private static final ExecutorService THREAD_POOL = Executors.newSingleThreadExecutor();
    private static final int THREAD_POOL_TERMINATION_TIME_IN_SECONDS = 60;

    private static void destroyThreadPool() {
        THREAD_POOL.shutdown();
        try {
            if (!THREAD_POOL.awaitTermination(THREAD_POOL_TERMINATION_TIME_IN_SECONDS, TimeUnit.SECONDS))
                THREAD_POOL.shutdownNow();
        } catch (InterruptedException e) {
            THREAD_POOL.shutdownNow();
        }
    }

    @Override
    public void start(Stage stage) throws IOException {
        var fxmlLoader = new FXMLLoader(MainApplication.class.getResource("main-view.fxml"));
        fxmlLoader.setControllerFactory(c -> new MainController(THREAD_POOL));
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
        destroyThreadPool();
    }
}
