package com.shivampaw.cpanelemailmanager.controller;

import com.shivampaw.cpanelemailmanager.EmailManager;
import com.shivampaw.cpanelemailmanager.Main;
import com.shivampaw.cpanelemailmanager.utils.JavaFXUtils;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;

import java.io.IOException;

public class MainWindowController {
    @FXML
    private ComboBox<String> domainsComboBox;

    public void initialize() {
        this.domainsComboBox.setItems(EmailManager.getInstance().getDomains());
        this.domainsComboBox.getSelectionModel().select(EmailManager.getInstance().getDomain());

        this.domainsComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            Stage changingDomainsProgressDialog = JavaFXUtils.showProgressDialog("Switching Domain to " + newValue + "...");
            new Thread(() -> {
                try {
                    EmailManager.getInstance().switchDomain(newValue);
                } catch (IOException e) {
                    JavaFXUtils.showErrorAlert("An error occurred whilst switching domain names.");
                }
                EmailManager.getInstance().getMailboxes();
                EmailManager.getInstance().getForwarders();
                Platform.runLater(changingDomainsProgressDialog::hide);
            }).start();
        });
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
