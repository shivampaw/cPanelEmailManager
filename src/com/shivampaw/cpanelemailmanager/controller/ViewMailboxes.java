package com.shivampaw.cpanelemailmanager.controller;

import com.shivampaw.cpanelemailmanager.Main;
import com.shivampaw.cpanelemailmanager.EmailManager;
import com.shivampaw.cpanelemailmanager.model.Mailbox;
import com.shivampaw.cpanelemailmanager.utils.JavaFXUtils;
import javafx.application.Platform;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.util.Comparator;
import java.util.Optional;

public class ViewMailboxes {
    @FXML
    private ListView<Mailbox> mailboxesListView;
    @FXML
    private GridPane editingGridPane;
    @FXML
    private Label editingLabel;
    @FXML
    private TextField mailboxQuota;
    @FXML
    private PasswordField mailboxPassword;
    @FXML
    private Label mailboxCurrentUsed;


    /**
     * Setup the mailboxesListView
     */
    public void initialize() {
        setCellFactory();
        setSelectionModel();

        SortedList<Mailbox> sortedAccounts = new SortedList<>(EmailManager.getInstance().getMailboxesList(), Comparator.comparing(Mailbox::getUser));
        mailboxesListView.setItems(sortedAccounts);
        mailboxesListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }

    /**
     * This is fired when we change the note in the mailboxesListView
     */
    private void setSelectionModel() {
        mailboxesListView.getSelectionModel().selectedItemProperty().addListener((observable, oldAccount, newAccount) -> {
            if (newAccount != null) {
                Mailbox item = mailboxesListView.getSelectionModel().getSelectedItem();
                editingGridPane.setVisible(true);
                editingLabel.setText(item.getEmail());
                mailboxQuota.setText(item.getDiskQuota());
                mailboxCurrentUsed.setText("Current Mailbox Usage: " + item.getDiskUsed() + "MB");
            } else {
                editingGridPane.setVisible(false);
            }
        });
    }

    /**
     * This is run for displaying the content in the mailboxesListView
     */
    private void setCellFactory() {
        mailboxesListView.setCellFactory(new Callback<ListView<Mailbox>, ListCell<Mailbox>>() {
            @Override
            public ListCell<Mailbox> call(ListView<Mailbox> param) {
                return new ListCell<Mailbox>() {
                    @Override
                    protected void updateItem(Mailbox item, boolean empty) {
                        super.updateItem(item, empty);
                        if(empty) {
                            setText(null);
                        } else {
                            setText(item.getEmail());
                        }
                    }
                };
            }
        });
    }

    /**
     * Show new mailbox fxml in modal dialog and then
     * process the result.
     */
    public void newMailbox() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(Main.parentWindow.getOwner());
        dialog.setTitle("New Mailbox");
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/shivampaw/cpanelemailmanager/view/mailboxes/NewMailbox.fxml"));

        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e) {
            System.err.println("Error loading new mailbox dialog!");
            e.printStackTrace();
        }

        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        NewMailboxes newMailbox = fxmlLoader.getController();
        Optional<ButtonType> result = dialog.showAndWait();

        if(result.isPresent() && result.get() == ButtonType.OK) {
            Stage creatingMailboxProgressDialog = JavaFXUtils.showProgressDialog("Creating Mailbox...");
            new Thread(() -> {
                newMailbox.newMailbox();
                Platform.runLater(creatingMailboxProgressDialog::hide);
            }).start();
        }
    }

    /**
     * Prompt for mailbox deletion and delete if OK pressed.
     */
    public void deleteMailbox() {
        Mailbox account = mailboxesListView.getSelectionModel().getSelectedItem();
        if (account != null) { // if there is no account selected then do nothing
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete Mailbox");
            alert.setHeaderText("Delete Mailbox " + account.getEmail());
            alert.setContentText("Are you sure you want to delete this mailbox?");

            Optional<ButtonType> result = alert.showAndWait(); // show confirmation box

            if (result.isPresent() && result.get() == ButtonType.OK) {
                Stage deletingMailboxProgressDialog = JavaFXUtils.showProgressDialog("Deleting Mailbox...");
                new Thread(() -> {
                    EmailManager.getInstance().deleteMailbox(account.getEmail());
                    Platform.runLater(deletingMailboxProgressDialog::hide);
                }).start();
            }
        }
    }

    /**
     * Edit mailbox based on what is entered in the GridPane
     */
    public void editMailbox() {
        Mailbox account = mailboxesListView.getSelectionModel().getSelectedItem();

        Stage editingMailboxProgressDialog = JavaFXUtils.showProgressDialog("Editing Mailbox...");

        String password = mailboxPassword.getText();
        mailboxPassword.setText("");

        new Thread(() -> {
            if(!password.equals("")) {
                EmailManager.getInstance().editMailboxPassword(account.getUser(), password);
            }
            if(!mailboxQuota.getText().equals("") && !mailboxQuota.getText().equals(account.getDiskQuota())) {
                EmailManager.getInstance().editMailboxQuota(account.getUser(), mailboxQuota.getText());
            }
            Platform.runLater(editingMailboxProgressDialog::hide);
        }).start();


    }

    public void backToMainWindow() throws IOException {
        Main.parentWindow.getScene().setRoot(FXMLLoader.load(getClass().getResource("/com/shivampaw/cpanelemailmanager/view/MainWindow.fxml")));
    }
}
