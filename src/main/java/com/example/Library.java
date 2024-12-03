package com.example;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.Writer;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.prefs.Preferences;

public class Library {
    private static Library instance = null;
    private final ArrayList<Book> booksList;
    private final ObservableList<Book> books;
    private final FilteredList<Book> filteredBooks;
    private final Map<String, String> users; // Map to store user data (IC -> Name)
    private File currentLoadedFile;

    private static final String PREF_KEY_LAST_FILE = "lastLoadedFilePath";
    private final Preferences prefs;

    private Library() {
        booksList = new ArrayList<>();
        books = FXCollections.observableArrayList(booksList);
        filteredBooks = new FilteredList<>(books, p -> true);
        users = new HashMap<>();
        prefs = Preferences.userNodeForPackage(Library.class);
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
        book.borrowBook(borrowerName);
    }

    public void returnBook(Book book) {
        book.returnBook();
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

    public FilteredList<Book> getFilteredBooks() {
        return filteredBooks;
    }

    // search method
    public void setSearchFilter(String query) {
        if (query == null || query.trim().isEmpty()) {
            filteredBooks.setPredicate(book -> true); // set predicate to be all true to show all books
            return;
        }

        String lowerCaseQuery = query.toLowerCase();

        filteredBooks.setPredicate(book -> book.getId().toLowerCase().contains(lowerCaseQuery) ||
                book.getTitle().toLowerCase().contains(lowerCaseQuery) ||
                book.getAuthor().toLowerCase().contains(lowerCaseQuery) ||
                book.getIsbn().toLowerCase().contains(lowerCaseQuery));
    }

    public File getCurrentLoadedFile() {
        return currentLoadedFile;
    }

    public void setCurrentLoadedFile(File currentLoadedFile) {
        this.currentLoadedFile = currentLoadedFile;
        if (currentLoadedFile != null) {
            prefs.put(PREF_KEY_LAST_FILE, currentLoadedFile.getAbsolutePath());
        }
    }

    public File getLastLoadedFile() {
        String path = prefs.get(PREF_KEY_LAST_FILE, null);
        if (path != null) {
            File file = new File(path);
            if (file.exists() && file.isFile()) {
                return file;
            }
        }
        return null;
    }

    public void saveCSV() throws IOException {
        if (currentLoadedFile == null) {
            throw new IOException("No CSV file is currently loaded.");
        }

        try (Writer writer = new BufferedWriter(new FileWriter(currentLoadedFile))) {
            // Write header
            writer.write("ID,Title,Author,ISBN,Availability,BorrowerName\n");

            // Write each book's data
            for (Book book : books) {
                writer.write(String.format("%s,%s,%s,%s,%s,%s\n",
                        (book.getId()),
                        (book.getTitle()),
                        (book.getAuthor()),
                        (book.getIsbn()),
                        (book.getAvailability()),
                        (book.getBorrowerName())));
            }
        }
    }
}