package com.example.farmconnect.utils;

import android.util.Log;

import com.example.farmconnect.SearchUserActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class FirebaseUtil {

    public static String currentUserId() {
        return FirebaseAuth.getInstance().getUid();
    }

    public static boolean isLoggedIn(){
        if (currentUserId()!=null){
            return true;
        }
        return false;
    }

    public static DocumentReference currentUserDetails() {
        return FirebaseFirestore.getInstance().collection("users").document(currentUserId());
    }

    public static CollectionReference allUserCollectionReference(){
        try {
            Log.d("Success", "allUserCollectionReference: success");
            return FirebaseFirestore.getInstance().collection("users");
        } catch (Exception e) {
            Log.d("Error", "allUserCollectionReference: "+e.getMessage());
        }
        return null;
    }
}
