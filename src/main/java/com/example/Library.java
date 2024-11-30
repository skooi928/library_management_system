package com.example;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Library {
    private static Library instance = null;
    private final ObservableList<Book> books;
    private final Map<String, String> users; // Map to store user data (IC -> Name)
    private File currentLoadedFile;

    private Library() {
        books = FXCollections.observableArrayList();
        users = new HashMap<>();
    }

    public static synchronized Library getInstance() {
        if (instance == null) {
            instance = new Library();
        }
        return instance;
    }

    public ObservableList<Book> getBooks() {
        return books;
    }

    public void addBook(Book book) {
        books.add(book);
    }

    public void removeBook(Book book) {
        books.remove(book);
    }

    public void borrowBook(Book book, String borrowerName) {
        book.setAvailability("Borrowed");
        book.setBorrowerName(borrowerName);
    }

    public void returnBook(Book book) {
        book.setAvailability("Available");
        book.setBorrowerName("");
    }

    public void loadUsers(File file) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;
        boolean isHeader = true;
        while ((line = br.readLine()) != null) {
            if (isHeader) { // Skip header
                isHeader = false;
                continue;
            }
            String[] values = line.split(",");
            if (values.length >= 2) {
                String ic = values[0].trim();
                String name = values[1].trim();
                users.put(ic, name);
            }
        }
        br.close();
    }

    public boolean userExists(String ic) {
        return users.containsKey(ic);
    }

    public String getUserName(String ic) {
        return users.get(ic);
    }

    public Map<String, String> getUsersMap() {
        return users;
    }

    public File getCurrentLoadedFile() {
        return currentLoadedFile;
    }

    public void setCurrentLoadedFile(File currentLoadedFile) {
        this.currentLoadedFile = currentLoadedFile;
    }
}