package com.example.farmconnect.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.farmconnect.Models.UserModel;
import com.example.farmconnect.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

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
    public static File convertBitmapToFile(Bitmap bitmap, Context context) {
        File filesDir = context.getApplicationContext().getFilesDir();
        File imageFile = new File(filesDir, "image.jpg");

        try {
            OutputStream os = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
            os.flush();
            os.close();
            return imageFile;
        } catch (Exception e) {
            Log.e("Bitmap To File", "Error writing bitmap to file: " + e.getMessage());
            return null;
        }
    }
    public static void parseAndSetToken(Context context, String responseBody) {
        try {
            JSONObject jsonObject = new JSONObject(responseBody);

            // Determine which key to use based on the presence of "data" or "user"
            String dataKey = jsonObject.has("data") ? "data" : "user";

            // Check if the key exists
            if (jsonObject.has(dataKey)) {
                JSONObject dataObject = jsonObject.getJSONObject(dataKey);

                // Check if the "token" key exists under the found key
                if (dataObject.has("token")) {
                    String token = dataObject.getString("token");

                    // Save token using TokenManager
                    if (TokenManager.hasToken(context))
                        TokenManager.clearToken(context);
                    TokenManager.saveToken(context, token);
                } else {
                    Log.e("TokenManager", "Key 'token' not found under '" + dataKey + "' object");
                }
            } else {
                Log.e("TokenManager", "Key '" + dataKey + "' not found in JSON response");
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("TokenManager", "Error parsing JSON or extracting token: " + e.getMessage());
        }
    }
}
