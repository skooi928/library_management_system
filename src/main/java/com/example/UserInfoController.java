package com.example;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;

public class UserInfoController {

    @FXML
    private TextField searchField;

    @FXML
    private TableView<UserEntry> userTableView;

    @FXML
    private TableColumn<UserEntry, String> icNumberColumn;

    @FXML
    private TableColumn<UserEntry, String> nameColumn;

    private ObservableList<UserEntry> userList;
    private FilteredList<UserEntry> filteredUsers;

    @FXML
    private void initialize() {
        initializeTableView();
        loadUsers();
        setupSearchFunctionality();
    }

    private void initializeTableView() {
        icNumberColumn.setCellValueFactory(new PropertyValueFactory<>("icNumber"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        userList = FXCollections.observableArrayList();
        filteredUsers = new FilteredList<>(userList, p -> true);
        SortedList<UserEntry> sortedData = new SortedList<>(filteredUsers);
        sortedData.comparatorProperty().bind(userTableView.comparatorProperty());

        userTableView.setItems(sortedData);
    }

    // add users to observable list
    private void loadUsers() {
        Map<String, String> usersMap = Library.getInstance().getUsersMap();
        for (Map.Entry<String, String> entry : usersMap.entrySet()) {
            userList.add(new UserEntry(entry.getKey(), entry.getValue()));
        }
    }

    private void setupSearchFunctionality() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredUsers.setPredicate(user -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();

                if (user.getName().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (user.getIcNumber().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                return false;
            });
        });
    }

    @FXML
    private void addUser() {
        if (Library.getInstance().getBooks().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "No CSV loaded.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AddUserDialog.fxml"));
            DialogPane dialogPane = loader.load();

            AddUserController controller = loader.getController();
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Add New User");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(userTableView.getScene().getWindow());
            Scene scene = new Scene(dialogPane);
            dialogStage.setScene(scene);
            controller.setDialogStage(dialogStage);
            dialogStage.showAndWait();

            if (controller.isSubmitted()) {
                String icNumber = controller.getIcNumber();
                String name = controller.getName();
                userList.add(new UserEntry(icNumber, name));
                showAlert(Alert.AlertType.INFORMATION, "Success", "User added successfully.");
            }

            saveUserData();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load Add User dialog.");
        }
    }

    @FXML
    private void deleteUser() {
        if (Library.getInstance().getBooks().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "No CSV loaded.");
            return;
        }

        UserEntry selectedUser = userTableView.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a user to delete.");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Delete Confirmation");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Are you sure you want to delete the selected user?");
        Optional<ButtonType> result = confirmation.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            userList.remove(selectedUser); // remove the user from the list (since observable list is bound to the table
                                           // view, table view is sync directly)
            Library.getInstance().getUsersMap().remove(selectedUser.getIcNumber()); // remove the ic number from the map
            showAlert(Alert.AlertType.INFORMATION, "Deleted", "User deleted successfully.");
        }

        saveUserData();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void saveUserData() {
        File currentFile = Library.getInstance().getCurrentLoadedFile();
        String libraryFileName = currentFile.getName();
        int dotIndex = libraryFileName.lastIndexOf('.');
        String baseName = (dotIndex == -1) ? libraryFileName : libraryFileName.substring(0, dotIndex);
        String userFileName = baseName + "Users.csv";
        File userFile = new File(currentFile.getParent(), userFileName);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(userFile))) {
            // Write header
            writer.write("IC Number,Name");
            writer.newLine();

            for (Map.Entry<String, String> entry : Library.getInstance().getUsersMap().entrySet()) {
                String line = String.format("%s,%s",
                        (entry.getKey()),
                        (entry.getValue()));
                writer.write(line);
                writer.newLine();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}