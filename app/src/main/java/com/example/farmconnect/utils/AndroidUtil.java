package com.example.farmconnect.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.farmconnect.Models.UserModel;

import java.io.ByteArrayOutputStream;

public class AndroidUtil {
    public static void showToast(Context context,String message) {
        Toast.makeText(context,message,Toast.LENGTH_LONG).show();
    }
    //validate email
    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }
    public static void passUserModelAsIntent(Intent intent, UserModel model){
        intent.putExtra("username",model.getUserName());
        intent.putExtra("phone",model.getMobile());
        intent.putExtra("userId",model.getUserId());
        intent.putExtra("fcmToken",model.getFcmToken());
    }

    public static UserModel getUserModelFromIntent(Intent intent){
        UserModel userModel = new UserModel();
        userModel.setUserName(intent.getStringExtra("username"));
        userModel.setMobile(intent.getStringExtra("phone"));
        userModel.setUserId(intent.getStringExtra("userId"));
        userModel.setFcmToken(intent.getStringExtra("fcmToken"));
        return userModel;
    }

    public static void setProfilePic(Context context, Uri imageUri, ImageView imageView){
        Glide.with(context)
                .load(imageUri)
                .apply(RequestOptions.circleCropTransform()) // Apply circle crop transformation
                .into(imageView);
    }


}
