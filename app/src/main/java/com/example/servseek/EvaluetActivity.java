package com.example.servseek;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class EvaluetActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private String userId;
    private boolean[] isRated;
    private RatingBar[] ratingBars;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluet);
        db = FirebaseFirestore.getInstance();

        ImageButton backButton = findViewById(R.id.back_btn);
        ratingBars = new RatingBar[]{
                findViewById(R.id.ratingBar),
                findViewById(R.id.ratingBar1),
                findViewById(R.id.ratingBar2),
                findViewById(R.id.ratingBar3),
                findViewById(R.id.ratingBar4)
        };

        isRated = new boolean[ratingBars.length];

        Log.d("EvaluateActivity", "averageNumberTextView initialized");

        userId = getIntent().getStringExtra("userId");

        for (int i = 0; i < ratingBars.length; i++) {
            final int index = i;
            ratingBars[i].setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
                isRated[index] = true;
                saveIndividualRating(index, rating);
                displaySessionAverage();
            });
        }

        fetchRatingsAndCalculateAverage();

        backButton.setOnClickListener(view -> finish());
    }

    private void fetchRatingsAndCalculateAverage() {
        DocumentReference userRef = db.collection("users").document(userId);
        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                double total = 0;
                int count = 0;
                for (int i = 0; i < ratingBars.length; i++) {
                    String ratingKey = "rating" + i;
                    Double rating = documentSnapshot.getDouble(ratingKey);
                    if (rating != null) {
                        ratingBars[i].setRating(rating.floatValue());
                        total += rating;
                        count++;
                        isRated[i] = true;
                    }
                }
                if (count > 0) {
                    double average = total / count;
                    displayAverage(average);
                }
            }
        });
    }

    private void saveIndividualRating(int index, float newRating) {
        DocumentReference userRef = db.collection("users").document(userId);
        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                double total = 0;
                int count = 0;
                Map<String, Object> ratingData = new HashMap<>();
                for (int i = 0; i < ratingBars.length; i++) {
                    String ratingKey = "rating" + i;
                    Double existingRating = documentSnapshot.getDouble(ratingKey);
                    if (existingRating != null) {
                        if (i == index) {
                            double averageRating = (existingRating + newRating) / 2;
                            ratingData.put(ratingKey, averageRating);
                            total += averageRating;
                        } else {
                            ratingData.put(ratingKey, existingRating);
                            total += existingRating;
                        }
                        count++;
                    } else if (i == index) {
                        ratingData.put(ratingKey, newRating);
                        total += newRating;
                        count++;
                    }
                }
                if (count > 0) {
                    double average = total / count;
                    ratingData.put("averageRating", average);
                    userRef.update(ratingData)
                            .addOnSuccessListener(aVoid -> displayAverage(average))
                            .addOnFailureListener(e -> Log.e("EvaluetActivity", "Failed to update rating", e));
                }
            }
        }).addOnFailureListener(e -> Log.e("EvaluetActivity", "Failed to fetch existing ratings", e));
    }

    private void displayAverage(double average) {
        TextView averageTextView = findViewById(R.id.averageNumberTextView);
        averageTextView.setText(String.format(Locale.US, "Average Rating: %.1f", average));
    }

    private void displaySessionAverage() {
        double total = 0;
        int count = 0;
        for (int i = 0; i < ratingBars.length; i++) {
            if (isRated[i]) {
                total += ratingBars[i].getRating();
                count++;
            }
        }
        if (count > 0) {
            double average = total / count;
            TextView averageTextView = findViewById(R.id.sessionAverageNumberTextView);
            averageTextView.setText(String.format(Locale.US, "Session Average: %.1f", average));
        }
    }
}