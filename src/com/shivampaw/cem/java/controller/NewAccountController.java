package com.shivampaw.cem.java.controller;

import com.shivampaw.cem.java.EmailManager;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class NewAccountController {
    @FXML
    private TextField accountUsername;
    @FXML
    private PasswordField accountPassword;
    @FXML
    private TextField accountQuota;

    /**
     * Create a new email account
     */
    public void newAccount()  {
        EmailManager.getInstance().createEmailAccount(accountUsername.getText(), accountPassword.getText(), accountQuota.getText());
    }
}
