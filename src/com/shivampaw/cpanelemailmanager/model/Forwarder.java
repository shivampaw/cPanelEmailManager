package com.shivampaw.cpanelemailmanager.model;

import javafx.beans.property.SimpleStringProperty;

public class Forwarder {
    private SimpleStringProperty destination = new SimpleStringProperty();
    private SimpleStringProperty forward = new SimpleStringProperty();

    public Forwarder(String forward, String dest) {
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
