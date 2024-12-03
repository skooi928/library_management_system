package com.example;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ReturnBookController {
    @FXML
    private TextField bookID;

    private Stage dialogStage;

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    @FXML
    private void returnBook() {
        String bookId = bookID.getText().trim();
        if (bookId.isEmpty()) {
            showAlert("Validation Error", "Please enter the Book ID.");
            return;
        }

        // Validate if the book is borrowed or not to be returned
        Book bookToReturn = null;
        for (Book book : Library.getInstance().getBooks()) {
            if (book.getId().equals(bookId)) {
                if (book.getAvailability().equalsIgnoreCase("Borrowed")) {
                    bookToReturn = book;
                    break;
                } else {
                    showAlert("Error", "Book is not currently borrowed.");
                    return;
                }
            }
        }

        if (bookToReturn == null) {
            showAlert("Error", "Book with ID " + bookId + " not found.");
            return;
        }

        Library.getInstance().returnBook(bookToReturn);
        showAlert("Success", "Book returned successfully.");

        // Save the updated book list to CSV
        try {
            Library.getInstance().saveCSV();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        dialogStage.close();
    }

    @FXML
    private void close() {
        dialogStage.close();
    }

    private void showAlert(String title, String message) {
        Alert.AlertType type = title.equalsIgnoreCase("Success") ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR;
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}