package com.example;

import java.util.Map;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
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
    private void initialize() {
        setupBorrowerNameValidation();
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
        if (id.isEmpty() || title.isEmpty() || author.isEmpty() || isbn.isEmpty() || availability.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Invalid Input");
            alert.setContentText("Please fill in all the required fields.");
            alert.showAndWait();
            return;
        }

        // Validate available type
        if (availability.equals("Available") && availability.equals("Borrowed")) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Invalid Availability");
            alert.setContentText("The entry can only be Available or Borrowed!");
            alert.showAndWait();
            return;
        }

        // Validate available + borrower
        if (!(availability.equals("Available") && borrowerName.equals(""))
                || !(availability.equals("Borrowed") && isValidBorrower(borrowerName))) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Invalid availability and borrower entry!");
            alert.setContentText(
                    "Available must have null borrower entry, while Borrowed must have a correct borrower name.");
            alert.showAndWait();
            return;
        }

        // Validate Borrower's Name
        if (!isValidBorrower(borrowerName)) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Invalid Borrower");
            alert.setContentText("This user is not in our system.");
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

    private boolean isValidBorrower(String borrowerName) {
        if (borrowerName.isEmpty()) {
            return false;
        }

        // Retrieve the users map from Library
        Map<String, String> usersMap = Library.getInstance().getUsersMap();

        if (usersMap == null || usersMap.isEmpty()) {
            return false;
        }

        // Check if the borrower name exists in the users map values
        return usersMap.containsValue(borrowerName);
    }

    // Real time changing the border color
    @FXML
    private void setupBorrowerNameValidation() {
        bookBorrowerName.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!Library.getInstance().getUsersMap().containsValue(newValue)) {
                bookBorrowerName.setStyle("-fx-border-color: red;");
            } else {
                // Reset to default style
                bookBorrowerName.setStyle("");
            }
        });
    }

    @FXML
    private void closeDialogStage() {
        dialogStage.close();
    }

}
