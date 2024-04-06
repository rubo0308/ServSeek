package com.example.servseek;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.servseek.adapter.PortfolioAdapter;
import com.example.servseek.model.UserModel;
import com.example.servseek.utils.AndroidUtil;
import com.example.servseek.utils.FirebaseUtil;
import com.google.firebase.firestore.FirebaseFirestore;

public class OtherUserActivity extends AppCompatActivity {
    ImageView profilePic;
    UserModel otherUser;
    String chatroomId;
    TextView usernameTextView, professionTextView, phoneTextView, aboutTextView, averageRatingTextView;
    ProgressBar progressBar;
    RecyclerView portfolioRecyclerView;
    UserModel otherUserModel;
    private PortfolioAdapter adapter;

    // A unique request code to identify the activity result
    private static final int EVALUATE_USER_REQUEST = 1;
    // Variable to hold the user ID being viewed
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_user);

        initializeViews();
        handleIntentExtras();
    }

    private void initializeViews() {
        otherUser = AndroidUtil.getUserModelFromIntent(getIntent());
        chatroomId = FirebaseUtil.getChatroomId(FirebaseUtil.currentUserId(), otherUser.getUserId());
        profilePic = findViewById(R.id.profile_image_view);
        usernameTextView = findViewById(R.id.editTextName);
        phoneTextView = findViewById(R.id.profile_phone);
        professionTextView = findViewById(R.id.profile_prof);
        aboutTextView = findViewById(R.id.editTextProfessionalDescription);
        averageRatingTextView = findViewById(R.id.averageNumberTextView);
        progressBar = findViewById(R.id.profile_progress_bar);
        portfolioRecyclerView = findViewById(R.id.portfolioRecyclerView);

        portfolioRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        adapter = new PortfolioAdapter(this, imageUri -> {});
        portfolioRecyclerView.setAdapter(adapter);

        Button evaluateButton = findViewById(R.id.evaluateButton);
        evaluateButton.setOnClickListener(v -> {
            // Ensure userId is not null or empty before proceeding
            if (userId != null && !userId.isEmpty()) {
                Intent intent = new Intent(OtherUserActivity.this, EvaluetActivity.class);
                intent.putExtra("userId", otherUser.getUserId());
                startActivity(intent);
            }
        });

        ImageButton backButton = findViewById(R.id.back_btn);
        backButton.setOnClickListener(view -> finish());
    }

    private void handleIntentExtras() {
        userId = getIntent().getStringExtra("userId");
        String imageUriStr = getIntent().getStringExtra("profileImageUri");

        if (imageUriStr != null && !imageUriStr.isEmpty()) {
            Uri imageUri = Uri.parse(imageUriStr);
            Glide.with(this).load(imageUri).into(profilePic);
        }

        if (userId != null && !userId.isEmpty()) {
            fetchUserData(userId);
        } else {
            finish();
        }
    }

    private void fetchUserData(String userId) {
        progressBar.setVisibility(View.VISIBLE);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(userId).get().addOnCompleteListener(task -> {
            progressBar.setVisibility(View.GONE);
            if (task.isSuccessful() && task.getResult() != null) {
                otherUserModel = task.getResult().toObject(UserModel.class);
                if (otherUserModel != null) {
                    updateUIWithUserData(otherUserModel);

                    // Fetch the profile picture from Firebase Storage
                    FirebaseUtil.getOtherProfilePicStorageRef(userId).getDownloadUrl().addOnSuccessListener(uri -> {
                        // Load the image into the ImageView using Glide
                        Glide.with(OtherUserActivity.this).load(uri).into(profilePic);
                    }).addOnFailureListener(e -> {
                        // Handle any errors
                    });
                }
            } else {

            }
        });
    }


    private void updateUIWithUserData(UserModel user) {
        usernameTextView.setText(user.getUsername());
        phoneTextView.setText(user.getPhone());
        professionTextView.setText(user.getProfession());
        aboutTextView.setText(user.getAbout());
        averageRatingTextView.setText(String.valueOf(user.getAverageRating()));

        if (profilePic.getDrawable() == null && user.getImageUrl() != null && !user.getImageUrl().isEmpty()) {
            Glide.with(this).load(user.getImageUrl()).into(profilePic);
        }

        if (user.getPortfolio() != null && !user.getPortfolio().isEmpty()) {
            adapter.updatePortfolio(user.getPortfolio());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EVALUATE_USER_REQUEST && resultCode == RESULT_OK && data != null) {
            // Code to handle the activity result
        }
    }
}
