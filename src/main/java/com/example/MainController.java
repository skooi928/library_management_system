package com.example;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

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
}