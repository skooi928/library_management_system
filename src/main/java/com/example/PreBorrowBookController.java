package com.example;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class PreBorrowBookController {
    @FXML
    private TextField borrowerICNo;

    private Stage dialogStage;

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public String getBorrowerICNo() {
        return borrowerICNo.getText();
    }

    @FXML
    private void next() {
        String icNo = getBorrowerICNo();
        if (icNo.isEmpty()) {
            showAlert("Validation Error", "Please enter your IC number.");
            return;
        }

        if (!Library.getInstance().userExists(icNo)) {
            showAlert("Error", "User not found.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("borrowBookDialog.fxml"));
            Parent newRoot = loader.load();

            BorrowBookController borrowBookController = loader.getController();
            borrowBookController.setBorrowerICNo(icNo);
            borrowBookController.setDialogStage(dialogStage);

            dialogStage.setScene(new Scene(newRoot));
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load borrow book dialog.");
        }
    }

    @FXML
    private void close() {
        dialogStage.close();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}