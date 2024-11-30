package com.example;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Book {
    private final StringProperty id;
    private final StringProperty title;
    private final StringProperty author;
    private final StringProperty isbn;
    private final StringProperty availability;
    private final StringProperty borrowerName;

    public Book(String id, String title, String author, String isbn, String availability, String borrowerName) {
        this.id = new SimpleStringProperty(id);
        this.title = new SimpleStringProperty(title);
        this.author = new SimpleStringProperty(author);
        this.isbn = new SimpleStringProperty(isbn);
        this.availability = new SimpleStringProperty(availability);
        this.borrowerName = new SimpleStringProperty(borrowerName);
    }

    // Getters and Setters

    public String getId() {
        return id.get();
    }

    public void setId(String value) {
        id.set(value);
    }

    public StringProperty idProperty() {
        return id;
    }

    public String getTitle() {
        return title.get();
    }

    public void setTitle(String value) {
        title.set(value);
    }

    public StringProperty titleProperty() {
        return title;
    }

    public String getAuthor() {
        return author.get();
    }

    public void setAuthor(String value) {
        author.set(value);
    }

    public StringProperty authorProperty() {
        return author;
    }

    public String getIsbn() {
        return isbn.get();
    }

    public void setIsbn(String value) {
        isbn.set(value);
    }

    public StringProperty isbnProperty() {
        return isbn;
    }

    public String getAvailability() {
        return availability.get();
    }

    public void setAvailability(String value) {
        availability.set(value);
    }

    public StringProperty availabilityProperty() {
        return availability;
    }

    public String getBorrowerName() {
        return borrowerName.get();
    }

    public void setBorrowerName(String value) {
        borrowerName.set(value);
    }

    public StringProperty borrowerNameProperty() {
        return borrowerName;
    }
}