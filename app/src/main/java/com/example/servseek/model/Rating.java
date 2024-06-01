package com.example.servseek.model;

public class Rating {
    private float rating;
    private String userId;

    public Rating() {
        // No-arg constructor for Firestore
    }

    public Rating(float rating, String userId) {
        this.rating = rating;
        this.userId = userId;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
