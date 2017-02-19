package com.shivampaw.cpanelemailmanager.controller;

import com.shivampaw.cpanelemailmanager.EmailManager;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class NewForwarder {
    @FXML
    private TextField forwardFrom;
    @FXML
    private TextField forwardTo;

    void newForwarder()  {
        EmailManager.getInstance().createForwarder(forwardFrom.getText(), forwardTo.getText());
    }
}
