package com.shivampaw.cpanelemailmanager.controller;

import com.shivampaw.cpanelemailmanager.EmailManager;
import com.shivampaw.cpanelemailmanager.Main;
import com.shivampaw.cpanelemailmanager.model.Forwarder;
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
    private TableView<Forwarder> forwardersTableView;
    @FXML
    private TableColumn<Forwarder, String> forward;
    @FXML
    private TableColumn<Forwarder, String> destination;

    /**
     * Setup the emailForwarderListView
     */
    public void initialize() {
        forward.setCellValueFactory(new PropertyValueFactory<>("forward"));
        destination.setCellValueFactory(new PropertyValueFactory<>("destination"));

        forward.setCellFactory(TextFieldTableCell.forTableColumn());
        destination.setCellFactory(TextFieldTableCell.forTableColumn());

        forwardersTableView.setItems(EmailManager.getInstance().getForwardersList());
        forwardersTableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }

    /**
     * Show new forwarder dialog and process it
     */
    public void newForwarder() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(Main.parentWindow.getOwner());
        dialog.setTitle("New Forwarder");
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/shivampaw/cpanelemailmanager/view/forwarders/NewForwarder.fxml"));

        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e) {
            System.err.println("Error loading new forwarder dialog!");
            e.printStackTrace();
        }

        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        NewForwarder newForwarderController = fxmlLoader.getController();
        Optional<ButtonType> result = dialog.showAndWait(); // shows the dialog

        if(result.isPresent() && result.get() == ButtonType.OK) {
            Stage creatingForwarderStage = JavaFXUtils.showProgressDialog("Creating Forwarder...");
            new Thread(() -> {
                newForwarderController.newForwarder();
                Platform.runLater(creatingForwarderStage::hide);
            }).start();
        }
    }

    /**
     * Prompt for forward deletion and delete if OK
     */
    public void deleteForwarder() {
        Forwarder account = forwardersTableView.getSelectionModel().getSelectedItem();
        if (account != null) { // if there is no account selected then do nothing
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete Email Forwarder");
            alert.setHeaderText("Delete Forwarder to " + account.getDestination());
            alert.setContentText("Are you sure you want to delete this forwarder from " + account.getForward() + " to " + account.getDestination() + "?");

            Optional<ButtonType> result = alert.showAndWait(); // show confirmation box

            if (result.isPresent() && result.get() == ButtonType.OK) {
                Stage deletingForwarderStage = JavaFXUtils.showProgressDialog("Deleting Forwarder...");
                new Thread(() -> {
                    EmailManager.getInstance().deleteForwarder(account.getForward(), account.getDestination());
                    EmailManager.getInstance().getForwardersList().remove(account);
                    Platform.runLater(deletingForwarderStage::hide);
                }).start();
            }
        }
    }


    public void backToMainWindow() throws IOException {
        Main.parentWindow.getScene().setRoot(FXMLLoader.load(getClass().getResource("/com/shivampaw/cpanelemailmanager/view/MainWindow.fxml")));
    }
}
