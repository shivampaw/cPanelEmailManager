package com.shivampaw.cpanelemailmanager.controller;

import com.shivampaw.cpanelemailmanager.EmailManager;
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
    void newAccount()  {
        EmailManager.getInstance().createEmailAccount(accountUsername.getText(), accountPassword.getText(), accountQuota.getText());
    }
}
