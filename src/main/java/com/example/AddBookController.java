package com.example;

import java.io.IOException;
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

        // Validate duplicate ID
        if (Library.getInstance().getBooks().stream()
                .anyMatch(book -> book.getId().equals(id))) { // .stream() converts the ObservableList to a Stream
            // anyMatch checks if the condition is true or not for the predicate defined
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Duplicate ID");
            alert.setContentText("A book with this ID already exists.");
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
        if (!(availability.equals("Available") && borrowerName.equals("")
                || availability.equals("Borrowed") && isValidBorrower(borrowerName))) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Invalid availability and borrower entry!");
            alert.setContentText(
                    "Available must have null borrower entry, while Borrowed must have a correct borrower name.");
            alert.showAndWait();
            return;
        }

        Book newBook = new Book(id, title, author, isbn, availability, borrowerName);

        // Add the new book to the Library
        Library.getInstance().addBook(newBook);

        Alert confirmation = new Alert(Alert.AlertType.INFORMATION);
        confirmation.setTitle("Success");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Book added successfully!");
        confirmation.showAndWait();

        // Save the updated book list to CSV
        try {
            Library.getInstance().saveCSV();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        dialogStage.close();
    }

    private boolean isValidBorrower(String borrowerName) {
        if (borrowerName.isEmpty()) {
            return false;
        }

        // get the users map from Library
        Map<String, String> usersMap = Library.getInstance().getUsersMap();

        if (usersMap == null || usersMap.isEmpty()) {
            return false;
        }

        // check name in the list or not
        return usersMap.containsValue(borrowerName);
    }

    // Real time changing the border color
    @FXML
    private void setupBorrowerNameValidation() {
        bookBorrowerName.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!Library.getInstance().getUsersMap().containsValue(newValue)) {
                bookBorrowerName.setStyle("-fx-border-color: red;"); // if not exist
            } else {
                bookBorrowerName.setStyle(""); // if exist
            }
        });
    }

    @FXML
    private void closeDialogStage() {
        dialogStage.close();
    }

}
