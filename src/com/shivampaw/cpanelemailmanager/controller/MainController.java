package com.shivampaw.cpanelemailmanager.controller;

import com.shivampaw.cpanelemailmanager.Main;
import com.shivampaw.cpanelemailmanager.EmailManager;
import com.shivampaw.cpanelemailmanager.model.EmailPop;
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

public class MainController {
    @FXML
    private ListView<EmailPop> emailAccountListView;
    @FXML
    private GridPane editingGridPane;
    @FXML
    private Label editingLabel;
    @FXML
    private TextField accountQuota;
    @FXML
    private PasswordField accountPassword;
    @FXML
    private Label currentUsed;


    /**
     * Setup the emailAccountListView
     */
    public void initialize() {
        setCellFactory();
        setSelectionModel();

        SortedList<EmailPop> sortedAccounts = new SortedList<>(EmailManager.getInstance().getPopAccounts(), Comparator.comparing(EmailPop::getUser));
        emailAccountListView.setItems(sortedAccounts);
        emailAccountListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        emailAccountListView.getSelectionModel().selectFirst();
    }

    /**
     * This is fired when we change the note in the ListView
     */
    private void setSelectionModel() {
        emailAccountListView.getSelectionModel().selectedItemProperty().addListener((observable, oldAccount, newAccount) -> {
            if (newAccount != null) {
                EmailPop item = emailAccountListView.getSelectionModel().getSelectedItem();
                editingGridPane.setVisible(true);
                editingLabel.setText(item.getEmail());
                accountQuota.setText(item.getDiskQuota());
                currentUsed.setText("Current Account Usage: " + item.getDiskUsed() + "MB");
            } else {
                editingGridPane.setVisible(false);
            }
        });
    }

    /**
     * This is run for displaying the content in the ListView
     */
    private void setCellFactory() {
        emailAccountListView.setCellFactory(new Callback<ListView<EmailPop>, ListCell<EmailPop>>() {
            @Override
            public ListCell<EmailPop> call(ListView<EmailPop> param) {
                return new ListCell<EmailPop>() {
                    @Override
                    protected void updateItem(EmailPop item, boolean empty) {
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
     * Show new account fxml in modal dialog and then
     * process the result.
     */
    public void newAccount() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(Main.parentWindow.getOwner());
        dialog.setTitle("Create Email Account");
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/shivampaw/cpanelemailmanager/view/NewAccount.fxml"));

        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e) {
            System.err.println("Error loading new email account dialog!");
            e.printStackTrace();
        }

        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        NewAccountController newAccountController = fxmlLoader.getController();
        Optional<ButtonType> result = dialog.showAndWait();

        if(result.isPresent() && result.get() == ButtonType.OK) {
            Stage creatingAccountStage = JavaFXUtils.showProgressDialog("Creating Account...");
            new Thread(() -> {
                newAccountController.newAccount();
                Platform.runLater(creatingAccountStage::hide);
            }).start();
        }
    }

    /**
     * Prompt for account deletion and delete if OK pressed.
     */
    public void deleteAccount() {
        EmailPop account = emailAccountListView.getSelectionModel().getSelectedItem();
        if (account != null) { // if there is no account selected then do nothing
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete Email Account");
            alert.setHeaderText("Delete Account " + account.getEmail());
            alert.setContentText("Are you sure you want to delete this email account?");

            Optional<ButtonType> result = alert.showAndWait(); // show confirmation box

            if (result.isPresent() && result.get() == ButtonType.OK) {
                Stage deletingAccountStage = JavaFXUtils.showProgressDialog("Deleting Account...");
                new Thread(() -> {
                    EmailManager.getInstance().deleteEmailAccount(account.getEmail());
                    Platform.runLater(deletingAccountStage::hide);
                }).start();
            }
        }
    }

    /**
     * Edit account based on what is entered in the GridPane
     */
    public void editAccount() {
        EmailPop account = emailAccountListView.getSelectionModel().getSelectedItem();

        Stage editingAccountStage = JavaFXUtils.showProgressDialog("Editing Account...");

        String password = accountPassword.getText();
        accountPassword.setText("");

        new Thread(() -> {
            if(!password.equals("")) {
                EmailManager.getInstance().editEmailAccountPassword(account.getUser(), password);
            }
            if(!accountQuota.getText().equals("") && !accountQuota.getText().equals(account.getDiskQuota())) {
                EmailManager.getInstance().editEmailAccountQuota(account.getUser(), accountQuota.getText());
            }
            Platform.runLater(editingAccountStage::hide);
        }).start();


    }

    /**
     * Logout menu item pressed, let's logout!
     */
    public void logout() throws IOException {
        EmailManager.getInstance().logout();
    }

    /**
     * Open view forwarders window in a new modal window
     * @throws IOException exception thrown if error occurs whilst loading view forwarders dialog
     */
    public void viewForwarders() throws IOException {

        Stage viewForwardersProgressBar = JavaFXUtils.showProgressDialog("Loading Account Forwarders...");
        new Thread(() -> {
            EmailManager.getInstance().getForwardEmailAccounts();
            Platform.runLater(viewForwardersProgressBar::hide);
        }).start();

        Dialog dialog = new Dialog<>();
        dialog.initOwner(Main.parentWindow.getOwner());
        dialog.setTitle("View Email Forwarders");
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/shivampaw/cpanelemailmanager/view/ViewForwarders.fxml"));

        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e) {
            System.err.println("Error loading email forwarders!");
            e.printStackTrace();
        }

        dialog.show();
    }

    /**
     * Close application gracefully
     */
    public void closeApplication() {
        Platform.exit();
    }
}
