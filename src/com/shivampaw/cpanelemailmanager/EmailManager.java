package com.shivampaw.cpanelemailmanager;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.shivampaw.cpanelemailmanager.model.EmailForwarder;
import com.shivampaw.cpanelemailmanager.model.EmailPop;
import com.shivampaw.cpaneluapi.CPanelUAPI;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;

import java.io.IOException;
import java.util.HashMap;

public class EmailManager {
    private static EmailManager ourInstance = new EmailManager();
    private CPanelUAPI uapi;
    private ObservableList<EmailPop> popAccounts = FXCollections.observableArrayList();
    private ObservableList<EmailForwarder> forwardAccounts = FXCollections.observableArrayList();
    private String domain = "";

    /**
     * Private constructor
     */
    private EmailManager() {}

    /**
     * getInstance method for Singleton
     * @return EmailManager
     */
    public static EmailManager getInstance() {
        return ourInstance;
    }

    /**
     * Retrieve popAccounts
     * @return ObservableList<EmailPop>
     */
    public ObservableList<EmailPop> getPopAccounts() {
        return this.popAccounts;
    }

    /**
     * Retrieve forwardAccounts
     * @return ObservableList<EmailForward>
     */
    public ObservableList<EmailForwarder> getForwardAccounts() {
        return this.forwardAccounts;
    }

    ///////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////// LOGIN / LOGOUT / MISC //////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////
    /**
     * Login using the specified parameters
     * @param username cPanel Username
     * @param password cPanel Password
     * @param server cPanel Host
     */
    public void login(String username, String password, String server) {
        this.uapi = new CPanelUAPI(server, username, password);
        String json = this.uapi.call("DomainInfo", "list_domains");
        this.domain = new JsonParser().parse(json).getAsJsonObject().get("data").getAsJsonObject().get("main_domain").getAsString();
    }

    /**
     * Logout of the current account and show the LoginWindow
     */
    public void logout() throws IOException {
        this.uapi = null;
        this.domain = "";
        this.popAccounts = FXCollections.observableArrayList();
        this.forwardAccounts = FXCollections.observableArrayList();
        Main.parentWindow.getScene().setRoot(FXMLLoader.load(getClass().getResource("/com/shivampaw/cpanelemailmanager/view/LoginWindow.fxml")));
    }

    // TODO: Allow changing of domain on account (default should be main domain)

    /**
     * Check response and show error if not successful.
     * If successful then reload email popAccounts and forwardAccounts
     * @param json Json to check
     */
    private void checkResponse(String json) {
        JsonObject obj = new JsonParser().parse(json).getAsJsonObject();
        int status = obj.get("status").getAsInt();
        Platform.runLater(() -> {
            if(status == 1) {
                getPopEmailAccounts();
                getForwardEmailAccounts();
            } else {
                new Alert(Alert.AlertType.ERROR, obj.get("errors").getAsJsonArray().get(0).getAsString()).show();
            }
        });
    }

    ////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////// EMAIL POP ACCOUNTS //////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////
    /**
     * Fetch and store cPanel Pop Accounts through API Call
     */
    public void getPopEmailAccounts() {
        String json = this.uapi.call("Email", "list_pops_with_disk", new HashMap<String, String>(){{
            put("domain", domain);
        }});
        JsonArray emailPops = new JsonParser().parse(json).getAsJsonObject().get("data").getAsJsonArray();
        this.popAccounts.clear();

        for(JsonElement el : emailPops) {
            JsonObject obj = el.getAsJsonObject();
            String user = obj.get("user").getAsString();
            String email = obj.get("email").getAsString();
            String diskQuota = obj.get("diskquota").getAsString();
            String diskUsed = obj.get("diskused").getAsString();

            this.popAccounts.add(new EmailPop(user, email, diskQuota, diskUsed));
        }
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
            put("domain", domain);
        }});
        checkResponse(json);
    }

    /**
     * Delete email account through api call
     * @param email Email address to delete
     */
    public void deleteEmailAccount(String email) {
        String json = this.uapi.call("Email", "delete_pop", new HashMap<String, String>(){{
            put("email", email);
            put("domain", domain);
        }});
        checkResponse(json);
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
            put("domain", domain);
        }});
        checkResponse(json);
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
            put("domain", domain);
        }});
        checkResponse(json);
    }

    //////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////
    ////////////////////////////// EMAIL FORWARDERS //////////////////////////////
    //////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////
    /**
     * Fetch and store cPanel Forward Accounts through API Call
     */
    public void getForwardEmailAccounts() {
        String json = this.uapi.call("Email", "list_forwarders", new HashMap<String, String>(){{
            put("domain", domain);
        }});
        JsonArray emailForwarders = new JsonParser().parse(json).getAsJsonObject().get("data").getAsJsonArray();
        this.forwardAccounts.clear();

        for(JsonElement el : emailForwarders) {
            JsonObject obj = el.getAsJsonObject();

            // TODO: FOR SOME REASON CPANELS UAPI GIVES THESE TWO THE WRONG WAY ROUND!!
            String forward = obj.get("dest").getAsString();
            String destination = obj.get("forward").getAsString();

            this.forwardAccounts.add(new EmailForwarder(forward, destination));
        }

    }

    /**
     * Create email forwarder through API call with specified params
     * @param forwardFrom Email address to forward from
     * @param forwardTo Email address to forward to
     */
    public void createEmailForwarder(String forwardFrom, String forwardTo) {
        String json = this.uapi.call("Email", "add_forwarder", new HashMap<String, String>(){{
            put("email", forwardFrom + "@" + domain);
            put("fwdopt", "fwd");
            put("fwdemail", forwardTo);
            put("domain", domain);
        }});
        checkResponse(json);
    }

    /**
     * Delete email forwarder through api call
     * @param address Email address that is being forwarded from
     * @param dest Email address being forwarded to
     */
    public void deleteEmailForwarder(String address, String dest) {
        String json = this.uapi.call("Email", "delete_forwarder", new HashMap<String, String>(){{
            put("address", address);
            put("forwarder", dest);
        }});
        checkResponse(json);
    }

}
