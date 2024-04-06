package com.example.servseek;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.example.servseek.adapter.PortfolioAdapter;
import com.example.servseek.model.UserModel;
import com.example.servseek.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;

public class OtherUserActivity extends AppCompatActivity {
    ImageView profilePic;
    TextView usernameInput, professionInput, phoneInput, aboutInput, averageNumberTextView, logoutBtn;
    ProgressBar progressBar;
    RecyclerView portfolioRecyclerView;
    UserModel otherUserModel;

    private PortfolioAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_user); // This layout is assumed to be identical to your fragment's layout

        initializeViews();
        String userId = getIntent().getStringExtra("userId");
        if (userId != null && !userId.isEmpty()) {
            fetchUserData(userId);
        } else {
            finish(); // Finish activity if no user ID is provided, or handle this case as needed
        }
    }

    private void initializeViews() {
        profilePic = findViewById(R.id.profile_image_view);
        usernameInput = findViewById(R.id.editTextName);
        phoneInput = findViewById(R.id.profile_phone);
        professionInput = findViewById(R.id.profile_prof);
        aboutInput = findViewById(R.id.editTextProfessionalDescription);
        averageNumberTextView = findViewById(R.id.averageNumberTextView);
        logoutBtn = findViewById(R.id.logout_btn); // You might want to hide or remove this if not applicable
        progressBar = findViewById(R.id.profile_progress_bar);
        portfolioRecyclerView = findViewById(R.id.portfolioRecyclerView);

        adapter = new PortfolioAdapter(this, null); // Assuming PortfolioAdapter can be initialized this way
        portfolioRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        portfolioRecyclerView.setAdapter(adapter);
    }

    private void fetchUserData(String userId) {
        progressBar.setVisibility(ProgressBar.VISIBLE);
        FirebaseUtil.getUserById(userId).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                progressBar.setVisibility(ProgressBar.GONE);
                if (task.isSuccessful() && task.getResult() != null) {
                    UserModel user = task.getResult().toObject(UserModel.class);
                    if (user != null) {
                        updateUIWithUserData(user);
                    }
                } else {
                    // Handle error
                }
            }
        });
    }

    private void updateUIWithUserData(UserModel user) {
        // Set user information to views
        usernameInput.setText(user.getUsername());
        phoneInput.setText(user.getPhone());
        professionInput.setText(user.getProfession());
        aboutInput.setText(user.getAbout());
        averageNumberTextView.setText(String.valueOf(user.getAverageRating()));
        // Update portfolio RecyclerView and profile picture as needed
    }
}
