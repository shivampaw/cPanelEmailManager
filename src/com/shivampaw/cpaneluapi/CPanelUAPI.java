package com.shivampaw.cpaneluapi;

import com.google.gson.Gson;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Base64;
import java.util.HashMap;

public class CPanelUAPI {
    private String host;
    private String username;
    private String password;

    public CPanelUAPI(String host, String username, String password) {
        this.host = host;
        this.username = username;
        this.password = password;
    }

    public String call(String module, String function) {
        return call(module, function, new HashMap<>());
    }

    public String call(String module, String function, HashMap<String, String> params) {
        UriBuilder uriBuilder = new UriBuilder(this.host + ":2083");
        uriBuilder.addSubfolder("execute");
        uriBuilder.addSubfolder(module);
        uriBuilder.addSubfolder(function);
        params.forEach(uriBuilder::addParameter);

        StringBuilder sb = new StringBuilder();

        try {
            URL url = uriBuilder.getURL();
            HttpsURLConnection conn = (HttpsURLConnection)url.openConnection();
            conn.addRequestProperty("Authorization", "Basic " + Base64.getEncoder().encodeToString((this.username + ":"
                    + this.password).getBytes()));
            conn.setConnectTimeout(5000);
            BufferedReader br;
            if (200 <= conn.getResponseCode() && conn.getResponseCode() <= 299) {
                br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            } else {
                br = new BufferedReader(new InputStreamReader((conn.getErrorStream())));
            }
            String output;
            while((output = br.readLine()) != null) {
                sb.append(output);
            }
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }

        return isJSONValid(sb.toString()) ? sb.toString() : "Error";
    }


    private boolean isJSONValid(String jsonInString) {
        Gson gson = new Gson();
        try {
            gson.fromJson(jsonInString, Object.class);
            return true;
        } catch(com.google.gson.JsonSyntaxException ex) {
            return false;
        }
    }


}
