package com.shivampaw.cpanelemailmanager.controller;

import com.shivampaw.cpanelemailmanager.EmailManager;
import com.shivampaw.cpanelemailmanager.model.Forwarder;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class NewForwarder {
    @FXML
    private TextField forwardFrom;
    @FXML
    private TextField forwardTo;

    /**
     * Create a new forwarder
     */
    Forwarder newForwarder()  {
        EmailManager.getInstance().createForwarder(forwardFrom.getText(), forwardTo.getText());
        return new Forwarder(forwardFrom.getText(), forwardTo.getText());
    }
}
