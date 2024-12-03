package com.example;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SceneManager {

    private static Stage primaryStage;
    private static String currentFXML;

    // Initialize with dashboard.fxml
    public static void initialize(Stage stage) throws IOException {
        primaryStage = stage;
        switchScene("dashboard.fxml");
    }

    // Switch to a new scene and update the currentFXML
    public static void switchScene(String fxmlFile) throws IOException {
        if (fxmlFile.equals(currentFXML)) {
            return; // Avoid reloading the same scene
        }

        currentFXML = fxmlFile;

        // Load new scene
        FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource(fxmlFile));
        Parent root = loader.load();

        // Set scene
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public static String getCurrentFXML() {
        return currentFXML;
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }
}