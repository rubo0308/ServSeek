package com.example.servseek;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import com.example.servseek.model.UserModel;
import com.example.servseek.utils.AndroidUtil;
import com.example.servseek.utils.FirebaseUtil;

public class SplashActivity extends AppCompatActivity {
    private TextView animatedTextView;
    private String fullText = "Welcome to ServSeek!!!";
    private int index;
    private long delay = 120;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        animatedTextView = findViewById(R.id.animatedTextView);
        animateText();

        handleIntent();
    }

    private void animateText() {
        index = 0;
        animatedTextView.setText("");
        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (index < fullText.length()) {
                    animatedTextView.append(String.valueOf(fullText.charAt(index++)));
                    handler.postDelayed(this, delay);
                }
            }
        };
        handler.post(runnable);
    }

    private void handleIntent() {
        if (getIntent().getExtras() != null) {
            String userId = getIntent().getExtras().getString("userId");
            System.out.println(userId);
            FirebaseUtil.allUserCollectionReference().document(userId).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            UserModel model = task.getResult().toObject(UserModel.class);
                            Intent mainIntent = new Intent(this, MainActivity.class);
                            mainIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            startActivity(mainIntent);

                            Intent intent = new Intent(this, MainActivity.class);
                            AndroidUtil.passUserModelAsIntent(intent, model);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        }
                    });
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (FirebaseUtil.isLoggedIn()) {
                        startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    } else {
                        startActivity(new Intent(SplashActivity.this, LoginPhoneNumberActivity.class));
                    }
                    finish();
                }
            }, 2800);
        }
    }
}
