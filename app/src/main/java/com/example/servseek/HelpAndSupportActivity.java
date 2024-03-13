package com.example.servseek;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

public class HelpAndSupportActivity extends AppCompatActivity {
    EditText editTextEmail;
    Button btnSaveEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_and_support);

        TextView emailContact = findViewById(R.id.email_contact);
       // Reference to the EditText
        editTextEmail = findViewById(R.id.inputEmail);
        btnSaveEmail = findViewById(R.id.btnSaveEmail);
        ImageButton backButton = findViewById(R.id.back_bn);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Finish the activity and return to the previous one
                finish();
            }
        });

        btnSaveEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveEmail();
            }
        });
        emailContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setData(Uri.parse("mailto:rubenhalla16@gmail.com"));
                if (emailIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(emailIntent);
                } else {
                    // Handle the situation where no email app is installed
                    Toast.makeText(HelpAndSupportActivity.this,
                            "No email app installed", Toast.LENGTH_SHORT).show();
                }
            }
        });

        findViewById(R.id.phone_contact).setOnClickListener(view -> {

            Intent dialIntent = new Intent(Intent.ACTION_DIAL);
            dialIntent.setData(Uri.parse("tel:(123) 456-7890"));
            startActivity(dialIntent);
        });

        findViewById(R.id.chat_support_button).setOnClickListener(view -> {

        });
        Button chatWithSupportButton = findViewById(R.id.chat_support_button);
        chatWithSupportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("mailto:rubenhalla16@gmail.com"));
                intent.putExtra(Intent.EXTRA_SUBJECT, "Support Request"); // Pre-fill the subject line

                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                } else {
                    Toast.makeText(HelpAndSupportActivity.this,
                            "No email app installed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void saveEmail() {
        String email = editTextEmail.getText().toString().trim();
        if (!email.isEmpty()) {
            String userId = "unique_user_id";
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("users").document(userId)
                    .update("email", email)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(HelpAndSupportActivity.this, "Email saved successfully", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(HelpAndSupportActivity.this, "Error saving email", Toast.LENGTH_SHORT).show();
                    });
        } else {
            editTextEmail.setError("Email cannot be empty");
        }
    }
}
