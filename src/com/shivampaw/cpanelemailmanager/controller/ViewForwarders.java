package com.shivampaw.cpanelemailmanager.controller;

import com.shivampaw.cpanelemailmanager.EmailManager;
import com.shivampaw.cpanelemailmanager.Main;
import com.shivampaw.cpanelemailmanager.model.EmailForwarder;
import com.shivampaw.cpanelemailmanager.utils.JavaFXUtils;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

public class ViewForwarders {
    @FXML
    private TableView<EmailForwarder> emailForwarderTableView;
    @FXML
    private TableColumn<EmailForwarder, String> forward;
    @FXML
    private TableColumn<EmailForwarder, String> destination;

    /**
     * Setup the emailForwarderListView
     */
    public void initialize() {
        forward.setCellValueFactory(new PropertyValueFactory<>("forward"));
        destination.setCellValueFactory(new PropertyValueFactory<>("destination"));

        forward.setCellFactory(TextFieldTableCell.forTableColumn());
        destination.setCellFactory(TextFieldTableCell.forTableColumn());

        emailForwarderTableView.setItems(EmailManager.getInstance().getForwardAccounts());
        emailForwarderTableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        emailForwarderTableView.getSelectionModel().selectFirst();
    }

    /**
     * Show new forwarder dialog and process it
     */
    public void newForwarder() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(Main.parentWindow.getOwner());
        dialog.setTitle("Create Email Forwarder");
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/shivampaw/cpanelemailmanager/view/NewForwarder.fxml"));

        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e) {
            System.err.println("Error loading new email forwarder dialog!");
            e.printStackTrace();
        }

        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        NewForwarderController newForwarderController = fxmlLoader.getController();
        Optional<ButtonType> result = dialog.showAndWait(); // shows the dialog

        if(result.isPresent() && result.get() == ButtonType.OK) {
            Stage creatingForwarderStage = JavaFXUtils.showProgressDialog("Creating Forwarder...");
            new Thread(() -> {
                EmailForwarder newForwarder = newForwarderController.newForwarder();
                emailForwarderTableView.getSelectionModel().select(newForwarder);
                Platform.runLater(creatingForwarderStage::hide);
            }).start();
        }
    }

    /**
     * Prompt for forward deletion and delete if OK
     */
    public void deleteForwarder() {
        EmailForwarder account = emailForwarderTableView.getSelectionModel().getSelectedItem();
        if (account != null) { // if there is no account selected then do nothing
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete Email Forwarder");
            alert.setHeaderText("Delete Forwarder to " + account.getDestination());
            alert.setContentText("Are you sure you want to delete this email forwarder from " + account.getForward() + " to " + account.getDestination() + "?");

            Optional<ButtonType> result = alert.showAndWait(); // show confirmation box

            if (result.isPresent() && result.get() == ButtonType.OK) {
                Stage deletingForwarderStage = JavaFXUtils.showProgressDialog("Deleting Forwarder...");
                new Thread(() -> {
                    EmailManager.getInstance().deleteEmailForwarder(account.getForward(), account.getDestination());
                    EmailManager.getInstance().getForwardAccounts().remove(account);
                    Platform.runLater(deletingForwarderStage::hide);
                }).start();
            }
        }
    }

    @FXML
    public void close() throws IOException {
        Stage stage = (Stage) emailForwarderTableView.getScene().getWindow();
        stage.close();
    }
}
