package com.shivampaw.cem.java.datamodel;

import javafx.beans.property.SimpleStringProperty;

public class EmailForwarder {
    private SimpleStringProperty destination = new SimpleStringProperty();
    private SimpleStringProperty forward = new SimpleStringProperty();

    public EmailForwarder(String forward, String dest) {
        this.forward.set(forward);
        this.destination.set(dest);
    }

    public String getDestination() {
        return destination.get();
    }

    public String getForward() {
        return forward.get();
    }
}
