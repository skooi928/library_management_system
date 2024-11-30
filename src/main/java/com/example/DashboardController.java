package com.example;

import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.TextField;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DashboardController {

    @FXML
    private AnchorPane csvDropPane;

    @FXML
    private Button uploadCSVbtn;

    private TableView<Book> tableView;

    @FXML
    private Button addBookBtn;

    @FXML
    private Button borrowBookBtn;

    @FXML
    private Button returnBookBtn;

    @FXML
    private TextField searchField;

    private FilteredList<Book> filteredData;

    @FXML
    private void initialize() {
        setupDragAndDrop();
        initializeTableView();
        setupSearchFunctionality(); // add the event listener where the textfield is changed everytime
    }

    private void setupDragAndDrop() {
        csvDropPane.setOnDragOver((DragEvent event) -> {
            Dragboard db = event.getDragboard();
            if (db.hasFiles()) {
                boolean accept = false;
                for (File file : db.getFiles()) {
                    if (file.getName().toLowerCase().endsWith(".csv")) {
                        accept = true;
                        break;
                    }
                }
                if (accept) {
                    event.acceptTransferModes(TransferMode.COPY);
                }
            }
            event.consume();
        });

        csvDropPane.setOnDragDropped((DragEvent event) -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasFiles()) {
                List<File> files = db.getFiles();
                // Process only the first CSV file
                for (File file : files) {
                    if (file.getName().toLowerCase().endsWith(".csv")) {
                        try {
                            loadCSV(file);
                            success = true;
                        } catch (IOException e) {
                            showAlert("Error", "Failed to load CSV file.");
                        }
                        break;
                    }
                }
            }
            event.setDropCompleted(success);
            event.consume();
        });
    }

    private void initializeTableView() {
        tableView = new TableView<>();
        tableView.setPrefSize(csvDropPane.getWidth(), csvDropPane.getHeight());

        // Define table columns
        TableColumn<Book, String> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Book, String> titleColumn = new TableColumn<>("Title");
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));

        TableColumn<Book, String> authorColumn = new TableColumn<>("Author");
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));

        TableColumn<Book, String> isbnColumn = new TableColumn<>("ISBN");
        isbnColumn.setCellValueFactory(new PropertyValueFactory<>("isbn"));

        TableColumn<Book, String> availabilityColumn = new TableColumn<>("Availability");
        availabilityColumn.setCellValueFactory(new PropertyValueFactory<>("availability"));

        TableColumn<Book, String> borrowerNameColumn = new TableColumn<>("Borrower's Name");
        borrowerNameColumn.setCellValueFactory(new PropertyValueFactory<>("borrowerName"));

        // Add columns to TableView
        tableView.getColumns().addAll(idColumn, titleColumn, authorColumn, isbnColumn, availabilityColumn,
                borrowerNameColumn);

        filteredData = new FilteredList<>(Library.getInstance().getBooks(), p -> true); // only show p is true
        SortedList<Book> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tableView.comparatorProperty());
        tableView.setItems(sortedData);

        // Add TableView to AnchorPane
        AnchorPane.setTopAnchor(tableView, 0.0);
        AnchorPane.setBottomAnchor(tableView, 0.0);
        AnchorPane.setLeftAnchor(tableView, 0.0);
        AnchorPane.setRightAnchor(tableView, 0.0);
        csvDropPane.getChildren().add(tableView);
    }

    private void loadCSV(File file) throws IOException {
        Library.getInstance().setCurrentLoadedFile(file);

        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;
        List<Book> data = new ArrayList<>();
        String[] headers = null;

        // To read line by line
        int rowCount = 0;
        while ((line = br.readLine()) != null) {
            String[] values = line.split(",");
            if (rowCount == 0) {
                headers = values;
            } else {
                if (values.length < 6) {
                    // Handle missing fields by filling empty strings
                    String[] completeValues = new String[6];
                    System.arraycopy(values, 0, completeValues, 0, values.length);
                    for (int i = values.length; i < 6; i++) {
                        completeValues[i] = "";
                    }
                    values = completeValues;
                }
                Book book = new Book(
                        values[0].trim(),
                        values[1].trim(),
                        values[2].trim(),
                        values[3].trim(),
                        values[4].trim(),
                        values[5].trim());
                data.add(book);
            }
            rowCount++;
        }
        br.close(); // Remember to close file

        // Don't load if empty (without header)
        if (headers == null) {
            showAlert("Error", "CSV file is empty.");
            return;
        }

        // Clear existing data in Library
        Library.getInstance().getBooks().clear();

        // Add data to Library
        Library.getInstance().getBooks().addAll(data);

        // Derive the user CSV file name based on the library file name
        String libraryFileName = file.getName(); // e.g., "Library1.csv"
        int dotIndex = libraryFileName.lastIndexOf('.');
        String baseName = (dotIndex == -1) ? libraryFileName : libraryFileName.substring(0, dotIndex);
        String userFileName = baseName + "Users.csv"; // e.g., "Library1Users.csv"

        File userFile = new File(file.getParent(), userFileName);
        if (userFile.exists() && userFile.isFile()) {
            Library.getInstance().loadUsers(userFile);
        } else {
            showAlert("Error", "User data file \"" + userFileName + "\" not found.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void uploadViaBtn(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open CSV File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));

        File selectedFile = fileChooser.showOpenDialog(SceneManager.getPrimaryStage());
        if (selectedFile != null) {
            try {
                loadCSV(selectedFile);
            } catch (IOException e) {
                showAlert("Error", "Failed to load CSV file.");
            }
        }
    }

    @FXML
    private void addPopUp(ActionEvent event) {
        try {
            if (Library.getInstance().getBooks().isEmpty()) {
                showAlert("Error", "No CSV loaded.");
                return;
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("addBookDialog.fxml"));
            Parent newRoot = loader.load();

            AddBookController addBookController = loader.getController();

            Stage dialog = new Stage();
            dialog.setTitle("Add Book");
            dialog.setResizable(false);
            dialog.setScene(new Scene(newRoot));

            // Pass the stage to the controller
            addBookController.setDialogStage(dialog);

            // Make the dialog modal
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initOwner(SceneManager.getPrimaryStage());

            dialog.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Add book dialog failed to load.");
        }
    }

    @FXML
    private void borrowPopUp(ActionEvent event) {
        if (Library.getInstance().getBooks().isEmpty()) {
            showAlert("Error", "No books loaded.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("preBorrowBook.fxml"));
            Parent newRoot = loader.load();

            PreBorrowBookController preBorrowBookController = loader.getController();

            Stage dialog = new Stage();
            dialog.setTitle("Borrow Book");
            dialog.setResizable(false);
            dialog.setScene(new Scene(newRoot));

            // Pass the stage to the controller
            preBorrowBookController.setDialogStage(dialog);

            // Make the dialog modal
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initOwner(SceneManager.getPrimaryStage());

            dialog.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Borrow book dialog failed to load.");
        }
    }

    @FXML
    private void returnPopUp(ActionEvent event) {
        if (Library.getInstance().getBooks().isEmpty()) {
            showAlert("Error", "No books loaded.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("returnBookDialog.fxml"));
            Parent newRoot = loader.load();

            ReturnBookController returnBookController = loader.getController();

            Stage dialog = new Stage();
            dialog.setTitle("Return Book");
            dialog.setResizable(false);
            dialog.setScene(new Scene(newRoot));

            // Pass the stage to the controller
            returnBookController.setDialogStage(dialog);

            // Make the dialog modal
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initOwner(SceneManager.getPrimaryStage());

            dialog.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Return book dialog failed to load.");
        }
    }

    private void setupSearchFunctionality() {
        // Add listener to searchField to filter the list
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(book -> {
                // If search field is empty, display all books
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();

                if (book.getTitle().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches title
                } else if (book.getAuthor().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches author
                } else if (book.getIsbn().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches ISBN
                }
                return false; // Does not match
            });
        });
    }
}