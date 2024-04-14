package com.example.farmconnect.utils;

import com.google.firebase.auth.FirebaseAuth;
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
}
