package com.shivampaw.cem.java.controller;

import com.shivampaw.cem.java.datamodel.EmailAccountsData;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class AccountController {
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
        EmailAccountsData.getInstance().createEmailAccount(accountUsername.getText(), accountPassword.getText(), accountQuota.getText());
    }
}
