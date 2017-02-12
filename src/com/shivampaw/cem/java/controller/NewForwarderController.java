package com.shivampaw.cem.java.controller;

import com.shivampaw.cem.java.EmailManager;
import com.shivampaw.cem.java.datamodel.EmailForwarder;
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
    public EmailForwarder newForwarder()  {
        EmailManager.getInstance().createEmailForwarder(forwardFrom.getText(), forwardTo.getText());
        EmailForwarder fwd = new EmailForwarder(forwardFrom.getText(), forwardTo.getText());
        EmailManager.getInstance().getForwardAccounts().add(fwd);

        return fwd;
    }
}
