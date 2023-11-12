package com.example.marketoptimizer;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


import java.io.IOException;
import java.util.Objects;

public class OptimizerApplication extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(OptimizerApplication.class.getResource("start-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1920, 1080);
        stage.setTitle("MarketOptimizer");
        primaryStage = stage;
        stage.setScene(scene);
        stage.show();
    }


    public static void changeScene(String fxml) throws IOException {
        Parent pane = FXMLLoader.load(Objects.requireNonNull(OptimizerApplication.class.getResource(fxml)));
        primaryStage.getScene().setRoot(pane);
    }

    public static void main(String[] args) {
        launch();
    }

}