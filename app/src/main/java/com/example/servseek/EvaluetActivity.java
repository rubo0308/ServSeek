package com.example.servseek;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.servseek.adapter.SearchUserRecyclerAdapter;
import com.example.servseek.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EvaluetActivity extends AppCompatActivity {
    private FirebaseFirestore db;


    private RatingBar ratingBar, ratingBar1, ratingBar2, ratingBar3, ratingBar4;
    private boolean[] isRated = {false, false, false, false, false};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluet);
        db = FirebaseFirestore.getInstance();


        ImageButton backButton = findViewById(R.id.back_btn);
        ratingBar = findViewById(R.id.ratingBar);
        ratingBar1 = findViewById(R.id.ratingBar1);
        ratingBar2 = findViewById(R.id.ratingBar2);
        ratingBar3 = findViewById(R.id.ratingBar3);
        ratingBar4 = findViewById(R.id.ratingBar4);
        EditText editText = findViewById(R.id.edittext);
        EditText editText1 = findViewById(R.id.edittext1);
        EditText editText2 = findViewById(R.id.edittext2);
        EditText editText3 = findViewById(R.id.edittext3);
        TextView averageNumberTextView = findViewById(R.id.averageNumberTextView);
        Log.d("EvaluetActivity", "averageNumberTextView initialized");

        EditText editText4 = findViewById(R.id.edittext4);

        RatingBar.OnRatingBarChangeListener listener = new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                // Mark the corresponding RatingBar as rated
                if (ratingBar.getId() == R.id.ratingBar) {
                    isRated[0] = true;
                } else if (ratingBar.getId() == R.id.ratingBar1) {
                    isRated[1] = true;
                } else if (ratingBar.getId() == R.id.ratingBar2) {
                    isRated[2] = true;
                } else if (ratingBar.getId() == R.id.ratingBar3) {
                    isRated[3] = true;
                } else if (ratingBar.getId() == R.id.ratingBar4) {
                    isRated[4] = true;
                }



                if (areAllRated()) {
                    float averageRating = calculateAverageRating();
                    showToastWithAverageRating(averageRating);
                }
            }
        };

        // Set the listener to all the RatingBars
        ratingBar.setOnRatingBarChangeListener(listener);
        ratingBar1.setOnRatingBarChangeListener(listener);
        ratingBar2.setOnRatingBarChangeListener(listener);
        ratingBar3.setOnRatingBarChangeListener(listener);
        ratingBar4.setOnRatingBarChangeListener(listener);


        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Save average rating to Firebase
                if (areAllRated()) {
                    float averageRating = calculateAverageRating();
                    saveRatingToFirebase(averageRating);
                    String averageRatingText = "Average Rating: " + averageRating;
                    averageNumberTextView.setText(averageRatingText);
                    Log.d("EvaluetActivity", "Average Rating set to TextView: " + averageRatingText);
                    System.out.println("LINE 98");
                }
                // Finish the activity and return to the previous one
                finish();
            }
        });
    }

    private boolean areAllRated() {
        for (boolean rated : isRated) {
            if (!rated) return false;
        }
        return true;
    }


    private float calculateAverageRating() {
        float totalRatings = ratingBar.getRating() + ratingBar1.getRating() +
                ratingBar2.getRating() + ratingBar3.getRating() +
                ratingBar4.getRating();

        return totalRatings / 5;
    }

    private void showToastWithAverageRating(float averageRating) {
        Toast.makeText(this, "Average Rating: " + averageRating, Toast.LENGTH_LONG).show();
    }
    private void openNewActivityWithRating(float averageRating) {
        Intent intent = new Intent(EvaluetActivity.this, SearchUserRecyclerAdapter.class);
        intent.putExtra("average_rating", averageRating);
        startActivity(intent);
    }

    private void saveRatingToFirebase(float averageRating) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = FirebaseUtil.currentUserId();
        System.out.println("User ID: " + userId);
        if (userId != null) {
            Map<String, Object> ratingData = new HashMap<>();
            ratingData.put("averageRating", averageRating);

            db.collection("users").document(userId).update("averageRating", averageRating)
                    .addOnSuccessListener(aVoid -> Toast.makeText(EvaluetActivity.this, "Average Rating saved to Firebase", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(EvaluetActivity.this, "Failed to save Average Rating to Firebase", Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(EvaluetActivity.this, "Current user ID is null", Toast.LENGTH_SHORT).show();
        }
    }
}
