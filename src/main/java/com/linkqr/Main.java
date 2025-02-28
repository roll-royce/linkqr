package com.linkqr;

import java.net.URL;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            // Correct path for FXML
            URL fxmlLocation = getClass().getResource("/com/linkqr/login.fxml");
            if (fxmlLocation == null) {
                throw new RuntimeException("❌ FXML file not found! Check the path: /com/linkqr/login.fxml");
            }

            Parent root = FXMLLoader.load(fxmlLocation);
            primaryStage.setTitle("LinkQR - Login");
            primaryStage.setScene(new Scene(root));
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("❌ Error loading FXML: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
