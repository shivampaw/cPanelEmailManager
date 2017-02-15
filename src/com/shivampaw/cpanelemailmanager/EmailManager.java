package com.shivampaw.cpanelemailmanager;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.shivampaw.cpanelemailmanager.model.Forwarder;
import com.shivampaw.cpanelemailmanager.model.Mailbox;
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
    private ObservableList<Mailbox> mailboxes = FXCollections.observableArrayList();
    private ObservableList<Forwarder> forwarders = FXCollections.observableArrayList();
    private String domain = "";
    private ObservableList<String> domains = FXCollections.observableArrayList();

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
     * Retrieve mailboxes
     * @return ObservableList<Mailbox>
     */
    public ObservableList<Mailbox> getMailboxesList() {
        return this.mailboxes;
    }

    /**
     * Retrieve forwarders
     * @return ObservableList<Forwarder>
     */
    public ObservableList<Forwarder> getForwardersList() {
        return this.forwarders;
    }

    /**
     * Get domain to access
     * @return String domain to access
     */
    public String getDomain() {
        return domain;
    }

    /**
     * Retrieve forwarders
     * @return ObservableList<Forwarder>
     */
    public ObservableList<String> getDomains() {
        return this.domains;
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
        JsonObject domainsObject = new JsonParser().parse(json).getAsJsonObject().get("data").getAsJsonObject();
        this.domain = domainsObject.get("main_domain").getAsString();

        this.domains.add(this.domain);
        String[] parameters = { "addon_domains", "parked_domains", "sub_domains" };
        for(String parameter : parameters) {
            for(JsonElement domain : domainsObject.get(parameter).getAsJsonArray()) {
                this.domains.add(domain.getAsString());
            }
        }
    }

    /**
     * Logout of the current account and show the LoginWindow
     */
    public void logout() throws IOException {
        this.uapi = null;
        this.domain = "";
        this.domains = FXCollections.observableArrayList();
        this.mailboxes = FXCollections.observableArrayList();
        this.forwarders = FXCollections.observableArrayList();;
        Main.parentWindow.getScene().setRoot(FXMLLoader.load(getClass().getResource("/com/shivampaw/cpanelemailmanager/view/LoginWindow.fxml")));
    }

    /**
     * Switch domain in use to specified domain
     */
    public void switchDomain(String newDomain) throws IOException {
        this.domain = newDomain;
        this.mailboxes = FXCollections.observableArrayList();
        this.forwarders = FXCollections.observableArrayList();
        Main.parentWindow.getScene().setRoot(FXMLLoader.load(getClass().getResource("/com/shivampaw/cpanelemailmanager/view/MainWindow.fxml")));
    }

    /**
     * Check response and show error if not successful.
     * If successful then reload email mailboxes and forwarders
     * @param json Json to check
     */
    private void checkResponse(String json) {
        JsonObject obj = new JsonParser().parse(json).getAsJsonObject();
        int status = obj.get("status").getAsInt();
        Platform.runLater(() -> {
            if(status == 1) {
                getMailboxes();
                getForwarders();
            } else {
                new Alert(Alert.AlertType.ERROR, obj.get("errors").getAsJsonArray().get(0).getAsString()).show();
            }
        });
    }

    /////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////
    ////////////////////////////// EMAIL MAILBOXES //////////////////////////////
    /////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////
    /**
     * Fetch and store cPanel Mailboxes through API Call
     */
    public void getMailboxes() {
        String json = this.uapi.call("Email", "list_pops_with_disk", new HashMap<String, String>(){{
            put("domain", domain);
        }});
        JsonArray emailPops = new JsonParser().parse(json).getAsJsonObject().get("data").getAsJsonArray();
        this.mailboxes.clear();

        for(JsonElement el : emailPops) {
            JsonObject obj = el.getAsJsonObject();
            String user = obj.get("user").getAsString();
            String email = obj.get("email").getAsString();
            String diskQuota = obj.get("diskquota").getAsString();
            String diskUsed = obj.get("diskused").getAsString();

            this.mailboxes.add(new Mailbox(user, email, diskQuota, diskUsed));
        }
    }

    /**
     * Create mailbox through API call with specified params
     * @param username Account Username
     * @param password Account Password
     * @param quota Account Quota
     */
    public void createMailbox(String username, String password, String quota) {
        String json = this.uapi.call("Email", "add_pop", new HashMap<String, String>(){{
            put("email", username);
            put("password", password);
            put("quota", quota);
            put("domain", domain);
        }});
        checkResponse(json);
    }

    /**
     * Delete mailbox through api call
     * @param email Email address to delete
     */
    public void deleteMailbox(String email) {
        String json = this.uapi.call("Email", "delete_pop", new HashMap<String, String>(){{
            put("email", email);
            put("domain", domain);
        }});
        checkResponse(json);
    }

    /**
     * Edit mailbox quota
     * @param user Username
     * @param quota New Quota
     */
    public void editMailboxQuota(String user, String quota) {
        String json = this.uapi.call("Email", "edit_pop_quota", new HashMap<String, String>(){{
            put("email", user);
            put("quota", quota);
            put("domain", domain);
        }});
        checkResponse(json);
    }

    /**
     * Edit mailbox password
     * @param user Username
     * @param password Password
     */
    public void editMailboxPassword(String user, String password) {
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
     * Fetch and store cPanel forwarders through API Call
     */
    public void getForwarders() {
        String json = this.uapi.call("Email", "list_forwarders", new HashMap<String, String>(){{
            put("domain", domain);
        }});
        JsonArray emailForwarders = new JsonParser().parse(json).getAsJsonObject().get("data").getAsJsonArray();
        this.forwarders.clear();

        for(JsonElement el : emailForwarders) {
            JsonObject obj = el.getAsJsonObject();

            // TODO: FOR SOME REASON CPANELS UAPI GIVES THESE TWO THE WRONG WAY ROUND!!
            String forward = obj.get("dest").getAsString();
            String destination = obj.get("forward").getAsString();

            this.forwarders.add(new Forwarder(forward, destination));
        }

    }

    /**
     * Create forwarder through API call with specified params
     * @param forwardFrom Email address to forward from
     * @param forwardTo Email address to forward to
     */
    public void createForwarder(String forwardFrom, String forwardTo) {
        String json = this.uapi.call("Email", "add_forwarder", new HashMap<String, String>(){{
            put("email", forwardFrom + "@" + domain);
            put("fwdopt", "fwd");
            put("fwdemail", forwardTo);
            put("domain", domain);
        }});
        checkResponse(json);
    }

    /**
     * Delete forwarder through api call
     * @param address Email address that is being forwarded from
     * @param dest Email address being forwarded to
     */
    public void deleteForwarder(String address, String dest) {
        String json = this.uapi.call("Email", "delete_forwarder", new HashMap<String, String>(){{
            put("address", address);
            put("forwarder", dest);
        }});
        checkResponse(json);
    }

}
