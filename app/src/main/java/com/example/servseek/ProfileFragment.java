package com.example.servseek;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.example.servseek.adapter.PortfolioAdapter;
import com.example.servseek.model.UserModel;
import com.example.servseek.utils.AndroidUtil;
import com.example.servseek.utils.FirebaseUtil;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class ProfileFragment extends Fragment {

    ImageView profilePic;
    EditText usernameInput;
    EditText phoneInput;
    Button updateProfileBtn;
    ProgressBar progressBar;
    TextView logoutBtn;
    UserModel currentUserModel;
    ActivityResultLauncher<Intent> imagePickLauncher;
    Uri selectedImageUri;

    private PortfolioAdapter adapter;
    private final int PICK_IMAGE_REQUEST = 1; // Request code for picking an image
    private int currentImagePosition = -1;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imagePickLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null && data.getData() != null) {
                            selectedImageUri = data.getData();
                            setImageUri(selectedImageUri); // Update the ImageView with the selected image
                        }
                    }
                }
        );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        profilePic = view.findViewById(R.id.profile_image_view);
        usernameInput = view.findViewById(R.id.profile_username);
        phoneInput = view.findViewById(R.id.profile_phone);
        updateProfileBtn = view.findViewById(R.id.profle_update_btn); // Ensure the ID matches your layout
        progressBar = view.findViewById(R.id.profile_progress_bar);
        logoutBtn = view.findViewById(R.id.logout_btn);

        getUserData();

        updateProfileBtn.setOnClickListener((v) -> updateBtnClick());

        logoutBtn.setOnClickListener((v) -> FirebaseMessaging.getInstance().deleteToken().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUtil.logout();
                Intent intent = new Intent(getContext(), SplashActivity.class); // Adjust as per your activity
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        }));

        profilePic.setOnClickListener((v) -> ImagePicker.with(this)
                .cropSquare()
                .compress(512)
                .maxResultSize(512, 512)
                .createIntent(intent -> {
                    imagePickLauncher.launch(intent);
                    return null;
                }));

        RecyclerView portfolioRecyclerView = view.findViewById(R.id.portfolioRecyclerView);
        portfolioRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        adapter = new PortfolioAdapter(getContext(), this::onImageClick);
        portfolioRecyclerView.setAdapter(adapter);

        // Fetch portfolio images from Firestore
        fetchPortfolioImages();

        return view;
    }

    public void onImageClick(int position) {
        currentImagePosition = position;
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            if (currentImagePosition != -1) {
                adapter.addImageUri(imageUri.toString()); // Convert URI to string and add to adapter
                currentImagePosition = -1; // Reset after handling
                uploadPortfolioImage(imageUri); // Upload the image to Firebase Storage
            }
        }
    }


    private void uploadPortfolioImage(Uri imageUri) {
        StorageReference portfolioRef = FirebaseStorage.getInstance().getReference().child("portfolio_images/" + System.currentTimeMillis() + ".jpg");
        portfolioRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> portfolioRef.getDownloadUrl().addOnSuccessListener(uri -> {
            String imageUrl = uri.toString();
            // Update the Firebase Realtime Database or Firestore with the image URL
            // For example, if using Firestore:
            FirebaseUtil.updatePortfolioImageUrl(imageUrl);

            // Here you should update your portfolio list and Firestore database with the new image URL
            FirebaseUtil.uploadImageToStorage(FirebaseUtil.currentUserId(), uri);
            AndroidUtil.showToast(getContext(), "Upload successful");
        })).addOnFailureListener(e -> AndroidUtil.showToast(getContext(), "Upload failed: " + e.getMessage()));
    }

    private void fetchPortfolioImages() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("users").document(FirebaseUtil.currentUserId());

        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                List<String> portfolio = documentSnapshot.toObject(UserModel.class).getPortfolio();
                if (portfolio != null && !portfolio.isEmpty()) {
                    // Fetch image URLs from Firebase Storage
                    for (String imageUrl : portfolio) {
                        FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl).getDownloadUrl()
                                .addOnSuccessListener(uri -> {
                                    adapter.addImageUri(uri.toString());
                                });
                    }
                }
            }
        });
    }


    void updateBtnClick() {
        String newUsername = usernameInput.getText().toString();
        if (newUsername.isEmpty() || newUsername.length() < 3) {
            usernameInput.setError("Username length should be at least 3 chars");
            return;
        }
        currentUserModel.setUsername(newUsername);
        setInProgress(true);

        if (selectedImageUri != null) {
            FirebaseUtil.getCurrentProfilePicStorageRef().putFile(selectedImageUri)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            uploadPortfolioImage(selectedImageUri);
                        } else {
                            AndroidUtil.showToast(getContext(), "Failed to upload image");
                            setInProgress(false);
                        }
                    });
        } else {
            updateToFirestore();
        }
    }

    void updateToFirestore() {
        FirebaseUtil.currentUserDetails().set(currentUserModel)
                .addOnCompleteListener(task -> {
                    setInProgress(false);
                    if (task.isSuccessful()) {
                        AndroidUtil.showToast(getContext(), "Updated successfully");
                    } else {
                        AndroidUtil.showToast(getContext(), "Update failed");
                    }
                });
    }

    void getUserData() {
        setInProgress(true);
        FirebaseUtil.getCurrentProfilePicStorageRef().getDownloadUrl()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Profile picture exists, set it
                        Uri uri = task.getResult();
                        AndroidUtil.setProfilePic(getContext(), uri, profilePic);
                    } else {
                        // Profile picture does not exist, set a default image
                        profilePic.setImageResource(R.drawable.baseline_person_24); // Fallback image
                    }
                    // Continue to fetch other user details
                    FirebaseUtil.currentUserDetails().get().addOnCompleteListener(task2 -> {
                        setInProgress(false);
                        if (task2.isSuccessful() && task2.getResult() != null) {
                            currentUserModel = task2.getResult().toObject(UserModel.class);
                            if (currentUserModel != null) {
                                usernameInput.setText(currentUserModel.getUsername());
                                phoneInput.setText(currentUserModel.getPhone());
                            }
                        }
                    });
                });
    }

    void setInProgress(boolean inProgress) {
        progressBar.setVisibility(inProgress ? View.VISIBLE : View.GONE);
        updateProfileBtn.setVisibility(inProgress ? View.GONE : View.VISIBLE);
    }

    private void setImageUri(Uri imageUri) {
        if (imageUri != null) {
            profilePic.setImageURI(imageUri);
        }
    }
}