package com.shivampaw.cem.java.datamodel.CPanelResponses;

import com.shivampaw.cem.java.datamodel.EmailAccount;

import java.util.ArrayList;

public class ListPopsWithDisk extends CPanelResponse {
    private ArrayList<EmailAccount> data;

    public ArrayList<EmailAccount> getData() {
        return data;
    }
}
