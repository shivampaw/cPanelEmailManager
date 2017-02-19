package com.shivampaw.cpanelemailmanager;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.shivampaw.cpanelemailmanager.model.Forwarder;
import com.shivampaw.cpanelemailmanager.model.Mailbox;
import com.shivampaw.cpanelemailmanager.utils.JavaFXUtils;
import com.shivampaw.cpaneluapi.CPanelUAPI;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;

import java.io.IOException;
import java.util.HashMap;

public class EmailManager {
    private static EmailManager ourInstance = new EmailManager();
    private CPanelUAPI api;
    private ObservableList<Mailbox> mailboxes = FXCollections.observableArrayList();
    private ObservableList<Forwarder> forwarders = FXCollections.observableArrayList();
    private String domain = "";
    private ObservableList<String> domains = FXCollections.observableArrayList();

    private EmailManager() {}

    public static EmailManager getInstance() {
        return ourInstance;
    }

    public ObservableList<Mailbox> getMailboxesList() {
        return this.mailboxes;
    }

    public ObservableList<Forwarder> getForwardersList() {
        return this.forwarders;
    }

    public String getDomain() {
        return domain;
    }

    public ObservableList<String> getDomains() {
        return this.domains;
    }

    ///////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////// LOGIN / LOGOUT / MISC //////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////

    public void login(String username, String password, String server) {
        this.api = new CPanelUAPI(server, username, password);

        String json = this.api.call("DomainInfo", "list_domains");
        JsonObject domainsObject = new JsonParser().parse(json).getAsJsonObject().get("data").getAsJsonObject();
        this.domain = domainsObject.get("main_domain").getAsString();
        this.domains.add(this.domain);

        String[] domainArrays = { "addon_domains", "parked_domains", "sub_domains" };
        for(String domainArray : domainArrays) {
            for(JsonElement domain : domainsObject.get(domainArray).getAsJsonArray()) {
                this.domains.add(domain.getAsString());
            }
        }
    }

    public void logout() throws IOException {
        this.api = null;
        this.domain = "";
        this.domains = FXCollections.observableArrayList();
        this.mailboxes = FXCollections.observableArrayList();
        this.forwarders = FXCollections.observableArrayList();
        Main.parentWindow.getScene().setRoot(FXMLLoader.load(getClass().getResource("/com/shivampaw/cpanelemailmanager/view/LoginWindow.fxml")));
    }

    public void switchActiveDomain(String newDomain) throws IOException {
        this.domain = newDomain;
        this.mailboxes = FXCollections.observableArrayList();
        this.forwarders = FXCollections.observableArrayList();
        Main.parentWindow.getScene().setRoot(FXMLLoader.load(getClass().getResource("/com/shivampaw/cpanelemailmanager/view/MainWindow.fxml")));
    }

    private void checkCpanelResponse(String json) {
        JsonObject obj = new JsonParser().parse(json).getAsJsonObject();
        int status = obj.get("status").getAsInt();
        Platform.runLater(() -> {
            if(status == 1) {
                getMailboxesFromCpanel();
                getForwardersFromCpanel();
            } else {
                JavaFXUtils.showErrorAlert(obj.get("errors").getAsJsonArray().get(0).getAsString());
            }
        });
    }

    /////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////
    ////////////////////////////// EMAIL MAILBOXES //////////////////////////////
    /////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////

    public void getMailboxesFromCpanel() {
        String json = this.api.call("Email", "list_pops_with_disk", new HashMap<String, String>(){{
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

    public void createMailbox(String username, String password, String quota) {
        String json = this.api.call("Email", "add_pop", new HashMap<String, String>(){{
            put("email", username);
            put("password", password);
            put("quota", quota);
            put("domain", domain);
        }});
        checkCpanelResponse(json);
    }

    public void deleteMailbox(String email) {
        String json = this.api.call("Email", "delete_pop", new HashMap<String, String>(){{
            put("email", email);
            put("domain", domain);
        }});
        checkCpanelResponse(json);
    }

    public void editMailboxQuota(String user, String quota) {
        String json = this.api.call("Email", "edit_pop_quota", new HashMap<String, String>(){{
            put("email", user);
            put("quota", quota);
            put("domain", domain);
        }});
        checkCpanelResponse(json);
    }

    public void editMailboxPassword(String user, String password) {
        String json = this.api.call("Email", "passwd_pop", new HashMap<String, String>(){{
            put("email", user);
            put("password", password);
            put("domain", domain);
        }});
        checkCpanelResponse(json);
    }

    //////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////
    ////////////////////////////// EMAIL FORWARDERS //////////////////////////////
    //////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////

    public void getForwardersFromCpanel() {
        String json = this.api.call("Email", "list_forwarders", new HashMap<String, String>(){{
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

    public void createForwarder(String forwardFrom, String forwardTo) {
        String json = this.api.call("Email", "add_forwarder", new HashMap<String, String>(){{
            put("email", forwardFrom + "@" + domain);
            put("fwdopt", "fwd");
            put("fwdemail", forwardTo);
            put("domain", domain);
        }});
        checkCpanelResponse(json);
    }

    public void deleteForwarder(String address, String dest) {
        String json = this.api.call("Email", "delete_forwarder", new HashMap<String, String>(){{
            put("address", address);
            put("forwarder", dest);
        }});
        checkCpanelResponse(json);
    }

}
