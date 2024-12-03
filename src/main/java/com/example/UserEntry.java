package com.example;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class UserEntry {
    private final StringProperty icNumber;
    private final StringProperty name;

    public UserEntry(String icNumber, String name) {
        this.icNumber = new SimpleStringProperty(icNumber);
        this.name = new SimpleStringProperty(name);
    }

    public String getIcNumber() {
        return icNumber.get();
    }

    public String getName() {
        return name.get();
    }
}