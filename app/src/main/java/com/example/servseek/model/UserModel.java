package com.example.servseek.model;

import com.google.firebase.Timestamp;

import java.util.List;

public class UserModel {
    private String phone;
    private String username;

    private float averageRating;

    private String profession;
    private Timestamp createdTimestamp;
    private String userId;
    private String fcmToken;

    private String name;


    private String about;
   private String imageUrl; // Image URL field added

    private List<String> portfolio;
    private String id;
    private String email;




    public UserModel() {
    }

    public UserModel(String phone, String username, Timestamp createdTimestamp, String userId) {
        this.phone = phone;
        this.username = username;
        this.createdTimestamp = createdTimestamp;
        this.userId = userId;
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFcmToken() {
        return fcmToken;
    }
    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }
    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }



    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }


   /* public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }*/

    public List<String> getPortfolio() {
        return portfolio;
    }

    public void setPortfolio(List<String> portfolio) {
        this.portfolio = portfolio;
    }


    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public float getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(float averageRating) {
        this.averageRating = averageRating;
    }

}
