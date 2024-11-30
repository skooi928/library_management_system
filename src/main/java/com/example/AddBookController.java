package com.example;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AddBookController {

    @FXML
    private TextField bookID;

    @FXML
    private TextField bookTitle;

    @FXML
    private TextField bookAuthor;

    @FXML
    private TextField bookISBN;

    @FXML
    private TextField bookAvailability;

    @FXML
    private TextField bookBorrowerName;

    private Stage dialogStage;

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    @FXML
    private void addBook() {
        String id = bookID.getText().trim(); // using .trim() to remove excess whitespaces
        String title = bookTitle.getText().trim(); // using .trim() to remove excess whitespaces
        String author = bookAuthor.getText().trim(); // using .trim() to remove excess whitespaces
        String isbn = bookISBN.getText().trim(); // using .trim() to remove excess whitespaces
        String availability = bookAvailability.getText().trim(); // using .trim() to remove excess whitespaces
        String borrowerName = bookBorrowerName.getText().trim(); // using .trim() to remove excess whitespaces

        // Input validation
        if (id.isEmpty() || title.isEmpty() || author.isEmpty() || isbn.isEmpty()) {
            // Show an alert or error message
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Invalid Input");
            alert.setContentText("Please fill in all the required fields.");
            alert.showAndWait();
            return;
        }

        Book newBook = new Book(id, title, author, isbn, availability, borrowerName);

        // Add to the shared Library
        Library.getInstance().addBook(newBook);

        // Optionally, show confirmation
        Alert confirmation = new Alert(Alert.AlertType.INFORMATION);
        confirmation.setTitle("Success");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Book added successfully!");
        confirmation.showAndWait();

        // Close the dialog
        dialogStage.close();
    }

    @FXML
    private void closeDialogStage() {
        dialogStage.close();
    }

}
