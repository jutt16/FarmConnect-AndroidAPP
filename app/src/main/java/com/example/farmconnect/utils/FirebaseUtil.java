package com.example.farmconnect.utils;

import android.util.Log;

import com.example.farmconnect.Models.UserModel;
import com.example.farmconnect.SearchUserActivity;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.List;

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

//    public static String currentUserName() {
//        UserModel currentUserModel=null;
//        String name=null;
//        currentUserDetails().get().addOnCompleteListener(task -> {
//            currentUserModel = task.getResult().toObject(UserModel.class);
//        });
//        name = currentUserModel.getUserName();
//        return name;
//    }

    public static CollectionReference allUserCollectionReference(){
        try {
            Log.d("Success", "allUserCollectionReference: success");
            return FirebaseFirestore.getInstance().collection("users");
        } catch (Exception e) {
            Log.d("Error", "allUserCollectionReference: "+e.getMessage());
        }
        return null;
    }
    public static DocumentReference getChatroomReference(String chatroomId){
        return  FirebaseFirestore.getInstance().collection("chatrooms").document(chatroomId);
    }
    public static CollectionReference getChatroomMessageReference(String chatroomId){
        return getChatroomReference(chatroomId).collection("chats");
    }

    public static String getChatroomId(String userId1,String userId2){
        if(userId1.hashCode()<userId2.hashCode()){
            return userId1+"_"+userId2;
        }else {
            return userId2+"_"+userId1;
        }
    }
    public  static  CollectionReference allChatroomCollectionReference(){
        return FirebaseFirestore.getInstance().collection("chatrooms");
    }
    public static DocumentReference getOtherUserFromChatroom(List<String> userIds){
        if (userIds.get(0).equals(FirebaseUtil.currentUserId())){
            return allUserCollectionReference().document(userIds.get(1));
        }else {
            return allUserCollectionReference().document(userIds.get(0));
        }
    }
    public static String timestampToString(Timestamp timestamp){
        return new SimpleDateFormat("HH:MM").format(timestamp.toDate());
    }
    public static void logout(){
        FirebaseAuth.getInstance().signOut();
    }
    public static StorageReference getCurrentProfilePicStorageRef(){
        return FirebaseStorage.getInstance().getReference().child("profile_pic")
                .child(FirebaseUtil.currentUserId());
    }
    public static StorageReference getOtherProfilePicStorageRef(String otherUserId){
        return FirebaseStorage.getInstance().getReference().child("profile_pic")
                .child(otherUserId);
    }
}
