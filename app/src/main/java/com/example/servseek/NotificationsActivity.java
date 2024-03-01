package com.example.servseek;




import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class NotificationsActivity extends AppCompatActivity {
    private TextView labelNotificationStatus;
    private Switch switchNotification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        labelNotificationStatus = findViewById(R.id.label_notification_status);
        switchNotification = findViewById(R.id.switch1);
        updateNotificationStatusText(switchNotification.isChecked());


        // Set the initial text based on the switch state


        ImageButton backButton = findViewById(R.id.back_btn);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Finish the activity and return to the previous one
                finish();
            }
        });
        switchNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                updateNotificationStatusText(isChecked);
            }
        });
    }

    private void updateNotificationStatusText(boolean isOn) {
        labelNotificationStatus.setText(isOn ? "Notification is on" : "Notification is off");
    }

    }

