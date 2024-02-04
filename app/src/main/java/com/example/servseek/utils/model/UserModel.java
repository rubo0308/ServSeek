package com.example.servseek.utils.model;

import java.security.Timestamp;

public class UserModel {
    private String phone;
    private String username;
    private Timestamp createdTimestamp;


    public UserModel(String phoneNumber, String username, com.google.firebase.Timestamp now) {
    }
    public UserModel(String phone, String username, Timestamp createdTimestamp) {
        this.phone = phone;
        this.username = username;
        this.createdTimestamp = createdTimestamp;

    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Timestamp getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(Timestamp createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }
}
