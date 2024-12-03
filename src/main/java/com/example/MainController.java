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

    // @FXML
    // private Button settingBtn;

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

    // add selected to change the color of the button
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
                // case "settings.fxml":
                // settingBtn.getStyleClass().add("selected");
                // break;
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
        }
        // else if (clickedButton == settingBtn) {
        // targetFXML = "settings.fxml";
        // }
        else if (clickedButton == aboutBtn) {
            targetFXML = "about.fxml";
        }

        handleButtonAction(clickedButton, targetFXML);
    }

    private void resetButtonStyles() {
        viewDashboardBtn.getStyleClass().remove("selected");
        userInfoBtn.getStyleClass().remove("selected");
        // settingBtn.getStyleClass().remove("selected");
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

        boolean librarySaved = saveLibraryData(currentFile);

        if (librarySaved) {
            // Derive User File Name
            String libraryFileName = currentFile.getName(); // e.g. "Library1.csv"
            int dotIndex = libraryFileName.lastIndexOf('.');
            String baseName = (dotIndex == -1) ? libraryFileName : libraryFileName.substring(0, dotIndex);
            String userFileName = baseName + "Users.csv"; // e.g. "Library1Users.csv"

            File userFile = new File(currentFile.getParent(), userFileName);

            // Save User Data
            boolean usersSaved = saveUserData(userFile);

            if (usersSaved) {
                showAlert("Success", "Library and User data saved successfully.");
            }
        }
    }

    private boolean saveLibraryData(File libraryFile) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(libraryFile))) {
            // Header
            writer.write("ID,Title,Author,ISBN,Availability,BorrowerName");
            writer.newLine();

            for (Book book : Library.getInstance().getBooks()) {
                String line = String.format("%s,%s,%s,%s,%s,%s",
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

    private boolean saveUserData(File userFile) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(userFile))) {
            // Write header
            writer.write("IC Number,Name");
            writer.newLine();

            for (Map.Entry<String, String> entry : Library.getInstance().getUsersMap().entrySet()) {
                String line = String.format("%s,%s",
                        (entry.getKey()),
                        (entry.getValue()));
                writer.write(line);
                writer.newLine();
            }

            return true;
        } catch (IOException e) {
            showAlert("Error", "Failed to save user data.");
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
//      handleSave(event);
        Stage stage = SceneManager.getPrimaryStage();
        stage.close();
    }

}