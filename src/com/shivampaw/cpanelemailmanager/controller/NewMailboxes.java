package com.shivampaw.cpanelemailmanager.controller;

import com.shivampaw.cpanelemailmanager.EmailManager;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class NewMailboxes {
    @FXML
    private TextField mailboxUsername;
    @FXML
    private PasswordField mailboxPassword;
    @FXML
    private TextField mailboxQuota;

    /**
     * Create a new mailbox
     */
    void newMailbox()  {
        EmailManager.getInstance().createMailbox(mailboxUsername.getText(), mailboxPassword.getText(), mailboxQuota.getText());
    }
}
