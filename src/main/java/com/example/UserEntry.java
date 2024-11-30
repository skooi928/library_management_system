package com.example;

public class UserEntry {
    private final String icNumber;
    private final String name;

    public UserEntry(String icNumber, String name) {
        this.icNumber = icNumber;
        this.name = name;
    }

    public String getIcNumber() {
        return icNumber;
    }

    public String getName() {
        return name;
    }
}