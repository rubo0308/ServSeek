package com.example.servseek;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageButton;

import com.example.servseek.utils.FirebaseUtil;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import org.checkerframework.checker.nullness.qual.NonNull;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    ImageButton searchButton;

    ProfileFragment profileFragment;
    HomeFragment homeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Verify if the user data exists before setting the content view
        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                setContentView(R.layout.activity_main);

                profileFragment = new ProfileFragment();
                homeFragment = new HomeFragment();
                bottomNavigationView = findViewById(R.id.bottom_navigation);
                searchButton = findViewById(R.id.main_search_btn);
                searchButton.setOnClickListener((v) -> {
                    startActivity(new Intent(MainActivity.this, SearchUserActivity.class));
                });

                bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        if (item.getItemId() == R.id.menu_home) {
                            getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout, homeFragment).commit();
                        }
                        if (item.getItemId() == R.id.menu_profile) {
                            getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout, profileFragment).commit();
                        }

                        return true;
                    }
                });
                bottomNavigationView.setSelectedItemId(R.id.menu_home);
                getFCMToken();
            } else {
                // User data not found, navigate back to login
                SharedPreferences.Editor editor = getSharedPreferences("UserPrefs", MODE_PRIVATE).edit();
                editor.clear();
                editor.apply();
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(MainActivity.this, LoginPhoneNumberActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }

    void getFCMToken() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String token = task.getResult();
                FirebaseUtil.currentUserDetails().update("fcmToken", token);
            }
        });
    }
}
