package com.example.servseek.model;

public class Rating {
    private float rating;

    public Rating() {
        // Empty constructor needed for Firestore
    }

    public Rating(float rating) {
        this.rating = rating;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }
}
