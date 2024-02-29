package com.example.servseek.utils;

import static android.content.ContentValues.TAG;

import android.net.Uri;
import android.util.Log;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.text.SimpleDateFormat;
import java.util.List;

public class FirebaseUtil {

    // Returns the current user's ID from FirebaseAuth
    public static String currentUserId() {
        return FirebaseAuth.getInstance().getUid();
    }

    // Checks if a user is logged in
    public static boolean isLoggedIn() {
        return currentUserId() != null;
    }

    // Gets a DocumentReference pointing to the current user's details in Firestore
    public static DocumentReference currentUserDetails() {
        return FirebaseFirestore.getInstance().collection("users").document(currentUserId());
    }

    // Returns a CollectionReference to all users in Firestore
    public static CollectionReference allUserCollectionReference() {
        return FirebaseFirestore.getInstance().collection("users");
    }

    // Returns a DocumentReference for a specific chatroom in Firestore
    public static DocumentReference getChatroomReference(String chatroomId) {
        return FirebaseFirestore.getInstance().collection("chatrooms").document(chatroomId);
    }

    // Generates a chatroom ID based on two user IDs
    public static String getChatroomId(String userId1, String userId2) {
        if (userId1.hashCode() < userId2.hashCode()) {
            return userId1 + "_" + userId2;
        } else {
            return userId2 + "_" + userId1;
        }
    }

    // Returns a CollectionReference for messages within a specific chatroom
    public static CollectionReference getChatroomMessageReference(String chatroomId) {
        return getChatroomReference(chatroomId).collection("chats");
    }

    // Returns a CollectionReference to all chatrooms in Firestore
    public static CollectionReference allChatroomCollectionReference() {
        return FirebaseFirestore.getInstance().collection("chatrooms");
    }

    // Determines the other user in a chatroom based on the current user's ID
    public static DocumentReference getOtherUserFromChatroom(List<String> userIds) {
        if (userIds.get(0).equals(FirebaseUtil.currentUserId())) {
            return allUserCollectionReference().document(userIds.get(1));
        } else {
            return allUserCollectionReference().document(userIds.get(0));
        }
    }

    // Converts a Timestamp to a String representation
    public static String timestampToString(Timestamp timestamp) {
        return new SimpleDateFormat("HH:mm").format(timestamp.toDate());
    }

    // Logs out the current user
    public static void logout() {
        FirebaseAuth.getInstance().signOut();
    }

    // Returns a StorageReference for the current user's profile picture
    public static StorageReference getCurrentProfilePicStorageRef() {
        return FirebaseStorage.getInstance().getReference()
                .child("profile_pic")
                .child(FirebaseUtil.currentUserId());
    }

    // Returns a StorageReference for another user's profile picture
    public static StorageReference getOtherProfilePicStorageRef(String otherUserId) {
        return FirebaseStorage.getInstance().getReference()
                .child("profile_pic")
                .child(otherUserId);
    }

    // Uploads an image to Firebase Storage and logs the URL or error
    public static void uploadImageToStorage(String userId, Uri imageUri) {
        StorageReference imageRef = FirebaseStorage.getInstance().getReference()
                .child("images")
                .child(userId)
                .child("portfolio_" + System.currentTimeMillis() + ".jpg");

        imageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl()
                        .addOnSuccessListener(downloadUri -> {
                            String imageUrl = downloadUri.toString();
                            Log.d(TAG, "Image uploaded successfully. URL: " + imageUrl);
                            // Additional actions like updating Firestore can be added here
                        })
                        .addOnFailureListener(exception -> Log.e(TAG, "Failed to get download URL: " + exception.getMessage())))
                .addOnFailureListener(exception -> Log.e(TAG, "Image upload failed: " + exception.getMessage()));
    }


    public static void updatePortfolioImageUrl(String imageUrl) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("users").document(currentUserId());

        // Assuming you have a "portfolio" field in your user document
        userRef.update("portfolio", FieldValue.arrayUnion(imageUrl))
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Portfolio image URL added to Firestore"))
                .addOnFailureListener(e -> Log.e(TAG, "Error updating portfolio image URL", e));
    }
    // Initialize Firebase Database and optionally enable offline persistence
}