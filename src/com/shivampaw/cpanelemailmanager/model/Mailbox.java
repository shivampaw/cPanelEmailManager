package com.shivampaw.cpanelemailmanager.model;

public class Mailbox {
    private String user;
    private String email;
    private String diskquota;
    private String diskused;


    public Mailbox(String user, String email, String diskquota, String diskused) {
        this.user = user;
        this.email = email;
        this.diskquota = diskquota;
        this.diskused = diskused;
    }

    public String getUser() {
        return user;
    }

    public String getEmail() {
        return email;
    }

    public String getDiskQuota() {
        return diskquota;
    }

    public String getDiskUsed() {
        return diskused;
    }
}
