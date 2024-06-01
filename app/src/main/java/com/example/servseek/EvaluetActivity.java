package com.example.servseek;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.content.Intent;
import android.widget.TextView;

import com.example.servseek.utils.FirebaseUtil;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.example.servseek.model.Rating;

import java.util.Locale;

public class EvaluetActivity extends AppCompatActivity {
    private RatingBar ratingBar;
    private RatingBar ratingBar1;
    private RatingBar ratingBar2;
    private RatingBar ratingBar3;
    private RatingBar ratingBar4;
    private TextView sessionAverageTextView;
    private String userId;
    private String currentUserId;
    private float sessionAverageRating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluet);

        ImageButton backButton = findViewById(R.id.back_btn);
        Button submitButton = findViewById(R.id.submitRatingButton);
        ratingBar = findViewById(R.id.ratingBar);
        ratingBar1 = findViewById(R.id.ratingBar1);
        ratingBar2 = findViewById(R.id.ratingBar2);
        ratingBar3 = findViewById(R.id.ratingBar3);
        ratingBar4 = findViewById(R.id.ratingBar4);
        sessionAverageTextView = findViewById(R.id.sessionAverageNumberTextView);

        userId = getIntent().getStringExtra("userId");
        currentUserId = FirebaseUtil.currentUserId();

        backButton.setOnClickListener(view -> finish());
        submitButton.setOnClickListener(view -> checkAndSubmitRating());
    }

    private void checkAndSubmitRating() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(userId).collection("ratings")
                .whereEqualTo("userId", currentUserId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            // User has already rated
                            sessionAverageTextView.setText("You have already rated this user.");
                        } else {
                            // User has not rated yet, proceed to submit the rating
                            submitRating();
                        }
                    } else {
                        // Handle error
                        sessionAverageTextView.setText("Error checking rating. Please try again.");
                    }
                });
    }

    private void submitRating() {
        float rating0 = ratingBar.getRating();
        float rating1 = ratingBar1.getRating();
        float rating2 = ratingBar2.getRating();
        float rating3 = ratingBar3.getRating();
        float rating4 = ratingBar4.getRating();

        sessionAverageRating = (rating0 + rating1 + rating2 + rating3 + rating4) / 5;
        sessionAverageTextView.setText(String.format(Locale.US, "Session Average: %.1f", sessionAverageRating));

        saveRatingToFirestore(sessionAverageRating);

        Intent resultIntent = new Intent();
        resultIntent.putExtra("averageRating", sessionAverageRating);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    private void saveRatingToFirestore(float averageRating) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Rating rating = new Rating(averageRating, currentUserId);
        db.collection("users").document(userId).collection("ratings").add(rating)
                .addOnSuccessListener(documentReference -> {
                    // Handle success
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                });
    }
}
