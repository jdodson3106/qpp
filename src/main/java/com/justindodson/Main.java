package com.justindodson;

import com.justindodson.Controllers.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/main_ui.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("Quantum Principal Partners");
        primaryStage.setScene(new Scene(root, 1280, 762));
        primaryStage.show();

        MainController cont = loader.getController();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
