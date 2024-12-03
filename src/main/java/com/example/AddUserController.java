package com.example;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AddUserController {

    @FXML
    private TextField icNumberField;

    @FXML
    private TextField nameField;

    private Stage dialogStage;
    private boolean isSubmitted = false;

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    @FXML
    private void handleAddUser() {
        String icNumber = icNumberField.getText().trim();
        String name = nameField.getText().trim();

        if (icNumber.isEmpty() || name.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Invalid Input", "IC Number and Name cannot be empty.");
            return;
        }

        // Duplicate IC Number validation
        if (Library.getInstance().getUsersMap().containsKey(icNumber)) {
            showAlert(Alert.AlertType.ERROR, "Duplicate IC Number", "A user with this IC Number already exists.");
            return;
        }

        // Add the new user to the Library
        Library.getInstance().getUsersMap().put(icNumber, name);

        isSubmitted = true;
        dialogStage.close();
    }

    @FXML
    private void handleCancel() {
        dialogStage.close();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        if (type == Alert.AlertType.ERROR) {
            alert.setHeaderText("Error");
        } else {
            alert.setHeaderText(null);
        }
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public boolean isSubmitted() {
        return isSubmitted;
    }

    public String getIcNumber() {
        return icNumberField.getText().trim();
    }

    public String getName() {
        return nameField.getText().trim();
    }
}