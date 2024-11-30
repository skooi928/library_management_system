package com.example;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class MainController {

    @FXML
    private Button viewDashboardBtn;

    @FXML
    private Button userInfoBtn;

    @FXML
    private Button settingBtn;

    @FXML
    private Button aboutBtn;

    @FXML
    private void initialize() {
        resetButtonStyles();
        setButtonStyleBasedOnCurrentFXML();
    }

    private String getCurrentFXML() {
        return SceneManager.getCurrentFXML();
    }

    private void setButtonStyleBasedOnCurrentFXML() {
        String currentFXML = getCurrentFXML();
        if (currentFXML != null) {
            switch (currentFXML) {
                case "dashboard.fxml":
                    viewDashboardBtn.getStyleClass().add("selected");
                    break;
                case "userInfo.fxml":
                    userInfoBtn.getStyleClass().add("selected");
                    break;
                case "settings.fxml":
                    settingBtn.getStyleClass().add("selected");
                    break;
                case "about.fxml":
                    aboutBtn.getStyleClass().add("selected");
                    break;
                default:
                    break;
            }
        }
    }

    @FXML
    private void handleButtonClick(ActionEvent event) {
        Button clickedButton = (Button) event.getSource();
        String targetFXML = null;

        if (clickedButton == viewDashboardBtn) {
            targetFXML = "dashboard.fxml";
        } else if (clickedButton == userInfoBtn) {
            targetFXML = "userInfo.fxml";
        } else if (clickedButton == settingBtn) {
            targetFXML = "settings.fxml";
        } else if (clickedButton == aboutBtn) {
            targetFXML = "about.fxml";
        }

        handleButtonAction(clickedButton, targetFXML);
    }

    private void resetButtonStyles() {
        viewDashboardBtn.getStyleClass().remove("selected");
        userInfoBtn.getStyleClass().remove("selected");
        settingBtn.getStyleClass().remove("selected");
        aboutBtn.getStyleClass().remove("selected");
    }

    private void handleButtonAction(Button button, String fxmlFileName) {
        try {
            SceneManager.switchScene(fxmlFileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSave(ActionEvent event) {
        File currentFile = Library.getInstance().getCurrentLoadedFile();

        if (currentFile == null) {
            showAlert("Error", "No CSV file loaded, can't save!");
            return;
        }

        // Save Library Data
        boolean librarySaved = saveLibraryData(currentFile);

        if (librarySaved) {
            showAlert("Success", "Library data saved successfully.");
        }
    }

    private boolean saveLibraryData(File libraryFile) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(libraryFile))) {
            // Write header
            writer.write("ID,Title,Author,ISBN,Availability,BorrowerName");
            writer.newLine();

            for (Book book : Library.getInstance().getBooks()) {
                String line = String.format("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"",
                        (book.getId()),
                        (book.getTitle()),
                        (book.getAuthor()),
                        (book.getIsbn()),
                        (book.getAvailability()),
                        (book.getBorrowerName()));
                writer.write(line);
                writer.newLine();
            }

            return true;
        } catch (IOException e) {
            showAlert("Error", "Failed to save library data.");
            e.printStackTrace();
            return false;
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        if (title.equalsIgnoreCase("Error")) {
            alert.setAlertType(AlertType.ERROR);
        }
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleQuit(ActionEvent event) {
        Stage stage = SceneManager.getPrimaryStage();
        stage.close();
    }

}