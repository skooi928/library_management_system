package com.example;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class DashboardController {

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
        handleButtonAction(viewDashboardBtn, "dashboard");
        // viewDashboardBtn.setOnAction(event -> switchScene("dashboard"));
        // userInfoBtn.setOnAction(event -> switchScene("user_info"));
        // settingBtn.setOnAction(event -> switchScene("settings"));
        // aboutBtn.setOnAction(event -> switchScene("about"));
    }

    @FXML
    private void handleButtonClick(ActionEvent event) {
        Button clickedButton = (Button) event.getSource();
        if (clickedButton == viewDashboardBtn) {
            handleButtonAction(viewDashboardBtn, "dashboard");
        } else if (clickedButton == userInfoBtn) {
            handleButtonAction(userInfoBtn, "user_info");
        } else if (clickedButton == settingBtn) {
            handleButtonAction(settingBtn, "settings");
        } else if (clickedButton == aboutBtn) {
            handleButtonAction(aboutBtn, "about");
        }
    }

    private void resetButtonStyles() {
        viewDashboardBtn.getStyleClass().remove("selected");
        userInfoBtn.getStyleClass().remove("selected");
        settingBtn.getStyleClass().remove("selected");
        aboutBtn.getStyleClass().remove("selected");
    }

    private void handleButtonAction(Button button, String fxml) {
        resetButtonStyles();
        button.getStyleClass().add("selected");
        // switchScene(fxml);
    }

    // private void switchScene(String fxml) {
    // try {
    // App.setRoot(fxml);
    // } catch (IOException e) {
    // e.printStackTrace();
    // }
    // }
}