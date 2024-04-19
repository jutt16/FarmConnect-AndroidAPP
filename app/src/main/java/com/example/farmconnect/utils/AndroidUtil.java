package com.example.farmconnect.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
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
    public static void setImageByUrl(Context context, String imageUrl, ImageView imageView){
        Glide.with(context)
                .load(imageUrl)
                .apply(RequestOptions.circleCropTransform()) // Apply circle crop transformation
                .into(imageView);
    }
//    public static void setMenuItemIconFromUrl(Context context, final MenuItem menuItem, String imageUrl) {
//        RequestOptions requestOptions = new RequestOptions()
//                .diskCacheStrategy(DiskCacheStrategy.ALL);
//
//        Glide.with(context)
//                .asBitmap()
//                .load(imageUrl)
//                .apply(requestOptions)
//                .into(new CustomTarget<Bitmap>() {
//                    @Override
//                    public void onResourceReady(@NonNull Bitmap resource, Transition<? super Bitmap> transition) {
//                        // Convert Bitmap to Drawable
//                        Drawable drawable = new BitmapDrawable(context.getResources(), resource);
//                        // Set MenuItem icon
//                        menuItem.setIcon(drawable);
//                    }
//
//                    @Override
//                    public void onLoadFailed(@NonNull Drawable errorDrawable) {
//                        super.onLoadFailed(errorDrawable);
//                        Log.e("MenuIconLoader", "Failed to load image for MenuItem");
//                    }
//
//                    @Override
//                    public void onLoadCleared(Drawable placeholder) {
//                        // Placeholder cleanup
//                    }
//                });
//    }
}
