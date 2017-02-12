package com.shivampaw.cpanelemailmanager.controller;

import com.shivampaw.cpanelemailmanager.EmailManager;
import com.shivampaw.cpanelemailmanager.model.EmailForwarder;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class NewForwarderController {
    @FXML
    private TextField forwardFrom;
    @FXML
    private TextField forwardTo;

    /**
     * Create a new email account
     */
    EmailForwarder newForwarder()  {
        EmailManager.getInstance().createEmailForwarder(forwardFrom.getText(), forwardTo.getText());
        return new EmailForwarder(forwardFrom.getText(), forwardTo.getText());
    }
}
