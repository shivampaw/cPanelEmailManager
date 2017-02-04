package com.shivampaw.cem.java;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    public static Stage parentWindow;

    @Override
    public void start(Stage primaryStage) throws Exception{
        parentWindow = primaryStage;

        Parent root = FXMLLoader.load(getClass().getResource("/com/shivampaw/cem/resources/LoginWindow.fxml"));
        primaryStage.setTitle("cPanel Email Manager");
        primaryStage.setScene(new Scene(root, 900, 600));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
