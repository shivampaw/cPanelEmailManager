package com.shivampaw.cem.java.controller;

import com.shivampaw.cem.java.Main;
import com.shivampaw.cem.java.datamodel.EmailAccount;
import com.shivampaw.cem.java.datamodel.EmailAccountsData;
import com.shivampaw.cem.java.utils.JavaFXUtils;
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

public class ShowController {
    @FXML
    private ListView<EmailAccount> emailAccountListView;
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

        SortedList<EmailAccount> sortedAccounts = new SortedList<>(EmailAccountsData.getInstance().getAccounts(), Comparator.comparing(EmailAccount::getUser));
        emailAccountListView.setItems(sortedAccounts);
        emailAccountListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        emailAccountListView.getSelectionModel().selectFirst();
    }

    /**
     * This is fired when we change the note in the ListView
     */
    private void setSelectionModel() {
        emailAccountListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue != null) { // if a account note has been selected
                EmailAccount item = emailAccountListView.getSelectionModel().getSelectedItem();
                editingGridPane.setVisible(true);
                editingLabel.setText(item.getEmail());
                accountQuota.setText(item.getDiskQuota());
                currentUsed.setText("Current Account Usage: " + item.getDiskUsed() + "MB");
            } else { // if no new account has been selected
                editingGridPane.setVisible(false);
            }
        });
    }

    /**
     * This is run for displaying the content in the ListView
     */
    private void setCellFactory() {
        emailAccountListView.setCellFactory(new Callback<ListView<EmailAccount>, ListCell<EmailAccount>>() {
            @Override
            public ListCell<EmailAccount> call(ListView<EmailAccount> param) {
                return new ListCell<EmailAccount>() {
                    @Override
                    protected void updateItem(EmailAccount item, boolean empty) {
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
     * Show new account fxml
     */
    public void newAccount() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(Main.parentWindow.getOwner());
        dialog.setTitle("Create Email Account");
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/shivampaw/cem/resources/NewAccount.fxml"));

        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e) {
            System.err.println("Error loading new email account dialog!");
            e.printStackTrace();
        }

        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        AccountController accountController = fxmlLoader.getController();
        Optional<ButtonType> result = dialog.showAndWait(); // shows the dialog

        if(result.isPresent() && result.get() == ButtonType.OK) {
            Stage creatingAccountStage = JavaFXUtils.showProgressDialog("Creating Account...");
            new Thread(() -> {
                accountController.newAccount();
                Platform.runLater(creatingAccountStage::hide);
            }).start();
        }
    }

    /**
     * Prompt for account deletion and delete if OK
     */
    public void deleteAccount() {
        EmailAccount account = emailAccountListView.getSelectionModel().getSelectedItem();
        if(account != null) { // if there is no note selected then do nothing
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete Email Account");
            alert.setHeaderText("Delete Account " + account.getEmail());
            alert.setContentText("Are you sure you want to delete this email account?");

            Optional<ButtonType> result = alert.showAndWait(); // show confirmation box

            if (result.isPresent() && result.get() == ButtonType.OK) {
                Stage deletingAccountStage = JavaFXUtils.showProgressDialog("Deleting Account...");
                new Thread(() -> {
                    EmailAccountsData.getInstance().deleteEmailAccount(account.getEmail());
                    Platform.runLater(deletingAccountStage::hide);
                }).start();
            }
        }
    }

    /**
     * Edit account based on what is entered in the GridPane
     */
    public void editAccount() {
        EmailAccount account = emailAccountListView.getSelectionModel().getSelectedItem();

        Stage editingAccountStage = JavaFXUtils.showProgressDialog("Editing Account...");

        String password = accountPassword.getText();
        accountPassword.setText("");

        new Thread(() -> {
            if(!password.equals("")) {
                EmailAccountsData.getInstance().editEmailAccountPassword(account.getUser(), password);
            }
            if(!accountQuota.getText().equals("") && !accountQuota.getText().equals(account.getDiskQuota())) {
                EmailAccountsData.getInstance().editEmailAccountQuota(account.getUser(), accountQuota.getText());
            }
            Platform.runLater(editingAccountStage::hide);
        }).start();


    }

    /**
     * Close application gracefully
     */
    public void closeApplication() {
        Platform.exit();
    }
}
