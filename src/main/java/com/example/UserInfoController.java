package com.example;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.Map;

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
        // Bind TableView columns to UserEntry properties
        icNumberColumn.setCellValueFactory(new PropertyValueFactory<>("icNumber"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        // Initialize the ObservableList with user data
        userList = FXCollections.observableArrayList();
        filteredUsers = new FilteredList<>(userList, p -> true);
        SortedList<UserEntry> sortedData = new SortedList<>(filteredUsers);
        sortedData.comparatorProperty().bind(userTableView.comparatorProperty());

        userTableView.setItems(sortedData);
    }

    // Add the user from the hashmap to the observable list
    private void loadUsers() {
        Map<String, String> usersMap = Library.getInstance().getUsersMap();
        for (Map.Entry<String, String> entry : usersMap.entrySet()) {
            userList.add(new UserEntry(entry.getKey(), entry.getValue()));
        }
    }

    private void setupSearchFunctionality() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredUsers.setPredicate(user -> {
                // If search field is empty, display all users
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();

                // Search by Name
                if (user.getName().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                // Search by IC Number
                else if (user.getIcNumber().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                return false; // Does not match
            });
        });
    }
}