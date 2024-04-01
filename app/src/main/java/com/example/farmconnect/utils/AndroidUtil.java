package com.example.farmconnect.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;

public class AndroidUtil {
    public static void showToast(Context context,String message) {
        Toast.makeText(context,message,Toast.LENGTH_LONG).show();
    }
    //validate email
    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }
    //to convert the bitmap to a URI
    public static Uri getImageUri(Context context, Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "Title", null);
        return Uri.parse(path);
    }

}
