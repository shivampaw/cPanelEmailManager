package com.shivampaw.cpanelemailmanager.controller;

import com.shivampaw.cpanelemailmanager.EmailManager;
import com.shivampaw.cpanelemailmanager.Main;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;

import java.io.IOException;

public class MainWindow {
    @FXML
    private Label currentDomain;

    public void initialize() {
        this.currentDomain.setText(EmailManager.getInstance().getDomain());
    }

    public void viewMailboxes() throws IOException {
        Main.parentWindow.getScene().setRoot(FXMLLoader.load(getClass().getResource("/com/shivampaw/cpanelemailmanager/view/mailboxes/ViewMailboxes.fxml")));
    }

    public void viewForwarders() throws IOException {
        Main.parentWindow.getScene().setRoot(FXMLLoader.load(getClass().getResource("/com/shivampaw/cpanelemailmanager/view/forwarders/ViewForwarders.fxml")));
    }

    public void logout() throws IOException {
        EmailManager.getInstance().logout();
    }
}
