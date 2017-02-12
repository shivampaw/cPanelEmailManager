package com.shivampaw.cem.java.controller;

import com.shivampaw.cem.java.Main;
import com.shivampaw.cem.java.EmailManager;
import com.shivampaw.cem.java.utils.JavaFXUtils;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
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
     * @throws IOException
     */
    @FXML
    public void login() throws IOException {

        Stage loginStage = JavaFXUtils.showProgressDialog("Logging In...");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    EmailManager.getInstance().login(cPanelUsername.getText(), cPanelPassword.getText(), cPanelServer.getText());
                    EmailManager.getInstance().getPopEmailAccounts();
                    Main.parentWindow.getScene().setRoot(FXMLLoader.load(getClass().getResource("/com/shivampaw/cem/resources/MainWindow.fxml")));
                } catch (Exception e) {
                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setHeaderText("Login Failed");
                        alert.setContentText("Check you entered the correct details and are using a valid host that supports SSL.");
                        alert.show();
                    });
                }
                Platform.runLater(loginStage::hide);
            }
        }).start();
    }
}
