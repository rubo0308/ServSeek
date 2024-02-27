package com.example.servseek;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.servseek.utils.FirebaseUtil;
import com.google.firebase.messaging.FirebaseMessaging;

public class SettingsFragment extends Fragment {
    TextView logoutButton;


    public SettingsFragment() {
        // Required empty public constructor
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        logoutButton = view.findViewById(R.id.logout_button);

        logoutButton.setOnClickListener((v) -> FirebaseMessaging.getInstance().deleteToken().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUtil.logout();
                Intent intent = new Intent(getContext(), SplashActivity.class); // Adjust as per your activity
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        }));

        return view;
    }

}
