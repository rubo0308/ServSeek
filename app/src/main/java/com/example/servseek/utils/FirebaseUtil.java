
package com.example.servseek.utils;

import static android.content.ContentValues.TAG;

import android.net.Uri;
import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.text.SimpleDateFormat;
import java.util.List;

public class FirebaseUtil {


    public static String currentUserId() {
        return FirebaseAuth.getInstance().getUid();
    }
    public static void updateUserProfession(String profession) {
        if (isLoggedIn()) {
            currentUserDetails().update("profession", profession)
                    .addOnSuccessListener(aVoid -> Log.d(TAG, "User profession updated successfully."))
                    .addOnFailureListener(e -> Log.e(TAG, "Error updating user profession.", e));
        }
    }


    public static boolean isLoggedIn() {
        return currentUserId() != null;
    }

    public static DocumentReference currentUserDetails() {
        return FirebaseFirestore.getInstance().collection("users").document(currentUserId());
    }


    public static CollectionReference allUserCollectionReference() {
        return FirebaseFirestore.getInstance().collection("users");
    }

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

    public static CollectionReference getChatroomMessageReference(String chatroomId) {
        return getChatroomReference(chatroomId).collection("chats");
    }

    public static CollectionReference allChatroomCollectionReference() {
        return FirebaseFirestore.getInstance().collection("chatrooms");
    }

    public static DocumentReference getOtherUserFromChatroom(List<String> userIds) {
        if (userIds.get(0).equals(FirebaseUtil.currentUserId())) {
            return allUserCollectionReference().document(userIds.get(1));
        } else {
            return allUserCollectionReference().document(userIds.get(0));
        }
    }

    public static String timestampToString(Timestamp timestamp) {
        return new SimpleDateFormat("HH:mm").format(timestamp.toDate());
    }

    public static void logout() {
        FirebaseAuth.getInstance().signOut();
    }

    public static StorageReference getCurrentProfilePicStorageRef() {
        return FirebaseStorage.getInstance().getReference()
                .child("profile_pic")
                .child(FirebaseUtil.currentUserId());
    }

    public static StorageReference getOtherProfilePicStorageRef(String otherUserId) {
        return FirebaseStorage.getInstance().getReference()
                .child("profile_pic")
                .child(otherUserId);
    }

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
                        })
                        .addOnFailureListener(exception -> Log.e(TAG, "Failed to get download URL: " + exception.getMessage())))
                .addOnFailureListener(exception -> Log.e(TAG, "Image upload failed: " + exception.getMessage()));
    }


    public static void updatePortfolioImageUrl(String imageUrl) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("users").document(currentUserId());

        userRef.update("portfolio", FieldValue.arrayUnion(imageUrl))
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Portfolio image URL added to Firestore"))
                .addOnFailureListener(e -> Log.e(TAG, "Error updating portfolio image URL", e));
    }

    public static Task<DocumentSnapshot> getUserById(String userId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();


        DocumentReference userDocRef = db.collection("users").document(userId);


        return userDocRef.get();
    }
}
