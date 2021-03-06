package com.shivampaw.cpanelemailmanager.controller;

import com.shivampaw.cpanelemailmanager.EmailManager;
import com.shivampaw.cpanelemailmanager.Main;
import com.shivampaw.cpanelemailmanager.utils.JavaFXUtils;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {
    @FXML
    private TextField cPanelServer;
    @FXML
    private TextField cPanelUsername;
    @FXML
    private TextField cPanelPassword;

    /**
     * Remove focus from server field
     */
    @FXML
    public void initialize() {
        Platform.runLater( () -> cPanelUsername.getParent().requestFocus() );
    }

    /**
     * Login the user whilst showing a progress dialog
     * @throws IOException exception if error occurs loading main window.
     */
    @FXML
    public void login() throws IOException {

        Stage loginProgressDialog = JavaFXUtils.showProgressDialog("Logging In...");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    EmailManager.getInstance().login(cPanelUsername.getText(), cPanelPassword.getText(), cPanelServer.getText());
                    EmailManager.getInstance().getMailboxes();
                    EmailManager.getInstance().getForwarders();
                    Main.parentWindow.getScene().setRoot(FXMLLoader.load(getClass().getResource("/com/shivampaw/cpanelemailmanager/view/MainWindow.fxml")));
                } catch (Exception e) {
                    Platform.runLater(() -> JavaFXUtils.showErrorAlert("Login Failed", "Check you entered the correct details and are using a valid host that supports SSL."));
                }
                Platform.runLater(loginProgressDialog::hide);
            }
        }).start();
    }
}
