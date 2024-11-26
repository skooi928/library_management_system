package com.example;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;

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

    private TableView<ObservableList<String>> tableView;

    @FXML
    private Button addBookBtn;

    @FXML
    private Button borrowBookBtn;

    @FXML
    private Button returnBookBtn;

    @FXML
    private void initialize() {
        setupDragAndDrop();
    }

    private void setupDragAndDrop() {
        csvDropPane.setOnDragOver(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
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
            }
        });

        csvDropPane.setOnDragDropped(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
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
            }
        });
    }

    private void loadCSV(File file) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;
        List<String[]> data = new ArrayList<>();
        String[] headers = null;

        int rowCount = 0;
        while ((line = br.readLine()) != null) {
            String[] values = line.split(",");
            if (rowCount == 0) {
                headers = values;
            } else {
                data.add(values);
            }
            rowCount++;
        }
        br.close();

        // Don't load if empty
        if (headers == null) {
            showAlert("Error", "CSV file is empty.");
            return;
        }

        // Initialize TableView
        if (tableView != null) {
            csvDropPane.getChildren().remove(tableView);
        }

        tableView = new TableView<>();
        tableView.setPrefSize(csvDropPane.getWidth(), csvDropPane.getHeight());

        // Create columns
        for (int i = 0; i < headers.length; i++) {
            final int colIndex = i;
            TableColumn<ObservableList<String>, String> column = new TableColumn<>(headers[i]);
            column.setCellValueFactory(param -> {
                ObservableList<String> row = param.getValue();
                if (colIndex >= row.size()) {
                    return new javafx.beans.property.SimpleStringProperty("");
                } else {
                    return new javafx.beans.property.SimpleStringProperty(row.get(colIndex));
                }
            });
            tableView.getColumns().add(column);
        }

        // Add data
        ObservableList<ObservableList<String>> tableData = FXCollections.observableArrayList();

        for (String[] rowData : data) {
            ObservableList<String> row = FXCollections.observableArrayList();
            for (String cell : rowData) {
                row.add(cell);
            }
            tableData.add(row);
        }

        tableView.setItems(tableData);

        // Add TableView to AnchorPane
        AnchorPane.setTopAnchor(tableView, 0.0);
        AnchorPane.setBottomAnchor(tableView, 0.0);
        AnchorPane.setLeftAnchor(tableView, 0.0);
        AnchorPane.setRightAnchor(tableView, 0.0);
        csvDropPane.getChildren().add(tableView);
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
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("addBookDialog.fxml"));
            DialogPane addBookDialogPane = loader.load();

            Dialog<ButtonType> dialog = new Dialog<>();

            dialog.setTitle("Add Books");
            dialog.setResizable(false);
            dialog.setDialogPane(addBookDialogPane);

            // At the same time, stop the user from interacting with the main window
            dialog.initModality(Modality.APPLICATION_MODAL);
            Optional<ButtonType> clickedButton = dialog.showAndWait();

            if (clickedButton.get() == ButtonType.OK) {
                System.out.println("OK button clicked");
                // implement the logic to add the book
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}