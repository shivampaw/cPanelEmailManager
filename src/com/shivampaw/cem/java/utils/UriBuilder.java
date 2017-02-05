package com.shivampaw.cem.java.utils;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class UriBuilder {
    private StringBuilder folders, params;
    private String connType, host;

    void setConnectionType(String conn) {
        connType = conn;
    }

    UriBuilder(){
        folders = new StringBuilder();
        params = new StringBuilder();
    }

    UriBuilder(String host) {
        this();
        this.host = host;
    }

    void addSubfolder(String folder) {
        folders.append("/");
        folders.append(folder);
    }

    void addParameter(String parameter, String value) {
        if (params.toString().length() > 0) {
            params.append("&");
        }
        params.append(parameter);
        params.append("=");
        params.append(value);
    }

    URL getURL() throws URISyntaxException, MalformedURLException {
        return new URI(connType, host, folders.toString(),
                params.toString(), null).toURL();
    }

    String getRelativeURL() throws URISyntaxException, MalformedURLException{
        URI uri = new URI(null, null, folders.toString(), params.toString(), null);
        return uri.toString();
    }
}