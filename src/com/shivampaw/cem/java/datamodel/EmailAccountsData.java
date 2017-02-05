package com.shivampaw.cem.java.datamodel;

import com.google.gson.Gson;
import com.shivampaw.cem.java.datamodel.CPanelResponses.CPanelResponse;
import com.shivampaw.cem.java.datamodel.CPanelResponses.ListPopsWithDisk;
import com.shivampaw.cem.java.utils.CPanelUAPI;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;

import java.util.HashMap;

public class EmailAccountsData {
    private CPanelUAPI uapi;
    private ObservableList<EmailAccount> accounts = FXCollections.observableArrayList();

    private static EmailAccountsData ourInstance = new EmailAccountsData();

    /**
     * getInstance method for Singleton
     * @return EmailAccountsData
     */
    public static EmailAccountsData getInstance() {
        return ourInstance;
    }

    /**
     * Private constructor
     */
    private EmailAccountsData() {}

    /**
     * Retrieve accounts
     * @return ObservableList<EmailAccount>
     */
    public ObservableList<EmailAccount> getAccounts() {
        return this.accounts;
    }

    /**
     * Login using the specified parameters
     * @param username cPanel Username
     * @param password cPanel Password
     * @param server cPanel Host
     */
    public void login(String username, String password, String server) {
        this.uapi = new CPanelUAPI(server, username, password);
    }

    /**
     * Fetch and store cPanel Accounts through API Call
     */
    public void getEmailAccounts() {
        String json = this.uapi.call("Email", "list_pops_with_disk");
        Gson gson = new Gson();
        ListPopsWithDisk emailAccounts = gson.fromJson(json, ListPopsWithDisk.class);
        this.accounts.clear();
        this.accounts.addAll(emailAccounts.getData());
    }

    /**
     * Create email account through API call with specified params
     * @param username Account Username
     * @param password Account Password
     * @param quota Account Quota
     */
    public void createEmailAccount(String username, String password, String quota) {
        String json = this.uapi.call("Email", "add_pop", new HashMap<String, String>(){{
            put("email", username);
            put("password", password);
            put("quota", quota);
        }});
        Gson gson = new Gson();
        CPanelResponse response = gson.fromJson(json, CPanelResponse.class);
        checkResponse(response);
    }

    /**
     * Delete email account through api call
     * @param email Email
     */
    public void deleteEmailAccount(String email) {
        String json = this.uapi.call("Email", "delete_pop", new HashMap<String, String>(){{
            put("email", email);
        }});

        Gson gson = new Gson();
        CPanelResponse response = gson.fromJson(json, CPanelResponse.class);
        checkResponse(response);
    }

    /**
     * Edit user's quota
     * @param user Username
     * @param quota New Quota
     */
    public void editEmailAccountQuota(String user, String quota) {
        String json = this.uapi.call("Email", "edit_pop_quota", new HashMap<String, String>(){{
            put("email", user);
            put("quota", quota);
        }});

        Gson gson = new Gson();
        CPanelResponse response = gson.fromJson(json, CPanelResponse.class);
        checkResponse(response);
    }

    /**
     * Edit user's password
     * @param user Username
     * @param password Password
     */
    public void editEmailAccountPassword(String user, String password) {
        String json = this.uapi.call("Email", "passwd_pop", new HashMap<String, String>(){{
            put("email", user);
            put("password", password);
        }});

        Gson gson = new Gson();
        CPanelResponse response = gson.fromJson(json, CPanelResponse.class);
        checkResponse(response);
    }

    /**
     * Check response of CPanelResponse and show error if not successful.
     * If successful then reload email accounts
     * @param response CPanelResponse
     */
    private void checkResponse(CPanelResponse response) {
        Platform.runLater(() -> {
            if(response.getStatus() == 1) {
                    getEmailAccounts();
            } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR, response.getErrors()[0]);
                    alert.show();
            }
        });
    }

    public CPanelUAPI getUAPI() {
        return uapi;
    }
}
