package com.example;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class BorrowBookController {
    @FXML
    private TextField bookID;

    private Stage dialogStage;
    private String borrowerICNo;

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setBorrowerICNo(String borrowerICNo) {
        this.borrowerICNo = borrowerICNo;
    }

    @FXML
    private void borrowBook() {
        String bookId = bookID.getText().trim();
        if (bookId.isEmpty()) {
            showAlert("Validation Error", "Please enter the Book ID.");
            return;
        }

        Book bookToBorrow = null;
        for (Book book : Library.getInstance().getBooks()) {
            if (book.getId().equals(bookId)) {
                if (book.getAvailability().equals("Available")) {
                    bookToBorrow = book;
                    break;
                } else {
                    showAlert("Error", "Book is not available for borrowing.");
                    return;
                }
            }
        }

        if (bookToBorrow == null) {
            showAlert("Error", "Book with ID " + bookId + " not found.");
            return;
        }

        String borrowerName = Library.getInstance().getUserName(borrowerICNo);
        Library.getInstance().borrowBook(bookToBorrow, borrowerName);

        showAlert("Success", "Book borrowed successfully by " + borrowerName + ".");

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