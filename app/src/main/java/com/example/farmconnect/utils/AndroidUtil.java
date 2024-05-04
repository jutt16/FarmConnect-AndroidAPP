package com.example.farmconnect.utils;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.farmconnect.Models.UserModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
public class AndroidUtil {
    // Method to decode a Uri to a Bitmap
    public static Bitmap uriToBitmap(Context context, Uri uri) {
        try {
            // Open an input stream from the Uri
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            if (inputStream != null) {
                // Decode the input stream into a Bitmap
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                inputStream.close(); // Close the input stream
                return bitmap; // Return the decoded Bitmap
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null; // Return null if unable to decode Bitmap
    }

    public static Bitmap createVideoThumbnail(Uri videoUri,Context mContext) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(mContext, videoUri);
            // Retrieve a frame at the given time (in microseconds)
            return retriever.getFrameAtTime();
        } catch (Exception e) {
            Log.e("CreatePostActivity", "Error retrieving video thumbnail: " + e.getMessage());
            return null;
        }
    }
    public static String getRealPathFromURI(Uri uri,Context context) {
        String[] projection = {MediaStore.Video.Media.DATA};
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(projection[0]);
            String realPath = cursor.getString(columnIndex);
            cursor.close();
            Log.d("Path:",realPath);
            return realPath;
        } else {
            Log.d("Path:",uri.getPath());
            return uri.getPath();
        }
    }
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

    public static void setImageByUrlwithfixsize(Context context, String imageUrl, ImageView friendProfileImage) {
        Glide.with(context)
                .load(imageUrl)
                .apply(RequestOptions.centerCropTransform())
                .into(friendProfileImage);
    }
    public static void setProfilePicByUrl(Context context, String imageUrl, ImageView imageView){
        Glide.with(context)
                .load(imageUrl)
                .apply(RequestOptions.circleCropTransform()) // Apply circle crop transformation
                .into(imageView);
    }
    public static String getFileExtensionFromUri(Context context,Uri uri) {
        String extension=null;
        // Determine the MIME type of the media using ContentResolver
        String mimeType = context.getContentResolver().getType(Uri.parse(uri.toString()));
        if (mimeType != null) {
            switch (mimeType) {
                case "video/mp4":
                    extension = ".mp4";
                    break;
                case "image/jpeg":
                    extension = ".jpg";
                    break;
                case "image/png":
                    extension = ".png";
                    break;
                case "video/avi":
                    extension = ".avi";
                    break;
                // Add more cases as needed for different MIME types
                default:
                    extension = ".dat";  // Default or unknown type handling
                    break;
            }
        }
        return extension;
    }

}
