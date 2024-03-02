package com.example.servseek;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.servseek.utils.FirebaseUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;

public class SettingsFragment extends Fragment {
    FirebaseAuth mAuth;
    TextView logoutButton;
    TextView helpSupportButton;
    TextView notificationsButton; // Add this line
// Reference for the help and support button

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        // Initialize the logout button and its click listener
        logoutButton = view.findViewById(R.id.logout_button);
        logoutButton.setClickable(true);

        mAuth = FirebaseAuth.getInstance();

        logoutButton.setOnClickListener((v) -> FirebaseMessaging.getInstance().deleteToken().addOnCompleteListener(task -> {
            if (mAuth.getCurrentUser() == null) {
                logoutButton.setClickable(false);
            } else if (mAuth.getCurrentUser() != null) {
                logoutButton.setClickable(true);

                if (task.isSuccessful()) {
                    FirebaseUtil.logout();
                    Intent intent = new Intent(getContext(), SplashActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }
        }));

        // Initialize the help and support button and its click listener
        helpSupportButton = view.findViewById(R.id.help_and_support_button); // Replace with your actual button ID
        helpSupportButton.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), HelpAndSupportActivity.class);
            startActivity(intent);
        });
        notificationsButton = view.findViewById(R.id.notifications_button); // Make sure the ID matches your layout
        notificationsButton.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), NotificationsActivity.class);
            startActivity(intent);
        });
        return view;
    }
}
